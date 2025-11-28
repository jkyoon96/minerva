# EduForum Database - Quick Reference Card

## Connection Info

```
Database: eduforum
User:     eduforum
Password: eduforum12
Host:     localhost
Port:     5432
```

## Quick Commands

### Setup
```bash
# Complete setup (from database/scripts/)
./migrate.sh && ./seed.sh

# Verify
psql -U eduforum -d eduforum -c "\dt auth.*"
```

### Connect
```bash
# Connect to database
psql -h localhost -U eduforum -d eduforum

# Connect as postgres (for admin)
psql -h localhost -U postgres -d eduforum
```

### Useful SQL Queries

```sql
-- List all schemas
\dn

-- List tables in a schema
\dt auth.*
\dt course.*
\dt live.*
\dt learning.*
\dt assess.*
\dt analytics.*

-- Count records in all user tables
SELECT 'users' as table, COUNT(*) FROM auth.users
UNION ALL
SELECT 'courses', COUNT(*) FROM course.courses
UNION ALL
SELECT 'sessions', COUNT(*) FROM course.sessions
UNION ALL
SELECT 'enrollments', COUNT(*) FROM course.enrollments;

-- View all roles and permissions
SELECT r.name as role, p.name as permission
FROM auth.roles r
JOIN auth.role_permissions rp ON r.id = rp.role_id
JOIN auth.permissions p ON p.id = rp.permission_id
ORDER BY r.name, p.name;

-- View all test users with roles
SELECT u.email, u.first_name, u.last_name, r.name as role
FROM auth.users u
JOIN auth.user_roles ur ON u.id = ur.user_id
JOIN auth.roles r ON r.id = ur.role_id
ORDER BY r.name, u.email;

-- View courses with professor info
SELECT c.code, c.title, u.first_name || ' ' || u.last_name as professor
FROM course.courses c
JOIN auth.users u ON c.professor_id = u.id
WHERE c.deleted_at IS NULL;

-- View enrollments by course
SELECT c.code, c.title, COUNT(e.id) as enrolled
FROM course.courses c
LEFT JOIN course.enrollments e ON c.id = e.course_id AND e.status = 'active'
GROUP BY c.id, c.code, c.title;
```

## Schema Structure

```
eduforum/
├── auth         (8 tables)  - Authentication & Authorization
├── course       (6 tables)  - Course Management
├── live         (6 tables)  - Live Session Interactions
├── learning     (9 tables)  - Active Learning Tools
├── assess       (4 tables)  - Assessment & Grading
└── analytics    (5 tables)  - Analytics & Reporting
```

## Test Data

### Test Users (after seeding)

| Email | Password | Role | Name |
|-------|----------|------|------|
| admin@eduforum.com | (hashed) | admin | Admin User |
| prof.kim@eduforum.com | (hashed) | professor | Minsoo Kim |
| prof.lee@eduforum.com | (hashed) | professor | Jieun Lee |
| ta.park@eduforum.com | (hashed) | ta | Hyunwoo Park |
| student1@eduforum.com | (hashed) | student | Soyeon Choi |
| student2@eduforum.com | (hashed) | student | Jihoon Jung |
| student3@eduforum.com | (hashed) | student | Minji Kang |
| student4@eduforum.com | (hashed) | student | Donghyun Yoon |
| student5@eduforum.com | (hashed) | student | Seunghee Han |

### Test Courses

- **CS101** - Introduction to Computer Science (Prof. Kim)
  - Invite Code: CS101SPRING
  - 5 students + 1 TA enrolled

- **CS201** - Data Structures and Algorithms (Prof. Lee)
  - Invite Code: CS201SPRING
  - 3 students enrolled

## Common Operations

### Backup & Restore

```bash
# Backup
pg_dump -h localhost -U eduforum -d eduforum -F c -f backup.dump

# Restore
pg_restore -h localhost -U eduforum -d eduforum -F c backup.dump
```

### Reset Database

```bash
cd database/scripts
./rollback.sh
./migrate.sh
./seed.sh
```

### Check Database Size

```sql
SELECT pg_size_pretty(pg_database_size('eduforum'));
```

### Vacuum & Analyze

```sql
VACUUM ANALYZE;
```

## Troubleshooting

### Cannot connect
```bash
# Check PostgreSQL is running
sudo systemctl status postgresql

# Check port
sudo netstat -plnt | grep 5432
```

### Permission denied
```bash
# Grant all privileges
psql -U postgres -d eduforum << EOF
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA auth TO eduforum;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA auth TO eduforum;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA course TO eduforum;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA course TO eduforum;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA live TO eduforum;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA live TO eduforum;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA learning TO eduforum;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA learning TO eduforum;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA assess TO eduforum;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA assess TO eduforum;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA analytics TO eduforum;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA analytics TO eduforum;
EOF
```

### Database exists error
```bash
# Drop and recreate
cd database/scripts
./rollback.sh
./migrate.sh
```

## File Locations

```
/mnt/d/Development/git/minerva/database/
├── README.md                    # Full documentation
├── SUMMARY.md                   # Detailed summary
├── QUICK_REFERENCE.md          # This file
├── migrations/                  # 10 migration files
├── rollback/                    # 10 rollback files
├── seeds/                       # 1 seed file
└── scripts/                     # 3 shell scripts
    ├── migrate.sh
    ├── rollback.sh
    └── seed.sh
```

## Next Steps After Migration

1. Update seed data passwords with real bcrypt hashes
2. Configure application database connection
3. Set up backup schedule
4. Configure monitoring
5. Review and adjust JSONB schemas as needed
6. Add application-specific indexes based on query patterns
7. Consider partitioning for analytics tables (optional)

---

For detailed information, see `/database/README.md` and `/docs/06-database-design.md`
