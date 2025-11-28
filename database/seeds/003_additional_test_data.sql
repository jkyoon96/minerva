-- ============================================
-- SEED 003: Additional Test Data
-- ============================================
-- Description: Expands test data with more users, courses, and learning activities
-- Author: System
-- Date: 2025-01-29

-- ============================================
-- Additional Test Users (총 20명으로 확장)
-- ============================================

-- 추가 교수 1명
INSERT INTO auth.users (email, password_hash, first_name, last_name, status, email_verified_at) VALUES
    ('prof.choi@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '승민', 'Choi', 'active', NOW());

-- 추가 조교 2명
INSERT INTO auth.users (email, password_hash, first_name, last_name, status, email_verified_at) VALUES
    ('ta.kim@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '영희', 'Kim', 'active', NOW()),
    ('ta.lee@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '철수', 'Lee', 'active', NOW());

-- 추가 학생 8명 (총 13명)
INSERT INTO auth.users (email, password_hash, first_name, last_name, status, email_verified_at) VALUES
    ('student6@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '유진', 'Song', 'active', NOW()),
    ('student7@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '준호', 'Lim', 'active', NOW()),
    ('student8@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '서연', 'Shin', 'active', NOW()),
    ('student9@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '민재', 'Oh', 'active', NOW()),
    ('student10@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '지우', 'Kwon', 'active', NOW()),
    ('student11@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '태양', 'Baek', 'active', NOW()),
    ('student12@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '수빈', 'Nam', 'active', NOW()),
    ('student13@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '하늘', 'Go', 'active', NOW());

-- 역할 할당
INSERT INTO auth.user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, 1
FROM auth.users u, auth.roles r
WHERE u.email = 'prof.choi@eduforum.com' AND r.name = 'professor';

INSERT INTO auth.user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, 1
FROM auth.users u, auth.roles r
WHERE u.email IN ('ta.kim@eduforum.com', 'ta.lee@eduforum.com') AND r.name = 'ta';

INSERT INTO auth.user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, 1
FROM auth.users u, auth.roles r
WHERE u.email IN (
    'student6@eduforum.com', 'student7@eduforum.com', 'student8@eduforum.com',
    'student9@eduforum.com', 'student10@eduforum.com', 'student11@eduforum.com',
    'student12@eduforum.com', 'student13@eduforum.com'
) AND r.name = 'student';

-- ============================================
-- 추가 코스 (총 5개)
-- ============================================

INSERT INTO course.courses (professor_id, code, title, description, semester, year, is_published, invite_code)
SELECT
    u.id,
    'MATH201',
    '선형대수학',
    '벡터 공간, 선형 변환, 고유값 및 고유벡터에 대한 심화 과정',
    'Spring',
    2025,
    TRUE,
    'MATH201SPRING'
FROM auth.users u
WHERE u.email = 'prof.choi@eduforum.com';

INSERT INTO course.courses (professor_id, code, title, description, semester, year, is_published, invite_code)
SELECT
    u.id,
    'CS301',
    '운영체제',
    '프로세스 관리, 메모리 관리, 파일 시스템 등 운영체제의 핵심 개념',
    'Spring',
    2025,
    TRUE,
    'CS301SPRING'
FROM auth.users u
WHERE u.email = 'prof.kim@eduforum.com';

INSERT INTO course.courses (professor_id, code, title, description, semester, year, is_published, invite_code)
SELECT
    u.id,
    'CS202',
    '데이터베이스 시스템',
    'SQL, 데이터베이스 설계, 정규화, 트랜잭션 관리',
    'Spring',
    2025,
    TRUE,
    'CS202SPRING'
FROM auth.users u
WHERE u.email = 'prof.lee@eduforum.com';

-- ============================================
-- 코스 등록 (Enrollments)
-- ============================================

-- MATH201에 학생 8명 등록
INSERT INTO course.enrollments (user_id, course_id, role, status)
SELECT u.id, c.id, 'student', 'active'
FROM auth.users u, course.courses c
WHERE u.email IN (
    'student1@eduforum.com', 'student2@eduforum.com', 'student5@eduforum.com',
    'student6@eduforum.com', 'student7@eduforum.com', 'student8@eduforum.com',
    'student9@eduforum.com', 'student10@eduforum.com'
) AND c.code = 'MATH201';

-- MATH201에 조교 배정
INSERT INTO course.enrollments (user_id, course_id, role, status)
SELECT u.id, c.id, 'ta', 'active'
FROM auth.users u, course.courses c
WHERE u.email = 'ta.kim@eduforum.com' AND c.code = 'MATH201';

-- CS301에 학생 6명 등록
INSERT INTO course.enrollments (user_id, course_id, role, status)
SELECT u.id, c.id, 'student', 'active'
FROM auth.users u, course.courses c
WHERE u.email IN (
    'student3@eduforum.com', 'student4@eduforum.com', 'student6@eduforum.com',
    'student11@eduforum.com', 'student12@eduforum.com', 'student13@eduforum.com'
) AND c.code = 'CS301';

-- CS301에 조교 배정
INSERT INTO course.enrollments (user_id, course_id, role, status)
SELECT u.id, c.id, 'ta', 'active'
FROM auth.users u, course.courses c
WHERE u.email = 'ta.lee@eduforum.com' AND c.code = 'CS301';

-- CS202에 학생 10명 등록
INSERT INTO course.enrollments (user_id, course_id, role, status)
SELECT u.id, c.id, 'student', 'active'
FROM auth.users u, course.courses c
WHERE u.email IN (
    'student1@eduforum.com', 'student4@eduforum.com', 'student5@eduforum.com',
    'student7@eduforum.com', 'student8@eduforum.com', 'student9@eduforum.com',
    'student10@eduforum.com', 'student11@eduforum.com', 'student12@eduforum.com',
    'student13@eduforum.com'
) AND c.code = 'CS202';

-- CS202에 조교 배정
INSERT INTO course.enrollments (user_id, course_id, role, status)
SELECT u.id, c.id, 'ta', 'active'
FROM auth.users u, course.courses c
WHERE u.email = 'ta.park@eduforum.com' AND c.code = 'CS202';

-- ============================================
-- 추가 세션 데이터
-- ============================================

-- CS101 추가 세션
INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT c.id, 'Week 3: 조건문과 반복문', '제어 구조와 논리적 사고', NOW() + INTERVAL '15 days', 90, 'scheduled'
FROM course.courses c WHERE c.code = 'CS101';

INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT c.id, 'Week 4: 함수와 모듈', '코드 재사용과 모듈화', NOW() + INTERVAL '22 days', 90, 'scheduled'
FROM course.courses c WHERE c.code = 'CS101';

-- CS201 세션
INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT c.id, 'Week 2: 동적 프로그래밍', '메모이제이션과 타뷸레이션', NOW() + INTERVAL '9 days', 90, 'scheduled'
FROM course.courses c WHERE c.code = 'CS201';

-- MATH201 세션
INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT c.id, 'Week 1: 벡터와 벡터 공간', '벡터의 기본 연산과 선형 결합', NOW() + INTERVAL '3 days', 90, 'scheduled'
FROM course.courses c WHERE c.code = 'MATH201';

INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT c.id, 'Week 2: 행렬과 행렬 연산', '행렬 곱셈과 역행렬', NOW() + INTERVAL '10 days', 90, 'scheduled'
FROM course.courses c WHERE c.code = 'MATH201';

-- CS301 세션
INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT c.id, 'Week 1: 운영체제 개론', '운영체제의 역할과 구조', NOW() + INTERVAL '4 days', 90, 'scheduled'
FROM course.courses c WHERE c.code = 'CS301';

-- CS202 세션
INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT c.id, 'Week 1: 데이터베이스 설계', 'ER 모델과 관계형 모델', NOW() + INTERVAL '5 days', 90, 'scheduled'
FROM course.courses c WHERE c.code = 'CS202';

-- ============================================
-- 투표 샘플 데이터
-- ============================================

-- CS101 세션의 투표
INSERT INTO learning.polls (session_id, created_by, question, type, is_anonymous, show_results, status)
SELECT
    s.id,
    u.id,
    '파이썬을 처음 배우는 분들은 몇 명인가요?',
    'single',
    TRUE,
    TRUE,
    'draft'
FROM course.sessions s
JOIN course.courses c ON s.course_id = c.id
JOIN auth.users u ON c.professor_id = u.id
WHERE c.code = 'CS101' AND s.title LIKE 'Week 1:%';

-- 투표 옵션
INSERT INTO learning.poll_options (poll_id, option_text, order_index)
SELECT p.id, '처음 배웁니다', 0
FROM learning.polls p
WHERE p.question LIKE '%파이썬%';

INSERT INTO learning.poll_options (poll_id, option_text, order_index)
SELECT p.id, '조금 배워봤습니다', 1
FROM learning.polls p
WHERE p.question LIKE '%파이썬%';

INSERT INTO learning.poll_options (poll_id, option_text, order_index)
SELECT p.id, '어느 정도 할 줄 압니다', 2
FROM learning.polls p
WHERE p.question LIKE '%파이썬%';

-- CS201 세션의 투표
INSERT INTO learning.polls (session_id, created_by, question, type, is_anonymous, show_results, status)
SELECT
    s.id,
    u.id,
    '이번 주제에서 가장 어려운 부분은?',
    'single',
    FALSE,
    TRUE,
    'draft'
FROM course.sessions s
JOIN course.courses c ON s.course_id = c.id
JOIN auth.users u ON c.professor_id = u.id
WHERE c.code = 'CS201' AND s.title LIKE 'Week 1:%';

-- ============================================
-- 퀴즈 샘플 데이터
-- ============================================

-- CS101 퀴즈
INSERT INTO learning.quizzes (
    course_id, created_by, title, description,
    time_limit_sec, show_answers, passing_score, status
)
SELECT
    c.id,
    u.id,
    'Week 1 Quiz: 프로그래밍 기초',
    '변수, 데이터 타입, 기본 연산자에 대한 퀴즈',
    600,
    TRUE,
    70,
    'draft'
FROM course.courses c
JOIN auth.users u ON c.professor_id = u.id
WHERE c.code = 'CS101';

-- 퀴즈 문제 (객관식)
INSERT INTO learning.questions (
    quiz_id, course_id, question_text, type, options, correct_answer, points, difficulty, order_index
)
SELECT
    q.id,
    c.id,
    '다음 중 Python의 기본 데이터 타입이 아닌 것은?',
    'multiple_choice',
    '["int", "float", "string", "array"]'::jsonb,
    '["array"]'::jsonb,
    10,
    'easy',
    0
FROM learning.quizzes q
JOIN course.courses c ON q.course_id = c.id
WHERE c.code = 'CS101' AND q.title LIKE 'Week 1%';

INSERT INTO learning.questions (
    quiz_id, course_id, question_text, type, options, correct_answer, points, difficulty, order_index
)
SELECT
    q.id,
    c.id,
    '변수명으로 적절하지 않은 것은?',
    'multiple_choice',
    '["my_var", "myVar", "2myvar", "_myvar"]'::jsonb,
    '["2myvar"]'::jsonb,
    10,
    'easy',
    1
FROM learning.quizzes q
JOIN course.courses c ON q.course_id = c.id
WHERE c.code = 'CS101' AND q.title LIKE 'Week 1%';

-- CS201 퀴즈
INSERT INTO learning.quizzes (
    course_id, created_by, title, description,
    time_limit_sec, show_answers, passing_score, status
)
SELECT
    c.id,
    u.id,
    '자료구조 중간고사',
    '배열, 연결 리스트, 스택, 큐에 대한 종합 평가',
    1800,
    FALSE,
    60,
    'draft'
FROM course.courses c
JOIN auth.users u ON c.professor_id = u.id
WHERE c.code = 'CS201';

-- ============================================
-- 분석 로그 샘플 데이터
-- ============================================

-- 참여 로그 (과거 세션 기록)
INSERT INTO analytics.participation_logs (
    session_id, user_id, talk_time_sec, chat_count,
    poll_count, quiz_count, engagement_score, recorded_at
)
SELECT
    s.id,
    e.user_id,
    FLOOR(RANDOM() * 300 + 60)::INTEGER,  -- 1~5분 발언
    FLOOR(RANDOM() * 10 + 1)::INTEGER,     -- 1~10개 채팅
    FLOOR(RANDOM() * 3)::INTEGER,          -- 0~2개 투표 참여
    FLOOR(RANDOM() * 2)::INTEGER,          -- 0~1개 퀴즈 참여
    (RANDOM() * 40 + 60)::DECIMAL(5,2),    -- 60~100점 참여도
    NOW() - INTERVAL '3 days'
FROM course.sessions s
JOIN course.enrollments e ON s.course_id = e.course_id
WHERE e.role = 'student'
  AND s.course_id IN (SELECT id FROM course.courses WHERE code IN ('CS101', 'CS201'))
LIMIT 30;

-- 상호작용 로그
INSERT INTO analytics.interaction_logs (
    session_id, from_user_id, to_user_id, interaction_type, context
)
SELECT
    s.id,
    e1.user_id,
    e2.user_id,
    'chat_reply',
    '{"message": "좋은 질문이네요!"}'::jsonb
FROM course.sessions s
JOIN course.enrollments e1 ON s.course_id = e1.course_id
JOIN course.enrollments e2 ON s.course_id = e2.course_id
WHERE e1.role = 'student' AND e2.role = 'student' AND e1.user_id != e2.user_id
  AND s.course_id IN (SELECT id FROM course.courses WHERE code = 'CS101')
LIMIT 20;

-- 경고 알림
INSERT INTO analytics.alerts (
    user_id, course_id, type, severity, message, data
)
SELECT
    e.user_id,
    c.id,
    'low_participation',
    'medium',
    '최근 3개 세션에서 참여도가 낮습니다.',
    '{"sessions_count": 3, "avg_engagement": 45.2}'::jsonb
FROM course.courses c
JOIN course.enrollments e ON c.id = e.course_id
WHERE c.code = 'CS101' AND e.role = 'student'
LIMIT 2;

-- 일일 통계
INSERT INTO analytics.daily_stats (
    course_id, stat_date, active_users, total_sessions,
    total_talk_time_sec, avg_engagement, quiz_attempts, avg_quiz_score
)
SELECT
    c.id,
    CURRENT_DATE - INTERVAL '1 day',
    15,
    2,
    4500,
    75.5,
    12,
    82.3
FROM course.courses c
WHERE c.code = 'CS101';

-- 성적 초기화
INSERT INTO assess.grades (user_id, course_id, participation_score, quiz_average, assignment_average, final_score)
SELECT e.user_id, e.course_id, 0, 0, 0, 0
FROM course.enrollments e
WHERE e.role = 'student' AND e.status = 'active'
  AND NOT EXISTS (
    SELECT 1 FROM assess.grades g
    WHERE g.user_id = e.user_id AND g.course_id = e.course_id
  );

-- Comments
COMMENT ON TABLE auth.users IS 'Contains 20 test users: 1 admin, 3 professors, 3 TAs, 13 students';
COMMENT ON TABLE course.courses IS 'Contains 5 test courses across different subjects';
COMMENT ON TABLE learning.polls IS 'Contains sample polls for active learning';
COMMENT ON TABLE learning.quizzes IS 'Contains sample quizzes with various difficulty levels';
COMMENT ON TABLE analytics.participation_logs IS 'Contains sample participation tracking data';

-- ============================================
-- Rollback Script
-- ============================================
-- Execute these commands in reverse order to rollback

-- DELETE FROM analytics.daily_stats WHERE course_id IN (SELECT id FROM course.courses WHERE code IN ('MATH201', 'CS301', 'CS202'));
-- DELETE FROM analytics.alerts WHERE course_id IN (SELECT id FROM course.courses WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202'));
-- DELETE FROM analytics.interaction_logs WHERE session_id IN (SELECT id FROM course.sessions WHERE course_id IN (SELECT id FROM course.courses WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202')));
-- DELETE FROM analytics.participation_logs WHERE session_id IN (SELECT id FROM course.sessions WHERE course_id IN (SELECT id FROM course.courses WHERE code IN ('CS101', 'CS201')));
-- DELETE FROM learning.questions WHERE quiz_id IN (SELECT id FROM learning.quizzes WHERE course_id IN (SELECT id FROM course.courses WHERE code IN ('CS101', 'CS201')));
-- DELETE FROM learning.quizzes WHERE course_id IN (SELECT id FROM course.courses WHERE code IN ('CS101', 'CS201'));
-- DELETE FROM learning.poll_options WHERE poll_id IN (SELECT id FROM learning.polls);
-- DELETE FROM learning.polls WHERE session_id IN (SELECT id FROM course.sessions WHERE course_id IN (SELECT id FROM course.courses WHERE code IN ('CS101', 'CS201')));
-- DELETE FROM assess.grades WHERE user_id IN (SELECT id FROM auth.users WHERE email LIKE 'student%@eduforum.com' OR email LIKE 'ta.%@eduforum.com');
-- DELETE FROM course.sessions WHERE course_id IN (SELECT id FROM course.courses WHERE code IN ('CS101', 'CS201', 'MATH201', 'CS301', 'CS202'));
-- DELETE FROM course.enrollments WHERE course_id IN (SELECT id FROM course.courses WHERE code IN ('MATH201', 'CS301', 'CS202'));
-- DELETE FROM course.courses WHERE code IN ('MATH201', 'CS301', 'CS202');
-- DELETE FROM auth.user_roles WHERE user_id IN (SELECT id FROM auth.users WHERE email IN ('prof.choi@eduforum.com', 'ta.kim@eduforum.com', 'ta.lee@eduforum.com') OR email LIKE 'student%@eduforum.com' AND id > (SELECT MAX(id) FROM auth.users WHERE email = 'student5@eduforum.com'));
-- DELETE FROM auth.users WHERE email IN ('prof.choi@eduforum.com', 'ta.kim@eduforum.com', 'ta.lee@eduforum.com') OR email IN ('student6@eduforum.com', 'student7@eduforum.com', 'student8@eduforum.com', 'student9@eduforum.com', 'student10@eduforum.com', 'student11@eduforum.com', 'student12@eduforum.com', 'student13@eduforum.com');
