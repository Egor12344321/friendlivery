package com.globallogix.auth.controller;


import com.globallogix.auth.entity.VerificationStatus;
import com.globallogix.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/kyc")
public class KycController {
    private final UserService userService;

    @PostMapping("/documents/upload")
    public ResponseEntity<String> uploadPassport(
            @RequestParam("passportFront") MultipartFile passportFront,
            @RequestParam("passportBack") MultipartFile passportBack,
            @RequestParam("selfie") MultipartFile selfieWithPassport
    ) {
        //отправка доков
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        userService.updateVerificationStatus(VerificationStatus.IN_PROCESS, username);
        return ResponseEntity.ok("Data sent successfully");
    }

    @PostMapping("/apply/sender")
    public ResponseEntity<String> becomeSender(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        userService.becomeSender(username);
        return ResponseEntity.ok("You become sender");
    }
}
