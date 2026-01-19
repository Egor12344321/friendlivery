package com.globallogix.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendMailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

//    @Async("emailTaskExecutor")
    public void sendSimpleMail(String email, String subject, String text) {
        try{
            log.info("Адрес отправителя: {}", from);
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
