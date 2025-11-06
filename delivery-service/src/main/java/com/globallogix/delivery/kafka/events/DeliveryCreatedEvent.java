package com.globallogix.delivery.kafka.events;

import com.globallogix.delivery.entity.Delivery;

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
    public static DeliveryCreatedEvent fromEntity(Delivery delivery) {
        return new DeliveryCreatedEvent(
                UUID.randomUUID().toString(),
                delivery.getId(),
                delivery.getTrackingNumber(),
                delivery.getSenderId(),
                delivery.getFromAirport(),
                delivery.getToAirport(),
                delivery.getDesiredDate(),
                delivery.getDeliveryDeadline(),
                delivery.getDescription(),
                delivery.getWeight(),
                delivery.getDimensions(),
                delivery.getPrice(),
                delivery.getStatus().name(),
                delivery.getCreatedAt()
        );
    }

    public String getRoute() {
        return fromAirport + " → " + toAirport;
    }

    public boolean isUrgent() {
        return deliveryDeadline != null &&
                deliveryDeadline.isBefore(LocalDate.now().plusDays(3));
    }
}