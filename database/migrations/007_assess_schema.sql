-- ============================================
-- MIGRATION 007: Assessment Schema
-- ============================================
-- Description: Creates assessment and grading schema and tables
-- Author: System
-- Date: 2025-01-28

-- Create schema
CREATE SCHEMA IF NOT EXISTS assess;

-- Grades table
CREATE TABLE assess.grades (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    participation_score DECIMAL(5,2) DEFAULT 0,
    quiz_average    DECIMAL(5,2) DEFAULT 0,
    assignment_average DECIMAL(5,2) DEFAULT 0,
    final_score     DECIMAL(5,2) DEFAULT 0,
    letter_grade    VARCHAR(2),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT grades_user_course_unique UNIQUE (user_id, course_id)
);

CREATE INDEX idx_grades_course_id ON assess.grades(course_id);
CREATE INDEX idx_grades_user_id ON assess.grades(user_id);

CREATE TRIGGER update_grades_updated_at
    BEFORE UPDATE ON assess.grades
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- AI grading results table
CREATE TABLE assess.ai_gradings (
    id              BIGSERIAL PRIMARY KEY,
    submission_id   BIGINT REFERENCES course.submissions(id) ON DELETE CASCADE,
    quiz_answer_id  BIGINT REFERENCES learning.quiz_answers(id) ON DELETE CASCADE,
    ai_score        DECIMAL(5,2) NOT NULL,
    confidence      DECIMAL(3,2) NOT NULL,
    feedback        JSONB NOT NULL DEFAULT '{}',
    keywords_found  JSONB DEFAULT '[]',
    similarity_score DECIMAL(3,2),
    model_version   VARCHAR(50) NOT NULL,
    reviewed_by     BIGINT REFERENCES auth.users(id),
    reviewed_at     TIMESTAMPTZ,
    final_score     DECIMAL(5,2),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT ai_gradings_submission_or_answer CHECK (
        (submission_id IS NOT NULL AND quiz_answer_id IS NULL) OR
        (submission_id IS NULL AND quiz_answer_id IS NOT NULL)
    )
);

CREATE INDEX idx_ai_gradings_submission_id ON assess.ai_gradings(submission_id);
CREATE INDEX idx_ai_gradings_quiz_answer_id ON assess.ai_gradings(quiz_answer_id);

-- Peer evaluations table
CREATE TABLE assess.peer_evaluations (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    evaluator_id    BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    evaluatee_id    BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    assignment_id   BIGINT REFERENCES course.assignments(id) ON DELETE SET NULL,
    criteria        JSONB NOT NULL,
    scores          JSONB NOT NULL,
    comments        TEXT,
    is_anonymous    BOOLEAN NOT NULL DEFAULT TRUE,
    submitted_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT peer_evaluations_self_check CHECK (evaluator_id != evaluatee_id)
);

CREATE INDEX idx_peer_evaluations_course_id ON assess.peer_evaluations(course_id);
CREATE INDEX idx_peer_evaluations_evaluatee_id ON assess.peer_evaluations(evaluatee_id);

-- Code executions table
CREATE TABLE assess.code_executions (
    id              BIGSERIAL PRIMARY KEY,
    quiz_answer_id  BIGINT NOT NULL REFERENCES learning.quiz_answers(id) ON DELETE CASCADE,
    language        VARCHAR(50) NOT NULL,
    source_code     TEXT NOT NULL,
    test_cases      JSONB NOT NULL,
    passed_count    INTEGER NOT NULL DEFAULT 0,
    total_count     INTEGER NOT NULL DEFAULT 0,
    execution_time_ms INTEGER,
    memory_used_kb  INTEGER,
    stdout          TEXT,
    stderr          TEXT,
    status          VARCHAR(20) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_code_executions_quiz_answer_id ON assess.code_executions(quiz_answer_id);

-- Comments
COMMENT ON SCHEMA assess IS 'Assessment and grading schema';
COMMENT ON TABLE assess.grades IS 'Student final grades';
COMMENT ON TABLE assess.ai_gradings IS 'AI-powered grading results';
COMMENT ON TABLE assess.peer_evaluations IS 'Peer evaluation results';
COMMENT ON TABLE assess.code_executions IS 'Code execution results for coding problems';
