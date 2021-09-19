package com.learn.bookstore.service;

import com.learn.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    Book getBookById(int id);
    String saveBook(Book book);
    boolean deleteBookById(int id);

    Page<Book> findAll(Pageable pageable);
    Book editBook(Book book);
}
