package com.learn.bookstore.daoimpl;


import com.learn.bookstore.dao.BookDao;
import com.learn.bookstore.entity.Book;
import com.learn.bookstore.entity.BookIntro;
import com.learn.bookstore.entity.BookLabel;
import com.learn.bookstore.responsitory.BookIntroRepository;
import com.learn.bookstore.responsitory.BookLabelRepository;
import com.learn.bookstore.responsitory.BookRepository;
import com.learn.bookstore.utils.RedisUtil;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName BookDaoImpl
 * @Description TODO
 * @Author thunderBoy
 * @Date 2019/11/5 20:20
 */
@Repository
public class BookDaoImpl implements BookDao {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookIntroRepository bookIntroRepository;
    @Autowired
    private BookLabelRepository bookLabelRepository;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<Book> getAllBooks() {

        List<Book> books = bookRepository.findAll();
        List<BookIntro> bookIntros = bookIntroRepository.findAll();
        for (int i=0;i<books.size();i++) {
            books.get(i).setIntro(bookIntros.get(i).getIntro());
            books.get(i).setBookLabel(bookIntros.get(i).getBookLabel());
        }
//        List<Object> b = redisUtil.lGet("book",0,-1);
//        books = (List<Book>) JSONArray.parseObject(b.toString(), Book.class);
        return books;
    }

    @Override
    public Book getBookById(int id) {

        Book book = null;

        System.out.println("Searching Book: " + id + " in Redis");

        Object b = redisUtil.get("book" + id);
        if (b == null) {
            System.out.println("Book: " + id + " is not in Redis");
            System.out.println("Searching Book: " + id + " in DB");
            book = bookRepository.findById(id).orElse(null);
            BookIntro intro = bookIntroRepository.findById(id).orElse(null);
            book.setIntro(intro.getIntro());
            redisUtil.set("book" + id, JSONArray.toJSON(book));
        } else {
            book = JSONArray.parseObject(b.toString(), Book.class);
            System.out.println("book: " + id + " is in Redis");
        }

        return book;
    }

    @Override
    public Book saveBook(Book book) {
        try {
            redisUtil.set("book" + book.getId(), JSONArray.toJSON(book));
            book = bookRepository.save(book);
            BookIntro intro = new BookIntro(book.getId(),book.getIntro());
            bookIntroRepository.save(intro);
            return book;
        }catch (Exception e){
            return null;
        }

    }

    @Override
    public boolean deleteBook(int id) {
        try {
            redisUtil.del("book" + id);
            bookRepository.deleteById(id);
            bookIntroRepository.deleteById(id);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {

        return bookRepository.findAll(pageable);

    }

    @Override
    public List<Book> labelSearch(String keyWord) {
//
//        bookLabelRepository.deleteAll();
//
//        BookLabel lishi = new BookLabel("历史");
//        BookLabel zhengzhi = new BookLabel("政治");
//        BookLabel zhuanji = new BookLabel("传记");
//        BookLabel ertong = new BookLabel("儿童");
//        BookLabel jiaoyu = new BookLabel("教育");
//        BookLabel jishu = new BookLabel("技术");
//        BookLabel xiaoshuo = new BookLabel("小说");
//        BookLabel rensheng = new BookLabel("人生哲理");
//        BookLabel tuolaji = new BookLabel("拖拉机");
//        BookLabel fupo = new BookLabel("富婆");
//        BookLabel gaochan = new BookLabel("高产");
//
//        lishi.setBookList(Arrays.asList(1,10));
//        zhengzhi.setBookList(Arrays.asList(1));
//        zhuanji.setBookList(Arrays.asList(1));
//        ertong.setBookList(Arrays.asList(6,10));
//        jiaoyu.setBookList(Arrays.asList(4,6));
//        jishu.setBookList(Arrays.asList(5,7,8));
//        xiaoshuo.setBookList(Arrays.asList(2,3,9));
//        rensheng.setBookList(Arrays.asList(3,9));
//        tuolaji.setBookList(Arrays.asList(7));
//        fupo.setBookList(Arrays.asList(8));
//        gaochan.setBookList(Arrays.asList(5));
//
//        lishi.nearWith(zhengzhi);
//        lishi.nearWith(rensheng);
//        lishi.nearWith(zhuanji);
//        zhengzhi.nearWith(lishi);
//        rensheng.nearWith(xiaoshuo);
//
//        ertong.nearWith(jiaoyu);
//        jiaoyu.nearWith(ertong);
//        jiaoyu.nearWith(jishu);
//        jiaoyu.nearWith(lishi);
//
//        jishu.nearWith(fupo);
//        jishu.nearWith(tuolaji);
//        jishu.nearWith(jiaoyu);
//        jishu.nearWith(gaochan);
//
//        bookLabelRepository.save(lishi);
//        bookLabelRepository.save(zhengzhi);
//        bookLabelRepository.save(zhuanji);
//        bookLabelRepository.save(ertong);
//        bookLabelRepository.save(jiaoyu);
//        bookLabelRepository.save(jishu);
//        bookLabelRepository.save(xiaoshuo);
//        bookLabelRepository.save(rensheng);
//        bookLabelRepository.save(tuolaji);
//        bookLabelRepository.save(fupo);
//        bookLabelRepository.save(gaochan);

        keyWord = keyWord.substring(0, keyWord.length() - 1);
        List<BookLabel> labelList = bookLabelRepository.findByNeighborsName(keyWord);
        List<Integer> bookInedx = new ArrayList<>(10);
        if(bookLabelRepository.findByName(keyWord)!= null){
            bookInedx = bookLabelRepository.findByName(keyWord).getBookList();
        }

        for(int i=0;i<labelList.size();i++){
            List<Integer> books = labelList.get(i).getBookList();
            for(int j=0;j<books.size();j++){
                Integer bookId = books.get(j);
                if(!bookInedx.contains(bookId)){
                    bookInedx.add(bookId);
                }
            }
        }
        List<Book> searchList = new ArrayList<>(10);

        for(int i=0;i<bookInedx.size();i++){
            Book book = bookRepository.findById(bookInedx.get(i)).orElse(null);
            book.setBookLabel(bookIntroRepository.findById(bookInedx.get(i)).orElse(null).getBookLabel());
            searchList.add(book);
        }

        return searchList;
    }


}
