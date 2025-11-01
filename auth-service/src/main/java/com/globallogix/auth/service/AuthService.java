package com.globallogix.auth.service;

import com.globallogix.auth.dto.AuthRequest;
import com.globallogix.auth.dto.AuthResponse;
import com.globallogix.auth.dto.RegisterRequest;
import com.globallogix.auth.entity.RefreshTokenEntity;
import com.globallogix.auth.entity.User;
import com.globallogix.auth.exception.InvalidCredentialsException;
import com.globallogix.auth.repository.RefreshTokenRepository;
import com.globallogix.auth.repository.UserRepository;
import com.globallogix.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshRedisService refreshRedisService;
    public AuthResponse register(RegisterRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            log.info("Ошибка регистрации из-за не идентичного мейла: {}", request.getEmail());
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()){
            log.info("Ошибка регистрации из-за не идентичности username: {}", request.getUsername());
            throw new RuntimeException("Пользователь с таким username существует");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of("USER"))
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(true)
                .build();
        User savedUser = userRepository.save(user);
        log.info("Пользователь {} успешно зарегистрирован", savedUser.getId());
        String jwtToken = jwtUtil.generateToken(savedUser);
        log.info("AccessToken generated successfully");
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);
        log.info("RefreshToken generated successfully");
        RefreshTokenEntity refreshTokenModified = RefreshTokenEntity.builder()
                .token(refreshToken).user(user)
                .build();
        refreshRedisService.saveRefreshToCache(refreshToken, user.getUsername());
        log.info("Refresh token saved in cache successfully");
        refreshTokenRepository.save(refreshTokenModified);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .type("Bearer")
                .message("Пользователь зарегистрирован")
                .build();
    }


    public AuthResponse authenticate(AuthRequest request){
        try {
            log.info("Authentication started");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );


            User user = (User) authentication.getPrincipal();
            String jwtToken = jwtUtil.generateToken(user);
            log.info("Access token generated successfully");
            String refreshToken = jwtUtil.generateRefreshToken(user);
            log.info("Refresh token generated successfully");




            return AuthResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .message("Успешно")
                    .build();
        } catch (AuthenticationException e){
            throw new InvalidCredentialsException("Неправильный логин или пароль");
        }


    }

    public boolean validateToken(String token){
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e){
            return false;
        }
    }

}
