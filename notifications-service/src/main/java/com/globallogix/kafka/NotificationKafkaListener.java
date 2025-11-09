package com.globallogix.kafka;


import com.globallogix.kafka.events.DeliveryEventDto;
import com.globallogix.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationKafkaListener {
    private final EmailService emailService;


    @KafkaListener(topics = "delivery.created")
    public void handleDeliveryCreated(ConsumerRecord<String, DeliveryEventDto> record) {
        DeliveryEventDto event = record.value();
        try {
            log.info("Delivery {} created by sender {}",
                    event.getDeliveryId(), event.getSenderId());

            String senderEmail = "sender_" + event.getSenderId() + "@friendlivery.demo";
            String courierEmail = "courier_" + event.getSenderId() + "@friendlivery.demo";

            emailService.sendDeliveryCreated(event, senderEmail);
            emailService.sendDeliveryOfferEmail(courierEmail, event.getFromAirport() + "->" + event.getToAirport(), event.getPrice());
        } catch (Exception e) {
            log.error("Error processing delivery created: {}", event, e);
        }
    }

    @KafkaListener(topics = "delivery.assigned")
    public void handleDeliveryAssigned(ConsumerRecord<String, DeliveryEventDto> record) {
        DeliveryEventDto event = record.value();
        try {
            log.info("Delivery {} assigned to courier {}", event.getDeliveryId(), event.getCourierId());

            String courierEmail = "courier_" + event.getCourierId() + "@friendlivery.demo";
            emailService.sendDeliveryAssigned(event, courierEmail);

        } catch (Exception e) {
            log.error("Error processing delivery assigned: {}", event, e);
        }
    }

    @KafkaListener(topics = "delivery.handover.confirmed")
    public void handleHandoverConfirmed(ConsumerRecord<String, DeliveryEventDto> record) {
        DeliveryEventDto event = record.value();
        try {
            log.info("Delivery {} handover confirmed", event.getDeliveryId());

            String senderEmail = "sender_" + event.getSenderId() + "@friendlivery.demo";
            String courierEmail = "courier_" + event.getCourierId() + "@friendlivery.demo";

            emailService.sendHandoverConfirmedToSender(event, senderEmail);
            emailService.sendHandoverConfirmedToCourier(event, courierEmail);

        } catch (Exception e) {
            log.error("Error processing handover confirmed: {}", event, e);
        }
    }

    @KafkaListener(topics = "delivery.completed")
    public void handleDeliveryCompleted(ConsumerRecord<String, DeliveryEventDto> record) {
        DeliveryEventDto event = record.value();
        try {
            log.info("Delivery {} completed", event.getDeliveryId());

            String senderEmail = "sender_" + event.getSenderId() + "@friendlivery.demo";
            String courierEmail = "courier_" + event.getCourierId() + "@friendlivery.demo";

            emailService.sendDeliveryCompletedToSender(event, senderEmail);
            emailService.sendDeliveryCompletedToCourier(event, courierEmail);

        } catch (Exception e) {
            log.error("Error processing delivery completed: {}", event, e);
        }
    }

    @KafkaListener(topics = "delivery.cancelled")
    public void handleDeliveryCancelled(ConsumerRecord<String, DeliveryEventDto> record) {
        DeliveryEventDto event = record.value();
        try {
            log.info("Delivery {} cancelled", event.getDeliveryId());

            String senderEmail = "sender_" + event.getSenderId() + "@friendlivery.demo";
            emailService.sendDeliveryCancelled(event, senderEmail);

            if (event.getCourierId() != null) {
                String courierEmail = "courier_" + event.getCourierId() + "@friendlivery.demo";
                emailService.sendDeliveryCancelledToCourier(event, courierEmail);
            }

        } catch (Exception e) {
            log.error("Error processing delivery cancelled: {}", event, e);
        }
    }

}
