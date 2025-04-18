package com.example.redislab.aspect;

import com.example.redislab.annotation.RedisLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RedisLockAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ExpressionParser parser = new SpelExpressionParser();

    public RedisLockAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(lock)")
    public Object lockMethod(ProceedingJoinPoint joinPoint, RedisLock lock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Create evaluation context
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        String evaluatedKey = parser.parseExpression(lock.key()).getValue(context, String.class);
        String redisKey = "lock:" + evaluatedKey;

        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, "locked", 10, TimeUnit.SECONDS);
        System.out.println("Lock key = " + redisKey + " | Lock acquired = " + success);

        if (Boolean.TRUE.equals(success)) {
            try {
                return joinPoint.proceed();
            } finally {
                redisTemplate.delete(redisKey);
            }
        } else {
            throw new IllegalStateException("Another process is executing this method.");}
}
}