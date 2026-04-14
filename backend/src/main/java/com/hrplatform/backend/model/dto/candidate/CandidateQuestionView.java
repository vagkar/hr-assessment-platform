package com.hrplatform.backend.model.dto.candidate;

import com.hrplatform.backend.model.entity.QuestionType;

import java.util.List;

public record CandidateQuestionView(
        Long id,
        String text,
        QuestionType type,
        Integer orderIndex,
        List<CandidateOptionView> options
) {}
