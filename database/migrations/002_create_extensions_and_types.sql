-- ============================================
-- MIGRATION 002: Create Extensions and Types
-- ============================================
-- Description: Creates PostgreSQL extensions, ENUM types, and helper functions
-- Author: System
-- Date: 2025-01-28

-- PostgreSQL Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ENUM Types
CREATE TYPE user_status AS ENUM ('active', 'inactive', 'suspended', 'pending');
CREATE TYPE enrollment_role AS ENUM ('student', 'ta', 'auditor');
CREATE TYPE enrollment_status AS ENUM ('active', 'dropped', 'completed');
CREATE TYPE session_status AS ENUM ('scheduled', 'live', 'ended', 'cancelled');
CREATE TYPE assignment_status AS ENUM ('draft', 'published', 'closed');
CREATE TYPE submission_status AS ENUM ('submitted', 'graded', 'returned', 'resubmitted');
CREATE TYPE poll_type AS ENUM ('single', 'multiple', 'open_text', 'scale');
CREATE TYPE poll_status AS ENUM ('draft', 'active', 'ended');
CREATE TYPE question_type AS ENUM ('single_choice', 'multiple_choice', 'true_false', 'short_answer', 'essay', 'code');
CREATE TYPE quiz_status AS ENUM ('draft', 'active', 'ended');
CREATE TYPE quiz_attempt_status AS ENUM ('in_progress', 'submitted', 'graded');
CREATE TYPE alert_type AS ENUM ('absence', 'low_participation', 'grade_drop', 'inactivity');
CREATE TYPE alert_severity AS ENUM ('low', 'medium', 'high', 'critical');
CREATE TYPE content_type AS ENUM ('file', 'folder', 'link', 'video');
CREATE TYPE recording_status AS ENUM ('processing', 'ready', 'failed');
CREATE TYPE breakout_status AS ENUM ('pending', 'active', 'ended');
CREATE TYPE reaction_type AS ENUM ('thumbs_up', 'clap', 'heart', 'laugh', 'thinking', 'surprised');

-- Helper Functions
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Comment
COMMENT ON FUNCTION update_updated_at_column() IS 'Automatically updates the updated_at timestamp';
