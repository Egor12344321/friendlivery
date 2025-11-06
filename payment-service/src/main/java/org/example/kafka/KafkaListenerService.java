package org.example.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.PaymentEventDto;
import org.example.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;

import java.math.BigDecimal;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class KafkaListenerService {
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @KafkaListener(topics = "delivery.handover.confirmed")
    public void handleHandoverConfirmed(String jsonEvent) {
        try {
            log.info("Received handover confirmed event: {}", jsonEvent);

            PaymentEventDto event = objectMapper.readValue(jsonEvent, PaymentEventDto.class);

            paymentService.createPayment(event);

        } catch (Exception e) {
            log.error("Error processing handover confirmed event: {}", jsonEvent, e);
        }
    }

    @KafkaListener(topics = "delivery.completed")
    public void handleDeliveryCompleted(String jsonEvent) {
        try {
            log.info("Received delivery completed event: {}", jsonEvent);

            PaymentEventDto event = objectMapper.readValue(jsonEvent, PaymentEventDto.class);

            paymentService.captureFunds(event);

        } catch (Exception e) {
            log.error("Error processing delivery completed event: {}", jsonEvent, e);
        }
    }

    @KafkaListener(topics = "delivery.cancelled")
    public void handleDeliveryCancelled(String jsonEvent) {
        try {
            log.info("Received delivery cancelled event: {}", jsonEvent);

            PaymentEventDto event = objectMapper.readValue(jsonEvent, PaymentEventDto.class);

            paymentService.refundFunds(event);

        } catch (Exception e) {
            log.error("Error processing delivery cancelled event: {}", jsonEvent, e);
        }
    }
}
