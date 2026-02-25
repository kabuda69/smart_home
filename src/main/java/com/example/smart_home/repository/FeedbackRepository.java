package com.example.smart_home.repository;

import com.example.smart_home.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Page<Feedback> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Feedback> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Feedback> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
}
