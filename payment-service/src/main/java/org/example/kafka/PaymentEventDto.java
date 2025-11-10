package org.example.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentEventDto {
    private Long userId;
    private String confirmationUrl;
    private Long deliveryId;
}
