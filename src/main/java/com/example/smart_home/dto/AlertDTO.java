package com.example.smart_home.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlertDTO {
    private Long id;
    private Long deviceId;
    private String deviceName;
    private Double thresholdValue;
    private Double actualValue;
    private String message;
    private String alertType;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
