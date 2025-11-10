package org.example.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.dto.YooKassaPaymentRequest;
import org.example.dto.YooKassaPaymentResponse;
import org.example.entity.Payment;
import org.example.entity.PaymentStatus;
import org.example.kafka.DeliveryEventDto;
import org.example.kafka.PaymentEventDto;
import org.example.repository.PaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final YokassaClient yokassaClient;
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEventDto> kafkaTemplate;

    public void createPayment(DeliveryEventDto event){
        log.info("Creating payment");

        try{
            YooKassaPaymentRequest request = new YooKassaPaymentRequest(
                    event.getPrice(),
                    new YooKassaPaymentRequest.Confirmation("redirect", "https://friendlivery.ru/success"),
                    "Оплата доставки #" + event.getDeliveryId(),
                    Map.of(
                            "delivery_id", event.getDeliveryId().toString(),
                            "sender_id", event.getDeliveryId().toString()
                    )
            );
            YooKassaPaymentResponse response = yokassaClient.createPayment(request);
            Payment payment = Payment.builder()
                    .deliveryId(event.getDeliveryId())
                    .senderId(event.getSenderId())
                    .amount(event.getPrice())
                    .status(PaymentStatus.PENDING)
                    .externalId(response.id())
                    .confirmationUrl(response.confirmation().confirmationUrl())
                    .description(response.description())
                    .build();

            paymentRepository.save(payment);
            log.info("Payment saved successfully");
            kafkaTemplate.send("payment-notification", new PaymentEventDto(event.getSenderId(), response.confirmation().confirmationUrl(), event.getDeliveryId()));
            log.info("PAYMENT-SERVICE: Payment confirmation url sent to kafka");
        } catch (Exception e){
            log.error("Failed to create payment for delivery: {}", event.getDeliveryId());
        }
    }


    public void captureFunds(DeliveryEventDto event) {
        try {
            log.info("Capturing funds for delivery: {}, courier: {}, amount: {}",
                    event.getDeliveryId(), event.getCourierId(), event.getPrice());

            Payment payment = paymentRepository.findByDeliveryId(event.getDeliveryId())
                    .orElseThrow(() -> new RuntimeException("Payment not found for delivery: " + event.getDeliveryId()));

            if (payment.getStatus() != PaymentStatus.PENDING) {
                throw new RuntimeException("Payment is not in PENDING status for delivery: " + event.getDeliveryId());
            }

            yokassaClient.capturePayment(payment.getExternalId(), event.getPrice());

            payment.setStatus(PaymentStatus.CAPTURED);
            paymentRepository.save(payment);

            payoutToCourier(event.getCourierId(), event.getPrice());

            log.info("Funds captured successfully for delivery: {}", event.getDeliveryId());

        } catch (Exception e) {
            log.error("Failed to capture funds for delivery: {}", event.getDeliveryId(), e);
        }
    }


    public void refundFunds(DeliveryEventDto event) {
        try {
            log.info("Refunding funds for delivery: {}", event.getDeliveryId());

            Payment payment = paymentRepository.findByDeliveryId(event.getDeliveryId())
                    .orElseThrow(() -> new RuntimeException("Payment not found for delivery: " + event.getDeliveryId()));

            if (payment.getStatus() == PaymentStatus.PENDING) {
                yokassaClient.cancelPayment(payment.getExternalId());
                payment.setStatus(PaymentStatus.CANCELLED);
                paymentRepository.save(payment);
                log.info("Payment cancelled for delivery: {}", event.getDeliveryId());
            }
            else if (payment.getStatus() == PaymentStatus.CAPTURED) {
                yokassaClient.refundPayment(payment.getExternalId(), event.getPrice());
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
                log.info("Payment refunded for delivery: {}", event.getDeliveryId());
            }

        } catch (Exception e) {
            log.error("Failed to refund funds for delivery: {}", event.getDeliveryId(), e);
        }
    }


    private void payoutToCourier(Long courierId, BigDecimal amount) {
        try {
            BigDecimal courierAmount = amount.multiply(BigDecimal.valueOf(0.8));

            yokassaClient.payoutToCourier(courierId, courierAmount);

            log.info("Paid out to courier: {}, amount: {}", courierId, courierAmount);

        } catch (Exception e) {
            log.error("Failed to payout to courier: {}", courierId, e);
        }
    }
}
