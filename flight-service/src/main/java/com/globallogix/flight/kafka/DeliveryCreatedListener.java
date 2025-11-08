package com.globallogix.flight.kafka;

import com.globallogix.flight.kafka.events.DeliveryEventDto;
import com.globallogix.flight.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;


@Slf4j
@RequiredArgsConstructor
public class DeliveryCreatedListener {
    private final MatchingService matchingService;

    @KafkaListener(topics = "delivery.created")
    public void handleDeliveryCreation(ConsumerRecord<String, DeliveryEventDto> record) {
        log.debug("FLIGHT-SERVICE: Delivery creation handled, starting matching");
        matchingService.findMatchingCouriers(record.value());
    }

}
