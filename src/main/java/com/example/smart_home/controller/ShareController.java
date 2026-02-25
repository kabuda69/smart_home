package com.example.smart_home.controller;

import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/share")
public class ShareController {
    @Autowired
    private ShareService shareService;
    
    @PostMapping
    public ApiResponse<String> createShareLink(Authentication auth, @RequestBody Map<String, Object> body) {
        Long userId = (Long) auth.getPrincipal();
        Long deviceId = Long.valueOf(body.get("deviceId").toString());
        int expireHours = body.containsKey("expireHours") ? (Integer) body.get("expireHours") : 24;
        String uuid = shareService.createShareLink(userId, deviceId, expireHours);
        return ApiResponse.success("分享链接创建成功", uuid);
    }
    
    @GetMapping("/{uuid}")
    public ApiResponse<Map<String, Object>> getSharedSnapshot(@PathVariable String uuid) {
        return ApiResponse.success(shareService.getSharedSnapshot(uuid));
    }
}
