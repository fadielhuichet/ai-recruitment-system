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
public class RecruiterApplicationStatsResponse {
    private long totalApplications;
    private long pendingReview;
    private long acceptedThisMonth;
    private long refusedThisMonth;
    private long applicationsThisWeek;
    private long applicationsPreviousWeek;
    private double averageScore;
    private Map<String, Long> weeklyData;
}