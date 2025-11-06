package com.globallogix.flight.kafka.events;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryCreatedEvent(
        String eventId,
        Long deliveryId,
        String trackingNumber,
        Long senderId,

        String fromAirport,
        String toAirport,
        LocalDate desiredDate,
        LocalDate deliveryDeadline,

        String description,
        Double weight,
        String dimensions,
        BigDecimal price,

        String status,
        LocalDateTime createdAt
) {
}

