# Database Migration Checklist

Use this checklist when executing the database migration for GitHub Issue #282 (DB-001).

## Pre-Migration Checklist

### Environment Verification

- [ ] PostgreSQL 16+ is installed
  ```bash
  psql --version
  ```

- [ ] PostgreSQL service is running
  ```bash
  sudo systemctl status postgresql
  ```

- [ ] PostgreSQL is listening on port 5432
  ```bash
  sudo netstat -plnt | grep 5432
  ```

- [ ] You have PostgreSQL superuser credentials (default: `postgres`)
  ```bash
  psql -U postgres -c "SELECT version();"
  ```

### Backup Existing Data (if applicable)

- [ ] Backup existing `eduforum` database (if it exists)
  ```bash
  pg_dump -h localhost -U postgres -d eduforum -F c -f eduforum_backup_$(date +%Y%m%d).dump
  ```

- [ ] Verify backup file was created
  ```bash
  ls -lh eduforum_backup_*.dump
  ```

### File Verification

- [ ] All migration files present (10 files)
  ```bash
  ls -1 database/migrations/*.sql | wc -l
  # Expected: 10
  ```

- [ ] All rollback files present (10 files)
  ```bash
  ls -1 database/rollback/*.sql | wc -l
  # Expected: 10
  ```

- [ ] Seed file present
  ```bash
  ls -1 database/seeds/*.sql
  # Expected: 001_test_data.sql
  ```

- [ ] Scripts are executable
  ```bash
  ls -l database/scripts/*.sh
  # Should show 'x' permission
  ```

## Migration Execution

### Step 1: Run Migrations

- [ ] Navigate to scripts directory
  ```bash
  cd /mnt/d/Development/git/minerva/database/scripts
  ```

- [ ] Execute migration script
  ```bash
  ./migrate.sh localhost postgres
  ```

- [ ] Verify no errors in output
  - Look for green checkmarks (✓)
  - No red X marks (✗)

- [ ] Record migration completion time
  - Start: _______________
  - End: _______________

### Step 2: Verify Database Creation

- [ ] Connect to database
  ```bash
  psql -h localhost -U eduforum -d eduforum
  ```

- [ ] Verify all schemas exist
  ```sql
  \dn
  -- Expected: auth, course, live, learning, assess, analytics
  ```

- [ ] Count tables by schema
  ```sql
  SELECT schemaname, COUNT(*)
  FROM pg_tables
  WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
  GROUP BY schemaname
  ORDER BY schemaname;
  -- Expected:
  -- analytics | 5
  -- assess    | 4
  -- auth      | 8
  -- course    | 6
  -- learning  | 9
  -- live      | 6
  ```

- [ ] Verify roles were created
  ```sql
  SELECT name FROM auth.roles ORDER BY name;
  -- Expected: admin, professor, student, ta
  ```

- [ ] Verify permissions were created
  ```sql
  SELECT COUNT(*) FROM auth.permissions;
  -- Expected: 17
  ```

- [ ] Verify role-permission mappings
  ```sql
  SELECT r.name, COUNT(rp.permission_id) as permissions
  FROM auth.roles r
  LEFT JOIN auth.role_permissions rp ON r.id = rp.role_id
  GROUP BY r.id, r.name
  ORDER BY r.name;
  -- Expected:
  -- admin     | 17
  -- professor | 8
  -- student   | 5
  -- ta        | 5
  ```

- [ ] Verify extensions installed
  ```sql
  SELECT extname FROM pg_extension WHERE extname IN ('uuid-ossp', 'pgcrypto', 'pg_trgm');
  -- Expected: all 3 extensions
  ```

- [ ] Verify ENUM types created
  ```sql
  SELECT typname FROM pg_type
  WHERE typname IN ('user_status', 'session_status', 'poll_type', 'quiz_status')
  ORDER BY typname;
  -- Expected: all 4 types (and more)
  ```

### Step 3: Run Seed Data (Optional for Testing)

- [ ] Execute seed script
  ```bash
  ./seed.sh localhost
  ```

- [ ] Verify users created
  ```sql
  SELECT email, first_name, last_name FROM auth.users ORDER BY email;
  -- Expected: 9 users
  ```

- [ ] Verify user roles assigned
  ```sql
  SELECT u.email, r.name as role
  FROM auth.users u
  JOIN auth.user_roles ur ON u.id = ur.user_id
  JOIN auth.roles r ON r.id = ur.role_id
  ORDER BY r.name, u.email;
  -- Expected: 9 role assignments
  ```

- [ ] Verify courses created
  ```sql
  SELECT code, title FROM course.courses ORDER BY code;
  -- Expected: CS101, CS201
  ```

- [ ] Verify enrollments created
  ```sql
  SELECT c.code, COUNT(e.id) as enrolled
  FROM course.courses c
  LEFT JOIN course.enrollments e ON c.id = e.course_id
  GROUP BY c.id, c.code
  ORDER BY c.code;
  -- Expected: CS101 (6), CS201 (3)
  ```

- [ ] Verify sessions created
  ```sql
  SELECT COUNT(*) FROM course.sessions;
  -- Expected: 3
  ```

- [ ] Verify assignments created
  ```sql
  SELECT COUNT(*) FROM course.assignments;
  -- Expected: 2
  ```

## Post-Migration Verification

### Data Integrity

- [ ] Check foreign key constraints
  ```sql
  SELECT COUNT(*) FROM information_schema.table_constraints
  WHERE constraint_type = 'FOREIGN KEY'
  AND table_schema IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics');
  -- Expected: 40+
  ```

- [ ] Check triggers
  ```sql
  SELECT trigger_schema, COUNT(*)
  FROM information_schema.triggers
  WHERE trigger_schema IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
  GROUP BY trigger_schema;
  -- Expected: Multiple schemas with triggers
  ```

- [ ] Verify indexes created
  ```sql
  SELECT schemaname, COUNT(*)
  FROM pg_indexes
  WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
  GROUP BY schemaname;
  -- Expected: Multiple indexes per schema
  ```

### Performance

- [ ] Check database size
  ```sql
  SELECT pg_size_pretty(pg_database_size('eduforum'));
  ```

- [ ] Run VACUUM ANALYZE
  ```sql
  VACUUM ANALYZE;
  ```

### Security

- [ ] Verify user permissions
  ```sql
  \du eduforum
  -- Should show database creation privilege
  ```

- [ ] Test connection with eduforum user
  ```bash
  psql -h localhost -U eduforum -d eduforum -c "SELECT current_user;"
  -- Expected: eduforum
  ```

## Documentation

- [ ] Update project documentation with connection info
- [ ] Document any issues encountered during migration
- [ ] Record migration completion in project log
- [ ] Update GitHub Issue #282 with completion status

## Rollback Testing (Optional but Recommended)

⚠️ Only perform on a test instance, not production!

- [ ] Create test backup before rollback
  ```bash
  pg_dump -h localhost -U eduforum -d eduforum -F c -f test_backup.dump
  ```

- [ ] Execute rollback script
  ```bash
  ./rollback.sh localhost postgres
  ```

- [ ] Verify database dropped
  ```bash
  psql -U postgres -l | grep eduforum
  # Should return no results
  ```

- [ ] Re-run migration
  ```bash
  ./migrate.sh localhost postgres
  ```

- [ ] Verify migration succeeds after rollback

## Sign-off

Migration completed by: _______________

Date: _______________

Time: _______________

Issues encountered: _______________

Resolution: _______________

Status: [ ] Success  [ ] Failed  [ ] Partial

## Notes

Additional notes or observations:

_______________________________________________

_______________________________________________

_______________________________________________

_______________________________________________
