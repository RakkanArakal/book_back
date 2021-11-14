package com.learn.bookstore.responsitory;

import com.learn.bookstore.entity.BookLabel;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface BookLabelRepository extends Neo4jRepository<BookLabel,Long> {
    BookLabel findByName(String name);
    List<BookLabel> findByNeighborsName(String name);

}
