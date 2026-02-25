package com.example.smart_home.repository;

import com.example.smart_home.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
    Page<Log> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Log> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
