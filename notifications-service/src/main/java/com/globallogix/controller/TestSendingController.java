package com.globallogix.controller;


import com.globallogix.service.EmailService;
import com.globallogix.service.SendMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/send")
@RequiredArgsConstructor
public class TestSendingController {

    private final SendMailService sendMailService;

    @PostMapping
    public void sendMail(@RequestParam String email){
        sendMailService.sendSimpleMail(email, "Тест", "Тест");
    }

}
