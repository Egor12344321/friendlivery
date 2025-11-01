package com.globallogix.flight.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Instant expiresAt;
    private UserDto user;

    public static AuthResponse of(String token, String refreshToken, Instant expiresAt, UserDto user) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresAt(expiresAt);
        response.setUser(user);
        return response;
    }

    public static CommonResponse<AuthResponse> success(String token, String refreshToken, Instant expiresAt, UserDto user) {
        AuthResponse authResponse = AuthResponse.of(token, refreshToken, expiresAt, user);
        return CommonResponse.success(authResponse, "Авторизация успешна");
    }
}