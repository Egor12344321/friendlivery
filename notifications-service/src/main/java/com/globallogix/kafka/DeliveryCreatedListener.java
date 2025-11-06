package com.globallogix.kafka;

import com.globallogix.kafka.events.DeliveryCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;


@Slf4j
public class DeliveryCreatedListener {

    @KafkaListener(topics = "delivery.created")
    public void consumeOrder(ConsumerRecord<String, DeliveryCreatedEvent> record) {
        log.info(
                "Received delivery: order={}, key={}, partition={}",
                record.value(),
                record.key(),
                record.partition()
        );
    }

}
