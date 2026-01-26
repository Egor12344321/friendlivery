package com.globallogix.auth.controller;


import com.globallogix.auth.dto.request.UploadPassportRequest;
import com.globallogix.auth.dto.response.PassportVerificationResponse;
import com.globallogix.auth.service.client.UploadPassportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test/kyc")
public class TestKycController {

    private final UploadPassportService uploadPassportService;

    @PostMapping("/passport/verification")
    public ResponseEntity<PassportVerificationResponse> uploadPassportData(@Valid @RequestBody UploadPassportRequest request){
        PassportVerificationResponse response = uploadPassportService.uploadPassportData(request, "username");
        return ResponseEntity.ok(response);
    }

}
