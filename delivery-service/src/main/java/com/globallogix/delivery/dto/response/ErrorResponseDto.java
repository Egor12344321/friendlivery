package com.globallogix.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        @Schema(description = "Error message", example = "Delivery not found")
        String message,
        @Schema(description = "Detailed error message", example = "Delivery with id 123 not found")
        String detailedMessage,
        @Schema(description = "Error timestamp", example = "2024-01-15T10:30:00")
        LocalDateTime errorTime
) {
}
