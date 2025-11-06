package com.globallogix.auth.controller;


import com.globallogix.auth.dto.request.AuthRequest;
import com.globallogix.auth.dto.request.RegisterRequest;
import com.globallogix.auth.dto.response.AuthResponse;
import com.globallogix.auth.dto.response.UpdateTokens;
import com.globallogix.auth.dto.response.ValidationResponse;
import com.globallogix.auth.security.JwtUtil;
import com.globallogix.auth.service.AuthService;
import com.globallogix.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@EnableMethodSecurity
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        AuthResponse response = authService.register(request);
        ResponseCookie refreshCookie = refreshTokenService.setRefreshTokenToCookie(response);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request){
        AuthResponse response = authService.authenticate(request);
        ResponseCookie refreshCookie = refreshTokenService.setRefreshTokenToCookie(response);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshTokens(@CookieValue("refresh") String refreshToken){
        UpdateTokens result = refreshTokenService.updateTokens(refreshToken);

        ResponseCookie newCookie = ResponseCookie.from("refresh", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .maxAge(7 * 24 * 60 * 60) // 7 дней
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                .body(AuthResponse.builder()
                        .accessToken(result.getAccessToken())
                        .type("Bearer")
                        .message(result.getMessage())
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refresh", required = false) String refreshToken,
            HttpServletRequest request) {

        try {
            if (refreshToken != null) {
                authService.logout(refreshToken);
            }

            ResponseCookie deleteCookie = ResponseCookie.from("refresh", "")
                    .httpOnly(true)
                    .secure(false) // true in production
                    .maxAge(0)
                    .path("/")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .build();

        } catch (Exception e) {
            ResponseCookie deleteCookie = ResponseCookie.from("refresh", "")
                    .httpOnly(true)
                    .maxAge(0)
                    .path("/")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .build();
        }
    }
}
