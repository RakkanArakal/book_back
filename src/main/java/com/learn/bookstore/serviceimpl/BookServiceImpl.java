package com.learn.bookstore.serviceimpl;

import com.learn.bookstore.dao.BookDao;
import com.learn.bookstore.entity.Book;
import com.learn.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

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
    public String saveBook(Book book) {
        Book result = bookDao.saveBook(book);
        if(result != null)
            return "success";
        else
            return "error";
    }

    @Override
    public boolean deleteBookById(int id) {
        Book b =  bookDao.getBookById(id);
        if(b!=null){
            return  bookDao.deleteBook(id);
        }else {
            return false;
        }
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookDao.findAll(pageable);
    }

    @Override
    public Book editBook(Book book) {
        return bookDao.saveBook(book);
    }

}
