package com.globallogix.payment.service;


import com.globallogix.payment.dto.PaymentRequest;
import com.globallogix.payment.dto.PaymentResponse;
import com.globallogix.payment.dto.yookassa.YooKassaPaymentRequest;
import com.globallogix.payment.dto.yookassa.YooKassaPaymentResponse;
import com.globallogix.payment.entity.Payment;
import com.globallogix.payment.entity.PaymentStatus;
import com.globallogix.payment.kafka.events.DeliveryCreatedEvent;
import com.globallogix.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate;
    private final YooKassaClient yooKassaClient;

    public PaymentResponse createPayment(PaymentRequest request){
        log.debug("Creating payment started");
        Payment saved_payment = paymentRepository.save(mapRequestToEntity(request));
        log.info("Payment saved to db");
        return mapPaymentToResponse(saved_payment);
    }

    private PaymentResponse mapPaymentToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getDeliveryId(),
                PaymentStatus.PROCESSING,
                "Платеж успешно создан"
        );
    }

    private Payment mapRequestToEntity(PaymentRequest request){
        return Payment.builder()
                .amount(request.amount())
                .deliveryId(request.deliveryId())
                .senderId(request.senderId())
                .build();

    }


    @KafkaListener(topics = "delivery.created")
    public void createPaymentForDelivery(DeliveryCreatedEvent event) {
        log.info("Received delivery created event: {}", event.deliveryId());

        try {
            YooKassaPaymentRequest ykRequest = createYooKassaRequest(event);
            YooKassaPaymentResponse ykResponse = yooKassaClient.createPayment(ykRequest);

            Payment payment = Payment.builder()
                    .deliveryId(event.deliveryId())
                    .senderId(event.senderId())
                    .amount(event.price())
                    .externalId(ykResponse.id()) // ID из ЮKassa
                    .confirmationUrl(ykResponse.confirmation().confirmationUrl()) // Ссылка на оплату
                    .status(PaymentStatus.PROCESSING)
                    .description("Оплата доставки #" + event.deliveryId())
                    .build();

            Payment savedPayment = paymentRepository.save(payment);

            log.info("Payment created for delivery {}. Confirmation URL: {}",
                    event.deliveryId(), ykResponse.confirmation().confirmationUrl());

        } catch (Exception e) {
            log.error("Failed to create payment for delivery: {}", event.deliveryId(), e);
        }
    }

    private YooKassaPaymentRequest createYooKassaRequest(DeliveryCreatedEvent event) {
        return new YooKassaPaymentRequest(
                new YooKassaPaymentRequest.Amount(event.price().toString(), "RUB"),
                new YooKassaPaymentRequest.PaymentMethodData("bank_card"),
                new YooKassaPaymentRequest.Confirmation("redirect", "https://friendlivery.ru/payment/success"),
                true,
                "Оплата доставки #" + event.deliveryId(),
                Map.of(
                        "delivery_id", event.deliveryId().toString(),
                        "sender_id", event.senderId().toString()
                )
        );
    }



}
