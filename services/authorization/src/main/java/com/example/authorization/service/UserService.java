package com.example.authorization.service;

import com.example.authorization.config.SecurityConfig;
import com.example.authorization.dto.CreateUserRequest;
import com.example.authorization.entity.User;
import com.example.authorization.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final SecurityConfig securityConfig;

    private final ConfirmationTokenService tokenService;

    @Value("${settings.application_url}")
    private String applicationUrl;

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

        userRepository.save(user);

        // вот этот блок 👇
        String token = tokenService.generateConfirmationToken(user);
        String confirmUrl = applicationUrl + "/api/users/confirm?token=" + token;
        System.out.println(confirmUrl);

        return user;
    }

}
