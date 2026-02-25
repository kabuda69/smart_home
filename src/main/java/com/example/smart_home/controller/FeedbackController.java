package com.example.smart_home.controller;

import com.example.smart_home.dto.ApiResponse;
import com.example.smart_home.dto.FeedbackDTO;
import com.example.smart_home.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;
    
    @GetMapping
    public ApiResponse<java.util.List<FeedbackDTO>> getMyFeedbacks(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        // 返回List而不是Page，前端更容易处理
        return ApiResponse.success(feedbackService.getUserFeedbackList(userId));
    }
    
    @PostMapping
    public ApiResponse<FeedbackDTO> createFeedback(Authentication auth, @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success("反馈提交成功", feedbackService.createFeedback(userId, body.get("title"), body.get("content")));
    }
}
