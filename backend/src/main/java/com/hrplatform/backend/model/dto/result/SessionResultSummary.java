package com.hrplatform.backend.model.dto.result;

import com.hrplatform.backend.model.entity.SessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SessionResultSummary(
        Long sessionId,
        String candidateName,
        String candidateEmail,
        SessionStatus status,
        BigDecimal score,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {}
