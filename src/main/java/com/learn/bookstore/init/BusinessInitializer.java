package com.learn.bookstore.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;

/**
 * 业务初始化器
 *
 * @author zrx
 */
@Component
public class BusinessInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        //加载ik分词器配置 防止第一次查询慢
        Dictionary.initial(DefaultConfig.getInstance());
    }
}
