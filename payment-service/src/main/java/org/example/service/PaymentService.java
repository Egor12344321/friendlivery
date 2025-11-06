package org.example.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.YooKassaPaymentRequest;
import org.example.dto.YooKassaPaymentResponse;
import org.example.entity.Payment;
import org.example.entity.PaymentStatus;
import org.example.kafka.events.DeliveryCreatedEvent;
import org.example.repository.PaymentRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final YokassaClient yokassaClient;
    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "delivery.created")
    private void createPayment(DeliveryCreatedEvent event){
        log.info("Creating payment");

        try{
            YooKassaPaymentRequest request = new YooKassaPaymentRequest(
                    event.price(),
                    new YooKassaPaymentRequest.Confirmation("redirect", "https://friendlivery.ru/success"),
                    "Оплата доставки #" + event.deliveryId(),
                    Map.of(
                            "delivery_id", event.deliveryId().toString(),
                            "sender_id", event.senderId().toString()
                    )
            );
            YooKassaPaymentResponse response = yokassaClient.createPayment(request);
            Payment payment = Payment.builder()
                    .deliveryId(event.deliveryId())
                    .senderId(event.senderId())
                    .amount(event.price())
                    .status(PaymentStatus.PENDING)
                    .externalId(response.id())
                    .confirmationUrl(response.confirmation().confirmationUrl())
                    .description(response.description())
                    .build();

            paymentRepository.save(payment);
            log.info("Payment saved successfully");

        } catch (Exception e){
            log.error("Failed to create payment for delivery: {}", event.deliveryId());
        }
    }



}
