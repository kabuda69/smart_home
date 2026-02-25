package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "notification_type", length = 50)
    private String notificationType; // POPUP, LOG, EMAIL
    
    @Column(name = "enabled")
    private Boolean enabled = true;
}
