package com.globallogix.flight.dto;


import jakarta.validation.constraints.Future;

import java.time.LocalDate;


public record CourierRouteRequest (
         String departureAirport,
         String arrivalAirport,
         Integer priority,
         @Future
         LocalDate flightDate
){
}
