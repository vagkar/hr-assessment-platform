CREATE TABLE questions (
    id            BIGSERIAL   PRIMARY KEY,
    assessment_id BIGINT      NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    text          TEXT        NOT NULL,
    type          VARCHAR(30) NOT NULL CHECK (type IN ('MULTIPLE_CHOICE', 'TRUE_FALSE', 'OPEN_TEXT')),
    order_index   INT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_questions_assessment_id ON questions(assessment_id);

CREATE TABLE question_options (
    id          BIGSERIAL    PRIMARY KEY,
    question_id BIGINT       NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    text        VARCHAR(500) NOT NULL,
    is_correct  BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_question_options_question_id ON question_options(question_id);
