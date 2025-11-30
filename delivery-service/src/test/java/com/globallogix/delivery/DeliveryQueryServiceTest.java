package com.globallogix.delivery;


import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.DeliveryStatus;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotAvailableException;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotFoundException;
import com.globallogix.delivery.repository.DeliveryRepository;
import com.globallogix.delivery.service.DeliveryQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryQueryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryQueryService deliveryQueryService;

    private Delivery createTestDelivery(Long id, DeliveryStatus status, Long senderId, Long courierId) {
        return Delivery.builder()
                .id(id)
                .trackingNumber("FLV123456")
                .senderId(senderId)
                .courierId(courierId)
                .fromAirport("SVO")
                .toAirport("JFK")
                .desiredDate(LocalDate.now().plusDays(5))
                .deliveryDeadline(LocalDate.now().plusDays(10))
                .description("Test package")
                .weight(2.5)
                .dimensions("30x20x10")
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .price(new BigDecimal("550"))
                .build();
    }

    @Test
    void getDelivery_Success() {
        Long deliveryId = 1L;
        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.SEARCHING, 123L, null);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        DeliveryResponse result = deliveryQueryService.getDelivery(deliveryId);

        assertNotNull(result);
        assertEquals(deliveryId, result.id());
        assertEquals("SVO", result.fromAirport());
        assertEquals("JFK", result.toAirport());
        assertEquals(DeliveryStatus.SEARCHING, result.status());
        verify(deliveryRepository).findById(deliveryId);
    }

    @Test
    void getDelivery_NotFound() {
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () ->
                deliveryQueryService.getDelivery(deliveryId));

        verify(deliveryRepository).findById(deliveryId);
    }

    @Test
    void getUserDeliveries_Success() {
        Long userId = 123L;
        Delivery delivery1 = createTestDelivery(1L, DeliveryStatus.SEARCHING, userId, null);
        Delivery delivery2 = createTestDelivery(2L, DeliveryStatus.COURIER_FOUND, userId, 555L);

        when(deliveryRepository.findBySenderId(userId)).thenReturn(List.of(delivery1, delivery2));

        List<DeliveryResponse> result = deliveryQueryService.getUserDeliveries(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).senderId());
        assertEquals(userId, result.get(1).senderId());
        verify(deliveryRepository).findBySenderId(userId);
    }

    @Test
    void getUserDeliveries_NotFound() {
        Long userId = 123L;
        when(deliveryRepository.findBySenderId(userId)).thenThrow(new RuntimeException("DB error"));

        assertThrows(DeliveryNotFoundException.class, () ->
                deliveryQueryService.getUserDeliveries(userId));

        verify(deliveryRepository).findBySenderId(userId);
    }

    @Test
    void getAllDeliveries_Success() {
        Delivery delivery1 = createTestDelivery(1L, DeliveryStatus.SEARCHING, 123L, null);
        Delivery delivery2 = createTestDelivery(2L, DeliveryStatus.IN_PROGRESS, 124L, 555L);

        when(deliveryRepository.findAll()).thenReturn(List.of(delivery1, delivery2));

        List<DeliveryResponse> result = deliveryQueryService.getAllDeliveries();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(deliveryRepository).findAll();
    }

    @Test
    void getAllDeliveries_NotFound() {
        when(deliveryRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        assertThrows(DeliveryNotFoundException.class, () ->
                deliveryQueryService.getAllDeliveries());

        verify(deliveryRepository).findAll();
    }

    @Test
    void findAvailableDeliveries_Success() {
        Delivery delivery1 = createTestDelivery(1L, DeliveryStatus.SEARCHING, 123L, null);
        Delivery delivery2 = createTestDelivery(2L, DeliveryStatus.SEARCHING, 124L, null);

        when(deliveryRepository.findByStatusAndCourierIdIsNull(DeliveryStatus.SEARCHING))
                .thenReturn(List.of(delivery1, delivery2));

        List<DeliveryResponse> result = deliveryQueryService.findAvailableDeliveries();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(DeliveryStatus.SEARCHING, result.get(0).status());
        assertEquals(DeliveryStatus.SEARCHING, result.get(1).status());
        verify(deliveryRepository).findByStatusAndCourierIdIsNull(DeliveryStatus.SEARCHING);
    }

    @Test
    void findAvailableDeliveries_NotAvailable() {
        when(deliveryRepository.findByStatusAndCourierIdIsNull(DeliveryStatus.SEARCHING))
                .thenThrow(new RuntimeException("No deliveries"));

        assertThrows(DeliveryNotAvailableException.class, () ->
                deliveryQueryService.findAvailableDeliveries());

        verify(deliveryRepository).findByStatusAndCourierIdIsNull(DeliveryStatus.SEARCHING);
    }


}
