package com.learn.bookstore.responsitory;

import com.learn.bookstore.entity.BookIntro;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookIntroRepository extends MongoRepository<BookIntro,Integer> {

}
