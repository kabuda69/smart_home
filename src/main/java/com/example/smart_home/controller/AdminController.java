package com.example.smart_home.controller;

import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.dto.FeedbackDTO;
import com.example.smart_home.dto.LogDTO;
import com.example.smart_home.dto.UserDTO;
import com.example.smart_home.entity.DeviceType;
import com.example.smart_home.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeviceTypeService deviceTypeService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private LogService logService;
    
    // 用户管理
    @GetMapping("/users")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        return ApiResponse.success(userService.getAllUsers());
    }
    
    @PutMapping("/users/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        userService.updateUserStatus(id, body.get("enabled"));
        return ApiResponse.success("用户状态已更新", null);
    }
    
    // 设备类型管理
    @GetMapping("/device-types")
    public ApiResponse<List<DeviceType>> getAllDeviceTypes() {
        return ApiResponse.success(deviceTypeService.getAllTypes());
    }
    
    @PostMapping("/device-types")
    public ApiResponse<DeviceType> createDeviceType(@RequestBody DeviceType type) {
        return ApiResponse.success("设备类型创建成功", deviceTypeService.createType(type));
    }
    
    @PutMapping("/device-types/{id}")
    public ApiResponse<DeviceType> updateDeviceType(@PathVariable Long id, @RequestBody DeviceType type) {
        return ApiResponse.success("设备类型更新成功", deviceTypeService.updateType(id, type));
    }
    
    @DeleteMapping("/device-types/{id}")
    public ApiResponse<Void> deleteDeviceType(@PathVariable Long id) {
        deviceTypeService.deleteType(id);
        return ApiResponse.success("设备类型已删除", null);
    }
    
    // 反馈管理
    @GetMapping("/feedbacks")
    public ApiResponse<Page<FeedbackDTO>> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(feedbackService.getAllFeedbacks(PageRequest.of(page, size)));
    }
    
    @PutMapping("/feedbacks/{id}/reply")
    public ApiResponse<FeedbackDTO> replyFeedback(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ApiResponse.success("回复成功", feedbackService.replyFeedback(id, body.get("reply")));
    }
    
    // 日志查看
    @GetMapping("/logs")
    public ApiResponse<Page<LogDTO>> getAllLogs(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(logService.getAllLogs(PageRequest.of(page, size)));
    }
}
