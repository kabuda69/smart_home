package com.example.smart_home.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String role;
    private String email;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
