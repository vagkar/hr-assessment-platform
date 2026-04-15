package com.hrplatform.backend.model.dto.result;

import com.hrplatform.backend.model.entity.QuestionType;

public record AnswerDetail(
        Long questionId,
        String questionText,
        QuestionType questionType,
        Long selectedOptionId,
        String selectedOptionText,
        String openTextAnswer,
        Boolean isCorrect
) {}
