package com.learn.bookstore.daoimpl;


import com.learn.bookstore.dao.BookDao;
import com.learn.bookstore.entity.Book;
import com.learn.bookstore.responsitory.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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

    @Override
    public List<Book> getAllBooks() {

        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(int id) {

        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public Book saveBook(Book book) {

        return bookRepository.save(book);
    }

    @Override
    public boolean deleteBook(int id) {
        try {
            bookRepository.deleteById(id);
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
