package com.atguigu.gmall.index.aspect;


import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GamllCache;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class GmallCacheAspect {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("@annotation(com.atguigu.gmall.index.annotation.GamllCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        GamllCache cache = signature.getMethod().getAnnotation(GamllCache.class);
        String prefix = StringUtils.isEmpty(cache.value()) ? cache.prefix():cache.value();
        String key = prefix+ Arrays.asList(joinPoint.getArgs()).toString();

        result = cacheHit(signature, key);

        if(result != null){
            return result;
        }

        RLock lock = redissonClient.getLock("Lock");
        lock.lock();

        result = cacheHit(signature, key);

        if(result != null){
            lock.unlock();
            return result;
        }


        result = joinPoint.proceed(joinPoint.getArgs());

        this.redisTemplate.opsForValue().set(key,JSON.toJSONString(result));

        lock.unlock();

        return result;
    }


    public Object cacheHit(MethodSignature signature,String key){
        String cache = redisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank(cache)){
            Class returnType = signature.getMethod().getReturnType();

            return JSON.parseObject(cache,returnType);
        }
        return null;
    }
}
