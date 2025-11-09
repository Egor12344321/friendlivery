package com.globallogix.chat.kafka;

import com.globallogix.chat.kafka.events.DeliveryEventDto;
import com.globallogix.chat.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class FlightKafkaListener {
    private final MatchingService matchingService;

    @KafkaListener(topics = "delivery.created")
    public void handleDeliveryCreation(ConsumerRecord<String, DeliveryEventDto> record) {
        log.debug("FLIGHT-SERVICE: Delivery creation handled, starting matching");
        matchingService.findMatchingCouriers(record.value());
    }

}
