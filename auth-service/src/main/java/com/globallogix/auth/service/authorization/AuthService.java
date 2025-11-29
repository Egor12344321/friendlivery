package com.globallogix.auth.service.authorization;

import com.globallogix.auth.dto.request.AuthRequest;
import com.globallogix.auth.dto.response.authorization.AuthResponse;
import com.globallogix.auth.dto.request.RegisterRequest;
import com.globallogix.auth.entity.User;
import com.globallogix.auth.entity.enums.UserRoles;
import com.globallogix.auth.exception.EmailNotUniqueException;
import com.globallogix.auth.exception.InvalidCredentialsException;
import com.globallogix.auth.repository.UserRepository;
import com.globallogix.auth.security.JwtUtil;
import com.globallogix.auth.service.refresh.RedisServiceCrud;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final RedisServiceCrud redisServiceCrud;


    public AuthResponse register(RegisterRequest request){

        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            log.info("Ошибка регистрации из-за не идентичного мейла: {}", request.getEmail());
            throw new EmailNotUniqueException("Пользователь с таким email уже существует");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()){
            log.info("Ошибка регистрации из-за не идентичности username: {}", request.getUsername());
            throw new EmailNotUniqueException("Пользователь с таким username существует");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone_number(request.getPhoneNumber())
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(true)
                .roles(Set.of(UserRoles.USER))  
                .build();
        User savedUser = userRepository.save(user);

        log.info("Пользователь {} успешно зарегистрирован", savedUser.getId());

        String jwtToken = jwtUtil.generateToken(savedUser);
        log.info("AccessToken generated successfully");

        String refreshToken = jwtUtil.generateRefreshToken(savedUser);
        log.info("RefreshToken generated successfully");


        redisServiceCrud.saveRefreshToCache(refreshToken, user.getUsername());
        log.info("Refresh token saved in cache successfully");


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
            redisServiceCrud.saveRefreshToCache(refreshToken, user.getUsername());
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
        redisServiceCrud.deleteTokenFromCache(username);
    }

}
