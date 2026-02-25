package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "scenes")
public class Scene {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false, length = 50)
    private String name; // 回家模式, 离家模式
    
    @Column(length = 200)
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = false;
    
    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SceneAction> actions;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
