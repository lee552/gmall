package com.atguigu.gmall.wms.listener;


import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.SkuLockVO;
import com.atguigu.gmall.wms.service.impl.WareSkuServiceImpl;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnlockListener {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private WareSkuDao wareSkuDao;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "WARE_UNLOCK_QUEUE"),
                                            exchange = @Exchange(value = "WARE_UNLOCK_EXCHANGE",
                                                                ignoreDeclarationExceptions = "true",
                                                                type = ExchangeTypes.TOPIC),
                                            key = "WARE_UNLOCK"))
    public void unlock(String orderToken){

        String skuLockVOsSTRING = redisTemplate.opsForValue().get(WareSkuServiceImpl.SKU_LOCK_PREFIX+orderToken);
        List<SkuLockVO> skuLockVOS = JSON.parseArray(skuLockVOsSTRING, SkuLockVO.class);
        for (SkuLockVO skuLockVO : skuLockVOS) {
            wareSkuDao.unLock(skuLockVO);

            redisTemplate.delete(WareSkuServiceImpl.SKU_LOCK_PREFIX+orderToken);
        }

    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "GMALL_WARE_DOWN"),
                                            exchange = @Exchange(value = "GMALL_WARE_EXCHANGE",ignoreDeclarationExceptions = "true",
                                            type = ExchangeTypes.TOPIC),
                                            key = "WARE_DOWN"))
    public void wareDown(String orderToken){
        String key = WareSkuServiceImpl.SKU_LOCK_PREFIX + orderToken;
        String skuLockVOsString = redisTemplate.opsForValue().get(key);
        List<SkuLockVO> lockVOS = JSON.parseArray(skuLockVOsString, SkuLockVO.class);

        for (SkuLockVO lockVO : lockVOS) {
            wareSkuDao.skuWareDown(lockVO);
        }

        redisTemplate.delete(key);


    }

}
