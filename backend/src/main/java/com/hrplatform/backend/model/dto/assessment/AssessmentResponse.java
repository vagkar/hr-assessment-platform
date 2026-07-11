package com.hrplatform.backend.model.dto.assessment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AssessmentResponse(
        Long id,
        Long companyId,
        String title,
        String description,
        Integer durationMinutes,
        Boolean isActive,
        Instant createdAt,
        long inviteCount,
        long completedCount,
        BigDecimal avgScore,
        List<BigDecimal> recentScores
) {}
