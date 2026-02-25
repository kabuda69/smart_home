package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private DeviceType deviceType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(length = 20)
    private String status = "offline"; // online, offline
    
    @Column(name = "power_state")
    private Boolean powerState = false;
    
    @Column(name = "current_value")
    private Double currentValue;
    
    @Column(name = "threshold_min")
    private Double thresholdMin;
    
    @Column(name = "threshold_max")
    private Double thresholdMax;
    
    @Column(name = "params", columnDefinition = "TEXT")
    private String params; // JSON格式的设备参数
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
