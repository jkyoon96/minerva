-- ============================================
-- ROLLBACK 004: Drop Course Schema
-- ============================================
-- Description: Drops all course schema tables and the schema itself
-- Author: System
-- Date: 2025-01-28

-- Drop schema and all tables within it
DROP SCHEMA IF EXISTS course CASCADE;
