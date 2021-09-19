package com.learn.bookstore.responsitory;

import com.learn.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserResponsitory extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    @Query(value = "select * from user where account=? and password=?", nativeQuery = true)
    User findUserByLogin(String account, String password);

    @Query(value = "select * from user where id=?", nativeQuery = true)
    User findUserById(Integer id);

}
