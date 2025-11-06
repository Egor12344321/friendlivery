package com.globallogix.payment.dto;

import com.globallogix.payment.entity.PaymentStatus;

public record PaymentResponse (
        String paymentId,
        Long deliveryId,
        PaymentStatus status,
        String message
){
}
