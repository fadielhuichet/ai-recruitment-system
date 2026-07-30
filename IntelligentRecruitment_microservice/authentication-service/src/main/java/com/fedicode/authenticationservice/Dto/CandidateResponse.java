package com.fedicode.authenticationservice.Dto;


import com.fedicode.authenticationservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponse {
    private int id;
    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    private String cvFilePath;
    private Role role;
    private LocalDate dateOfBirth;
    private String profileImage;
    private String message;
}
