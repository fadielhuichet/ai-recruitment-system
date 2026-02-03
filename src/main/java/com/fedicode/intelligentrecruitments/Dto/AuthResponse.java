package com.fedicode.intelligentrecruitments.Dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String companyName;
    private String message;
}