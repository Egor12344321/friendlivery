package com.globallogix.delivery.kafka;


import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.kafka.events.DeliveryCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryKafkaProducer {
    private final KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate;


    public void sendDeliveryToKafka(DeliveryCreatedEvent delivery) {
        kafkaTemplate.send("delivery.created", delivery.eventId(), delivery);
        log.info("Delivery sent to kafka: id={}", delivery.eventId());
    }
}
