package com.fedicode.authenticationservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {
    private int id;
    private String firstName;

    private String lastName;
    private LocalDate dateOfBirth;

    private String email;

    private String phone;

    private String password;

    private String country;

    private LocalDateTime createdAt;
    private String profileImage;


    private String cvFilePath;

    private Role role=Role.CANDIDATE;
    private Status status=Status.ACTIVE;

    private LocalDateTime codeExpiration;
    private String verificationCode;


}
