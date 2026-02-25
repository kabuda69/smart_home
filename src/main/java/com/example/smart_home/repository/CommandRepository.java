package com.example.smart_home.repository;

import com.example.smart_home.entity.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CommandRepository extends JpaRepository<Command, Long> {
    List<Command> findByDeviceIdOrderByExecutedAtDesc(Long deviceId);
    List<Command> findByUserIdOrderByExecutedAtDesc(Long userId);
    
    long countByUserId(Long userId);
    
    @Query("SELECT d.name, COUNT(c) FROM Command c JOIN c.device d WHERE d.user.id = :userId AND c.executedAt >= :startDate GROUP BY d.id, d.name ORDER BY COUNT(c) DESC")
    List<Object[]> getCommandFrequency(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT c.commandType, COUNT(c) FROM Command c WHERE c.executedAt >= :startDate GROUP BY c.commandType")
    List<Object[]> getCommandTypeStats(@Param("startDate") LocalDateTime startDate);
    
    void deleteByDeviceId(Long deviceId);
}
