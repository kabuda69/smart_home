package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 场景实体
 * 表示智能家居系统中的智能场景，包含一系列设备动作
 */
@Data
@Entity
@Table(name = "scenes")
public class Scene {
    /**
     * 场景ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 场景所属用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    /**
     * 场景名称（如回家模式, 离家模式）
     */
    @Column(nullable = false, length = 50)
    private String name; // 回家模式, 离家模式
    
    /**
     * 场景描述
     */
    @Column(length = 200)
    private String description;
    
    /**
     * 是否激活
     */
    @Column(name = "is_active")
    private Boolean isActive = false;
    
    /**
     * 场景包含的动作列表
     */
    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SceneAction> actions;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
