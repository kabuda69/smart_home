package com.example.smart_home.service;

import com.example.smart_home.dto.*;
import com.example.smart_home.entity.NotificationPreference;
import com.example.smart_home.entity.User;
import com.example.smart_home.repository.NotificationPreferenceRepository;
import com.example.smart_home.repository.UserRepository;
import com.example.smart_home.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationPreferenceRepository notificationPrefRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private LogService logService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());  // 明文密码
        user.setEmail(request.getEmail());
        user.setRole("USER");
        userRepository.save(user);
        
        // 创建默认通知偏好
        for (String type : Arrays.asList("POPUP", "LOG")) {
            NotificationPreference pref = new NotificationPreference();
            pref.setUser(user);
            pref.setNotificationType(type);
            pref.setEnabled(true);
            notificationPrefRepository.save(pref);
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 记录注册日志
        logService.log(user.getId(), "USER_REGISTER", "用户注册", null);
        
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (!user.getEnabled()) {
            throw new RuntimeException("账户已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 记录登录日志
        logService.log(user.getId(), "USER_LOGIN", "用户登录", null);
        
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
    
    @Transactional
    public void updateUserStatus(Long userId, Boolean enabled) {
        User user = getUserById(userId);
        user.setEnabled(enabled);
        userRepository.save(user);
    }
    
    public List<NotificationPreferenceDTO> getNotificationPreferences(Long userId) {
        return notificationPrefRepository.findByUserId(userId).stream()
                .map(this::toPrefDTO)
                .collect(Collectors.toList());
    }
    
    private NotificationPreferenceDTO toPrefDTO(NotificationPreference pref) {
        NotificationPreferenceDTO dto = new NotificationPreferenceDTO();
        dto.setId(pref.getId());
        dto.setNotificationType(pref.getNotificationType());
        dto.setEnabled(pref.getEnabled());
        return dto;
    }
    
    @Transactional
    public void updateNotificationPreference(Long userId, String type, Boolean enabled) {
        NotificationPreference pref = notificationPrefRepository.findByUserIdAndNotificationType(userId, type)
                .orElseGet(() -> {
                    NotificationPreference newPref = new NotificationPreference();
                    newPref.setUser(getUserById(userId));
                    newPref.setNotificationType(type);
                    return newPref;
                });
        pref.setEnabled(enabled);
        notificationPrefRepository.save(pref);
    }
}
