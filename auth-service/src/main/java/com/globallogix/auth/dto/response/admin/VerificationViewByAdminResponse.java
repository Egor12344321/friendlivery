package com.globallogix.auth.dto.response.admin;

import com.globallogix.auth.dto.response.documents.DocumentVerificationResponse;
import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;

import java.util.List;

public record VerificationViewByAdminResponse(
        int numberOfPendingVerifications,
        List<DocumentVerificationResponse> documents,
        VerificationDocumentsStatus status
) {
}
