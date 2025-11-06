package com.globallogix.payment.kafka;

import com.globallogix.payment.dto.PaymentRequest;
import com.globallogix.payment.kafka.events.DeliveryCreatedEvent;
import com.globallogix.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;


@Slf4j
@RequiredArgsConstructor
public class DeliveryCreatedListener {
    private final PaymentService paymentService;

    @KafkaListener(topics = "delivery.created")
    public void consumeDelivery(ConsumerRecord<String, DeliveryCreatedEvent> record) {
        DeliveryCreatedEvent deliveryCreatedEvent = record.value();
        PaymentRequest paymentRequest = new PaymentRequest(
                deliveryCreatedEvent.deliveryId(),
                deliveryCreatedEvent.senderId(),
                deliveryCreatedEvent.price()
        );
        log.info("PaymentRequest created");
        paymentService.createPayment(paymentRequest);

        log.info(
                "Received delivery: delivery={}, key={}, partition={}",
                record.value(),
                record.key(),
                record.partition()
        );
    }

}
