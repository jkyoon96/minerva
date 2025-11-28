-- ============================================
-- Rollback Script for All Seed Data
-- ============================================
-- Description: Removes all test data inserted by seed scripts
-- Author: System
-- Date: 2025-01-29
-- Usage: Execute this script to clean up test database

-- ============================================
-- ROLLBACK ORDER (역순 실행)
-- ============================================

-- 1. Analytics data
DELETE FROM analytics.daily_stats
WHERE course_id IN (
    SELECT id FROM course.courses
    WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
);

DELETE FROM analytics.interaction_logs
WHERE session_id IN (
    SELECT id FROM course.sessions
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM analytics.participation_logs
WHERE session_id IN (
    SELECT id FROM course.sessions
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM analytics.alerts
WHERE course_id IN (
    SELECT id FROM course.courses
    WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
);

DELETE FROM analytics.notification_settings
WHERE user_id IN (SELECT id FROM auth.users);

-- 2. Learning data
DELETE FROM learning.quiz_answers
WHERE attempt_id IN (
    SELECT id FROM learning.quiz_attempts
    WHERE quiz_id IN (
        SELECT id FROM learning.quizzes
        WHERE course_id IN (
            SELECT id FROM course.courses
            WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
        )
    )
);

DELETE FROM learning.quiz_attempts
WHERE quiz_id IN (
    SELECT id FROM learning.quizzes
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM learning.questions
WHERE quiz_id IN (
    SELECT id FROM learning.quizzes
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM learning.quizzes
WHERE course_id IN (
    SELECT id FROM course.courses
    WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
);

DELETE FROM learning.poll_votes
WHERE poll_option_id IN (
    SELECT id FROM learning.poll_options
    WHERE poll_id IN (SELECT id FROM learning.polls)
);

DELETE FROM learning.poll_options;

DELETE FROM learning.polls
WHERE session_id IN (
    SELECT id FROM course.sessions
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM learning.whiteboard_elements
WHERE whiteboard_id IN (
    SELECT id FROM learning.whiteboards
    WHERE session_id IN (
        SELECT id FROM course.sessions
        WHERE course_id IN (
            SELECT id FROM course.courses
            WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
        )
    )
);

DELETE FROM learning.whiteboards
WHERE session_id IN (
    SELECT id FROM course.sessions
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

-- 3. Assessment data
DELETE FROM assess.peer_evaluations
WHERE submission_id IN (
    SELECT id FROM course.submissions
    WHERE assignment_id IN (
        SELECT id FROM course.assignments
        WHERE course_id IN (
            SELECT id FROM course.courses
            WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
        )
    )
);

DELETE FROM assess.ai_gradings
WHERE submission_id IN (
    SELECT id FROM course.submissions
    WHERE assignment_id IN (
        SELECT id FROM course.assignments
        WHERE course_id IN (
            SELECT id FROM course.courses
            WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
        )
    )
);

DELETE FROM assess.grades
WHERE course_id IN (
    SELECT id FROM course.courses
    WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
);

-- 4. Course data
DELETE FROM course.submissions
WHERE assignment_id IN (
    SELECT id FROM course.assignments
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM course.assignments
WHERE course_id IN (
    SELECT id FROM course.courses
    WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
);

DELETE FROM course.contents
WHERE session_id IN (
    SELECT id FROM course.sessions
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM live.reactions
WHERE session_id IN (
    SELECT id FROM course.sessions
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM live.chats
WHERE session_id IN (
    SELECT id FROM course.sessions
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM live.breakout_participants
WHERE breakout_room_id IN (
    SELECT id FROM live.breakout_rooms
    WHERE session_id IN (
        SELECT id FROM course.sessions
        WHERE course_id IN (
            SELECT id FROM course.courses
            WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
        )
    )
);

DELETE FROM live.breakout_rooms
WHERE session_id IN (
    SELECT id FROM course.sessions
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM live.session_participants
WHERE session_id IN (
    SELECT id FROM course.sessions
    WHERE course_id IN (
        SELECT id FROM course.courses
        WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
    )
);

DELETE FROM course.sessions
WHERE course_id IN (
    SELECT id FROM course.courses
    WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
);

DELETE FROM course.enrollments
WHERE course_id IN (
    SELECT id FROM course.courses
    WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')
);

DELETE FROM course.courses
WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202');

-- 5. Auth data
DELETE FROM auth.two_factor_auth
WHERE user_id IN (
    SELECT id FROM auth.users
    WHERE email LIKE '%@eduforum.com'
);

DELETE FROM auth.refresh_tokens
WHERE user_id IN (
    SELECT id FROM auth.users
    WHERE email LIKE '%@eduforum.com'
);

DELETE FROM auth.oauth_accounts
WHERE user_id IN (
    SELECT id FROM auth.users
    WHERE email LIKE '%@eduforum.com'
);

DELETE FROM auth.user_roles
WHERE user_id IN (
    SELECT id FROM auth.users
    WHERE email LIKE '%@eduforum.com'
);

DELETE FROM auth.users
WHERE email LIKE '%@eduforum.com';

-- ============================================
-- Verify Cleanup
-- ============================================

SELECT 'Remaining test users:' AS info, COUNT(*) AS count FROM auth.users WHERE email LIKE '%@eduforum.com';
SELECT 'Remaining test courses:' AS info, COUNT(*) AS count FROM course.courses WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202');
SELECT 'Remaining enrollments:' AS info, COUNT(*) AS count FROM course.enrollments;
SELECT 'Remaining sessions:' AS info, COUNT(*) AS count FROM course.sessions;
SELECT 'Remaining polls:' AS info, COUNT(*) AS count FROM learning.polls;
SELECT 'Remaining quizzes:' AS info, COUNT(*) AS count FROM learning.quizzes;
SELECT 'Remaining grades:' AS info, COUNT(*) AS count FROM assess.grades;
SELECT 'Remaining alerts:' AS info, COUNT(*) AS count FROM analytics.alerts;

-- ============================================
-- Reset Sequences (Optional)
-- ============================================
-- Uncomment if you want to reset auto-increment sequences

-- SELECT setval('auth.users_id_seq', 1, false);
-- SELECT setval('course.courses_id_seq', 1, false);
-- SELECT setval('course.sessions_id_seq', 1, false);
-- SELECT setval('learning.polls_id_seq', 1, false);
-- SELECT setval('learning.quizzes_id_seq', 1, false);
-- SELECT setval('analytics.alerts_id_seq', 1, false);
