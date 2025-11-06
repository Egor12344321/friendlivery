package com.globallogix.auth.service;

import com.globallogix.auth.dto.request.AuthRequest;
import com.globallogix.auth.dto.response.AuthResponse;
import com.globallogix.auth.dto.request.RegisterRequest;
import com.globallogix.auth.entity.User;
import com.globallogix.auth.entity.UserRoles;
import com.globallogix.auth.exception.InvalidCredentialsException;
import com.globallogix.auth.repository.UserRepository;
import com.globallogix.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshRedisService refreshRedisService;


    public AuthResponse register(RegisterRequest request){

        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            log.info("Ошибка регистрации из-за не идентичного мейла: {}", request.getEmail());
            System.out.println(1);
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()){
            log.info("Ошибка регистрации из-за не идентичности username: {}", request.getUsername());
            System.out.println(4);
            throw new RuntimeException("Пользователь с таким username существует");
        }
        System.out.println(3);
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone_number(request.getPhoneNumber())
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(true)
                .build();
        System.out.println(5);
        User savedUser = userRepository.save(user);
        System.out.println(6);
        log.info("Пользователь {} успешно зарегистрирован", savedUser.getId());

        String jwtToken = jwtUtil.generateToken(savedUser);
        log.info("AccessToken generated successfully");
        System.out.println(7);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);
        log.info("RefreshToken generated successfully");
        System.out.println(8);

        refreshRedisService.saveRefreshToCache(refreshToken, user.getUsername());
        log.info("Refresh token saved in cache successfully");
        System.out.println(9);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
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
            refreshRedisService.saveRefreshToCache(refreshToken, user.getUsername());
            log.info("Refresh token saved to cache");

            return AuthResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .message("Успешная аутентификация")
                    .build();
        } catch (AuthenticationException e){
            throw new InvalidCredentialsException("Неправильный логин или пароль");
        }
    }

    public void logout(String token){
        String username = jwtUtil.extractUsername(token);
        refreshRedisService.deleteTokenFromCache(username);
    }

}
