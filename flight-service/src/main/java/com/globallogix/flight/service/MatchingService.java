package com.globallogix.flight.service;


import com.globallogix.flight.client.DeliveryClient;
import com.globallogix.flight.dto.DeliveryDto;
import com.globallogix.flight.entity.CourierProfile;
import com.globallogix.flight.entity.CourierRoute;
import com.globallogix.flight.exception.custom_exceptions.RoutesNotFoundException;
import com.globallogix.flight.kafka.events.DeliveryEventDto;
import com.globallogix.flight.repository.CourierProfileRepository;
import com.globallogix.flight.repository.CourierRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class MatchingService {
    private final DeliveryClient deliveryClient;
    private final CourierProfileRepository courierProfileRepository;
    private final CourierRouteRepository courierRouteRepository;
    private final KafkaTemplate<String, DeliveryEventDto> kafkaTemplate;

    public List<DeliveryDto> findMatchingDeliveries(Long courierId){
        log.debug("MATCHING-SERVICE: Поиск подходящих заявок для курьера: {}", courierId);
        CourierProfile profile = courierProfileRepository.findById(courierId)
                .orElseThrow(() -> new RuntimeException("Courier`s profile not found"));
        List<CourierRoute> routes = courierRouteRepository.findByUserId(courierId)
                .orElseThrow(() -> new RoutesNotFoundException("Courier`s routes not found"));

        List<DeliveryDto> allDeliveries = deliveryClient.getAvailableDeliveries();
        log.info("Получено {} доступных заявок", allDeliveries.size());
        return allDeliveries.stream()
                .filter(delivery -> matchesRoutes(delivery, routes))
                .filter(delivery -> matchesWeight(delivery, profile))
                .filter(delivery -> matchesAirports(delivery, routes))
                .collect(Collectors.toList());
    }

    private boolean matchesAirports(DeliveryDto delivery, List<CourierRoute> routes) {
        boolean matches = routes.stream()
                .anyMatch(route ->
                        route.getDepartureAirport().equals(delivery.getFromAirport())
                                && route.getArrivalAirport().equals(delivery.getToAirport())
                );

        log.debug("Маршрут {}->{}: {}",
                delivery.getFromAirport(), delivery.getToAirport(),
                matches ? "СОВПАЛ" : "НЕ СОВПАЛ");
        return matches;
    }

    private boolean matchesWeight(DeliveryDto delivery, CourierProfile profile) {
        boolean matches = delivery.getWeight() <= profile.getMaxWeight();

        log.debug("Вес заявки {}кг vs курьер {}кг: {}",
                delivery.getWeight(), profile.getMaxWeight(),
                matches ? "ПОДХОДИТ" : "СЛИШКОМ ТЯЖЕЛО");
        return matches;
    }

    private boolean matchesRoutes(DeliveryDto delivery, List<CourierRoute> routes) {
        boolean matches = routes.stream()
                .anyMatch(route ->
                        route.getFlightDate().isBefore(delivery.getDeliveryDeadline())
                                || route.getFlightDate().isEqual(delivery.getDeliveryDeadline())
                );

        log.debug("Дедлайн доставки {}: {}",
                delivery.getDeliveryDeadline(),
                matches ? "УСПЕВАЕТ" : "НЕ УСПЕВАЕТ");
        return matches;
    }


    public void findMatchingCouriers(DeliveryEventDto event) {
        log.debug("Matching couriers started for delivery: {}", event.getDeliveryId());
        List<CourierRoute> routes = courierRouteRepository.findAll();
        if (routes.isEmpty()) {
            log.info("No courier routes found");
            return;
        }

        for (CourierRoute route : routes) {
            if (route.getDepartureAirport().equals(event.getFromAirport()) &&
                    route.getArrivalAirport().equals(event.getToAirport())) {
                notifyCourier(route.getUserId(), event);

            }
        }
    }

    private void notifyCourier(Long userId, DeliveryEventDto event) {
        log.info("Notification to matched courier creation: {}", userId);
        kafkaTemplate.send("courier.notifications", event);
    }
}
