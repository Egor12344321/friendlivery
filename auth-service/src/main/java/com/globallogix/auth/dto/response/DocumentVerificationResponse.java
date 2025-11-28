package com.globallogix.auth.dto.response;

import java.time.LocalDateTime;

public record DocumentVerificationResponse(
         boolean success,
         String message,
         String status,
         LocalDateTime submittedAt
) {
}
