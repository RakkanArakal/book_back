package com.learn.bookstore.config;


import java.io.IOException;

import javax.servlet.Filter;

import javax.servlet.FilterChain;

import javax.servlet.FilterConfig;

import javax.servlet.ServletException;

import javax.servlet.ServletRequest;

import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;



import org.slf4j.Logger;

import org.slf4j.LoggerFactory;



public class corsFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(corsFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("初始化filter");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse ServletResponse , FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req =(HttpServletRequest)servletRequest;

        HttpServletResponse resp =(HttpServletResponse)ServletResponse;

        logger.info("执行filter......允许跨域..........");

        resp.setContentType("text/html;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
        resp.setHeader("Access-Control-Max-Age", "3600");
        resp.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,Authorization,SessionToken,JSESSIONID,token");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("XDomainRequestAllowed","1");

        filterChain.doFilter(req, resp);

    }

    public void destroy() {
        logger.info("filter被销毁");
    }




}
