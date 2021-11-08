package com.learn.bookstore.controller;

import com.learn.bookstore.entity.Book;
import com.learn.bookstore.responsitory.BookRepository;
import com.learn.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/home")

public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/book")
    public List<Book> getAllBook(String name){
        return bookService.getAllBooks();
    }

    @GetMapping("/book/{page}/{size}")
    public Page<Book> getAllBook(@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        Pageable pageable = PageRequest.of(page-1,size);
        return bookService.findAll(pageable);
    }

    @PostMapping("/Book")
    public String save(@RequestBody Book book){
        return bookService.saveBook(book);
    }

    @GetMapping("/Book/{id}")
    public Book getBookInfoById(@PathVariable int id){
        return bookService.getBookById(id);
    }

    @PutMapping("/Book")
    public Book editBook(@RequestBody Book book){ return bookService.editBook(book); }

    @DeleteMapping("Book/{id}")
    public boolean deleteById(@PathVariable int id){ return bookService.deleteBookById(id); }

    @PostMapping("/searchBooks")
    public List<Book> fullTextSearch(@RequestBody String key) throws UnsupportedEncodingException {
        String nodeName= URLDecoder.decode(key, "utf-8");
        System.out.print(nodeName);
        return bookService.fullTextSearch(nodeName);
    }
}
