-- ============================================
-- ROLLBACK 006: Drop Active Learning Schema
-- ============================================
-- Description: Drops all active learning schema tables and the schema itself
-- Author: System
-- Date: 2025-01-28

-- Drop schema and all tables within it
DROP SCHEMA IF EXISTS learning CASCADE;
