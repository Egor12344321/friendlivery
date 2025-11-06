package org.example.service;


import lombok.extern.slf4j.Slf4j;
import org.example.dto.YooKassaPaymentRequest;
import org.example.dto.YooKassaPaymentResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Component
@Slf4j
public class YokassaClient {
    public YooKassaPaymentResponse createPayment(YooKassaPaymentRequest request) {
        log.info("📨 [MOCK] Creating payment in YooKassa: {}", request.description());



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
}
