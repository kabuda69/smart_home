package com.example.smart_home.service;

import com.example.smart_home.entity.Device;
import com.example.smart_home.entity.SharedSnapshot;
import com.example.smart_home.repository.DeviceRepository;
import com.example.smart_home.repository.SharedSnapshotRepository;
import com.example.smart_home.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ShareService {
    @Autowired
    private SharedSnapshotRepository snapshotRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public String createShareLink(Long userId, Long deviceId, int expireHours) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        
        // 验证设备归属
        if (!device.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权分享此设备");
        }
        
        Map<String, Object> statusData = new HashMap<>();
        statusData.put("deviceName", device.getName());
        statusData.put("typeName", device.getDeviceType().getName());
        statusData.put("status", device.getStatus());
        statusData.put("powerState", device.getPowerState());
        statusData.put("currentValue", device.getCurrentValue());
        statusData.put("snapshotTime", LocalDateTime.now().toString());
        
        SharedSnapshot snapshot = new SharedSnapshot();
        snapshot.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在")));
        snapshot.setDevice(device);
        snapshot.setLinkUuid(UUID.randomUUID().toString());
        snapshot.setExpireTime(LocalDateTime.now().plusHours(expireHours));
        
        try {
            snapshot.setStatusData(objectMapper.writeValueAsString(statusData));
        } catch (Exception e) {
            throw new RuntimeException("序列化失败");
        }
        
        snapshotRepository.save(snapshot);
        return snapshot.getLinkUuid();
    }
    
    public Map<String, Object> getSharedSnapshot(String uuid) {
        SharedSnapshot snapshot = snapshotRepository.findByLinkUuid(uuid)
                .orElseThrow(() -> new RuntimeException("链接不存在或已过期"));
        
        if (snapshot.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("链接已过期");
        }
        
        try {
            return objectMapper.readValue(snapshot.getStatusData(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("解析失败");
        }
    }
    
    @Scheduled(cron = "0 0 * * * *") // 每小时清理过期快照
    @Transactional
    public void cleanExpiredSnapshots() {
        snapshotRepository.findByExpireTimeBefore(LocalDateTime.now())
                .forEach(s -> snapshotRepository.delete(s));
    }
}
