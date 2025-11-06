package com.globallogix.delivery.service;

import com.globallogix.delivery.dto.request.DeliveryRequest;
import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.DeliveryStatus;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotAvailableException;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotFoundException;
import com.globallogix.delivery.kafka.events.DeliveryCreatedEvent;
import com.globallogix.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate;


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

        kafkaTemplate.send("delivery.created",
               DeliveryCreatedEvent.fromEntity(delivery));
        return mapToDeliveryResponse(savedDelivery);
    }

    private DeliveryResponse mapToDeliveryResponse(Delivery delivery) {
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

    private BigDecimal calculatePrice(Double weight){
        BigDecimal basePrice = BigDecimal.valueOf(300);
        BigDecimal pricePerKg = BigDecimal.valueOf(100);

        if (weight == null || weight <= 0) {
            return basePrice;
        }

        return basePrice.add(pricePerKg.multiply(BigDecimal.valueOf(weight)))
                .setScale(0, RoundingMode.UP);
    }

    @Cacheable(value = "deliveries", key = "#deliveryId")
    public DeliveryResponse getDelivery(Long deliveryId){
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        return mapToDeliveryResponse(delivery);
    }

    public List<DeliveryResponse> getUserDeliveries(Long userId){
        try {
            List<Delivery> deliveries = deliveryRepository.findBySenderId(userId);
            return deliveries.stream().map(this::mapToDeliveryResponse).toList();
        } catch (Exception e){
            throw new DeliveryNotFoundException("User's deliveries not found");
        }
    }

    public List<DeliveryResponse> getAllDeliveries(){
        try {
            List<Delivery> deliveries = deliveryRepository.findAll();
            return deliveries.stream().map(this::mapToDeliveryResponse).toList();
        } catch (Exception e){
            throw new DeliveryNotFoundException("Deliveries not found");
        }
    }

    public List<DeliveryResponse> findAvailableDeliveries(){
        try {
            List<Delivery> deliveries = deliveryRepository.findByStatusAndCourierIdIsNull(DeliveryStatus.SEARCHING);
            return deliveries.stream().map(this::mapToDeliveryResponse).toList();
        } catch (Exception e){
            throw new DeliveryNotAvailableException("No available deliveries");
        }
    }

    public DeliveryResponse assignToDelivery(Long courierId, Long deliveryId){

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        if (delivery.getStatus() != DeliveryStatus.SEARCHING || delivery.getCourierId() != null) {
            throw new RuntimeException("Delivery is not available");
        }

        delivery.setCourierId(courierId);
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        delivery.setUpdatedAt(LocalDateTime.now());

        Delivery updatedDelivery = deliveryRepository.save(delivery);

        return mapToDeliveryResponse(updatedDelivery);
    }

    private String generateTrackingNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return "FLV" + timestamp.substring(7) + random;
    }


    public void deleteDelivery(Long deliveryId) {
        deliveryRepository.deleteById(deliveryId);
    }
}
