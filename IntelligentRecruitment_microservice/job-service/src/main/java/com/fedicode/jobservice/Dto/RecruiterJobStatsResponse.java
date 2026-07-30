package com.fedicode.jobservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterJobStatsResponse {
    private long total;
    private long active;
    private long closed;
    private long thisMonth;
}
