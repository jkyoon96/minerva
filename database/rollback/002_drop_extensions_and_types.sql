-- ============================================
-- ROLLBACK 002: Drop Extensions and Types
-- ============================================
-- Description: Drops all ENUM types and PostgreSQL extensions
-- Author: System
-- Date: 2025-01-28

-- Drop helper function
DROP FUNCTION IF EXISTS update_updated_at_column();

-- Drop ENUM types (in reverse order of dependencies)
DROP TYPE IF EXISTS reaction_type CASCADE;
DROP TYPE IF EXISTS breakout_status CASCADE;
DROP TYPE IF EXISTS recording_status CASCADE;
DROP TYPE IF EXISTS content_type CASCADE;
DROP TYPE IF EXISTS alert_severity CASCADE;
DROP TYPE IF EXISTS alert_type CASCADE;
DROP TYPE IF EXISTS quiz_attempt_status CASCADE;
DROP TYPE IF EXISTS quiz_status CASCADE;
DROP TYPE IF EXISTS question_type CASCADE;
DROP TYPE IF EXISTS poll_status CASCADE;
DROP TYPE IF EXISTS poll_type CASCADE;
DROP TYPE IF EXISTS submission_status CASCADE;
DROP TYPE IF EXISTS assignment_status CASCADE;
DROP TYPE IF EXISTS session_status CASCADE;
DROP TYPE IF EXISTS enrollment_status CASCADE;
DROP TYPE IF EXISTS enrollment_role CASCADE;
DROP TYPE IF EXISTS user_status CASCADE;

-- Drop extensions
DROP EXTENSION IF EXISTS "pg_trgm";
DROP EXTENSION IF EXISTS "pgcrypto";
DROP EXTENSION IF EXISTS "uuid-ossp";
