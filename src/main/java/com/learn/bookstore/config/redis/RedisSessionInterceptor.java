package com.learn.bookstore.config.redis;

import com.learn.bookstore.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class RedisSessionInterceptor implements HandlerInterceptor {


    @Autowired
    RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));//支持跨域请求
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");//是否支持cookie跨域
        response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token");

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        return true;
//        if (username != null) {
//            String loginSessionId = (String) redisUtil.get(String.format("username:%s", username));
//
//            if (loginSessionId != null && loginSessionId.equals(session.getId())) {
//                // success
//                return true;
//            } else if (loginSessionId != null && !loginSessionId.equals(session.getId())) {
//                log.info("id mismatch, session invalidate: {}", session.getId());
//            }
//        } else {
//            log.info("please login");
//        }
//        response401(response);
//        return false;
    }

    private void response401(HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().print("401");
    }

}
