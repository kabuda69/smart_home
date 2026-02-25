package com.example.smart_home.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class CommandRequest {
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;
    
    private String commandType; // POWER_ON, POWER_OFF, SET_VALUE, SET_THRESHOLD
    private String commandValue;
}
