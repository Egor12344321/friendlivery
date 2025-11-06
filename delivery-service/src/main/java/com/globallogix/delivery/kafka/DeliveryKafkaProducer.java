package com.globallogix.delivery.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.kafka.events.DeliveryCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor

public class DeliveryKafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void sendDeliveryCreated(Delivery delivery) {
        Map<String, Object> event = Map.of(
                "eventType", "DELIVERY_CREATED",
                "deliveryId", delivery.getId(),
                "senderId", delivery.getSenderId(),
                "price", delivery.getPrice().toString()
        );

        sendEvent("delivery.created", event);
    }

    public void sendHandoverConfirmed(Delivery delivery) {
        Map<String, Object> event = Map.of(
                "eventType", "HANDOVER_CONFIRMED",
                "deliveryId", delivery.getId(),
                "senderId", delivery.getSenderId(),
                "courierId", delivery.getCourierId(),
                "price", delivery.getPrice().toString()
        );

        sendEvent("delivery.handover.confirmed", event);
    }

    public void sendDeliveryCompleted(Delivery delivery) {
        Map<String, Object> event = Map.of(
                "eventType", "DELIVERY_COMPLETED",
                "deliveryId", delivery.getId(),
                "courierId", delivery.getCourierId(),
                "price", delivery.getPrice().toString()
        );

        sendEvent("delivery.completed", event);
    }
    public void sendDeliveryCancelled(Delivery delivery) {
        Map<String, Object> event = Map.of(
                "eventType", "DELIVERY_CANCELLED",
                "deliveryId", delivery.getId(),
                "courierId", delivery.getCourierId(),
                "price", delivery.getPrice().toString()
        );

        sendEvent("delivery.completed", event);
    }

    private void sendEvent(String topic, Map<String, Object> event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, jsonEvent);
            log.info("Sent to {}: {}", topic, jsonEvent);
        } catch (Exception e) {
            log.error("Failed to send event to {}", topic, e);
        }
    }
}
