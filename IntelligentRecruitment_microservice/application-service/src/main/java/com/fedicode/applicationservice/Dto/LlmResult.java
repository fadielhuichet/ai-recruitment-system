package com.fedicode.applicationservice.Dto;

import java.math.BigDecimal;
import java.util.List;

public record LlmResult(
        BigDecimal score,
        String verdict,
        String recommendation,
        List<String> strengths,
        List<String> weaknesses,
        List<String> missing_requirements,
        List<String> risk_factors
) {}