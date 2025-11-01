package com.globallogix.delivery.dto.request;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDate;

public record DeliveryRequest(
        @NotNull
        String fromAirport,
        @NotNull
        String toAirport,

        LocalDate desiredDate,

        String description,
        @NotNull
        Double weight,
        String dimensions,
        String notes,
        @Future
        @NotNull
        LocalDate deliveryDeadline
) {
}
