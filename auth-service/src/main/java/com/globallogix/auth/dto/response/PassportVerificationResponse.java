package com.globallogix.auth.dto.response;

public record PassportVerificationResponse(
        boolean success,
        String message
) {
}
