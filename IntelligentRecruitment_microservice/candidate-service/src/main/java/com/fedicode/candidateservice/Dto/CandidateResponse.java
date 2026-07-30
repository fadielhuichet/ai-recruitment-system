package com.fedicode.candidateservice.Dto;

import com.fedicode.candidateservice.Entity.Status;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Builder
public record CandidateResponse (
    int id,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String email,
    String phone,
    String country,
    Status status,
    String profileImage,
    String cvFilePath,
    LocalDateTime createdAt){}
