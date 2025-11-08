package com.globallogix.flight.dto;


import lombok.Data;

import java.time.LocalDate;


public record CourierRouteRequest (
         String departureAirport,
         String arrivalAirport,
         Integer priority,
         LocalDate flightDate
){
}
