package com.globallogix.delivery.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDate;

public record DeliveryRequest(
        @NotNull
        @Schema(description = "Departure airport code", example = "SVO")
        String fromAirport,

        @NotNull
        @Schema(description = "Destination airport code", example = "JFK")
        String toAirport,

        @Schema(description = "Desired delivery date", example = "2024-12-25")
        LocalDate desiredDate,

        @Schema(description = "Package description", example = "Important documents")
        String description,
        @Schema(description = "Package weight in kg", example = "2.5")
        @NotNull
        Double weight,
        @Schema(description = "Package dimensions", example = "30x20x10 cm")
        String dimensions,
        @Schema(description = "Additional notes", example = "Fragile content")
        String notes,
        @Future
        @NotNull
        @Schema(description = "Delivery deadline", example = "2025-12-30")
        LocalDate deliveryDeadline
) {
}
