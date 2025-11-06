package com.globallogix.service;


import com.globallogix.kafka.events.DeliveryCreatedEvent;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.password}")
    private String from;

    public void sendDeliveryOfferEmail(String email, String route, String price){
        String subject = "📦 Новая доставка для вас!";
        String text = """
            Появилась новая доставка!
            
            Маршрут: %s
            Стоимость: %s
            
            Чтобы принять доставку, перейдите в личный кабинет:
            http://localhost:3000/courier/deliveries
            
            Успейте первым - доставки быстро разбирают!""".formatted(route, price);
        sendSimpleMail(email, subject, text);
    }
    public void sendDeliveryCreated(DeliveryCreatedEvent event, String email){
        String subject = "New delivery created successfully";
        String text = """
                Congratulations! Your delivery: %s created successfully
                """.formatted(event.deliveryId());
        sendSimpleMail(email, subject, text);
    }
    private void sendSimpleMail(String email, String subject, String text) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom(from);

            javaMailSender.send(message);
            log.info("Mail sent successfully");
        } catch (Exception e){
            log.info("Sending mail failed");
        }
    }
}
