package com.globallogix.auth.entity;


import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_documents")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocuments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "link_to_scan")
    private String linkToPhoto;

    @Column(name = "link_to_selfie")
    private String linkToSelfie;

    @Column(name = "documents_verification_status")
    @Enumerated(EnumType.STRING)
    private VerificationDocumentsStatus documentsVerificationStatus;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;


    @PrePersist
    public void onCreate(){
        this.submittedAt = LocalDateTime.now();
    }
}
