package com.learn.bookstore.serviceimpl;

import com.learn.bookstore.dao.BookDao;
import com.learn.bookstore.entity.Book;
import com.learn.bookstore.service.BookService;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    String path = "./lucene/indexLibrary";

    @Autowired
    private BookDao bookDao;

    @Override
    public List<Book> getAllBooks() {

        return bookDao.getAllBooks();
    }

    @Override
    public Book getBookById(int id) {

        return bookDao.getBookById(id);
    }

    @Override
    public List<Book> labelSearch(String keyWord) {
        return bookDao.labelSearch(keyWord);
    }
    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookDao.findAll(pageable);
    }

    @Override
    public List<Book> fullTextSearch(String keyWord) {
        List<Book> searchList = new ArrayList<>(10);

        IndexReader indexReader = null;
        Directory directory = null;
        try (Analyzer analyzer = new IKAnalyzer()) {
            directory = FSDirectory.open(Paths.get(path));
            //多项查询条件
            QueryParser queryParser = new MultiFieldQueryParser(new String[]{"name", "intro"}, analyzer);
            //单项
            Query query = queryParser.parse(!StringUtils.isEmpty(keyWord) ? keyWord : "*:*");
            indexReader = DirectoryReader.open(directory);
            //索引查询对象
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            //通过最后一个元素去搜索下一页的元素 如果lastSd为null，查询第一页
            TopDocs tds = indexSearcher.searchAfter(null, query, 10);
            QueryScorer queryScorer = new QueryScorer(query);
            //最佳摘要
            SimpleSpanFragmenter fragmenter = new SimpleSpanFragmenter(queryScorer, 200);
            //高亮前后标签
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("", "");
            //高亮对象
            Highlighter highlighter = new Highlighter(formatter, queryScorer);
            //设置高亮最佳摘要
            highlighter.setTextFragmenter(fragmenter);
            //遍历查询结果 把标题和内容替换为带高亮的最佳摘要

            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = indexSearcher.doc(sd.doc);
                Integer id = Integer.parseInt(doc.get("id"));
                Book book = bookDao.getBookById(id);
                //获取标题的最佳摘要
                String nameBestFragment = highlighter.getBestFragment(analyzer, "name", doc.get("name"));
                //获取文章内容的最佳摘要
                String introBestFragment = highlighter.getBestFragment(analyzer, "intro", doc.get("intro"));
                book.setId(id);
                if(nameBestFragment != null)
                    book.setName(nameBestFragment);
                else
                    book.setIntro(introBestFragment);
                searchList.add(book);
            }
            return searchList;


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("全文檢索出错：" + e.getMessage());
        } finally {
            if (indexReader != null) {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (directory != null) {
                try {
                    directory.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String saveBook(Book book) {
        Book result = bookDao.saveBook(book);
        if(result != null) {
            addOrUpIndex(result);
            return "success";
        }
        else
            return "error";
    }

    @Override
    public boolean deleteBookById(int id) {
        Book b =  bookDao.getBookById(id);

        if(b!=null){
            deleteIndex(b);
            return  bookDao.deleteBook(id);
        }else {
            return false;
        }
    }


    @Override
    public Book editBook(Book book) {
        addOrUpIndex(book);
        return bookDao.saveBook(book);
    }

    private void addOrUpIndex(Book book) {
        IndexWriter indexWriter = null;
        IndexReader indexReader = null;
        Directory directory = null;
        Analyzer analyzer = null;
        try {
            //创建索引目录文件
            File indexFile = new File(path);
            File[] files = indexFile.listFiles();
            // 1. 创建分词器,分析文档，对文档进行分词
            analyzer = new IKAnalyzer();
            // 2. 创建Directory对象,声明索引库的位置
            directory = FSDirectory.open(Paths.get(path));
            // 3. 创建IndexWriteConfig对象，写入索引需要的配置
            IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
            // 4.创建IndexWriter写入对象
            indexWriter = new IndexWriter(directory, writerConfig);
            // 5.写入到索引库，通过IndexWriter添加文档对象document
            Document doc = new Document();
            //查询是否有该索引，没有添加，有则更新
            TopDocs topDocs = null;
            //判断索引目录文件是否存在文件，如果没有文件，则为首次添加，有文件，则查询id是否已经存在
            if (files != null && files.length != 0) {
                //创建查询对象
                QueryParser queryParser = new QueryParser("id", analyzer);
                Query query = queryParser.parse(String.valueOf(book.getId()));
                indexReader = DirectoryReader.open(directory);
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                //查询获取命中条目
                topDocs = indexSearcher.search(query, 1);
            }
            //StringField 不分词 直接建索引 存储
            doc.add(new StringField("id", String.valueOf(book.getId()), Field.Store.YES));
            //TextField 分词 建索引 存储
            doc.add(new TextField("name", book.getName(), Field.Store.YES));
            doc.add(new TextField("intro", book.getIntro(), Field.Store.YES));

            //如果没有查询结果，添加
            if (topDocs != null && topDocs.totalHits.value == 0) {
                indexWriter.addDocument(doc);
                //否则，更新
            } else {
                indexWriter.updateDocument(new Term("id", String.valueOf(book.getId())), doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("添加索引库出错：" + e.getMessage());
        } finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (indexReader != null) {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (directory != null) {
                try {
                    directory.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (analyzer != null) {
                analyzer.close();
            }
        }
    }
    private void deleteIndex(Book book) {
        //删除全文检索
        IndexWriter indexWriter = null;
        Directory directory = null;
//        String path = "Z:/lucene/indexLibrary";
        try (Analyzer analyzer = new IKAnalyzer()) {
            directory = FSDirectory.open(Paths.get(path));
            IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, writerConfig);
            //根据id字段进行删除
            indexWriter.deleteDocuments(new Term("id", String.valueOf(book.getId())));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除索引库出错：" + e.getMessage());
        } finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (directory != null) {
                try {
                    directory.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
