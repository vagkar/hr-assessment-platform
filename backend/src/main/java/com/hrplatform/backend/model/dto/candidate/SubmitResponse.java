package com.hrplatform.backend.model.dto.candidate;

import com.hrplatform.backend.model.entity.SessionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record SubmitResponse(
        Long sessionId,
        SessionStatus status,
        BigDecimal score,
        Instant completedAt
) {}
