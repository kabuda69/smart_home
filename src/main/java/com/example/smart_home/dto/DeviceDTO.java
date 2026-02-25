package com.example.smart_home.dto;

import lombok.Data;

@Data
public class DeviceDTO {
    private Long id;
    private String name;
    private Long typeId;
    private String typeName;
    private String status;
    private Boolean powerState;
    private Double currentValue;
    private Double thresholdMin;
    private Double thresholdMax;
    private String params;
}
