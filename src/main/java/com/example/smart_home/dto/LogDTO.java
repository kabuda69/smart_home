package com.example.smart_home.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogDTO {
    private Long id;
    private String username;
    private String operation;
    private String details;
    private String ipAddress;
    private LocalDateTime createdAt;
}
