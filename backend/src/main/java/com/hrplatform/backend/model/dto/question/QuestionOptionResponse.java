package com.hrplatform.backend.model.dto.question;

public record QuestionOptionResponse(
        Long id,
        String text,
        Boolean isCorrect
) {}
