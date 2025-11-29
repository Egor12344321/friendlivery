package com.globallogix.auth.service.kyc;


import com.globallogix.auth.dto.request.DocumentVerificationRequest;
import com.globallogix.auth.dto.response.DocumentVerificationResponse;
import com.globallogix.auth.entity.User;
import com.globallogix.auth.entity.UserDocuments;
import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;
import com.globallogix.auth.exception.UserNotFoundException;
import com.globallogix.auth.repository.UserDocumentRepository;
import com.globallogix.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentsService {
    private final UserDocumentRepository userDocumentRepository;
    private final UserRepository userRepository;

    public DocumentVerificationResponse uploadDocuments(DocumentVerificationRequest request, String username){
        log.info("KYC-SERVICE: Starting uploading documents");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь для загрузки документов не найден"));
        UserDocuments userDocuments = mapFromRequestToEntity(request, user);
        UserDocuments saved_documents = userDocumentRepository.save(userDocuments);
        log.info("KYC-SERVICE: documents saved to db");
        return DocumentVerificationResponse.mapFromEntityToResponseSuccess(saved_documents);
    }


    private UserDocuments mapFromRequestToEntity(DocumentVerificationRequest request, User user){
        return UserDocuments.builder()
                .documentsVerificationStatus(VerificationDocumentsStatus.IN_PROCESS)
                .linkToPhoto(request.documentPageUrl())
                .linkToSelfie(request.photoWithPassportUrl())
                .user(user)
                .build();
    }
}
