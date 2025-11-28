-- ============================================
-- MIGRATION 011 & 012 VERIFICATION SCRIPT
-- ============================================
-- Description: Verifies that migrations 011 and 012 were applied successfully
-- Usage: psql -U eduforum_user -d eduforum_db -f verify_migration_011_012.sql
-- Author: System
-- Date: 2025-01-29

\set ON_ERROR_STOP on

-- Display current database info
\echo '========================================='
\echo 'DATABASE INFORMATION'
\echo '========================================='
SELECT current_database() as database_name,
       current_user as user_name,
       version() as postgres_version;

\echo ''
\echo '========================================='
\echo 'MIGRATION 011: INDEX VERIFICATION'
\echo '========================================='
\echo ''

-- Count indexes by schema
\echo '--- Index Count by Schema ---'
SELECT schemaname, COUNT(*) as index_count
FROM pg_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
GROUP BY schemaname
ORDER BY schemaname;

\echo ''
\echo '--- Newly Added Indexes (Migration 011) ---'
SELECT schemaname, tablename, indexname
FROM pg_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND (
    indexname LIKE 'idx_%_lower' OR
    indexname LIKE 'idx_%_trgm' OR
    indexname LIKE 'idx_%_gin' OR
    indexname LIKE 'idx_%_active%' OR
    indexname LIKE 'idx_%_current' OR
    indexname LIKE 'idx_%_upcoming' OR
    indexname LIKE 'idx_%_published%' OR
    indexname LIKE 'idx_%_needs_%' OR
    indexname LIKE 'idx_%_performance' OR
    indexname LIKE 'idx_%_engagement' OR
    indexname LIKE 'idx_%_review%'
)
ORDER BY schemaname, tablename, indexname;

\echo ''
\echo '--- GIN Indexes for JSONB Columns ---'
SELECT schemaname, tablename, indexname
FROM pg_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND indexname LIKE '%_gin'
ORDER BY schemaname, tablename;

\echo ''
\echo '--- Partial Indexes (with WHERE clause) ---'
SELECT schemaname, tablename, indexname, indexdef
FROM pg_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND indexdef LIKE '%WHERE%'
ORDER BY schemaname, tablename
LIMIT 10;

\echo ''
\echo '--- Index Sizes (Top 10) ---'
SELECT schemaname, tablename, indexname,
       pg_size_pretty(pg_relation_size(indexrelid)) as index_size
FROM pg_stat_user_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
ORDER BY pg_relation_size(indexrelid) DESC
LIMIT 10;

\echo ''
\echo '========================================='
\echo 'MIGRATION 012: CONSTRAINT VERIFICATION'
\echo '========================================='
\echo ''

-- Count constraints by schema
\echo '--- Constraint Count by Schema ---'
SELECT nsp.nspname as schema_name,
       COUNT(con.conname) as constraint_count
FROM pg_constraint con
JOIN pg_namespace nsp ON con.connamespace = nsp.oid
WHERE nsp.nspname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
GROUP BY nsp.nspname
ORDER BY nsp.nspname;

\echo ''
\echo '--- CHECK Constraints (Sample) ---'
SELECT nsp.nspname as schema_name,
       cls.relname as table_name,
       con.conname as constraint_name
FROM pg_constraint con
JOIN pg_class cls ON con.conrelid = cls.oid
JOIN pg_namespace nsp ON cls.relnamespace = nsp.oid
WHERE nsp.nspname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND con.contype = 'c'
ORDER BY nsp.nspname, cls.relname, con.conname
LIMIT 20;

\echo ''
\echo '--- Format Validation Constraints ---'
SELECT nsp.nspname as schema_name,
       cls.relname as table_name,
       con.conname as constraint_name
FROM pg_constraint con
JOIN pg_class cls ON con.conrelid = cls.oid
JOIN pg_namespace nsp ON cls.relnamespace = nsp.oid
WHERE nsp.nspname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND con.contype = 'c'
AND (
    con.conname LIKE '%_format_%' OR
    con.conname LIKE '%_valid'
)
ORDER BY nsp.nspname, cls.relname;

\echo ''
\echo '--- Range Validation Constraints ---'
SELECT nsp.nspname as schema_name,
       cls.relname as table_name,
       con.conname as constraint_name
FROM pg_constraint con
JOIN pg_class cls ON con.conrelid = cls.oid
JOIN pg_namespace nsp ON cls.relnamespace = nsp.oid
WHERE nsp.nspname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND con.contype = 'c'
AND (
    con.conname LIKE '%_range' OR
    con.conname LIKE '%_positive' OR
    con.conname LIKE '%_nonnegative'
)
ORDER BY nsp.nspname, cls.relname;

\echo ''
\echo '--- Logical Consistency Constraints ---'
SELECT nsp.nspname as schema_name,
       cls.relname as table_name,
       con.conname as constraint_name
FROM pg_constraint con
JOIN pg_class cls ON con.conrelid = cls.oid
JOIN pg_namespace nsp ON cls.relnamespace = nsp.oid
WHERE nsp.nspname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND con.contype = 'c'
AND (
    con.conname LIKE '%_after_%' OR
    con.conname LIKE '%_before_%' OR
    con.conname LIKE '%_timestamp%'
)
ORDER BY nsp.nspname, cls.relname;

\echo ''
\echo '--- Foreign Key Constraints with SET NULL ---'
SELECT nsp.nspname as schema_name,
       cls.relname as table_name,
       con.conname as constraint_name,
       CASE con.confdeltype
           WHEN 'a' THEN 'NO ACTION'
           WHEN 'r' THEN 'RESTRICT'
           WHEN 'c' THEN 'CASCADE'
           WHEN 'n' THEN 'SET NULL'
           WHEN 'd' THEN 'SET DEFAULT'
       END as on_delete_action
FROM pg_constraint con
JOIN pg_class cls ON con.conrelid = cls.oid
JOIN pg_namespace nsp ON cls.relnamespace = nsp.oid
WHERE nsp.nspname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND con.contype = 'f'
AND con.confdeltype = 'n'
ORDER BY nsp.nspname, cls.relname;

\echo ''
\echo '--- Unique Constraints ---'
SELECT nsp.nspname as schema_name,
       cls.relname as table_name,
       con.conname as constraint_name
FROM pg_constraint con
JOIN pg_class cls ON con.conrelid = cls.oid
JOIN pg_namespace nsp ON cls.relnamespace = nsp.oid
WHERE nsp.nspname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND con.contype = 'u'
ORDER BY nsp.nspname, cls.relname;

\echo ''
\echo '========================================='
\echo 'DATA QUALITY CHECKS'
\echo '========================================='
\echo ''

\echo '--- Sample Data Validation ---'

-- Check for invalid emails (should be 0)
\echo 'Invalid Email Formats:'
SELECT COUNT(*) as invalid_count
FROM auth.users
WHERE email !~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
AND deleted_at IS NULL;

-- Check for invalid scores (should be 0)
\echo ''
\echo 'Invalid Grade Scores:'
SELECT COUNT(*) as invalid_count
FROM assess.grades
WHERE final_score < 0 OR final_score > 100;

-- Check for time order violations (should be 0)
\echo ''
\echo 'Session Time Order Violations:'
SELECT COUNT(*) as violation_count
FROM course.sessions
WHERE ended_at IS NOT NULL AND started_at IS NOT NULL
AND ended_at < started_at;

\echo ''
\echo '========================================='
\echo 'PERFORMANCE STATISTICS'
\echo '========================================='
\echo ''

\echo '--- Total Index Size by Schema ---'
SELECT schemaname,
       COUNT(*) as index_count,
       pg_size_pretty(SUM(pg_relation_size(indexrelid))) as total_size
FROM pg_stat_user_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
GROUP BY schemaname
ORDER BY SUM(pg_relation_size(indexrelid)) DESC;

\echo ''
\echo '--- Table + Index Size (Top 10) ---'
SELECT schemaname, tablename,
       pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) as table_size,
       pg_size_pretty(pg_indexes_size(schemaname||'.'||tablename)) as index_size,
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as total_size
FROM pg_tables
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
LIMIT 10;

\echo ''
\echo '========================================='
\echo 'VERIFICATION SUMMARY'
\echo '========================================='
\echo ''

DO $$
DECLARE
    idx_count INTEGER;
    check_count INTEGER;
    fk_null_count INTEGER;
BEGIN
    -- Count indexes from migration 011
    SELECT COUNT(*) INTO idx_count
    FROM pg_indexes
    WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
    AND (
        indexname LIKE 'idx_%_lower' OR
        indexname LIKE 'idx_%_trgm' OR
        indexname LIKE 'idx_%_gin' OR
        indexname LIKE 'idx_%_active%' OR
        indexname LIKE 'idx_%_current' OR
        indexname LIKE 'idx_%_upcoming'
    );

    -- Count CHECK constraints from migration 012
    SELECT COUNT(*) INTO check_count
    FROM pg_constraint con
    JOIN pg_namespace nsp ON con.connamespace = nsp.oid
    WHERE nsp.nspname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
    AND con.contype = 'c'
    AND (
        con.conname LIKE '%_check' OR
        con.conname LIKE '%_valid' OR
        con.conname LIKE '%_range' OR
        con.conname LIKE '%_positive'
    );

    -- Count FK constraints with SET NULL
    SELECT COUNT(*) INTO fk_null_count
    FROM pg_constraint con
    JOIN pg_namespace nsp ON con.connamespace = nsp.oid
    WHERE nsp.nspname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
    AND con.contype = 'f'
    AND con.confdeltype = 'n';

    RAISE NOTICE '';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'MIGRATION 011 & 012 VERIFICATION RESULTS';
    RAISE NOTICE '========================================';
    RAISE NOTICE '';
    RAISE NOTICE 'Migration 011 (Indexes):';
    RAISE NOTICE '  - New Indexes Found: % (Expected: ~74)', idx_count;
    RAISE NOTICE '';
    RAISE NOTICE 'Migration 012 (Constraints):';
    RAISE NOTICE '  - CHECK Constraints: % (Expected: ~85)', check_count;
    RAISE NOTICE '  - FK SET NULL Constraints: % (Expected: 5)', fk_null_count;
    RAISE NOTICE '';

    IF idx_count >= 60 AND check_count >= 70 AND fk_null_count = 5 THEN
        RAISE NOTICE 'Status: ✅ MIGRATIONS VERIFIED SUCCESSFULLY';
    ELSE
        RAISE WARNING 'Status: ⚠️  SOME MIGRATIONS MAY BE INCOMPLETE';
    END IF;

    RAISE NOTICE '========================================';
    RAISE NOTICE '';
END $$;

\echo ''
\echo 'Verification complete!'
\echo 'For detailed optimization guide, see: DB-003-OPTIMIZATION-GUIDE.md'
\echo 'For work summary, see: DB-003-SUMMARY.md'
