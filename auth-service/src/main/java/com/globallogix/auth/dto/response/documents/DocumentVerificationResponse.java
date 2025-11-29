package com.globallogix.auth.dto.response.documents;

import com.globallogix.auth.entity.UserDocuments;

import java.time.LocalDateTime;

public record DocumentVerificationResponse(
         boolean success,
         String message,
         String status,
         LocalDateTime submittedAt
) {
    public static DocumentVerificationResponse mapFromEntityToResponseSuccess(UserDocuments document){
        return new DocumentVerificationResponse(
                true,
                "Документы успешно загружены",
                document.getDocumentsVerificationStatus().name(),
                document.getSubmittedAt()
        );
    }

}
