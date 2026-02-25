package com.example.smart_home.dto;

import lombok.Data;

@Data
public class NotificationPreferenceDTO {
    private Long id;
    private String notificationType;
    private Boolean enabled;
}
