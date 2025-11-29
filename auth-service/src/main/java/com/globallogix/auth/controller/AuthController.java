package com.globallogix.auth.controller;


import com.globallogix.auth.dto.request.AuthRequest;
import com.globallogix.auth.dto.request.RegisterRequest;
import com.globallogix.auth.dto.response.AuthResponse;
import com.globallogix.auth.dto.response.UpdateTokens;
import com.globallogix.auth.exception.InvalidTokenRefreshException;
import com.globallogix.auth.security.JwtUtil;
import com.globallogix.auth.service.authorization.AuthService;
import com.globallogix.auth.service.refresh.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
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
        log.debug("CONTROLLER: Login started");
        AuthResponse response = authService.authenticate(request);
        ResponseCookie refreshCookie = refreshTokenService.setRefreshTokenToCookie(response);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshTokens(
            @CookieValue(value = "refresh", required = false) String refreshToken) {
        log.debug("CONTROLLER: Updating tokens started");
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.error("CONTROLLER: Refresh not in cookie header");
            return createErrorResponse("Refresh token отсутствует");
        }
        log.info("Refresh token found in cookie: {}", refreshToken);
        try {

            UpdateTokens result = refreshTokenService.updateTokens(refreshToken);

            ResponseCookie newCookie = ResponseCookie.from("refresh", result.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .maxAge(7 * 24 * 60 * 60) // 7 дней
                    .path("/")
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                    .body(AuthResponse.builder()
                            .accessToken(result.getAccessToken())
                            .type("Bearer")
                            .message(result.getMessage())
                            .build());

        } catch (InvalidTokenRefreshException e) {
            return createErrorResponse("Требуется повторная авторизация");
        }
    }

    private ResponseEntity<?> createErrorResponse(String message) {
        ResponseCookie deleteCookie = ResponseCookie.from("refresh", "")
                .httpOnly(true)
                .secure(false)
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(Map.of("error", message));
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
            log.info("Refresh удален из кук");
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
