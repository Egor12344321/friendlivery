package com.globallogix.payment.dto.yookassa;

import java.util.Map;

public record YooKassaPaymentRequest(
        Amount amount,
        PaymentMethodData paymentMethodData,
        Confirmation confirmation,
        boolean capture,
        String description,
        Map<String, String> metadata
) {
    public record Amount(String value, String currency) {}
    public record PaymentMethodData(String type) {}
    public record Confirmation(String type, String returnUrl) {}
}