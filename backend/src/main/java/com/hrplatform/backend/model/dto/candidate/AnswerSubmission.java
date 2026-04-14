package com.hrplatform.backend.model.dto.candidate;

import jakarta.validation.constraints.NotNull;

public record AnswerSubmission(
        @NotNull Long questionId,
        Long selectedOptionId,
        String openTextAnswer
) {}
