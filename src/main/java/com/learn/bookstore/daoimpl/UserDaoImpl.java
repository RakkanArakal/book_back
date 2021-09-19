package com.learn.bookstore.daoimpl;

import com.learn.bookstore.dao.UserDao;
import com.learn.bookstore.entity.User;
import com.learn.bookstore.responsitory.UserResponsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    UserResponsitory userResponsitory;
    @Override
    public User getUser(String account, String password) {
        return userResponsitory.findUserByLogin(account,password);
    }

    @Override
    public List<User> getAllUsers() {
        return userResponsitory.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        return userResponsitory.findUserById(id);
    }

    @Override
    public User saveUser(User user){return userResponsitory.save(user);}


    @Override
    public UserResponsitory getUserResponsitory() {
        return userResponsitory;
    }
}