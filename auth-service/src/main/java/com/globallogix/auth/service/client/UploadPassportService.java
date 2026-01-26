package com.globallogix.auth.service.client;

import com.globallogix.auth.dto.request.UploadPassportRequest;
import com.globallogix.auth.dto.response.PassportVerificationResponse;

public interface UploadPassportService {
    PassportVerificationResponse uploadPassportData(UploadPassportRequest request, String username);
}
