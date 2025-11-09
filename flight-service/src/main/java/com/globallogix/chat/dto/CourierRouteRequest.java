package com.globallogix.chat.dto;


import java.time.LocalDate;


public record CourierRouteRequest (
         String departureAirport,
         String arrivalAirport,
         Integer priority,
         LocalDate flightDate
){
}
