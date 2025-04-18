package com.example.redislab.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cached {
    String key();
    long ttl() default 60; // Time-to-live in seconds
}