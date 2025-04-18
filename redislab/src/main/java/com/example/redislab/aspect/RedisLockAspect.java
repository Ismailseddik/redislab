package com.example.redislab.aspect;

import com.example.redislab.annotation.RedisLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RedisLockAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisLockAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(lock)")
    public Object lockMethod(ProceedingJoinPoint joinPoint, RedisLock lock) throws Throwable {
        String key = "lock:" + lock.key();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "locked", 10, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(success)) {
            try {
                return joinPoint.proceed();
            } finally {
                redisTemplate.delete(key);
            }
        } else {
            throw new IllegalStateException("Another process is executing this method.");}
}
}