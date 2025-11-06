package com.globallogix.auth.controller;


import com.globallogix.auth.entity.User;
import com.globallogix.auth.entity.VerificationStatus;
import com.globallogix.auth.exception.UserNotFoundException;
import com.globallogix.auth.repository.UserRepository;
import com.globallogix.auth.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/kyc/approve/{userId}")
    public void approveVerification(@PathVariable Long userId){

        log.info("Статус верификации пользователя изменен на VERIFIED");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/kyc/approve/{userId}")
    public void cancelVerification(@PathVariable Long userId){
        adminService.cancelVerification(userId);
        log.info("Статус верификации пользователя изменен на CANCELLED");
    }

}
