package com.learn.bookstore.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Document(collection = "bookintro")

public class BookIntro implements Serializable {
    @Id
    private Integer id;

    private String  intro;

    public BookIntro(int id,String intro){
        this.id = id;
        this.intro = intro;
    }

}
