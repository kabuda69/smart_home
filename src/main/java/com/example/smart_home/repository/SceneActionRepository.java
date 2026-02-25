package com.example.smart_home.repository;

import com.example.smart_home.entity.SceneAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SceneActionRepository extends JpaRepository<SceneAction, Long> {
    List<SceneAction> findBySceneIdOrderBySortOrder(Long sceneId);
    void deleteBySceneId(Long sceneId);
    void deleteByDeviceId(Long deviceId);
}
