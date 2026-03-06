package com.example.smart_home.service;

import com.example.smart_home.dto.CommandRequest;
import com.example.smart_home.dto.DeviceDTO;
import com.example.smart_home.entity.*;
import com.example.smart_home.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备服务
 * 处理设备相关的业务逻辑，包括设备管理、命令执行、状态监控等
 */
@Service
public class DeviceService {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private DeviceTypeRepository deviceTypeRepository;
    
    @Autowired
    private CommandRepository commandRepository;
    
    @Autowired
    private StatusHistoryRepository statusHistoryRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationPreferenceRepository notificationPrefRepository;
    
    @Autowired
    private SceneActionRepository sceneActionRepository;
    
    @Autowired
    private SharedSnapshotRepository sharedSnapshotRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private LogService logService;
    
    /**
     * 获取用户的所有设备列表
     * @param userId 用户ID
     * @return 设备DTO列表
     */
    public List<DeviceDTO> getUserDevices(Long userId) {
        return deviceRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取指定设备详情
     * @param deviceId 设备ID
     * @return 设备DTO
     */
    public DeviceDTO getDevice(Long deviceId) {
        return toDTO(deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在")));
    }
    
    /**
     * 获取指定设备详情并验证用户权限
     * @param deviceId 设备ID
     * @param userId 用户ID
     * @return 设备DTO
     */
    public DeviceDTO getDeviceWithAuth(Long deviceId, Long userId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        if (!device.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权访问此设备");
        }
        return toDTO(device);
    }
    
    /**
     * 更新设备信息
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param dto 设备更新信息
     * @return 更新后的设备DTO
     */
    @Transactional
    public DeviceDTO updateDevice(Long userId, Long deviceId, DeviceDTO dto) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        
        if (!device.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权修改此设备");
        }
        
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            device.setName(dto.getName());
        }
        if (dto.getThresholdMin() != null) {
            device.setThresholdMin(dto.getThresholdMin());
        }
        if (dto.getThresholdMax() != null) {
            device.setThresholdMax(dto.getThresholdMax());
        }
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);
        
        logService.log(userId, "DEVICE_UPDATE", "更新设备: " + device.getName(), null);
        
        return toDTO(device);
    }
    
    /**
     * 绑定新设备到用户
     * @param userId 用户ID
     * @param dto 设备信息
     * @return 绑定后的设备DTO
     */
    @Transactional
    public DeviceDTO bindDevice(Long userId, DeviceDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        DeviceType deviceType = deviceTypeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new RuntimeException("设备类型不存在"));
        
        Device device = new Device();
        device.setName(dto.getName());
        device.setUser(user);
        device.setDeviceType(deviceType);
        device.setStatus("offline");
        device.setPowerState(false);
        device.setCurrentValue(0.0);
        device.setThresholdMin(dto.getThresholdMin());
        device.setThresholdMax(dto.getThresholdMax());
        device.setParams(dto.getParams());
        deviceRepository.save(device);
        
        logService.log(userId, "DEVICE_BINDIND", "绑定设备: " + device.getName(), null);
        
        return toDTO(device);
    }
    
    /**
     * 执行设备控制命令
     * @param userId 用户ID
     * @param request 命令请求
     * @return 执行命令后的设备DTO
     */
    @Transactional
    public DeviceDTO executeCommand(Long userId, CommandRequest request) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        
        // 验证设备归属
        if (!device.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此设备");
        }
        
        User user = device.getUser();
        
        Command command = new Command();
        command.setDevice(device);
        command.setUser(user);
        command.setCommandType(request.getCommandType());
        command.setCommandValue(request.getCommandValue());
        command.setStatus("SUCCESS");
        
        String logDetail = "";
        
        switch (request.getCommandType()) {
            case "POWER_ON":
                device.setPowerState(true);
                device.setStatus("online");
                logDetail = "开启设备";
                break;
            case "POWER_OFF":
                device.setPowerState(false);
                // 关闭电源但设备仍在线（可以远程控制）
                logDetail = "关闭设备";
                break;
            case "SET_VALUE":
                if (!device.getPowerState()) {
                    throw new RuntimeException("请先开启设备");
                }
                Double value = Double.parseDouble(request.getCommandValue());
                device.setCurrentValue(value);
                logDetail = "设置数值: " + value;
                // 检查阈值
                checkThresholdAndAlert(device, value, user);
                break;
            case "SET_THRESHOLD":
                String[] parts = request.getCommandValue().split(",");
                double min = Double.parseDouble(parts[0]);
                double max = Double.parseDouble(parts[1]);
                if (min >= max) {
                    throw new RuntimeException("最小阈值必须小于最大阈值");
                }
                device.setThresholdMin(min);
                device.setThresholdMax(max);
                logDetail = "设置阈值: " + min + " - " + max;
                break;
            default:
                throw new RuntimeException("未知指令类型");
        }
        
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);
        commandRepository.save(command);
        
        // 记录状态历史
        StatusHistory history = new StatusHistory();
        history.setDevice(device);
        history.setStatusValue(device.getCurrentValue());
        history.setPowerState(device.getPowerState());
        statusHistoryRepository.save(history);
        
        // 记录操作日志
        logService.log(userId, "DEVICE_COMMAND", device.getName() + " - " + logDetail, null);
        
        // WebSocket推送设备状态更新
        DeviceDTO dto = toDTO(device);
        messagingTemplate.convertAndSend("/topic/device/" + device.getId(), dto);
        messagingTemplate.convertAndSend("/topic/devices/" + userId, dto);
        
        return dto;
    }
    
    /**
     * 核心阈值判断逻辑
     * 检查设备数值是否超出阈值范围，如果超出则创建警报并通知用户
     * @param device 设备
     * @param value 当前数值
     * @param user 用户
     */
    private void checkThresholdAndAlert(Device device, Double value, User user) {
        boolean exceeded = false;
        String message = "";
        Double threshold = null;
        
        if (device.getThresholdMax() != null && value > device.getThresholdMax()) {
            exceeded = true;
            threshold = device.getThresholdMax();
            message = device.getName() + " 数值(" + value + ")超过最大阈值(" + device.getThresholdMax() + ")";
        } else if (device.getThresholdMin() != null && value < device.getThresholdMin()) {
            exceeded = true;
            threshold = device.getThresholdMin();
            message = device.getName() + " 数值(" + value + ")低于最小阈值(" + device.getThresholdMin() + ")";
        }
        // 超限后的动作：创建警报、推送通知、记录日志
        if (exceeded) {
            // 创建警报
            Alert alert = new Alert();
            alert.setDevice(device);
            alert.setUser(user);
            alert.setThresholdValue(threshold);
            alert.setActualValue(value);
            alert.setAlertType("THRESHOLD_EXCEEDED");
            alert.setMessage(message);
            alert.setIsRead(false);
            alertRepository.save(alert);
            
            // 检查用户通知偏好
            boolean sendPopup = notificationPrefRepository
                    .findByUserIdAndNotificationType(user.getId(), "POPUP")
                    .map(NotificationPreference::getEnabled)
                    .orElse(true);
            
            if (sendPopup) {
                // WebSocket推送警报
                Map<String, Object> alertMsg = new HashMap<>();
                alertMsg.put("id", alert.getId());
                alertMsg.put("type", "THRESHOLD_EXCEEDED");
                alertMsg.put("message", message);
                alertMsg.put("deviceId", device.getId());
                alertMsg.put("deviceName", device.getName());
                alertMsg.put("time", LocalDateTime.now().toString());
                messagingTemplate.convertAndSend("/topic/alerts/" + user.getId(), alertMsg);
            }
            
            // 记录日志
            boolean logEnabled = notificationPrefRepository
                    .findByUserIdAndNotificationType(user.getId(), "LOG")
                    .map(NotificationPreference::getEnabled)
                    .orElse(true);
            if (logEnabled) {
                logService.log(user.getId(), "ALERT_TRIGGERED", message, null);
            }
        }
    }
    
    /**
     * 删除设备
     * @param deviceId 设备ID
     * @param userId 用户ID
     */
    @Transactional
    public void deleteDevice(Long deviceId, Long userId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        
        // 验证设备归属
        if (!device.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权删除此设备");
        }
        
        String deviceName = device.getName();
        
        // 删除关联的场景动作
        sceneActionRepository.deleteByDeviceId(deviceId);
        // 删除关联的分享快照
        sharedSnapshotRepository.deleteByDeviceId(deviceId);
        // 删除关联的历史记录
        statusHistoryRepository.deleteByDeviceId(deviceId);
        // 删除关联的命令记录
        commandRepository.deleteByDeviceId(deviceId);
        // 删除关联的警报
        alertRepository.deleteByDeviceId(deviceId);
        // 删除设备
        deviceRepository.delete(device);
        
        logService.log(userId, "DEVICE_DELETE", "删除设备: " + deviceName, null);
    }
    
    /**
     * 将Device实体转换为DeviceDTO
     * @param device 设备实体
     * @return 设备DTO
     */
    private DeviceDTO toDTO(Device device) {
        DeviceDTO dto = new DeviceDTO();
        dto.setId(device.getId());
        dto.setName(device.getName());
        dto.setTypeId(device.getDeviceType().getId());
        dto.setTypeName(device.getDeviceType().getName());
        dto.setStatus(device.getStatus());
        dto.setPowerState(device.getPowerState());
        dto.setCurrentValue(device.getCurrentValue());
        dto.setThresholdMin(device.getThresholdMin());
        dto.setThresholdMax(device.getThresholdMax());
        dto.setParams(device.getParams());
        return dto;
    }
}
