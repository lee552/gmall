package com.atguigu.gmall.wms.config;


import com.atguigu.gmall.wms.service.impl.WareInfoServiceImpl;
import com.atguigu.gmall.wms.service.impl.WareSkuServiceImpl;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {


    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.254.100:6379");
        return Redisson.create(config);
    }

}
