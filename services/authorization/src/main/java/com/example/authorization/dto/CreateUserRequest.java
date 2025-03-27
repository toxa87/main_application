package com.example.authorization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(@NotBlank(message = "Email обязателен")
                                @Email(message = "Формат email не валидный")
                                String email,

                                @NotBlank(message = "Имя пользователя обязательно")
                                @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
                                String username,

                                @NotBlank(message = "Пароль обязателен")
                                @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
                                String password) {
}
