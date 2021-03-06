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

    String path = "/Java/lucene/indexLibrary";

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
            //??????????????????
            QueryParser queryParser = new MultiFieldQueryParser(new String[]{"name", "intro"}, analyzer);
            //??????
            Query query = queryParser.parse(!StringUtils.isEmpty(keyWord) ? keyWord : "*:*");
            indexReader = DirectoryReader.open(directory);
            //??????????????????
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            //??????????????????????????????????????????????????? ??????lastSd???null??????????????????
            TopDocs tds = indexSearcher.searchAfter(null, query, 10);
            QueryScorer queryScorer = new QueryScorer(query);
            //????????????
            SimpleSpanFragmenter fragmenter = new SimpleSpanFragmenter(queryScorer, 200);
            //??????????????????
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("", "");
            //????????????
            Highlighter highlighter = new Highlighter(formatter, queryScorer);
            //????????????????????????
            highlighter.setTextFragmenter(fragmenter);
            //?????????????????? ???????????????????????????????????????????????????

            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = indexSearcher.doc(sd.doc);
                Integer id = Integer.parseInt(doc.get("id"));
                Book book = bookDao.getBookById(id);
                //???????????????????????????
                String nameBestFragment = highlighter.getBestFragment(analyzer, "name", doc.get("name"));
                //?????????????????????????????????
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
            throw new RuntimeException("?????????????????????" + e.getMessage());
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
        System.out.print(result.getIntro());
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
            //????????????????????????
            File indexFile = new File(path);
            File[] files = indexFile.listFiles();
            // 1. ???????????????,????????????????????????????????????
            analyzer = new IKAnalyzer();
            // 2. ??????Directory??????,????????????????????????
            directory = FSDirectory.open(Paths.get(path));
            // 3. ??????IndexWriteConfig????????????????????????????????????
            IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
            // 4.??????IndexWriter????????????
            indexWriter = new IndexWriter(directory, writerConfig);
            // 5.???????????????????????????IndexWriter??????????????????document
            Document doc = new Document();
            //??????????????????????????????????????????????????????
            TopDocs topDocs = null;
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????id??????????????????
            if (files != null && files.length != 0) {
                //??????????????????
                QueryParser queryParser = new QueryParser("id", analyzer);
                Query query = queryParser.parse(String.valueOf(book.getId()));
                indexReader = DirectoryReader.open(directory);
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                //????????????????????????
                topDocs = indexSearcher.search(query, 1);
            }
            //StringField ????????? ??????????????? ??????
            doc.add(new StringField("id", String.valueOf(book.getId()), Field.Store.YES));
            //TextField ?????? ????????? ??????
            doc.add(new TextField("name", book.getName(), Field.Store.YES));
            doc.add(new TextField("intro", book.getIntro(), Field.Store.YES));

            //?????????????????????????????????
            if (topDocs != null && topDocs.totalHits.value == 0) {
                indexWriter.addDocument(doc);
                //???????????????
            } else {
                indexWriter.updateDocument(new Term("id", String.valueOf(book.getId())), doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("????????????????????????" + e.getMessage());
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
        //??????????????????
        IndexWriter indexWriter = null;
        Directory directory = null;
//        String path = "Z:/lucene/indexLibrary";
        try (Analyzer analyzer = new IKAnalyzer()) {
            directory = FSDirectory.open(Paths.get(path));
            IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, writerConfig);
            //??????id??????????????????
            indexWriter.deleteDocuments(new Term("id", String.valueOf(book.getId())));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("????????????????????????" + e.getMessage());
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
