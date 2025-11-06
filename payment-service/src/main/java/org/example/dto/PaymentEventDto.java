package org.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PaymentEventDto {
    private String eventType;
    private Long deliveryId;
    private Long senderId;
    private Long courierId;
    private BigDecimal price;
    private Instant timestamp;
}