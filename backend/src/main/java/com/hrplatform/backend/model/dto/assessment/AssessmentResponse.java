package com.hrplatform.backend.model.dto.assessment;

import java.time.LocalDateTime;

public record AssessmentResponse(
        Long id,
        Long companyId,
        String title,
        String description,
        Integer durationMinutes,
        Boolean isActive,
        LocalDateTime createdAt
) {}
