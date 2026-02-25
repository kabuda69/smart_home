package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "threshold_value")
    private Double thresholdValue;
    
    @Column(name = "actual_value")
    private Double actualValue;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "alert_type", length = 50)
    private String alertType; // THRESHOLD_EXCEEDED, DEVICE_OFFLINE, ABNORMAL
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
