-- ============================================
-- ROLLBACK SCRIPT FOR MIGRATION 012
-- ============================================
-- Description: Removes all constraints added in migration 012
-- Usage: Run this script to undo migration 012
-- Author: System
-- Date: 2025-01-29

-- ============================================
-- 1. AUTH SCHEMA CONSTRAINTS
-- ============================================
ALTER TABLE auth.users DROP CONSTRAINT IF EXISTS users_email_format_check;
ALTER TABLE auth.users DROP CONSTRAINT IF EXISTS users_phone_format_check;
ALTER TABLE auth.users DROP CONSTRAINT IF EXISTS users_deleted_status_check;
ALTER TABLE auth.oauth_accounts DROP CONSTRAINT IF EXISTS oauth_token_expires_future;
ALTER TABLE auth.refresh_tokens DROP CONSTRAINT IF EXISTS refresh_token_expires_future;
ALTER TABLE auth.refresh_tokens DROP CONSTRAINT IF EXISTS refresh_token_revoked_after_created;
ALTER TABLE auth.password_reset_tokens DROP CONSTRAINT IF EXISTS password_reset_expires_future;
ALTER TABLE auth.password_reset_tokens DROP CONSTRAINT IF EXISTS password_reset_used_after_created;
ALTER TABLE auth.two_factor_auth DROP CONSTRAINT IF EXISTS twofa_verified_when_enabled;

-- ============================================
-- 2. COURSE SCHEMA CONSTRAINTS
-- ============================================
ALTER TABLE course.courses DROP CONSTRAINT IF EXISTS courses_code_format;
ALTER TABLE course.courses DROP CONSTRAINT IF EXISTS courses_semester_valid;
ALTER TABLE course.courses DROP CONSTRAINT IF EXISTS courses_year_reasonable;
ALTER TABLE course.courses DROP CONSTRAINT IF EXISTS courses_max_students_positive;
ALTER TABLE course.courses DROP CONSTRAINT IF EXISTS courses_invite_expires_future;
ALTER TABLE course.sessions DROP CONSTRAINT IF EXISTS sessions_duration_reasonable;
ALTER TABLE course.sessions DROP CONSTRAINT IF EXISTS sessions_scheduled_logical;
ALTER TABLE course.sessions DROP CONSTRAINT IF EXISTS sessions_started_after_scheduled;
ALTER TABLE course.sessions DROP CONSTRAINT IF EXISTS sessions_ended_after_started;
ALTER TABLE course.recordings DROP CONSTRAINT IF EXISTS recordings_duration_positive;
ALTER TABLE course.recordings DROP CONSTRAINT IF EXISTS recordings_filesize_positive;
ALTER TABLE course.contents DROP CONSTRAINT IF EXISTS contents_filesize_positive;
ALTER TABLE course.contents DROP CONSTRAINT IF EXISTS contents_no_self_reference;
ALTER TABLE course.contents DROP CONSTRAINT IF EXISTS contents_order_nonnegative;
ALTER TABLE course.contents DROP CONSTRAINT IF EXISTS contents_file_has_url;
ALTER TABLE course.assignments DROP CONSTRAINT IF EXISTS assignments_max_score_positive;
ALTER TABLE course.assignments DROP CONSTRAINT IF EXISTS assignments_late_penalty_range;
ALTER TABLE course.assignments DROP CONSTRAINT IF EXISTS assignments_max_attempts_positive;
ALTER TABLE course.assignments DROP CONSTRAINT IF EXISTS assignments_due_after_creation;
ALTER TABLE course.assignments DROP CONSTRAINT IF EXISTS assignments_published_timestamp;
ALTER TABLE course.submissions DROP CONSTRAINT IF EXISTS submissions_score_range;
ALTER TABLE course.submissions DROP CONSTRAINT IF EXISTS submissions_graded_timestamp;
ALTER TABLE course.submissions DROP CONSTRAINT IF EXISTS submissions_attempt_positive;
ALTER TABLE course.submissions DROP CONSTRAINT IF EXISTS submissions_submitted_after_created;

-- ============================================
-- 3. LIVE SCHEMA CONSTRAINTS
-- ============================================
ALTER TABLE live.session_participants DROP CONSTRAINT IF EXISTS participants_left_after_joined;
ALTER TABLE live.session_participants DROP CONSTRAINT IF EXISTS participants_connection_quality_valid;
ALTER TABLE live.session_participants DROP CONSTRAINT IF EXISTS participants_role_valid;
ALTER TABLE live.chats DROP CONSTRAINT IF EXISTS chats_message_not_empty;
ALTER TABLE live.chats DROP CONSTRAINT IF EXISTS chats_private_has_recipient;
ALTER TABLE live.breakout_rooms DROP CONSTRAINT IF EXISTS breakout_duration_positive;
ALTER TABLE live.breakout_rooms DROP CONSTRAINT IF EXISTS breakout_started_before_ended;
ALTER TABLE live.breakout_participants DROP CONSTRAINT IF EXISTS breakout_participants_left_after_joined;
ALTER TABLE live.breakout_participants DROP CONSTRAINT IF EXISTS breakout_assigned_by_valid;
ALTER TABLE live.hand_raises DROP CONSTRAINT IF EXISTS hand_raises_lowered_after_raised;
ALTER TABLE live.hand_raises DROP CONSTRAINT IF EXISTS hand_raises_called_after_raised;
ALTER TABLE live.hand_raises DROP CONSTRAINT IF EXISTS hand_raises_not_both_lowered_called;

-- ============================================
-- 4. LEARNING SCHEMA CONSTRAINTS
-- ============================================
ALTER TABLE learning.polls DROP CONSTRAINT IF EXISTS polls_time_limit_positive;
ALTER TABLE learning.polls DROP CONSTRAINT IF EXISTS polls_started_before_ended;
ALTER TABLE learning.poll_options DROP CONSTRAINT IF EXISTS poll_options_order_nonnegative;
ALTER TABLE learning.poll_options DROP CONSTRAINT IF EXISTS poll_options_text_not_empty;
ALTER TABLE learning.quizzes DROP CONSTRAINT IF EXISTS quizzes_time_limit_positive;
ALTER TABLE learning.quizzes DROP CONSTRAINT IF EXISTS quizzes_passing_score_range;
ALTER TABLE learning.quizzes DROP CONSTRAINT IF EXISTS quizzes_max_attempts_positive;
ALTER TABLE learning.quizzes DROP CONSTRAINT IF EXISTS quizzes_started_before_ended;
ALTER TABLE learning.questions DROP CONSTRAINT IF EXISTS questions_text_not_empty;
ALTER TABLE learning.questions DROP CONSTRAINT IF EXISTS questions_points_positive;
ALTER TABLE learning.questions DROP CONSTRAINT IF EXISTS questions_difficulty_valid;
ALTER TABLE learning.questions DROP CONSTRAINT IF EXISTS questions_order_nonnegative;
ALTER TABLE learning.quiz_attempts DROP CONSTRAINT IF EXISTS quiz_attempts_score_nonnegative;
ALTER TABLE learning.quiz_attempts DROP CONSTRAINT IF EXISTS quiz_attempts_max_score_positive;
ALTER TABLE learning.quiz_attempts DROP CONSTRAINT IF EXISTS quiz_attempts_score_within_max;
ALTER TABLE learning.quiz_attempts DROP CONSTRAINT IF EXISTS quiz_attempts_number_positive;
ALTER TABLE learning.quiz_attempts DROP CONSTRAINT IF EXISTS quiz_attempts_submitted_after_started;
ALTER TABLE learning.quiz_answers DROP CONSTRAINT IF EXISTS quiz_answers_points_nonnegative;
ALTER TABLE learning.whiteboard_elements DROP CONSTRAINT IF EXISTS whiteboard_elements_zindex_nonnegative;

-- ============================================
-- 5. ASSESSMENT SCHEMA CONSTRAINTS
-- ============================================
ALTER TABLE assess.grades DROP CONSTRAINT IF EXISTS grades_participation_range;
ALTER TABLE assess.grades DROP CONSTRAINT IF EXISTS grades_quiz_avg_range;
ALTER TABLE assess.grades DROP CONSTRAINT IF EXISTS grades_assignment_avg_range;
ALTER TABLE assess.grades DROP CONSTRAINT IF EXISTS grades_final_score_range;
ALTER TABLE assess.grades DROP CONSTRAINT IF EXISTS grades_letter_valid;
ALTER TABLE assess.ai_gradings DROP CONSTRAINT IF EXISTS ai_gradings_score_range;
ALTER TABLE assess.ai_gradings DROP CONSTRAINT IF EXISTS ai_gradings_confidence_range;
ALTER TABLE assess.ai_gradings DROP CONSTRAINT IF EXISTS ai_gradings_similarity_range;
ALTER TABLE assess.ai_gradings DROP CONSTRAINT IF EXISTS ai_gradings_final_score_range;
ALTER TABLE assess.ai_gradings DROP CONSTRAINT IF EXISTS ai_gradings_reviewed_timestamp;
ALTER TABLE assess.code_executions DROP CONSTRAINT IF EXISTS code_exec_counts_valid;
ALTER TABLE assess.code_executions DROP CONSTRAINT IF EXISTS code_exec_time_nonnegative;
ALTER TABLE assess.code_executions DROP CONSTRAINT IF EXISTS code_exec_memory_nonnegative;
ALTER TABLE assess.code_executions DROP CONSTRAINT IF EXISTS code_exec_status_valid;
ALTER TABLE assess.code_executions DROP CONSTRAINT IF EXISTS code_exec_language_valid;

-- ============================================
-- 6. ANALYTICS SCHEMA CONSTRAINTS
-- ============================================
ALTER TABLE analytics.participation_logs DROP CONSTRAINT IF EXISTS participation_talk_time_nonnegative;
ALTER TABLE analytics.participation_logs DROP CONSTRAINT IF EXISTS participation_counts_nonnegative;
ALTER TABLE analytics.participation_logs DROP CONSTRAINT IF EXISTS participation_engagement_range;
ALTER TABLE analytics.alerts DROP CONSTRAINT IF EXISTS alerts_severity_valid;
ALTER TABLE analytics.alerts DROP CONSTRAINT IF EXISTS alerts_resolved_timestamp;
ALTER TABLE analytics.daily_stats DROP CONSTRAINT IF EXISTS daily_stats_counts_nonnegative;
ALTER TABLE analytics.daily_stats DROP CONSTRAINT IF EXISTS daily_stats_engagement_range;
ALTER TABLE analytics.daily_stats DROP CONSTRAINT IF EXISTS daily_stats_quiz_score_range;
ALTER TABLE analytics.daily_stats DROP CONSTRAINT IF EXISTS daily_stats_date_not_future;

-- ============================================
-- 7. UNIQUE INDEXES
-- ============================================
DROP INDEX IF EXISTS live.idx_hand_raises_active_unique;
DROP INDEX IF EXISTS learning.idx_quiz_attempts_in_progress_unique;

-- ============================================
-- 8. RESTORE ORIGINAL FOREIGN KEY CONSTRAINTS
-- ============================================
-- Note: The original constraints used default ON DELETE behavior
-- We need to restore them to their original state

-- Submissions graded_by
ALTER TABLE course.submissions
DROP CONSTRAINT IF EXISTS submissions_graded_by_fkey,
ADD CONSTRAINT submissions_graded_by_fkey
FOREIGN KEY (graded_by) REFERENCES auth.users(id);

-- Hand raises called_by
ALTER TABLE live.hand_raises
DROP CONSTRAINT IF EXISTS hand_raises_called_by_fkey,
ADD CONSTRAINT hand_raises_called_by_fkey
FOREIGN KEY (called_by) REFERENCES auth.users(id);

-- User roles assigned_by
ALTER TABLE auth.user_roles
DROP CONSTRAINT IF EXISTS user_roles_assigned_by_fkey,
ADD CONSTRAINT user_roles_assigned_by_fkey
FOREIGN KEY (assigned_by) REFERENCES auth.users(id);

-- AI grading reviewed_by
ALTER TABLE assess.ai_gradings
DROP CONSTRAINT IF EXISTS ai_gradings_reviewed_by_fkey,
ADD CONSTRAINT ai_gradings_reviewed_by_fkey
FOREIGN KEY (reviewed_by) REFERENCES auth.users(id);

-- Alerts resolved_by
ALTER TABLE analytics.alerts
DROP CONSTRAINT IF EXISTS alerts_resolved_by_fkey,
ADD CONSTRAINT alerts_resolved_by_fkey
FOREIGN KEY (resolved_by) REFERENCES auth.users(id);

-- Completion message
DO $$
BEGIN
    RAISE NOTICE 'Migration 012 rollback completed successfully';
    RAISE NOTICE 'All additional constraints have been removed';
    RAISE NOTICE 'Foreign key constraints have been restored to original state';
END $$;
