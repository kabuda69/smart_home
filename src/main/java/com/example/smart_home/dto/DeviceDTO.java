package com.example.smart_home.dto;

import lombok.Data;

/**
 * 设备DTO
 * 用于设备相关的前后端数据传输
 */
@Data
public class DeviceDTO {
    /**
     * 设备ID
     */
    private Long id;
    
    /**
     * 设备名称
     */
    private String name;
    
    /**
     * 设备类型ID
     */
    private Long typeId;
    
    /**
     * 设备类型名称
     */
    private String typeName;
    
    /**
     * 设备状态
     */
    private String status;
    
    /**
     * 电源状态
     */
    private Boolean powerState;
    
    /**
     * 当前数值
     */
    private Double currentValue;
    
    /**
     * 最小阈值
     */
    private Double thresholdMin;
    
    /**
     * 最大阈值
     */
    private Double thresholdMax;
    
    /**
     * 设备参数
     */
    private String params;
}
