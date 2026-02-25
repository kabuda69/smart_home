package com.example.smart_home.repository;

import com.example.smart_home.entity.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SceneRepository extends JpaRepository<Scene, Long> {
    List<Scene> findByUserId(Long userId);
    Optional<Scene> findByUserIdAndIsActiveTrue(Long userId);
}
