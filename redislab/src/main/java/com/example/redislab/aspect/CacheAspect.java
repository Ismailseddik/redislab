package com.example.redislab.aspect;

import com.example.redislab.annotation.Cached;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
public class CacheAspect {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public CacheAspect(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(cached)")
    public Object cache(ProceedingJoinPoint joinPoint, Cached cached) throws Throwable {
        String key = cached.key();
        String cachedData = redisTemplate.opsForValue().get(key);

        if (cachedData != null) {
            Class<?> returnType = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getReturnType();
            return objectMapper.readValue(cachedData, returnType);
        }

        Object result = joinPoint.proceed();

        if (result != null) {
            String valueToCache = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(key, valueToCache, Duration.ofSeconds(cached.ttl()));
        }

        return result;
}
}
