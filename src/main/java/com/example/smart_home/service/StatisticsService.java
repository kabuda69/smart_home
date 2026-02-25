package com.example.smart_home.service;

import com.example.smart_home.dto.StatisticsDTO;
import com.example.smart_home.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private CommandRepository commandRepository;
    
    public StatisticsDTO getUserStatistics(Long userId) {
        StatisticsDTO stats = new StatisticsDTO();
        stats.setTotalDevices(deviceRepository.countByUserId(userId));
        stats.setOnlineDevices(deviceRepository.countOnlineByUserId(userId));
        stats.setUnreadAlerts(alertRepository.countUnreadByUserId(userId));
        
        // 按类型统计设备
        Map<String, Long> devicesByType = new HashMap<>();
        deviceRepository.countByTypeForUser(userId).forEach(row -> 
            devicesByType.put((String) row[0], (Long) row[1]));
        stats.setDevicesByType(devicesByType);
        
        // 每月警报统计（最近12个月）
        LocalDateTime startDate = LocalDateTime.now().minusMonths(12);
        List<Map<String, Object>> monthlyAlerts = alertRepository.getMonthlyAlertStats(userId, startDate).stream()
            .map(row -> {
                Map<String, Object> m = new HashMap<>();
                m.put("year", row[0]);
                m.put("month", row[1]);
                m.put("count", row[2]);
                return m;
            }).collect(Collectors.toList());
        stats.setMonthlyAlerts(monthlyAlerts);
        
        // 设备命令执行频率（30天内）
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        List<Map<String, Object>> cmdFreq = commandRepository.getCommandFrequency(userId, last30Days).stream()
            .map(row -> {
                Map<String, Object> m = new HashMap<>();
                m.put("deviceName", row[0]);
                m.put("commandCount", row[1]);
                return m;
            }).collect(Collectors.toList());
        stats.setCommandFrequency(cmdFreq);
        
        // 总警报数和总命令数
        stats.setTotalAlerts(alertRepository.countByUserId(userId));
        stats.setTotalCommands(commandRepository.countByUserId(userId));
        
        return stats;
    }
}
