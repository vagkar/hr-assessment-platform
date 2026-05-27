package com.hrplatform.backend.repository;

import com.hrplatform.backend.model.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllBySessionId(Long sessionId);
    void deleteAllBySessionId(Long sessionId);
    boolean existsByQuestionId(Long questionId);

    @Query("""
        SELECT a.question.id, a.question.text, a.question.orderIndex,
               COUNT(a), SUM(CASE WHEN a.isCorrect = true THEN 1 ELSE 0 END)
        FROM Answer a
        WHERE a.question.assessment.id = :assessmentId
          AND a.question.assessment.company.id = :companyId
          AND a.isCorrect IS NOT NULL
        GROUP BY a.question.id, a.question.text, a.question.orderIndex
        ORDER BY a.question.orderIndex
    """)
    List<Object[]> findQuestionDifficultyByAssessmentIdAndCompanyId(
            @Param("assessmentId") Long assessmentId,
            @Param("companyId") Long companyId
    );
}
