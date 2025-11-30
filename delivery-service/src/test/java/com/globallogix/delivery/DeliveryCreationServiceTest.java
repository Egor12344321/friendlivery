package com.globallogix.delivery;

import com.globallogix.delivery.dto.request.DeliveryRequest;
import com.globallogix.delivery.dto.response.DeliveryResponse;
import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.DeliveryStatus;
import com.globallogix.delivery.repository.DeliveryRepository;
import com.globallogix.delivery.service.DeliveryCreationService;
import com.globallogix.delivery.service.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryCreationServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private DeliveryCreationService deliveryCreationService;

    @Test
    void createDelivery_Success() {
        DeliveryRequest request = new DeliveryRequest(
                "SVO", "JFK", LocalDate.now().plusDays(5), "Documents",
                2.5, "30x20x10", "Notes", LocalDate.now().plusDays(10)
        );
        Long senderId = 123L;

        Delivery savedDelivery = Delivery.builder()
                .id(1L)
                .fromAirport("SVO")
                .toAirport("JFK")
                .status(DeliveryStatus.SEARCHING)
                .senderId(senderId)
                .price(new BigDecimal("550"))
                .build();

        when(deliveryRepository.save(any(Delivery.class))).thenReturn(savedDelivery);

        DeliveryResponse result = deliveryCreationService.createDelivery(request, senderId);

        assertNotNull(result);
        assertEquals("SVO", result.fromAirport());
        assertEquals("JFK", result.toAirport());
        assertEquals(DeliveryStatus.SEARCHING, result.status());
        assertEquals(new BigDecimal("550"), result.price());

        verify(deliveryRepository).save(any(Delivery.class));
        verify(eventPublisher).sendDeliveryCreated(any(Delivery.class));
    }

    @Test
    void calculatePrice_WithWeight() {
        DeliveryCreationService service = new DeliveryCreationService(deliveryRepository, eventPublisher);

        BigDecimal price = service.calculatePrice(2.5);

        assertEquals(new BigDecimal("550"), price);
    }

    @Test
    void calculatePrice_ZeroWeight() {
        DeliveryCreationService service = new DeliveryCreationService(deliveryRepository, eventPublisher);

        BigDecimal price = service.calculatePrice(0.0);

        assertEquals(new BigDecimal("300"), price);
    }

    @Test
    void calculatePrice_NullWeight() {
        DeliveryCreationService service = new DeliveryCreationService(deliveryRepository, eventPublisher);

        BigDecimal price = service.calculatePrice(null);

        assertEquals(new BigDecimal("300"), price);
    }
}