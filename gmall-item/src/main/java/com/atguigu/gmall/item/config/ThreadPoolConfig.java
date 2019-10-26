package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {


    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){

      return new ThreadPoolExecutor(5,
        Integer.MAX_VALUE,
        24l,
        TimeUnit.SECONDS,
        new LinkedBlockingDeque<>(),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.DiscardOldestPolicy());
    }

}
