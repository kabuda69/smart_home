package com.example.smart_home.controller;

import com.example.smart_home.dto.*;
import com.example.smart_home.entity.StatusHistory;
import com.example.smart_home.repository.StatusHistoryRepository;
import com.example.smart_home.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private StatusHistoryRepository statusHistoryRepository;
    // 获取当前用户绑定的所有设备列表
    @GetMapping
    public ApiResponse<List<DeviceDTO>> getMyDevices(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();// 权限校验：获取当前登录用户ID（身份认证）
        return ApiResponse.success(deviceService.getUserDevices(userId));
    }
    //获取指定ID的设备详情（需验证设备归属）
    @GetMapping("/{id}")
    public ApiResponse<DeviceDTO> getDevice(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal(); // 权限校验：身份认证
        return ApiResponse.success(deviceService.getDeviceWithAuth(id, userId));// 权限校验：设备归属验证
    }
    // 更新指定设备的信息（名称、描述等）
    @PutMapping("/{id}")
    public ApiResponse<DeviceDTO> updateDevice(Authentication auth, @PathVariable Long id, @RequestBody DeviceDTO dto) {
        Long userId = (Long) auth.getPrincipal();// 权限校验：身份认证
        return ApiResponse.success("设备更新成功", deviceService.updateDevice(userId, id, dto));// 权限校验：Service层验证设备归属
    }
    //绑定新设备到当前用户
    @PostMapping
    public ApiResponse<DeviceDTO> bindDevice(Authentication auth, @RequestBody DeviceDTO dto) {
        Long userId = (Long) auth.getPrincipal();// 权限校验：身份认证
        return ApiResponse.success("设备绑定成功", deviceService.bindDevice(userId, dto));// 权限校验：Service层绑定当前用户
    }
    //向指定设备发送控制命令（如开关、调节等）
    @PostMapping("/command")
    public ApiResponse<DeviceDTO> executeCommand(Authentication auth, @Valid @RequestBody CommandRequest request) {
        Long userId = (Long) auth.getPrincipal();// 权限校验：身份认证
        return ApiResponse.success(deviceService.executeCommand(userId, request));// 权限校验：Service层验证设备归属
    }
    // 删除指定ID的设备
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDevice(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();// 权限校验：身份认证
        deviceService.deleteDevice(id, userId);// 权限校验：Service层验证设备归属
        return ApiResponse.success("设备删除成功", null);
    }
    // 获取设备状态历史记录（分页）
    @GetMapping("/{id}/history")
    public ApiResponse<Page<StatusHistoryDTO>> getHistory(Authentication auth, @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal(); // 权限校验：身份认证
        deviceService.getDeviceWithAuth(id, userId);  // 权限校验：验证设备归属（非当前用户设备会抛异常）
        Page<StatusHistory> historyPage = statusHistoryRepository.findByDeviceIdOrderByRecordedAtDesc(id, PageRequest.of(page, size));
        return ApiResponse.success(historyPage.map(this::toHistoryDTO));
    }
    //获取指定时间范围内的设备状态历史，额外校验：@DateTimeFormat 校验时间参数格式合法性
    @GetMapping("/{id}/history/range")
    public ApiResponse<List<StatusHistoryDTO>> getHistoryByRange(Authentication auth, @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Long userId = (Long) auth.getPrincipal();// 权限校验：身份认证
        deviceService.getDeviceWithAuth(id, userId);// 权限校验：验证设备归属（非当前用户设备会抛异常）
        List<StatusHistory> historyList = statusHistoryRepository.findByDeviceIdAndTimeRange(id, start, end);
        return ApiResponse.success(historyList.stream().map(this::toHistoryDTO).collect(Collectors.toList()));
    }
    
    private StatusHistoryDTO toHistoryDTO(StatusHistory history) {
        StatusHistoryDTO dto = new StatusHistoryDTO();
        dto.setId(history.getId());
        dto.setStatusValue(history.getStatusValue());
        dto.setPowerState(history.getPowerState());
        dto.setRecordedAt(history.getRecordedAt());
        return dto;
    }
}
