package com.globallogix.flight;


import com.globallogix.flight.service.MatchingService;
import org.junit.jupiter.api.Test;

import com.globallogix.flight.client.DeliveryClient;
import com.globallogix.flight.dto.DeliveryDto;
import com.globallogix.flight.entity.CourierProfile;
import com.globallogix.flight.entity.CourierRoute;
import com.globallogix.flight.kafka.events.DeliveryEventDto;
import com.globallogix.flight.repository.CourierProfileRepository;
import com.globallogix.flight.repository.CourierRouteRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private DeliveryClient deliveryClient;

    @Mock
    private CourierProfileRepository courierProfileRepository;

    @Mock
    private CourierRouteRepository courierRouteRepository;

    @Mock
    private KafkaTemplate<String, DeliveryEventDto> kafkaTemplate;

    @InjectMocks
    private MatchingService matchingService;

    @Test
    void findMatchingDeliveries() {
        Long courierId = 1L;

        CourierProfile profile = new CourierProfile();
        profile.setMaxWeight(10.0);

        CourierRoute route = new CourierRoute();
        route.setDepartureAirport("SVO");
        route.setArrivalAirport("JFK");
        route.setFlightDate(LocalDate.now().plusDays(5));

        DeliveryDto matchingDelivery = new DeliveryDto();
        matchingDelivery.setFromAirport("SVO");
        matchingDelivery.setToAirport("JFK");
        matchingDelivery.setWeight(5.0);
        matchingDelivery.setDeliveryDeadline(LocalDate.now().plusDays(7));

        DeliveryDto nonMatchingDelivery = new DeliveryDto();
        nonMatchingDelivery.setFromAirport("LED");
        nonMatchingDelivery.setToAirport("CDG");
        nonMatchingDelivery.setWeight(15.0);
        nonMatchingDelivery.setDeliveryDeadline(LocalDate.now().plusDays(1));

        when(courierProfileRepository.findById(courierId)).thenReturn(Optional.of(profile));
        when(courierRouteRepository.findByUserId(courierId)).thenReturn(Optional.of(List.of(route)));
        when(deliveryClient.getAvailableDeliveries()).thenReturn(List.of(matchingDelivery, nonMatchingDelivery));

        List<DeliveryDto> result = matchingService.findMatchingDeliveries(courierId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SVO", result.get(0).getFromAirport());
        verify(courierProfileRepository).findById(courierId);
        verify(courierRouteRepository).findByUserId(courierId);
        verify(deliveryClient).getAvailableDeliveries();
    }

    @Test
    void findMatchingCouriers() {
        DeliveryEventDto event = new DeliveryEventDto();
        event.setDeliveryId(1L);
        event.setFromAirport("SVO");
        event.setToAirport("JFK");

        CourierRoute matchingRoute = new CourierRoute();
        matchingRoute.setUserId(123L);
        matchingRoute.setDepartureAirport("SVO");
        matchingRoute.setArrivalAirport("JFK");

        CourierRoute nonMatchingRoute = new CourierRoute();
        nonMatchingRoute.setUserId(456L);
        nonMatchingRoute.setDepartureAirport("LED");
        nonMatchingRoute.setArrivalAirport("CDG");

        when(courierRouteRepository.findAll()).thenReturn(List.of(matchingRoute, nonMatchingRoute));

        matchingService.findMatchingCouriers(event);

        verify(kafkaTemplate).send(eq("courier.notifications"), eq(event));
        verify(courierRouteRepository).findAll();
    }

    @Test
    void findMatchingCouriers_NoRoutes() {
        DeliveryEventDto event = new DeliveryEventDto();
        event.setDeliveryId(1L);

        when(courierRouteRepository.findAll()).thenReturn(List.of());

        matchingService.findMatchingCouriers(event);

        verify(courierRouteRepository).findAll();
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void matchesAirports() {
        DeliveryDto delivery = new DeliveryDto();
        delivery.setFromAirport("SVO");
        delivery.setToAirport("JFK");

        CourierRoute route = new CourierRoute();
        route.setDepartureAirport("SVO");
        route.setArrivalAirport("JFK");

        boolean result = matchingService.matchesAirports(delivery, List.of(route));

        assertTrue(result);
    }

    @Test
    void matchesWeight() {
        DeliveryDto delivery = new DeliveryDto();
        delivery.setWeight(5.0);

        CourierProfile profile = new CourierProfile();
        profile.setMaxWeight(10.0);

        boolean result = matchingService.matchesWeight(delivery, profile);

        assertTrue(result);
    }

    @Test
    void matchesRoutes() {
        DeliveryDto delivery = new DeliveryDto();
        delivery.setDeliveryDeadline(LocalDate.now().plusDays(7));

        CourierRoute route = new CourierRoute();
        route.setFlightDate(LocalDate.now().plusDays(5));

        boolean result = matchingService.matchesRoutes(delivery, List.of(route));

        assertTrue(result);
    }
}