package com.example.smart_home.controller;

import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.dto.SceneDTO;
import com.example.smart_home.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 场景控制器
 * 处理智能场景相关的HTTP请求，包括场景的创建、查询、更新、激活、停用和删除等操作
 */
@RestController
@RequestMapping("/api/scenes")
public class SceneController {
    
    @Autowired
    private SceneService sceneService;
    
    /**
     * 查询当前登录用户创建的所有智能场景列表
     * @param auth 认证信息，用于获取当前登录用户ID
     * @return 场景列表
     */
    @GetMapping
    public ApiResponse<List<SceneDTO>> getScenes(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sceneService.getUserScenes(userId));
    }
    
    /**
     * 根据ID查询当前登录用户名下单个智能场景的详情
     * @param auth 认证信息，用于获取当前登录用户ID
     * @param id 场景ID
     * @return 场景详情
     */
    @GetMapping("/{id}")
    public ApiResponse<SceneDTO> getScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sceneService.getScene(id, userId));
    }
    
    /**
     * 当前登录用户创建新的智能场景
     * @param auth 认证信息，用于获取当前登录用户ID
     * @param dto 场景信息
     * @return 创建后的场景信息
     */
    @PostMapping
    public ApiResponse<SceneDTO> createScene(Authentication auth, @RequestBody SceneDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("场景创建成功", sceneService.createScene(userId, dto));
    }
    
    /**
     * 更新当前登录用户名下指定ID的智能场景配置
     * @param auth 认证信息，用于获取当前登录用户ID
     * @param id 场景ID
     * @param dto 场景更新信息
     * @return 更新后的场景信息
     */
    @PutMapping("/{id}")
    public ApiResponse<SceneDTO> updateScene(Authentication auth, @PathVariable Long id, @RequestBody SceneDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("场景更新成功", sceneService.updateScene(userId, id, dto));
    }
    
    /**
     * 激活当前登录用户名下指定ID的智能场景（场景生效）
     * @param auth 认证信息，用于获取当前登录用户ID
     * @param id 场景ID
     * @return 操作结果
     */
    @PostMapping("/{id}/activate")
    public ApiResponse<Void> activateScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sceneService.activateScene(userId, id);
        return ApiResponse.success("场景已激活", null);
    }
    
    /**
     * 停用当前登录用户名下指定ID的智能场景（场景失效）
     * @param auth 认证信息，用于获取当前登录用户ID
     * @param id 场景ID
     * @return 操作结果
     */
    @PostMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sceneService.deactivateScene(userId, id);
        return ApiResponse.success("场景已停用", null);
    }
    
    /**
     * 删除当前登录用户名下指定ID的智能场景
     * @param auth 认证信息，用于获取当前登录用户ID
     * @param id 场景ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sceneService.deleteScene(userId, id);
        return ApiResponse.success("场景已删除", null);
    }
}
