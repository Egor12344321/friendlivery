package com.globallogix.delivery.dto.response;


import com.globallogix.delivery.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Builder
public record DeliveryPublicResponse (
         Long id,
         String fromAirport,
         String toAirport,
         LocalDate desiredDate,
         String description,
         Double weight,
         BigDecimal price,
         DeliveryStatus status
){
}
