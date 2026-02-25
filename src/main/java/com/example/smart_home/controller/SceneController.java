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
    
    @GetMapping
    public ApiResponse<List<SceneDTO>> getScenes(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sceneService.getUserScenes(userId));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<SceneDTO> getScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(sceneService.getScene(id, userId));
    }
    
    @PostMapping
    public ApiResponse<SceneDTO> createScene(Authentication auth, @RequestBody SceneDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("场景创建成功", sceneService.createScene(userId, dto));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<SceneDTO> updateScene(Authentication auth, @PathVariable Long id, @RequestBody SceneDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("场景更新成功", sceneService.updateScene(userId, id, dto));
    }
    
    @PostMapping("/{id}/activate")
    public ApiResponse<Void> activateScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sceneService.activateScene(userId, id);
        return ApiResponse.success("场景已激活", null);
    }
    
    @PostMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sceneService.deactivateScene(userId, id);
        return ApiResponse.success("场景已停用", null);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteScene(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        sceneService.deleteScene(userId, id);
        return ApiResponse.success("场景已删除", null);
    }
}
