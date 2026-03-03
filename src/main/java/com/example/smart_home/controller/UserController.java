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
    //通知偏好管理模块
    //查询当前登录用户的所有通知偏好设置（如设备告警、场景提醒、系统通知等类型的开关）
    @GetMapping("/preferences")
    public ApiResponse<List<NotificationPreferenceDTO>> getNotificationPreferences(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(userService.getNotificationPreferences(userId));
    }
    //更新当前登录用户指定类型的通知偏好开关状态
    @PutMapping("/preferences")
    public ApiResponse<Void> updateNotificationPreference(Authentication auth, @RequestBody Map<String, Object> body) {
        Long userId = (Long) auth.getPrincipal();
        String type = (String) body.get("type");
        Boolean enabled = (Boolean) body.get("enabled");
        userService.updateNotificationPreference(userId, type, enabled);
        return ApiResponse.success("通知偏好已更新", null);
    }
    //个人日志查询模块
    //分页查询当前登录用户的操作日志（和/api/logs接口功能一致，属于用户中心入口）
    @GetMapping("/logs")
    public ApiResponse<org.springframework.data.domain.Page<com.example.smart_home.dto.LogDTO>> getMyLogs(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(logService.getUserLogs(userId, org.springframework.data.domain.PageRequest.of(page, size)));
    }
}
