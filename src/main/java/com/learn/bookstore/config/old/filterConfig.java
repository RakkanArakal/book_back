//package com.learn.bookstore.config;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//
//import org.springframework.context.annotation.Bean;
//
//import org.springframework.context.annotation.Configuration;
//
//
//
//
//@Configuration
//
//public class filterConfig {
//
//    @Bean
//
//    public corsFilter logFilter() {
//
//        return new corsFilter();
//
//    }
//    @Bean
//
//    public FilterRegistrationBean testFilterRegistration() {
//
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//
//        registration.setFilter(logFilter());
//
//        registration.addUrlPatterns("/*");
//
//        registration.setOrder(1);
//
//        return registration;
//
//    }
//
//}
