package com.fedicode.recruiterservice.Dto;

import com.fedicode.recruiterservice.Entity.Status;
import lombok.Builder;


import java.time.LocalDateTime;
@Builder
public record RecruiterResponse(
        int id,
        String email,
        String companyName,
        String firstName,
        String lastName,
        String phone,
        Status status,
        String profileImage,
        LocalDateTime createdAt) {}
