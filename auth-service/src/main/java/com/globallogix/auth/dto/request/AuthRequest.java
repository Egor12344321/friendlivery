package com.globallogix.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Введите username")
    private String username;

    @NotBlank(message = "Введите пароль")
    private String password;

}
