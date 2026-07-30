package com.fedicode.authenticationservice.Dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    String email;
    String codeEmail;
    String newPassword;
}
