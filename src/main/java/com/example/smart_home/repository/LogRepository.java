package com.example.smart_home.repository;

import com.example.smart_home.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface LogRepository extends JpaRepository<Log, Long> {
    Page<Log> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Log> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 按用户ID和时间段查询日志
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 日志分页结果
     */
    @Query("SELECT l FROM Log l WHERE l.user.id = :userId AND l.createdAt BETWEEN :startTime AND :endTime ORDER BY l.createdAt DESC")
    Page<Log> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, 
                                                @Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime, 
                                                Pageable pageable);
    
    /**
     * 按时间段查询所有日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 日志分页结果
     */
    @Query("SELECT l FROM Log l WHERE l.createdAt BETWEEN :startTime AND :endTime ORDER BY l.createdAt DESC")
    Page<Log> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime, 
                                       Pageable pageable);
}
