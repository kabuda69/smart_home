package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "device_types")
public class DeviceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(name = "param_template", columnDefinition = "TEXT")
    private String paramTemplate; // JSON格式的参数模板
    
    @Column(length = 200)
    private String description;
    
    @Column(length = 100)
    private String icon;
}
