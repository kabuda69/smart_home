package com.example.smart_home.controller;

import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.dto.NotificationPreferenceDTO;
import com.example.smart_home.service.LogService;
import com.example.smart_home.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private LogService logService;
    
    @GetMapping("/preferences")
    public ApiResponse<List<NotificationPreferenceDTO>> getNotificationPreferences(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(userService.getNotificationPreferences(userId));
    }
    
    @PutMapping("/preferences")
    public ApiResponse<Void> updateNotificationPreference(Authentication auth, @RequestBody Map<String, Object> body) {
        Long userId = (Long) auth.getPrincipal();
        String type = (String) body.get("type");
        Boolean enabled = (Boolean) body.get("enabled");
        userService.updateNotificationPreference(userId, type, enabled);
        return ApiResponse.success("通知偏好已更新", null);
    }
    
    @GetMapping("/logs")
    public ApiResponse<org.springframework.data.domain.Page<com.example.smart_home.dto.LogDTO>> getMyLogs(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(logService.getUserLogs(userId, org.springframework.data.domain.PageRequest.of(page, size)));
    }
}
