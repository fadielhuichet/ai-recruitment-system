package com.fedicode.jobservice.Dto;

import com.fedicode.jobservice.Entity.JobCategory;
import com.fedicode.jobservice.Entity.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {
    private int id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime createdAt;
    private String company;
    private JobCategoryDto category;
    private String customCategory;
    private JobStatus status;
    private int recruiterId;


    private String recruiterEmail;
    private String recruiterCompany;
    private String recruiterFirstName;
    private String recruiterLastName;
    private String recruiterPhone;
}