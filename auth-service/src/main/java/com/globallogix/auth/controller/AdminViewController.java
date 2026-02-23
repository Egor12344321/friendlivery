package com.globallogix.auth.controller;


import com.globallogix.auth.dto.response.admin.VerificationViewByAdminResponse;
import com.globallogix.auth.service.admin_abilities.AdminViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/view")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin View Management")
public class AdminViewController {
    private final AdminViewService adminViewService;

    @GetMapping("/kyc/pending")
    @Operation(description = "Получить все заявки пользователей на верификацию аккаунта")
    @PreAuthorize("hasRole('ADMIN')")
    public VerificationViewByAdminResponse getPendingVerifications(){
        log.info("ADMIN-VIEW-CONTROLLER: Starting getting pending verifications");
        return adminViewService.viewDocumentsInProgress();
    }

    @GetMapping("/kyc/verified")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Получить все верифицированные заявки")
    public VerificationViewByAdminResponse getCheckedVerifications(){
        log.info("ADMIN-VIEW-CONTROLLER: Starting getting checked verifications");
        return adminViewService.viewDocumentsVerified();
    }

    @GetMapping("/kyc/not-verified")
    @PreAuthorize("hasRole('ADMIN')")

    public VerificationViewByAdminResponse getUnCheckedVerifications(){
        log.info("ADMIN-VIEW-CONTROLLER: Starting getting unchecked verifications");
        return adminViewService.viewDocumentsNotVerified();
    }
}
