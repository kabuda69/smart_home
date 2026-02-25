package com.example.smart_home.controller;

import com.example.smart_home.dto.AlertDTO;
import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    @Autowired
    private AlertService alertService;
    
    @GetMapping
    public ApiResponse<Page<AlertDTO>> getAlerts(Authentication auth,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(alertService.getUserAlerts(userId, PageRequest.of(page, size)));
    }
    
    @GetMapping("/unread")
    public ApiResponse<List<AlertDTO>> getUnreadAlerts(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(alertService.getUnreadAlerts(userId));
    }
    
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        alertService.markAsRead(id, userId);
        return ApiResponse.success(null);
    }
    
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        alertService.markAllAsRead(userId);
        return ApiResponse.success(null);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAlert(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        alertService.deleteAlert(id, userId);
        return ApiResponse.success("警报已删除", null);
    }
}
