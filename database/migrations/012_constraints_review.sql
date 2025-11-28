-- ============================================
-- MIGRATION 012: Additional Constraints Review & Optimization
-- ============================================
-- Description: Adds additional UNIQUE, CHECK, and FOREIGN KEY constraints for data integrity
-- Author: System
-- Date: 2025-01-29
-- Dependencies: 003-008 (schema creation)

-- ============================================
-- 1. AUTH SCHEMA - Enhanced Constraints
-- ============================================

-- Ensure email format is valid
ALTER TABLE auth.users
ADD CONSTRAINT users_email_format_check
CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Phone number format validation (optional field)
ALTER TABLE auth.users
ADD CONSTRAINT users_phone_format_check
CHECK (phone IS NULL OR phone ~ '^\+?[0-9]{10,15}$');

-- Status cannot be changed to deleted via status field
ALTER TABLE auth.users
ADD CONSTRAINT users_deleted_status_check
CHECK (NOT (deleted_at IS NOT NULL AND status = 'active'));

-- OAuth token expiry must be in future when set
ALTER TABLE auth.oauth_accounts
ADD CONSTRAINT oauth_token_expires_future
CHECK (token_expires_at IS NULL OR token_expires_at > created_at);

-- Refresh token must expire in future
ALTER TABLE auth.refresh_tokens
ADD CONSTRAINT refresh_token_expires_future
CHECK (expires_at > created_at);

-- Cannot revoke token before it's created
ALTER TABLE auth.refresh_tokens
ADD CONSTRAINT refresh_token_revoked_after_created
CHECK (revoked_at IS NULL OR revoked_at >= created_at);

-- Password reset must expire in future
ALTER TABLE auth.password_reset_tokens
ADD CONSTRAINT password_reset_expires_future
CHECK (expires_at > created_at);

-- Cannot use reset token before it's created
ALTER TABLE auth.password_reset_tokens
ADD CONSTRAINT password_reset_used_after_created
CHECK (used_at IS NULL OR used_at >= created_at);

-- 2FA verified timestamp validation
ALTER TABLE auth.two_factor_auth
ADD CONSTRAINT twofa_verified_when_enabled
CHECK (NOT (is_enabled = TRUE AND verified_at IS NULL));

-- ============================================
-- 2. COURSE SCHEMA - Enhanced Constraints
-- ============================================

-- Course code format (e.g., CS101, MATH200)
ALTER TABLE course.courses
ADD CONSTRAINT courses_code_format
CHECK (code ~ '^[A-Z]{2,4}[0-9]{3,4}[A-Z]?$');

-- Semester validation
ALTER TABLE course.courses
ADD CONSTRAINT courses_semester_valid
CHECK (semester IN ('Spring', 'Summer', 'Fall', 'Winter'));

-- Year must be reasonable (current year ± 5 years)
ALTER TABLE course.courses
ADD CONSTRAINT courses_year_reasonable
CHECK (year BETWEEN EXTRACT(YEAR FROM NOW())::INTEGER - 5 AND EXTRACT(YEAR FROM NOW())::INTEGER + 5);

-- Max students must be positive
ALTER TABLE course.courses
ADD CONSTRAINT courses_max_students_positive
CHECK (max_students > 0 AND max_students <= 1000);

-- Invite expiry must be in future when set
ALTER TABLE course.courses
ADD CONSTRAINT courses_invite_expires_future
CHECK (invite_expires_at IS NULL OR invite_expires_at > NOW());

-- Session duration must be reasonable (15 min to 8 hours)
ALTER TABLE course.sessions
ADD CONSTRAINT sessions_duration_reasonable
CHECK (duration_minutes BETWEEN 15 AND 480);

-- Session scheduled in future for new sessions
ALTER TABLE course.sessions
ADD CONSTRAINT sessions_scheduled_logical
CHECK (status != 'scheduled' OR scheduled_at >= created_at);

-- Started timestamp must be after scheduled
ALTER TABLE course.sessions
ADD CONSTRAINT sessions_started_after_scheduled
CHECK (started_at IS NULL OR started_at >= scheduled_at);

-- Ended timestamp must be after started
ALTER TABLE course.sessions
ADD CONSTRAINT sessions_ended_after_started
CHECK (ended_at IS NULL OR (started_at IS NOT NULL AND ended_at >= started_at));

-- Recording duration must be positive
ALTER TABLE course.recordings
ADD CONSTRAINT recordings_duration_positive
CHECK (duration_seconds IS NULL OR duration_seconds > 0);

-- Recording file size must be positive
ALTER TABLE course.recordings
ADD CONSTRAINT recordings_filesize_positive
CHECK (file_size_bytes IS NULL OR file_size_bytes > 0);

-- Content file size must be positive
ALTER TABLE course.contents
ADD CONSTRAINT contents_filesize_positive
CHECK (file_size_bytes IS NULL OR file_size_bytes > 0);

-- Folder cannot be its own parent (prevent circular reference)
ALTER TABLE course.contents
ADD CONSTRAINT contents_no_self_reference
CHECK (folder_id IS NULL OR folder_id != id);

-- Order index must be non-negative
ALTER TABLE course.contents
ADD CONSTRAINT contents_order_nonnegative
CHECK (order_index >= 0);

-- File type must have URL
ALTER TABLE course.contents
ADD CONSTRAINT contents_file_has_url
CHECK (type != 'file' OR file_url IS NOT NULL);

-- Assignment max score must be positive
ALTER TABLE course.assignments
ADD CONSTRAINT assignments_max_score_positive
CHECK (max_score > 0 AND max_score <= 1000);

-- Late penalty must be reasonable (0-100%)
ALTER TABLE course.assignments
ADD CONSTRAINT assignments_late_penalty_range
CHECK (late_penalty_percent IS NULL OR (late_penalty_percent >= 0 AND late_penalty_percent <= 100));

-- Max attempts must be positive
ALTER TABLE course.assignments
ADD CONSTRAINT assignments_max_attempts_positive
CHECK (max_attempts IS NULL OR (max_attempts > 0 AND max_attempts <= 10));

-- Due date must be after creation for new assignments
ALTER TABLE course.assignments
ADD CONSTRAINT assignments_due_after_creation
CHECK (status != 'draft' OR due_date >= created_at);

-- Published timestamp must be set when published
ALTER TABLE course.assignments
ADD CONSTRAINT assignments_published_timestamp
CHECK (status != 'published' OR published_at IS NOT NULL);

-- Submission score must be within range
ALTER TABLE course.submissions
ADD CONSTRAINT submissions_score_range
CHECK (score IS NULL OR (score >= 0 AND score <= 100));

-- Graded timestamp must be set when status is graded
ALTER TABLE course.submissions
ADD CONSTRAINT submissions_graded_timestamp
CHECK (status != 'graded' OR (graded_at IS NOT NULL AND graded_by IS NOT NULL));

-- Attempt number must be positive
ALTER TABLE course.submissions
ADD CONSTRAINT submissions_attempt_positive
CHECK (attempt_number > 0);

-- Submission must be after creation
ALTER TABLE course.submissions
ADD CONSTRAINT submissions_submitted_after_created
CHECK (submitted_at >= created_at);

-- ============================================
-- 3. LIVE SESSION SCHEMA - Enhanced Constraints
-- ============================================

-- Participant left time must be after joined
ALTER TABLE live.session_participants
ADD CONSTRAINT participants_left_after_joined
CHECK (left_at IS NULL OR left_at >= joined_at);

-- Connection quality validation
ALTER TABLE live.session_participants
ADD CONSTRAINT participants_connection_quality_valid
CHECK (connection_quality IN ('excellent', 'good', 'fair', 'poor', 'disconnected'));

-- Role validation
ALTER TABLE live.session_participants
ADD CONSTRAINT participants_role_valid
CHECK (role IN ('host', 'cohost', 'participant', 'observer'));

-- Message cannot be empty for text type
ALTER TABLE live.chats
ADD CONSTRAINT chats_message_not_empty
CHECK (type != 'text' OR LENGTH(TRIM(message)) > 0);

-- Private message must have recipient
ALTER TABLE live.chats
ADD CONSTRAINT chats_private_has_recipient
CHECK (NOT is_private OR private_to_id IS NOT NULL);

-- Breakout room duration must be positive
ALTER TABLE live.breakout_rooms
ADD CONSTRAINT breakout_duration_positive
CHECK (duration_minutes IS NULL OR (duration_minutes > 0 AND duration_minutes <= 240));

-- Breakout started before ended
ALTER TABLE live.breakout_rooms
ADD CONSTRAINT breakout_started_before_ended
CHECK (ended_at IS NULL OR (started_at IS NOT NULL AND ended_at >= started_at));

-- Breakout participant left after joined
ALTER TABLE live.breakout_participants
ADD CONSTRAINT breakout_participants_left_after_joined
CHECK (left_at IS NULL OR left_at >= joined_at);

-- Assignment method validation
ALTER TABLE live.breakout_participants
ADD CONSTRAINT breakout_assigned_by_valid
CHECK (assigned_by IN ('auto', 'manual', 'random', 'self'));

-- Hand lowered/called must be after raised
ALTER TABLE live.hand_raises
ADD CONSTRAINT hand_raises_lowered_after_raised
CHECK (lowered_at IS NULL OR lowered_at >= raised_at);

ALTER TABLE live.hand_raises
ADD CONSTRAINT hand_raises_called_after_raised
CHECK (called_at IS NULL OR called_at >= raised_at);

-- Cannot be both lowered and called
ALTER TABLE live.hand_raises
ADD CONSTRAINT hand_raises_not_both_lowered_called
CHECK (lowered_at IS NULL OR called_at IS NULL);

-- ============================================
-- 4. LEARNING TOOLS SCHEMA - Enhanced Constraints
-- ============================================

-- Poll time limit must be positive
ALTER TABLE learning.polls
ADD CONSTRAINT polls_time_limit_positive
CHECK (time_limit_sec IS NULL OR (time_limit_sec > 0 AND time_limit_sec <= 3600));

-- Poll started before ended
ALTER TABLE learning.polls
ADD CONSTRAINT polls_started_before_ended
CHECK (ended_at IS NULL OR (started_at IS NOT NULL AND ended_at >= started_at));

-- Poll option order must be non-negative
ALTER TABLE learning.poll_options
ADD CONSTRAINT poll_options_order_nonnegative
CHECK (order_index >= 0);

-- Poll option text cannot be empty
ALTER TABLE learning.poll_options
ADD CONSTRAINT poll_options_text_not_empty
CHECK (LENGTH(TRIM(option_text)) > 0);

-- Quiz time limit must be positive
ALTER TABLE learning.quizzes
ADD CONSTRAINT quizzes_time_limit_positive
CHECK (time_limit_sec IS NULL OR (time_limit_sec > 0 AND time_limit_sec <= 14400));

-- Passing score must be 0-100
ALTER TABLE learning.quizzes
ADD CONSTRAINT quizzes_passing_score_range
CHECK (passing_score >= 0 AND passing_score <= 100);

-- Max attempts must be positive
ALTER TABLE learning.quizzes
ADD CONSTRAINT quizzes_max_attempts_positive
CHECK (max_attempts IS NULL OR (max_attempts > 0 AND max_attempts <= 10));

-- Quiz started before ended
ALTER TABLE learning.quizzes
ADD CONSTRAINT quizzes_started_before_ended
CHECK (ended_at IS NULL OR (started_at IS NOT NULL AND ended_at >= started_at));

-- Question text cannot be empty
ALTER TABLE learning.questions
ADD CONSTRAINT questions_text_not_empty
CHECK (LENGTH(TRIM(question_text)) > 0);

-- Question points must be positive
ALTER TABLE learning.questions
ADD CONSTRAINT questions_points_positive
CHECK (points > 0 AND points <= 100);

-- Difficulty validation
ALTER TABLE learning.questions
ADD CONSTRAINT questions_difficulty_valid
CHECK (difficulty IN ('easy', 'medium', 'hard', 'expert'));

-- Order index must be non-negative
ALTER TABLE learning.questions
ADD CONSTRAINT questions_order_nonnegative
CHECK (order_index >= 0);

-- Quiz attempt scores must be non-negative
ALTER TABLE learning.quiz_attempts
ADD CONSTRAINT quiz_attempts_score_nonnegative
CHECK (score IS NULL OR score >= 0);

ALTER TABLE learning.quiz_attempts
ADD CONSTRAINT quiz_attempts_max_score_positive
CHECK (max_score IS NULL OR max_score > 0);

-- Score cannot exceed max score
ALTER TABLE learning.quiz_attempts
ADD CONSTRAINT quiz_attempts_score_within_max
CHECK (score IS NULL OR max_score IS NULL OR score <= max_score);

-- Attempt number must be positive
ALTER TABLE learning.quiz_attempts
ADD CONSTRAINT quiz_attempts_number_positive
CHECK (attempt_number > 0);

-- Submitted must be after started
ALTER TABLE learning.quiz_attempts
ADD CONSTRAINT quiz_attempts_submitted_after_started
CHECK (submitted_at IS NULL OR submitted_at >= started_at);

-- Quiz answer points must be non-negative
ALTER TABLE learning.quiz_answers
ADD CONSTRAINT quiz_answers_points_nonnegative
CHECK (points_earned IS NULL OR points_earned >= 0);

-- Whiteboard element z-index must be non-negative
ALTER TABLE learning.whiteboard_elements
ADD CONSTRAINT whiteboard_elements_zindex_nonnegative
CHECK (z_index >= 0);

-- ============================================
-- 5. ASSESSMENT SCHEMA - Enhanced Constraints
-- ============================================

-- Grade scores must be 0-100
ALTER TABLE assess.grades
ADD CONSTRAINT grades_participation_range
CHECK (participation_score >= 0 AND participation_score <= 100);

ALTER TABLE assess.grades
ADD CONSTRAINT grades_quiz_avg_range
CHECK (quiz_average >= 0 AND quiz_average <= 100);

ALTER TABLE assess.grades
ADD CONSTRAINT grades_assignment_avg_range
CHECK (assignment_average >= 0 AND assignment_average <= 100);

ALTER TABLE assess.grades
ADD CONSTRAINT grades_final_score_range
CHECK (final_score >= 0 AND final_score <= 100);

-- Letter grade validation
ALTER TABLE assess.grades
ADD CONSTRAINT grades_letter_valid
CHECK (letter_grade IS NULL OR letter_grade IN ('A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D+', 'D', 'D-', 'F'));

-- AI grading score must be 0-100
ALTER TABLE assess.ai_gradings
ADD CONSTRAINT ai_gradings_score_range
CHECK (ai_score >= 0 AND ai_score <= 100);

-- Confidence must be 0-1
ALTER TABLE assess.ai_gradings
ADD CONSTRAINT ai_gradings_confidence_range
CHECK (confidence >= 0 AND confidence <= 1);

-- Similarity score must be 0-1
ALTER TABLE assess.ai_gradings
ADD CONSTRAINT ai_gradings_similarity_range
CHECK (similarity_score IS NULL OR (similarity_score >= 0 AND similarity_score <= 1));

-- Final score must be 0-100
ALTER TABLE assess.ai_gradings
ADD CONSTRAINT ai_gradings_final_score_range
CHECK (final_score IS NULL OR (final_score >= 0 AND final_score <= 100));

-- Reviewed timestamp must be set when reviewed
ALTER TABLE assess.ai_gradings
ADD CONSTRAINT ai_gradings_reviewed_timestamp
CHECK (reviewed_by IS NULL OR reviewed_at IS NOT NULL);

-- Code execution counts validation
ALTER TABLE assess.code_executions
ADD CONSTRAINT code_exec_counts_valid
CHECK (passed_count >= 0 AND total_count >= 0 AND passed_count <= total_count);

-- Execution time must be non-negative
ALTER TABLE assess.code_executions
ADD CONSTRAINT code_exec_time_nonnegative
CHECK (execution_time_ms IS NULL OR execution_time_ms >= 0);

-- Memory usage must be non-negative
ALTER TABLE assess.code_executions
ADD CONSTRAINT code_exec_memory_nonnegative
CHECK (memory_used_kb IS NULL OR memory_used_kb >= 0);

-- Status validation
ALTER TABLE assess.code_executions
ADD CONSTRAINT code_exec_status_valid
CHECK (status IN ('pending', 'running', 'completed', 'failed', 'timeout', 'error'));

-- Language validation
ALTER TABLE assess.code_executions
ADD CONSTRAINT code_exec_language_valid
CHECK (language IN ('python', 'java', 'javascript', 'typescript', 'cpp', 'c', 'go', 'rust', 'ruby', 'php', 'swift', 'kotlin'));

-- ============================================
-- 6. ANALYTICS SCHEMA - Enhanced Constraints
-- ============================================

-- Participation metrics must be non-negative
ALTER TABLE analytics.participation_logs
ADD CONSTRAINT participation_talk_time_nonnegative
CHECK (talk_time_sec >= 0);

ALTER TABLE analytics.participation_logs
ADD CONSTRAINT participation_counts_nonnegative
CHECK (chat_count >= 0 AND poll_count >= 0 AND quiz_count >= 0
       AND hand_raise_count >= 0 AND reaction_count >= 0 AND breakout_time_sec >= 0);

-- Engagement score must be 0-100
ALTER TABLE analytics.participation_logs
ADD CONSTRAINT participation_engagement_range
CHECK (engagement_score >= 0 AND engagement_score <= 100);

-- Alert severity validation
ALTER TABLE analytics.alerts
ADD CONSTRAINT alerts_severity_valid
CHECK (severity IN ('low', 'medium', 'high', 'critical'));

-- Resolved timestamp validation
ALTER TABLE analytics.alerts
ADD CONSTRAINT alerts_resolved_timestamp
CHECK (resolved_by IS NULL OR resolved_at IS NOT NULL);

-- Daily stats metrics must be non-negative
ALTER TABLE analytics.daily_stats
ADD CONSTRAINT daily_stats_counts_nonnegative
CHECK (active_users >= 0 AND total_sessions >= 0
       AND total_talk_time_sec >= 0 AND quiz_attempts >= 0);

-- Average engagement must be 0-100
ALTER TABLE analytics.daily_stats
ADD CONSTRAINT daily_stats_engagement_range
CHECK (avg_engagement >= 0 AND avg_engagement <= 100);

-- Average quiz score must be 0-100
ALTER TABLE analytics.daily_stats
ADD CONSTRAINT daily_stats_quiz_score_range
CHECK (avg_quiz_score IS NULL OR (avg_quiz_score >= 0 AND avg_quiz_score <= 100));

-- Stat date cannot be in future
ALTER TABLE analytics.daily_stats
ADD CONSTRAINT daily_stats_date_not_future
CHECK (stat_date <= CURRENT_DATE);

-- ============================================
-- ENHANCED FOREIGN KEY CONSTRAINTS
-- ============================================

-- Review existing ON DELETE behaviors and add missing ones

-- Course enrollments: when user is deleted, remove enrollment
-- Already has CASCADE - OK

-- Session participants: ensure cleanup
-- Already has CASCADE - OK

-- Submissions graded_by: preserve grader reference even if grader account deleted
ALTER TABLE course.submissions
DROP CONSTRAINT IF EXISTS submissions_graded_by_fkey,
ADD CONSTRAINT submissions_graded_by_fkey
FOREIGN KEY (graded_by) REFERENCES auth.users(id) ON DELETE SET NULL;

-- Hand raises called_by: preserve caller reference
ALTER TABLE live.hand_raises
DROP CONSTRAINT IF EXISTS hand_raises_called_by_fkey,
ADD CONSTRAINT hand_raises_called_by_fkey
FOREIGN KEY (called_by) REFERENCES auth.users(id) ON DELETE SET NULL;

-- User roles assigned_by: preserve assigner reference
ALTER TABLE auth.user_roles
DROP CONSTRAINT IF EXISTS user_roles_assigned_by_fkey,
ADD CONSTRAINT user_roles_assigned_by_fkey
FOREIGN KEY (assigned_by) REFERENCES auth.users(id) ON DELETE SET NULL;

-- AI grading reviewed_by: preserve reviewer reference
ALTER TABLE assess.ai_gradings
DROP CONSTRAINT IF EXISTS ai_gradings_reviewed_by_fkey,
ADD CONSTRAINT ai_gradings_reviewed_by_fkey
FOREIGN KEY (reviewed_by) REFERENCES auth.users(id) ON DELETE SET NULL;

-- Alerts resolved_by: preserve resolver reference
ALTER TABLE analytics.alerts
DROP CONSTRAINT IF EXISTS alerts_resolved_by_fkey,
ADD CONSTRAINT alerts_resolved_by_fkey
FOREIGN KEY (resolved_by) REFERENCES auth.users(id) ON DELETE SET NULL;

-- ============================================
-- UNIQUE CONSTRAINTS (additional)
-- ============================================

-- Prevent duplicate active hand raises for same user in session
CREATE UNIQUE INDEX idx_hand_raises_active_unique
ON live.hand_raises(session_id, user_id)
WHERE lowered_at IS NULL AND called_at IS NULL;

-- Prevent duplicate quiz attempts in progress
CREATE UNIQUE INDEX idx_quiz_attempts_in_progress_unique
ON learning.quiz_attempts(quiz_id, user_id)
WHERE status = 'in_progress';

-- Ensure only one grade record per user per course
-- Already exists as CONSTRAINT grades_user_course_unique - OK

-- Ensure only one 2FA record per user
-- Already exists as UNIQUE in table definition - OK

-- Ensure only one notification setting per user
-- Already exists as UNIQUE in table definition - OK

-- ============================================
-- ROLLBACK SCRIPT
-- ============================================
-- To rollback constraints, run:
/*
-- Auth schema
ALTER TABLE auth.users DROP CONSTRAINT IF EXISTS users_email_format_check;
ALTER TABLE auth.users DROP CONSTRAINT IF EXISTS users_phone_format_check;
ALTER TABLE auth.users DROP CONSTRAINT IF EXISTS users_deleted_status_check;
ALTER TABLE auth.oauth_accounts DROP CONSTRAINT IF EXISTS oauth_token_expires_future;
ALTER TABLE auth.refresh_tokens DROP CONSTRAINT IF EXISTS refresh_token_expires_future;
ALTER TABLE auth.refresh_tokens DROP CONSTRAINT IF EXISTS refresh_token_revoked_after_created;
ALTER TABLE auth.password_reset_tokens DROP CONSTRAINT IF EXISTS password_reset_expires_future;
ALTER TABLE auth.password_reset_tokens DROP CONSTRAINT IF EXISTS password_reset_used_after_created;
ALTER TABLE auth.two_factor_auth DROP CONSTRAINT IF EXISTS twofa_verified_when_enabled;

-- Course schema
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

-- Live schema
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

-- Learning schema
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

-- Assessment schema
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

-- Analytics schema
ALTER TABLE analytics.participation_logs DROP CONSTRAINT IF EXISTS participation_talk_time_nonnegative;
ALTER TABLE analytics.participation_logs DROP CONSTRAINT IF EXISTS participation_counts_nonnegative;
ALTER TABLE analytics.participation_logs DROP CONSTRAINT IF EXISTS participation_engagement_range;
ALTER TABLE analytics.alerts DROP CONSTRAINT IF EXISTS alerts_severity_valid;
ALTER TABLE analytics.alerts DROP CONSTRAINT IF EXISTS alerts_resolved_timestamp;
ALTER TABLE analytics.daily_stats DROP CONSTRAINT IF EXISTS daily_stats_counts_nonnegative;
ALTER TABLE analytics.daily_stats DROP CONSTRAINT IF EXISTS daily_stats_engagement_range;
ALTER TABLE analytics.daily_stats DROP CONSTRAINT IF EXISTS daily_stats_quiz_score_range;
ALTER TABLE analytics.daily_stats DROP CONSTRAINT IF EXISTS daily_stats_date_not_future;

-- Unique indexes
DROP INDEX IF EXISTS idx_hand_raises_active_unique;
DROP INDEX IF EXISTS idx_quiz_attempts_in_progress_unique;
*/
