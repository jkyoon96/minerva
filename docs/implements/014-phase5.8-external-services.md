# Phase 5.8: 외부 서비스 연동 (옵션 B)

**작업일**: 2024-11-29
**커밋**: 8eda12c

## 개요

이메일 발송 서비스와 파일 스토리지 서비스를 구현하고, 다양한 프로바이더를 지원하도록 구성했습니다.

## 구현 이슈

| 이슈 | 제목 | 상태 |
|------|------|------|
| #6 | [E1-S1-T5] 이메일 발송 서비스 통합 | ✅ 완료 |
| #9 | [E1-S1-T8] 이메일 인증 완료 페이지 | ✅ 완료 |
| #29 | [E1-S5-T2] 재설정 이메일 발송 로직 | ✅ 완료 |
| #88 | [E2-S2-T3] 초대 이메일 발송 큐 처리 | ✅ 완료 |
| #104 | [E2-S5-T1] 파일 업로드 API (S3 연동) | ✅ 완료 |
| #105 | [E2-S5-T2] 폴더 구조 CRUD API | ✅ 완료 |
| #106 | [E2-S5-T3] 파일 권한 관리 로직 | ✅ 완료 |
| #107 | [E2-S5-T4] 파일 검색 API | ✅ 완료 |
| #108 | [E2-S5-T5] 콘텐츠 라이브러리 UI | ✅ 완료 |
| #109 | [E2-S5-T6] 드래그 앤 드롭 업로드 | ✅ 완료 |

## 이메일 서비스

### 아키텍처

```
┌─────────────────┐     ┌──────────────────┐
│  EmailService   │────▶│ EmailQueueService │
│   (Interface)   │     └────────┬─────────┘
└────────┬────────┘              │
         │                       ▼
    ┌────┴────┬─────────────────────────────┐
    │         │                             │
    ▼         ▼                             ▼
┌───────┐ ┌───────┐ ┌───────────┐    ┌─────────────┐
│Console│ │ SMTP  │ │ SendGrid  │    │EmailJobProc │
│Service│ │Service│ │ Service   │    │  (Scheduled)│
└───────┘ └───────┘ └───────────┘    └─────────────┘
```

### 구현 파일

**인터페이스 & 구현체**:
- `EmailService.java` - 이메일 서비스 인터페이스
- `ConsoleEmailService.java` - 개발용 콘솔 출력
- `SmtpEmailService.java` - SMTP 프로바이더
- `SendGridEmailService.java` - SendGrid API

**비동기 큐**:
- `EmailQueue.java` - 이메일 큐 엔티티
- `EmailQueueRepository.java` - 리포지토리
- `EmailQueueService.java` - 큐 관리 서비스
- `EmailJobProcessor.java` - 스케줄드 프로세서

**템플릿**:
- `welcome.html` - 환영 이메일
- `email-verification.html` - 이메일 인증
- `password-reset.html` - 비밀번호 재설정
- `course-invitation.html` - 코스 초대

### 설정

```yaml
# application.yml
app:
  mail:
    provider: ${MAIL_PROVIDER:console}  # console, smtp, sendgrid
    from: ${MAIL_FROM:noreply@eduforum.com}

# SMTP 설정
spring:
  mail:
    host: ${SMTP_HOST:smtp.gmail.com}
    port: ${SMTP_PORT:587}
    username: ${SMTP_USERNAME:}
    password: ${SMTP_PASSWORD:}

# SendGrid 설정
app:
  sendgrid:
    api-key: ${SENDGRID_API_KEY:}
```

### 이메일 큐 처리

```java
@Scheduled(fixedDelay = 30000) // 30초마다
public void processEmailQueue() {
    List<EmailQueue> pendingEmails = repository
        .findByStatusOrderByCreatedAtAsc(EmailStatus.PENDING);

    for (EmailQueue email : pendingEmails) {
        try {
            emailService.send(email.getTo(), email.getSubject(),
                             email.getBody(), email.isHtml());
            email.setStatus(EmailStatus.SENT);
            email.setSentAt(OffsetDateTime.now());
        } catch (Exception e) {
            email.setRetryCount(email.getRetryCount() + 1);
            if (email.getRetryCount() >= MAX_RETRIES) {
                email.setStatus(EmailStatus.FAILED);
            }
            email.setErrorMessage(e.getMessage());
        }
        repository.save(email);
    }
}
```

## 파일 스토리지 서비스

### 아키텍처

```
┌─────────────────┐
│ StorageService  │
│   (Interface)   │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌───────┐ ┌───────┐
│ Local │ │  S3   │
│Storage│ │Storage│
└───────┘ └───────┘
```

### 구현 파일

**엔티티**:
- `StoredFile.java` - 파일 엔티티
- `FileFolder.java` - 폴더 엔티티
- `FilePermission.java` - 파일 권한 엔티티

**서비스**:
- `StorageService.java` - 스토리지 인터페이스
- `LocalStorageService.java` - 로컬 파일시스템
- `S3StorageService.java` - AWS S3
- `FileService.java` - 파일 비즈니스 로직
- `FolderService.java` - 폴더 비즈니스 로직

**컨트롤러**:
- `FileController.java` - 파일 API (12개 엔드포인트)
- `FolderController.java` - 폴더 API (7개 엔드포인트)

### API 엔드포인트

**파일 API**:
```
POST   /v1/courses/{courseId}/files          # 업로드
GET    /v1/courses/{courseId}/files          # 목록
GET    /v1/files/{fileId}                    # 상세
GET    /v1/files/{fileId}/download           # 다운로드
PUT    /v1/files/{fileId}                    # 수정
DELETE /v1/files/{fileId}                    # 삭제
POST   /v1/files/{fileId}/copy               # 복사
POST   /v1/files/{fileId}/move               # 이동
GET    /v1/courses/{courseId}/files/search   # 검색
POST   /v1/files/bulk-delete                 # 일괄 삭제
POST   /v1/files/{fileId}/permissions        # 권한 추가
DELETE /v1/files/{fileId}/permissions/{id}   # 권한 삭제
```

**폴더 API**:
```
POST   /v1/courses/{courseId}/folders        # 생성
GET    /v1/courses/{courseId}/folders        # 목록
GET    /v1/folders/{folderId}                # 상세
PUT    /v1/folders/{folderId}                # 수정
DELETE /v1/folders/{folderId}                # 삭제
POST   /v1/folders/{folderId}/move           # 이동
GET    /v1/courses/{courseId}/folders/tree   # 트리 구조
```

### 설정

```yaml
app:
  storage:
    provider: ${STORAGE_PROVIDER:local}  # local, s3
    local:
      base-path: ${STORAGE_LOCAL_PATH:./uploads}
    s3:
      bucket: ${AWS_S3_BUCKET:}
      region: ${AWS_REGION:ap-northeast-2}
      access-key: ${AWS_ACCESS_KEY:}
      secret-key: ${AWS_SECRET_KEY:}
```

## 프론트엔드 구현

### 파일 관리 UI

**파일**:
- `src/app/(dashboard)/courses/[courseId]/files/page.tsx`
- `src/components/files/file-upload-dropzone.tsx`
- `src/components/files/file-list.tsx`
- `src/components/files/folder-tree.tsx`
- `src/components/files/file-preview-modal.tsx`

**기능**:
- 드래그 앤 드롭 업로드
- 폴더 트리 네비게이션
- 파일 미리보기 (이미지, PDF, 텍스트)
- 파일 검색
- 일괄 선택 및 삭제
- 권한 관리 UI

### 이메일 인증 페이지

**파일**: `src/app/(auth)/verify-email/page.tsx`

**기능**:
- 토큰 기반 이메일 인증
- 인증 성공/실패 UI
- 재발송 요청 기능

## 데이터베이스 마이그레이션

### V010: 이메일 큐

```sql
CREATE TABLE email_queue (
    id BIGSERIAL PRIMARY KEY,
    to_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,
    is_html BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'PENDING',
    retry_count INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_email_queue_status ON email_queue(status);
```

### V011: 파일 스토리지

```sql
-- 폴더 테이블
CREATE TABLE file_folders (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES courses(id),
    parent_id BIGINT REFERENCES file_folders(id),
    name VARCHAR(255) NOT NULL,
    path VARCHAR(1000) NOT NULL,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 파일 테이블
CREATE TABLE stored_files (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES courses(id),
    folder_id BIGINT REFERENCES file_folders(id),
    original_name VARCHAR(500) NOT NULL,
    stored_name VARCHAR(500) NOT NULL,
    storage_path VARCHAR(1000) NOT NULL,
    content_type VARCHAR(255),
    file_size BIGINT NOT NULL,
    checksum VARCHAR(64),
    uploaded_by BIGINT REFERENCES users(id),
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 파일 권한 테이블
CREATE TABLE file_permissions (
    id BIGSERIAL PRIMARY KEY,
    file_id BIGINT REFERENCES stored_files(id),
    user_id BIGINT REFERENCES users(id),
    permission_type VARCHAR(20) NOT NULL,
    granted_by BIGINT REFERENCES users(id),
    granted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(file_id, user_id, permission_type)
);
```

## 통계

| 항목 | 수량 |
|------|------|
| 생성된 파일 | 70개 |
| 총 코드 라인 | 8,703줄 |
| API 엔드포인트 | 19개 |
| DB 테이블 | 3개 |
| 이메일 템플릿 | 4개 |

## 테스트

```bash
# 이메일 발송 테스트 (Console 모드)
curl -X POST http://localhost:8080/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com"}'
# 콘솔에서 이메일 내용 확인

# 파일 업로드 테스트
curl -X POST http://localhost:8080/v1/courses/1/files \
  -H "Authorization: Bearer {token}" \
  -F "file=@test.pdf"

# 폴더 생성 테스트
curl -X POST http://localhost:8080/v1/courses/1/folders \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"name":"Week 1 Materials"}'
```

## 다음 단계

- 옵션 C: 프로젝트 마무리 (Docker, CI/CD, 테스트, 문서)
