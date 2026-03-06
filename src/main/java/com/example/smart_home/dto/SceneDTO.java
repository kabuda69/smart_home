package com.example.smart_home.dto;

import lombok.Data;
import java.util.List;

/**
 * 场景DTO
 * 用于场景相关的前后端数据传输
 */
@Data
public class SceneDTO {
    /**
     * 场景ID
     */
    private Long id;
    
    /**
     * 场景名称
     */
    private String name;
    
    /**
     * 场景描述
     */
    private String description;
    
    /**
     * 是否激活
     */
    private Boolean isActive;
    
    /**
     * 场景动作列表
     */
    private List<SceneActionDTO> actions;
}
