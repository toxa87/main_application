package com.example.authorization.service;

import com.example.authorization.entity.ConfirmationToken;
import com.example.authorization.entity.User;
import com.example.authorization.repository.ConfirmationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private static final long EXPIRATION_TIME_CONFIRMATION_TOKEN = 15*60;

    public String generateConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(token);
        confirmationToken.setUser(user);
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiredAt(LocalDateTime.now().plusSeconds(EXPIRATION_TIME_CONFIRMATION_TOKEN));
        confirmationTokenRepository.save(confirmationToken);

        return token;
    }

}
