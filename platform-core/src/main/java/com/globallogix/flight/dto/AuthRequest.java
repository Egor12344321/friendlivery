package com.globallogix.flight.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotBlank(message = "Введите имя")
    @Size(min = 3, max = 30, message = "Имя от 3 до 30 символов")
    private String username;

    @NotBlank(message = "Введите пароль")
    @Size(min = 6, message = "Пароль минимум 6 символов")
    private String message;

    @Email(message = "Неправильный ввод email")
    private String email;
}
