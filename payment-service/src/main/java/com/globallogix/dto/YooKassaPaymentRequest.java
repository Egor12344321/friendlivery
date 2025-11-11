package com.globallogix.dto;

import java.math.BigDecimal;
import java.util.Map;

public record YooKassaPaymentRequest(
        BigDecimal amount,
        Confirmation confirmation,
        String description,
        Map<String, String> metadata
) {
    public record Confirmation(String type, String returnUrl) {}
}
