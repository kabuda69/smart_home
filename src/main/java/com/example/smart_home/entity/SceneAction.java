package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "scene_actions")
public class SceneAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id")
    private Scene scene;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;
    
    @Column(name = "action_type", length = 50)
    private String actionType; // POWER_ON, POWER_OFF, SET_VALUE
    
    @Column(name = "action_value")
    private String actionValue;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
