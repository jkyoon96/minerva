# Common Modules - Quick Start

## Overview

This document provides a quick overview of the common modules added to the EduForum backend in BE-002.

## 📦 What's Included

### 🔍 Logging & Monitoring
- **LoggingAspect**: Automatic method execution logging for Controllers and Services
- **RequestLoggingFilter**: HTTP request/response logging with traceId
- **MDC Support**: Request tracing across the application

### 📊 Auditing
- **BaseEntity**: Automatic createdAt, updatedAt, createdBy, updatedBy
- **AuditConfig**: JPA Auditing configuration
- **AuditorAwareImpl**: Current user extraction from Security Context

### 🛠️ Utilities
- **DateTimeUtil**: Date/time formatting, parsing, calculations
- **StringUtil**: String validation, masking, manipulation
- **JsonUtil**: JSON serialization/deserialization

### 📄 Pagination
- **PageRequest**: Unified pagination request DTO
- **PageResponse**: Standardized paginated response wrapper

### ✅ Validation
- **@ValidEnum**: Enum validation
- **@ValidPassword**: Password strength validation
- **@ValidPhone**: Korean phone number validation

### 📋 Constants
- **ApiConstants**: API paths, pagination defaults, regex patterns
- **SecurityConstants**: JWT, roles, security settings
- **ErrorMessages**: Localized error messages (Korean)

## 🚀 Quick Usage

### 1. Create an Entity
```java
@Entity
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
}
// createdAt, updatedAt, createdBy, updatedBy are automatic
```

### 2. Add Pagination to Controller
```java
@GetMapping
public ApiResponse<PageResponse<CourseDto>> getCourses(@Valid PageRequest pageRequest) {
    Page<Course> page = repository.findAll(pageRequest.toPageable());
    return ApiResponse.success(PageResponse.of(page));
}
```

### 3. Use Custom Validation
```java
public class RegisterRequest {
    @ValidPassword
    private String password;
    
    @ValidPhone
    private String phone;
}
```

### 4. Use Utility Classes
```java
// Date/Time
String formatted = DateTimeUtil.format(LocalDateTime.now());
LocalDate nextWeek = DateTimeUtil.plusDays(today, 7);

// String
String masked = StringUtil.maskEmail("user@example.com"); // "u**@example.com"
boolean valid = StringUtil.isValidEmail(email);

// JSON
String json = JsonUtil.toJson(object);
User user = JsonUtil.fromJson(json, User.class);
```

## 📚 Documentation

- **Full Implementation**: `BE-002-IMPLEMENTATION.md`
- **Developer Guide**: `DEVELOPER-GUIDE.md`

## 🔗 Key Constants

```java
// API Paths
ApiConstants.COURSES_API_PATH     // "/v1/courses"
ApiConstants.USERS_API_PATH       // "/v1/users"

// Pagination
ApiConstants.DEFAULT_PAGE_SIZE    // 20
ApiConstants.MAX_PAGE_SIZE        // 100

// Security
SecurityConstants.ROLE_STUDENT    // "ROLE_STUDENT"
SecurityConstants.ROLE_PROFESSOR  // "ROLE_PROFESSOR"

// Errors
ErrorMessages.USER_NOT_FOUND
ErrorMessages.INVALID_EMAIL_FORMAT
```

## ✨ Features

- ✅ **AOP-based logging** with method execution time
- ✅ **Request tracing** with unique traceId per request
- ✅ **JPA Auditing** for automatic created/updated tracking
- ✅ **Pagination** with Spring Data support
- ✅ **Custom validators** for common fields
- ✅ **Comprehensive utilities** for date/time, strings, JSON
- ✅ **Centralized constants** for maintainability
- ✅ **Korean error messages** for user-friendly errors

## 🎯 Next Steps

1. Run the application: `./gradlew bootRun`
2. Check logs for automatic request logging
3. Create your first entity extending `BaseEntity`
4. Add pagination to your endpoints using `PageRequest`/`PageResponse`
5. Use custom validators in your DTOs

---

**Created**: 2025-11-29 | **Version**: 1.0
