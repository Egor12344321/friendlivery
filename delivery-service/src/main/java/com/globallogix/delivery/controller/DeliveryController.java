package com.globallogix.delivery.controller;

import com.globallogix.delivery.dto.request.DeliveryRequest;
import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public DeliveryResponse createDelivery(@RequestBody @Valid DeliveryRequest deliveryRequest, @RequestHeader("X-User-Id") Long senderId){
        return deliveryService.createDelivery(deliveryRequest, senderId);
    }

    @GetMapping
    public List<DeliveryResponse> finAllDeliveries(){
        return deliveryService.getAllDeliveries();
    }

    @GetMapping("/{id}")
    public DeliveryResponse getDelivery(@PathVariable Long id){
        return deliveryService.getDelivery(id);
    }

    @GetMapping("/my")
    public List<DeliveryResponse> getUserDeliveries(@RequestHeader("X-User-Id") Long id){
        return deliveryService.getUserDeliveries(id);
    }

    @GetMapping("/available")
    public List<DeliveryResponse> getAvailableDeliveries(){
        return deliveryService.findAvailableDeliveries();
    }

    @PostMapping("/{id}/take")
    public DeliveryResponse assignToDelivery(@PathVariable Long id, @RequestHeader("X-User-Id") Long courierId){
        return deliveryService.assignToDelivery(courierId, id);
    }

    @PostMapping("/{deliveryId}/confirm-handover")
    public ResponseEntity<Delivery> confirmHandOver(
            @PathVariable Long deliveryId,
            @RequestParam Long senderId
    ){
        log.info("Confirmation handover started");
        Delivery delivery = deliveryService.confirmDeliveryBySender(deliveryId, senderId);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{deliveryId}/confirm-pickup")
    public ResponseEntity<Delivery> confirmPickUp(
            @PathVariable Long deliveryId,
            @RequestParam Long courierId
    ){
        log.info("Confirmation pickup started");
        Delivery delivery = deliveryService.confirmDeliveryBySender(deliveryId, courierId);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{deliveryId}/confirm-delivery")
    public ResponseEntity<Delivery> confirmDelivery(
            @PathVariable Long deliveryId,
            @RequestParam Long courierId
    ){
        Delivery delivery = deliveryService.confirmDelivery(deliveryId, courierId);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{deliveryId}/confirm-delivery-arrive")
    public ResponseEntity<Delivery> confirmArriveDelivery(
            @PathVariable Long deliveryId,
            @RequestParam Long senderId
    ) {
        Delivery delivery = deliveryService.confirmArriveDelivery(deliveryId, senderId);
        return ResponseEntity.ok(delivery);
    }

    @DeleteMapping("/delete/{deliveryId}")
    @CacheEvict(value = "deliveries", key = "#deliveryId")
    public void deleteDelivery(@PathVariable Long deliveryId){deliveryService.deleteDelivery(deliveryId);}
}
