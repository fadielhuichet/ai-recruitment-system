package com.fedicode.applicationservice.Dto;

import com.fedicode.applicationservice.Entity.Application;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class AdminApplicationResponse {
    int id;
    String firstName;
    String lastName;
    String email;
    String phone;
    BigDecimal llmScore;
    String llmAnalysis;
    String cvFilePath;
    LocalDateTime createdAt;
    Application.ApplicationStatus status;
    //Job-details
    String title;
    String description;
    String location;
    LocalDateTime jobCreatedAt;

    //Recruiter-details
    String recruiterFirstName;
    String recruiterLastName;
    String recruiterEmail;

}
