package com.example.smart_home.repository;

import com.example.smart_home.entity.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * 场景仓库
 * 用于场景相关的数据库操作
 */
public interface SceneRepository extends JpaRepository<Scene, Long> {
    
    /**
     * 根据用户ID查找场景列表
     * @param userId 用户ID
     * @return 场景列表
     */
    List<Scene> findByUserId(Long userId);
    
    /**
     * 查找用户当前激活的场景
     * @param userId 用户ID
     * @return 激活的场景（如果存在）
     */
    Optional<Scene> findByUserIdAndIsActiveTrue(Long userId);
}
