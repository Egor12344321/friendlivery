package com.globallogix.auth.service.admin_abilities;


import com.globallogix.auth.dto.response.documents.DocumentVerificationResponse;
import com.globallogix.auth.dto.response.admin.VerificationViewByAdminResponse;
import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;
import com.globallogix.auth.repository.UserDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminViewService {
    private final UserDocumentRepository userDocumentRepository;

    public VerificationViewByAdminResponse viewDocumentsByStatus(VerificationDocumentsStatus status) {
        List<DocumentVerificationResponse> documents = getDocumentsByStatus(status);
        return mapToViewResponse(documents, status);
    }

    public VerificationViewByAdminResponse viewDocumentsInProgress(){
        List<DocumentVerificationResponse> documentVerificationResponses = getDocumentsByStatus(VerificationDocumentsStatus.IN_PROCESS);
        return mapToViewResponse(documentVerificationResponses, VerificationDocumentsStatus.IN_PROCESS);
    }

    public VerificationViewByAdminResponse viewDocumentsVerified(){
        List<DocumentVerificationResponse> documentVerificationResponses = getDocumentsByStatus(VerificationDocumentsStatus.VERIFIED);
        return mapToViewResponse(documentVerificationResponses, VerificationDocumentsStatus.VERIFIED);
    }

    public VerificationViewByAdminResponse viewDocumentsNotVerified(){
        List<DocumentVerificationResponse> documentVerificationResponses = getDocumentsByStatus(VerificationDocumentsStatus.NOT_VERIFIED);
        return mapToViewResponse(documentVerificationResponses, VerificationDocumentsStatus.NOT_VERIFIED);
    }


    private List<DocumentVerificationResponse> getDocumentsByStatus(VerificationDocumentsStatus status){
        return userDocumentRepository.findByDocumentsVerificationStatus(status)
                .stream().map(DocumentVerificationResponse::mapFromEntityToResponseSuccess).toList();
    }

    private VerificationViewByAdminResponse mapToViewResponse(List<DocumentVerificationResponse> documents, VerificationDocumentsStatus status){
        return new VerificationViewByAdminResponse(
                documents.size(),
                documents,
                status
        );
    }
}
