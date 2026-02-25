package com.globallogix.delivery.controller;


import com.globallogix.delivery.dto.request.DeliveryRequest;
import com.globallogix.delivery.dto.response.DeliveryResponse;
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
@RequiredArgsConstructor
@RequestMapping("/api/deliveries")
public class DeliveryCrudController {
    private final DeliveryCreationService deliveryCreationService;
    private final DeliveryQueryService deliveryQueryService;
    private final DeliveryAssignmentService deliveryAssignmentService;

    @PostMapping
    @Tag(name = "Создать заказ")
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody @Valid DeliveryRequest deliveryRequest,
                                                           @RequestHeader("X-User-Id") Long senderId, @RequestHeader("X-Forwarded-For") String ip) {
        log.info("Получен IP-адрес: {}", ip);
        log.info("Создание доставки пользователем: {}", senderId);
        DeliveryResponse response = deliveryCreationService.createDelivery(deliveryRequest, senderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Tag(name = "Получить все доставки")
    public ResponseEntity<List<DeliveryResponse>> findAllDeliveries() {
        List<DeliveryResponse> responses = deliveryQueryService.getAllDeliveries();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Tag(name = "Получить доставки по id")
    public ResponseEntity<DeliveryResponse> findById(@PathVariable Long id) {
        DeliveryResponse response = deliveryQueryService.getDelivery(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{deliveryId}")
    @Tag(name = "Удаление доставки")
    @CacheEvict(value = "deliveries", key = "#deliveryId")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDelivery(@PathVariable Long deliveryId,
                               @RequestHeader("X-User-Id") Long userId) {
        log.info("CONTROLLER: Удаление доставки {} пользователем {}", deliveryId, userId);
        deliveryAssignmentService.deleteDelivery(deliveryId, userId);
    }

}
