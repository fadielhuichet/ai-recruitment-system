package com.fedicode.candidateservice.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
@Data
@Builder
public class CandidateStatsResponse {
    private long total;
    private long active;
    private long suspended;
    private long thisMonth;
    private Map<String,Long> monthlyData;
}
