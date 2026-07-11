package com.hrplatform.backend.model.dto.candidate;

import com.hrplatform.backend.model.entity.SessionStatus;

import java.time.Instant;
import java.util.List;

public record CandidateSessionView(
        Long sessionId,
        String assessmentTitle,
        Integer durationMinutes,
        SessionStatus status,
        Instant startedAt,
        List<CandidateQuestionView> questions
) {}
