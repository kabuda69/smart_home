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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    
    // 用户管理模块
    //查询系统中所有用户的列表（管理员视角）
    @GetMapping("/users")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        return ApiResponse.success(userService.getAllUsers());
    }
    //更新指定用户的启用/禁用状态
    @PutMapping("/users/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        userService.updateUserStatus(id, body.get("enabled"));
        return ApiResponse.success("用户状态已更新", null);
    }
    
    // 设备类型管理模块
    //查询系统中所有设备类型（如灯、空调、窗帘等）
    @GetMapping("/device-types")
    public ApiResponse<List<DeviceType>> getAllDeviceTypes() {
        return ApiResponse.success(deviceTypeService.getAllTypes());
    }
    //新增设备类型
    @PostMapping("/device-types")
    public ApiResponse<DeviceType> createDeviceType(@RequestBody DeviceType type) {
        return ApiResponse.success("设备类型创建成功", deviceTypeService.createType(type));
    }
    //更新指定ID的设备类型信息
    @PutMapping("/device-types/{id}")
    public ApiResponse<DeviceType> updateDeviceType(@PathVariable Long id, @RequestBody DeviceType type) {
        return ApiResponse.success("设备类型更新成功", deviceTypeService.updateType(id, type));
    }
    //删除指定ID的设备类型
    @DeleteMapping("/device-types/{id}")
    public ApiResponse<Void> deleteDeviceType(@PathVariable Long id) {
        deviceTypeService.deleteType(id);
        return ApiResponse.success("设备类型已删除", null);
    }
    
    // 反馈管理模块
    //分页查询系统中所有用户的反馈信息
    @GetMapping("/feedbacks")
    public ApiResponse<Page<FeedbackDTO>> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(feedbackService.getAllFeedbacks(PageRequest.of(page, size)));
    }
    //回复指定ID的用户反馈
    @PutMapping("/feedbacks/{id}/reply")
    public ApiResponse<FeedbackDTO> replyFeedback(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ApiResponse.success("回复成功", feedbackService.replyFeedback(id, body.get("reply")));
    }
    
    // 日志查看模块
    //分页查询系统所有操作日志（支持时间段筛选）
    @GetMapping("/logs")
    public ApiResponse<Page<LogDTO>> getAllLogs(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        if (startTime != null && endTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);
            return ApiResponse.success(logService.getAllLogsByTimeRange(start, end, PageRequest.of(page, size)));
        }
        return ApiResponse.success(logService.getAllLogs(PageRequest.of(page, size)));
    }
}
