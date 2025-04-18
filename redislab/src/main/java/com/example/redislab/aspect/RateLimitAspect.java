package com.example.redislab.aspect;

import com.example.redislab.annotation.RateLimited;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    public RateLimitAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = "rate_limit:" + rateLimited.key();
        Integer count = (Integer) redisTemplate.opsForValue().get(key);

        if (count == null) {
            redisTemplate.opsForValue().set(key, 1, Duration.ofSeconds(rateLimited.durationInSeconds()));
        } else if (count < rateLimited.maxRequests()) {
            redisTemplate.opsForValue().increment(key);
        } else {
            throw new RuntimeException("Rate limit exceeded");
        }

        return joinPoint.proceed();
}
}
