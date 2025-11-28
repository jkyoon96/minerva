-- ============================================
-- MIGRATION 008: Analytics Schema
-- ============================================
-- Description: Creates analytics and reporting schema and tables
-- Author: System
-- Date: 2025-01-28

-- Create schema
CREATE SCHEMA IF NOT EXISTS analytics;

-- Participation logs table
CREATE TABLE analytics.participation_logs (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    talk_time_sec   INTEGER NOT NULL DEFAULT 0,
    chat_count      INTEGER NOT NULL DEFAULT 0,
    poll_count      INTEGER NOT NULL DEFAULT 0,
    quiz_count      INTEGER NOT NULL DEFAULT 0,
    hand_raise_count INTEGER NOT NULL DEFAULT 0,
    reaction_count  INTEGER NOT NULL DEFAULT 0,
    breakout_time_sec INTEGER NOT NULL DEFAULT 0,
    engagement_score DECIMAL(5,2) NOT NULL DEFAULT 0,
    recorded_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_participation_logs_session_id ON analytics.participation_logs(session_id);
CREATE INDEX idx_participation_logs_user_id ON analytics.participation_logs(user_id);
CREATE INDEX idx_participation_logs_recorded_at ON analytics.participation_logs(recorded_at);

-- Alerts table
CREATE TABLE analytics.alerts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    type            alert_type NOT NULL,
    severity        alert_severity NOT NULL DEFAULT 'medium',
    message         TEXT NOT NULL,
    data            JSONB DEFAULT '{}',
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at     TIMESTAMPTZ,
    resolved_by     BIGINT REFERENCES auth.users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_alerts_user_id ON analytics.alerts(user_id);
CREATE INDEX idx_alerts_course_id ON analytics.alerts(course_id);
CREATE INDEX idx_alerts_type ON analytics.alerts(type);
CREATE INDEX idx_alerts_is_read ON analytics.alerts(is_read) WHERE is_read = FALSE;
CREATE INDEX idx_alerts_created_at ON analytics.alerts(created_at);

-- Interaction logs table
CREATE TABLE analytics.interaction_logs (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    from_user_id    BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    to_user_id      BIGINT REFERENCES auth.users(id) ON DELETE CASCADE,
    interaction_type VARCHAR(50) NOT NULL,
    context         JSONB DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_interaction_logs_session_id ON analytics.interaction_logs(session_id);
CREATE INDEX idx_interaction_logs_from_user_id ON analytics.interaction_logs(from_user_id);
CREATE INDEX idx_interaction_logs_to_user_id ON analytics.interaction_logs(to_user_id);
CREATE INDEX idx_interaction_logs_created_at ON analytics.interaction_logs(created_at);

-- Daily stats table
CREATE TABLE analytics.daily_stats (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    stat_date       DATE NOT NULL,
    active_users    INTEGER NOT NULL DEFAULT 0,
    total_sessions  INTEGER NOT NULL DEFAULT 0,
    total_talk_time_sec BIGINT NOT NULL DEFAULT 0,
    avg_engagement  DECIMAL(5,2) NOT NULL DEFAULT 0,
    quiz_attempts   INTEGER NOT NULL DEFAULT 0,
    avg_quiz_score  DECIMAL(5,2),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT daily_stats_unique UNIQUE (course_id, stat_date)
);

CREATE INDEX idx_daily_stats_course_id ON analytics.daily_stats(course_id);
CREATE INDEX idx_daily_stats_stat_date ON analytics.daily_stats(stat_date);

-- Notification settings table
CREATE TABLE analytics.notification_settings (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE UNIQUE,
    email_enabled   BOOLEAN NOT NULL DEFAULT TRUE,
    push_enabled    BOOLEAN NOT NULL DEFAULT TRUE,
    alert_types     JSONB NOT NULL DEFAULT '["absence", "low_participation", "grade_drop"]',
    quiet_hours     JSONB DEFAULT '{"start": "22:00", "end": "08:00"}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER update_notification_settings_updated_at
    BEFORE UPDATE ON analytics.notification_settings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON SCHEMA analytics IS 'Analytics and reporting schema';
COMMENT ON TABLE analytics.participation_logs IS 'Session participation tracking';
COMMENT ON TABLE analytics.alerts IS 'System alerts and notifications';
COMMENT ON TABLE analytics.interaction_logs IS 'User interaction tracking for network analysis';
COMMENT ON TABLE analytics.daily_stats IS 'Daily aggregated statistics';
