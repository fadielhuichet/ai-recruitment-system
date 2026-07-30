package com.fedicode.applicationservice.Dto;

import com.fedicode.applicationservice.Entity.Application;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {
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
}
