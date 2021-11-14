package com.learn.bookstore.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Data
@Getter
@Setter
@Table(name = "book_info")

public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String  name;
    private String  author;
    private String  isbn;
    private Float  price;
    private String  img;
    private Integer stock;
    @Transient
    private String  intro;
    @Transient
    private String bookLabel;


}
