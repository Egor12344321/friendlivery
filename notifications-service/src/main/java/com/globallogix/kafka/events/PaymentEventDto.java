package com.globallogix.kafka.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEventDto {
    private Long userId;
    private String confirmationUrl;
    private Long deliveryId;

}
