package com.learn.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class corsFilter implements WebMvcConfigurer {
    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*") // 允许任何域名使用
                        .allowedMethods("*") // 允许任何方法（post、get等）
                        .allowedHeaders("*") // 允许任何请求头
                        .allowCredentials(true) // 带上cookie信息
                        .maxAge(3600); // 在3600秒内，不需要再发送预检验请求，可以缓存该结果
            }
        };
    }
}