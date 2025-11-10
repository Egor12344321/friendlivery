package com.globallogix.kafka;


import com.globallogix.client.UserClient;
import com.globallogix.kafka.events.DeliveryEventDto;
import com.globallogix.kafka.events.PaymentEventDto;
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
    private final UserClient userClient;

    @KafkaListener(topics = "delivery.created")
    public void handleDeliveryCreated(ConsumerRecord<String, DeliveryEventDto> record) {
        DeliveryEventDto event = record.value();
        try {
            log.info("Delivery {} created by sender {}",
                    event.getDeliveryId(), event.getSenderId());

            String senderEmail = userClient.getUserEmail(record.value().getSenderId());


            emailService.sendDeliveryCreated(event, senderEmail);
        } catch (Exception e) {
            log.error("Error processing delivery created: {}", event, e);
        }
    }

    @KafkaListener(topics = "delivery.assigned")
    public void handleDeliveryAssigned(ConsumerRecord<String, DeliveryEventDto> record) {
        DeliveryEventDto event = record.value();
        try {
            log.info("Delivery {} assigned to courier {}", event.getDeliveryId(), event.getSenderId());

            String senderEmail = userClient.getUserEmail(record.value().getSenderId());
            emailService.sendDeliveryAssigned(event, senderEmail);

        } catch (Exception e) {
            log.error("Error processing delivery assigned: {}", event, e);
        }
    }

    @KafkaListener(topics = "delivery.handover.confirmed")
    public void handleHandoverConfirmed(ConsumerRecord<String, DeliveryEventDto> record) {
        DeliveryEventDto event = record.value();
        try {
            log.info("Delivery {} handover confirmed", event.getDeliveryId());

            String senderEmail = userClient.getUserEmail(record.value().getSenderId());
            String courierEmail = userClient.getUserEmail(record.value().getCourierId());

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

            String senderEmail = userClient.getUserEmail(record.value().getSenderId());
            String courierEmail = userClient.getUserEmail(record.value().getCourierId());

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

            String senderEmail = userClient.getUserEmail(record.value().getSenderId());
            emailService.sendDeliveryCancelled(event, senderEmail);

            if (event.getCourierId() != null) {
                String courierEmail = userClient.getUserEmail(record.value().getCourierId());
                emailService.sendDeliveryCancelledToCourier(event, courierEmail);
            }

        } catch (Exception e) {
            log.error("Error processing delivery cancelled: {}", event, e);
        }
    }

    @KafkaListener(topics = "courier.notifications")
    public void sendCourierForMatchedDelivery(ConsumerRecord<String, DeliveryEventDto> record){
        DeliveryEventDto event = record.value();
        try {
            log.info("New delivery for courier: {}", event.getCourierId());

            String courierEmail = userClient.getUserEmail(record.value().getCourierId());
            emailService.sendMatchedDelivery(event, courierEmail);


        } catch (Exception e) {
            log.error("Error processing courier notification: {}", event, e);
        }
    }


    @KafkaListener(topics = "payment-notification")
    public void sendPaymentUrlToSender(ConsumerRecord<String, PaymentEventDto> record){
        PaymentEventDto event = record.value();
        try {
            log.info("Sending confirmation url to sender: {}", event.getUserId());

            String senderEmail = userClient.getUserEmail(record.value().getUserId());
            emailService.sendPaymentUrl(event, senderEmail);


        } catch (Exception e) {
            log.error("Error processing courier notification: {}", event, e);
        }
    }

}
