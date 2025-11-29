package com.globallogix.auth.controller;


import com.globallogix.auth.dto.request.DocumentVerificationRequest;
import com.globallogix.auth.dto.response.DocumentVerificationResponse;
import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;
import com.globallogix.auth.service.kyc.DocumentsService;
import com.globallogix.auth.service.kyc.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/kyc")
public class KycController {
    private final UserService userService;
    private final DocumentsService documentsService;

    @PostMapping("/documents/upload")
    public ResponseEntity<DocumentVerificationResponse> uploadPassport(
            @Valid @RequestBody DocumentVerificationRequest request
            ) {
        log.info("KYC-CONTROLLER: Started documents uploading - getting username from context");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        log.info("KYC-CONTROLLER: Got username from context: {}; starting uploading to db", username);
        DocumentVerificationResponse response = documentsService.uploadDocuments(request, username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply/sender")
    public ResponseEntity<String> becomeSender(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        userService.becomeSender(username);
        return ResponseEntity.ok("You become sender");
    }
}
