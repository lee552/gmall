package com.atguigu.gmall.order.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class JedisConfig {

    @Bean
    public JedisPool jedisPool(){
        return new JedisPool("192.168.254.100");
    }
}
