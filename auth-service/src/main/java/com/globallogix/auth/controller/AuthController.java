package com.globallogix.auth.controller;


import com.globallogix.auth.dto.*;
import com.globallogix.auth.security.JwtUtil;
import com.globallogix.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@EnableMethodSecurity
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request){
        return authService.register(request);

    }



    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request){
        AuthResponse response = authService.authenticate(request);
        ResponseCookie refreshCookie = ResponseCookie.from("refresh", response.getRefreshToken())
                .httpOnly(true)
                .maxAge(7 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, String.valueOf(refreshCookie))
                .body(response);
    }


    @GetMapping("/validate")
    public ValidationResponse validateToken(@RequestParam String token){
        boolean isValid = authService.validateToken(token);
        return ValidationResponse.builder()
                .valid(isValid)
                .message(isValid ? "Токен действителен" : "Токен не действителен")
                .build();
    }

    @GetMapping("/health")
    public HealthResponse health(){
        return HealthResponse.builder()
                .status("UP")
                .service("auth-service")
                .timeStamp(LocalDateTime.now())
                .version("1.0.0")
                .build();
    }

}
