# EduForum Backend - Developer Guide

## Quick Reference for Common Modules

This guide provides quick examples for using the common modules implemented in BE-002.

---

## 📋 Table of Contents

1. [Entity Creation with Auditing](#entity-creation-with-auditing)
2. [Pagination](#pagination)
3. [Custom Validation](#custom-validation)
4. [Utility Classes](#utility-classes)
5. [Constants](#constants)
6. [Logging](#logging)

---

## 1. Entity Creation with Auditing

### Extend BaseEntity for automatic audit fields

```java
import com.eduforum.api.common.audit.BaseEntity;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    // createdAt, updatedAt, createdBy, updatedBy
    // are inherited from BaseEntity and automatically managed
}
```

When you create or update an entity:
```java
Course course = new Course();
course.setCode("CS101");
course.setName("Introduction to Computer Science");
courseRepository.save(course);
// createdAt, createdBy are automatically set
// updatedAt, updatedBy are automatically updated on every save
```

---

## 2. Pagination

### Request with Pagination

```java
import com.eduforum.api.common.dto.PageRequest;
import com.eduforum.api.common.dto.PageResponse;

@RestController
@RequestMapping(ApiConstants.COURSES_API_PATH)
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ApiResponse<PageResponse<CourseDto>> getCourses(
        @Valid PageRequest pageRequest
    ) {
        // Convert to Spring Data Pageable
        Pageable pageable = pageRequest.toPageable();

        // Query with pagination
        Page<Course> coursePage = courseRepository.findAll(pageable);

        // Convert to PageResponse with DTO mapping
        List<CourseDto> courseDtos = coursePage.getContent().stream()
            .map(CourseDto::from)
            .toList();

        PageResponse<CourseDto> response = PageResponse.of(coursePage, courseDtos);

        return ApiResponse.success(response);
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<CourseDto>> searchCourses(
        @RequestParam String keyword,
        @Valid PageRequest pageRequest
    ) {
        // Custom sorting
        Sort customSort = Sort.by(
            Sort.Order.desc("createdAt"),
            Sort.Order.asc("code")
        );
        Pageable pageable = pageRequest.toPageable(customSort);

        Page<Course> coursePage = courseRepository.findByNameContaining(keyword, pageable);
        PageResponse<CourseDto> response = PageResponse.of(coursePage);

        return ApiResponse.success(response);
    }
}
```

### Client Request Example
```
GET /api/v1/courses?page=0&size=20&sortField=createdAt&sortDirection=DESC
```

### Response Example
```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false,
    "empty": false,
    "sortField": "createdAt",
    "sortDirection": "DESC"
  },
  "message": "요청이 성공적으로 처리되었습니다."
}
```

---

## 3. Custom Validation

### Using Custom Validation Annotations

```java
import com.eduforum.api.common.validation.*;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = ErrorMessages.REQUIRED_FIELD_MISSING)
    @Email(message = ErrorMessages.INVALID_EMAIL_FORMAT)
    private String email;

    @ValidPassword  // Custom: 8~20자, 대소문자+숫자+특수문자
    private String password;

    @ValidPhone  // Custom: 한국 휴대폰 번호 형식
    private String phone;

    @NotBlank(message = "학번은 필수입니다.")
    @Pattern(regexp = ApiConstants.STUDENT_ID_REGEX, message = ErrorMessages.INVALID_STUDENT_ID_FORMAT)
    private String studentId;

    @ValidEnum(enumClass = UserRole.class, message = "유효하지 않은 역할입니다.")
    private String role;
}
```

### Define Custom Validator

```java
// 1. Create annotation
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidStudentIdValidator.class)
@Documented
public @interface ValidStudentId {
    String message() default "유효하지 않은 학번입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 2. Create validator
public class ValidStudentIdValidator implements ConstraintValidator<ValidStudentId, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.matches("^\\d{8,10}$");
    }
}
```

---

## 4. Utility Classes

### DateTimeUtil

```java
import com.eduforum.api.common.util.DateTimeUtil;

// Current time
LocalDateTime now = DateTimeUtil.now();
LocalDate today = DateTimeUtil.today();

// Formatting
String formatted = DateTimeUtil.format(now);
// "2025-11-29 14:30:45"

String korean = DateTimeUtil.formatKorean(today);
// "2025년 11월 29일"

// Parsing
LocalDateTime dt = DateTimeUtil.parseDateTime("2025-11-29 14:30:45");
LocalDate date = DateTimeUtil.parseDate("2025-11-29");

// Date calculations
LocalDate nextWeek = DateTimeUtil.plusDays(today, 7);
LocalDate lastMonth = DateTimeUtil.plusMonths(today, -1);

long daysBetween = DateTimeUtil.daysBetween(startDate, endDate);
long hoursBetween = DateTimeUtil.hoursBetween(startTime, endTime);

// Comparisons
boolean isPast = DateTimeUtil.isPast(deadline);
boolean isFuture = DateTimeUtil.isFuture(eventDate);
boolean isToday = DateTimeUtil.isToday(someDate);

// Start/End of period
LocalDateTime startOfDay = DateTimeUtil.startOfDay(today);
LocalDateTime endOfDay = DateTimeUtil.endOfDay(today);
LocalDate startOfMonth = DateTimeUtil.startOfMonth(today);
```

### StringUtil

```java
import com.eduforum.api.common.util.StringUtil;

// Null/blank checks
boolean isEmpty = StringUtil.isEmpty(str);
boolean isNotBlank = StringUtil.isNotBlank(str);

// Default values
String safe = StringUtil.defaultIfBlank(str, "default");
String trimmed = StringUtil.trimToNull(str);

// Masking
String maskedEmail = StringUtil.maskEmail("user@example.com");
// "u**@example.com"

String maskedPhone = StringUtil.maskPhone("010-1234-5678");
// "010-****-5678"

String maskedName = StringUtil.maskName("홍길동");
// "홍*동"

// Validation
boolean validEmail = StringUtil.isValidEmail("user@example.com");
boolean validPhone = StringUtil.isValidPhone("010-1234-5678");
boolean validStudentId = StringUtil.isValidStudentId("20231234");

// String manipulation
String truncated = StringUtil.truncate(longText, 100);
// "Long text here... (truncated at 97 chars)..."

String repeated = StringUtil.repeat("*", 5);
// "*****"

String padded = StringUtil.leftPad("42", 5, '0');
// "00042"
```

### JsonUtil

```java
import com.eduforum.api.common.util.JsonUtil;

// Object to JSON
User user = new User();
String json = JsonUtil.toJson(user);
String prettyJson = JsonUtil.toPrettyJson(user);

// JSON to Object
User user = JsonUtil.fromJson(json, User.class);

// JSON to List
List<User> users = JsonUtil.toList(jsonArray, User.class);

// JSON to Map
Map<String, Object> map = JsonUtil.toMap(json);

// Object to Map
Map<String, Object> userMap = JsonUtil.toMap(user);

// TypeReference for generics
List<User> users = JsonUtil.fromJson(json, new TypeReference<List<User>>() {});

// Deep copy
User copy = JsonUtil.deepCopy(original, User.class);

// Validation
boolean valid = JsonUtil.isValidJson(jsonString);
```

---

## 5. Constants

### Using Constants

```java
import com.eduforum.api.common.constant.*;

// API paths
@RequestMapping(ApiConstants.COURSES_API_PATH)  // "/v1/courses"

// Pagination
int defaultSize = ApiConstants.DEFAULT_PAGE_SIZE;  // 20
int maxSize = ApiConstants.MAX_PAGE_SIZE;  // 100

// Security
String jwtHeader = SecurityConstants.TOKEN_HEADER;  // "Authorization"
String roleStudent = SecurityConstants.ROLE_STUDENT;  // "ROLE_STUDENT"
long tokenValidity = SecurityConstants.ACCESS_TOKEN_VALIDITY_SECONDS;  // 3600

// Error messages
throw new BusinessException(
    ErrorCode.NOT_FOUND,
    ErrorMessages.USER_NOT_FOUND
);

throw new BusinessException(
    ErrorCode.INVALID_INPUT,
    ErrorMessages.INVALID_EMAIL_FORMAT
);

// Validation patterns
@Pattern(regexp = ApiConstants.EMAIL_REGEX)
@Pattern(regexp = ApiConstants.PHONE_REGEX)
@Pattern(regexp = ApiConstants.PASSWORD_REGEX)
```

---

## 6. Logging

### Automatic Logging with AOP

Controllers and Services are automatically logged:

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUser(@PathVariable Long id) {
        // Automatically logs:
        // 🎯 [Controller] UserController.getUser() 호출
        // 📥 [Request] UserController.getUser() - Parameters: [1]
        // ✅ [Controller] UserController.getUser() 완료 - 실행시간: 45ms
        // 📤 [Response] UserController.getUser() - Result: {...}

        return userService.getUser(id);
    }
}

@Service
public class UserService {

    public UserDto getUser(Long id) {
        // Automatically logs:
        // 🔧 [Service] UserService.getUser() 시작
        // ✅ [Service] UserService.getUser() 완료 - 실행시간: 12ms

        // ... service logic
    }
}
```

### Manual Logging

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    public void someMethod() {
        // MDC traceId is automatically available in logs
        log.info("Processing user registration");
        log.debug("User data: {}", userData);
        log.warn("Duplicate email detected: {}", email);
        log.error("Failed to send email", exception);
    }
}
```

### Log Output Example

```
2025-11-29 14:30:45.123 [a1b2c3d4e5f6g7h8] [http-nio-8000-exec-1] INFO  c.e.a.controller.UserController - 🎯 [Controller] UserController.getUser() 호출
2025-11-29 14:30:45.125 [a1b2c3d4e5f6g7h8] [http-nio-8000-exec-1] DEBUG c.e.a.controller.UserController - 📥 [Request] UserController.getUser() - Parameters: [1]
2025-11-29 14:30:45.130 [a1b2c3d4e5f6g7h8] [http-nio-8000-exec-1] DEBUG c.e.a.service.UserService - 🔧 [Service] UserService.getUser() 시작
2025-11-29 14:30:45.142 [a1b2c3d4e5f6g7h8] [http-nio-8000-exec-1] DEBUG c.e.a.service.UserService - ✅ [Service] UserService.getUser() 완료 - 실행시간: 12ms
2025-11-29 14:30:45.168 [a1b2c3d4e5f6g7h8] [http-nio-8000-exec-1] INFO  c.e.a.controller.UserController - ✅ [Controller] UserController.getUser() 완료 - 실행시간: 45ms
```

### Request Logging Example

```
2025-11-29 14:30:45.000 [a1b2c3d4e5f6g7h8] INFO  c.e.a.c.l.RequestLoggingFilter - 📨 [TraceId: a1b2c3d4e5f6g7h8] GET /api/v1/users/1 - IP: 127.0.0.1
2025-11-29 14:30:45.168 [a1b2c3d4e5f6g7h8] INFO  c.e.a.c.l.RequestLoggingFilter - ✅ [TraceId: a1b2c3d4e5f6g7h8] GET /api/v1/users/1 - Status: 200 - Duration: 168ms
```

---

## 🎯 Best Practices

### 1. Always use BaseEntity for domain entities
```java
// ✅ Good
@Entity
public class Course extends BaseEntity { ... }

// ❌ Bad - missing audit fields
@Entity
public class Course { ... }
```

### 2. Use PageRequest/PageResponse for list endpoints
```java
// ✅ Good
public ApiResponse<PageResponse<CourseDto>> getCourses(PageRequest pageRequest) { ... }

// ❌ Bad - custom pagination logic
public ApiResponse<List<CourseDto>> getCourses(int page, int size) { ... }
```

### 3. Use Constants instead of magic strings/numbers
```java
// ✅ Good
@RequestMapping(ApiConstants.COURSES_API_PATH)
if (size > ApiConstants.MAX_PAGE_SIZE) { ... }

// ❌ Bad - magic values
@RequestMapping("/v1/courses")
if (size > 100) { ... }
```

### 4. Use Utility classes for common operations
```java
// ✅ Good
String formatted = DateTimeUtil.format(dateTime);
boolean valid = StringUtil.isValidEmail(email);

// ❌ Bad - reimplementing logic
String formatted = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
boolean valid = email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
```

### 5. Use Custom Validation Annotations
```java
// ✅ Good
@ValidPassword
private String password;

@ValidPhone
private String phone;

// ❌ Bad - regex in field
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")
private String password;
```

---

## 🔧 Configuration

### Enable/Disable Logging

In `application.yml`:
```yaml
logging:
  level:
    com.eduforum.api: DEBUG  # Change to INFO to reduce verbosity
    org.springframework.aop: DEBUG  # Change to INFO to disable AOP logs
```

### Customize Pagination Defaults

In `ApiConstants.java`:
```java
public static final int DEFAULT_PAGE_SIZE = 20;  // Change default page size
public static final int MAX_PAGE_SIZE = 100;     // Change max page size
```

---

## 📚 Additional Resources

- **BE-002 Implementation**: `/apps/backend/BE-002-IMPLEMENTATION.md`
- **API Documentation**: http://localhost:8000/api/docs/swagger-ui.html
- **Health Check**: http://localhost:8000/api/health

---

**Last Updated**: 2025-11-29
