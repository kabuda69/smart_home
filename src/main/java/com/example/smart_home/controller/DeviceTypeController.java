package com.example.smart_home.controller;

import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.entity.DeviceType;
import com.example.smart_home.service.DeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/device-types")
public class DeviceTypeController {
    @Autowired
    private DeviceTypeService deviceTypeService;
    //设备类型查询模块
    //查询系统中所有设备类型（如灯、空调、窗帘、传感器等）
    @GetMapping
    public ApiResponse<List<DeviceType>> getAllTypes() {
        return ApiResponse.success(deviceTypeService.getAllTypes());
    }
    //根据ID查询单个设备类型的详情
    @GetMapping("/{id}")
    public ApiResponse<DeviceType> getType(@PathVariable Long id) {
        return ApiResponse.success(deviceTypeService.getType(id));
    }
}
