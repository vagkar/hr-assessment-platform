package com.hrplatform.backend.model.dto.candidate;

import com.hrplatform.backend.model.entity.SessionStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CandidateSessionView(
        Long sessionId,
        String assessmentTitle,
        Integer durationMinutes,
        SessionStatus status,
        LocalDateTime startedAt,
        List<CandidateQuestionView> questions
) {}
