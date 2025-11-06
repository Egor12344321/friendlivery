package com.globallogix.flight;


import lombok.Data;

import java.time.LocalDate;

@Data
public class CourierRouteRequest {
    private String departureAirport;
    private String arrivalAirport;
    private Integer priority;
    private LocalDate flightDate;
}
