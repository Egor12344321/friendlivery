package com.globallogix.delivery.repository;

import com.globallogix.delivery.entity.UserLocations;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocationsRepository extends JpaRepository<UserLocations, Long> {
}
