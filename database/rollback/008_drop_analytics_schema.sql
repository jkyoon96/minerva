-- ============================================
-- ROLLBACK 008: Drop Analytics Schema
-- ============================================
-- Description: Drops all analytics schema tables and the schema itself
-- Author: System
-- Date: 2025-01-28

-- Drop schema and all tables within it
DROP SCHEMA IF EXISTS analytics CASCADE;
