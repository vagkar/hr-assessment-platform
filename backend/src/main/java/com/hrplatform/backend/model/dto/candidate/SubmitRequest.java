package com.hrplatform.backend.model.dto.candidate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SubmitRequest(
        @NotEmpty @Valid List<AnswerSubmission> answers
) {}
