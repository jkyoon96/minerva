-- EduForum Database Initialization Script
-- This script runs when the PostgreSQL container is first created

-- Enable UUID extension for primary keys
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable pgcrypto for password hashing
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Set timezone
SET timezone = 'UTC';

-- Create database if not exists (usually already created by POSTGRES_DB)
-- This is a no-op if database already exists
SELECT 'Database initialization completed' as status;

-- Note: Table creation is handled by Spring Boot JPA/Hibernate
-- This script is for extensions and initial setup only
