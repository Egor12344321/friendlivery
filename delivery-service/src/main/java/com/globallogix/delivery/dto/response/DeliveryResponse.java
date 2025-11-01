package com.globallogix.delivery.dto.response;

import com.globallogix.delivery.entity.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Builder
public record DeliveryResponse(
         Long id,
         Long senderId,
         Long courierId,
         String fromAirport,
         String toAirport,
         LocalDate desiredDate,
         String description,
         Double weight,
         String dimensions,
         DeliveryStatus status,
         LocalDateTime createdAt,
         LocalDateTime updatedAt,
         BigDecimal price
) {

}