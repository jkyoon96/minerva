-- ============================================
-- MIGRATION 005: Live Session Schema
-- ============================================
-- Description: Creates live session schema and tables
-- Author: System
-- Date: 2025-01-28

-- Create schema
CREATE SCHEMA IF NOT EXISTS live;

-- Session participants table
CREATE TABLE live.session_participants (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    left_at         TIMESTAMPTZ,
    role            VARCHAR(20) NOT NULL DEFAULT 'participant',
    is_camera_on    BOOLEAN NOT NULL DEFAULT FALSE,
    is_mic_on       BOOLEAN NOT NULL DEFAULT FALSE,
    is_screen_sharing BOOLEAN NOT NULL DEFAULT FALSE,
    connection_quality VARCHAR(20) DEFAULT 'good',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT session_participants_unique UNIQUE (session_id, user_id, joined_at)
);

CREATE INDEX idx_session_participants_session_id ON live.session_participants(session_id);
CREATE INDEX idx_session_participants_user_id ON live.session_participants(user_id);

-- Chats table
CREATE TABLE live.chats (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    reply_to_id     BIGINT REFERENCES live.chats(id) ON DELETE SET NULL,
    message         TEXT NOT NULL,
    type            VARCHAR(20) NOT NULL DEFAULT 'text',
    file_url        VARCHAR(500),
    is_private      BOOLEAN NOT NULL DEFAULT FALSE,
    private_to_id   BIGINT REFERENCES auth.users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_chats_session_id ON live.chats(session_id);
CREATE INDEX idx_chats_user_id ON live.chats(user_id);
CREATE INDEX idx_chats_created_at ON live.chats(created_at);

-- Breakout rooms table
CREATE TABLE live.breakout_rooms (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    topic           TEXT,
    duration_minutes INTEGER DEFAULT 10,
    status          breakout_status NOT NULL DEFAULT 'pending',
    started_at      TIMESTAMPTZ,
    ended_at        TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_breakout_rooms_session_id ON live.breakout_rooms(session_id);

-- Breakout participants table
CREATE TABLE live.breakout_participants (
    id              BIGSERIAL PRIMARY KEY,
    breakout_room_id BIGINT NOT NULL REFERENCES live.breakout_rooms(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    left_at         TIMESTAMPTZ,
    assigned_by     VARCHAR(20) NOT NULL DEFAULT 'auto',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT breakout_participants_unique UNIQUE (breakout_room_id, user_id)
);

CREATE INDEX idx_breakout_participants_room_id ON live.breakout_participants(breakout_room_id);
CREATE INDEX idx_breakout_participants_user_id ON live.breakout_participants(user_id);

-- Reactions table
CREATE TABLE live.reactions (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    type            reaction_type NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reactions_session_id ON live.reactions(session_id);
CREATE INDEX idx_reactions_created_at ON live.reactions(created_at);

-- Hand raises table
CREATE TABLE live.hand_raises (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    raised_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    lowered_at      TIMESTAMPTZ,
    called_at       TIMESTAMPTZ,
    called_by       BIGINT REFERENCES auth.users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_hand_raises_session_id ON live.hand_raises(session_id);
CREATE INDEX idx_hand_raises_raised_at ON live.hand_raises(raised_at) WHERE lowered_at IS NULL;

-- Comments
COMMENT ON SCHEMA live IS 'Live session interaction schema';
COMMENT ON TABLE live.session_participants IS 'Session participant tracking';
COMMENT ON TABLE live.chats IS 'Session chat messages';
COMMENT ON TABLE live.breakout_rooms IS 'Breakout room management';
