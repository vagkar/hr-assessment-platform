package com.hrplatform.backend.model.dto.result;

import com.hrplatform.backend.model.entity.SessionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record SessionResultSummary(
        Long sessionId,
        String candidateName,
        String candidateEmail,
        SessionStatus status,
        BigDecimal score,
        Instant startedAt,
        Instant completedAt
) {}
