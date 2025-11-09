package com.globallogix.delivery.dto.response;

import com.globallogix.delivery.entity.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Builder
public record DeliveryResponse(
         @Schema(description = "Delivery ID", example = "1")
         Long id,
         @Schema(description = "Sender ID", example = "12345")
         Long senderId,
         @Schema(description = "Courier ID", example = "67890")
         Long courierId,
         @Schema(description = "Departure airport", example = "SVO")
         String fromAirport,
         @Schema(description = "Destination airport", example = "JFK")
         String toAirport,
         @Schema(description = "Desired date", example = "2024-12-25")
         LocalDate desiredDate,
         @Schema(description = "Package description", example = "Important documents")
         String description,
         @Schema(description = "Package weight", example = "2.5")
         Double weight,
         @Schema(description = "Package dimensions", example = "30x20x10 cm")
         String dimensions,
         @Schema(description = "Delivery status", example = "CREATED")
         DeliveryStatus status,
         @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
         LocalDateTime createdAt,
         @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
         LocalDateTime updatedAt,
         @Schema(description = "Delivery price", example = "150.00")
         BigDecimal price
) {

}