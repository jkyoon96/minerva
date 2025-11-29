-- Development Seed Data
-- This script is only loaded in development mode
-- It creates sample data for testing

-- Note: This script will run AFTER init-db.sql
-- Wait for Spring Boot to create tables first

-- Placeholder for development seed data
-- Tables will be created by Spring Boot JPA

SELECT 'Development seed data script loaded' as status;

-- Example (uncomment and modify after tables are created):
-- INSERT INTO users (id, username, email, password_hash, role, created_at, updated_at)
-- VALUES
--   (uuid_generate_v4(), 'admin', 'admin@eduforum.com', crypt('admin123', gen_salt('bf')), 'ADMIN', NOW(), NOW()),
--   (uuid_generate_v4(), 'professor1', 'prof1@eduforum.com', crypt('prof123', gen_salt('bf')), 'PROFESSOR', NOW(), NOW()),
--   (uuid_generate_v4(), 'student1', 'student1@eduforum.com', crypt('student123', gen_salt('bf')), 'STUDENT', NOW(), NOW());
