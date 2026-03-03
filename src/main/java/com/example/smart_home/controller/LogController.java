package com.example.smart_home.controller;

import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.dto.LogDTO;
import com.example.smart_home.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    @Autowired
    private LogService logService;
    //日志查询模块
    //分页查询当前登录用户的所有操作日志（如设备控制、反馈提交、账号操作等）
    @GetMapping
    public ApiResponse<Page<LogDTO>> getMyLogs(Authentication auth,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(logService.getUserLogs(userId, PageRequest.of(page, size)));
    }
}
