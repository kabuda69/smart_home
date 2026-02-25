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
    
    @GetMapping
    public ApiResponse<List<DeviceDTO>> getMyDevices(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(deviceService.getUserDevices(userId));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<DeviceDTO> getDevice(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(deviceService.getDeviceWithAuth(id, userId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<DeviceDTO> updateDevice(Authentication auth, @PathVariable Long id, @RequestBody DeviceDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("设备更新成功", deviceService.updateDevice(userId, id, dto));
    }
    
    @PostMapping
    public ApiResponse<DeviceDTO> bindDevice(Authentication auth, @RequestBody DeviceDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("设备绑定成功", deviceService.bindDevice(userId, dto));
    }
    
    @PostMapping("/command")
    public ApiResponse<DeviceDTO> executeCommand(Authentication auth, @Valid @RequestBody CommandRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(deviceService.executeCommand(userId, request));
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDevice(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        deviceService.deleteDevice(id, userId);
        return ApiResponse.success("设备删除成功", null);
    }
    
    @GetMapping("/{id}/history")
    public ApiResponse<Page<StatusHistoryDTO>> getHistory(Authentication auth, @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        // 验证设备归属
        deviceService.getDeviceWithAuth(id, userId);
        Page<StatusHistory> historyPage = statusHistoryRepository.findByDeviceIdOrderByRecordedAtDesc(id, PageRequest.of(page, size));
        return ApiResponse.success(historyPage.map(this::toHistoryDTO));
    }
    
    @GetMapping("/{id}/history/range")
    public ApiResponse<List<StatusHistoryDTO>> getHistoryByRange(Authentication auth, @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Long userId = (Long) auth.getPrincipal();
        // 验证设备归属
        deviceService.getDeviceWithAuth(id, userId);
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
