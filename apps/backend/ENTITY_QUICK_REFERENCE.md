# JPA Entity Quick Reference

## Entity-Table Mapping

| Entity Class | Database Table | Schema | Primary Key Type |
|--------------|----------------|--------|------------------|
| User | users | auth | Long (BIGSERIAL) |
| Role | roles | auth | Long (BIGSERIAL) |
| Permission | permissions | auth | Long (BIGSERIAL) |
| UserRole | user_roles | auth | Long (BIGSERIAL) |
| RolePermission | role_permissions | auth | Long (BIGSERIAL) |
| RefreshToken | refresh_tokens | auth | Long (BIGSERIAL) |
| TwoFactorAuth | two_factor_auth | auth | Long (BIGSERIAL) |
| Course | courses | course | Long (BIGSERIAL) |
| CourseSession | sessions | course | Long (BIGSERIAL) |
| Enrollment | enrollments | course | Long (BIGSERIAL) |
| Assignment | assignments | course | Long (BIGSERIAL) |

## Enum Types

| Java Enum | PostgreSQL Type | Values |
|-----------|-----------------|--------|
| UserStatus | user_status | ACTIVE, INACTIVE, SUSPENDED, PENDING |
| EnrollmentRole | enrollment_role | STUDENT, TA, AUDITOR |
| EnrollmentStatus | enrollment_status | ACTIVE, DROPPED, COMPLETED |
| SessionStatus | session_status | SCHEDULED, LIVE, ENDED, CANCELLED |
| AssignmentStatus | assignment_status | DRAFT, PUBLISHED, CLOSED |

## Entity Relationships

### User Relationships
```
User 1 ─── * UserRole ─── * Role
User 1 ─── * RefreshToken
User 1 ─── 1 TwoFactorAuth
User 1 ─── * Course (as professor)
User 1 ─── * Enrollment
```

### Course Relationships
```
Course 1 ─── * Enrollment ─── * User
Course 1 ─── * CourseSession
Course 1 ─── * Assignment
Course * ─── 1 User (professor)
```

## Common Repository Methods

### User Operations
```java
// Find by email
Optional<User> findByEmail(String email)

// Find active users by status
List<User> findActiveByStatus(UserStatus status)

// Check email exists
boolean existsByEmail(String email)
```

### Course Operations
```java
// Find by professor
List<Course> findActiveCoursesByProfessor(User professor)

// Find by semester/year
List<Course> findActiveCoursesBySemesterAndYear(String semester, Integer year)

// Find published courses
List<Course> findPublishedCourses()
```

### Enrollment Operations
```java
// Check enrollment
boolean isActivelyEnrolled(User user, Course course)

// Count students
Long countActiveStudents(Course course)

// Find enrollments
List<Enrollment> findActiveEnrollmentsByCourse(Course course)
```

### Session Operations
```java
// Find upcoming sessions
List<CourseSession> findUpcomingSessions(Course course, OffsetDateTime now)

// Find live sessions
List<CourseSession> findLiveSessions()
```

## JSONB Fields

| Entity | Field | Type | Description |
|--------|-------|------|-------------|
| RefreshToken | deviceInfo | Map<String, Object> | Device metadata |
| TwoFactorAuth | backupCodes | List<String> | 2FA backup codes |
| Course | settings | Map<String, Object> | Course configuration |
| CourseSession | settings | Map<String, Object> | Session configuration |
| Assignment | attachments | List<Object> | File attachments |

## Example Usage

### Creating a User
```java
User user = User.builder()
    .email("john.doe@example.com")
    .passwordHash(encodedPassword)
    .firstName("John")
    .lastName("Doe")
    .status(UserStatus.PENDING)
    .build();
userRepository.save(user);
```

### Creating a Course
```java
Course course = Course.builder()
    .professor(professor)
    .code("CS101")
    .title("Introduction to Computer Science")
    .semester("Spring")
    .year(2024)
    .isPublished(false)
    .build();
courseRepository.save(course);
```

### Enrolling a Student
```java
Enrollment enrollment = Enrollment.builder()
    .user(student)
    .course(course)
    .role(EnrollmentRole.STUDENT)
    .status(EnrollmentStatus.ACTIVE)
    .build();
enrollmentRepository.save(enrollment);
```

### Creating a Session
```java
CourseSession session = CourseSession.builder()
    .course(course)
    .title("Introduction to Java")
    .scheduledAt(OffsetDateTime.now().plusDays(1))
    .durationMinutes(90)
    .status(SessionStatus.SCHEDULED)
    .build();
sessionRepository.save(session);
```

## Helper Methods

### User
- `getFullName()` - Returns "firstName lastName"
- `isEmailVerified()` - Checks if email is verified
- `verifyEmail()` - Sets email verification timestamp
- `updateLastLogin()` - Updates last login time

### Course
- `publish()` - Publishes the course
- `unpublish()` - Unpublishes the course
- `getFullCode()` - Returns "code - semester year"

### CourseSession
- `start()` - Starts the session (sets status to LIVE)
- `end()` - Ends the session
- `cancel()` - Cancels the session
- `isLive()` - Checks if session is currently live
- `hasEnded()` - Checks if session has ended

### Enrollment
- `drop()` - Drops the enrollment
- `complete()` - Marks enrollment as completed
- `reactivate()` - Reactivates enrollment
- `isActive()` - Checks if enrollment is active

### Assignment
- `publish()` - Publishes the assignment
- `close()` - Closes the assignment
- `isPublished()` / `isClosed()` / `isPastDue()`

### RefreshToken
- `isExpired()` - Checks if token expired
- `isRevoked()` - Checks if token revoked
- `revoke()` - Revokes the token
- `isValid()` - Checks if token is valid

## Base Entity Fields (Inherited)

All entities extending `BaseEntity` have:
- `createdAt` - Auto-set on creation
- `updatedAt` - Auto-updated on modification
- `deletedAt` - For soft delete
- `delete()` - Soft delete method
- `isDeleted()` - Check if deleted

## Configuration Summary

### application-dev.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://210.115.229.12:5432/eduforum
    username: eduforum
    password: eduforum12
  jpa:
    hibernate:
      ddl-auto: validate  # Don't modify schema
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### JpaConfig.java
```java
@Configuration
@EnableJpaRepositories(basePackages = "com.eduforum.api.domain")
@EnableJpaAuditing
public class JpaConfig { }
```

## Testing Checklist

- [ ] Application starts without errors
- [ ] Hibernate validates schema successfully
- [ ] Can save and retrieve User entities
- [ ] Can save and retrieve Course entities
- [ ] Can create Enrollments
- [ ] JSONB fields serialize/deserialize correctly
- [ ] Enum types map correctly
- [ ] Timestamps are auto-populated
- [ ] Soft delete works (deletedAt is set)
- [ ] Relationships load correctly (lazy loading)

## Common Pitfalls

1. **Forgetting `@Table(schema = "...")`** - Hibernate looks in 'public' schema by default
2. **Using `LocalDateTime` instead of `OffsetDateTime`** - Loses timezone info
3. **Eager loading relationships** - Causes N+1 queries
4. **Exposing entities in REST APIs** - Use DTOs instead
5. **Not setting `columnDefinition` for enums** - May not map to PostgreSQL types correctly
6. **Modifying schema with `ddl-auto: update`** - Should be 'validate' in production
