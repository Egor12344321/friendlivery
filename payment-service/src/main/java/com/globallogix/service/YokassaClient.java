package com.globallogix.service;


import lombok.extern.slf4j.Slf4j;
import com.globallogix.dto.YooKassaPaymentRequest;
import com.globallogix.dto.YooKassaPaymentResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class YokassaClient {
    public YooKassaPaymentResponse createPayment(YooKassaPaymentRequest request) {
        log.info("Creating payment in YooKassa: {}", request.description());



        String externalId = "yk_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String confirmationUrl = "http://localhost:8080/mock/yookassa/payment/" + externalId;

        return new YooKassaPaymentResponse(
                externalId,                    // ID в системе ЮKassa
                "pending",
                false,
                request.amount(),
                new YooKassaPaymentResponse.Confirmation("redirect", confirmationUrl),
                java.time.Instant.now(),
                request.description(),
                request.metadata()
        );
    }

    public void capturePayment(String paymentId, BigDecimal price) {
        log.info("CAPTURING payment: id={}, amount={}", paymentId, price);
    }

    public void cancelPayment(String paymentId) {
        log.info("CANCELLING payment: id={}", paymentId);
    }

    public void refundPayment(String paymentId, BigDecimal amount) {
        log.info("REFUNDING payment: id={}, amount={}", paymentId, amount);
    }

    public void payoutToCourier(Long courierId, BigDecimal amount) {
        log.info("PAYOUT to courier: id={}, amount={}", courierId, amount);
    }

}
