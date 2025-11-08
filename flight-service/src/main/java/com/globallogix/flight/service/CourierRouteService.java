package com.globallogix.flight.service;


import com.globallogix.flight.dto.CourierRouteRequest;
import com.globallogix.flight.entity.CourierRoute;
import com.globallogix.flight.exception.custom_exceptions.RoutesNotFoundException;
import com.globallogix.flight.repository.CourierRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourierRouteService {
    private final CourierRouteRepository courierRouteRepository;

    public CourierRoute createRoute(Long userId, CourierRouteRequest request) {
        log.debug("FLIGHT-SERVICE: Create route for user {} started", userId);
        CourierRoute route = CourierRoute.builder()
                .departureAirport(request.departureAirport())
                .arrivalAirport(request.arrivalAirport())
                .userId(userId)
                .flightDate(request.flightDate())
                .build();
        log.info("FLIGHT-SERVICE: Rout created successfully");
        courierRouteRepository.save(route);
        log.info("FLIGHT-SERVICE: Rout saved to db successfully");
        return route;
    }


    public List<CourierRoute> getUserRoutes(Long userId) {
        log.debug("FLIGHT-SERVICE: Started getting user's routes");
        List<CourierRoute> routes = courierRouteRepository.findByUserId(userId)
                .orElseThrow(() -> new RoutesNotFoundException("Routes for user " + userId + " not found"));
        log.info("FLIGHT-SERVICE: Routes found successfully for user {}", userId);
        return routes;
    }
}
