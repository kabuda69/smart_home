package com.example.smart_home.service;

import com.example.smart_home.dto.LogDTO;
import com.example.smart_home.entity.Log;
import com.example.smart_home.repository.LogRepository;
import com.example.smart_home.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class LogService {
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private UserRepository userRepository;
    
    public void log(Long userId, String operation, String details, String ip) {
        Log log = new Log();
        if (userId != null) {
            log.setUser(userRepository.findById(userId).orElse(null));
        }
        log.setOperation(operation);
        log.setDetails(details);
        log.setIpAddress(ip);
        logRepository.save(log);
    }
    
    public Page<LogDTO> getUserLogs(Long userId, Pageable pageable) {
        return logRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::toDTO);
    }
    
    /**
     * 按时间段查询用户日志
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 日志分页结果
     */
    public Page<LogDTO> getUserLogsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return logRepository.findByUserIdAndCreatedAtBetween(userId, startTime, endTime, pageable).map(this::toDTO);
    }
    
    public Page<LogDTO> getAllLogs(Pageable pageable) {
        return logRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toDTO);
    }
    
    /**
     * 按时间段查询所有日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 日志分页结果
     */
    public Page<LogDTO> getAllLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return logRepository.findByCreatedAtBetween(startTime, endTime, pageable).map(this::toDTO);
    }
    
    private LogDTO toDTO(Log log) {
        LogDTO dto = new LogDTO();
        dto.setId(log.getId());
        dto.setUsername(log.getUser() != null ? log.getUser().getUsername() : "系统");
        dto.setOperation(log.getOperation());
        dto.setDetails(log.getDetails());
        dto.setIpAddress(log.getIpAddress());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}
