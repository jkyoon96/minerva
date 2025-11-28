-- ============================================
-- MIGRATION 011: Additional Performance Indexes
-- ============================================
-- Description: Creates additional indexes for query optimization based on common access patterns
-- Author: System
-- Date: 2025-01-29
-- Dependencies: 003-008 (schema creation)

-- ============================================
-- 1. AUTH SCHEMA - Authentication & User Management
-- ============================================

-- User email search optimization (case-insensitive)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_email_lower
ON auth.users(LOWER(email))
WHERE deleted_at IS NULL;
COMMENT ON INDEX idx_users_email_lower IS 'Optimize case-insensitive email searches';

-- User name search optimization
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_name
ON auth.users(last_name, first_name)
WHERE deleted_at IS NULL;
COMMENT ON INDEX idx_users_name IS 'Optimize user name searches and sorting';

-- Active users by status
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_active_status
ON auth.users(status, last_login_at DESC)
WHERE deleted_at IS NULL AND status = 'active';
COMMENT ON INDEX idx_users_active_status IS 'Optimize active user queries with recent login sorting';

-- Email verification tracking
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_unverified
ON auth.users(created_at DESC)
WHERE email_verified_at IS NULL AND deleted_at IS NULL;
COMMENT ON INDEX idx_users_unverified IS 'Find unverified users for reminder emails';

-- OAuth provider lookup
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_oauth_provider_lookup
ON auth.oauth_accounts(provider, user_id)
WHERE access_token IS NOT NULL;
COMMENT ON INDEX idx_oauth_provider_lookup IS 'Optimize OAuth provider user lookups';

-- Active refresh tokens
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_refresh_tokens_active
ON auth.refresh_tokens(user_id, expires_at DESC)
WHERE revoked_at IS NULL;
COMMENT ON INDEX idx_refresh_tokens_active IS 'Find valid refresh tokens for a user';

-- Token cleanup (expired tokens)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_refresh_tokens_cleanup
ON auth.refresh_tokens(expires_at)
WHERE revoked_at IS NULL AND expires_at < NOW();
COMMENT ON INDEX idx_refresh_tokens_cleanup IS 'Optimize cleanup of expired tokens';

-- Password reset token lookup
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_password_reset_valid
ON auth.password_reset_tokens(token_hash, expires_at)
WHERE used_at IS NULL;
COMMENT ON INDEX idx_password_reset_valid IS 'Validate password reset tokens';

-- Role permissions lookup
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_role_permissions_role
ON auth.role_permissions(role_id, permission_id);
COMMENT ON INDEX idx_role_permissions_role IS 'Optimize role permission checks';

-- 2FA enabled users
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_2fa_enabled
ON auth.two_factor_auth(user_id, is_enabled)
WHERE is_enabled = TRUE;
COMMENT ON INDEX idx_2fa_enabled IS 'Find users with 2FA enabled';

-- ============================================
-- 2. COURSE SCHEMA - Course Management
-- ============================================

-- Course title search (full-text)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_courses_title_trgm
ON course.courses USING gin(title gin_trgm_ops)
WHERE deleted_at IS NULL;
COMMENT ON INDEX idx_courses_title_trgm IS 'Full-text search on course titles';

-- Course code search (case-insensitive)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_courses_code_lower
ON course.courses(LOWER(code))
WHERE deleted_at IS NULL;
COMMENT ON INDEX idx_courses_code_lower IS 'Case-insensitive course code search';

-- Published courses by semester
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_courses_published_semester
ON course.courses(year DESC, semester, professor_id)
WHERE deleted_at IS NULL AND is_published = TRUE;
COMMENT ON INDEX idx_courses_published_semester IS 'List published courses by semester';

-- Course invite code lookup
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_courses_invite_active
ON course.courses(invite_code, invite_expires_at)
WHERE deleted_at IS NULL AND invite_code IS NOT NULL AND invite_expires_at > NOW();
COMMENT ON INDEX idx_courses_invite_active IS 'Validate active invite codes';

-- Course settings JSONB search
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_courses_settings_gin
ON course.courses USING gin(settings)
WHERE deleted_at IS NULL;
COMMENT ON INDEX idx_courses_settings_gin IS 'Search within course settings JSONB';

-- Enrollments by role and status
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_enrollments_role_status
ON course.enrollments(course_id, role, status);
COMMENT ON INDEX idx_enrollments_role_status IS 'Filter enrollments by role and status';

-- Student active enrollments
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_enrollments_student_active
ON course.enrollments(user_id, joined_at DESC)
WHERE role = 'student' AND status = 'active';
COMMENT ON INDEX idx_enrollments_student_active IS 'Student active course list';

-- TA and instructor course access
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_enrollments_staff
ON course.enrollments(user_id, course_id)
WHERE role IN ('ta', 'instructor') AND status = 'active';
COMMENT ON INDEX idx_enrollments_staff IS 'Staff course access lookup';

-- ============================================
-- 3. SESSION SCHEMA - Live Sessions
-- ============================================

-- Upcoming sessions by date range
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_sessions_upcoming
ON course.sessions(course_id, scheduled_at)
WHERE status IN ('scheduled', 'in_progress') AND scheduled_at > NOW();
COMMENT ON INDEX idx_sessions_upcoming IS 'Find upcoming sessions';

-- Sessions by date range
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_sessions_date_range
ON course.sessions(scheduled_at, ended_at)
WHERE status != 'cancelled';
COMMENT ON INDEX idx_sessions_date_range IS 'Query sessions within date ranges';

-- Active/in-progress sessions
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_sessions_active
ON course.sessions(status, started_at DESC)
WHERE status = 'in_progress';
COMMENT ON INDEX idx_sessions_active IS 'Find currently active sessions';

-- Session settings JSONB
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_sessions_settings_gin
ON course.sessions USING gin(settings);
COMMENT ON INDEX idx_sessions_settings_gin IS 'Search session settings';

-- Recording status
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_recordings_status
ON course.recordings(status, created_at DESC);
COMMENT ON INDEX idx_recordings_status IS 'Filter recordings by processing status';

-- Recording metadata JSONB
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_recordings_metadata_gin
ON course.recordings USING gin(metadata)
WHERE metadata IS NOT NULL;
COMMENT ON INDEX idx_recordings_metadata_gin IS 'Search recording metadata';

-- Content by type and visibility
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_contents_type_visible
ON course.contents(course_id, type, order_index)
WHERE is_visible = TRUE;
COMMENT ON INDEX idx_contents_type_visible IS 'List visible contents by type';

-- Folder contents
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_contents_folder_order
ON course.contents(folder_id, order_index)
WHERE folder_id IS NOT NULL;
COMMENT ON INDEX idx_contents_folder_order IS 'List folder contents in order';

-- ============================================
-- 4. ASSIGNMENT & SUBMISSION SCHEMA
-- ============================================

-- Published assignments by due date
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_assignments_published_due
ON course.assignments(course_id, due_date)
WHERE status = 'published';
COMMENT ON INDEX idx_assignments_published_due IS 'List published assignments by due date';

-- Upcoming assignments
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_assignments_upcoming
ON course.assignments(course_id, due_date)
WHERE status = 'published' AND due_date > NOW();
COMMENT ON INDEX idx_assignments_upcoming IS 'Find upcoming assignments';

-- Assignment attachments JSONB
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_assignments_attachments_gin
ON course.assignments USING gin(attachments)
WHERE attachments IS NOT NULL;
COMMENT ON INDEX idx_assignments_attachments_gin IS 'Search assignment attachments';

-- Submissions needing grading
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_submissions_needs_grading
ON course.submissions(assignment_id, submitted_at)
WHERE status = 'submitted' AND graded_at IS NULL;
COMMENT ON INDEX idx_submissions_needs_grading IS 'Find submissions awaiting grading';

-- Late submissions
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_submissions_late
ON course.submissions(assignment_id, user_id)
WHERE is_late = TRUE;
COMMENT ON INDEX idx_submissions_late IS 'Track late submissions';

-- User submission history
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_submissions_user_history
ON course.submissions(user_id, assignment_id, attempt_number DESC);
COMMENT ON INDEX idx_submissions_user_history IS 'User submission attempts history';

-- Graded submissions
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_submissions_graded
ON course.submissions(assignment_id, graded_at DESC)
WHERE status = 'graded';
COMMENT ON INDEX idx_submissions_graded IS 'Recently graded submissions';

-- ============================================
-- 5. LIVE SESSION INTERACTIONS
-- ============================================

-- Current participants (joined but not left)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_participants_current
ON live.session_participants(session_id, joined_at DESC)
WHERE left_at IS NULL;
COMMENT ON INDEX idx_participants_current IS 'Current session participants';

-- Participant connection quality
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_participants_quality
ON live.session_participants(session_id, connection_quality)
WHERE left_at IS NULL;
COMMENT ON INDEX idx_participants_quality IS 'Monitor participant connection quality';

-- Screen sharing participants
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_participants_screenshare
ON live.session_participants(session_id, user_id)
WHERE is_screen_sharing = TRUE AND left_at IS NULL;
COMMENT ON INDEX idx_participants_screenshare IS 'Find users currently screen sharing';

-- Session chat messages (chronological)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_chats_session_time
ON live.chats(session_id, created_at)
WHERE deleted_at IS NULL;
COMMENT ON INDEX idx_chats_session_time IS 'Chronological chat messages';

-- Private messages
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_chats_private
ON live.chats(session_id, private_to_id, created_at)
WHERE is_private = TRUE AND deleted_at IS NULL;
COMMENT ON INDEX idx_chats_private IS 'Private chat messages';

-- Active breakout rooms
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_breakout_active
ON live.breakout_rooms(session_id, status, started_at)
WHERE status = 'active';
COMMENT ON INDEX idx_breakout_active IS 'Active breakout rooms';

-- Breakout participants currently in room
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_breakout_participants_current
ON live.breakout_participants(breakout_room_id, joined_at)
WHERE left_at IS NULL;
COMMENT ON INDEX idx_breakout_participants_current IS 'Current breakout room participants';

-- Reaction counts by session
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_reactions_session_type
ON live.reactions(session_id, type, created_at DESC);
COMMENT ON INDEX idx_reactions_session_type IS 'Aggregate reactions by type';

-- Active hand raises
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hand_raises_active
ON live.hand_raises(session_id, raised_at)
WHERE lowered_at IS NULL AND called_at IS NULL;
COMMENT ON INDEX idx_hand_raises_active IS 'Active hand raises waiting to be called';

-- ============================================
-- 6. LEARNING TOOLS - Polls & Quizzes
-- ============================================

-- Active polls by session
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_polls_active
ON learning.polls(session_id, started_at DESC)
WHERE status = 'active';
COMMENT ON INDEX idx_polls_active IS 'Currently active polls';

-- Poll results aggregation
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_poll_votes_aggregation
ON learning.poll_votes(poll_option_id)
WHERE voted_at IS NOT NULL;
COMMENT ON INDEX idx_poll_votes_aggregation IS 'Aggregate poll vote counts';

-- User poll participation
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_poll_votes_user
ON learning.poll_votes(user_id, voted_at DESC);
COMMENT ON INDEX idx_poll_votes_user IS 'User poll voting history';

-- Published quizzes by course
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_quizzes_published
ON learning.quizzes(course_id, started_at DESC)
WHERE status IN ('active', 'published');
COMMENT ON INDEX idx_quizzes_published IS 'Published/active quizzes';

-- Quiz questions by difficulty
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_questions_difficulty
ON learning.questions(course_id, difficulty, type);
COMMENT ON INDEX idx_questions_difficulty IS 'Filter questions by difficulty';

-- Question tags for question bank
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_questions_tags_gin
ON learning.questions USING gin(tags);
COMMENT ON INDEX idx_questions_tags_gin IS 'Search questions by tags';

-- Quiz attempts in progress
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_quiz_attempts_progress
ON learning.quiz_attempts(quiz_id, user_id, started_at DESC)
WHERE status = 'in_progress';
COMMENT ON INDEX idx_quiz_attempts_progress IS 'In-progress quiz attempts';

-- Quiz attempts completed
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_quiz_attempts_completed
ON learning.quiz_attempts(quiz_id, submitted_at DESC, score DESC)
WHERE status = 'completed';
COMMENT ON INDEX idx_quiz_attempts_completed IS 'Completed quiz attempts with scores';

-- User quiz performance
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_quiz_attempts_user_performance
ON learning.quiz_attempts(user_id, quiz_id, score DESC);
COMMENT ON INDEX idx_quiz_attempts_user_performance IS 'User quiz performance tracking';

-- Whiteboard elements by type
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_whiteboard_elements_type
ON learning.whiteboard_elements(whiteboard_id, type, z_index);
COMMENT ON INDEX idx_whiteboard_elements_type IS 'Whiteboard elements rendering order';

-- Whiteboard data JSONB
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_whiteboard_elements_data_gin
ON learning.whiteboard_elements USING gin(data);
COMMENT ON INDEX idx_whiteboard_elements_data_gin IS 'Search whiteboard element data';

-- ============================================
-- 7. ASSESSMENT SCHEMA
-- ============================================

-- Grades by course (leaderboard)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_grades_course_score
ON assess.grades(course_id, final_score DESC, letter_grade);
COMMENT ON INDEX idx_grades_course_score IS 'Course grade leaderboard';

-- Student grade lookup
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_grades_student
ON assess.grades(user_id, course_id);
COMMENT ON INDEX idx_grades_student IS 'Student grade lookup across courses';

-- AI grading review queue
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_ai_gradings_review
ON assess.ai_gradings(confidence, created_at)
WHERE reviewed_at IS NULL AND confidence < 0.8;
COMMENT ON INDEX idx_ai_gradings_review IS 'Low-confidence AI gradings needing review';

-- AI grading by model version
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_ai_gradings_model
ON assess.ai_gradings(model_version, created_at DESC);
COMMENT ON INDEX idx_ai_gradings_model IS 'Track AI grading by model version';

-- AI grading feedback JSONB
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_ai_gradings_feedback_gin
ON assess.ai_gradings USING gin(feedback);
COMMENT ON INDEX idx_ai_gradings_feedback_gin IS 'Search AI grading feedback';

-- Peer evaluations by assignment
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_peer_eval_assignment
ON assess.peer_evaluations(assignment_id, evaluatee_id);
COMMENT ON INDEX idx_peer_eval_assignment IS 'Peer evaluations per assignment';

-- Peer evaluation scores JSONB
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_peer_eval_scores_gin
ON assess.peer_evaluations USING gin(scores);
COMMENT ON INDEX idx_peer_eval_scores_gin IS 'Search peer evaluation scores';

-- Code execution results
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_code_exec_status
ON assess.code_executions(quiz_answer_id, status, passed_count);
COMMENT ON INDEX idx_code_exec_status IS 'Code execution test results';

-- Code execution by language
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_code_exec_language
ON assess.code_executions(language, created_at DESC);
COMMENT ON INDEX idx_code_exec_language IS 'Code executions by programming language';

-- ============================================
-- 8. ANALYTICS SCHEMA
-- ============================================

-- Participation logs by session and user
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_participation_session_user
ON analytics.participation_logs(session_id, user_id, recorded_at DESC);
COMMENT ON INDEX idx_participation_session_user IS 'User participation in sessions';

-- Engagement score tracking
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_participation_engagement
ON analytics.participation_logs(user_id, engagement_score DESC, recorded_at DESC);
COMMENT ON INDEX idx_participation_engagement IS 'Track user engagement scores';

-- Low engagement alerts
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_participation_low_engagement
ON analytics.participation_logs(user_id, session_id)
WHERE engagement_score < 30;
COMMENT ON INDEX idx_participation_low_engagement IS 'Identify low engagement sessions';

-- Unread alerts by severity
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_alerts_severity_unread
ON analytics.alerts(user_id, severity, created_at DESC)
WHERE is_read = FALSE;
COMMENT ON INDEX idx_alerts_severity_unread IS 'Unread alerts by severity';

-- Alert resolution tracking
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_alerts_resolved
ON analytics.alerts(course_id, type, resolved_at DESC)
WHERE resolved_at IS NOT NULL;
COMMENT ON INDEX idx_alerts_resolved IS 'Resolved alerts tracking';

-- Alerts data JSONB
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_alerts_data_gin
ON analytics.alerts USING gin(data);
COMMENT ON INDEX idx_alerts_data_gin IS 'Search alert data';

-- Interaction logs by type
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_interaction_type
ON analytics.interaction_logs(session_id, interaction_type, created_at);
COMMENT ON INDEX idx_interaction_type IS 'Filter interactions by type';

-- User interaction network
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_interaction_network
ON analytics.interaction_logs(from_user_id, to_user_id, created_at DESC);
COMMENT ON INDEX idx_interaction_network IS 'Build user interaction networks';

-- Interaction context JSONB
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_interaction_context_gin
ON analytics.interaction_logs USING gin(context);
COMMENT ON INDEX idx_interaction_context_gin IS 'Search interaction context';

-- Daily stats by date range
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_daily_stats_date_range
ON analytics.daily_stats(course_id, stat_date DESC);
COMMENT ON INDEX idx_daily_stats_date_range IS 'Query daily stats by date range';

-- Daily stats metrics
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_daily_stats_metrics
ON analytics.daily_stats(course_id, avg_engagement DESC, avg_quiz_score DESC);
COMMENT ON INDEX idx_daily_stats_metrics IS 'Compare daily performance metrics';

-- Notification preferences
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_notification_alert_types_gin
ON analytics.notification_settings USING gin(alert_types);
COMMENT ON INDEX idx_notification_alert_types_gin IS 'Search notification preferences by alert types';

-- ============================================
-- ROLLBACK SCRIPT
-- ============================================
-- To rollback, run:
-- DROP INDEX CONCURRENTLY IF EXISTS idx_users_email_lower;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_users_name;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_users_active_status;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_users_unverified;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_oauth_provider_lookup;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_refresh_tokens_active;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_refresh_tokens_cleanup;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_password_reset_valid;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_role_permissions_role;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_2fa_enabled;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_courses_title_trgm;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_courses_code_lower;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_courses_published_semester;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_courses_invite_active;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_courses_settings_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_enrollments_role_status;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_enrollments_student_active;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_enrollments_staff;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_sessions_upcoming;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_sessions_date_range;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_sessions_active;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_sessions_settings_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_recordings_status;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_recordings_metadata_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_contents_type_visible;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_contents_folder_order;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_assignments_published_due;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_assignments_upcoming;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_assignments_attachments_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_submissions_needs_grading;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_submissions_late;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_submissions_user_history;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_submissions_graded;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_participants_current;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_participants_quality;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_participants_screenshare;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_chats_session_time;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_chats_private;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_breakout_active;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_breakout_participants_current;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_reactions_session_type;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_hand_raises_active;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_polls_active;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_poll_votes_aggregation;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_poll_votes_user;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_quizzes_published;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_questions_difficulty;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_questions_tags_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_quiz_attempts_progress;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_quiz_attempts_completed;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_quiz_attempts_user_performance;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_whiteboard_elements_type;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_whiteboard_elements_data_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_grades_course_score;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_grades_student;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_ai_gradings_review;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_ai_gradings_model;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_ai_gradings_feedback_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_peer_eval_assignment;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_peer_eval_scores_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_code_exec_status;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_code_exec_language;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_participation_session_user;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_participation_engagement;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_participation_low_engagement;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_alerts_severity_unread;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_alerts_resolved;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_alerts_data_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_interaction_type;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_interaction_network;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_interaction_context_gin;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_daily_stats_date_range;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_daily_stats_metrics;
-- DROP INDEX CONCURRENTLY IF EXISTS idx_notification_alert_types_gin;
