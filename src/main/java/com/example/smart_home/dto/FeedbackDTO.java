package com.example.smart_home.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackDTO {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String content;
    private String status;
    private String adminReply;
    private LocalDateTime createdAt;
}
