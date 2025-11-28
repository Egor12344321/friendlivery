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

    @Column(name = "trackingNumber")
    String trackingNumber;

    @Column(name = "senderId")
    Long senderId;

    @Column(name = "courierId")
    Long courierId;

    @Column(name = "fromAirport", nullable = false)
    private String fromAirport;
    @Column(name = "toAirport", nullable = false)
    private String toAirport;
    @Column(name = "desiredDate", nullable = false)
    private LocalDate desiredDate;
    @Column(name = "deliveryDeadline", nullable = false)
    private LocalDate deliveryDeadline;

    @Column(name = "description")
    private String description;
    @Column(name = "weight", nullable = false)
    private Double weight;
    @Column(name = "courierAcceptedAt")
    private LocalDateTime courierAcceptedAt;
    @Column(name = "senderAcceptedAt")
    private LocalDateTime senderAcceptedAt;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "dimensions")
    private String dimensions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
