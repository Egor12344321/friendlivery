package com.globallogix.auth;


import com.globallogix.auth.service.authorization.AuthService;
import com.globallogix.auth.service.refresh.RedisServiceCrud;
import org.junit.jupiter.api.Test;

import com.globallogix.auth.dto.request.AuthRequest;
import com.globallogix.auth.dto.request.RegisterRequest;
import com.globallogix.auth.dto.response.authorization.AuthResponse;
import com.globallogix.auth.entity.User;
import com.globallogix.auth.exception.InvalidCredentialsException;
import com.globallogix.auth.repository.UserRepository;
import com.globallogix.auth.security.JwtUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RedisServiceCrud redisServiceCrud;

    @InjectMocks
    private AuthService authService;

    @Test
    void register() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUsername("testuser");
        request.setPhoneNumber("+123456789");
        request.setFirstName("John");
        request.setLastName("Doe");

        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(savedUser)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(savedUser)).thenReturn("refresh-token");

        AuthResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Bearer", result.getType());
        verify(userRepository).findByEmail(request.getEmail());
        verify(userRepository).findByUsername(request.getUsername());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(savedUser);
        verify(jwtUtil).generateRefreshToken(savedUser);
        verify(redisServiceCrud).saveRefreshToCache("refresh-token", "testuser");
    }

    @Test
    void register_ExistingEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setUsername("newuser");

        User existingUser = new User();
        existingUser.setEmail("existing@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));

        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ExistingUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setUsername("existinguser");

        User existingUser = new User();
        existingUser.setUsername("existinguser");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));

        assertEquals("Пользователь с таким username существует", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate() {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(user)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("refresh-token");

        AuthResponse result = authService.authenticate(request);

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(user);
        verify(jwtUtil).generateRefreshToken(user);
        verify(redisServiceCrud).saveRefreshToCache("refresh-token", "testuser");
    }

    @Test
    void authenticate_InvalidCredentials() {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new InvalidCredentialsException("Неправильный логин или пароль"));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> authService.authenticate(request));

        assertEquals("Неправильный логин или пароль", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void logout() {
        String token = "jwt-token";
        String username = "testuser";

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        doNothing().when(redisServiceCrud).deleteTokenFromCache(username);

        authService.logout(token);

        verify(jwtUtil).extractUsername(token);
        verify(redisServiceCrud).deleteTokenFromCache(username);
    }
}