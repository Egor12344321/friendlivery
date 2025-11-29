package com.globallogix.auth.controller;


import com.globallogix.auth.dto.response.admin.VerificationViewByAdminResponse;
import com.globallogix.auth.service.admin_abilities.AdminViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/view")
@RequiredArgsConstructor
@Slf4j
public class AdminViewController {
    private final AdminViewService adminViewService;

    @GetMapping("/kyc/pending")
    public VerificationViewByAdminResponse getPendingVerifications(){
        log.info("ADMIN-VIEW-CONTROLLER: Starting getting pending verifications");
        return adminViewService.viewDocumentsInProgress();
    }

    @GetMapping("/kyc/verified")
    public VerificationViewByAdminResponse getCheckedVerifications(){
        log.info("ADMIN-VIEW-CONTROLLER: Starting getting checked verifications");
        return adminViewService.viewDocumentsVerified();
    }

    @GetMapping("/kyc/not-verified")
    public VerificationViewByAdminResponse getUnCheckedVerifications(){
        log.info("ADMIN-VIEW-CONTROLLER: Starting getting unchecked verifications");
        return adminViewService.viewDocumentsNotVerified();
    }
}
