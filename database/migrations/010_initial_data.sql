-- ============================================
-- MIGRATION 010: Initial Data
-- ============================================
-- Description: Inserts initial roles, permissions, and role-permission mappings
-- Author: System
-- Date: 2025-01-28

-- Insert roles
INSERT INTO auth.roles (name, description) VALUES
    ('admin', '시스템 관리자 - 전체 시스템 관리 권한'),
    ('professor', '교수 - 코스 생성, 세션 진행, 성적 관리'),
    ('ta', '조교 - 코스 관리 보조, 채점 보조'),
    ('student', '학생 - 코스 수강, 세션 참여');

-- Insert permissions
INSERT INTO auth.permissions (name, resource, action, description) VALUES
    ('user:read', 'user', 'read', '사용자 조회'),
    ('user:write', 'user', 'write', '사용자 생성/수정'),
    ('user:delete', 'user', 'delete', '사용자 삭제'),
    ('course:read', 'course', 'read', '코스 조회'),
    ('course:write', 'course', 'write', '코스 생성/수정'),
    ('course:delete', 'course', 'delete', '코스 삭제'),
    ('session:read', 'session', 'read', '세션 조회'),
    ('session:write', 'session', 'write', '세션 시작/종료'),
    ('grade:read', 'grade', 'read', '성적 조회'),
    ('grade:write', 'grade', 'write', '성적 입력/수정'),
    ('analytics:read', 'analytics', 'read', '분석 데이터 조회'),
    ('poll:read', 'poll', 'read', '투표 조회'),
    ('poll:write', 'poll', 'write', '투표 생성/관리'),
    ('quiz:read', 'quiz', 'read', '퀴즈 조회'),
    ('quiz:write', 'quiz', 'write', '퀴즈 생성/관리'),
    ('chat:read', 'chat', 'read', '채팅 조회'),
    ('chat:write', 'chat', 'write', '채팅 전송');

-- Map all permissions to admin role
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM auth.roles r, auth.permissions p
WHERE r.name = 'admin';

-- Map professor permissions
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM auth.roles r, auth.permissions p
WHERE r.name = 'professor'
  AND p.name IN (
    'course:read', 'course:write',
    'session:read', 'session:write',
    'grade:read', 'grade:write',
    'analytics:read',
    'poll:read', 'poll:write',
    'quiz:read', 'quiz:write',
    'chat:read', 'chat:write'
  );

-- Map TA permissions
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM auth.roles r, auth.permissions p
WHERE r.name = 'ta'
  AND p.name IN (
    'course:read',
    'session:read',
    'grade:read', 'grade:write',
    'poll:read',
    'quiz:read',
    'chat:read', 'chat:write'
  );

-- Map student permissions
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM auth.roles r, auth.permissions p
WHERE r.name = 'student'
  AND p.name IN (
    'course:read',
    'session:read',
    'grade:read',
    'poll:read',
    'quiz:read',
    'chat:read', 'chat:write'
  );

-- Set search path for all schemas
ALTER DATABASE eduforum SET search_path TO auth, course, live, learning, assess, analytics, public;
