package com.hrplatform.backend.model.dto.assessment;

public record AssessmentStatsRow(
        Long assessmentId,
        Long inviteCount,
        Long completedCount,
        Double avgScore
) {}
