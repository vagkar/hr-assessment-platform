package com.hrplatform.backend.model.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionOptionRequest(
        @NotBlank String text,
        @NotNull Boolean isCorrect
) {}
