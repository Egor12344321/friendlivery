package com.globallogix.flight.service;


import com.globallogix.flight.client.DeliveryClient;
import com.globallogix.flight.DeliveryDto;
import com.globallogix.flight.entity.CourierProfile;
import com.globallogix.flight.entity.CourierRoute;
import com.globallogix.flight.repository.CourierProfileRepository;
import com.globallogix.flight.repository.CourierRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<DeliveryDto> findMatchingDeliveries(Long courierId){
        log.info("Поиск подходящих заявок для курьера: {}", courierId);
        CourierProfile profile = courierProfileRepository.findById(courierId)
                .orElseThrow(() -> new RuntimeException("Courier`s profile not found"));
        List<CourierRoute> routes = courierRouteRepository.findByUserId(courierId);
        if (routes.isEmpty()){
            log.warn("У курьера {} нет маршрутов", courierId);
            return List.of();
        }
        List<DeliveryDto> allDeliveries = deliveryClient.getAvailableDeliveries();
        log.info("Получено {} доступных заявок", allDeliveries.size());
        return allDeliveries.stream()
                .filter(delivery -> matchesRoutes(delivery, routes))
                .filter(delivery -> matchesWeight(delivery, profile))
                .filter(delivery -> matchesDeadline(delivery, routes))
                .collect(Collectors.toList());
    }

    private boolean matchesDeadline(DeliveryDto delivery, List<CourierRoute> routes) {
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

        log.debug("📅 Дедлайн доставки {}: {}",
                delivery.getDeliveryDeadline(),
                matches ? "УСПЕВАЕТ" : "НЕ УСПЕВАЕТ");
        return matches;
    }

}
