package com.fedicode.authenticationservice.Dto;

import com.fedicode.authenticationservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String firstName;
    private String username;
    private String lastName;
    private String companyName;
    private String phone;
    private Role role;
    private String profileImage;
    private String message;
}