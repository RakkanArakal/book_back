package com.learn.bookstore.controller;

import com.learn.bookstore.entity.Book;
import com.learn.bookstore.responsitory.BookRepository;
import com.learn.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")

public class BookController {


    @Autowired
    private BookService bookService;

    @GetMapping("/getbook")
    public List<Book> getAllBook(String name){
        return bookService.getAllBooks();
    }

    @GetMapping("/getbook/{page}/{size}")
    public Page<Book> getAllBook(@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        Pageable pageable = PageRequest.of(page-1,size);
        return bookService.findAll(pageable);
    }

    @PostMapping("/save")
    public String save(@RequestBody Book book){
        return bookService.saveBook(book);
    }

    @GetMapping("/getBookById")
    public Book getBookInfoById(int id){ return bookService.getBookById(id); }

    @PostMapping("/editBook")
    public Book editBook(@RequestBody Book book){ return bookService.editBook(book); }

    @DeleteMapping("deleteBook/{id}")
    public boolean deleteById(@PathVariable int id){ return bookService.deleteBookById(id); }
}
