package com.hrplatform.backend.model.dto.question;

import com.hrplatform.backend.model.entity.QuestionType;

import java.util.List;

public record QuestionResponse(
        Long id,
        Long assessmentId,
        String text,
        QuestionType type,
        Integer orderIndex,
        List<QuestionOptionResponse> options
) {}
