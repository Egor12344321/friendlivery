package com.globallogix.payment.dto.yookassa;

import java.time.Instant;
import java.util.Map;

public record YooKassaPaymentResponse(
        String id,
        String status,
        boolean paid,
        YooKassaPaymentRequest.Amount amount,
        Confirmation confirmation,
        Instant createdAt,
        String description,
        Map<String, String> metadata
) {

    public record Confirmation(String type, String confirmationUrl) {}
}