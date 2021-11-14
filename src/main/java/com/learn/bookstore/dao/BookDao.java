package com.learn.bookstore.dao;

import com.learn.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookDao {
    List<Book> getAllBooks();
    Book getBookById(int id);
    Book saveBook(Book book);

    boolean deleteBook(int id);

    Page<Book> findAll(Pageable pageable);

    List<Book> labelSearch(String keyWord);
}
