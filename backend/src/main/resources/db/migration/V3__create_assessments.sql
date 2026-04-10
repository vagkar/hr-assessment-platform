CREATE TABLE assessments (
    id               BIGSERIAL    PRIMARY KEY,
    company_id       BIGINT       NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    duration_minutes INT,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_assessments_company_id ON assessments(company_id);
