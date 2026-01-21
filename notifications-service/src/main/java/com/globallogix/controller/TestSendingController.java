package com.globallogix.controller;


import com.globallogix.service.EmailService;
import com.globallogix.service.SendMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/send")
@RequiredArgsConstructor
@Slf4j
public class TestSendingController {

    private final SendMailService sendMailService;

    @PostMapping
    public void sendMail(@RequestParam("email") String email){
        log.info("Started testing email sending to email: {}", email);
        sendMailService.sendSimpleMail(email, "Тест", "Тест");
    }

}
