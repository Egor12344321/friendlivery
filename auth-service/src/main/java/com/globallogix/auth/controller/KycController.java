package com.globallogix.auth.controller;


import com.globallogix.auth.dto.request.DocumentVerificationRequest;
import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;
import com.globallogix.auth.service.UserService;
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

    @PostMapping("/documents/upload")
    public ResponseEntity<String> uploadPassport(
            @Valid @RequestBody DocumentVerificationRequest request
            ) {
        //отправка доков
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        userService.updateVerificationStatus(VerificationDocumentsStatus.IN_PROCESS, username);
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
