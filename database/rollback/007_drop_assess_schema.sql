-- ============================================
-- ROLLBACK 007: Drop Assessment Schema
-- ============================================
-- Description: Drops all assessment schema tables and the schema itself
-- Author: System
-- Date: 2025-01-28

-- Drop schema and all tables within it
DROP SCHEMA IF EXISTS assess CASCADE;
