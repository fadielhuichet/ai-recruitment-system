package com.fedicode.adminservice.dto;

import lombok.Data;

@Data
public class AdminChangeInfoRequest {
    private String username;
    private String email;
    private String phone;
}

