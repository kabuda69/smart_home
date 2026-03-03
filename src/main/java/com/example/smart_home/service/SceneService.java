package com.example.smart_home.service;

import com.example.smart_home.dto.CommandRequest;
import com.example.smart_home.dto.SceneActionDTO;
import com.example.smart_home.dto.SceneDTO;
import com.example.smart_home.entity.*;
import com.example.smart_home.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SceneService {
    @Autowired
    private SceneRepository sceneRepository;
    @Autowired
    private SceneActionRepository sceneActionRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private LogService logService;
    
    public List<SceneDTO> getUserScenes(Long userId) {
        return sceneRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public SceneDTO getScene(Long sceneId, Long userId) {
        Scene scene = sceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("场景不存在"));
        if (!scene.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权访问此场景");
        }
        return toDTO(scene);
    }
    
    public SceneDTO getScene(Long sceneId) {
        return toDTO(sceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("场景不存在")));
    }
    //
    @Transactional
    public SceneDTO createScene(Long userId, SceneDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Scene scene = new Scene();
        scene.setUser(user);
        scene.setName(dto.getName());
        scene.setDescription(dto.getDescription());
        scene.setIsActive(false);
        sceneRepository.save(scene);
        
        // 保存场景动作
        if (dto.getActions() != null && !dto.getActions().isEmpty()) {
            int order = 0;
            for (SceneActionDTO actionDTO : dto.getActions()) {
                Device device = deviceRepository.findById(actionDTO.getDeviceId())
                        .orElseThrow(() -> new RuntimeException("设备不存在: " + actionDTO.getDeviceId()));
                
                // 验证设备归属
                if (!device.getUser().getId().equals(userId)) {
                    throw new RuntimeException("设备不属于当前用户");
                }
                
                SceneAction action = new SceneAction();
                action.setScene(scene);
                action.setDevice(device);
                action.setActionType(actionDTO.getActionType());
                action.setActionValue(actionDTO.getActionValue());
                action.setSortOrder(order++);
                sceneActionRepository.save(action);
            }
        }
        
        logService.log(userId, "SCENE_CREATE", "创建场景: " + scene.getName(), null);
        
        return toDTO(sceneRepository.findById(scene.getId()).get());
    }
    
    @Transactional
    public SceneDTO updateScene(Long userId, Long sceneId, SceneDTO dto) {
        Scene scene = sceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("场景不存在"));
        
        if (!scene.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权修改此场景");
        }
        
        scene.setName(dto.getName());
        scene.setDescription(dto.getDescription());
        sceneRepository.save(scene);
        
        // 删除旧动作，添加新动作
        sceneActionRepository.deleteBySceneId(sceneId);
        
        if (dto.getActions() != null && !dto.getActions().isEmpty()) {
            int order = 0;
            for (SceneActionDTO actionDTO : dto.getActions()) {
                Device device = deviceRepository.findById(actionDTO.getDeviceId())
                        .orElseThrow(() -> new RuntimeException("设备不存在"));
                
                SceneAction action = new SceneAction();
                action.setScene(scene);
                action.setDevice(device);
                action.setActionType(actionDTO.getActionType());
                action.setActionValue(actionDTO.getActionValue());
                action.setSortOrder(order++);
                sceneActionRepository.save(action);
            }
        }
        
        logService.log(userId, "SCENE_UPDATE", "更新场景: " + scene.getName(), null);
        
        return toDTO(scene);
    }
    
    @Transactional
    public void activateScene(Long userId, Long sceneId) {
        Scene scene = sceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("场景不存在"));
        
        if (!scene.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权激活此场景");
        }
        
        // 先停用当前激活的场景（前置逻辑）
        sceneRepository.findByUserIdAndIsActiveTrue(userId).ifPresent(s -> {
            s.setIsActive(false);
            sceneRepository.save(s);
        });
        
        // 激活新场景（状态标记）
        scene.setIsActive(true);
        sceneRepository.save(scene);
        
        // 执批量命令执行开始
        // 1. 获取该场景下的所有动作（按执行顺序排序）
        List<SceneAction> actions = sceneActionRepository.findBySceneIdOrderBySortOrder(sceneId);
        // 2. 批量遍历执行（核心循环：逐个执行每个设备命令）
        for (SceneAction action : actions) {
            try {
                // 3. 构建设备命令请求（复用DeviceService的命令格式）
                CommandRequest cmd = new CommandRequest();
                cmd.setDeviceId(action.getDevice().getId());
                cmd.setCommandType(action.getActionType());
                cmd.setCommandValue(action.getActionValue());
                // 4. 调用DeviceService执行单个命令（批量的核心：循环调用=批量）
                deviceService.executeCommand(userId, cmd);
            } catch (Exception e) {
                // 5.记录错误但继续执行其他动作
                logService.log(userId, "SCENE_ACTION_ERROR", 
                        "场景动作执行失败: " + action.getDevice().getName() + " - " + e.getMessage(), null);
            }
        }
        //批量命令执行结束
        logService.log(userId, "SCENE_ACTIVATE", "激活场景: " + scene.getName(), null);
    }
    
    @Transactional
    public void deactivateScene(Long userId, Long sceneId) {
        Scene scene = sceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("场景不存在"));
        
        if (!scene.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此场景");
        }
        
        scene.setIsActive(false);
        sceneRepository.save(scene);
    }
    
    @Transactional
    public void deleteScene(Long userId, Long sceneId) {
        Scene scene = sceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("场景不存在"));
        
        if (!scene.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权删除此场景");
        }
        
        String sceneName = scene.getName();
        sceneActionRepository.deleteBySceneId(sceneId);
        sceneRepository.delete(scene);
        
        logService.log(userId, "SCENE_DELETE", "删除场景: " + sceneName, null);
    }
    
    private SceneDTO toDTO(Scene scene) {
        SceneDTO dto = new SceneDTO();
        dto.setId(scene.getId());
        dto.setName(scene.getName());
        dto.setDescription(scene.getDescription());
        dto.setIsActive(scene.getIsActive());
        
        // 加载场景动作
        List<SceneAction> actions = sceneActionRepository.findBySceneIdOrderBySortOrder(scene.getId());
        if (actions != null && !actions.isEmpty()) {
            List<SceneActionDTO> actionDTOs = new ArrayList<>();
            for (SceneAction action : actions) {
                SceneActionDTO actionDTO = new SceneActionDTO();
                actionDTO.setId(action.getId());
                actionDTO.setDeviceId(action.getDevice().getId());
                actionDTO.setDeviceName(action.getDevice().getName());
                actionDTO.setActionType(action.getActionType());
                actionDTO.setActionValue(action.getActionValue());
                actionDTO.setSortOrder(action.getSortOrder());
                actionDTOs.add(actionDTO);
            }
            dto.setActions(actionDTOs);
        } else {
            dto.setActions(new ArrayList<>());
        }
        
        return dto;
    }
}
