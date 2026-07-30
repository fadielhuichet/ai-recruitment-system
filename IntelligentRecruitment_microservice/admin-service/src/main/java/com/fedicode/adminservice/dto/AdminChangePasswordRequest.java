package com.fedicode.adminservice.dto;

import lombok.Data;

@Data
public class AdminChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
}

