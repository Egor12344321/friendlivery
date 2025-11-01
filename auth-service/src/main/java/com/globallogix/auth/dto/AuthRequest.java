package com.globallogix.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Введите username")
    private String username;

    @NotBlank(message = "Введите пароль")
    private String password;

}
