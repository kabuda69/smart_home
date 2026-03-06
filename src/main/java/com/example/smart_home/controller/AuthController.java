package com.example.smart_home.controller;

import com.example.smart_home.dto.*;
import com.example.smart_home.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 认证控制器
 * 处理用户注册和登录相关的HTTP请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册接口
     * 新用户创建账号
     * @param request 注册请求参数，包含用户名、密码、邮箱等信息
     * @return 注册结果，包含用户信息和JWT令牌
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }
    
    /**
     * 用户登录接口
     * 验证账号密码并生成JWT令牌
     * @param request 登录请求参数，包含用户名和密码
     * @return 登录结果，包含用户信息和JWT令牌
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }
}
