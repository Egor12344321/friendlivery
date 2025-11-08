package com.globallogix.delivery.controller;

import com.globallogix.delivery.dto.request.DeliveryRequest;
import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    private Long parseUserId(String userIdStr) {
        try {
            return Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: {}", userIdStr);
            throw new IllegalArgumentException("Invalid user ID format");
        }
    }

    @PostMapping
    public DeliveryResponse createDelivery(@RequestBody @Valid DeliveryRequest deliveryRequest,
                                           @RequestHeader("X-User-Id") String senderIdStr) {
        Long senderId = parseUserId(senderIdStr); // на самом деле можно не парсить, spring делает сам это
        log.info("Создание доставки пользователем: {}", senderId);
        return deliveryService.createDelivery(deliveryRequest, senderId);
    }

    @GetMapping
    public List<DeliveryResponse> findAllDeliveries() {
        return deliveryService.getAllDeliveries();
    }

    @GetMapping("/{id}")
    public DeliveryResponse getDelivery(@PathVariable Long id) {
        return deliveryService.getDelivery(id);
    }

    @GetMapping("/my")
    public List<DeliveryResponse> getUserDeliveries(@RequestHeader("X-User-Id") String userIdStr) {
        Long userId = parseUserId(userIdStr);
        log.info("Получение доставок пользователя: {}", userId);
        return deliveryService.getUserDeliveries(userId);
    }

    @GetMapping("/available")
    public List<DeliveryResponse> getAvailableDeliveries() {
        return deliveryService.findAvailableDeliveries();
    }

    @PostMapping("/{id}/take")
    public DeliveryResponse assignToDelivery(@PathVariable Long id,
                                             @RequestHeader("X-User-Id") String courierIdStr) {
        Long courierId = parseUserId(courierIdStr);
        log.info("Курьер {} берет доставку {}", courierId, id);
        return deliveryService.assignToDelivery(courierId, id);
    }

    @PostMapping("/{deliveryId}/confirm-handover")
    public ResponseEntity<Delivery> confirmHandOver(@PathVariable Long deliveryId,
                                                    @RequestHeader("X-User-Id") String senderIdStr) {
        Long senderId = parseUserId(senderIdStr);
        log.info("Подтверждение передачи доставки {} отправителем {}", deliveryId, senderId);
        Delivery delivery = deliveryService.confirmDeliveryBySender(deliveryId, senderId);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{deliveryId}/confirm-pickup")
    public ResponseEntity<Delivery> confirmPickUp(@PathVariable Long deliveryId,
                                                  @RequestHeader("X-User-Id") String courierIdStr) {
        Long courierId = parseUserId(courierIdStr);
        log.info("Подтверждение получения доставки {} курьером {}", deliveryId, courierId);
        Delivery delivery = deliveryService.confirmDeliveryByCourier(deliveryId, courierId);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{deliveryId}/confirm-delivery")
    public ResponseEntity<Delivery> confirmDelivery(@PathVariable Long deliveryId,
                                                    @RequestHeader("X-User-Id") String courierIdStr) {
        Long courierId = parseUserId(courierIdStr); // на самом деле можно не парсить, spring делает сам это
        log.info("Подтверждение доставки {} курьером {}", deliveryId, courierId);
        Delivery delivery = deliveryService.confirmDelivery(deliveryId, courierId);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{deliveryId}/confirm-delivery-arrive")
    public ResponseEntity<Delivery> confirmArriveDelivery(@PathVariable Long deliveryId,
                                                          @RequestHeader("X-User-Id") String senderIdStr) {
        Long senderId = parseUserId(senderIdStr);
        log.info("Подтверждение получения доставки {} отправителем {}", deliveryId, senderId);
        Delivery delivery = deliveryService.confirmArriveDelivery(deliveryId, senderId);
        return ResponseEntity.ok(delivery);
    }

    @DeleteMapping("/delete/{deliveryId}")
    @CacheEvict(value = "deliveries", key = "#deliveryId")
    public void deleteDelivery(@PathVariable Long deliveryId,
                               @RequestHeader("X-User-Id") String userIdStr) {
        Long userId = parseUserId(userIdStr);
        log.info("Удаление доставки {} пользователем {}", deliveryId, userId);
        deliveryService.deleteDelivery(deliveryId, userId);
    }
}