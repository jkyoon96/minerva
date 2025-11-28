# EduForum Database

PostgreSQL database schema and migration scripts for the EduForum platform.

## Overview

This directory contains all database-related files including:
- **Migrations**: DDL scripts to create database schema
- **Rollback**: Scripts to undo migrations
- **Seeds**: Test data for development
- **Scripts**: Shell scripts to run migrations, rollbacks, and seeds

## Database Structure

- **Database Name**: `eduforum`
- **User**: `eduforum`
- **Password**: `eduforum12`
- **PostgreSQL Version**: 16+

### Schemas

The database is organized into 6 domain-based schemas:

1. **auth** - Authentication and authorization (7 tables)
2. **course** - Course management (6 tables)
3. **live** - Live session interactions (6 tables)
4. **learning** - Active learning tools (9 tables)
5. **assess** - Assessment and grading (4 tables)
6. **analytics** - Analytics and reporting (5 tables)

**Total**: 34 tables

## Quick Start

### Prerequisites

- PostgreSQL 16+ installed and running
- PostgreSQL superuser access (default: `postgres`)

### 1. Run Migrations

```bash
cd database/scripts
./migrate.sh [postgres_host] [postgres_user]
```

Default values: `localhost` and `postgres`

This will:
- Create the `eduforum` database
- Create the `eduforum` user with password `eduforum12`
- Install required extensions (uuid-ossp, pgcrypto, pg_trgm)
- Create all ENUM types
- Create all schemas and tables
- Create all indexes
- Insert initial roles and permissions

### 2. Seed Test Data (Optional)

```bash
cd database/scripts
./seed.sh [postgres_host]
```

This will insert:
- 9 test users (1 admin, 2 professors, 1 TA, 5 students)
- 2 test courses (CS101, CS201)
- 3 test sessions
- 2 test assignments
- Initial enrollments and grades

### 3. Verify Installation

```bash
# Connect to database
psql -h localhost -U eduforum -d eduforum

# List all schemas
\dn

# List tables in auth schema
\dt auth.*

# List tables in all schemas
\dt auth.*
\dt course.*
\dt live.*
\dt learning.*
\dt assess.*
\dt analytics.*
```

## Migration Files

Located in `migrations/` directory:

| File | Description |
|------|-------------|
| 001_create_database.sql | Creates database and user |
| 002_create_extensions_and_types.sql | PostgreSQL extensions and ENUM types |
| 003_auth_schema.sql | Authentication schema |
| 004_course_schema.sql | Course management schema |
| 005_live_schema.sql | Live session schema |
| 006_learning_schema.sql | Active learning schema |
| 007_assess_schema.sql | Assessment schema |
| 008_analytics_schema.sql | Analytics schema |
| 009_indexes.sql | Additional performance indexes |
| 010_initial_data.sql | Roles, permissions, and mappings |

## Rollback

To completely remove the database:

```bash
cd database/scripts
./rollback.sh [postgres_host] [postgres_user]
```

**WARNING**: This will permanently delete all data and drop the database!

## Test Users

After seeding, the following test users are available:

| Email | Role | Name |
|-------|------|------|
| admin@eduforum.com | Admin | Admin User |
| prof.kim@eduforum.com | Professor | Minsoo Kim |
| prof.lee@eduforum.com | Professor | Jieun Lee |
| ta.park@eduforum.com | TA | Hyunwoo Park |
| student1@eduforum.com | Student | Soyeon Choi |
| student2@eduforum.com | Student | Jihoon Jung |
| student3@eduforum.com | Student | Minji Kang |
| student4@eduforum.com | Student | Donghyun Yoon |
| student5@eduforum.com | Student | Seunghee Han |

Note: Passwords are bcrypt hashed in the seed file.

## Schema Overview

### Auth Schema (auth)
- users, roles, permissions
- user_roles, role_permissions
- oauth_accounts, two_factor_auth
- refresh_tokens, password_reset_tokens

### Course Schema (course)
- courses, enrollments, sessions
- recordings, contents
- assignments, submissions

### Live Schema (live)
- session_participants, chats
- breakout_rooms, breakout_participants
- reactions, hand_raises

### Learning Schema (learning)
- polls, poll_options, poll_votes
- quizzes, questions
- quiz_attempts, quiz_answers
- whiteboards, whiteboard_elements

### Assessment Schema (assess)
- grades, ai_gradings
- peer_evaluations, code_executions

### Analytics Schema (analytics)
- participation_logs, alerts
- interaction_logs, daily_stats
- notification_settings

## Manual Operations

### Connect to Database

```bash
psql -h localhost -U eduforum -d eduforum
```

### Run Individual Migration

```bash
psql -h localhost -U eduforum -d eduforum -f migrations/003_auth_schema.sql
```

### Backup Database

```bash
pg_dump -h localhost -U eduforum -d eduforum -F c -f eduforum_backup.dump
```

### Restore Database

```bash
pg_restore -h localhost -U eduforum -d eduforum -F c eduforum_backup.dump
```

## Troubleshooting

### Connection Issues

If you get connection errors:

```bash
# Check PostgreSQL is running
sudo systemctl status postgresql

# Check PostgreSQL is listening
sudo netstat -plnt | grep 5432
```

### Permission Issues

If you get permission errors:

```bash
# Grant privileges to eduforum user
psql -U postgres -d eduforum -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA auth TO eduforum;"
psql -U postgres -d eduforum -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA auth TO eduforum;"
```

### Reset Database

To start fresh:

```bash
./rollback.sh
./migrate.sh
./seed.sh
```

## Documentation

For detailed database design documentation, see:
- `/docs/06-database-design.md` - Complete database design specification

## Development Notes

- All tables use `BIGSERIAL` for primary keys
- Timestamps use `TIMESTAMPTZ` (timezone-aware)
- Soft deletes use `deleted_at` column
- All tables have `created_at` and `updated_at` (where applicable)
- JSONB is used for flexible/nested data
- Foreign keys use `ON DELETE CASCADE` or `ON DELETE SET NULL` as appropriate
- Indexes are created for all foreign keys and frequently queried columns

## License

Internal project - Minerva University
