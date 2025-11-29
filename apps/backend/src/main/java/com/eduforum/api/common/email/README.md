# Email Service Documentation

## Overview

EduForum의 이메일 발송 서비스는 다양한 이메일 프로바이더를 지원하며, 비동기 큐 시스템을 통해 안정적인 이메일 발송을 보장합니다.

## Architecture

```
EmailService (Interface)
├── ConsoleEmailService (개발용)
├── SmtpEmailService (SMTP)
└── SendGridEmailService (SendGrid API)

EmailQueueService
└── EmailJobProcessor (@Scheduled)

EmailTemplateService
└── HTML Templates
```

## Supported Providers

### 1. Console (Development)
- 실제 이메일을 발송하지 않고 콘솔에 출력
- 개발 환경에서 기본으로 사용
- 외부 서비스 없이 동작

### 2. SMTP
- Spring Mail을 사용한 SMTP 발송
- Gmail, AWS SES, SendGrid SMTP 등 지원
- `spring.mail` 설정 필요

### 3. SendGrid
- SendGrid REST API 사용
- 높은 전송률과 안정성
- `sendgrid.api-key` 설정 필요

## Configuration

### application.yml

```yaml
# Application Settings
app:
  frontend:
    url: http://localhost:3000
  email:
    provider: console  # console, smtp, sendgrid
    from: noreply@eduforum.com
    from-name: EduForum

# SMTP Settings (provider: smtp)
spring.mail:
  host: smtp.gmail.com
  port: 587
  username: ${MAIL_USERNAME}
  password: ${MAIL_PASSWORD}
  properties:
    mail:
      smtp:
        auth: true
        starttls:
          enable: true

# SendGrid Settings (provider: sendgrid)
sendgrid:
  api-key: ${SENDGRID_API_KEY}
```

### Environment Variables

**Development (Console Mode)**
```bash
# No configuration needed
```

**Production (SMTP)**
```bash
EMAIL_PROVIDER=smtp
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
FRONTEND_URL=https://eduforum.com
```

**Production (SendGrid)**
```bash
EMAIL_PROVIDER=sendgrid
SENDGRID_API_KEY=SG.xxxxxxxxxxxxx
FRONTEND_URL=https://eduforum.com
```

## Usage

### 1. Simple Email

```java
@Autowired
private EmailService emailService;

// Synchronous
EmailResult result = emailService.sendEmail(
    "user@example.com",
    "Welcome!",
    "<h1>Welcome to EduForum</h1>"
);

// Asynchronous (recommended)
EmailRequest request = EmailRequest.builder()
    .to("user@example.com")
    .subject("Welcome!")
    .body("<h1>Welcome to EduForum</h1>")
    .html(true)
    .build();
emailService.sendAsync(request);
```

### 2. Template Email

```java
Map<String, Object> variables = new HashMap<>();
variables.put("userName", "John Doe");
variables.put("verificationUrl", "https://eduforum.com/verify?token=xxx");
variables.put("expiresIn", "24");

// Synchronous
EmailResult result = emailService.sendTemplateEmail(
    "user@example.com",
    "email-verification",
    variables
);

// Asynchronous (recommended)
EmailRequest request = EmailRequest.builder()
    .to("user@example.com")
    .templateName("email-verification")
    .variables(variables)
    .build();
emailService.sendAsync(request);
```

### 3. Advanced Options

```java
EmailRequest request = EmailRequest.builder()
    .to("user@example.com")
    .subject("Custom Subject")
    .body("Email body")
    .html(true)
    .fromName("Custom From Name")
    .replyTo("support@eduforum.com")
    .build();

emailService.sendAsync(request);
```

## Email Templates

### Available Templates

1. **welcome.html** - 회원가입 환영 이메일
   - Variables: `userName`, `dashboardUrl`

2. **email-verification.html** - 이메일 인증
   - Variables: `userName`, `verificationCode`, `verificationUrl`, `expiresIn`

3. **password-reset.html** - 비밀번호 재설정
   - Variables: `userName`, `resetToken`, `resetUrl`, `expiresIn`

4. **course-invitation.html** - 코스 초대
   - Variables: `userName`, `instructorName`, `courseTitle`, `courseCode`, `courseDescription`, `semester`, `startDate`, `enrollmentUrl`, `expiresIn`

### Template Location

```
src/main/resources/templates/email/
├── welcome.html
├── email-verification.html
├── password-reset.html
└── course-invitation.html
```

### Creating Custom Templates

1. Create HTML file in `src/main/resources/templates/email/`
2. Use `{{variableName}}` for variable substitution
3. Add default subject in `EmailTemplateService.getTemplateSubject()`

Example:
```html
<!DOCTYPE html>
<html>
<body>
    <h1>Hello {{userName}}!</h1>
    <p>{{customMessage}}</p>
</body>
</html>
```

## Email Queue System

### Features

- **Asynchronous Processing**: 이메일을 큐에 등록 후 백그라운드에서 처리
- **Auto Retry**: 실패 시 자동 재시도 (최대 3회)
- **Scheduled Processing**: 1분마다 대기 중인 이메일 처리
- **Auto Cleanup**: 30일 이상 경과한 완료/실패 작업 자동 삭제

### Job Status

- `PENDING`: 발송 대기
- `PROCESSING`: 처리 중
- `RETRYING`: 재시도 중
- `SENT`: 발송 완료
- `FAILED`: 발송 실패 (재시도 초과)

### Scheduled Jobs

```java
@Scheduled(fixedDelay = 60000)  // 1분마다
public void processPendingJobs()

@Scheduled(cron = "0 0 0 * * *")  // 매일 자정
public void cleanupOldJobs()

@Scheduled(fixedDelay = 600000)  // 10분마다
public void logQueueStatus()
```

### Monitoring

Queue status is logged every 10 minutes:
```
Email Queue Status - Pending: 5, Processing: 2, Retrying: 1, Sent: 142, Failed: 3
```

## Integration Examples

### 1. User Registration (AuthService)

```java
@Transactional
public UserProfileResponse register(RegisterRequest request) {
    // ... create user logic ...

    // Generate verification token
    String verificationToken = UUID.randomUUID().toString();

    // Send welcome + verification emails
    sendWelcomeAndVerificationEmail(user, verificationToken);

    return mapToUserProfileResponse(user);
}
```

### 2. Password Reset (PasswordResetService)

```java
@Transactional
public void requestPasswordReset(PasswordResetRequest request) {
    // ... create reset token logic ...

    // Send password reset email
    sendPasswordResetEmail(user, token);
}
```

### 3. Course Invitation (EnrollmentService)

```java
@Transactional
public List<EnrollmentResponse> bulkEnrollFromCsv(Long courseId, MultipartFile file) {
    // ... process CSV ...

    if (user == null) {
        // Send invitation email to non-existing users
        sendCourseInvitationEmail(email, firstName, lastName, course);
        continue;
    }

    // ... enroll existing users ...
}
```

## Database Schema

### email_jobs Table

```sql
CREATE TABLE email_jobs (
    id BIGSERIAL PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body TEXT,
    template_name VARCHAR(100),
    template_variables TEXT,  -- JSON
    is_html BOOLEAN NOT NULL DEFAULT true,
    from_name VARCHAR(100),
    reply_to VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    error_message TEXT,
    message_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);
```

## Error Handling

### Error Codes

- `EM001`: EMAIL_SEND_FAILED - 이메일 발송 실패
- `EM002`: EMAIL_TEMPLATE_NOT_FOUND - 템플릿을 찾을 수 없음
- `EM003`: EMAIL_TEMPLATE_RENDER_FAILED - 템플릿 렌더링 실패
- `EM004`: EMAIL_JOB_NOT_FOUND - 이메일 작업을 찾을 수 없음
- `EM005`: EMAIL_INVALID_RECIPIENT - 유효하지 않은 수신자

### Best Practices

1. **Always use async for user-facing operations**
   ```java
   // Good - doesn't block user registration
   emailService.sendAsync(request);

   // Bad - blocks user until email is sent
   emailService.send(request);
   ```

2. **Wrap email sending in try-catch**
   ```java
   try {
       emailService.sendAsync(request);
   } catch (Exception e) {
       log.error("Failed to queue email", e);
       // Don't fail the main operation
   }
   ```

3. **Provide fallback for template variables**
   ```java
   variables.put("description",
       course.getDescription() != null ? course.getDescription() : "");
   ```

## Testing

### Development Mode (Console)

```yaml
app:
  email:
    provider: console
```

Output:
```
================================================================================
📧 EMAIL (Console Mode)
================================================================================
To:       user@example.com
Subject:  Welcome to EduForum
From:     EduForum
ReplyTo:  N/A
Type:     HTML
--------------------------------------------------------------------------------
<html>...</html>
================================================================================
```

### SMTP Testing (Gmail)

1. Enable 2FA in Google Account
2. Generate App Password
3. Configure:
   ```yaml
   spring.mail:
     username: your-email@gmail.com
     password: your-app-password
   ```

### SendGrid Testing

1. Create SendGrid account
2. Generate API Key
3. Configure:
   ```yaml
   sendgrid:
     api-key: SG.xxxxxxxxxxxxx
   ```

## Troubleshooting

### SMTP Connection Timeout

**Problem**: `Could not connect to SMTP host`

**Solution**:
```yaml
spring.mail:
  properties:
    mail:
      smtp:
        connectiontimeout: 10000
        timeout: 10000
```

### SendGrid 403 Forbidden

**Problem**: `SendGrid API error: 403`

**Solution**:
- Verify API key is correct
- Check API key permissions (Mail Send)
- Verify sender email is verified in SendGrid

### Emails Not Being Sent

**Problem**: Emails stuck in `PENDING` status

**Solution**:
- Check scheduler is enabled: `@EnableScheduling`
- Verify email provider configuration
- Check application logs for errors

### Template Not Found

**Problem**: `Template not found: welcome.html`

**Solution**:
- Verify template exists in `src/main/resources/templates/email/`
- Check template file name matches exactly (case-sensitive)
- Rebuild project

## Performance Considerations

- **Batch Processing**: Queue processes up to 10 emails per minute
- **Retry Strategy**: 3 retries with exponential backoff
- **Cleanup**: Old jobs deleted after 30 days
- **Connection Pool**: SMTP uses connection pooling

## Security

- **Email Validation**: All recipient emails are validated
- **No Sensitive Data**: Never include passwords in emails
- **Token Expiry**: All email tokens have expiration times
- **Rate Limiting**: Consider implementing rate limiting for production

## Related Issues

- #6: 이메일 발송 서비스 통합
- #29: 재설정 이메일 발송 로직
- #88: 초대 이메일 발송 큐 처리
