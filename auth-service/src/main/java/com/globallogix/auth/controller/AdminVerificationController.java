package com.globallogix.auth.controller;


import com.globallogix.auth.service.admin_abilities.AdminVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin/verification")
public class AdminVerificationController {
    private final AdminVerificationService adminVerificationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/kyc/approve/{userId}")
    public void approveVerification(@PathVariable Long userId){
        adminVerificationService.approveVerification(userId);
        log.info("Статус верификации пользователя: {} изменен на VERIFIED", userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/kyc/cancel/{userId}")
    public void cancelVerification(@PathVariable Long userId){
        adminVerificationService.cancelVerification(userId);
        log.info("Статус верификации пользователя изменен на CANCELLED");
    }

}
