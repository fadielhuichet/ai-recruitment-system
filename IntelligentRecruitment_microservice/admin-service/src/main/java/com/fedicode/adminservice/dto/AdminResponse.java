package com.fedicode.adminservice.dto;

import com.fedicode.adminservice.Entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminResponse {
    private int id;
    private String email;
    private String phone;
    private String username;
    private LocalDateTime createdAt;

}
