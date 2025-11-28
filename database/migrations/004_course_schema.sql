-- ============================================
-- MIGRATION 004: Course Schema
-- ============================================
-- Description: Creates course management schema and tables
-- Author: System
-- Date: 2025-01-28

-- Create schema
CREATE SCHEMA IF NOT EXISTS course;

-- Courses table
CREATE TABLE course.courses (
    id              BIGSERIAL PRIMARY KEY,
    professor_id    BIGINT NOT NULL REFERENCES auth.users(id),
    code            VARCHAR(50) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    semester        VARCHAR(20) NOT NULL,
    year            INTEGER NOT NULL,
    thumbnail_url   VARCHAR(500),
    invite_code     VARCHAR(20) UNIQUE,
    invite_expires_at TIMESTAMPTZ,
    max_students    INTEGER DEFAULT 50,
    is_published    BOOLEAN NOT NULL DEFAULT FALSE,
    settings        JSONB NOT NULL DEFAULT '{
        "grading_weights": {
            "participation": 30,
            "quiz": 30,
            "assignment": 40
        },
        "allow_late_submission": true,
        "late_penalty_percent": 10
    }',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT courses_code_year_semester_unique UNIQUE (code, year, semester)
);

CREATE INDEX idx_courses_professor_id ON course.courses(professor_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_courses_semester_year ON course.courses(year, semester) WHERE deleted_at IS NULL;
CREATE INDEX idx_courses_invite_code ON course.courses(invite_code) WHERE deleted_at IS NULL AND invite_code IS NOT NULL;

CREATE TRIGGER update_courses_updated_at
    BEFORE UPDATE ON course.courses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Enrollments table
CREATE TABLE course.enrollments (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    role            enrollment_role NOT NULL DEFAULT 'student',
    status          enrollment_status NOT NULL DEFAULT 'active',
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT enrollments_user_course_unique UNIQUE (user_id, course_id)
);

CREATE INDEX idx_enrollments_course_id ON course.enrollments(course_id);
CREATE INDEX idx_enrollments_user_id ON course.enrollments(user_id);
CREATE INDEX idx_enrollments_status ON course.enrollments(status);

-- Sessions table
CREATE TABLE course.sessions (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    scheduled_at    TIMESTAMPTZ NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 90,
    status          session_status NOT NULL DEFAULT 'scheduled',
    started_at      TIMESTAMPTZ,
    ended_at        TIMESTAMPTZ,
    meeting_url     VARCHAR(500),
    settings        JSONB NOT NULL DEFAULT '{
        "enable_waiting_room": false,
        "auto_record": true,
        "allow_chat": true,
        "allow_reactions": true
    }',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sessions_course_id ON course.sessions(course_id);
CREATE INDEX idx_sessions_scheduled_at ON course.sessions(scheduled_at);
CREATE INDEX idx_sessions_status ON course.sessions(status);

CREATE TRIGGER update_sessions_updated_at
    BEFORE UPDATE ON course.sessions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Recordings table
CREATE TABLE course.recordings (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    file_url        VARCHAR(500) NOT NULL,
    duration_seconds INTEGER,
    file_size_bytes BIGINT,
    captions_url    VARCHAR(500),
    thumbnail_url   VARCHAR(500),
    status          recording_status NOT NULL DEFAULT 'processing',
    metadata        JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_recordings_session_id ON course.recordings(session_id);

-- Contents table
CREATE TABLE course.contents (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    folder_id       BIGINT REFERENCES course.contents(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    type            content_type NOT NULL DEFAULT 'file',
    file_url        VARCHAR(500),
    file_size_bytes BIGINT,
    mime_type       VARCHAR(100),
    is_visible      BOOLEAN NOT NULL DEFAULT TRUE,
    order_index     INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contents_course_id ON course.contents(course_id);
CREATE INDEX idx_contents_folder_id ON course.contents(folder_id);

-- Assignments table
CREATE TABLE course.assignments (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    due_date        TIMESTAMPTZ NOT NULL,
    max_score       INTEGER NOT NULL DEFAULT 100,
    allow_late      BOOLEAN NOT NULL DEFAULT TRUE,
    late_penalty_percent INTEGER DEFAULT 10,
    max_attempts    INTEGER DEFAULT 1,
    attachments     JSONB DEFAULT '[]',
    status          assignment_status NOT NULL DEFAULT 'draft',
    published_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_assignments_course_id ON course.assignments(course_id);
CREATE INDEX idx_assignments_due_date ON course.assignments(due_date);
CREATE INDEX idx_assignments_status ON course.assignments(status);

CREATE TRIGGER update_assignments_updated_at
    BEFORE UPDATE ON course.assignments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Submissions table
CREATE TABLE course.submissions (
    id              BIGSERIAL PRIMARY KEY,
    assignment_id   BIGINT NOT NULL REFERENCES course.assignments(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    file_url        VARCHAR(500),
    content         TEXT,
    submitted_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    score           DECIMAL(5,2),
    feedback        TEXT,
    graded_at       TIMESTAMPTZ,
    graded_by       BIGINT REFERENCES auth.users(id),
    status          submission_status NOT NULL DEFAULT 'submitted',
    attempt_number  INTEGER NOT NULL DEFAULT 1,
    is_late         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_submissions_assignment_id ON course.submissions(assignment_id);
CREATE INDEX idx_submissions_user_id ON course.submissions(user_id);
CREATE INDEX idx_submissions_status ON course.submissions(status);

CREATE TRIGGER update_submissions_updated_at
    BEFORE UPDATE ON course.submissions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON SCHEMA course IS 'Course management schema';
COMMENT ON TABLE course.courses IS 'Course information';
COMMENT ON TABLE course.enrollments IS 'Student enrollments in courses';
COMMENT ON TABLE course.sessions IS 'Live session schedules';
