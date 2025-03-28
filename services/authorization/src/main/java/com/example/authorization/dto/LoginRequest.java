package com.example.authorization.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    @Email(message = "Формат email не валидный")
    private String email;
    private String password;
}
