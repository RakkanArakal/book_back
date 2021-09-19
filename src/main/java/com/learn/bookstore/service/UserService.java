package com.learn.bookstore.service;

import com.learn.bookstore.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUser();
    User getUser(String account, String password);
    User saveUser(User user);
    User banUser(User user);
}
