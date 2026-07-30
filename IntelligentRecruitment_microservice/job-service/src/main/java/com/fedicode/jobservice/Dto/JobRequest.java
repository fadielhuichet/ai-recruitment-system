package com.fedicode.jobservice.Dto;

import com.fedicode.jobservice.Entity.JobCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobRequest {
    @NotBlank
    String title;
    @NotBlank
    String description;
    @NotBlank
    String company;
    @NotBlank
    String location;
    @NotNull
    JobCategory category;

    String customCategory;




}
