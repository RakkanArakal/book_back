package com.learn.bookstore.dao;

import com.learn.bookstore.entity.User;
import com.learn.bookstore.responsitory.UserResponsitory;

import java.util.List;

public interface UserDao {
   User getUser(String account, String password);
   List<User> getAllUsers();
   User getUserById(Integer id);
   User saveUser(User user);
   UserResponsitory getUserResponsitory();
}
