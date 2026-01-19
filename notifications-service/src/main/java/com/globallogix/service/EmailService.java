package com.globallogix.service;


import com.globallogix.kafka.events.DeliveryEventDto;
import com.globallogix.kafka.events.PaymentEventDto;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final SendMailService sendMailService;


    public void sendDeliveryOfferEmail(String email, String route, BigDecimal price){
        String subject = "Новая доставка для вас!";
        String text = """
            Появилась новая доставка!
            
            Маршрут: %s
            Стоимость: %s
            
            Успейте первым - доставки быстро разбирают!""".formatted(route, price);
        log.info("EMAIL-SERVICE: Sending delivery offer to {}", email);
        sendMailService.sendSimpleMail(email, subject, text);
    }
    public void sendDeliveryCreated(DeliveryEventDto event, String email){
        String subject = "New delivery created successfully";
        String text = """
                Congratulations! Your delivery: %s created successfully
                """.formatted(event.getDeliveryId());
        log.info("EMAIL-SERVICE: Sending delivery creation to: {}", email);
        sendMailService.sendSimpleMail(email, subject, text);
    }

    public void sendMatchedDelivery(DeliveryEventDto event, String courierEmail) {
        String subject = "New matched delivery " + event.getDeliveryId();
        log.info("NOTIFICATION_matched: To: {}, Subject: {}", courierEmail, subject);
    }

    public void sendDeliveryAssigned(DeliveryEventDto event, String courierEmail) {
        String subject = "Delivery Assigned " + event.getDeliveryId();
        log.info("NOTIFICATION1: To: {}, Subject: {}", courierEmail, subject);
    }

    public void sendHandoverConfirmedToSender(DeliveryEventDto event, String email) {
        String subject = "Handover Confirmed " + event.getDeliveryId();
        log.info("NOTIFICATION2: To: {}, Subject: {}", email, subject);
    }

    public void sendHandoverConfirmedToCourier(DeliveryEventDto event, String email) {
        String subject = "Delivery Details " + event.getDeliveryId();
        log.info("NOTIFICATION3: To: {}, Subject: {}", email, subject);
    }

    public void sendDeliveryCompletedToSender(DeliveryEventDto event, String email) {
        String subject = "Delivery Completed " + event.getDeliveryId();
        log.info("NOTIFICATION4: To: {}, Subject: {}", email, subject);
    }

    public void sendDeliveryCompletedToCourier(DeliveryEventDto event, String email) {
        String subject = "Payment Processed " + event.getDeliveryId();
        log.info("NOTIFICATION5: To: {}, Subject: {}", email, subject);
    }

    public void sendDeliveryCancelled(DeliveryEventDto event, String email) {
        String subject = "Delivery Cancelled " + event.getDeliveryId();
        log.info("NOTIFICATION6: To: {}, Subject: {}", email, subject);
    }

    public void sendDeliveryCancelledToCourier(DeliveryEventDto event, String email) {
        String subject = "Delivery Cancelled " + event.getDeliveryId();
        log.info("NOTIFICATION7: To: {}, Subject: {}", email, subject);
    }

    public void sendPaymentUrl(PaymentEventDto event, String senderEmail) {
        String subject = "Payment for delivery: " + event.getDeliveryId();
        log.info("NOTIFICATION8: To: {}, Subject: {}", senderEmail, subject);
    }
}
