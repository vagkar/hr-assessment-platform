-- Convert all timestamps from TIMESTAMP (naive) to TIMESTAMPTZ (timezone-aware).
-- Existing data is treated as UTC — this matches the Railway container's default timezone
-- where all production LocalDateTime.now() values were captured.
-- After this migration, timestamps serialize as ISO 8601 with a "Z" suffix and are
-- unambiguously interpretable in any client timezone.

ALTER TABLE companies
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

ALTER TABLE users
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

ALTER TABLE assessments
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

ALTER TABLE candidate_sessions
    ALTER COLUMN started_at   TYPE TIMESTAMPTZ USING started_at   AT TIME ZONE 'UTC',
    ALTER COLUMN completed_at TYPE TIMESTAMPTZ USING completed_at AT TIME ZONE 'UTC';
