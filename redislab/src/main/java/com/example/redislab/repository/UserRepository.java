package com.example.redislab.repository;

import com.example.redislab.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long>{
        }