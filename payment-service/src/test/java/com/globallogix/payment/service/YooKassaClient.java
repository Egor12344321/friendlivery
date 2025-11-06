package com.globallogix.payment.service;

import com.globallogix.payment.dto.yookassa.YooKassaPaymentRequest;
import com.globallogix.payment.dto.yookassa.YooKassaPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class YooKassaClient {

    public YooKassaPaymentResponse createPayment(YooKassaPaymentRequest request) {
        log.info("📨 [MOCK] Sending to YooKassa: amount={}, description={}",
                request.amount().value(), request.description());


        String externalId = "yk_" + UUID.randomUUID().toString().substring(0, 16);
        String confirmationUrl = "http://localhost:8080/mock/yookassa/payment/" + externalId;

        return new YooKassaPaymentResponse(
                externalId,
                "pending",
                false,
                request.amount(),
                new YooKassaPaymentResponse.Confirmation("redirect", confirmationUrl),
                java.time.Instant.now(),
                request.description(),
                request.metadata()
        );
    }
}