package com.example.smart_home.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StatusHistoryDTO {
    private Long id;
    private Double statusValue;
    private Boolean powerState;
    private LocalDateTime recordedAt;
}
