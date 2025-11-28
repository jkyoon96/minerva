-- ============================================
-- MIGRATION 006: Active Learning Schema
-- ============================================
-- Description: Creates active learning tools schema and tables
-- Author: System
-- Date: 2025-01-28

-- Create schema
CREATE SCHEMA IF NOT EXISTS learning;

-- Polls table
CREATE TABLE learning.polls (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    created_by      BIGINT NOT NULL REFERENCES auth.users(id),
    question        TEXT NOT NULL,
    type            poll_type NOT NULL DEFAULT 'single',
    is_anonymous    BOOLEAN NOT NULL DEFAULT FALSE,
    allow_multiple  BOOLEAN NOT NULL DEFAULT FALSE,
    show_results    BOOLEAN NOT NULL DEFAULT TRUE,
    time_limit_sec  INTEGER,
    status          poll_status NOT NULL DEFAULT 'draft',
    started_at      TIMESTAMPTZ,
    ended_at        TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_polls_session_id ON learning.polls(session_id);
CREATE INDEX idx_polls_status ON learning.polls(status);

CREATE TRIGGER update_polls_updated_at
    BEFORE UPDATE ON learning.polls
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Poll options table
CREATE TABLE learning.poll_options (
    id              BIGSERIAL PRIMARY KEY,
    poll_id         BIGINT NOT NULL REFERENCES learning.polls(id) ON DELETE CASCADE,
    option_text     TEXT NOT NULL,
    order_index     INTEGER NOT NULL DEFAULT 0,
    is_correct      BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_poll_options_poll_id ON learning.poll_options(poll_id);

-- Poll votes table
CREATE TABLE learning.poll_votes (
    id              BIGSERIAL PRIMARY KEY,
    poll_option_id  BIGINT NOT NULL REFERENCES learning.poll_options(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    text_response   TEXT,
    voted_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT poll_votes_unique UNIQUE (poll_option_id, user_id)
);

CREATE INDEX idx_poll_votes_poll_option_id ON learning.poll_votes(poll_option_id);
CREATE INDEX idx_poll_votes_user_id ON learning.poll_votes(user_id);

-- Quizzes table
CREATE TABLE learning.quizzes (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT REFERENCES course.sessions(id) ON DELETE SET NULL,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    created_by      BIGINT NOT NULL REFERENCES auth.users(id),
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    time_limit_sec  INTEGER,
    shuffle_questions BOOLEAN NOT NULL DEFAULT FALSE,
    show_answers    BOOLEAN NOT NULL DEFAULT TRUE,
    passing_score   INTEGER DEFAULT 60,
    max_attempts    INTEGER DEFAULT 1,
    status          quiz_status NOT NULL DEFAULT 'draft',
    started_at      TIMESTAMPTZ,
    ended_at        TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_quizzes_course_id ON learning.quizzes(course_id);
CREATE INDEX idx_quizzes_session_id ON learning.quizzes(session_id);
CREATE INDEX idx_quizzes_status ON learning.quizzes(status);

CREATE TRIGGER update_quizzes_updated_at
    BEFORE UPDATE ON learning.quizzes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Questions table
CREATE TABLE learning.questions (
    id              BIGSERIAL PRIMARY KEY,
    quiz_id         BIGINT REFERENCES learning.quizzes(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    question_text   TEXT NOT NULL,
    type            question_type NOT NULL,
    options         JSONB,
    correct_answer  JSONB NOT NULL,
    explanation     TEXT,
    points          INTEGER NOT NULL DEFAULT 1,
    difficulty      VARCHAR(20) DEFAULT 'medium',
    tags            JSONB DEFAULT '[]',
    order_index     INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_questions_quiz_id ON learning.questions(quiz_id);
CREATE INDEX idx_questions_course_id ON learning.questions(course_id);
CREATE INDEX idx_questions_type ON learning.questions(type);
CREATE INDEX idx_questions_tags ON learning.questions USING GIN(tags);

CREATE TRIGGER update_questions_updated_at
    BEFORE UPDATE ON learning.questions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Quiz attempts table
CREATE TABLE learning.quiz_attempts (
    id              BIGSERIAL PRIMARY KEY,
    quiz_id         BIGINT NOT NULL REFERENCES learning.quizzes(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    started_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    submitted_at    TIMESTAMPTZ,
    score           DECIMAL(5,2),
    max_score       DECIMAL(5,2),
    status          quiz_attempt_status NOT NULL DEFAULT 'in_progress',
    attempt_number  INTEGER NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_quiz_attempts_quiz_id ON learning.quiz_attempts(quiz_id);
CREATE INDEX idx_quiz_attempts_user_id ON learning.quiz_attempts(user_id);
CREATE INDEX idx_quiz_attempts_status ON learning.quiz_attempts(status);

CREATE TRIGGER update_quiz_attempts_updated_at
    BEFORE UPDATE ON learning.quiz_attempts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Quiz answers table
CREATE TABLE learning.quiz_answers (
    id              BIGSERIAL PRIMARY KEY,
    attempt_id      BIGINT NOT NULL REFERENCES learning.quiz_attempts(id) ON DELETE CASCADE,
    question_id     BIGINT NOT NULL REFERENCES learning.questions(id) ON DELETE CASCADE,
    answer          JSONB NOT NULL,
    is_correct      BOOLEAN,
    points_earned   DECIMAL(5,2) DEFAULT 0,
    answered_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT quiz_answers_unique UNIQUE (attempt_id, question_id)
);

CREATE INDEX idx_quiz_answers_attempt_id ON learning.quiz_answers(attempt_id);
CREATE INDEX idx_quiz_answers_question_id ON learning.quiz_answers(question_id);

-- Whiteboards table
CREATE TABLE learning.whiteboards (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT REFERENCES course.sessions(id) ON DELETE CASCADE,
    breakout_room_id BIGINT REFERENCES live.breakout_rooms(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL DEFAULT 'Whiteboard',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_whiteboards_session_id ON learning.whiteboards(session_id);
CREATE INDEX idx_whiteboards_breakout_room_id ON learning.whiteboards(breakout_room_id);

-- Whiteboard elements table
CREATE TABLE learning.whiteboard_elements (
    id              BIGSERIAL PRIMARY KEY,
    whiteboard_id   BIGINT NOT NULL REFERENCES learning.whiteboards(id) ON DELETE CASCADE,
    created_by      BIGINT NOT NULL REFERENCES auth.users(id),
    type            VARCHAR(50) NOT NULL,
    data            JSONB NOT NULL,
    z_index         INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_whiteboard_elements_whiteboard_id ON learning.whiteboard_elements(whiteboard_id);
CREATE INDEX idx_whiteboard_elements_created_by ON learning.whiteboard_elements(created_by);

CREATE TRIGGER update_whiteboard_elements_updated_at
    BEFORE UPDATE ON learning.whiteboard_elements
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON SCHEMA learning IS 'Active learning tools schema';
COMMENT ON TABLE learning.polls IS 'Live polling system';
COMMENT ON TABLE learning.quizzes IS 'Quiz management';
COMMENT ON TABLE learning.whiteboards IS 'Collaborative whiteboards';
