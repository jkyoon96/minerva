-- ============================================
-- ROLLBACK SCRIPT FOR MIGRATION 011
-- ============================================
-- Description: Removes all indexes created in migration 011
-- Usage: Run this script to undo migration 011
-- Author: System
-- Date: 2025-01-29

-- ============================================
-- 1. AUTH SCHEMA INDEXES
-- ============================================
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_users_email_lower;
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_users_name;
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_users_active_status;
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_users_unverified;
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_oauth_provider_lookup;
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_refresh_tokens_active;
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_refresh_tokens_cleanup;
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_password_reset_valid;
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_role_permissions_role;
DROP INDEX CONCURRENTLY IF EXISTS auth.idx_2fa_enabled;

-- ============================================
-- 2. COURSE SCHEMA INDEXES
-- ============================================
DROP INDEX CONCURRENTLY IF EXISTS course.idx_courses_title_trgm;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_courses_code_lower;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_courses_published_semester;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_courses_invite_active;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_courses_settings_gin;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_enrollments_role_status;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_enrollments_student_active;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_enrollments_staff;

-- ============================================
-- 3. SESSION SCHEMA INDEXES
-- ============================================
DROP INDEX CONCURRENTLY IF EXISTS course.idx_sessions_upcoming;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_sessions_date_range;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_sessions_active;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_sessions_settings_gin;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_recordings_status;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_recordings_metadata_gin;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_contents_type_visible;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_contents_folder_order;

-- ============================================
-- 4. ASSIGNMENT & SUBMISSION INDEXES
-- ============================================
DROP INDEX CONCURRENTLY IF EXISTS course.idx_assignments_published_due;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_assignments_upcoming;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_assignments_attachments_gin;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_submissions_needs_grading;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_submissions_late;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_submissions_user_history;
DROP INDEX CONCURRENTLY IF EXISTS course.idx_submissions_graded;

-- ============================================
-- 5. LIVE SESSION INTERACTION INDEXES
-- ============================================
DROP INDEX CONCURRENTLY IF EXISTS live.idx_participants_current;
DROP INDEX CONCURRENTLY IF EXISTS live.idx_participants_quality;
DROP INDEX CONCURRENTLY IF EXISTS live.idx_participants_screenshare;
DROP INDEX CONCURRENTLY IF EXISTS live.idx_chats_session_time;
DROP INDEX CONCURRENTLY IF EXISTS live.idx_chats_private;
DROP INDEX CONCURRENTLY IF EXISTS live.idx_breakout_active;
DROP INDEX CONCURRENTLY IF EXISTS live.idx_breakout_participants_current;
DROP INDEX CONCURRENTLY IF EXISTS live.idx_reactions_session_type;
DROP INDEX CONCURRENTLY IF EXISTS live.idx_hand_raises_active;

-- ============================================
-- 6. LEARNING TOOLS INDEXES
-- ============================================
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_polls_active;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_poll_votes_aggregation;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_poll_votes_user;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_quizzes_published;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_questions_difficulty;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_questions_tags_gin;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_quiz_attempts_progress;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_quiz_attempts_completed;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_quiz_attempts_user_performance;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_whiteboard_elements_type;
DROP INDEX CONCURRENTLY IF EXISTS learning.idx_whiteboard_elements_data_gin;

-- ============================================
-- 7. ASSESSMENT SCHEMA INDEXES
-- ============================================
DROP INDEX CONCURRENTLY IF EXISTS assess.idx_grades_course_score;
DROP INDEX CONCURRENTLY IF EXISTS assess.idx_grades_student;
DROP INDEX CONCURRENTLY IF EXISTS assess.idx_ai_gradings_review;
DROP INDEX CONCURRENTLY IF EXISTS assess.idx_ai_gradings_model;
DROP INDEX CONCURRENTLY IF EXISTS assess.idx_ai_gradings_feedback_gin;
DROP INDEX CONCURRENTLY IF EXISTS assess.idx_peer_eval_assignment;
DROP INDEX CONCURRENTLY IF EXISTS assess.idx_peer_eval_scores_gin;
DROP INDEX CONCURRENTLY IF EXISTS assess.idx_code_exec_status;
DROP INDEX CONCURRENTLY IF EXISTS assess.idx_code_exec_language;

-- ============================================
-- 8. ANALYTICS SCHEMA INDEXES
-- ============================================
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_participation_session_user;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_participation_engagement;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_participation_low_engagement;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_alerts_severity_unread;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_alerts_resolved;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_alerts_data_gin;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_interaction_type;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_interaction_network;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_interaction_context_gin;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_daily_stats_date_range;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_daily_stats_metrics;
DROP INDEX CONCURRENTLY IF EXISTS analytics.idx_notification_alert_types_gin;

-- Completion message
DO $$
BEGIN
    RAISE NOTICE 'Migration 011 rollback completed successfully';
    RAISE NOTICE 'All additional indexes have been removed';
END $$;
