package com.example.authorization.controller;

import com.example.authorization.dto.CreateUserRequest;
import com.example.authorization.dto.JwtResponse;
import com.example.authorization.dto.LoginRequest;
import com.example.authorization.entity.ConfirmationToken;
import com.example.authorization.entity.User;
import com.example.authorization.repository.ConfirmationTokenRepository;
import com.example.authorization.repository.UserRepository;
import com.example.authorization.service.AuthService;
import com.example.authorization.service.ConfirmationTokenService;
import com.example.authorization.service.RateLimiterService;
import com.example.authorization.service.UserService;
import io.github.bucket4j.Bucket;
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
    private final ConfirmationTokenRepository tokenRepository;
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
            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new JwtResponse(token));
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
            ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));

            if (confirmationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired");
            }

            User user = confirmationToken.getUser();
            user.setActive(true);
            userRepository.save(user);
            tokenRepository.delete(confirmationToken);

            return ResponseEntity.ok("Пользователь подтверждён");
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Что-то пошло не так"));
        }
    }

}
