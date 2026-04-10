CREATE TABLE candidate_sessions (
    id              BIGSERIAL    PRIMARY KEY,
    assessment_id   BIGINT       NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    candidate_name  VARCHAR(255),
    candidate_email VARCHAR(255),
    invite_token    VARCHAR(255) NOT NULL UNIQUE,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED')),
    started_at      TIMESTAMP,
    completed_at    TIMESTAMP,
    score           NUMERIC(5, 2)
);

CREATE INDEX idx_sessions_assessment_id ON candidate_sessions(assessment_id);
CREATE INDEX idx_sessions_invite_token  ON candidate_sessions(invite_token);

CREATE TABLE answers (
    id                 BIGSERIAL PRIMARY KEY,
    session_id         BIGINT    NOT NULL REFERENCES candidate_sessions(id) ON DELETE CASCADE,
    question_id        BIGINT    NOT NULL REFERENCES questions(id),
    selected_option_id BIGINT             REFERENCES question_options(id),
    open_text_answer   TEXT,
    is_correct         BOOLEAN
);

CREATE INDEX idx_answers_session_id ON answers(session_id);
