package com.learn.bookstore.controller;

import com.learn.bookstore.constant.Constant;
import com.learn.bookstore.entity.Book;
import com.learn.bookstore.entity.User;
import com.learn.bookstore.service.UserService;
import com.learn.bookstore.utils.RedisUtil;
import com.learn.bookstore.utils.sessionutils.SessionUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Scope("session")
@RequestMapping("/home")
public class UserController {

    static AtomicInteger count=new AtomicInteger(0);

    @Autowired
    UserService userService;

    @PostMapping("/Login")
    public User login(@RequestBody User user){

        User auth = userService.getUser(user.getAccount(),user.getPassword());
//        System.out.print(auth);
        if(auth != null){
            JSONObject obj = new JSONObject();
            obj.put(Constant.USER_ID, auth.getId());
            obj.put(Constant.USERNAME, auth.getAccount());
//            obj.put(Constant.USER_TYPE, auth.getUserType());
            SessionUtil.setSession(obj);

            auth.setCount(count.incrementAndGet());
        }
        return auth;
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
