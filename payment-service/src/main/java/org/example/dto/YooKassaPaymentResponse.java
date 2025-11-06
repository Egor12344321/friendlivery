package org.example.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record YooKassaPaymentResponse (
        String id,
        String status,
        boolean paid,
        BigDecimal amount,
        Confirmation confirmation,
        Instant createdAt,
        String description,
        Map<String, String> metadata
){
    public record Confirmation(String type, String confirmationUrl) {}
}
