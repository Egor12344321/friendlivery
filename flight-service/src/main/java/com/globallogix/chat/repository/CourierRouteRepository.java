package com.globallogix.chat.repository;

import com.globallogix.chat.entity.CourierRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourierRouteRepository extends JpaRepository<CourierRoute, Long> {
    Optional<List<CourierRoute>> findByUserId(Long userId);

}
