package com.globallogix.flight.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class DeliveryEventDto {
    private String eventType;
    private Long deliveryId;
    private LocalDateTime timestamp;
    private Long senderId;
    private Long courierId;
    private BigDecimal price;
    private String fromAirport;
    private String toAirport;
    private LocalDate deliveryDeadline;


    public DeliveryEventDto() {
        this.timestamp = LocalDateTime.now();
    }

    public DeliveryEventDto(String eventType, Long deliveryId) {
        this();
        this.eventType = eventType;
        this.deliveryId = deliveryId;
    }
}