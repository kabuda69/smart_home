package com.example.smart_home.repository;

import com.example.smart_home.entity.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long> {
    Optional<DeviceType> findByName(String name);
    boolean existsByName(String name);
}
