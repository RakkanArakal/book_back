package com.learn.bookstore.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Node
public class BookLabel {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public BookLabel(String name){
        this.name = name;
    }

    @Relationship(type = "NEAR")
    public Set<BookLabel> neighbors;

    public void nearWith(BookLabel bookLabel) {
        if (neighbors == null) {
            neighbors = new HashSet<>();
        }
        neighbors.add(bookLabel);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<Integer> bookList;

    public List<Integer> getBookList(){return bookList;}

    public void setBookList(List<Integer> bookList) {this.bookList = bookList; }
}

