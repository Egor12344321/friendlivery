package com.globallogix.delivery;

import com.globallogix.delivery.dto.request.DeliveryRequest;
import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.DeliveryStatus;
import com.globallogix.delivery.kafka.DeliveryKafkaProducer;
import com.globallogix.delivery.repository.DeliveryRepository;
import com.globallogix.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryKafkaProducer deliveryKafkaProducer;

    @InjectMocks
    private DeliveryService deliveryService;

    @Test
    void createDelivery() {
        DeliveryRequest request = new DeliveryRequest(
                "SVO", "JFK", null, "Documents", 2.5, "30x20x10", "Notes",
                LocalDate.now().plusDays(7)
        );

        Delivery savedDelivery = Delivery.builder()
                .id(1L)
                .fromAirport("SVO")
                .toAirport("JFK")
                .status(DeliveryStatus.SEARCHING)
                .senderId(123L)
                .build();

        when(deliveryRepository.save(any(Delivery.class))).thenReturn(savedDelivery);

        DeliveryResponse result = deliveryService.createDelivery(request, 123L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("SVO", result.fromAirport());
        assertEquals(DeliveryStatus.SEARCHING, result.status());

        verify(deliveryRepository).save(any(Delivery.class));
        verify(deliveryKafkaProducer).sendDeliveryCreated(any(Delivery.class));
    }

    @Test
    void assignToDelivery() {
        Long deliveryId = 1L;
        Long courierId = 555L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setStatus(DeliveryStatus.SEARCHING);
        delivery.setCourierId(null);

        Delivery updatedDelivery = new Delivery();
        updatedDelivery.setId(deliveryId);
        updatedDelivery.setCourierId(courierId);
        updatedDelivery.setStatus(DeliveryStatus.COURIER_FOUND);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        DeliveryResponse result = deliveryService.assignToDelivery(courierId, deliveryId);

        assertNotNull(result);
        assertEquals(courierId, result.courierId());
        assertEquals(DeliveryStatus.COURIER_FOUND, result.status());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
        verify(deliveryKafkaProducer).sendDeliveryAssigned(any(Delivery.class));
    }

    @Test
    void findAvailableDeliveries() {
        Delivery delivery1 = new Delivery();
        delivery1.setStatus(DeliveryStatus.SEARCHING);

        Delivery delivery2 = new Delivery();
        delivery2.setStatus(DeliveryStatus.SEARCHING);

        when(deliveryRepository.findByStatusAndCourierIdIsNull(DeliveryStatus.SEARCHING)).thenReturn(List.of(delivery1, delivery2));

        var result = deliveryService.findAvailableDeliveries();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(deliveryRepository).findByStatusAndCourierIdIsNull(DeliveryStatus.SEARCHING);
    }

    @Test
    void getDeliveryById() {
        Long deliveryId = 1L;
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setFromAirport("SVO");

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        DeliveryResponse result = deliveryService.getDelivery(deliveryId);

        assertNotNull(result);
        assertEquals(deliveryId, result.id());
        assertEquals("SVO", result.fromAirport());
        verify(deliveryRepository).findById(deliveryId);
    }

    @Test
    void confirmDeliveryBySender() {
        Long deliveryId = 1L;
        Long senderId = 123L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setSenderId(senderId);
        delivery.setStatus(DeliveryStatus.COURIER_FOUND);

        Delivery updatedDelivery = new Delivery();
        updatedDelivery.setId(deliveryId);
        updatedDelivery.setStatus(DeliveryStatus.PICKUP_PENDING);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        Delivery result = deliveryService.confirmDeliveryBySender(deliveryId, senderId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.PICKUP_PENDING, result.getStatus());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
        verify(deliveryKafkaProducer).sendHandoverConfirmed(any(Delivery.class));
    }

    @Test
    void getUserDeliveries() {
        Long userId = 123L;

        Delivery delivery1 = new Delivery();
        delivery1.setSenderId(userId);

        Delivery delivery2 = new Delivery();
        delivery2.setSenderId(userId);

        when(deliveryRepository.findBySenderId(userId)).thenReturn(List.of(delivery1, delivery2));

        List<DeliveryResponse> result = deliveryService.getUserDeliveries(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(deliveryRepository).findBySenderId(userId);
    }

    @Test
    void deleteDelivery_DeleteAndSendKafkaEvent() {
        Long deliveryId = 1L;
        Long userId = 123L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setSenderId(userId);
        delivery.setStatus(DeliveryStatus.SEARCHING);
        delivery.setCourierId(null);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        doNothing().when(deliveryRepository).deleteById(deliveryId);

        deliveryService.deleteDelivery(deliveryId, userId);

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).deleteById(deliveryId);
        verify(deliveryKafkaProducer).sendDeliveryCancelled(delivery);
    }

    @Test
    void deleteDelivery_NotAvailableStatus() {
        Long deliveryId = 1L;
        Long userId = 123L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setSenderId(userId);
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        delivery.setCourierId(456L);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryService.deleteDelivery(deliveryId, userId));

        assertEquals("Delivery is not available to delete", exception.getMessage());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).deleteById(any());
        verify(deliveryKafkaProducer, never()).sendDeliveryCancelled(any());
    }

    @Test
    void deleteDelivery_NotAuthorized() {
        Long deliveryId = 1L;
        Long userId = 123L;
        Long differentUserId = 456L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setSenderId(differentUserId);
        delivery.setStatus(DeliveryStatus.SEARCHING);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryService.deleteDelivery(deliveryId, userId));

        assertEquals("Not authorized", exception.getMessage());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).deleteById(any());
        verify(deliveryKafkaProducer, never()).sendDeliveryCancelled(any());
    }
    @Test
    void confirmDeliveryByCourier() {
        Long deliveryId = 1L;
        Long courierId = 555L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setCourierId(courierId);
        delivery.setStatus(DeliveryStatus.PICKUP_PENDING);

        Delivery updatedDelivery = new Delivery();
        updatedDelivery.setId(deliveryId);
        updatedDelivery.setStatus(DeliveryStatus.IN_PROGRESS);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        Delivery result = deliveryService.confirmDeliveryByCourier(deliveryId, courierId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.IN_PROGRESS, result.getStatus());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void confirmDeliveryByCourier_NotAuthorized() {
        Long deliveryId = 1L;
        Long courierId = 555L;
        Long differentCourierId = 666L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setCourierId(differentCourierId);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryService.confirmDeliveryByCourier(deliveryId, courierId));

        assertEquals("Not authorized", exception.getMessage());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void confirmDelivery() {
        Long deliveryId = 1L;
        Long courierId = 555L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setCourierId(courierId);
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);

        Delivery updatedDelivery = new Delivery();
        updatedDelivery.setId(deliveryId);
        updatedDelivery.setStatus(DeliveryStatus.ARRIVED);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        Delivery result = deliveryService.confirmDelivery(deliveryId, courierId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.ARRIVED, result.getStatus());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void confirmArriveDelivery() {
        Long deliveryId = 1L;
        Long senderId = 123L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setSenderId(senderId);
        delivery.setStatus(DeliveryStatus.ARRIVED);

        Delivery updatedDelivery = new Delivery();
        updatedDelivery.setId(deliveryId);
        updatedDelivery.setStatus(DeliveryStatus.DELIVERED);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        Delivery result = deliveryService.confirmArriveDelivery(deliveryId, senderId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.DELIVERED, result.getStatus());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
        verify(deliveryKafkaProducer).sendDeliveryCompleted(delivery);
    }

    @Test
    void confirmArriveDelivery_NotAuthorized_ShouldThrowException() {
        Long deliveryId = 1L;
        Long senderId = 123L;
        Long differentSenderId = 456L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setSenderId(differentSenderId);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deliveryService.confirmArriveDelivery(deliveryId, senderId));

        assertEquals("Not authorized", exception.getMessage());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }


}
