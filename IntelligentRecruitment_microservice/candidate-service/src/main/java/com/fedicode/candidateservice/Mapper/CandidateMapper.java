package com.fedicode.candidateservice.Mapper;

import com.fedicode.candidateservice.Dto.CandidateResponse;
import com.fedicode.candidateservice.Entity.Candidate;

public class CandidateMapper {

    public static CandidateResponse toResponse(Candidate candidate){
        return CandidateResponse.builder()
                .id(candidate.getId())
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .dateOfBirth(candidate.getDateOfBirth())
                .createdAt(candidate.getCreatedAt())
                .status(candidate.getStatus())
                .profileImage(candidate.getProfileImage())
                .country(candidate.getCountry())
                .build();
    }
}
