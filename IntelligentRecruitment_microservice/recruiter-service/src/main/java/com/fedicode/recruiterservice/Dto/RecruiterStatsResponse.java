package com.fedicode.recruiterservice.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class RecruiterStatsResponse {
    private long total;
    private long active;
    private long suspended;
    private long thisMonth;
    private Map<String,Long> monthlyData;
}
