CREATE TABLE users (
    id            BIGSERIAL    PRIMARY KEY,
    company_id    BIGINT       NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name          VARCHAR(255),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(50)  NOT NULL DEFAULT 'ADMIN',
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_company_id ON users(company_id);
CREATE INDEX idx_users_email      ON users(email);
