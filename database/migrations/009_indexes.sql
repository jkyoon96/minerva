-- ============================================
-- MIGRATION 009: Additional Indexes
-- ============================================
-- Description: Creates additional indexes for query optimization
-- Author: System
-- Date: 2025-01-28

-- Course enrollments composite index
CREATE INDEX idx_enrollments_user_course_status
ON course.enrollments(user_id, course_id, status);

-- Session scheduling index
CREATE INDEX idx_sessions_course_scheduled
ON course.sessions(course_id, scheduled_at)
WHERE status != 'cancelled';

-- Active session participants index
CREATE INDEX idx_session_participants_active
ON live.session_participants(session_id, user_id)
WHERE left_at IS NULL;

-- Pending submissions index
CREATE INDEX idx_submissions_pending
ON course.submissions(assignment_id, user_id, status)
WHERE status = 'submitted';

-- Quiz answers lookup index
CREATE INDEX idx_quiz_answers_attempt_question
ON learning.quiz_answers(attempt_id, question_id);

-- Participation logs by period index
CREATE INDEX idx_participation_logs_user_period
ON analytics.participation_logs(user_id, recorded_at);

-- Unread alerts index
CREATE INDEX idx_alerts_unread
ON analytics.alerts(user_id, created_at DESC)
WHERE is_read = FALSE;

-- Comments
COMMENT ON INDEX idx_enrollments_user_course_status IS 'Optimize enrollment queries by user and course';
COMMENT ON INDEX idx_sessions_course_scheduled IS 'Optimize session scheduling queries';
COMMENT ON INDEX idx_session_participants_active IS 'Optimize active participant queries';
