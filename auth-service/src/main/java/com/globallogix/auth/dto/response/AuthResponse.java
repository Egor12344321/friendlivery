package com.globallogix.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private String username;
    private String email;
    private String message;

    public AuthResponse(String token, String username, String email, String message){
        this.accessToken = token;
        this.username = username;
        this.email = email;
        this.message = message;
        this.type = "Bearer";
    }
}
