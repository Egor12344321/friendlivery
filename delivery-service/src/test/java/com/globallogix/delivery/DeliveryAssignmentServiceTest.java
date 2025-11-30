package com.globallogix.delivery;

import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.DeliveryStatus;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotAvailableException;
import com.globallogix.delivery.exceptions.custom_exceptions.DeliveryNotFoundException;
import com.globallogix.delivery.repository.DeliveryRepository;
import com.globallogix.delivery.service.DeliveryAssignmentService;
import com.globallogix.delivery.service.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryAssignmentServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private DeliveryAssignmentService deliveryAssignmentService;

    private Delivery createTestDelivery(Long id, DeliveryStatus status, Long senderId, Long courierId) {
        return Delivery.builder()
                .id(id)
                .senderId(senderId)
                .courierId(courierId)
                .status(status)
                .fromAirport("SVO")
                .toAirport("JFK")
                .build();
    }

    @Test
    void assignToDelivery_Success() {
        Long deliveryId = 1L;
        Long courierId = 555L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.SEARCHING, 123L, null);
        Delivery updatedDelivery = createTestDelivery(deliveryId, DeliveryStatus.COURIER_FOUND, 123L, courierId);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        DeliveryResponse result = deliveryAssignmentService.assignToDelivery(courierId, deliveryId);

        assertNotNull(result);
        assertEquals(courierId, result.courierId());
        assertEquals(DeliveryStatus.COURIER_FOUND, result.status());

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
        verify(eventPublisher).sendDeliveryAssigned(delivery);
    }

    @Test
    void assignToDelivery_NotFound() {
        Long deliveryId = 1L;
        Long courierId = 555L;

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                deliveryAssignmentService.assignToDelivery(courierId, deliveryId));

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void assignToDelivery_NotAvailable() {
        Long deliveryId = 1L;
        Long courierId = 555L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.IN_PROGRESS, 123L, 666L);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        assertThrows(DeliveryNotAvailableException.class, () ->
                deliveryAssignmentService.assignToDelivery(courierId, deliveryId));

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void deleteDelivery_Success() {
        Long deliveryId = 1L;
        Long userId = 123L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.SEARCHING, userId, null);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        doNothing().when(deliveryRepository).deleteById(deliveryId);

        deliveryAssignmentService.deleteDelivery(deliveryId, userId);

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).deleteById(deliveryId);
        verify(eventPublisher).sendDeliveryCancelled(delivery);
    }

    @Test
    void deleteDelivery_NotAuthorized() {
        Long deliveryId = 1L;
        Long userId = 123L;
        Long differentUserId = 456L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.SEARCHING, differentUserId, null);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryAssignmentService.deleteDelivery(deliveryId, userId));

        assertEquals("Not authorized", exception.getMessage());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).deleteById(any());
    }

    @Test
    void deleteDelivery_NotAvailableStatus() {
        Long deliveryId = 1L;
        Long userId = 123L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.IN_PROGRESS, userId, 555L);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        assertThrows(DeliveryNotAvailableException.class,
                () -> deliveryAssignmentService.deleteDelivery(deliveryId, userId));

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).deleteById(any());
    }

    @Test
    void confirmDeliveryBySender_Success() {
        Long deliveryId = 1L;
        Long senderId = 123L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.COURIER_FOUND, senderId, 555L);
        Delivery updatedDelivery = createTestDelivery(deliveryId, DeliveryStatus.PICKUP_PENDING, senderId, 555L);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        DeliveryResponse result = deliveryAssignmentService.confirmDeliveryBySender(deliveryId, senderId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.PICKUP_PENDING, result.status());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
        verify(eventPublisher).sendHandoverConfirmed(delivery);
    }

    @Test
    void confirmDeliveryBySender_NotAuthorized() {
        Long deliveryId = 1L;
        Long senderId = 123L;
        Long differentSenderId = 456L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.COURIER_FOUND, differentSenderId, 555L);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryAssignmentService.confirmDeliveryBySender(deliveryId, senderId));

        assertEquals("Not authorized", exception.getMessage());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void confirmDeliveryByCourier_Success() {
        Long deliveryId = 1L;
        Long courierId = 555L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.PICKUP_PENDING, 123L, courierId);
        Delivery updatedDelivery = createTestDelivery(deliveryId, DeliveryStatus.IN_PROGRESS, 123L, courierId);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        DeliveryResponse result = deliveryAssignmentService.confirmDeliveryByCourier(deliveryId, courierId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.IN_PROGRESS, result.status());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void confirmDeliveryByCourier_NotAuthorized() {
        Long deliveryId = 1L;
        Long courierId = 555L;
        Long differentCourierId = 666L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.PICKUP_PENDING, 123L, differentCourierId);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryAssignmentService.confirmDeliveryByCourier(deliveryId, courierId));

        assertEquals("Not authorized", exception.getMessage());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void confirmDelivery_Success() {
        Long deliveryId = 1L;
        Long courierId = 555L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.IN_PROGRESS, 123L, courierId);
        Delivery updatedDelivery = createTestDelivery(deliveryId, DeliveryStatus.ARRIVED, 123L, courierId);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        DeliveryResponse result = deliveryAssignmentService.confirmDelivery(deliveryId, courierId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.ARRIVED, result.status());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void confirmDelivery_NotAuthorized() {
        Long deliveryId = 1L;
        Long courierId = 555L;
        Long differentCourierId = 666L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.IN_PROGRESS, 123L, differentCourierId);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryAssignmentService.confirmDelivery(deliveryId, courierId));

        assertEquals("Not authorized", exception.getMessage());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void confirmArriveDelivery_Success() {
        Long deliveryId = 1L;
        Long senderId = 123L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.ARRIVED, senderId, 555L);
        Delivery updatedDelivery = createTestDelivery(deliveryId, DeliveryStatus.DELIVERED, senderId, 555L);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        DeliveryResponse result = deliveryAssignmentService.confirmArriveDelivery(deliveryId, senderId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.DELIVERED, result.status());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
        verify(eventPublisher).sendDeliveryCompleted(delivery);
    }

    @Test
    void confirmArriveDelivery_NotAuthorized() {
        Long deliveryId = 1L;
        Long senderId = 123L;
        Long differentSenderId = 456L;

        Delivery delivery = createTestDelivery(deliveryId, DeliveryStatus.ARRIVED, differentSenderId, 555L);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryAssignmentService.confirmArriveDelivery(deliveryId, senderId));

        assertEquals("Not authorized", exception.getMessage());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }
}