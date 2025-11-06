package com.globallogix.payment.dto;


import java.math.BigDecimal;

public record PaymentRequest(
        Long deliveryId,
        Long senderId,
        BigDecimal amount
) {

}
