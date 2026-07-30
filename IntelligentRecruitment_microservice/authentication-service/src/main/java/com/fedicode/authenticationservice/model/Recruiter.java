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
public class Recruiter {
    private int id;
    private String email;
    private String companyName;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private String profileImage;
    private LocalDateTime codeExpiration;
    private String verificationCode;
    private Status status=Status.ACTIVE;
    private Role role=Role.RECRUITER;

}
