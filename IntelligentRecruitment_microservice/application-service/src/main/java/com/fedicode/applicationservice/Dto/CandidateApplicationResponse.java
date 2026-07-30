package com.fedicode.applicationservice.Dto;

import com.fedicode.applicationservice.Entity.Application;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateApplicationResponse {

    // Application
    private Integer applicationId;
    private Application.ApplicationStatus status;
    private LocalDateTime appliedAt;

    // Job
    private Integer jobId;
    private String jobTitle;
    private String company;

    // Optional
    private String recruiterProfileImage;
    private String location;
    private String employmentType;
}