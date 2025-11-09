package com.globallogix.chat.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "courier_profiles")
public class CourierProfile {
    @Id
    private Long userId;

    private Double maxWeight;
    private String preferredAirlines;
    private Boolean notificationEnabled = true;
    private String contactPreference = "TELEGRAM";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}

