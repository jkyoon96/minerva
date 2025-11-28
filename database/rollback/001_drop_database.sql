-- ============================================
-- ROLLBACK 001: Drop Database and User
-- ============================================
-- Description: Drops the eduforum database and user account
-- Author: System
-- Date: 2025-01-28

-- Terminate all connections to the database
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'eduforum'
  AND pid <> pg_backend_pid();

-- Drop database
DROP DATABASE IF EXISTS eduforum;

-- Drop user
DROP USER IF EXISTS eduforum;
