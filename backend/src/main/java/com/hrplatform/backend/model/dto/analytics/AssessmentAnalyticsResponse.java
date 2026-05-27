package com.hrplatform.backend.model.dto.analytics;

import java.math.BigDecimal;
import java.util.List;

public record AssessmentAnalyticsResponse(
        long completedCount,
        long totalInvites,
        BigDecimal avgScore,
        BigDecimal medianScore,
        BigDecimal highScore,
        BigDecimal lowScore,
        Integer avgCompletionMinutes,
        int allottedMinutes,
        List<Long> scoreDistribution,
        List<AssessmentAnalyticsResponse.QuestionDifficulty> perQuestionStats
) {
    public record QuestionDifficulty(Long questionId, String questionText, BigDecimal correctRate) {}
}
