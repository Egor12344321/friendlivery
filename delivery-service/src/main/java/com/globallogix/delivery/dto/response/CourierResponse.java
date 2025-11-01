package com.globallogix.delivery.dto.response;

import lombok.Data;

import java.time.LocalDate;




public record CourierResponse(
         Long courierId,
         String courierName,
         String flightNumber,
         String fromAirport,
         String toAirport,
         LocalDate flightDate,
         Double availableWeight,
         Double rating
) {
}
