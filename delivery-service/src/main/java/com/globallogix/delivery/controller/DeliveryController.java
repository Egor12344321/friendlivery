package com.globallogix.delivery.controller;

import com.globallogix.delivery.dto.request.DeliveryRequest;
import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.service.DeliveryAssignmentService;
import com.globallogix.delivery.service.DeliveryCreationService;
import com.globallogix.delivery.service.DeliveryQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery Management", description = "APIs for managing deliveries")
public class DeliveryController {
    private final DeliveryCreationService deliveryCreationService;
    private final DeliveryQueryService deliveryQueryService;
    private final DeliveryAssignmentService deliveryAssignmentService;

    private Long parseUserId(String userIdStr) {
        try {
            return Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: {}", userIdStr);
            throw new IllegalArgumentException("Invalid user ID format");
        }
    }

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody @Valid DeliveryRequest deliveryRequest,
                                           @RequestHeader("X-User-Id") String senderIdStr) {
        Long senderId = parseUserId(senderIdStr); // на самом деле можно не парсить, spring делает сам это
        log.info("Создание доставки пользователем: {}", senderId);
        DeliveryResponse response = deliveryCreationService.createDelivery(deliveryRequest, senderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> findAllDeliveries() {
        List<DeliveryResponse> responses = deliveryQueryService.getAllDeliveries();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDelivery(@PathVariable Long id) {
        DeliveryResponse response = deliveryQueryService.getDelivery(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<DeliveryResponse>> getUserDeliveries(@RequestHeader("X-User-Id") String userIdStr) {
        Long userId = parseUserId(userIdStr);
        log.info("Получение доставок пользователя: {}", userId);
        List<DeliveryResponse> responses = deliveryQueryService.getUserDeliveries(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DeliveryResponse>> getAvailableDeliveries() {
        List<DeliveryResponse> responses = deliveryQueryService.findAvailableDeliveries();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/take")
    public ResponseEntity<DeliveryResponse> assignToDelivery(@PathVariable Long id,
                                             @RequestHeader("X-User-Id") String courierIdStr) {
        Long courierId = parseUserId(courierIdStr);
        log.info("Курьер {} берет доставку {}", courierId, id);
        DeliveryResponse response = deliveryAssignmentService.assignToDelivery(courierId, id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{deliveryId}/confirm-handover")
    public ResponseEntity<DeliveryResponse> confirmHandOver(@PathVariable Long deliveryId,
                                                    @RequestHeader("X-User-Id") String senderIdStr) {
        Long senderId = parseUserId(senderIdStr);
        log.info("Подтверждение передачи доставки {} отправителем {}", deliveryId, senderId);
        DeliveryResponse response = deliveryAssignmentService.confirmDeliveryBySender(deliveryId, senderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{deliveryId}/confirm-pickup")
    public ResponseEntity<DeliveryResponse> confirmPickUp(@PathVariable Long deliveryId,
                                                  @RequestHeader("X-User-Id") String courierIdStr) {
        Long courierId = parseUserId(courierIdStr);
        log.info("Подтверждение получения доставки {} курьером {}", deliveryId, courierId);
        DeliveryResponse response = deliveryAssignmentService.confirmDeliveryByCourier(deliveryId, courierId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{deliveryId}/confirm-delivery")
    public ResponseEntity<DeliveryResponse> confirmDelivery(@PathVariable Long deliveryId,
                                                    @RequestHeader("X-User-Id") String courierIdStr) {
        Long courierId = parseUserId(courierIdStr); // на самом деле можно не парсить, spring делает сам это
        log.info("Подтверждение доставки {} курьером {}", deliveryId, courierId);
        DeliveryResponse response = deliveryAssignmentService.confirmDelivery(deliveryId, courierId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{deliveryId}/confirm-delivery-arrive")
    public ResponseEntity<DeliveryResponse> confirmArriveDelivery(@PathVariable Long deliveryId,
                                                          @RequestHeader("X-User-Id") String senderIdStr) {
        Long senderId = parseUserId(senderIdStr);
        log.info("Подтверждение получения доставки {} отправителем {}", deliveryId, senderId);
        DeliveryResponse response = deliveryAssignmentService.confirmArriveDelivery(deliveryId, senderId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{deliveryId}")
    @CacheEvict(value = "deliveries", key = "#deliveryId")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDelivery(@PathVariable Long deliveryId,
                               @RequestHeader("X-User-Id") String userIdStr) {
        Long userId = parseUserId(userIdStr);
        log.info("CONTROLLER: Удаление доставки {} пользователем {}", deliveryId, userId);
        deliveryAssignmentService.deleteDelivery(deliveryId, userId);
    }
}