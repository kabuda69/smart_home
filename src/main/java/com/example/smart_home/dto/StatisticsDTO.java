package com.example.smart_home.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsDTO {
    private Long totalDevices;
    private Long onlineDevices;
    private Long unreadAlerts;
    private Long totalAlerts;
    private Long totalCommands;
    private Map<String, Long> devicesByType;
    private List<Map<String, Object>> monthlyAlerts;
    private List<Map<String, Object>> commandFrequency;
}
