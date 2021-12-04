package com.learn.bookstore.config.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.server.session.DefaultWebSessionManager;


@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60)
public class RedisSessionConfig {
}
