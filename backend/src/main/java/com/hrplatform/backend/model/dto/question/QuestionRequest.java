package com.hrplatform.backend.model.dto.question;

import com.hrplatform.backend.model.entity.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuestionRequest(
        @NotBlank String text,
        @NotNull QuestionType type,
        @NotNull Integer orderIndex,
        @Valid List<QuestionOptionRequest> options
) {}
