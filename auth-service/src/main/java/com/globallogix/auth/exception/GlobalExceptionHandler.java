package com.globallogix.auth.exception;


import com.globallogix.auth.dto.response.AuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({TokenNotFoundException.class, InvalidTokenRefreshException.class})
    public ResponseEntity<AuthResponse> handleTokenNotFoundException(RuntimeException e){
        log.error("Handle token exception");
        ResponseCookie deleteCookie = ResponseCookie.from("refresh", "")
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(AuthResponse.builder()
                        .message(e.getMessage())
                        .build());
    }
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<?> handleRunTimeException(RuntimeException e){
        log.error("Handle runtime exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("?? Введенные данные уже зарегистрирваны в системе");
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<AuthResponse> handleUserNotFoundException(UserNotFoundException e){
        ResponseCookie deleteCookie = ResponseCookie.from("refresh", "")
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(AuthResponse.builder()
                        .message("Требуется повторный вход")
                        .build());
    }
}
