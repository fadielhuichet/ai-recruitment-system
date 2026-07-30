package com.fedicode.candidateservice.Dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    String currentPassword;
    String newPassword;
}
