package com.globallogix.flight;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeliveryDto {
    private Long id;
    private String fromAirport;
    private String toAirport;
    private LocalDate deliveryDeadline;
    private LocalDate desiredDate;
    private Double weight;
    private BigDecimal price;
    private String status;

}