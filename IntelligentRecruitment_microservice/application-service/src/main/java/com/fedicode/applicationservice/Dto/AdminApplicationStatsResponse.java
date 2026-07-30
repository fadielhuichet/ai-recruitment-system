package com.fedicode.applicationservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminApplicationStatsResponse {
    private long totalApplications;
    private long pendingApplications;
    private long analyzedApplications;
    private long acceptedApplications;
    private long refusedApplications;
    private long acceptedThisMonth;
    private long refusedThisMonth;
    private double averageScore;
    private Map<String,Long> monthlyData;
}