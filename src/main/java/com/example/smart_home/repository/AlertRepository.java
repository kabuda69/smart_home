package com.example.smart_home.repository;

import com.example.smart_home.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    Page<Alert> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Alert> findByUserIdAndIsReadFalse(Long userId);
    List<Alert> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    long countByUserId(Long userId);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.user.id = :userId AND a.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT FUNCTION('YEAR', a.createdAt), FUNCTION('MONTH', a.createdAt), COUNT(a) FROM Alert a WHERE a.user.id = :userId AND a.createdAt >= :startDate GROUP BY FUNCTION('YEAR', a.createdAt), FUNCTION('MONTH', a.createdAt) ORDER BY FUNCTION('YEAR', a.createdAt), FUNCTION('MONTH', a.createdAt)")
    List<Object[]> getMonthlyAlertStats(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
    
    void deleteByDeviceId(Long deviceId);
}
