package com.globallogix.auth.entity;


import com.globallogix.auth.entity.enums.DocumentsVerificationStatus;
import com.globallogix.auth.entity.enums.PhotoType;
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

    @Column(name = "photo_type")
    @Enumerated(EnumType.STRING)
    private PhotoType photoType;

    @Column(name = "link_to_photo")
    private String linkToPhoto;

    @Column(name = "documents_verification_status")
    @Enumerated(EnumType.STRING)
    private DocumentsVerificationStatus documentsVerificationStatus;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;


    @PrePersist
    public void onCreate(){
        this.submittedAt = LocalDateTime.now();
    }
}
