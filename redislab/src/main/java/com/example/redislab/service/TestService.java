package com.example.redislab.service;

import com.example.redislab.annotation.Cached;
import com.example.redislab.model.User;
import com.example.redislab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TestService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Cached(key = "user:#id", ttl=120)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
}
}
