package com.learn.bookstore.controller;

import com.learn.bookstore.entity.Book;
import com.learn.bookstore.entity.User;
import com.learn.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Scope("session")
@RequestMapping("/home")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/Login")
    public User login(String userName, String password){
        System.out.println(userService);
        System.out.println(this);
        return userService.getUser(userName,password);
    }

    @GetMapping("/getuser")
    public List<User> getAllUser(){
        return userService.getAllUser();
    }

    @PostMapping("/signup")
    public String signup(@RequestBody User user){
        User result = userService.saveUser(user);
        if(result != null)
            return "success";
        else
            return "error";
    }

    @PostMapping("/ban")
    public User ban(@RequestBody User user){
        return userService.banUser(user);
    }
}
