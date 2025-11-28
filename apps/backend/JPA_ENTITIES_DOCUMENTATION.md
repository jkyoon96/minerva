# JPA Entities Documentation - BE-003

This document describes the JPA entity mappings created for the EduForum backend application.

## Overview

JPA entities have been created to map to the existing PostgreSQL database schema at `210.115.229.12:5432/eduforum`. The entities follow Spring Data JPA best practices and include proper relationship mappings, enum types, and auditing support.

## Directory Structure

```
src/main/java/com/eduforum/api/domain/
├── common/
│   └── entity/
│       └── BaseEntity.java           # Base class with audit fields
├── auth/
│   ├── entity/
│   │   ├── User.java                 # auth.users table
│   │   ├── Role.java                 # auth.roles table
│   │   ├── Permission.java           # auth.permissions table
│   │   ├── UserRole.java             # auth.user_roles table
│   │   ├── RolePermission.java       # auth.role_permissions table
│   │   ├── RefreshToken.java         # auth.refresh_tokens table
│   │   ├── TwoFactorAuth.java        # auth.two_factor_auth table
│   │   └── UserStatus.java           # Enum for user_status type
│   └── repository/
│       ├── UserRepository.java
│       ├── RoleRepository.java
│       ├── PermissionRepository.java
│       └── RefreshTokenRepository.java
└── course/
    ├── entity/
    │   ├── Course.java               # course.courses table
    │   ├── CourseSession.java        # course.sessions table
    │   ├── Enrollment.java           # course.enrollments table
    │   ├── Assignment.java           # course.assignments table
    │   ├── EnrollmentRole.java       # Enum for enrollment_role type
    │   ├── EnrollmentStatus.java     # Enum for enrollment_status type
    │   ├── SessionStatus.java        # Enum for session_status type
    │   └── AssignmentStatus.java     # Enum for assignment_status type
    └── repository/
        ├── CourseRepository.java
        ├── CourseSessionRepository.java
        ├── EnrollmentRepository.java
        └── AssignmentRepository.java
```

## Entity Details

### Common Entities

#### BaseEntity
Abstract base class providing common audit fields:
- `createdAt` - Automatically set on entity creation
- `updatedAt` - Automatically updated on entity modification
- `deletedAt` - For soft delete support

Uses Spring Data JPA's `@EntityListeners(AuditingEntityListener.class)` for automatic timestamp management.

### Auth Schema Entities

#### User
Maps to `auth.users` table with the following features:
- **Primary Key**: `id` (BIGSERIAL)
- **Unique Fields**: `email`
- **Enum**: `UserStatus` (ACTIVE, INACTIVE, SUSPENDED, PENDING)
- **Relationships**:
  - One-to-Many with `UserRole`
  - One-to-Many with `RefreshToken`
  - One-to-One with `TwoFactorAuth`
- **Helper Methods**:
  - `getFullName()` - Concatenates first and last name
  - `isEmailVerified()` - Checks email verification status
  - `verifyEmail()` - Sets email verification timestamp
  - `updateLastLogin()` - Updates last login timestamp

#### Role
Maps to `auth.roles` table:
- **Primary Key**: `id` (BIGSERIAL)
- **Unique Fields**: `name`
- **Relationships**:
  - One-to-Many with `UserRole`
  - One-to-Many with `RolePermission`

#### Permission
Maps to `auth.permissions` table:
- **Primary Key**: `id` (BIGSERIAL)
- **Unique Fields**: `name`
- **Fields**: `resource`, `action`, `description`
- **Relationships**:
  - One-to-Many with `RolePermission`

#### UserRole
Maps to `auth.user_roles` table (junction table):
- Many-to-One with `User`
- Many-to-One with `Role`
- Tracks `assignedAt` and `assignedBy`
- Unique constraint on `(user_id, role_id)`

#### RolePermission
Maps to `auth.role_permissions` table (junction table):
- Many-to-One with `Role`
- Many-to-One with `Permission`
- Unique constraint on `(role_id, permission_id)`

#### RefreshToken
Maps to `auth.refresh_tokens` table:
- **JSONB Field**: `deviceInfo` - Stores device metadata
- **INET Field**: `ipAddress` - Stores client IP
- **Helper Methods**:
  - `isExpired()` - Checks if token is expired
  - `isRevoked()` - Checks if token is revoked
  - `revoke()` - Marks token as revoked
  - `isValid()` - Checks if token is valid (not expired and not revoked)

#### TwoFactorAuth
Maps to `auth.two_factor_auth` table:
- **JSONB Field**: `backupCodes` - List of backup codes
- One-to-One with `User`
- **Helper Methods**:
  - `enable()` - Enables 2FA
  - `disable()` - Disables 2FA

### Course Schema Entities

#### Course
Maps to `course.courses` table:
- **Primary Key**: `id` (BIGSERIAL)
- **Unique Constraint**: `(code, year, semester)`
- **JSONB Field**: `settings` - Course configuration
- **Relationships**:
  - Many-to-One with `User` (professor)
  - One-to-Many with `Enrollment`
  - One-to-Many with `CourseSession`
  - One-to-Many with `Assignment`
- **Helper Methods**:
  - `publish()` / `unpublish()`
  - `getFullCode()` - Returns formatted course code

#### CourseSession
Maps to `course.sessions` table (named `CourseSession` to avoid HTTP Session conflict):
- **Primary Key**: `id` (BIGSERIAL)
- **Enum**: `SessionStatus` (SCHEDULED, LIVE, ENDED, CANCELLED)
- **JSONB Field**: `settings` - Session configuration
- **Relationships**:
  - Many-to-One with `Course`
- **Helper Methods**:
  - `start()` - Starts session
  - `end()` - Ends session
  - `cancel()` - Cancels session
  - `isLive()` / `hasEnded()`

#### Enrollment
Maps to `course.enrollments` table:
- **Primary Key**: `id` (BIGSERIAL)
- **Unique Constraint**: `(user_id, course_id)`
- **Enums**: `EnrollmentRole` (STUDENT, TA, AUDITOR), `EnrollmentStatus` (ACTIVE, DROPPED, COMPLETED)
- **Relationships**:
  - Many-to-One with `User`
  - Many-to-One with `Course`
- **Helper Methods**:
  - `drop()` / `complete()` / `reactivate()`
  - `isActive()`

#### Assignment
Maps to `course.assignments` table:
- **Primary Key**: `id` (BIGSERIAL)
- **Enum**: `AssignmentStatus` (DRAFT, PUBLISHED, CLOSED)
- **JSONB Field**: `attachments` - List of file attachments
- **Relationships**:
  - Many-to-One with `Course`
- **Helper Methods**:
  - `publish()` / `close()`
  - `isPublished()` / `isClosed()` / `isPastDue()`

## Repository Interfaces

All repositories extend `JpaRepository<T, Long>` and include custom query methods:

### UserRepository
- `findByEmail(String email)`
- `findActiveByEmail(String email)` - Excludes soft-deleted
- `existsByEmail(String email)`
- `findByStatus(UserStatus status)`
- `findActiveByStatus(UserStatus status)`
- `findAllActive()` - All non-deleted users

### RoleRepository
- `findByName(String name)`
- `existsByName(String name)`

### PermissionRepository
- `findByName(String name)`
- `findByResource(String resource)`
- `findByResourceAndAction(String resource, String action)`

### RefreshTokenRepository
- `findByTokenHash(String tokenHash)`
- `findValidByTokenHash(String tokenHash, OffsetDateTime now)`
- `findValidTokensByUser(User user, OffsetDateTime now)`
- `deleteByExpiresAtBefore(OffsetDateTime dateTime)` - Cleanup expired tokens

### CourseRepository
- `findByInviteCode(String inviteCode)`
- `findByProfessor(User professor)`
- `findActiveCoursesByProfessor(User professor)`
- `findBySemesterAndYear(String semester, Integer year)`
- `findPublishedCourses()`
- `existsByCodeAndSemesterAndYear(String code, String semester, Integer year)`

### CourseSessionRepository
- `findByCourseOrderByScheduledAtDesc(Course course)`
- `findByStatus(SessionStatus status)`
- `findUpcomingSessions(Course course, OffsetDateTime now)`
- `findLiveSessions()`
- `findSessionsBetween(OffsetDateTime start, OffsetDateTime end)`

### EnrollmentRepository
- `findByUserAndCourse(User user, Course course)`
- `findActiveEnrollmentsByCourse(Course course)`
- `findActiveEnrollmentsByUser(User user)`
- `findByCourseAndRoleAndStatus(Course course, EnrollmentRole role, EnrollmentStatus status)`
- `countActiveStudents(Course course)`
- `isActivelyEnrolled(User user, Course course)`

### AssignmentRepository
- `findByCourseOrderByDueDateAsc(Course course)`
- `findPublishedAssignmentsByCourse(Course course)`
- `findUpcomingAssignments(Course course, OffsetDateTime now)`
- `findPastDueAssignments(Course course, OffsetDateTime now)`
- `findAssignmentsDueBetween(OffsetDateTime start, OffsetDateTime end)`

## Configuration

### JpaConfig.java
Updated to include:
- `@EnableJpaRepositories(basePackages = "com.eduforum.api.domain")`
- `@EnableJpaAuditing` - Enables automatic timestamp management

### application-dev.yml
Updated JPA configuration:
```yaml
jpa:
  show-sql: true
  hibernate:
    ddl-auto: validate  # Changed from 'update' to 'validate' since schema exists
  properties:
    hibernate:
      dialect: org.hibernate.dialect.PostgreSQLDialect
      format_sql: true
      default_schema: public
  open-in-view: false  # Best practice for REST APIs
```

## Key Design Decisions

### 1. Enum Mapping
PostgreSQL custom types (e.g., `user_status`) are mapped to Java enums using `@Enumerated(EnumType.STRING)` with `columnDefinition` to specify the database type.

### 2. JSONB Support
JSONB columns use Hibernate's `@JdbcTypeCode(SqlTypes.JSON)` annotation with `columnDefinition = "jsonb"`.

### 3. Timestamp Management
- Uses `OffsetDateTime` instead of `LocalDateTime` to preserve timezone information
- Automatic timestamp management via JPA Auditing
- Database triggers (`update_updated_at_column()`) work alongside JPA auditing

### 4. Soft Delete
- `BaseEntity` includes `deletedAt` field
- Repositories include methods to filter soft-deleted entities (e.g., `findAllActive()`)
- Can be enhanced with `@Where` annotation for automatic filtering

### 5. Lazy Loading
- All relationships use `FetchType.LAZY` by default to avoid N+1 queries
- Use DTOs or entity graphs for specific query optimization

### 6. Schema Mapping
- Explicitly specify schema in `@Table(schema = "auth")` or `@Table(schema = "course")`
- Ensures correct table resolution in multi-schema database

### 7. CourseSession Naming
- Named `CourseSession` instead of `Session` to avoid conflict with `jakarta.servlet.http.Session`

## Testing the Entities

To verify the entity mappings work correctly:

1. **Start the application** - Spring Boot will validate entity mappings against the database schema
2. **Check logs** - Look for Hibernate schema validation messages
3. **Test repositories** - Use repository methods to perform CRUD operations

Example test queries:

```java
// Find user by email
User user = userRepository.findByEmail("john.doe@example.com")
    .orElseThrow(() -> new EntityNotFoundException("User not found"));

// Find active courses for a professor
List<Course> courses = courseRepository.findActiveCoursesByProfessor(professor);

// Check if user is enrolled in course
boolean isEnrolled = enrollmentRepository.isActivelyEnrolled(user, course);

// Find upcoming sessions
List<CourseSession> sessions = sessionRepository.findUpcomingSessions(
    course,
    OffsetDateTime.now()
);
```

## Next Steps

1. **Add remaining schemas**:
   - Live schema (live_sessions, participants, etc.)
   - Learning schema (polls, quizzes, breakout_rooms, etc.)
   - Assess schema (submissions, grades, etc.)
   - Analytics schema (participation_events, engagement_scores, etc.)

2. **Implement DTOs**:
   - Create DTO classes for API requests/responses
   - Avoid exposing entities directly in REST endpoints

3. **Add Entity Graphs**:
   - Define `@NamedEntityGraph` for optimized queries
   - Prevents N+1 query problems

4. **Implement Soft Delete Filtering**:
   - Add `@Where(clause = "deleted_at IS NULL")` to entities
   - Or use Hibernate Filters for more flexibility

5. **Add Validation**:
   - Use Bean Validation annotations (@NotNull, @Email, @Size, etc.)
   - Validate at entity level for business rules

6. **Create Service Layer**:
   - Implement business logic in service classes
   - Keep repositories focused on data access

## Database Connection

The application connects to the production database:
- **Host**: 210.115.229.12
- **Port**: 5432
- **Database**: eduforum
- **Username**: eduforum
- **Password**: eduforum12

**Important**: The `ddl-auto` is set to `validate` to prevent accidental schema modifications. The database schema is managed via SQL migration files in `/database/migrations/`.

## Troubleshooting

### Schema Validation Errors
If you encounter schema validation errors:
1. Verify the database schema matches the migration files
2. Check enum type names match exactly (case-sensitive)
3. Ensure JSONB columns are properly defined
4. Verify foreign key relationships are correct

### N+1 Query Issues
If experiencing performance issues:
1. Use `@EntityGraph` for specific queries
2. Enable SQL logging to identify problematic queries
3. Consider using DTOs with custom queries
4. Use `JOIN FETCH` in JPQL queries

### JSONB Mapping Issues
If JSONB fields don't map correctly:
1. Ensure Hibernate version supports `@JdbcTypeCode`
2. Verify PostgreSQL driver version is up-to-date
3. Check `columnDefinition = "jsonb"` is set correctly

## References

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate User Guide](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html)
- [PostgreSQL JSONB Support](https://www.postgresql.org/docs/current/datatype-json.html)
