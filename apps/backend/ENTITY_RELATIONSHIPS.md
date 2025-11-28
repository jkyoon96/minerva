# Entity Relationship Diagram

## Overview
This document provides a visual representation of the entity relationships in the EduForum application.

---

## Auth Schema Relationships

```
┌──────────────────┐
│      User        │
│ ──────────────── │
│ id (PK)          │
│ email (UK)       │
│ password_hash    │
│ first_name       │
│ last_name        │
│ status           │
└────────┬─────────┘
         │
         │ 1
         │
         ├─────────────────────────────────────┐
         │                                     │
         │ *                                   │ *
┌────────▼─────────┐                  ┌────────▼─────────┐
│    UserRole      │                  │  RefreshToken    │
│ ──────────────── │                  │ ──────────────── │
│ id (PK)          │                  │ id (PK)          │
│ user_id (FK)     │                  │ user_id (FK)     │
│ role_id (FK)     │                  │ token_hash (UK)  │
│ assigned_at      │                  │ device_info JSON │
│ assigned_by (FK) │                  │ expires_at       │
└────────┬─────────┘                  │ revoked_at       │
         │                            └──────────────────┘
         │ *
         │
         │ 1
┌────────▼─────────┐
│      Role        │
│ ──────────────── │
│ id (PK)          │
│ name (UK)        │
│ description      │
└────────┬─────────┘
         │
         │ 1
         │
         │ *
┌────────▼─────────────┐
│  RolePermission      │
│ ──────────────────── │
│ id (PK)              │
│ role_id (FK)         │
│ permission_id (FK)   │
└────────┬─────────────┘
         │
         │ *
         │
         │ 1
┌────────▼─────────┐
│   Permission     │
│ ──────────────── │
│ id (PK)          │
│ name (UK)        │
│ resource         │
│ action           │
└──────────────────┘


┌──────────────────┐
│      User        │
└────────┬─────────┘
         │ 1
         │
         │ 1
┌────────▼─────────┐
│  TwoFactorAuth   │
│ ──────────────── │
│ id (PK)          │
│ user_id (FK, UK) │
│ secret           │
│ backup_codes JSON│
│ is_enabled       │
└──────────────────┘
```

---

## Course Schema Relationships

```
┌──────────────────┐
│      User        │
│   (Professor)    │
└────────┬─────────┘
         │
         │ 1
         │
         │ *
┌────────▼──────────────────┐
│        Course             │
│ ───────────────────────── │
│ id (PK)                   │
│ professor_id (FK)         │
│ code                      │
│ title                     │
│ semester                  │
│ year                      │
│ invite_code (UK)          │
│ is_published              │
│ settings (JSONB)          │
│ ───────────────────────── │
│ UK: (code,year,semester)  │
└───────┬───────────────────┘
        │
        │ 1
        │
        ├───────────────────────────────┬─────────────────────┐
        │                               │                     │
        │ *                             │ *                   │ *
┌───────▼───────────┐        ┌──────────▼──────────┐  ┌──────▼──────────┐
│   Enrollment      │        │   CourseSession     │  │   Assignment    │
│ ───────────────── │        │ ─────────────────── │  │ ─────────────── │
│ id (PK)           │        │ id (PK)             │  │ id (PK)         │
│ user_id (FK)      │        │ course_id (FK)      │  │ course_id (FK)  │
│ course_id (FK)    │        │ title               │  │ title           │
│ role (ENUM)       │        │ scheduled_at        │  │ description     │
│ status (ENUM)     │        │ duration_minutes    │  │ due_date        │
│ joined_at         │        │ status (ENUM)       │  │ max_score       │
│ ───────────────── │        │ started_at          │  │ status (ENUM)   │
│ UK: (user,course) │        │ ended_at            │  │ attachments JSON│
└───────┬───────────┘        │ meeting_url         │  └─────────────────┘
        │                    │ settings (JSONB)    │
        │ *                  └─────────────────────┘
        │
        │ 1
┌───────▼───────────┐
│      User         │
│   (Student/TA)    │
└───────────────────┘
```

---

## Complete System View

```
                    ┌─────────────────────────────────────────┐
                    │           USER (Central Entity)          │
                    │  ─────────────────────────────────────  │
                    │  - Authentication (Password, Email)     │
                    │  - Profile (Name, Photo, Phone)         │
                    │  - Status (Active, Pending, Suspended)  │
                    └───┬─────────────────────┬───────────────┘
                        │                     │
        ┌───────────────┴───────┐    ┌────────┴──────────┐
        │                       │    │                   │
        │ AUTHENTICATION        │    │  AUTHORIZATION    │
        │                       │    │                   │
┌───────▼───────┐      ┌────────▼────┐     ┌────────▼────────┐
│ RefreshToken  │      │ TwoFactorAuth│     │    UserRole     │
│ (JWT Tokens)  │      │ (2FA Setup)  │     │ (Role Assign)   │
└───────────────┘      └──────────────┘     └────────┬────────┘
                                                      │
                                                      │
                                             ┌────────▼────────┐
                                             │      Role       │
                                             │   (Admin,Prof,  │
                                             │   Student,TA)   │
                                             └────────┬────────┘
                                                      │
                                             ┌────────▼────────┐
                                             │ RolePermission  │
                                             └────────┬────────┘
                                                      │
                                             ┌────────▼────────┐
                                             │   Permission    │
                                             │ (Resource+Act)  │
                                             └─────────────────┘


                    ┌─────────────────────────────────────────┐
                    │           USER (as Professor)            │
                    └───┬─────────────────────────────────────┘
                        │
                        │ Creates
                        │
                ┌───────▼────────────────────────────┐
                │         COURSE                     │
                │  ────────────────────────────────  │
                │  - Code, Title, Semester, Year     │
                │  - Grading Weights (JSONB)         │
                │  - Invite Code, Max Students       │
                └───┬────────────────────────────┬───┘
                    │                            │
        ┌───────────┴───────┐        ┌──────────┴──────────┐
        │                   │        │                     │
        │ ENROLLMENT        │        │  COURSE CONTENT     │
        │                   │        │                     │
┌───────▼───────┐  ┌────────▼────┐  ┌────────▼──────┐  ┌──▼──────────┐
│   Student     │  │     TA      │  │ CourseSession │  │ Assignment  │
│ (Enrollment)  │  │ (Enrollment)│  │ (Live Class)  │  │ (Homework)  │
└───────────────┘  └─────────────┘  │ - Scheduled   │  │ - Due Date  │
                                    │ - Live        │  │ - Max Score │
                                    │ - Ended       │  │ - Published │
                                    └───────────────┘  └─────────────┘
```

---

## Enum Types and Values

### User Status Flow
```
PENDING ──register──> ACTIVE
  │                     │
  │                     │ suspend
  │                     ▼
  │                  SUSPENDED
  │                     │
  │                     │ deactivate
  │                     ▼
  └──────────────> INACTIVE
```

### Enrollment Status Flow
```
        ┌─── join course ───> ACTIVE
        │                       │
        │                       │ drop
        │                       ▼
        │                    DROPPED
        │
        └─── complete ─────> COMPLETED
```

### Session Status Flow
```
SCHEDULED ──start──> LIVE ──end──> ENDED
    │
    │ cancel
    ▼
CANCELLED
```

### Assignment Status Flow
```
DRAFT ──publish──> PUBLISHED ──close──> CLOSED
```

---

## Cardinality Reference

| Relationship | Cardinality | Description |
|--------------|-------------|-------------|
| User ↔ UserRole | 1:N | User can have multiple roles |
| Role ↔ UserRole | 1:N | Role can be assigned to multiple users |
| User ↔ RefreshToken | 1:N | User can have multiple active tokens |
| User ↔ TwoFactorAuth | 1:1 | User has at most one 2FA config |
| Role ↔ RolePermission | 1:N | Role can have multiple permissions |
| Permission ↔ RolePermission | 1:N | Permission can belong to multiple roles |
| User ↔ Course | 1:N | Professor creates multiple courses |
| Course ↔ Enrollment | 1:N | Course has multiple enrolled users |
| User ↔ Enrollment | 1:N | User can enroll in multiple courses |
| Course ↔ CourseSession | 1:N | Course has multiple sessions |
| Course ↔ Assignment | 1:N | Course has multiple assignments |

---

## JSONB Field Structures

### Course.settings
```json
{
  "grading_weights": {
    "participation": 30,
    "quiz": 30,
    "assignment": 40
  },
  "allow_late_submission": true,
  "late_penalty_percent": 10
}
```

### CourseSession.settings
```json
{
  "enable_waiting_room": false,
  "auto_record": true,
  "allow_chat": true,
  "allow_reactions": true
}
```

### RefreshToken.deviceInfo
```json
{
  "device_type": "desktop",
  "browser": "Chrome",
  "os": "Windows 10",
  "user_agent": "Mozilla/5.0..."
}
```

### TwoFactorAuth.backupCodes
```json
[
  "CODE1-ABCD-1234",
  "CODE2-EFGH-5678",
  "CODE3-IJKL-9012"
]
```

### Assignment.attachments
```json
[
  {
    "filename": "instructions.pdf",
    "url": "https://storage.example.com/files/123.pdf",
    "size": 1048576,
    "mime_type": "application/pdf"
  }
]
```

---

## Database Constraints

### Unique Constraints
- `users.email` - Email must be unique
- `roles.name` - Role name must be unique
- `permissions.name` - Permission name must be unique
- `refresh_tokens.token_hash` - Token hash must be unique
- `courses.invite_code` - Invite code must be unique
- `courses.(code, year, semester)` - Course code unique per semester
- `enrollments.(user_id, course_id)` - User can only enroll once per course
- `user_roles.(user_id, role_id)` - User-role pair must be unique
- `role_permissions.(role_id, permission_id)` - Role-permission pair must be unique

### Foreign Key Constraints
All relationships use `ON DELETE CASCADE` or `ON DELETE RESTRICT` based on business rules:

**CASCADE** (delete children when parent deleted):
- UserRole → User
- UserRole → Role
- RolePermission → Role
- RolePermission → Permission
- RefreshToken → User
- TwoFactorAuth → User
- Enrollment → User
- Enrollment → Course
- CourseSession → Course
- Assignment → Course

**RESTRICT** (prevent deletion if children exist):
- Course → User (professor_id)

---

## Index Strategy

### Auth Schema Indexes
```sql
-- User lookups
CREATE INDEX idx_users_email ON auth.users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_status ON auth.users(status) WHERE deleted_at IS NULL;

-- User roles
CREATE INDEX idx_user_roles_user_id ON auth.user_roles(user_id);

-- Refresh tokens
CREATE INDEX idx_refresh_tokens_user_id ON auth.refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON auth.refresh_tokens(expires_at)
  WHERE revoked_at IS NULL;
```

### Course Schema Indexes
```sql
-- Course lookups
CREATE INDEX idx_courses_professor_id ON course.courses(professor_id)
  WHERE deleted_at IS NULL;
CREATE INDEX idx_courses_semester_year ON course.courses(year, semester)
  WHERE deleted_at IS NULL;
CREATE INDEX idx_courses_invite_code ON course.courses(invite_code)
  WHERE deleted_at IS NULL AND invite_code IS NOT NULL;

-- Enrollments
CREATE INDEX idx_enrollments_course_id ON course.enrollments(course_id);
CREATE INDEX idx_enrollments_user_id ON course.enrollments(user_id);
CREATE INDEX idx_enrollments_status ON course.enrollments(status);

-- Sessions
CREATE INDEX idx_sessions_course_id ON course.sessions(course_id);
CREATE INDEX idx_sessions_scheduled_at ON course.sessions(scheduled_at);
CREATE INDEX idx_sessions_status ON course.sessions(status);

-- Assignments
CREATE INDEX idx_assignments_course_id ON course.assignments(course_id);
CREATE INDEX idx_assignments_due_date ON course.assignments(due_date);
CREATE INDEX idx_assignments_status ON course.assignments(status);
```

---

## Query Optimization Tips

### Use Entity Graphs
```java
@EntityGraph(attributePaths = {"enrollments", "sessions"})
List<Course> findWithDetailsById(Long id);
```

### Use Projections
```java
interface CourseProjection {
    String getCode();
    String getTitle();
}

List<CourseProjection> findAllProjectedBy();
```

### Use JOIN FETCH
```java
@Query("SELECT c FROM Course c JOIN FETCH c.professor WHERE c.id = :id")
Optional<Course> findByIdWithProfessor(@Param("id") Long id);
```

### Batch Fetching
```yaml
spring.jpa.properties.hibernate.default_batch_fetch_size: 20
```

---

This diagram provides a comprehensive view of all entity relationships, constraints, and optimization strategies for the EduForum application.
