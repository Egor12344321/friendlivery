package com.globallogix.chat.exception;


import com.globallogix.chat.dto.ErrorResponseDto;
import com.globallogix.chat.exception.custom_exceptions.ProfileNotFoundException;
import com.globallogix.chat.exception.custom_exceptions.RoutesNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RoutesNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRoutesNotFoundException(
            Exception e
    ){
        log.error("Handle RoutesNotFoundException", e);
        var errorDto = new ErrorResponseDto(
                "Routes not found",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }
    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCourierProfileNotFoundException(
            Exception e
    ){
        log.error("Handle courierNotFoundException", e);
        var errorDto = new ErrorResponseDto(
                "Courier profile not found",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }
}
