package com.globallogix.auth.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username не может быть пустым")
    @Size(min = 3, max = 50, message = "Username должен содержать от 3 до 50 символов")
    private String username;

    @Email(message = "Неправильный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;

    private String phoneNumber;

    private String firstName;
    private String lastName;

}
