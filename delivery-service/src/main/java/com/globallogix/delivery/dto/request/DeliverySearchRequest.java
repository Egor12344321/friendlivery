package com.globallogix.delivery.dto.request;

import lombok.Data;

import java.time.LocalDate;


public record DeliverySearchRequest(
         String fromAirport,
         String toAirport,
         LocalDate desiredDate,
         Double maxWeight
) {
}