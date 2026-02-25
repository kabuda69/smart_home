package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "shared_snapshots")
public class SharedSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;
    
    @Column(name = "status_data", columnDefinition = "TEXT")
    private String statusData; // JSON格式的状态数据
    
    @Column(name = "link_uuid", unique = true, length = 36)
    private String linkUuid;
    
    @Column(name = "expire_time")
    private LocalDateTime expireTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
