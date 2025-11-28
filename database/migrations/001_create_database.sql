-- ============================================
-- MIGRATION 001: Create Database and User
-- ============================================
-- Description: Creates the eduforum database and user account
-- Author: System
-- Date: 2025-01-28

-- Create database
CREATE DATABASE eduforum
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- Connect to eduforum database
\c eduforum

-- Create user
CREATE USER eduforum WITH PASSWORD 'eduforum12';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE eduforum TO eduforum;

-- Grant schema creation privilege
ALTER USER eduforum CREATEDB;

-- Grant usage on public schema
GRANT ALL ON SCHEMA public TO eduforum;
