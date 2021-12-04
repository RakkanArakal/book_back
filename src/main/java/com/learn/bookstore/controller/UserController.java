package com.learn.bookstore.controller;

import com.alibaba.fastjson.JSONArray;
import com.learn.bookstore.constant.Constant;
import com.learn.bookstore.entity.Book;
import com.learn.bookstore.entity.User;
import com.learn.bookstore.service.UserService;
import com.learn.bookstore.utils.RedisUtil;
//import com.learn.bookstore.utils.sessionutils.SessionUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
//@Scope("session")
@RequestMapping("/home")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RedisUtil redisUtil;

    @PostMapping("/Login")
    public User login(HttpServletRequest request,@RequestBody User user){

        User auth = userService.getUser(user.getAccount(),user.getPassword());
//        System.out.print(auth);
        if(auth != null){
            JSONObject obj = new JSONObject();
            obj.put(Constant.USER_ID, auth.getId());
            obj.put(Constant.USERNAME, auth.getAccount());
//            obj.put(Constant.USER_TYPE, auth.getUserType());
//            SessionUtil.setSession(obj);


            HttpSession session = request.getSession();
            session.setAttribute("username", user.getAccount());
            redisUtil.set("username:"+user.getAccount(), session.getId());


            AtomicInteger count=new AtomicInteger(0);

            Object c = redisUtil.get("count");
            if( c != null){
                count = JSONArray.parseObject(c.toString(), AtomicInteger.class);
            }
            auth.setCount(count.incrementAndGet());
            redisUtil.set("count", JSONArray.toJSON(count));
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
