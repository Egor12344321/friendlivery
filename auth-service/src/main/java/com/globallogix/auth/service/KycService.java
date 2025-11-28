package com.globallogix.auth.service;


import com.globallogix.auth.dto.request.DocumentVerificationRequest;
import com.globallogix.auth.dto.response.DocumentVerificationResponse;
import com.globallogix.auth.entity.UserDocuments;
import com.globallogix.auth.entity.enums.DocumentsVerificationStatus;
import com.globallogix.auth.repository.UserDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycService {
    private final UserDocumentRepository userDocumentRepository;

    public DocumentVerificationResponse uploadDocuments(DocumentVerificationRequest request){
        log.info("KYC-SERVICE: Starting uploading documents");

    }

    private UserDocuments mapFromRequestToEntity(DocumentVerificationRequest request){
        return UserDocuments.builder()
                .documentsVerificationStatus(DocumentsVerificationStatus.IN_PROCESS)
                .linkToPhoto(request.documentPageUrl())
                .

                .build();
    }
}
