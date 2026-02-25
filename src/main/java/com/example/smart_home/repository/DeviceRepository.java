package com.example.smart_home.repository;

import com.example.smart_home.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserId(Long userId);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.user.id = :userId AND d.status = 'online'")
    long countOnlineByUserId(@Param("userId") Long userId);
    
    @Query("SELECT d.deviceType.name, COUNT(d) FROM Device d WHERE d.user.id = :userId GROUP BY d.deviceType.name")
    List<Object[]> countByTypeForUser(@Param("userId") Long userId);
}
