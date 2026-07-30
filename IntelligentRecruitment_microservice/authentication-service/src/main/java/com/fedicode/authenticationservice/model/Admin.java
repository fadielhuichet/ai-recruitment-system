package com.fedicode.authenticationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {

    private int id;
    private String email;
    private String username;
    private String password;
    private Role role=Role.ADMIN;
    private LocalDateTime createdAt;
    private String phone;

}
