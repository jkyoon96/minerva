-- V005__Create_Assessment_Schema.sql
-- E5: Assessment & Feedback Domain Schema

-- Create assessment schema
CREATE SCHEMA IF NOT EXISTS assessment;

-- Create enums
CREATE TYPE grading_type AS ENUM ('AUTO', 'AI', 'MANUAL', 'PEER');
CREATE TYPE grading_status AS ENUM ('PENDING', 'GRADED', 'REVIEWED', 'FINALIZED');
CREATE TYPE submission_status AS ENUM ('SUBMITTED', 'RUNNING', 'COMPLETED', 'ERROR', 'COMPILE_ERROR');
CREATE TYPE execution_status AS ENUM ('SUCCESS', 'FAILED', 'TIMEOUT', 'MEMORY_LIMIT', 'RUNTIME_ERROR', 'WRONG_ANSWER');
CREATE TYPE event_type AS ENUM ('ATTENDANCE', 'POLL_RESPONSE', 'QUIZ_COMPLETE', 'ASSIGNMENT_SUBMIT', 'DISCUSSION_PARTICIPATE', 'BREAKOUT_PARTICIPATE', 'WHITEBOARD_CONTRIBUTE', 'CHAT_MESSAGE', 'VIDEO_ON', 'RAISE_HAND');
CREATE TYPE feedback_type AS ENUM ('PERFORMANCE', 'IMPROVEMENT', 'RESOURCE', 'ENCOURAGEMENT', 'WARNING');

-- ============================================
-- Grading Tables
-- ============================================

-- Grading results table
CREATE TABLE assessment.grading_results (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    submission_id BIGINT,
    student_id BIGINT NOT NULL,
    grader_id BIGINT,
    grading_type grading_type NOT NULL,
    status grading_status NOT NULL DEFAULT 'PENDING',
    score NUMERIC(5, 2),
    max_score NUMERIC(5, 2),
    ai_confidence NUMERIC(5, 2),
    feedback TEXT,
    grading_details JSONB DEFAULT '{}',
    graded_at TIMESTAMPTZ,
    reviewed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_grading_results_assignment ON assessment.grading_results(assignment_id);
CREATE INDEX idx_grading_results_student ON assessment.grading_results(student_id);
CREATE INDEX idx_grading_results_submission ON assessment.grading_results(submission_id);
CREATE INDEX idx_grading_results_status ON assessment.grading_results(status);

-- Answer statistics table
CREATE TABLE assessment.answer_statistics (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    total_responses INTEGER NOT NULL DEFAULT 0,
    correct_responses INTEGER NOT NULL DEFAULT 0,
    incorrect_responses INTEGER NOT NULL DEFAULT 0,
    option_distribution JSONB DEFAULT '{}',
    response_time_avg JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(quiz_id, question_id)
);

CREATE INDEX idx_answer_statistics_quiz ON assessment.answer_statistics(quiz_id);
CREATE INDEX idx_answer_statistics_question ON assessment.answer_statistics(question_id);

-- ============================================
-- Code Evaluation Tables
-- ============================================

-- Code submissions table
CREATE TABLE assessment.code_submissions (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    language VARCHAR(50) NOT NULL,
    code TEXT NOT NULL,
    status submission_status NOT NULL DEFAULT 'SUBMITTED',
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    executed_at TIMESTAMPTZ,
    passed_tests INTEGER DEFAULT 0,
    total_tests INTEGER DEFAULT 0,
    execution_time_ms BIGINT,
    memory_used_kb BIGINT,
    compiler_output JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_code_submissions_assignment ON assessment.code_submissions(assignment_id);
CREATE INDEX idx_code_submissions_student ON assessment.code_submissions(student_id);
CREATE INDEX idx_code_submissions_status ON assessment.code_submissions(status);

-- Test cases table
CREATE TABLE assessment.test_cases (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    test_name VARCHAR(100) NOT NULL,
    input_data TEXT,
    expected_output TEXT,
    is_hidden BOOLEAN NOT NULL DEFAULT FALSE,
    points INTEGER,
    time_limit_ms INTEGER,
    memory_limit_kb INTEGER,
    display_order INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_test_cases_assignment ON assessment.test_cases(assignment_id);
CREATE INDEX idx_test_cases_order ON assessment.test_cases(assignment_id, display_order);

-- Execution results table
CREATE TABLE assessment.execution_results (
    id BIGSERIAL PRIMARY KEY,
    submission_id BIGINT NOT NULL REFERENCES assessment.code_submissions(id) ON DELETE CASCADE,
    test_case_id BIGINT NOT NULL REFERENCES assessment.test_cases(id) ON DELETE CASCADE,
    status execution_status NOT NULL,
    actual_output TEXT,
    error_message TEXT,
    execution_time_ms BIGINT,
    memory_used_kb BIGINT,
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_execution_results_submission ON assessment.execution_results(submission_id);
CREATE INDEX idx_execution_results_test_case ON assessment.execution_results(test_case_id);

-- Plagiarism reports table
CREATE TABLE assessment.plagiarism_reports (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    submission_id_1 BIGINT NOT NULL,
    submission_id_2 BIGINT NOT NULL,
    student_id_1 BIGINT NOT NULL,
    student_id_2 BIGINT NOT NULL,
    similarity_score NUMERIC(5, 2) NOT NULL,
    algorithm VARCHAR(50),
    matched_segments JSONB,
    analysis_details JSONB,
    is_flagged BOOLEAN NOT NULL DEFAULT FALSE,
    reviewed_by BIGINT,
    reviewed_at TIMESTAMPTZ,
    review_notes TEXT,
    checked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_plagiarism_reports_assignment ON assessment.plagiarism_reports(assignment_id);
CREATE INDEX idx_plagiarism_reports_submission1 ON assessment.plagiarism_reports(submission_id_1);
CREATE INDEX idx_plagiarism_reports_submission2 ON assessment.plagiarism_reports(submission_id_2);
CREATE INDEX idx_plagiarism_reports_flagged ON assessment.plagiarism_reports(is_flagged);
CREATE INDEX idx_plagiarism_reports_similarity ON assessment.plagiarism_reports(similarity_score DESC);

-- ============================================
-- Feedback Tables
-- ============================================

-- Feedbacks table
CREATE TABLE assessment.feedbacks (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    submission_id BIGINT,
    grading_result_id BIGINT,
    feedback_type feedback_type NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_ai_generated BOOLEAN NOT NULL DEFAULT FALSE,
    generated_by BIGINT,
    metadata JSONB DEFAULT '{}',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMPTZ,
    sent_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_feedbacks_student ON assessment.feedbacks(student_id);
CREATE INDEX idx_feedbacks_course ON assessment.feedbacks(course_id);
CREATE INDEX idx_feedbacks_submission ON assessment.feedbacks(submission_id);
CREATE INDEX idx_feedbacks_is_read ON assessment.feedbacks(is_read);

-- Learning resources table
CREATE TABLE assessment.learning_resources (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    topic VARCHAR(200) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    title VARCHAR(300) NOT NULL,
    url VARCHAR(500),
    description TEXT,
    difficulty_level VARCHAR(20),
    estimated_duration_minutes INTEGER,
    relevance_score INTEGER,
    tags JSONB,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    is_bookmarked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_learning_resources_student ON assessment.learning_resources(student_id);
CREATE INDEX idx_learning_resources_course ON assessment.learning_resources(course_id);
CREATE INDEX idx_learning_resources_topic ON assessment.learning_resources(topic);
CREATE INDEX idx_learning_resources_completed ON assessment.learning_resources(is_completed);

-- ============================================
-- Participation Tables
-- ============================================

-- Participation events table
CREATE TABLE assessment.participation_events (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    session_id BIGINT,
    event_type event_type NOT NULL,
    event_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    points INTEGER DEFAULT 1,
    event_data JSONB DEFAULT '{}',
    is_counted BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_participation_events_student ON assessment.participation_events(student_id);
CREATE INDEX idx_participation_events_course ON assessment.participation_events(course_id);
CREATE INDEX idx_participation_events_type ON assessment.participation_events(event_type);
CREATE INDEX idx_participation_events_time ON assessment.participation_events(event_time);

-- Participation scores table
CREATE TABLE assessment.participation_scores (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    total_score NUMERIC(10, 2) NOT NULL DEFAULT 0,
    attendance_score NUMERIC(10, 2) DEFAULT 0,
    activity_score NUMERIC(10, 2) DEFAULT 0,
    engagement_score NUMERIC(10, 2) DEFAULT 0,
    score_breakdown JSONB DEFAULT '{}',
    last_calculated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(student_id, course_id)
);

CREATE INDEX idx_participation_scores_student ON assessment.participation_scores(student_id);
CREATE INDEX idx_participation_scores_course ON assessment.participation_scores(course_id);
CREATE INDEX idx_participation_scores_total ON assessment.participation_scores(total_score DESC);

-- Participation weights table
CREATE TABLE assessment.participation_weights (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    event_type event_type NOT NULL,
    weight NUMERIC(5, 2) NOT NULL DEFAULT 1.0,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(course_id, event_type)
);

CREATE INDEX idx_participation_weights_course ON assessment.participation_weights(course_id);

-- ============================================
-- Peer Review Tables
-- ============================================

-- Peer reviews table
CREATE TABLE assessment.peer_reviews (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    submission_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    reviewee_id BIGINT NOT NULL,
    score NUMERIC(5, 2),
    max_score NUMERIC(5, 2),
    comments TEXT,
    rubric_scores JSONB DEFAULT '{}',
    is_submitted BOOLEAN NOT NULL DEFAULT FALSE,
    submitted_at TIMESTAMPTZ,
    is_outlier BOOLEAN NOT NULL DEFAULT FALSE,
    is_anonymous BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_peer_reviews_assignment ON assessment.peer_reviews(assignment_id);
CREATE INDEX idx_peer_reviews_submission ON assessment.peer_reviews(submission_id);
CREATE INDEX idx_peer_reviews_reviewer ON assessment.peer_reviews(reviewer_id);
CREATE INDEX idx_peer_reviews_reviewee ON assessment.peer_reviews(reviewee_id);
CREATE INDEX idx_peer_reviews_submitted ON assessment.peer_reviews(is_submitted);

-- Peer review assignments table
CREATE TABLE assessment.peer_review_assignments (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL UNIQUE,
    reviews_per_submission INTEGER NOT NULL DEFAULT 3,
    is_anonymous BOOLEAN NOT NULL DEFAULT TRUE,
    is_auto_assigned BOOLEAN NOT NULL DEFAULT TRUE,
    review_deadline TIMESTAMPTZ,
    min_score INTEGER,
    max_score INTEGER,
    rubric JSONB,
    settings JSONB DEFAULT '{}',
    remove_outliers BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_peer_review_assignments_assignment ON assessment.peer_review_assignments(assignment_id);
CREATE INDEX idx_peer_review_assignments_active ON assessment.peer_review_assignments(is_active);

-- ============================================
-- Comments
-- ============================================

COMMENT ON SCHEMA assessment IS 'E5: Assessment & Feedback domain - grading, code evaluation, feedback, participation, peer review';

COMMENT ON TABLE assessment.grading_results IS 'Grading results for assignments (auto, AI, manual, peer)';
COMMENT ON TABLE assessment.answer_statistics IS 'Statistics for quiz answers';
COMMENT ON TABLE assessment.code_submissions IS 'Code submissions for programming assignments';
COMMENT ON TABLE assessment.test_cases IS 'Test cases for code evaluation';
COMMENT ON TABLE assessment.execution_results IS 'Execution results for code submissions';
COMMENT ON TABLE assessment.plagiarism_reports IS 'Plagiarism detection reports';
COMMENT ON TABLE assessment.feedbacks IS 'AI and manual feedback for students';
COMMENT ON TABLE assessment.learning_resources IS 'Recommended learning resources';
COMMENT ON TABLE assessment.participation_events IS 'Student participation events';
COMMENT ON TABLE assessment.participation_scores IS 'Calculated participation scores';
COMMENT ON TABLE assessment.participation_weights IS 'Weight configuration for participation events';
COMMENT ON TABLE assessment.peer_reviews IS 'Peer evaluation reviews';
COMMENT ON TABLE assessment.peer_review_assignments IS 'Peer review configuration per assignment';
