package com.example.authorization.controller;

import com.example.authorization.dto.*;
import com.example.authorization.entity.ConfirmationToken;
import com.example.authorization.entity.User;
import com.example.authorization.repository.ConfirmationTokenRepository;
import com.example.authorization.repository.UserRepository;
import com.example.authorization.service.AuthService;
import com.example.authorization.service.RateLimiterService;
import com.example.authorization.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final RateLimiterService rateLimiter;
    //Репозитории
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest request,
                                        HttpServletRequest httpRequest) {

        String ip = httpRequest.getRemoteAddr();
        if (!rateLimiter.tryConsume(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Слишком много запросов");
        }

        try {
            User created = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse tokens = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(tokens);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Что-то пошло не так"));
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmUser(@RequestParam String token) {
        try {
            ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Токен подтверждения не найден"));

            if (confirmationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Токен подтверждения просрочился");
            }

            User user = confirmationToken.getUser();
            user.setActive(true);
            userRepository.save(user);
            confirmationTokenRepository.delete(confirmationToken);

            return ResponseEntity.ok("Пользователь подтверждён");
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode()) // 400, 401 и т.д.
                    .body(Map.of("error", e.getReason())); // сообщение, которое ты передал
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Что-то пошло не так"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode()) // 400, 401 и т.д.
                    .body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Что-то пошло не так"));
        }
    }

    @PostMapping("/logout-token")
    public ResponseEntity<?> logoutToken(
            @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest // для доступа к access-токену в заголовке
    ) {
        try {
            authService.logout(request, httpRequest);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode()) // 400, 401 и т.д.
                    .body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Что-то пошло не так"));
        }
    }


}
