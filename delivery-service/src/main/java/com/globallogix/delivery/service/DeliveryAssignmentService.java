package com.globallogix.delivery.service;


import com.globallogix.delivery.annotations.GeoAudit;
import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.DeliveryStatus;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotAvailableException;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotFoundException;
import com.globallogix.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryAssignmentService {
    private final DeliveryRepository deliveryRepository;
    private final EventPublisher eventPublisher;


    @Transactional
    public DeliveryResponse assignToDelivery(Long courierId, Long deliveryId){
        log.info("Assign to delivery started");
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        if (delivery.getStatus() != DeliveryStatus.SEARCHING || delivery.getCourierId() != null) {
            throw new DeliveryNotAvailableException("Delivery is not available to assign");
        }

        delivery.setCourierId(courierId);
        delivery.setStatus(DeliveryStatus.COURIER_FOUND);
        delivery.setUpdatedAt(LocalDateTime.now());

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        eventPublisher.sendDeliveryAssigned(delivery);
        log.info("Delivery saved to db");
        return DeliveryMapper.mapToDeliveryResponse(updatedDelivery);
    }




    @Transactional
    public void deleteDelivery(Long deliveryId, Long userId) {
        log.debug("SERVICE: Starting removal delivery: {}", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (delivery.getStatus() != DeliveryStatus.SEARCHING && delivery.getStatus() != DeliveryStatus.CREATED || delivery.getCourierId() != null) {
            throw new DeliveryNotAvailableException("Delivery is not available to delete");
        }
        if (!delivery.getSenderId().equals(userId)){
            throw new RuntimeException("Not authorized");
        }
        deliveryRepository.deleteById(deliveryId);
        log.info("Delivery deleted from db");
        eventPublisher.sendDeliveryCancelled(delivery);
    }


    @Transactional
    @GeoAudit
    public DeliveryResponse confirmDeliveryBySender(Long deliveryId, Long senderId) {
        log.debug("SERVICE: confirmDeliveryBySender started");
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (!delivery.getSenderId().equals(senderId)){
            throw new RuntimeException("Not authorized");
        }
        delivery.setStatus(DeliveryStatus.PICKUP_PENDING);
        delivery.setSenderAcceptedAt(LocalDateTime.now());

        eventPublisher.sendHandoverConfirmed(delivery);
        Delivery saved_delivery = deliveryRepository.save(delivery);
        return DeliveryMapper.mapToDeliveryResponse(saved_delivery);
    }


    @Transactional
    @GeoAudit
    public DeliveryResponse confirmDeliveryByCourier(Long deliveryId, Long courierId) {
        log.debug("SERVICE: confirmDeliveryByCourier started");
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (!delivery.getCourierId().equals(courierId)){
            throw new RuntimeException("Not authorized");
        }
        delivery.setCourierAcceptedAt(LocalDateTime.now());
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        Delivery saved_delivery = deliveryRepository.save(delivery);
        return DeliveryMapper.mapToDeliveryResponse(saved_delivery);
    }


    @Transactional
    @GeoAudit
    public DeliveryResponse confirmDelivery(Long deliveryId, Long courierId) {
        log.debug("SERVICE: confirmDeliveryArrive by courier started");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (!delivery.getCourierId().equals(courierId)){
            throw new RuntimeException("Not authorized");
        }
        delivery.setStatus(DeliveryStatus.ARRIVED);
        Delivery saved_delivery = deliveryRepository.save(delivery);
        return DeliveryMapper.mapToDeliveryResponse(delivery);
    };

    @Transactional
    @GeoAudit
    public DeliveryResponse confirmArriveDelivery(Long deliveryId, Long senderId) {
        log.debug("SERVICE: confirmDeliveryArrive by sender started");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        if (!delivery.getSenderId().equals(senderId)){
            throw new RuntimeException("Not authorized");
        }
        delivery.setStatus(DeliveryStatus.DELIVERED);
        eventPublisher.sendDeliveryCompleted(delivery);
        Delivery saved_delivery = deliveryRepository.save(delivery);
        return DeliveryMapper.mapToDeliveryResponse(delivery);
    }}
