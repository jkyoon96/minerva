-- ============================================
-- ROLLBACK 005: Drop Live Session Schema
-- ============================================
-- Description: Drops all live session schema tables and the schema itself
-- Author: System
-- Date: 2025-01-28

-- Drop schema and all tables within it
DROP SCHEMA IF EXISTS live CASCADE;
