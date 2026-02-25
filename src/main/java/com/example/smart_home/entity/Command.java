package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "commands")
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "command_type", length = 50)
    private String commandType; // POWER_ON, POWER_OFF, SET_VALUE, SET_THRESHOLD
    
    @Column(name = "command_value")
    private String commandValue;
    
    @Column(name = "executed_at")
    private LocalDateTime executedAt = LocalDateTime.now();
    
    @Column(length = 20)
    private String status = "SUCCESS"; // SUCCESS, FAILED, PENDING
}
