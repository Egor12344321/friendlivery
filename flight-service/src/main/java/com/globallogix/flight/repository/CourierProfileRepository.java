package com.globallogix.flight.repository;

import com.globallogix.flight.entity.CourierProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CourierProfileRepository extends JpaRepository<CourierProfile, Long> {
    CourierProfile findByUserId(Long userId);
}
