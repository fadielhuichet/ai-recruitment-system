package com.fedicode.applicationservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateApplicationStatsResponse {

    private long total;
    private long accepted;
    private long refused;
    private long analyzed;
    private long pending;
}