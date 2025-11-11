package com.globallogix.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.globallogix.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentKafkaListener {
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @KafkaListener(topics = "delivery.handover.confirmed")
    public void handleHandoverConfirmed(ConsumerRecord<String, DeliveryEventDto> record) {

        try {
            log.info("Received handover confirmed: {}", record.value());

            DeliveryEventDto event = record.value();
            paymentService.createPayment(event);
        } catch (Exception e) {
            log.error("Error processing handover confirmed event: {}", record.value(), e);
        }
    }

    @KafkaListener(topics = "delivery.completed")
    public void handleDeliveryCompleted(ConsumerRecord<String, DeliveryEventDto> record) {
        try {
            log.info("Received delivery completed: {}", record.value());

            DeliveryEventDto event = record.value();
            paymentService.captureFunds(event);
        } catch (Exception e) {
            log.error("Error processing delivery completed event: {}", record.value(), e);
        }
    }

    @KafkaListener(topics = "delivery.cancelled")
    public void handleDeliveryCancelled(ConsumerRecord<String, DeliveryEventDto> record) {

        try {
            log.info("Received delivery cancelled: {}", record.value());
            DeliveryEventDto event = record.value();
            paymentService.refundFunds(event);
        } catch (Exception e) {
            log.error("Error processing delivery cancelled event: {}", record.value(), e);
        }
    }


}
