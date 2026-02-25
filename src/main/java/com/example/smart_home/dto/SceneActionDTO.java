package com.example.smart_home.dto;

import lombok.Data;

@Data
public class SceneActionDTO {
    private Long id;
    private Long deviceId;
    private String deviceName;
    private String actionType;
    private String actionValue;
    private Integer sortOrder;
}
