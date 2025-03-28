package com.example.authorization.service;

import com.example.authorization.config.SecurityConfig;
import com.example.authorization.dto.CreateUserRequest;
import com.example.authorization.entity.User;
import com.example.authorization.repository.UserRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final SecurityConfig securityConfig;

    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Данный email уже зарегистрирован");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Данное имя пользователя уже зарегистрировано");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());

        String encodedPassword = securityConfig.passwordEncoder().encode(request.password());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

}
