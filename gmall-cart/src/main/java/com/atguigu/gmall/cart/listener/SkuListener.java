package com.atguigu.gmall.cart.listener;


import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SkuListener {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final String CURRENT_PRICE = "GMALL:CART:CURRENTPRICE:";

    @RabbitListener(bindings = @QueueBinding(value = @Queue("SKU_TB_QUEUE"),
                                             exchange = @Exchange(value = "GMALL_SKUINFO_EXCHANGE",
                                                                  ignoreDeclarationExceptions = "true",
                                                                  type = ExchangeTypes.TOPIC),
                                             key = "SKUINFO_UPDATE"))
    public void recieveMessege(Map<String,String> map){
        String price = map.get("price");
        String skuId = map.get("skuId");
        redisTemplate.opsForValue().set(CURRENT_PRICE+skuId,price);


    }


}
