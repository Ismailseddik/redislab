package com.example.redislab.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    String key();
    int maxRequests();
    int durationInSeconds();
}