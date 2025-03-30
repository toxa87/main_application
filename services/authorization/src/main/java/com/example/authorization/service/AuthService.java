package com.example.authorization.service;

import com.example.authorization.security.SecurityConfig;
import com.example.authorization.dto.AuthResponse;
import com.example.authorization.dto.RefreshTokenRequest;
import com.example.authorization.entity.AccessTokenBlacklist;
import com.example.authorization.entity.RefreshToken;
import com.example.authorization.entity.User;
import com.example.authorization.repository.AccessTokenBlacklistRepository;
import com.example.authorization.repository.RefreshTokenRepository;
import com.example.authorization.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final SecurityConfig securityConfig;


    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким email не найден"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        return new AuthResponse(accessToken,refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = securityConfig.hashToken(request.getRefreshToken());

        RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new RuntimeException("Refresh token не найден"));

        if (storedToken.isExpired()|| storedToken.isRevoked()){
            throw new RuntimeException("Refresh token невалидный");
        }

        User user = storedToken.getUser();
        String newAccessToken = tokenService.generateAccessToken(user);
        String newRefreshToken = tokenService.generateRefreshToken(user);

        return new AuthResponse(newAccessToken,newRefreshToken);

    }

    public void logout(RefreshTokenRequest request, HttpServletRequest servletRequest) {
        String refreshToken = securityConfig.hashToken(request.getRefreshToken());

        RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new RuntimeException("Refresh token не найден"));

        refreshTokenRepository.delete(storedToken);

        String authHeader = servletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            // 3. Сохраняем access-токен в blacklist
            AccessTokenBlacklist entry = new AccessTokenBlacklist();
            entry.setAccessToken(accessToken);
            entry.setExpiredAt(tokenService.getExpiration(accessToken)); // или просто now + 15 min
            accessTokenBlacklistRepository.save(entry);
        }
    }
}
