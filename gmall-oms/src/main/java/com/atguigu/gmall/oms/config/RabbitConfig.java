package com.atguigu.gmall.oms.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public Exchange exchange(){
        return new TopicExchange("ORDER_TTL_DEAD_EXCHANGE",true,false,null);
    }

    @Bean
    public Queue ttlQueue(){
        Map<String,Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", "ORDER_TTL_DEAD_EXCHANGE");
        map.put("x-dead-letter-routing-key", "ORDER-CLOSE");
        map.put("x-message-ttl", 120000);
        return new Queue("ORDER_TTL_QUEUE",true,false,false,map);
    }

    @Bean
    public Binding ttlBinding(){
        return new Binding("ORDER_TTL_QUEUE", Binding.DestinationType.QUEUE,"ORDER_TTL_DEAD_EXCHANGE","ORDER-CREATE",null);
    }


    @Bean
    public Queue deadQueue(){
        return new Queue("ORDER_DEAD_QUEUE",true,false,false,null);
    }

    @Bean
    public Binding deadBinding(){
        return new Binding("ORDER_DEAD_QUEUE", Binding.DestinationType.QUEUE,"ORDER_TTL_DEAD_EXCHANGE","ORDER-CLOSE",null);
    }


}
