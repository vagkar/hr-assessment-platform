package com.hrplatform.backend.model.dto.dashboard;

import java.math.BigDecimal;

public record DashboardStatsResponse(
        long activeAssessments,
        long invitesSent,
        long completed,
        BigDecimal completionRate,
        BigDecimal avgScore
) {}
