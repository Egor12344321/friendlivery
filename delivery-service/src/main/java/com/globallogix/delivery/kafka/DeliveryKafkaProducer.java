package com.globallogix.delivery.kafka;


import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.kafka.events.DeliveryEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor

public class DeliveryKafkaProducer {
    private final KafkaTemplate<String, DeliveryEventDto> kafkaTemplate;



    public void sendDeliveryCreated(Delivery delivery) {
        DeliveryEventDto event = mapDeliveryToEvent(delivery, "DELIVERY_CREATED");
        sendEvent("delivery.created", event);
        log.info("Delivery DELIVERY_CREATED event sent to kafka");
    }

    public void sendHandoverConfirmed(Delivery delivery) {
        DeliveryEventDto event = mapDeliveryToEvent(delivery, "HANDOVER_CONFIRMED");
        sendEvent("delivery.handover.confirmed", event);
        log.info("Delivery HANDOVER_CONFIRMED event sent to kafka");
    }

    public void sendDeliveryCompleted(Delivery delivery) {
        DeliveryEventDto event = mapDeliveryToEvent(delivery, "DELIVERY_COMPLETED");
        sendEvent("delivery.completed", event);
        log.info("Delivery DELIVERY_COMPLETED event sent to kafka");
    }

    public void sendDeliveryAssigned(Delivery delivery){
        DeliveryEventDto event = mapDeliveryToEvent(delivery, "DELIVERY_ASSIGNED");
        sendEvent("delivery.assigned", event);
        log.info("Delivery DELIVERY_ASSIGNED event sent to kafka");
    }


    public void sendDeliveryCancelled(Delivery delivery) {
        DeliveryEventDto event = mapDeliveryToEvent(delivery, "DELIVERY_CANCELLED");
        sendEvent("delivery.cancelled", event);
        log.info("Delivery DELIVERY_CANCELLED event sent to kafka");
    }

    private void sendEvent(String topic, DeliveryEventDto event) {
        try {
            kafkaTemplate.send(topic, event);
            log.info("Sent to {}: {}", topic, event);
        } catch (Exception e) {
            log.error("Failed to send event to {}", topic, e);
        }
    }
    private DeliveryEventDto mapDeliveryToEvent(Delivery delivery, String eventType) {
        DeliveryEventDto event = new DeliveryEventDto(eventType, delivery.getId());

        event.setSenderId(delivery.getSenderId());
        event.setCourierId(delivery.getCourierId());
        event.setPrice(delivery.getPrice());
        event.setFromAirport(delivery.getFromAirport());
        event.setToAirport(delivery.getToAirport());
        event.setDeliveryDeadline(delivery.getDeliveryDeadline());

        return event;
    }
}
