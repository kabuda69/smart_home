package com.example.smart_home.controller;

import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.dto.StatisticsDTO;
import com.example.smart_home.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;
    //数据统计查询模块
    //查询当前登录用户的个性化统计数据（如设备在线数、场景执行次数、能耗统计等）
    @GetMapping
    public ApiResponse<StatisticsDTO> getStatistics(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(statisticsService.getUserStatistics(userId));
    }
}
