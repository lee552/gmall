package com.atguigu.gmall.item.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.annotation.ItemCache;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class ItemCacheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Around("@annotation(com.atguigu.gmall.item.annotation.ItemCache)")
    public Object itemCacheAround(ProceedingJoinPoint joinPoint){
        Object result = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ItemCache itemCache = signature.getMethod().getAnnotation(ItemCache.class);
        String key = itemCache.value()==null?itemCache.prefix():itemCache.value();
        Object[] args = joinPoint.getArgs();
        key = key+args.toString();
        String itemString = redisTemplate.opsForValue().get("key");
        if (StringUtils.isNotBlank(itemString)){
            result = JSON.parseObject(itemString, signature.getReturnType());
            return  result;
        }

        try {
            result = joinPoint.proceed(args);
            redisTemplate.opsForValue().set(key,JSON.toJSONString(result));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return result;
    }

}
