-- ============================================
-- SEED 001: Test Data
-- ============================================
-- Description: Inserts test data for development and testing
-- Author: System
-- Date: 2025-01-28

-- Test Users
INSERT INTO auth.users (email, password_hash, first_name, last_name, status, email_verified_at) VALUES
    -- Admin user
    ('admin@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'Admin', 'User', 'active', NOW()),

    -- Professor users
    ('prof.kim@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'Minsoo', 'Kim', 'active', NOW()),
    ('prof.lee@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'Jieun', 'Lee', 'active', NOW()),

    -- TA users
    ('ta.park@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'Hyunwoo', 'Park', 'active', NOW()),

    -- Student users
    ('student1@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'Soyeon', 'Choi', 'active', NOW()),
    ('student2@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'Jihoon', 'Jung', 'active', NOW()),
    ('student3@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'Minji', 'Kang', 'active', NOW()),
    ('student4@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'Donghyun', 'Yoon', 'active', NOW()),
    ('student5@eduforum.com', '$2a$10$XQrb9Z4aHYC/xL1mYq8KBOKqJ7w8qZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'Seunghee', 'Han', 'active', NOW());

-- Assign roles to users
INSERT INTO auth.user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, 1
FROM auth.users u, auth.roles r
WHERE u.email = 'admin@eduforum.com' AND r.name = 'admin';

INSERT INTO auth.user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, 1
FROM auth.users u, auth.roles r
WHERE u.email IN ('prof.kim@eduforum.com', 'prof.lee@eduforum.com') AND r.name = 'professor';

INSERT INTO auth.user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, 1
FROM auth.users u, auth.roles r
WHERE u.email = 'ta.park@eduforum.com' AND r.name = 'ta';

INSERT INTO auth.user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, 1
FROM auth.users u, auth.roles r
WHERE u.email LIKE 'student%@eduforum.com' AND r.name = 'student';

-- Test Courses
INSERT INTO course.courses (professor_id, code, title, description, semester, year, is_published, invite_code)
SELECT
    u.id,
    'CS101',
    'Introduction to Computer Science',
    'An introductory course covering fundamental concepts of computer science including algorithms, data structures, and programming.',
    'Spring',
    2025,
    TRUE,
    'CS101SPRING'
FROM auth.users u
WHERE u.email = 'prof.kim@eduforum.com';

INSERT INTO course.courses (professor_id, code, title, description, semester, year, is_published, invite_code)
SELECT
    u.id,
    'CS201',
    'Data Structures and Algorithms',
    'Advanced course on data structures and algorithms with focus on complexity analysis and optimization.',
    'Spring',
    2025,
    TRUE,
    'CS201SPRING'
FROM auth.users u
WHERE u.email = 'prof.lee@eduforum.com';

-- Course Enrollments
-- Enroll students in CS101
INSERT INTO course.enrollments (user_id, course_id, role, status)
SELECT u.id, c.id, 'student', 'active'
FROM auth.users u, course.courses c
WHERE u.email LIKE 'student%@eduforum.com' AND c.code = 'CS101';

-- Enroll TA in CS101
INSERT INTO course.enrollments (user_id, course_id, role, status)
SELECT u.id, c.id, 'ta', 'active'
FROM auth.users u, course.courses c
WHERE u.email = 'ta.park@eduforum.com' AND c.code = 'CS101';

-- Enroll some students in CS201
INSERT INTO course.enrollments (user_id, course_id, role, status)
SELECT u.id, c.id, 'student', 'active'
FROM auth.users u, course.courses c
WHERE u.email IN ('student1@eduforum.com', 'student2@eduforum.com', 'student3@eduforum.com')
  AND c.code = 'CS201';

-- Test Sessions
INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT
    c.id,
    'Week 1: Introduction to Programming',
    'Overview of programming concepts and Python basics',
    NOW() + INTERVAL '1 day',
    90,
    'scheduled'
FROM course.courses c
WHERE c.code = 'CS101';

INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT
    c.id,
    'Week 2: Variables and Data Types',
    'Understanding variables, data types, and operators',
    NOW() + INTERVAL '8 days',
    90,
    'scheduled'
FROM course.courses c
WHERE c.code = 'CS101';

INSERT INTO course.sessions (course_id, title, description, scheduled_at, duration_minutes, status)
SELECT
    c.id,
    'Week 1: Advanced Data Structures',
    'Trees, graphs, and hash tables',
    NOW() + INTERVAL '2 days',
    90,
    'scheduled'
FROM course.courses c
WHERE c.code = 'CS201';

-- Test Assignments
INSERT INTO course.assignments (course_id, title, description, due_date, max_score, status, published_at)
SELECT
    c.id,
    'Assignment 1: Hello World Program',
    'Write your first Python program that prints "Hello, World!" and demonstrates basic variable usage.',
    NOW() + INTERVAL '7 days',
    100,
    'published',
    NOW()
FROM course.courses c
WHERE c.code = 'CS101';

INSERT INTO course.assignments (course_id, title, description, due_date, max_score, status, published_at)
SELECT
    c.id,
    'Assignment 1: Binary Search Tree Implementation',
    'Implement a binary search tree with insert, delete, and search operations.',
    NOW() + INTERVAL '14 days',
    100,
    'published',
    NOW()
FROM course.courses c
WHERE c.code = 'CS201';

-- Initialize grades for enrolled students
INSERT INTO assess.grades (user_id, course_id, participation_score, quiz_average, assignment_average, final_score)
SELECT e.user_id, e.course_id, 0, 0, 0, 0
FROM course.enrollments e
WHERE e.role = 'student' AND e.status = 'active';

-- Comments
COMMENT ON TABLE auth.users IS 'Contains test users: 1 admin, 2 professors, 1 TA, 5 students';
COMMENT ON TABLE course.courses IS 'Contains 2 test courses: CS101 and CS201';
COMMENT ON TABLE course.sessions IS 'Contains 3 test sessions across both courses';
