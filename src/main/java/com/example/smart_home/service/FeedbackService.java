package com.example.smart_home.service;

import com.example.smart_home.dto.FeedbackDTO;
import com.example.smart_home.entity.Feedback;
import com.example.smart_home.repository.FeedbackRepository;
import com.example.smart_home.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;
    
    public Page<FeedbackDTO> getUserFeedbacks(Long userId, Pageable pageable) {
        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::toDTO);
    }
    
    public java.util.List<FeedbackDTO> getUserFeedbackList(Long userId) {
        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }
    
    public Page<FeedbackDTO> getAllFeedbacks(Pageable pageable) {
        return feedbackRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toDTO);
    }
    //
    @Transactional
    public FeedbackDTO createFeedback(Long userId, String title, String content) {
        Feedback feedback = new Feedback();
        feedback.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在")));
        feedback.setTitle(title);
        feedback.setContent(content);
        feedbackRepository.save(feedback);
        return toDTO(feedback);
    }
    
    @Transactional
    public FeedbackDTO replyFeedback(Long feedbackId, String reply) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("反馈不存在"));
        feedback.setAdminReply(reply);
        feedback.setStatus("PROCESSED");
        feedback.setUpdatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);
        return toDTO(feedback);
    }
    
    private FeedbackDTO toDTO(Feedback f) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(f.getId());
        dto.setUserId(f.getUser().getId());
        dto.setUsername(f.getUser().getUsername());
        dto.setTitle(f.getTitle());
        dto.setContent(f.getContent());
        dto.setStatus(f.getStatus());
        dto.setAdminReply(f.getAdminReply());
        dto.setCreatedAt(f.getCreatedAt());
        return dto;
    }
}
