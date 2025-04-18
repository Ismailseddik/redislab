package com.example.redislab.controller;

import com.example.redislab.annotation.RateLimited;
import com.example.redislab.annotation.RedisLock;
import com.example.redislab.model.User;
import com.example.redislab.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return testService.createUser(user);
    }

    @GetMapping("/users/{id}")
    @RateLimited(key = "'user:' + #id", maxRequests = 5, durationInSeconds = 60)
    @RedisLock(key = "'lock_user_' + #id")
    public Optional<User> getUser(@PathVariable Long id) {
        return testService.getUserById(id);
}
}
