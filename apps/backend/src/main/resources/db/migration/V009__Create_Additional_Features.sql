-- =====================================================
-- Additional Features - Database Schema
-- Version: V009
-- Description: Login attempts, grading criteria, TA assignments
-- =====================================================

-- =====================================================
-- Login Attempts (Auth Domain)
-- =====================================================

-- Login attempts table
CREATE TABLE auth.login_attempts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES auth.users(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    attempt_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    success BOOLEAN NOT NULL DEFAULT FALSE,
    failure_reason VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for login attempts
CREATE INDEX idx_login_attempts_email ON auth.login_attempts(email, attempt_time DESC);
CREATE INDEX idx_login_attempts_user ON auth.login_attempts(user_id, attempt_time DESC);
CREATE INDEX idx_login_attempts_ip ON auth.login_attempts(ip_address, attempt_time DESC);
CREATE INDEX idx_login_attempts_time ON auth.login_attempts(attempt_time DESC);
CREATE INDEX idx_login_attempts_email_success ON auth.login_attempts(email, success, attempt_time DESC);

-- Trigger for updated_at
CREATE TRIGGER update_login_attempts_updated_at
    BEFORE UPDATE ON auth.login_attempts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE auth.login_attempts IS '로그인 시도 기록 - 실패 카운팅 및 계정 잠금 로직용';
COMMENT ON COLUMN auth.login_attempts.success IS '로그인 성공 여부';
COMMENT ON COLUMN auth.login_attempts.failure_reason IS '실패 사유 (잘못된 비밀번호, 계정 잠금 등)';

-- =====================================================
-- Grading Criteria (Course Domain)
-- =====================================================

-- Grading criteria table
CREATE TABLE course.grading_criteria (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    weight DECIMAL(5,2) NOT NULL CHECK (weight >= 0 AND weight <= 100),
    max_score DECIMAL(8,2) NOT NULL CHECK (max_score >= 0),
    order_index INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

-- Rubric items table
CREATE TABLE course.rubric_items (
    id BIGSERIAL PRIMARY KEY,
    criteria_id BIGINT NOT NULL REFERENCES course.grading_criteria(id) ON DELETE CASCADE,
    level VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    score DECIMAL(8,2) NOT NULL CHECK (score >= 0),
    order_index INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

-- Indexes for grading criteria
CREATE INDEX idx_grading_criteria_course ON course.grading_criteria(course_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_grading_criteria_order ON course.grading_criteria(course_id, order_index) WHERE deleted_at IS NULL;

-- Indexes for rubric items
CREATE INDEX idx_rubric_items_criteria ON course.rubric_items(criteria_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_rubric_items_order ON course.rubric_items(criteria_id, order_index) WHERE deleted_at IS NULL;

-- Triggers for updated_at
CREATE TRIGGER update_grading_criteria_updated_at
    BEFORE UPDATE ON course.grading_criteria
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_rubric_items_updated_at
    BEFORE UPDATE ON course.rubric_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE course.grading_criteria IS '평가 기준 - 코스별 채점 기준 정의';
COMMENT ON COLUMN course.grading_criteria.weight IS '평가 기준 가중치 (%)';
COMMENT ON COLUMN course.grading_criteria.max_score IS '최대 점수';
COMMENT ON COLUMN course.grading_criteria.order_index IS '표시 순서';
COMMENT ON TABLE course.rubric_items IS '루브릭 항목 - 평가 기준별 상세 채점 레벨';
COMMENT ON COLUMN course.rubric_items.level IS '평가 레벨 (예: Excellent, Good, Fair, Poor)';
COMMENT ON COLUMN course.rubric_items.score IS '해당 레벨의 점수';

-- =====================================================
-- TA Assignments (Course Domain)
-- =====================================================

-- Course TAs table
CREATE TABLE course.course_tas (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    ta_user_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    assigned_by BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    permissions JSONB NOT NULL DEFAULT '{
        "canGrade": true,
        "canManageStudents": true,
        "canManageSessions": false,
        "canManageAssignments": true,
        "canViewAnalytics": true,
        "canModerateDiscussions": true
    }'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    CONSTRAINT unique_course_ta UNIQUE(course_id, ta_user_id)
);

-- Indexes for course TAs
CREATE INDEX idx_course_tas_course ON course.course_tas(course_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_course_tas_ta_user ON course.course_tas(ta_user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_course_tas_assigned_by ON course.course_tas(assigned_by) WHERE deleted_at IS NULL;

-- Trigger for updated_at
CREATE TRIGGER update_course_tas_updated_at
    BEFORE UPDATE ON course.course_tas
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE course.course_tas IS 'TA 배정 - 코스별 TA 할당 및 권한 관리';
COMMENT ON COLUMN course.course_tas.ta_user_id IS 'TA로 배정된 사용자 ID';
COMMENT ON COLUMN course.course_tas.assigned_by IS 'TA를 배정한 교수 ID';
COMMENT ON COLUMN course.course_tas.permissions IS 'TA 권한 설정 (JSONB)';

-- =====================================================
-- Constraints & Validations
-- =====================================================

-- Ensure TA user has TA role (commented out - to be implemented in application logic)
-- This would require a trigger or application-level validation

-- Ensure grading criteria weights sum doesn't exceed 100% per course
-- (Application-level validation recommended)

-- =====================================================
-- Initial Data / Cleanup
-- =====================================================

-- No initial data needed for these tables
