package com.hrplatform.backend.repository;

import com.hrplatform.backend.model.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findAllByQuestionId(Long questionId);
    void deleteAllByQuestionId(Long questionId);
}
