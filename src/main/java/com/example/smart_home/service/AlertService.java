package com.example.smart_home.service;

import com.example.smart_home.dto.AlertDTO;
import com.example.smart_home.entity.Alert;
import com.example.smart_home.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {
    @Autowired
    private AlertRepository alertRepository;
    
    public Page<AlertDTO> getUserAlerts(Long userId, Pageable pageable) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::toDTO);
    }
    
    public List<AlertDTO> getUnreadAlerts(Long userId) {
        return alertRepository.findByUserIdAndIsReadFalse(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void markAsRead(Long alertId, Long userId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("警报不存在"));
        if (!alert.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此警报");
        }
        alert.setIsRead(true);
        alertRepository.save(alert);
    }
    
    @Transactional
    public void markAllAsRead(Long userId) {
        alertRepository.findByUserIdAndIsReadFalse(userId).forEach(a -> {
            a.setIsRead(true);
            alertRepository.save(a);
        });
    }
    
    @Transactional
    public void deleteAlert(Long alertId, Long userId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("警报不存在"));
        if (!alert.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此警报");
        }
        alertRepository.delete(alert);
    }
    
    public List<AlertDTO> getAllAlerts(Long userId) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private AlertDTO toDTO(Alert alert) {
        AlertDTO dto = new AlertDTO();
        dto.setId(alert.getId());
        dto.setDeviceId(alert.getDevice() != null ? alert.getDevice().getId() : null);
        dto.setDeviceName(alert.getDevice() != null ? alert.getDevice().getName() : null);
        dto.setThresholdValue(alert.getThresholdValue());
        dto.setActualValue(alert.getActualValue());
        dto.setMessage(alert.getMessage());
        dto.setAlertType(alert.getAlertType());
        dto.setIsRead(alert.getIsRead());
        dto.setCreatedAt(alert.getCreatedAt());
        return dto;
    }
}
