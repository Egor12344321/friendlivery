package com.globallogix.auth.repository;


import com.globallogix.auth.entity.UserDocuments;
import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocuments, Long> {
    List<UserDocuments> findByVerificationDocumentsStatus(VerificationDocumentsStatus status);
}
