package com.example.smart_home.dto;

import lombok.Data;
import java.util.List;

@Data
public class SceneDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private List<SceneActionDTO> actions;
}
