-- =====================================================
-- E3 실시간 세미나 시스템 - 데이터베이스 스키마
-- Version: V003
-- Description: Create seminar schema for real-time seminar rooms
-- =====================================================

-- Create schema
CREATE SCHEMA IF NOT EXISTS seminar;

-- =====================================================
-- ENUM Types
-- =====================================================

-- Room status enum
CREATE TYPE room_status AS ENUM (
    'WAITING',      -- Waiting room / before session starts
    'ACTIVE',       -- Session is live and active
    'ENDED'         -- Session has ended
);

-- Participant role enum
CREATE TYPE participant_role AS ENUM (
    'HOST',         -- Professor/instructor who created the room
    'CO_HOST',      -- Teaching assistant or co-instructor
    'PARTICIPANT'   -- Student participant
);

-- Participant status enum
CREATE TYPE participant_status AS ENUM (
    'WAITING',      -- In waiting room
    'JOINED',       -- Joined the active session
    'LEFT'          -- Left the session
);

-- Message type enum
CREATE TYPE message_type AS ENUM (
    'TEXT',         -- Regular text message
    'FILE',         -- File attachment
    'SYSTEM'        -- System notification message
);

-- Reaction type enum
CREATE TYPE reaction_type AS ENUM (
    'THUMBS_UP',    -- 👍
    'CLAP',         -- 👏
    'HEART',        -- ❤️
    'LAUGH',        -- 😂
    'SURPRISE'      -- 😮
);

-- Layout type enum
CREATE TYPE layout_type AS ENUM (
    'GALLERY',      -- Grid view of all participants
    'SPEAKER',      -- Focus on active speaker
    'SIDEBAR',      -- Main content with sidebar
    'PRESENTATION'  -- Full screen presentation mode
);

-- =====================================================
-- TABLES
-- =====================================================

-- Seminar rooms table
CREATE TABLE seminar.rooms (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    host_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    status room_status NOT NULL DEFAULT 'WAITING',
    max_participants INTEGER NOT NULL DEFAULT 100,
    started_at TIMESTAMPTZ,
    ended_at TIMESTAMPTZ,
    meeting_url VARCHAR(500),
    recording_url VARCHAR(500),
    layout layout_type NOT NULL DEFAULT 'GALLERY',
    settings JSONB NOT NULL DEFAULT '{
        "enableWaitingRoom": true,
        "autoRecord": true,
        "allowChat": true,
        "allowReactions": true,
        "allowScreenShare": true,
        "muteOnEntry": false,
        "videoOnEntry": true
    }'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

-- Room participants table
CREATE TABLE seminar.room_participants (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    role participant_role NOT NULL DEFAULT 'PARTICIPANT',
    status participant_status NOT NULL DEFAULT 'WAITING',
    joined_at TIMESTAMPTZ,
    left_at TIMESTAMPTZ,
    is_hand_raised BOOLEAN NOT NULL DEFAULT FALSE,
    is_muted BOOLEAN NOT NULL DEFAULT FALSE,
    is_video_on BOOLEAN NOT NULL DEFAULT TRUE,
    is_screen_sharing BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    CONSTRAINT unique_room_user UNIQUE(room_id, user_id)
);

-- Chat messages table
CREATE TABLE seminar.chat_messages (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id) ON DELETE CASCADE,
    sender_id BIGINT REFERENCES auth.users(id) ON DELETE SET NULL,
    message_type message_type NOT NULL DEFAULT 'TEXT',
    content TEXT NOT NULL,
    file_url VARCHAR(500),
    file_name VARCHAR(255),
    file_size BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

-- Reactions table
CREATE TABLE seminar.reactions (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    reaction_type reaction_type NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

-- =====================================================
-- INDEXES
-- =====================================================

-- Seminar rooms indexes
CREATE INDEX idx_rooms_session ON seminar.rooms(session_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_rooms_host ON seminar.rooms(host_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_rooms_status ON seminar.rooms(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_rooms_created ON seminar.rooms(created_at DESC) WHERE deleted_at IS NULL;

-- Room participants indexes
CREATE INDEX idx_participants_room ON seminar.room_participants(room_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_participants_user ON seminar.room_participants(user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_participants_status ON seminar.room_participants(room_id, status) WHERE deleted_at IS NULL;
CREATE INDEX idx_participants_hand_raised ON seminar.room_participants(room_id) WHERE is_hand_raised = TRUE AND deleted_at IS NULL;

-- Chat messages indexes
CREATE INDEX idx_chat_room_created ON seminar.chat_messages(room_id, created_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_chat_sender ON seminar.chat_messages(sender_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_chat_type ON seminar.chat_messages(room_id, message_type) WHERE deleted_at IS NULL;

-- Reactions indexes
CREATE INDEX idx_reaction_room_created ON seminar.reactions(room_id, created_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_reaction_user ON seminar.reactions(user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_reaction_type ON seminar.reactions(room_id, reaction_type) WHERE deleted_at IS NULL;

-- =====================================================
-- TRIGGERS
-- =====================================================

-- Updated timestamp trigger function (if not exists)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply updated_at trigger to all tables
CREATE TRIGGER update_rooms_updated_at
    BEFORE UPDATE ON seminar.rooms
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_participants_updated_at
    BEFORE UPDATE ON seminar.room_participants
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_messages_updated_at
    BEFORE UPDATE ON seminar.chat_messages
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reactions_updated_at
    BEFORE UPDATE ON seminar.reactions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- COMMENTS
-- =====================================================

COMMENT ON SCHEMA seminar IS 'E3 실시간 세미나 시스템 스키마';

COMMENT ON TABLE seminar.rooms IS '세미나 룸 - 실시간 세션을 위한 가상 룸';
COMMENT ON TABLE seminar.room_participants IS '룸 참가자 - 세미나 룸에 참가한 사용자';
COMMENT ON TABLE seminar.chat_messages IS '채팅 메시지 - 룸 내 채팅 메시지';
COMMENT ON TABLE seminar.reactions IS '반응 - 실시간 이모지 반응';

COMMENT ON COLUMN seminar.rooms.settings IS 'JSONB 룸 설정 (대기실, 자동 녹화, 채팅/반응 허용 등)';
COMMENT ON COLUMN seminar.room_participants.is_hand_raised IS '손들기 상태';
COMMENT ON COLUMN seminar.room_participants.is_screen_sharing IS '화면 공유 상태';
COMMENT ON COLUMN seminar.chat_messages.message_type IS '메시지 타입 (TEXT, FILE, SYSTEM)';
COMMENT ON COLUMN seminar.reactions.reaction_type IS '반응 타입 (THUMBS_UP, CLAP, HEART, LAUGH, SURPRISE)';
