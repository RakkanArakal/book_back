package com.learn.bookstore.daoimpl;


import com.learn.bookstore.dao.BookDao;
import com.learn.bookstore.entity.Book;
import com.learn.bookstore.entity.BookIntro;
import com.learn.bookstore.responsitory.BookIntroRepository;
import com.learn.bookstore.responsitory.BookRepository;
import com.learn.bookstore.utils.RedisUtil;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    RedisUtil redisUtil;

    @Override
    public List<Book> getAllBooks() {

        List<Book> books = bookRepository.findAll();
        List<BookIntro> bookIntros = bookIntroRepository.findAll();
        for (int i=0;i<books.size();i++) {
            books.get(i).setIntro(bookIntros.get(i).getIntro());
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
            bookIntroRepository.save(new BookIntro(book.getId(),book.getIntro()));
            bookRepository.save(book);
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


}
