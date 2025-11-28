-- ============================================
-- ROLLBACK 003: Drop Auth Schema
-- ============================================
-- Description: Drops all authentication schema tables and the schema itself
-- Author: System
-- Date: 2025-01-28

-- Drop schema and all tables within it
DROP SCHEMA IF EXISTS auth CASCADE;
