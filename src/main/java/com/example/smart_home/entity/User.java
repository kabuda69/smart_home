package com.example.smart_home.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 表示智能家居系统的用户
 */
@Data
@Entity
@Table(name = "users")
public class User {
    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名
     */
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    /**
     * 密码
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * 角色（USER, ADMIN）
     */
    @Column(length = 20)
    private String role = "USER"; // USER, ADMIN
    
    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled = true;
}
