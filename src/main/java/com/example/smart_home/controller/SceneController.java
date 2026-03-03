package com.example.smart_home.controller;

import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.dto.SceneDTO;
import com.example.smart_home.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/scenes")
public class SceneController {
    @Autowired
    private SceneService sceneService;
    //场景查询模块
    //查询当前登录用户创建的所有智能场景列表
    @GetMapping
    public ApiResponse<List<SceneDTO>> getScenes(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sceneService.getUserScenes(userId));
    }
    //根据ID查询当前登录用户名下单个智能场景的详情
    @GetMapping("/{id}")
    public ApiResponse<SceneDTO> getScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sceneService.getScene(id, userId));
    }
    //场景创建/更新模块
    //当前登录用户创建新的智能场景
    @PostMapping
    public ApiResponse<SceneDTO> createScene(Authentication auth, @RequestBody SceneDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("场景创建成功", sceneService.createScene(userId, dto));
    }
    //更新当前登录用户名下指定ID的智能场景配置
    @PutMapping("/{id}")
    public ApiResponse<SceneDTO> updateScene(Authentication auth, @PathVariable Long id, @RequestBody SceneDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("场景更新成功", sceneService.updateScene(userId, id, dto));
    }
    //场景激活/停用模块
    //激活当前登录用户名下指定ID的智能场景（场景生效）
    @PostMapping("/{id}/activate")
    public ApiResponse<Void> activateScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sceneService.activateScene(userId, id);
        return ApiResponse.success("场景已激活", null);
    }
    //停用当前登录用户名下指定ID的智能场景（场景失效）
    @PostMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sceneService.deactivateScene(userId, id);
        return ApiResponse.success("场景已停用", null);
    }
    //场景删除模块
    //删除当前登录用户名下指定ID的智能场景
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sceneService.deleteScene(userId, id);
        return ApiResponse.success("场景已删除", null);
    }
}
