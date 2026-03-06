package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 设备实体
 * 表示智能家居系统中的物理或虚拟设备
 */
@Data
@Entity
@Table(name = "devices")
public class Device {
    /**
     * 设备ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 设备名称
     */
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * 设备类型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private DeviceType deviceType;
    
    /**
     * 设备所属用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    /**
     * 设备状态（online, offline）
     */
    @Column(length = 20)
    private String status = "offline"; // online, offline
    
    /**
     * 电源状态
     */
    @Column(name = "power_state")
    private Boolean powerState = false;
    
    /**
     * 当前数值（如温度、亮度等）
     */
    @Column(name = "current_value")
    private Double currentValue;
    
    /**
     * 最小阈值
     */
    @Column(name = "threshold_min")
    private Double thresholdMin;
    
    /**
     * 最大阈值
     */
    @Column(name = "threshold_max")
    private Double thresholdMax;
    
    /**
     * 设备参数（JSON格式）
     */
    @Column(name = "params", columnDefinition = "TEXT")
    private String params; // JSON格式的设备参数
    
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
