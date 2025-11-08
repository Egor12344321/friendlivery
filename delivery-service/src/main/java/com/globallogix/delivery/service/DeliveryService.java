package com.globallogix.delivery.service;

import com.globallogix.delivery.dto.request.DeliveryRequest;
import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.DeliveryStatus;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotAvailableException;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotFoundException;
import com.globallogix.delivery.kafka.DeliveryKafkaProducer;
import com.globallogix.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryKafkaProducer deliveryKafkaProducer;

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
        log.info("Getting delivery with id: {}", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        return mapToDeliveryResponse(delivery);
    }



    public List<DeliveryResponse> getUserDeliveries(Long userId){
        log.debug("Getting user's: {} deliveries started", userId);
        try {
            List<Delivery> deliveries = deliveryRepository.findBySenderId(userId);
            return deliveries.stream().map(this::mapToDeliveryResponse).toList();
        } catch (Exception e){
            throw new DeliveryNotFoundException("User's deliveries not found");
        }
    }

    public List<DeliveryResponse> getAllDeliveries(){
        log.debug("Getting all deliveries");
        try {
            List<Delivery> deliveries = deliveryRepository.findAll();
            return deliveries.stream().map(this::mapToDeliveryResponse).toList();
        } catch (Exception e){
            throw new DeliveryNotFoundException("Deliveries not found");
        }
    }

    public List<DeliveryResponse> findAvailableDeliveries(){
        log.debug("Getting all available deliveries");
        try {
            List<Delivery> deliveries = deliveryRepository.findByStatusAndCourierIdIsNull(DeliveryStatus.SEARCHING);
            return deliveries.stream().map(this::mapToDeliveryResponse).toList();
        } catch (Exception e){
            throw new DeliveryNotAvailableException("No available deliveries");
        }
    }

    public DeliveryResponse assignToDelivery(Long courierId, Long deliveryId){
        log.info("Assign to delivery started");
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        if (delivery.getStatus() != DeliveryStatus.SEARCHING || delivery.getCourierId() != null) {
            throw new RuntimeException("Delivery is not available");
        }

        delivery.setCourierId(courierId);
        delivery.setStatus(DeliveryStatus.COURIER_FOUND);
        delivery.setUpdatedAt(LocalDateTime.now());

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery saved to db");
        return mapToDeliveryResponse(updatedDelivery);
    }

    private String generateTrackingNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return "FLV" + timestamp.substring(7) + random;
    }


    public void deleteDelivery(Long deliveryId, Long userId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (!delivery.getSenderId().equals(userId)){
            throw new RuntimeException("Not authorized");
        }
        deliveryRepository.deleteById(deliveryId);
        deliveryKafkaProducer.sendDeliveryCancelled(delivery);
    }

    public Delivery confirmDeliveryBySender(Long deliveryId, Long senderId) {
        log.debug("SERVICE: confirmDeliveryBySender started");
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (!delivery.getSenderId().equals(senderId)){
            throw new RuntimeException("Not authorized");
        }
        delivery.setStatus(DeliveryStatus.PICKUP_PENDING);
        delivery.setSenderAcceptedAt(LocalDateTime.now());

        deliveryKafkaProducer.sendHandoverConfirmed(delivery);
        return deliveryRepository.save(delivery);
    }

    public Delivery confirmDeliveryByCourier(Long deliveryId, Long courierId) {
        log.debug("SERVICE: confirmDeliveryByCourier started");
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (!delivery.getCourierId().equals(courierId)){
            throw new RuntimeException("Not authorized");
        }
        delivery.setCourierAcceptedAt(LocalDateTime.now());
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        return deliveryRepository.save(delivery);
    }

    public Delivery confirmDelivery(Long deliveryId, Long courierId) {
        log.debug("SERVICE: confirmDeliveryArrive by courier started");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (!delivery.getCourierId().equals(courierId)){
            throw new RuntimeException("Not authorized");
        }
        delivery.setStatus(DeliveryStatus.ARRIVED);
        return deliveryRepository.save(delivery);
    };

    public Delivery confirmArriveDelivery(Long deliveryId, Long senderId) {
        log.debug("SERVICE: confirmDeliveryArrive by sender started");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (!delivery.getSenderId().equals(senderId)){
            throw new RuntimeException("Not authorized");
        }
        delivery.setStatus(DeliveryStatus.DELIVERED);
        deliveryKafkaProducer.sendDeliveryCompleted(delivery);
        return deliveryRepository.save(delivery);
    }
}
