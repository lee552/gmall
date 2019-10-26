package com.atguigu.gmall.order.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPool {



    @Bean
    public ThreadPoolExecutor threadPoolExecutor(@Value("${thread.pool.corePoolSize}")Integer corePoolSize,
                                                 @Value("${thread.pool.maximumPoolSize}")Integer maximumPoolSize,
                                                 @Value("${thread.pool.keepAliveTime}")Integer keepAliveTime){
        return new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime, TimeUnit.SECONDS,new LinkedBlockingDeque<>(Integer.MAX_VALUE/2));
    }


}
