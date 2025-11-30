package com.globallogix.delivery.service;


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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryQueryService {
    private final DeliveryRepository deliveryRepository;


    @Transactional(readOnly = true)
    public DeliveryResponse getDelivery(Long deliveryId){
        log.info("Getting delivery with id: {}", deliveryId);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));
        return DeliveryMapper.mapToDeliveryResponse(delivery);
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> getUserDeliveries(Long userId){
        log.debug("Getting user's: {} deliveries started", userId);
        try {
            List<Delivery> deliveries = deliveryRepository.findBySenderId(userId);
            return deliveries.stream().map(DeliveryMapper::mapToDeliveryResponse).toList();
        } catch (Exception e){
            throw new DeliveryNotFoundException("User's deliveries not found");
        }
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getAllDeliveries(){
        log.debug("Getting all deliveries");
        try {
            List<Delivery> deliveries = deliveryRepository.findAll();
            return deliveries.stream().map(DeliveryMapper::mapToDeliveryResponse).toList();
        } catch (Exception e){
            throw new DeliveryNotFoundException("Deliveries not found");
        }
    }


    @Transactional(readOnly = true)
    public List<DeliveryResponse> findAvailableDeliveries(){
        log.debug("Getting all available deliveries");
        try {
            List<Delivery> deliveries = deliveryRepository.findByStatusAndCourierIdIsNull(DeliveryStatus.SEARCHING);
            return deliveries.stream().map(DeliveryMapper::mapToDeliveryResponse).toList();
        } catch (Exception e){
            throw new DeliveryNotAvailableException("No available deliveries");
        }
    }
}
