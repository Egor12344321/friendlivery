package com.globallogix.delivery.service;


import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import org.springframework.stereotype.Component;

public class DeliveryMapper {
    public static DeliveryResponse mapToDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .senderId(delivery.getSenderId())
                .courierId(delivery.getCourierId())
                .fromAirport(delivery.getFromAirport())
                .toAirport(delivery.getToAirport())
                .desiredDate(delivery.getDesiredDate())
                .weight(delivery.getWeight())
                .dimensions(delivery.getDimensions())
                .status(delivery.getStatus())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .price(delivery.getPrice())
                .id(delivery.getId())
                .build();
    }
}
