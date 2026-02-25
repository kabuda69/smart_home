package com.example.smart_home.repository;

import com.example.smart_home.entity.StatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    Page<StatusHistory> findByDeviceIdOrderByRecordedAtDesc(Long deviceId, Pageable pageable);
    
    @Query("SELECT sh FROM StatusHistory sh WHERE sh.device.id = :deviceId AND sh.recordedAt BETWEEN :start AND :end ORDER BY sh.recordedAt")
    List<StatusHistory> findByDeviceIdAndTimeRange(@Param("deviceId") Long deviceId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    void deleteByDeviceId(Long deviceId);
}
