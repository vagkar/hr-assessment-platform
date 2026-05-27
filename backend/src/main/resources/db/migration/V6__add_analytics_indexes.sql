-- Speeds up: countByAssessmentId + status, avgScoreByAssessmentId, recent scores window query.
-- These queries always filter on assessment_id AND status, so a composite index lets
-- PostgreSQL do a direct index scan instead of an index-scan-plus-filter step.
CREATE INDEX IF NOT EXISTS idx_sessions_assessment_status
    ON candidate_sessions(assessment_id, status);

-- Speeds up the per-question difficulty aggregation in AnswerRepository.
-- We GROUP BY question_id and filter where is_correct IS NOT NULL.
CREATE INDEX IF NOT EXISTS idx_answers_question_correctness
    ON answers(question_id, is_correct);

-- Speeds up: order-by completed_at DESC in window function for recent scores.
CREATE INDEX IF NOT EXISTS idx_sessions_completed_at
    ON candidate_sessions(completed_at DESC);
