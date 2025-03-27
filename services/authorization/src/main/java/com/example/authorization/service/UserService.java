package com.example.authorization.service;

import com.example.authorization.config.SecurityConfig;
import com.example.authorization.dto.CreateUserRequest;
import com.example.authorization.entity.User;
import com.example.authorization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityConfig securityConfig;

    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("Данный email уже зарегистрирован");
        }

        if (userRepository.existsByUsername(request.username())){
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
