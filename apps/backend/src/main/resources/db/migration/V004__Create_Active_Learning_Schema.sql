-- Create active learning schema
CREATE SCHEMA IF NOT EXISTS active;

-- Create enum types
CREATE TYPE active.poll_type AS ENUM ('MULTIPLE_CHOICE', 'RATING', 'WORD_CLOUD', 'OPEN_ENDED');
CREATE TYPE active.poll_status AS ENUM ('DRAFT', 'ACTIVE', 'CLOSED');
CREATE TYPE active.question_type AS ENUM ('MULTIPLE_CHOICE', 'TRUE_FALSE', 'SHORT_ANSWER', 'ESSAY');
CREATE TYPE active.quiz_status AS ENUM ('DRAFT', 'ACTIVE', 'COMPLETED');
CREATE TYPE active.breakout_status AS ENUM ('WAITING', 'ACTIVE', 'CLOSED');
CREATE TYPE active.assignment_method AS ENUM ('RANDOM', 'MANUAL', 'BALANCED');
CREATE TYPE active.whiteboard_tool AS ENUM ('PEN', 'HIGHLIGHTER', 'ERASER', 'TEXT', 'SHAPE');
CREATE TYPE active.speaking_status AS ENUM ('WAITING', 'SPEAKING', 'FINISHED');

-- Polls table
CREATE TABLE active.polls (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES course.courses(id),
    creator_id BIGINT NOT NULL REFERENCES auth.users(id),
    question VARCHAR(500) NOT NULL,
    type active.poll_type NOT NULL,
    status active.poll_status NOT NULL DEFAULT 'DRAFT',
    allow_multiple BOOLEAN DEFAULT FALSE,
    show_results BOOLEAN DEFAULT TRUE,
    anonymous BOOLEAN DEFAULT FALSE,
    settings JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_polls_course ON active.polls(course_id);
CREATE INDEX idx_polls_creator ON active.polls(creator_id);
CREATE INDEX idx_polls_status ON active.polls(status);

-- Poll options table
CREATE TABLE active.poll_options (
    id BIGSERIAL PRIMARY KEY,
    poll_id BIGINT NOT NULL REFERENCES active.polls(id) ON DELETE CASCADE,
    text VARCHAR(500) NOT NULL,
    display_order INTEGER NOT NULL,
    is_correct BOOLEAN,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_poll_options_poll ON active.poll_options(poll_id);

-- Poll responses table
CREATE TABLE active.poll_responses (
    id BIGSERIAL PRIMARY KEY,
    poll_id BIGINT NOT NULL REFERENCES active.polls(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES auth.users(id),
    selected_option_ids JSONB DEFAULT '[]',
    text_response TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    UNIQUE(poll_id, user_id)
);

CREATE INDEX idx_poll_responses_poll ON active.poll_responses(poll_id);
CREATE INDEX idx_poll_responses_user ON active.poll_responses(user_id);

-- Poll sessions table
CREATE TABLE active.poll_sessions (
    id BIGSERIAL PRIMARY KEY,
    poll_id BIGINT NOT NULL REFERENCES active.polls(id),
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id),
    started_at TIMESTAMPTZ,
    ended_at TIMESTAMPTZ,
    response_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_poll_sessions_room ON active.poll_sessions(room_id);

-- Question tags table
CREATE TABLE active.question_tags (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES course.courses(id),
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    UNIQUE(course_id, name)
);

-- Questions table
CREATE TABLE active.questions (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES course.courses(id),
    creator_id BIGINT NOT NULL REFERENCES auth.users(id),
    type active.question_type NOT NULL,
    question_text TEXT NOT NULL,
    options JSONB DEFAULT '[]',
    correct_answers JSONB DEFAULT '[]',
    explanation TEXT,
    points INTEGER NOT NULL DEFAULT 1,
    time_limit_seconds INTEGER,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_questions_course ON active.questions(course_id);
CREATE INDEX idx_questions_type ON active.questions(type);

-- Question tags mapping
CREATE TABLE active.question_tags_mapping (
    question_id BIGINT NOT NULL REFERENCES active.questions(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES active.question_tags(id) ON DELETE CASCADE,
    PRIMARY KEY (question_id, tag_id)
);

-- Quizzes table
CREATE TABLE active.quizzes (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES course.courses(id),
    creator_id BIGINT NOT NULL REFERENCES auth.users(id),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status active.quiz_status NOT NULL DEFAULT 'DRAFT',
    time_limit_minutes INTEGER,
    passing_score INTEGER,
    shuffle_questions BOOLEAN DEFAULT FALSE,
    show_correct_answers BOOLEAN DEFAULT TRUE,
    question_ids JSONB NOT NULL DEFAULT '[]',
    settings JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_quizzes_course ON active.quizzes(course_id);
CREATE INDEX idx_quizzes_status ON active.quizzes(status);

-- Quiz sessions table
CREATE TABLE active.quiz_sessions (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL REFERENCES active.quizzes(id),
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id),
    started_at TIMESTAMPTZ,
    ends_at TIMESTAMPTZ,
    submission_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_quiz_sessions_room ON active.quiz_sessions(room_id);

-- Quiz answers table
CREATE TABLE active.quiz_answers (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES active.quiz_sessions(id),
    user_id BIGINT NOT NULL REFERENCES auth.users(id),
    answers JSONB NOT NULL,
    score INTEGER,
    max_score INTEGER,
    auto_graded BOOLEAN DEFAULT FALSE,
    submitted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    UNIQUE(session_id, user_id)
);

CREATE INDEX idx_quiz_answers_session ON active.quiz_answers(session_id);

-- Breakout rooms table
CREATE TABLE active.breakout_rooms (
    id BIGSERIAL PRIMARY KEY,
    seminar_room_id BIGINT NOT NULL REFERENCES seminar.rooms(id),
    name VARCHAR(100) NOT NULL,
    status active.breakout_status NOT NULL DEFAULT 'WAITING',
    assignment_method active.assignment_method NOT NULL,
    max_participants INTEGER,
    duration_minutes INTEGER,
    started_at TIMESTAMPTZ,
    ends_at TIMESTAMPTZ,
    meeting_url VARCHAR(500),
    settings JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_breakout_rooms_seminar ON active.breakout_rooms(seminar_room_id);
CREATE INDEX idx_breakout_rooms_status ON active.breakout_rooms(status);

-- Breakout participants table
CREATE TABLE active.breakout_participants (
    id BIGSERIAL PRIMARY KEY,
    breakout_room_id BIGINT NOT NULL REFERENCES active.breakout_rooms(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES auth.users(id),
    joined_at TIMESTAMPTZ,
    left_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    UNIQUE(breakout_room_id, user_id)
);

CREATE INDEX idx_breakout_participants_room ON active.breakout_participants(breakout_room_id);

-- Whiteboards table
CREATE TABLE active.whiteboards (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id),
    name VARCHAR(200) NOT NULL,
    canvas_settings JSONB DEFAULT '{"width": 1920, "height": 1080, "backgroundColor": "#FFFFFF"}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_whiteboards_room ON active.whiteboards(room_id);

-- Whiteboard elements table
CREATE TABLE active.whiteboard_elements (
    id BIGSERIAL PRIMARY KEY,
    whiteboard_id BIGINT NOT NULL REFERENCES active.whiteboards(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES auth.users(id),
    tool active.whiteboard_tool NOT NULL,
    data JSONB NOT NULL,
    z_index INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_whiteboard_elements_whiteboard ON active.whiteboard_elements(whiteboard_id);

-- Speaking queue table
CREATE TABLE active.speaking_queue (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id),
    user_id BIGINT NOT NULL REFERENCES auth.users(id),
    status active.speaking_status NOT NULL DEFAULT 'WAITING',
    queue_position INTEGER,
    granted_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ,
    speaking_duration_seconds INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_speaking_queue_room ON active.speaking_queue(room_id);
CREATE INDEX idx_speaking_queue_status ON active.speaking_queue(status);

-- Discussion threads table
CREATE TABLE active.discussion_threads (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id),
    user_id BIGINT NOT NULL REFERENCES auth.users(id),
    title VARCHAR(200) NOT NULL,
    content TEXT,
    is_resolved BOOLEAN DEFAULT FALSE,
    upvote_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_discussion_threads_room ON active.discussion_threads(room_id);
CREATE INDEX idx_discussion_threads_resolved ON active.discussion_threads(is_resolved);
