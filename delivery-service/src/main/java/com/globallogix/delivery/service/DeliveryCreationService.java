package com.globallogix.delivery.service;


import com.globallogix.delivery.dto.request.DeliveryRequest;
import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.DeliveryStatus;
import com.globallogix.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryCreationService {

    private final DeliveryRepository deliveryRepository;
    private final  EventPublisher eventPublisher;


    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request, Long senderId){
        Delivery delivery = Delivery.builder()
                .fromAirport(request.fromAirport())
                .toAirport(request.toAirport())
                .weight(request.weight())
                .desiredDate(request.desiredDate())
                .description(request.description())
                .trackingNumber(generateTrackingNumber())
                .price(calculatePrice(request.weight()))
                .status(DeliveryStatus.SEARCHING)
                .courierId(null)
                .senderId(senderId)
                .deliveryDeadline(request.deliveryDeadline())
                .build();
        ;
        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery saved to db successfully");
        eventPublisher.sendDeliveryCreated(delivery);


        return DeliveryMapper.mapToDeliveryResponse(savedDelivery);
    }



    public BigDecimal calculatePrice(Double weight){
        BigDecimal basePrice = BigDecimal.valueOf(300);
        BigDecimal pricePerKg = BigDecimal.valueOf(100);

        if (weight == null || weight <= 0) {
            return basePrice;
        }

        return basePrice.add(pricePerKg.multiply(BigDecimal.valueOf(weight)))
                .setScale(0, RoundingMode.UP);
    }

    public String generateTrackingNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return "FLV" + timestamp.substring(7) + random;
    }

}
