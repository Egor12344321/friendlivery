package com.globallogix.delivery.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    String trackingNumber;

    @Column
    Long senderId;

    @Column
    Long courierId;


    @Column(nullable = false)
    private String fromAirport;

    private String toAirport;
    private LocalDate desiredDate;
    private LocalDate deliveryDeadline;

    private String description;
    private Double weight;
    private LocalDateTime courierAcceptedAt;
    private LocalDateTime senderAcceptedAt;
    private DeliveryStatus status = DeliveryStatus.CREATED;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BigDecimal price;
    private String dimensions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
