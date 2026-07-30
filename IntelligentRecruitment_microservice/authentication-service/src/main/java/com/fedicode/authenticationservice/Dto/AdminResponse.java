package com.fedicode.authenticationservice.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminResponse {
    private int id;
    private String email;
    private String password;
    private String phone;
    private String username;
    private LocalDateTime createdAt;

}
