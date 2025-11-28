# EduForum API 설계서 (OpenAPI 3.0)

> **버전**: 1.0.0
> **최종 수정일**: 2025-01-28
> **Base URL**: `https://api.eduforum.com/v1`
> **참조 문서**: PRD, 시스템 아키텍처, 데이터베이스 설계

---

## 목차

1. [API 개요](#1-api-개요)
2. [공통 규격](#2-공통-규격)
3. [인증 API (Authentication)](#3-인증-api-authentication)
4. [사용자 API (Users)](#4-사용자-api-users)
5. [코스 API (Courses)](#5-코스-api-courses)
6. [세션 API (Sessions)](#6-세션-api-sessions)
7. [실시간 세션 API (Live)](#7-실시간-세션-api-live)
8. [액티브 러닝 API (Active Learning)](#8-액티브-러닝-api-active-learning)
9. [평가 API (Assessment)](#9-평가-api-assessment)
10. [분석 API (Analytics)](#10-분석-api-analytics)
11. [WebSocket API](#11-websocket-api)

---

## 1. API 개요

### 1.1 OpenAPI 기본 정보

```yaml
openapi: 3.0.3
info:
  title: EduForum API
  description: 대학교/교육기관용 온라인 학습 플랫폼 API
  version: 1.0.0
  contact:
    name: EduForum Development Team
    email: dev@eduforum.com
  license:
    name: MIT
    url: https://opensource.org/licenses/MIT

servers:
  - url: https://api.eduforum.com/v1
    description: Production Server
  - url: https://staging-api.eduforum.com/v1
    description: Staging Server
  - url: http://localhost:8000/api/v1
    description: Local Development

tags:
  - name: Authentication
    description: 인증 및 토큰 관리
  - name: Users
    description: 사용자 프로필 관리
  - name: Courses
    description: 코스 생성 및 관리
  - name: Sessions
    description: 세션 스케줄링
  - name: Live
    description: 실시간 세션 (WebRTC)
  - name: Polls
    description: 투표/설문
  - name: Quizzes
    description: 퀴즈
  - name: Breakouts
    description: 분반 토론
  - name: Assignments
    description: 과제 관리
  - name: Grades
    description: 성적 관리
  - name: Analytics
    description: 학습 분석
```

### 1.2 API 설계 원칙

| 원칙 | 설명 |
|------|------|
| RESTful | 리소스 중심 URL, HTTP 메서드 활용 |
| JSON | 요청/응답 본문은 JSON 형식 |
| Versioning | URL 경로 버저닝 (`/v1/`) |
| Pagination | 리스트 응답은 커서 기반 페이지네이션 |
| Idempotency | PUT, DELETE는 멱등성 보장 |
| HATEOAS | 관련 리소스 링크 포함 (선택적) |

---

## 2. 공통 규격

### 2.1 인증 (Security Schemes)

```yaml
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: |
        JWT Access Token을 Authorization 헤더에 포함
        예시: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

    OAuth2:
      type: oauth2
      flows:
        authorizationCode:
          authorizationUrl: https://api.eduforum.com/oauth/authorize
          tokenUrl: https://api.eduforum.com/oauth/token
          scopes:
            read:profile: 프로필 읽기
            write:profile: 프로필 수정
            read:courses: 코스 정보 읽기
            write:courses: 코스 생성/수정
            read:grades: 성적 조회
            write:grades: 성적 입력
```

### 2.2 공통 헤더

| 헤더 | 필수 | 설명 |
|------|------|------|
| `Authorization` | O | Bearer {access_token} |
| `Content-Type` | O | application/json |
| `Accept` | - | application/json |
| `Accept-Language` | - | ko-KR, en-US |
| `X-Request-ID` | - | 요청 추적용 UUID |
| `X-Idempotency-Key` | - | 멱등성 보장 키 (POST 요청) |

### 2.3 공통 응답 형식

#### 성공 응답 (Single Resource)

```json
{
  "success": true,
  "data": {
    "id": "123",
    "type": "user",
    "attributes": { ... }
  },
  "meta": {
    "requestId": "req_abc123",
    "timestamp": "2025-01-28T09:00:00Z"
  }
}
```

#### 성공 응답 (Collection)

```json
{
  "success": true,
  "data": [
    { "id": "1", "type": "course", "attributes": { ... } },
    { "id": "2", "type": "course", "attributes": { ... } }
  ],
  "meta": {
    "pagination": {
      "total": 100,
      "page": 1,
      "perPage": 20,
      "totalPages": 5,
      "nextCursor": "eyJpZCI6MjB9",
      "prevCursor": null
    }
  }
}
```

#### 오류 응답

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력값이 올바르지 않습니다.",
    "details": [
      {
        "field": "email",
        "message": "유효한 이메일 형식이 아닙니다."
      }
    ]
  },
  "meta": {
    "requestId": "req_abc123",
    "timestamp": "2025-01-28T09:00:00Z"
  }
}
```

### 2.4 HTTP 상태 코드

| 코드 | 의미 | 사용 상황 |
|------|------|----------|
| 200 | OK | 성공 (GET, PUT, PATCH) |
| 201 | Created | 리소스 생성 성공 (POST) |
| 204 | No Content | 삭제 성공 (DELETE) |
| 400 | Bad Request | 잘못된 요청 형식 |
| 401 | Unauthorized | 인증 필요 또는 토큰 만료 |
| 403 | Forbidden | 권한 없음 |
| 404 | Not Found | 리소스 없음 |
| 409 | Conflict | 중복 리소스 |
| 422 | Unprocessable Entity | 유효성 검증 실패 |
| 429 | Too Many Requests | Rate Limit 초과 |
| 500 | Internal Server Error | 서버 오류 |

### 2.5 에러 코드 목록

```yaml
components:
  schemas:
    ErrorCode:
      type: string
      enum:
        # 인증 관련
        - AUTH_INVALID_CREDENTIALS      # 잘못된 자격 증명
        - AUTH_TOKEN_EXPIRED            # 토큰 만료
        - AUTH_TOKEN_INVALID            # 유효하지 않은 토큰
        - AUTH_2FA_REQUIRED             # 2FA 인증 필요
        - AUTH_2FA_INVALID              # 잘못된 2FA 코드
        - AUTH_ACCOUNT_LOCKED           # 계정 잠금
        - AUTH_EMAIL_NOT_VERIFIED       # 이메일 미인증

        # 권한 관련
        - FORBIDDEN_ACCESS              # 접근 권한 없음
        - FORBIDDEN_ROLE                # 역할 권한 없음

        # 리소스 관련
        - RESOURCE_NOT_FOUND            # 리소스 없음
        - RESOURCE_ALREADY_EXISTS       # 이미 존재
        - RESOURCE_DELETED              # 삭제된 리소스

        # 유효성 검증
        - VALIDATION_ERROR              # 유효성 검증 실패
        - VALIDATION_REQUIRED           # 필수 필드 누락
        - VALIDATION_FORMAT             # 형식 오류

        # 비즈니스 로직
        - COURSE_ENROLLMENT_FULL        # 수강 인원 초과
        - COURSE_ENROLLMENT_CLOSED      # 등록 마감
        - SESSION_NOT_STARTED           # 세션 미시작
        - SESSION_ALREADY_ENDED         # 세션 종료됨
        - QUIZ_TIME_EXPIRED             # 퀴즈 시간 초과
        - ASSIGNMENT_PAST_DUE           # 과제 마감 지남

        # 시스템
        - RATE_LIMIT_EXCEEDED           # Rate Limit 초과
        - SERVICE_UNAVAILABLE           # 서비스 불가
        - INTERNAL_ERROR                # 내부 오류
```

### 2.6 페이지네이션

#### Query Parameters

| 파라미터 | 타입 | 기본값 | 설명 |
|----------|------|--------|------|
| `page` | integer | 1 | 페이지 번호 |
| `per_page` | integer | 20 | 페이지당 항목 수 (max: 100) |
| `cursor` | string | - | 커서 기반 페이지네이션 |
| `sort` | string | -created_at | 정렬 기준 (- prefix: 내림차순) |

### 2.7 필터링 및 검색

```
GET /courses?status=active&instructor_id=123&q=프로그래밍
GET /users?role=student&created_at[gte]=2025-01-01
```

### 2.8 Rate Limiting

| 엔드포인트 그룹 | 제한 | 윈도우 |
|----------------|------|--------|
| 인증 API | 10 req | 1분 |
| 읽기 API | 100 req | 1분 |
| 쓰기 API | 30 req | 1분 |
| 파일 업로드 | 10 req | 1분 |

응답 헤더:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1706432460
```

---

## 3. 인증 API (Authentication)

### 3.1 회원가입

```yaml
/auth/register:
  post:
    tags: [Authentication]
    summary: 신규 회원가입
    operationId: registerUser
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [email, password, firstName, lastName]
            properties:
              email:
                type: string
                format: email
                example: "student@university.edu"
              password:
                type: string
                format: password
                minLength: 8
                pattern: "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
                example: "SecurePass123!"
              firstName:
                type: string
                maxLength: 50
                example: "홍"
              lastName:
                type: string
                maxLength: 50
                example: "길동"
              phone:
                type: string
                pattern: "^\\d{10,11}$"
                example: "01012345678"
    responses:
      201:
        description: 회원가입 성공 (이메일 인증 필요)
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserResponse'
            example:
              success: true
              data:
                id: "usr_abc123"
                email: "student@university.edu"
                firstName: "홍"
                lastName: "길동"
                status: "pending_verification"
                createdAt: "2025-01-28T09:00:00Z"
              meta:
                message: "인증 이메일이 발송되었습니다."
      409:
        description: 이메일 중복
        content:
          application/json:
            example:
              success: false
              error:
                code: "RESOURCE_ALREADY_EXISTS"
                message: "이미 등록된 이메일입니다."
      422:
        description: 유효성 검증 실패
```

### 3.2 이메일 인증

```yaml
/auth/verify-email:
  post:
    tags: [Authentication]
    summary: 이메일 인증 확인
    operationId: verifyEmail
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [token]
            properties:
              token:
                type: string
                description: 이메일로 발송된 인증 토큰
                example: "eyJhbGciOiJIUzI1NiIs..."
    responses:
      200:
        description: 이메일 인증 성공
        content:
          application/json:
            example:
              success: true
              data:
                verified: true
                message: "이메일이 인증되었습니다. 로그인해 주세요."
      400:
        description: 유효하지 않거나 만료된 토큰

/auth/resend-verification:
  post:
    tags: [Authentication]
    summary: 인증 이메일 재발송
    operationId: resendVerification
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [email]
            properties:
              email:
                type: string
                format: email
    responses:
      200:
        description: 이메일 발송됨 (존재 여부 무관하게 동일 응답)
```

### 3.3 로그인

```yaml
/auth/login:
  post:
    tags: [Authentication]
    summary: 이메일/비밀번호 로그인
    operationId: login
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [email, password]
            properties:
              email:
                type: string
                format: email
              password:
                type: string
                format: password
              rememberMe:
                type: boolean
                default: false
                description: true시 7일, false시 24시간 토큰 유효
    responses:
      200:
        description: 로그인 성공
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AuthTokenResponse'
            example:
              success: true
              data:
                accessToken: "eyJhbGciOiJSUzI1NiIs..."
                refreshToken: "eyJhbGciOiJSUzI1NiIs..."
                tokenType: "Bearer"
                expiresIn: 3600
                user:
                  id: "usr_abc123"
                  email: "student@university.edu"
                  firstName: "홍"
                  lastName: "길동"
                  roles: ["student"]
                  profileImageUrl: "https://cdn.eduforum.com/profiles/abc123.jpg"
      401:
        description: 인증 실패
        content:
          application/json:
            examples:
              invalid_credentials:
                value:
                  success: false
                  error:
                    code: "AUTH_INVALID_CREDENTIALS"
                    message: "이메일 또는 비밀번호가 올바르지 않습니다."
              account_locked:
                value:
                  success: false
                  error:
                    code: "AUTH_ACCOUNT_LOCKED"
                    message: "계정이 잠겼습니다. 15분 후 다시 시도해 주세요."
                    details:
                      lockedUntil: "2025-01-28T09:15:00Z"
      403:
        description: 2FA 인증 필요
        content:
          application/json:
            example:
              success: false
              error:
                code: "AUTH_2FA_REQUIRED"
                message: "2단계 인증이 필요합니다."
              data:
                tempToken: "tmp_xyz789"
                methods: ["totp", "backup_code"]
```

### 3.4 2FA 인증

```yaml
/auth/2fa/verify:
  post:
    tags: [Authentication]
    summary: 2FA 코드 검증
    operationId: verify2FA
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [tempToken, code]
            properties:
              tempToken:
                type: string
                description: 로그인 시 받은 임시 토큰
              code:
                type: string
                pattern: "^\\d{6}$"
                description: TOTP 코드 또는 백업 코드
              method:
                type: string
                enum: [totp, backup_code]
                default: totp
    responses:
      200:
        description: 2FA 인증 성공
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AuthTokenResponse'
      401:
        description: 잘못된 코드

/auth/2fa/setup:
  post:
    tags: [Authentication]
    summary: 2FA 설정 시작
    operationId: setup2FA
    security:
      - BearerAuth: []
    responses:
      200:
        description: 2FA 설정 정보
        content:
          application/json:
            example:
              success: true
              data:
                secret: "JBSWY3DPEHPK3PXP"
                qrCodeUrl: "data:image/png;base64,..."
                manualEntryKey: "JBSW Y3DP EHPK 3PXP"

/auth/2fa/enable:
  post:
    tags: [Authentication]
    summary: 2FA 활성화 완료
    operationId: enable2FA
    security:
      - BearerAuth: []
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [code]
            properties:
              code:
                type: string
                pattern: "^\\d{6}$"
    responses:
      200:
        description: 2FA 활성화 완료
        content:
          application/json:
            example:
              success: true
              data:
                enabled: true
                backupCodes:
                  - "A1B2C3D4"
                  - "E5F6G7H8"
                  - "I9J0K1L2"
                  - "M3N4O5P6"
                  - "Q7R8S9T0"
                message: "백업 코드를 안전한 곳에 보관하세요."

/auth/2fa/disable:
  post:
    tags: [Authentication]
    summary: 2FA 비활성화
    operationId: disable2FA
    security:
      - BearerAuth: []
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [password]
            properties:
              password:
                type: string
                description: 현재 비밀번호 확인
    responses:
      200:
        description: 2FA 비활성화 완료
```

### 3.5 OAuth 소셜 로그인

```yaml
/auth/oauth/{provider}:
  get:
    tags: [Authentication]
    summary: OAuth 로그인 시작
    operationId: startOAuth
    parameters:
      - name: provider
        in: path
        required: true
        schema:
          type: string
          enum: [google, microsoft]
      - name: redirect_uri
        in: query
        required: true
        schema:
          type: string
          format: uri
    responses:
      302:
        description: OAuth 제공자로 리다이렉트

/auth/oauth/{provider}/callback:
  post:
    tags: [Authentication]
    summary: OAuth 콜백 처리
    operationId: handleOAuthCallback
    parameters:
      - name: provider
        in: path
        required: true
        schema:
          type: string
          enum: [google, microsoft]
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [code]
            properties:
              code:
                type: string
                description: OAuth authorization code
              state:
                type: string
    responses:
      200:
        description: OAuth 로그인 성공
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AuthTokenResponse'
      201:
        description: 신규 계정 생성됨
```

### 3.6 토큰 갱신

```yaml
/auth/refresh:
  post:
    tags: [Authentication]
    summary: Access Token 갱신
    operationId: refreshToken
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [refreshToken]
            properties:
              refreshToken:
                type: string
    responses:
      200:
        description: 토큰 갱신 성공
        content:
          application/json:
            example:
              success: true
              data:
                accessToken: "eyJhbGciOiJSUzI1NiIs..."
                expiresIn: 3600
      401:
        description: Refresh Token 만료 또는 무효
```

### 3.7 로그아웃

```yaml
/auth/logout:
  post:
    tags: [Authentication]
    summary: 로그아웃
    operationId: logout
    security:
      - BearerAuth: []
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              allDevices:
                type: boolean
                default: false
                description: 모든 기기에서 로그아웃
    responses:
      204:
        description: 로그아웃 성공
```

### 3.8 비밀번호 재설정

```yaml
/auth/forgot-password:
  post:
    tags: [Authentication]
    summary: 비밀번호 재설정 요청
    operationId: forgotPassword
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [email]
            properties:
              email:
                type: string
                format: email
    responses:
      200:
        description: 이메일 발송됨 (존재 여부 무관)
        content:
          application/json:
            example:
              success: true
              data:
                message: "비밀번호 재설정 링크가 발송되었습니다."

/auth/reset-password:
  post:
    tags: [Authentication]
    summary: 새 비밀번호 설정
    operationId: resetPassword
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [token, password]
            properties:
              token:
                type: string
              password:
                type: string
                format: password
                minLength: 8
              confirmPassword:
                type: string
    responses:
      200:
        description: 비밀번호 변경 완료
      400:
        description: 토큰 만료 또는 무효
```

---

## 4. 사용자 API (Users)

### 4.1 내 프로필 조회/수정

```yaml
/users/me:
  get:
    tags: [Users]
    summary: 내 프로필 조회
    operationId: getMyProfile
    security:
      - BearerAuth: []
    responses:
      200:
        description: 프로필 정보
        content:
          application/json:
            example:
              success: true
              data:
                id: "usr_abc123"
                email: "student@university.edu"
                firstName: "홍"
                lastName: "길동"
                phone: "01012345678"
                profileImageUrl: "https://cdn.eduforum.com/profiles/abc123.jpg"
                bio: "컴퓨터공학 전공 3학년"
                status: "active"
                roles:
                  - name: "student"
                    assignedAt: "2025-01-01T00:00:00Z"
                settings:
                  language: "ko"
                  timezone: "Asia/Seoul"
                  notifications:
                    email: true
                    push: true
                twoFactorEnabled: true
                emailVerifiedAt: "2025-01-01T00:05:00Z"
                lastLoginAt: "2025-01-28T08:00:00Z"
                createdAt: "2025-01-01T00:00:00Z"

  patch:
    tags: [Users]
    summary: 내 프로필 수정
    operationId: updateMyProfile
    security:
      - BearerAuth: []
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            properties:
              firstName:
                type: string
                maxLength: 50
              lastName:
                type: string
                maxLength: 50
              phone:
                type: string
              bio:
                type: string
                maxLength: 500
              settings:
                type: object
                properties:
                  language:
                    type: string
                    enum: [ko, en]
                  timezone:
                    type: string
                  notifications:
                    type: object
    responses:
      200:
        description: 수정 완료
```

### 4.2 프로필 이미지 업로드

```yaml
/users/me/profile-image:
  post:
    tags: [Users]
    summary: 프로필 이미지 업로드
    operationId: uploadProfileImage
    security:
      - BearerAuth: []
    requestBody:
      required: true
      content:
        multipart/form-data:
          schema:
            type: object
            required: [file]
            properties:
              file:
                type: string
                format: binary
                description: JPEG/PNG, 최대 2MB
    responses:
      200:
        description: 업로드 성공
        content:
          application/json:
            example:
              success: true
              data:
                profileImageUrl: "https://cdn.eduforum.com/profiles/abc123_v2.jpg"

  delete:
    tags: [Users]
    summary: 프로필 이미지 삭제
    operationId: deleteProfileImage
    security:
      - BearerAuth: []
    responses:
      204:
        description: 삭제 완료
```

### 4.3 비밀번호 변경

```yaml
/users/me/password:
  put:
    tags: [Users]
    summary: 비밀번호 변경
    operationId: changePassword
    security:
      - BearerAuth: []
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [currentPassword, newPassword]
            properties:
              currentPassword:
                type: string
              newPassword:
                type: string
                minLength: 8
              confirmPassword:
                type: string
    responses:
      200:
        description: 비밀번호 변경 완료
      401:
        description: 현재 비밀번호 불일치
```

### 4.4 이메일 변경

```yaml
/users/me/email:
  put:
    tags: [Users]
    summary: 이메일 변경 요청
    operationId: requestEmailChange
    security:
      - BearerAuth: []
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [newEmail, password]
            properties:
              newEmail:
                type: string
                format: email
              password:
                type: string
                description: 현재 비밀번호 확인
    responses:
      200:
        description: 인증 이메일 발송됨
      409:
        description: 이메일 이미 사용 중

/users/me/email/confirm:
  post:
    tags: [Users]
    summary: 이메일 변경 확인
    operationId: confirmEmailChange
    security:
      - BearerAuth: []
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [token]
            properties:
              token:
                type: string
    responses:
      200:
        description: 이메일 변경 완료
```

### 4.5 사용자 목록 조회 (관리자)

```yaml
/users:
  get:
    tags: [Users]
    summary: 사용자 목록 조회
    operationId: listUsers
    security:
      - BearerAuth: []
    parameters:
      - name: role
        in: query
        schema:
          type: string
          enum: [admin, instructor, ta, student]
      - name: status
        in: query
        schema:
          type: string
          enum: [active, inactive, pending_verification]
      - name: q
        in: query
        schema:
          type: string
        description: 이름 또는 이메일 검색
      - $ref: '#/components/parameters/PageParam'
      - $ref: '#/components/parameters/PerPageParam'
      - $ref: '#/components/parameters/SortParam'
    responses:
      200:
        description: 사용자 목록
      403:
        description: 관리자 권한 필요
```

### 4.6 역할 관리 (관리자)

```yaml
/users/{userId}/roles:
  get:
    tags: [Users]
    summary: 사용자 역할 조회
    operationId: getUserRoles
    security:
      - BearerAuth: []
    parameters:
      - name: userId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 역할 목록

  post:
    tags: [Users]
    summary: 사용자에게 역할 부여
    operationId: assignRole
    security:
      - BearerAuth: []
    parameters:
      - name: userId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [roleId]
            properties:
              roleId:
                type: string
              courseId:
                type: string
                description: 코스 한정 역할 (TA 등)
    responses:
      201:
        description: 역할 부여 완료
      403:
        description: 관리자 권한 필요

  delete:
    tags: [Users]
    summary: 사용자 역할 제거
    operationId: removeRole
    security:
      - BearerAuth: []
    parameters:
      - name: userId
        in: path
        required: true
        schema:
          type: string
      - name: roleId
        in: query
        required: true
        schema:
          type: string
    responses:
      204:
        description: 역할 제거 완료
```

---

## 공통 컴포넌트 (Schemas)

```yaml
components:
  schemas:
    UserResponse:
      type: object
      properties:
        id:
          type: string
        email:
          type: string
          format: email
        firstName:
          type: string
        lastName:
          type: string
        profileImageUrl:
          type: string
          format: uri
          nullable: true
        status:
          type: string
          enum: [active, inactive, pending_verification]
        roles:
          type: array
          items:
            type: string
        createdAt:
          type: string
          format: date-time
        updatedAt:
          type: string
          format: date-time

    AuthTokenResponse:
      type: object
      properties:
        accessToken:
          type: string
        refreshToken:
          type: string
        tokenType:
          type: string
          default: "Bearer"
        expiresIn:
          type: integer
          description: Access Token 만료 시간 (초)
        user:
          $ref: '#/components/schemas/UserResponse'

  parameters:
    PageParam:
      name: page
      in: query
      schema:
        type: integer
        minimum: 1
        default: 1

    PerPageParam:
      name: per_page
      in: query
      schema:
        type: integer
        minimum: 1
        maximum: 100
        default: 20

    SortParam:
      name: sort
      in: query
      schema:
        type: string
      description: "정렬 기준 (- prefix: 내림차순). 예: -created_at"
```

---

> **다음 파트**: Part 2에서 코스 관리 API (Courses, Enrollments, Sessions, Content, Assignments)를 작성합니다.

---

## 5. 코스 API (Courses)

### 5.1 코스 목록 조회

```yaml
/courses:
  get:
    tags: [Courses]
    summary: 코스 목록 조회
    operationId: listCourses
    security:
      - BearerAuth: []
    parameters:
      - name: status
        in: query
        schema:
          type: string
          enum: [draft, active, archived]
      - name: semester
        in: query
        schema:
          type: string
          example: "2025-1"
      - name: instructor_id
        in: query
        schema:
          type: string
      - name: enrolled
        in: query
        schema:
          type: boolean
        description: "true: 내가 수강 중인 코스만"
      - name: q
        in: query
        schema:
          type: string
        description: 코스명/코드 검색
      - $ref: '#/components/parameters/PageParam'
      - $ref: '#/components/parameters/PerPageParam'
    responses:
      200:
        description: 코스 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "crs_abc123"
                  code: "CS101"
                  name: "컴퓨터 프로그래밍 기초"
                  description: "Python을 이용한 프로그래밍 입문"
                  semester: "2025-1"
                  status: "active"
                  instructor:
                    id: "usr_ins001"
                    name: "김교수"
                    profileImageUrl: "https://cdn.eduforum.com/profiles/ins001.jpg"
                  enrolledCount: 45
                  maxStudents: 50
                  thumbnailUrl: "https://cdn.eduforum.com/courses/cs101.jpg"
                  startDate: "2025-03-02"
                  endDate: "2025-06-20"
                  createdAt: "2025-01-15T10:00:00Z"
              meta:
                pagination:
                  total: 12
                  page: 1
                  perPage: 20

  post:
    tags: [Courses]
    summary: 코스 생성
    operationId: createCourse
    security:
      - BearerAuth: []
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/CourseCreateRequest'
          example:
            code: "CS101"
            name: "컴퓨터 프로그래밍 기초"
            description: "Python을 이용한 프로그래밍 입문 과정입니다."
            semester: "2025-1"
            maxStudents: 50
            startDate: "2025-03-02"
            endDate: "2025-06-20"
            settings:
              allowLateSubmission: true
              lateSubmissionPenalty: 10
              autoRecording: true
    responses:
      201:
        description: 코스 생성 성공
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CourseResponse'
      403:
        description: 교수/관리자 권한 필요
      409:
        description: 코스 코드 중복
```

### 5.2 코스 상세 조회/수정/삭제

```yaml
/courses/{courseId}:
  get:
    tags: [Courses]
    summary: 코스 상세 조회
    operationId: getCourse
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 코스 상세 정보
        content:
          application/json:
            example:
              success: true
              data:
                id: "crs_abc123"
                code: "CS101"
                name: "컴퓨터 프로그래밍 기초"
                description: "Python을 이용한 프로그래밍 입문"
                semester: "2025-1"
                status: "active"
                instructor:
                  id: "usr_ins001"
                  name: "김교수"
                  email: "prof.kim@university.edu"
                  profileImageUrl: "https://cdn.eduforum.com/profiles/ins001.jpg"
                teachingAssistants:
                  - id: "usr_ta001"
                    name: "이조교"
                    email: "ta.lee@university.edu"
                enrolledCount: 45
                maxStudents: 50
                thumbnailUrl: "https://cdn.eduforum.com/courses/cs101.jpg"
                startDate: "2025-03-02"
                endDate: "2025-06-20"
                settings:
                  allowLateSubmission: true
                  lateSubmissionPenalty: 10
                  autoRecording: true
                  engagementTracking: true
                syllabus:
                  - week: 1
                    topic: "Python 소개 및 환경 설정"
                  - week: 2
                    topic: "변수와 자료형"
                stats:
                  totalSessions: 30
                  completedSessions: 5
                  upcomingSessions: 25
                  averageAttendance: 92.5
                createdAt: "2025-01-15T10:00:00Z"
                updatedAt: "2025-01-20T14:30:00Z"
      404:
        description: 코스를 찾을 수 없음

  patch:
    tags: [Courses]
    summary: 코스 정보 수정
    operationId: updateCourse
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/CourseUpdateRequest'
    responses:
      200:
        description: 수정 완료
      403:
        description: 권한 없음
      404:
        description: 코스를 찾을 수 없음

  delete:
    tags: [Courses]
    summary: 코스 삭제 (보관)
    operationId: deleteCourse
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료
      403:
        description: 권한 없음
```

### 5.3 코스 썸네일 업로드

```yaml
/courses/{courseId}/thumbnail:
  post:
    tags: [Courses]
    summary: 코스 썸네일 이미지 업로드
    operationId: uploadCourseThumbnail
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        multipart/form-data:
          schema:
            type: object
            required: [file]
            properties:
              file:
                type: string
                format: binary
                description: JPEG/PNG, 권장 16:9, 최대 5MB
    responses:
      200:
        description: 업로드 성공
        content:
          application/json:
            example:
              success: true
              data:
                thumbnailUrl: "https://cdn.eduforum.com/courses/cs101_v2.jpg"
```

### 5.4 수강생 관리 (Enrollments)

```yaml
/courses/{courseId}/enrollments:
  get:
    tags: [Courses]
    summary: 수강생 목록 조회
    operationId: listEnrollments
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: status
        in: query
        schema:
          type: string
          enum: [enrolled, dropped, completed]
      - name: q
        in: query
        schema:
          type: string
        description: 이름/학번 검색
      - $ref: '#/components/parameters/PageParam'
      - $ref: '#/components/parameters/PerPageParam'
    responses:
      200:
        description: 수강생 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "enr_001"
                  student:
                    id: "usr_stu001"
                    name: "홍길동"
                    email: "hong@university.edu"
                    studentId: "2021001234"
                    profileImageUrl: "https://cdn.eduforum.com/profiles/stu001.jpg"
                  status: "enrolled"
                  enrolledAt: "2025-01-20T09:00:00Z"
                  stats:
                    attendanceRate: 95.0
                    averageGrade: 88.5
                    participationScore: 72

  post:
    tags: [Courses]
    summary: 수강생 등록 (단일/대량)
    operationId: enrollStudents
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            properties:
              studentIds:
                type: array
                items:
                  type: string
                description: 등록할 학생 ID 목록
              emails:
                type: array
                items:
                  type: string
                  format: email
                description: 이메일로 초대 (미가입자 포함)
          example:
            studentIds: ["usr_stu001", "usr_stu002"]
            emails: ["new.student@university.edu"]
    responses:
      201:
        description: 등록 완료
        content:
          application/json:
            example:
              success: true
              data:
                enrolled: 2
                invited: 1
                failed: []
      400:
        description: 수강 인원 초과

/courses/{courseId}/enrollments/import:
  post:
    tags: [Courses]
    summary: 수강생 일괄 등록 (CSV)
    operationId: importEnrollments
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        multipart/form-data:
          schema:
            type: object
            required: [file]
            properties:
              file:
                type: string
                format: binary
                description: CSV 파일 (학번, 이름, 이메일)
    responses:
      200:
        description: 등록 결과
        content:
          application/json:
            example:
              success: true
              data:
                total: 50
                enrolled: 48
                failed: 2
                errors:
                  - row: 12
                    email: "invalid-email"
                    reason: "유효하지 않은 이메일 형식"

/courses/{courseId}/enrollments/{enrollmentId}:
  delete:
    tags: [Courses]
    summary: 수강 취소
    operationId: cancelEnrollment
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: enrollmentId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 수강 취소 완료
```

### 5.5 TA 관리

```yaml
/courses/{courseId}/tas:
  get:
    tags: [Courses]
    summary: TA 목록 조회
    operationId: listTAs
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: TA 목록

  post:
    tags: [Courses]
    summary: TA 추가
    operationId: addTA
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [userId]
            properties:
              userId:
                type: string
              permissions:
                type: array
                items:
                  type: string
                  enum: [grade, moderate, content, analytics]
    responses:
      201:
        description: TA 추가 완료

/courses/{courseId}/tas/{taId}:
  delete:
    tags: [Courses]
    summary: TA 제거
    operationId: removeTA
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: taId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: TA 제거 완료
```

---

## 6. 세션 API (Sessions)

### 6.1 세션 목록 조회/생성

```yaml
/courses/{courseId}/sessions:
  get:
    tags: [Sessions]
    summary: 세션 목록 조회
    operationId: listSessions
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: status
        in: query
        schema:
          type: string
          enum: [scheduled, live, completed, cancelled]
      - name: from
        in: query
        schema:
          type: string
          format: date
      - name: to
        in: query
        schema:
          type: string
          format: date
      - $ref: '#/components/parameters/PageParam'
    responses:
      200:
        description: 세션 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "ses_001"
                  title: "Week 1: Python 소개"
                  description: "Python 언어 개요 및 개발 환경 설정"
                  sessionNumber: 1
                  scheduledAt: "2025-03-03T09:00:00Z"
                  duration: 90
                  status: "scheduled"
                  type: "lecture"
                  isRecorded: true
                  meetingUrl: null
                  attendees: 0
                  maxAttendees: 50

  post:
    tags: [Sessions]
    summary: 세션 생성
    operationId: createSession
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/SessionCreateRequest'
          example:
            title: "Week 1: Python 소개"
            description: "Python 언어 개요 및 개발 환경 설정"
            scheduledAt: "2025-03-03T09:00:00Z"
            duration: 90
            type: "lecture"
            settings:
              autoRecording: true
              waitingRoom: true
              allowChat: true
              allowScreenShare: false
    responses:
      201:
        description: 세션 생성 완료
      403:
        description: 권한 없음
```

### 6.2 세션 상세/수정/삭제

```yaml
/courses/{courseId}/sessions/{sessionId}:
  get:
    tags: [Sessions]
    summary: 세션 상세 조회
    operationId: getSession
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 세션 상세 정보
        content:
          application/json:
            example:
              success: true
              data:
                id: "ses_001"
                course:
                  id: "crs_abc123"
                  code: "CS101"
                  name: "컴퓨터 프로그래밍 기초"
                title: "Week 1: Python 소개"
                description: "Python 언어 개요 및 개발 환경 설정"
                sessionNumber: 1
                scheduledAt: "2025-03-03T09:00:00Z"
                startedAt: null
                endedAt: null
                duration: 90
                status: "scheduled"
                type: "lecture"
                host:
                  id: "usr_ins001"
                  name: "김교수"
                settings:
                  autoRecording: true
                  waitingRoom: true
                  allowChat: true
                  allowScreenShare: false
                  allowRaiseHand: true
                materials:
                  - id: "mat_001"
                    name: "Week1_Slides.pdf"
                    url: "https://cdn.eduforum.com/materials/week1.pdf"
                    type: "pdf"
                recordings: []
                polls: []
                quizzes: []

  patch:
    tags: [Sessions]
    summary: 세션 정보 수정
    operationId: updateSession
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/SessionUpdateRequest'
    responses:
      200:
        description: 수정 완료

  delete:
    tags: [Sessions]
    summary: 세션 삭제/취소
    operationId: deleteSession
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료
```

### 6.3 세션 자료 관리

```yaml
/courses/{courseId}/sessions/{sessionId}/materials:
  get:
    tags: [Sessions]
    summary: 세션 자료 목록
    operationId: listSessionMaterials
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 자료 목록

  post:
    tags: [Sessions]
    summary: 세션 자료 업로드
    operationId: uploadSessionMaterial
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        multipart/form-data:
          schema:
            type: object
            required: [file]
            properties:
              file:
                type: string
                format: binary
              name:
                type: string
              description:
                type: string
    responses:
      201:
        description: 업로드 완료

/courses/{courseId}/sessions/{sessionId}/materials/{materialId}:
  delete:
    tags: [Sessions]
    summary: 세션 자료 삭제
    operationId: deleteSessionMaterial
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: materialId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료
```

---

## 콘텐츠 라이브러리 API

### 콘텐츠 관리

```yaml
/courses/{courseId}/contents:
  get:
    tags: [Courses]
    summary: 콘텐츠 라이브러리 목록
    operationId: listContents
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: type
        in: query
        schema:
          type: string
          enum: [document, video, image, link, other]
      - name: q
        in: query
        schema:
          type: string
    responses:
      200:
        description: 콘텐츠 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "cnt_001"
                  name: "Python 기초 강의 노트"
                  description: "1-3주차 강의 노트 PDF"
                  type: "document"
                  mimeType: "application/pdf"
                  size: 2457600
                  url: "https://cdn.eduforum.com/contents/python_notes.pdf"
                  uploadedBy:
                    id: "usr_ins001"
                    name: "김교수"
                  createdAt: "2025-01-15T10:00:00Z"

  post:
    tags: [Courses]
    summary: 콘텐츠 업로드
    operationId: uploadContent
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        multipart/form-data:
          schema:
            type: object
            required: [file]
            properties:
              file:
                type: string
                format: binary
              name:
                type: string
              description:
                type: string
              folderId:
                type: string
    responses:
      201:
        description: 업로드 완료

/courses/{courseId}/contents/{contentId}:
  get:
    tags: [Courses]
    summary: 콘텐츠 상세/다운로드
    operationId: getContent
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: contentId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 콘텐츠 정보 또는 다운로드

  delete:
    tags: [Courses]
    summary: 콘텐츠 삭제
    operationId: deleteContent
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: contentId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료
```

---

## 과제 API (Assignments)

### 과제 관리

```yaml
/courses/{courseId}/assignments:
  get:
    tags: [Assignments]
    summary: 과제 목록 조회
    operationId: listAssignments
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: status
        in: query
        schema:
          type: string
          enum: [draft, published, closed]
    responses:
      200:
        description: 과제 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "asg_001"
                  title: "과제 1: Python 기초 실습"
                  description: "변수와 자료형 실습 문제"
                  type: "file_upload"
                  status: "published"
                  dueDate: "2025-03-10T23:59:59Z"
                  maxScore: 100
                  allowLateSubmission: true
                  lateDeadline: "2025-03-12T23:59:59Z"
                  latePenalty: 10
                  submissions:
                    total: 45
                    submitted: 38
                    graded: 25
                  createdAt: "2025-03-01T10:00:00Z"

  post:
    tags: [Assignments]
    summary: 과제 생성
    operationId: createAssignment
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/AssignmentCreateRequest'
          example:
            title: "과제 1: Python 기초 실습"
            description: "변수와 자료형 실습 문제를 풀어 제출하세요."
            instructions: "# 과제 안내\n\n1. 문제 1~5를 해결하세요.\n2. 소스코드(.py)로 제출하세요."
            type: "file_upload"
            dueDate: "2025-03-10T23:59:59Z"
            maxScore: 100
            settings:
              allowLateSubmission: true
              lateDeadline: "2025-03-12T23:59:59Z"
              latePenalty: 10
              allowedFileTypes: [".py", ".ipynb"]
              maxFileSize: 10485760
              maxFiles: 5
    responses:
      201:
        description: 과제 생성 완료

/courses/{courseId}/assignments/{assignmentId}:
  get:
    tags: [Assignments]
    summary: 과제 상세 조회
    operationId: getAssignment
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 과제 상세 정보

  patch:
    tags: [Assignments]
    summary: 과제 수정
    operationId: updateAssignment
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/AssignmentUpdateRequest'
    responses:
      200:
        description: 수정 완료

  delete:
    tags: [Assignments]
    summary: 과제 삭제
    operationId: deleteAssignment
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료
```

### 과제 제출

```yaml
/courses/{courseId}/assignments/{assignmentId}/submissions:
  get:
    tags: [Assignments]
    summary: 제출물 목록 (교수용)
    operationId: listSubmissions
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
      - name: status
        in: query
        schema:
          type: string
          enum: [pending, graded, late]
      - name: q
        in: query
        schema:
          type: string
        description: 학생 이름/학번 검색
    responses:
      200:
        description: 제출물 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "sub_001"
                  student:
                    id: "usr_stu001"
                    name: "홍길동"
                    studentId: "2021001234"
                  status: "graded"
                  submittedAt: "2025-03-09T15:30:00Z"
                  isLate: false
                  files:
                    - name: "solution.py"
                      size: 2048
                      url: "https://cdn.eduforum.com/submissions/sub_001/solution.py"
                  grade:
                    score: 95
                    feedback: "잘 작성했습니다. 변수명을 더 명확하게 하면 좋겠습니다."
                    gradedBy: "usr_ins001"
                    gradedAt: "2025-03-11T10:00:00Z"

  post:
    tags: [Assignments]
    summary: 과제 제출 (학생용)
    operationId: submitAssignment
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        multipart/form-data:
          schema:
            type: object
            required: [files]
            properties:
              files:
                type: array
                items:
                  type: string
                  format: binary
              comment:
                type: string
    responses:
      201:
        description: 제출 완료
      400:
        description: 마감 지남 또는 파일 형식 오류

/courses/{courseId}/assignments/{assignmentId}/submissions/me:
  get:
    tags: [Assignments]
    summary: 내 제출물 조회 (학생용)
    operationId: getMySubmission
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 내 제출물 정보
      404:
        description: 제출 내역 없음

/courses/{courseId}/assignments/{assignmentId}/submissions/{submissionId}:
  get:
    tags: [Assignments]
    summary: 제출물 상세 조회
    operationId: getSubmission
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
      - name: submissionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 제출물 상세

/courses/{courseId}/assignments/{assignmentId}/submissions/{submissionId}/grade:
  post:
    tags: [Assignments]
    summary: 제출물 채점
    operationId: gradeSubmission
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
      - name: submissionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [score]
            properties:
              score:
                type: number
                minimum: 0
              feedback:
                type: string
              rubricScores:
                type: object
                additionalProperties:
                  type: number
    responses:
      200:
        description: 채점 완료
```

---

## Course 관련 스키마

```yaml
components:
  schemas:
    CourseCreateRequest:
      type: object
      required: [code, name, semester]
      properties:
        code:
          type: string
          maxLength: 20
          example: "CS101"
        name:
          type: string
          maxLength: 200
          example: "컴퓨터 프로그래밍 기초"
        description:
          type: string
          maxLength: 2000
        semester:
          type: string
          example: "2025-1"
        maxStudents:
          type: integer
          minimum: 1
          maximum: 500
          default: 50
        startDate:
          type: string
          format: date
        endDate:
          type: string
          format: date
        settings:
          type: object
          properties:
            allowLateSubmission:
              type: boolean
              default: true
            lateSubmissionPenalty:
              type: integer
              minimum: 0
              maximum: 100
            autoRecording:
              type: boolean
              default: true
            engagementTracking:
              type: boolean
              default: true

    CourseUpdateRequest:
      type: object
      properties:
        name:
          type: string
        description:
          type: string
        status:
          type: string
          enum: [draft, active, archived]
        maxStudents:
          type: integer
        settings:
          type: object

    CourseResponse:
      type: object
      properties:
        id:
          type: string
        code:
          type: string
        name:
          type: string
        description:
          type: string
        semester:
          type: string
        status:
          type: string
          enum: [draft, active, archived]
        instructor:
          $ref: '#/components/schemas/UserSummary'
        enrolledCount:
          type: integer
        maxStudents:
          type: integer
        thumbnailUrl:
          type: string
          format: uri
        startDate:
          type: string
          format: date
        endDate:
          type: string
          format: date
        createdAt:
          type: string
          format: date-time
        updatedAt:
          type: string
          format: date-time

    SessionCreateRequest:
      type: object
      required: [title, scheduledAt, duration]
      properties:
        title:
          type: string
          maxLength: 200
        description:
          type: string
        scheduledAt:
          type: string
          format: date-time
        duration:
          type: integer
          minimum: 15
          maximum: 480
          description: 분 단위
        type:
          type: string
          enum: [lecture, seminar, lab, office_hours]
          default: lecture
        settings:
          type: object
          properties:
            autoRecording:
              type: boolean
            waitingRoom:
              type: boolean
            allowChat:
              type: boolean
            allowScreenShare:
              type: boolean
            allowRaiseHand:
              type: boolean

    SessionUpdateRequest:
      type: object
      properties:
        title:
          type: string
        description:
          type: string
        scheduledAt:
          type: string
          format: date-time
        duration:
          type: integer
        settings:
          type: object

    AssignmentCreateRequest:
      type: object
      required: [title, type, dueDate, maxScore]
      properties:
        title:
          type: string
        description:
          type: string
        instructions:
          type: string
          description: Markdown 형식
        type:
          type: string
          enum: [file_upload, code, text, quiz]
        dueDate:
          type: string
          format: date-time
        maxScore:
          type: integer
          minimum: 1
        settings:
          type: object
          properties:
            allowLateSubmission:
              type: boolean
            lateDeadline:
              type: string
              format: date-time
            latePenalty:
              type: integer
            allowedFileTypes:
              type: array
              items:
                type: string
            maxFileSize:
              type: integer
            maxFiles:
              type: integer

    AssignmentUpdateRequest:
      type: object
      properties:
        title:
          type: string
        description:
          type: string
        instructions:
          type: string
        status:
          type: string
          enum: [draft, published, closed]
        dueDate:
          type: string
          format: date-time
        settings:
          type: object

    UserSummary:
      type: object
      properties:
        id:
          type: string
        name:
          type: string
        email:
          type: string
        profileImageUrl:
          type: string
```

---

> **다음 파트**: Part 3에서 실시간 세션 API (Live Session, WebRTC, Recordings)를 작성합니다.

---

## 7. 실시간 세션 API (Live)

### 7.1 세션 시작/종료

```yaml
/sessions/{sessionId}/live/start:
  post:
    tags: [Live]
    summary: 라이브 세션 시작
    operationId: startLiveSession
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 세션 시작됨
        content:
          application/json:
            example:
              success: true
              data:
                sessionId: "ses_001"
                status: "live"
                startedAt: "2025-03-03T09:00:00Z"
                meetingUrl: "https://live.eduforum.com/ses_001"
                hostToken: "host_token_xyz"
                webrtc:
                  signalingUrl: "wss://signaling.eduforum.com"
                  iceServers:
                    - urls: "stun:stun.eduforum.com:3478"
                    - urls: "turn:turn.eduforum.com:3478"
                      username: "user"
                      credential: "pass"
      403:
        description: 호스트 권한 필요
      409:
        description: 이미 진행 중

/sessions/{sessionId}/live/end:
  post:
    tags: [Live]
    summary: 라이브 세션 종료
    operationId: endLiveSession
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              saveRecording:
                type: boolean
                default: true
              notifyParticipants:
                type: boolean
                default: true
    responses:
      200:
        description: 세션 종료됨
        content:
          application/json:
            example:
              success: true
              data:
                sessionId: "ses_001"
                status: "completed"
                endedAt: "2025-03-03T10:30:00Z"
                duration: 90
                stats:
                  totalParticipants: 45
                  peakParticipants: 42
                  averageDuration: 85
                recording:
                  status: "processing"
                  estimatedTime: 300
```

### 7.2 참가자 관리

```yaml
/sessions/{sessionId}/live/join:
  post:
    tags: [Live]
    summary: 세션 참가
    operationId: joinLiveSession
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              deviceInfo:
                type: object
                properties:
                  browser:
                    type: string
                  os:
                    type: string
                  hasCamera:
                    type: boolean
                  hasMicrophone:
                    type: boolean
    responses:
      200:
        description: 참가 성공
        content:
          application/json:
            example:
              success: true
              data:
                participantId: "prt_abc123"
                participantToken: "participant_token_xyz"
                sessionInfo:
                  title: "Week 1: Python 소개"
                  host:
                    id: "usr_ins001"
                    name: "김교수"
                  participantCount: 42
                  startedAt: "2025-03-03T09:00:00Z"
                webrtc:
                  signalingUrl: "wss://signaling.eduforum.com"
                  iceServers:
                    - urls: "stun:stun.eduforum.com:3478"
                settings:
                  canShareScreen: false
                  canChat: true
                  canRaiseHand: true
      403:
        description: 수강생만 참가 가능
      409:
        description: 대기실 대기 필요

/sessions/{sessionId}/live/leave:
  post:
    tags: [Live]
    summary: 세션 퇴장
    operationId: leaveLiveSession
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 퇴장 완료

/sessions/{sessionId}/live/participants:
  get:
    tags: [Live]
    summary: 참가자 목록 조회
    operationId: listParticipants
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 참가자 목록
        content:
          application/json:
            example:
              success: true
              data:
                host:
                  id: "usr_ins001"
                  name: "김교수"
                  isOnline: true
                  isSharingScreen: true
                participants:
                  - id: "prt_001"
                    user:
                      id: "usr_stu001"
                      name: "홍길동"
                      profileImageUrl: "https://cdn.eduforum.com/profiles/stu001.jpg"
                    status: "active"
                    joinedAt: "2025-03-03T09:02:00Z"
                    hasVideo: true
                    hasAudio: false
                    handRaised: false
                    engagement:
                      score: 85
                      status: "engaged"
                stats:
                  total: 42
                  active: 40
                  inactive: 2
```

### 7.3 대기실 관리

```yaml
/sessions/{sessionId}/live/waiting-room:
  get:
    tags: [Live]
    summary: 대기실 목록 조회
    operationId: listWaitingRoom
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 대기 중인 참가자 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "wait_001"
                  user:
                    id: "usr_stu010"
                    name: "박민수"
                    email: "park@university.edu"
                  requestedAt: "2025-03-03T09:15:00Z"
                  deviceInfo:
                    browser: "Chrome 120"
                    os: "Windows 11"

/sessions/{sessionId}/live/waiting-room/admit:
  post:
    tags: [Live]
    summary: 대기실 참가자 입장 허용
    operationId: admitFromWaitingRoom
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            properties:
              participantIds:
                type: array
                items:
                  type: string
              admitAll:
                type: boolean
                default: false
    responses:
      200:
        description: 입장 허용됨

/sessions/{sessionId}/live/waiting-room/reject:
  post:
    tags: [Live]
    summary: 대기실 참가자 거부
    operationId: rejectFromWaitingRoom
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [participantIds]
            properties:
              participantIds:
                type: array
                items:
                  type: string
              reason:
                type: string
    responses:
      200:
        description: 거부됨
```

### 7.4 미디어 제어

```yaml
/sessions/{sessionId}/live/participants/{participantId}/media:
  patch:
    tags: [Live]
    summary: 참가자 미디어 제어 (호스트)
    operationId: controlParticipantMedia
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: participantId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            properties:
              muteAudio:
                type: boolean
              muteVideo:
                type: boolean
              allowScreenShare:
                type: boolean
    responses:
      200:
        description: 제어 완료

/sessions/{sessionId}/live/participants/{participantId}/kick:
  post:
    tags: [Live]
    summary: 참가자 강제 퇴장
    operationId: kickParticipant
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: participantId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              reason:
                type: string
              allowRejoin:
                type: boolean
                default: false
    responses:
      200:
        description: 퇴장 처리됨

/sessions/{sessionId}/live/mute-all:
  post:
    tags: [Live]
    summary: 전체 음소거
    operationId: muteAllParticipants
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              allowUnmute:
                type: boolean
                default: true
                description: 참가자가 스스로 음소거 해제 가능 여부
    responses:
      200:
        description: 전체 음소거 완료
```

### 7.5 손들기/리액션

```yaml
/sessions/{sessionId}/live/raise-hand:
  post:
    tags: [Live]
    summary: 손들기
    operationId: raiseHand
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 손들기 완료

/sessions/{sessionId}/live/lower-hand:
  post:
    tags: [Live]
    summary: 손 내리기
    operationId: lowerHand
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              participantId:
                type: string
                description: 호스트가 특정 참가자 손 내리기
    responses:
      200:
        description: 손 내리기 완료

/sessions/{sessionId}/live/reactions:
  post:
    tags: [Live]
    summary: 리액션 보내기
    operationId: sendReaction
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [type]
            properties:
              type:
                type: string
                enum: [thumbs_up, clap, heart, laugh, surprised, thinking]
    responses:
      201:
        description: 리액션 전송됨
```

### 7.6 채팅

```yaml
/sessions/{sessionId}/live/chat:
  get:
    tags: [Live]
    summary: 채팅 메시지 조회
    operationId: getChatMessages
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: since
        in: query
        schema:
          type: string
          format: date-time
        description: 이후 메시지만 조회
      - name: limit
        in: query
        schema:
          type: integer
          default: 50
    responses:
      200:
        description: 채팅 메시지 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "msg_001"
                  sender:
                    id: "usr_stu001"
                    name: "홍길동"
                    role: "student"
                  content: "교수님, 질문 있습니다!"
                  type: "text"
                  createdAt: "2025-03-03T09:15:00Z"
                  isPrivate: false
                - id: "msg_002"
                  sender:
                    id: "usr_ins001"
                    name: "김교수"
                    role: "instructor"
                  content: "네, 말씀해주세요."
                  type: "text"
                  createdAt: "2025-03-03T09:15:30Z"
                  isPrivate: false

  post:
    tags: [Live]
    summary: 채팅 메시지 전송
    operationId: sendChatMessage
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [content]
            properties:
              content:
                type: string
                maxLength: 1000
              type:
                type: string
                enum: [text, file, link]
                default: text
              recipientId:
                type: string
                description: 비공개 메시지 수신자 (지정 시 DM)
              replyToId:
                type: string
                description: 답장 대상 메시지 ID
    responses:
      201:
        description: 메시지 전송됨

/sessions/{sessionId}/live/chat/{messageId}:
  delete:
    tags: [Live]
    summary: 채팅 메시지 삭제 (호스트)
    operationId: deleteChatMessage
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: messageId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료
```

### 7.7 화면 공유

```yaml
/sessions/{sessionId}/live/screen-share/start:
  post:
    tags: [Live]
    summary: 화면 공유 시작
    operationId: startScreenShare
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              shareAudio:
                type: boolean
                default: true
    responses:
      200:
        description: 화면 공유 시작됨
      403:
        description: 화면 공유 권한 없음
      409:
        description: 다른 사용자가 공유 중

/sessions/{sessionId}/live/screen-share/stop:
  post:
    tags: [Live]
    summary: 화면 공유 종료
    operationId: stopScreenShare
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 화면 공유 종료됨
```

### 7.8 녹화 관리

```yaml
/sessions/{sessionId}/recordings:
  get:
    tags: [Live]
    summary: 녹화 목록 조회
    operationId: listRecordings
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 녹화 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "rec_001"
                  title: "Week 1: Python 소개 - 녹화"
                  duration: 5400
                  size: 524288000
                  status: "ready"
                  url: "https://cdn.eduforum.com/recordings/rec_001.mp4"
                  thumbnailUrl: "https://cdn.eduforum.com/recordings/rec_001_thumb.jpg"
                  createdAt: "2025-03-03T10:35:00Z"
                  views: 25

/sessions/{sessionId}/recordings/{recordingId}:
  get:
    tags: [Live]
    summary: 녹화 상세/재생
    operationId: getRecording
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: recordingId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 녹화 정보
        content:
          application/json:
            example:
              success: true
              data:
                id: "rec_001"
                title: "Week 1: Python 소개 - 녹화"
                duration: 5400
                status: "ready"
                playbackUrl: "https://stream.eduforum.com/rec_001/playlist.m3u8"
                downloadUrl: "https://cdn.eduforum.com/recordings/rec_001.mp4"
                chapters:
                  - title: "도입"
                    startTime: 0
                  - title: "Python 설치"
                    startTime: 600
                  - title: "첫 번째 프로그램"
                    startTime: 1800
                transcription:
                  status: "ready"
                  url: "https://cdn.eduforum.com/recordings/rec_001.vtt"

  patch:
    tags: [Live]
    summary: 녹화 정보 수정
    operationId: updateRecording
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: recordingId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            properties:
              title:
                type: string
              isPublic:
                type: boolean
              chapters:
                type: array
                items:
                  type: object
                  properties:
                    title:
                      type: string
                    startTime:
                      type: integer
    responses:
      200:
        description: 수정 완료

  delete:
    tags: [Live]
    summary: 녹화 삭제
    operationId: deleteRecording
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: recordingId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료
```

---

> **다음 파트**: Part 4에서 액티브 러닝 API (Polls, Quizzes, Breakouts, Whiteboard)를 작성합니다.

---

## 8. 액티브 러닝 API (Active Learning)

### 8.1 투표/설문 (Polls)

```yaml
/sessions/{sessionId}/polls:
  get:
    tags: [Polls]
    summary: 투표 목록 조회
    operationId: listPolls
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: status
        in: query
        schema:
          type: string
          enum: [draft, active, closed]
    responses:
      200:
        description: 투표 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "poll_001"
                  question: "오늘 강의 내용 이해도는?"
                  type: "single_choice"
                  status: "closed"
                  options:
                    - id: "opt_1"
                      text: "완전히 이해함"
                      votes: 15
                      percentage: 35.7
                    - id: "opt_2"
                      text: "대체로 이해함"
                      votes: 20
                      percentage: 47.6
                    - id: "opt_3"
                      text: "이해하지 못함"
                      votes: 7
                      percentage: 16.7
                  totalVotes: 42
                  createdAt: "2025-03-03T09:30:00Z"

  post:
    tags: [Polls]
    summary: 투표 생성
    operationId: createPoll
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/PollCreateRequest'
          example:
            question: "오늘 강의 내용 이해도는?"
            type: "single_choice"
            options:
              - text: "완전히 이해함"
              - text: "대체로 이해함"
              - text: "이해하지 못함"
            settings:
              anonymous: true
              showResultsRealtime: true
              allowChangeVote: false
              duration: 60
    responses:
      201:
        description: 투표 생성됨

/sessions/{sessionId}/polls/{pollId}:
  get:
    tags: [Polls]
    summary: 투표 상세 조회
    operationId: getPoll
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: pollId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 투표 상세

  delete:
    tags: [Polls]
    summary: 투표 삭제
    operationId: deletePoll
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: pollId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료

/sessions/{sessionId}/polls/{pollId}/start:
  post:
    tags: [Polls]
    summary: 투표 시작
    operationId: startPoll
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: pollId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 투표 시작됨

/sessions/{sessionId}/polls/{pollId}/close:
  post:
    tags: [Polls]
    summary: 투표 종료
    operationId: closePoll
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: pollId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 투표 종료됨

/sessions/{sessionId}/polls/{pollId}/vote:
  post:
    tags: [Polls]
    summary: 투표하기
    operationId: submitVote
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: pollId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [optionIds]
            properties:
              optionIds:
                type: array
                items:
                  type: string
                description: 선택한 옵션 ID (복수 선택 가능)
              textResponse:
                type: string
                description: 주관식 응답 (워드클라우드용)
    responses:
      201:
        description: 투표 완료
      400:
        description: 투표 불가 (마감/이미 투표)

/sessions/{sessionId}/polls/{pollId}/results:
  get:
    tags: [Polls]
    summary: 투표 결과 조회
    operationId: getPollResults
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: pollId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 투표 결과
        content:
          application/json:
            example:
              success: true
              data:
                pollId: "poll_001"
                question: "오늘 강의 내용 이해도는?"
                totalParticipants: 42
                totalVotes: 42
                participationRate: 100
                options:
                  - id: "opt_1"
                    text: "완전히 이해함"
                    votes: 15
                    percentage: 35.7
                  - id: "opt_2"
                    text: "대체로 이해함"
                    votes: 20
                    percentage: 47.6
                wordCloud:
                  - word: "좋음"
                    count: 10
                  - word: "이해"
                    count: 8
```

### 8.2 퀴즈 (Quizzes)

```yaml
/sessions/{sessionId}/quizzes:
  get:
    tags: [Quizzes]
    summary: 퀴즈 목록 조회
    operationId: listQuizzes
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 퀴즈 목록

  post:
    tags: [Quizzes]
    summary: 퀴즈 생성
    operationId: createQuiz
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/QuizCreateRequest'
          example:
            title: "Week 1 이해도 퀴즈"
            description: "Python 기초 개념 확인"
            settings:
              timeLimit: 300
              shuffleQuestions: true
              shuffleOptions: true
              showCorrectAnswers: "after_close"
              allowRetake: false
              passingScore: 70
            questions:
              - type: "single_choice"
                question: "Python에서 변수를 선언할 때 필요한 키워드는?"
                options:
                  - text: "var"
                    isCorrect: false
                  - text: "let"
                    isCorrect: false
                  - text: "없음 (키워드 불필요)"
                    isCorrect: true
                  - text: "const"
                    isCorrect: false
                points: 10
                explanation: "Python은 동적 타이핑 언어로 변수 선언에 키워드가 필요 없습니다."
    responses:
      201:
        description: 퀴즈 생성됨

/sessions/{sessionId}/quizzes/{quizId}:
  get:
    tags: [Quizzes]
    summary: 퀴즈 상세 조회
    operationId: getQuiz
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: quizId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 퀴즈 상세 (학생: 정답 제외, 교수: 전체)

  patch:
    tags: [Quizzes]
    summary: 퀴즈 수정
    operationId: updateQuiz
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: quizId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/QuizUpdateRequest'
    responses:
      200:
        description: 수정 완료

  delete:
    tags: [Quizzes]
    summary: 퀴즈 삭제
    operationId: deleteQuiz
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: quizId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료

/sessions/{sessionId}/quizzes/{quizId}/start:
  post:
    tags: [Quizzes]
    summary: 퀴즈 시작
    operationId: startQuiz
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: quizId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 퀴즈 시작됨

/sessions/{sessionId}/quizzes/{quizId}/close:
  post:
    tags: [Quizzes]
    summary: 퀴즈 종료
    operationId: closeQuiz
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: quizId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 퀴즈 종료됨

/sessions/{sessionId}/quizzes/{quizId}/attempt:
  post:
    tags: [Quizzes]
    summary: 퀴즈 시작 (학생)
    operationId: startQuizAttempt
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: quizId
        in: path
        required: true
        schema:
          type: string
    responses:
      201:
        description: 퀴즈 시작됨
        content:
          application/json:
            example:
              success: true
              data:
                attemptId: "att_001"
                quiz:
                  title: "Week 1 이해도 퀴즈"
                  timeLimit: 300
                  questionsCount: 5
                startedAt: "2025-03-03T09:45:00Z"
                expiresAt: "2025-03-03T09:50:00Z"
                questions:
                  - id: "q_001"
                    type: "single_choice"
                    question: "Python에서 변수를 선언할 때 필요한 키워드는?"
                    options:
                      - id: "opt_1"
                        text: "var"
                      - id: "opt_2"
                        text: "let"
                      - id: "opt_3"
                        text: "없음 (키워드 불필요)"
                      - id: "opt_4"
                        text: "const"

/sessions/{sessionId}/quizzes/{quizId}/attempt/{attemptId}/submit:
  post:
    tags: [Quizzes]
    summary: 퀴즈 답안 제출
    operationId: submitQuizAttempt
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: quizId
        in: path
        required: true
        schema:
          type: string
      - name: attemptId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [answers]
            properties:
              answers:
                type: array
                items:
                  type: object
                  properties:
                    questionId:
                      type: string
                    selectedOptionIds:
                      type: array
                      items:
                        type: string
                    textAnswer:
                      type: string
    responses:
      200:
        description: 제출 완료
        content:
          application/json:
            example:
              success: true
              data:
                attemptId: "att_001"
                score: 80
                totalPoints: 100
                correctCount: 4
                totalQuestions: 5
                passed: true
                submittedAt: "2025-03-03T09:48:30Z"

/sessions/{sessionId}/quizzes/{quizId}/results:
  get:
    tags: [Quizzes]
    summary: 퀴즈 결과 통계 (교수용)
    operationId: getQuizResults
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: quizId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 퀴즈 결과 통계
        content:
          application/json:
            example:
              success: true
              data:
                quizId: "quiz_001"
                title: "Week 1 이해도 퀴즈"
                stats:
                  totalParticipants: 42
                  submittedCount: 40
                  averageScore: 78.5
                  highestScore: 100
                  lowestScore: 40
                  passRate: 85.0
                  averageTime: 240
                questionStats:
                  - questionId: "q_001"
                    correctRate: 90.0
                    averageTime: 30
                  - questionId: "q_002"
                    correctRate: 65.0
                    averageTime: 45
                scoreDistribution:
                  - range: "90-100"
                    count: 10
                  - range: "80-89"
                    count: 15
                  - range: "70-79"
                    count: 8
```

### 8.3 분반 토론 (Breakouts)

```yaml
/sessions/{sessionId}/breakouts:
  get:
    tags: [Breakouts]
    summary: 분반 목록 조회
    operationId: listBreakoutRooms
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 분반 목록
        content:
          application/json:
            example:
              success: true
              data:
                status: "active"
                duration: 600
                remainingTime: 420
                topic: "마케팅 전략 사례 분석"
                rooms:
                  - id: "brk_001"
                    name: "분반 1"
                    participantCount: 5
                    participants:
                      - id: "usr_stu001"
                        name: "홍길동"
                        isOnline: true
                  - id: "brk_002"
                    name: "분반 2"
                    participantCount: 5

  post:
    tags: [Breakouts]
    summary: 분반 생성/시작
    operationId: createBreakoutRooms
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/BreakoutCreateRequest'
          example:
            roomCount: 5
            assignmentMethod: "random"
            duration: 600
            topic: "마케팅 전략 사례 분석"
            settings:
              showTimer: true
              oneMinuteWarning: true
              autoReturn: true
              allowHostBroadcast: true
    responses:
      201:
        description: 분반 생성됨

/sessions/{sessionId}/breakouts/close:
  post:
    tags: [Breakouts]
    summary: 모든 분반 종료
    operationId: closeAllBreakoutRooms
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              returnDelay:
                type: integer
                default: 60
                description: 복귀까지 대기 시간 (초)
    responses:
      200:
        description: 분반 종료됨

/sessions/{sessionId}/breakouts/{roomId}/join:
  post:
    tags: [Breakouts]
    summary: 분반 입장
    operationId: joinBreakoutRoom
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: roomId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 입장 완료
        content:
          application/json:
            example:
              success: true
              data:
                roomId: "brk_001"
                roomName: "분반 1"
                topic: "마케팅 전략 사례 분석"
                remainingTime: 420
                participants:
                  - id: "usr_stu001"
                    name: "홍길동"
                    isOnline: true
                webrtc:
                  signalingUrl: "wss://signaling.eduforum.com/breakout/brk_001"

/sessions/{sessionId}/breakouts/{roomId}/leave:
  post:
    tags: [Breakouts]
    summary: 분반 퇴장 (메인 세션 복귀)
    operationId: leaveBreakoutRoom
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: roomId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 퇴장 완료

/sessions/{sessionId}/breakouts/{roomId}/move:
  post:
    tags: [Breakouts]
    summary: 참가자 분반 이동 (호스트)
    operationId: moveParticipant
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: roomId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [participantId, targetRoomId]
            properties:
              participantId:
                type: string
              targetRoomId:
                type: string
    responses:
      200:
        description: 이동 완료

/sessions/{sessionId}/breakouts/broadcast:
  post:
    tags: [Breakouts]
    summary: 전체 분반에 메시지 브로드캐스트
    operationId: broadcastToBreakouts
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [message]
            properties:
              message:
                type: string
              type:
                type: string
                enum: [text, announcement, time_warning]
                default: announcement
    responses:
      200:
        description: 브로드캐스트 완료
```

### 8.4 화이트보드 (Whiteboard)

```yaml
/sessions/{sessionId}/whiteboards:
  get:
    tags: [Whiteboard]
    summary: 화이트보드 목록 조회
    operationId: listWhiteboards
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 화이트보드 목록

  post:
    tags: [Whiteboard]
    summary: 화이트보드 생성
    operationId: createWhiteboard
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            properties:
              name:
                type: string
              isCollaborative:
                type: boolean
                default: true
    responses:
      201:
        description: 화이트보드 생성됨
        content:
          application/json:
            example:
              success: true
              data:
                id: "wb_001"
                name: "브레인스토밍 보드"
                isCollaborative: true
                collaborationUrl: "wss://collab.eduforum.com/wb_001"
                createdAt: "2025-03-03T09:50:00Z"

/sessions/{sessionId}/whiteboards/{whiteboardId}:
  get:
    tags: [Whiteboard]
    summary: 화이트보드 조회
    operationId: getWhiteboard
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: whiteboardId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 화이트보드 정보
        content:
          application/json:
            example:
              success: true
              data:
                id: "wb_001"
                name: "브레인스토밍 보드"
                elements:
                  - id: "elem_001"
                    type: "rectangle"
                    x: 100
                    y: 100
                    width: 200
                    height: 100
                    fill: "#FFE082"
                    stroke: "#F57C00"
                  - id: "elem_002"
                    type: "text"
                    x: 150
                    y: 140
                    content: "아이디어 1"
                    fontSize: 16
                collaborationUrl: "wss://collab.eduforum.com/wb_001"

  delete:
    tags: [Whiteboard]
    summary: 화이트보드 삭제
    operationId: deleteWhiteboard
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: whiteboardId
        in: path
        required: true
        schema:
          type: string
    responses:
      204:
        description: 삭제 완료

/sessions/{sessionId}/whiteboards/{whiteboardId}/export:
  get:
    tags: [Whiteboard]
    summary: 화이트보드 이미지로 내보내기
    operationId: exportWhiteboard
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: whiteboardId
        in: path
        required: true
        schema:
          type: string
      - name: format
        in: query
        schema:
          type: string
          enum: [png, svg, pdf]
          default: png
    responses:
      200:
        description: 내보내기 URL
        content:
          application/json:
            example:
              success: true
              data:
                downloadUrl: "https://cdn.eduforum.com/whiteboards/wb_001.png"
                expiresAt: "2025-03-03T10:50:00Z"
```

### 8.5 질문/토론 (Q&A)

```yaml
/sessions/{sessionId}/questions:
  get:
    tags: [Questions]
    summary: 질문 목록 조회
    operationId: listQuestions
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: status
        in: query
        schema:
          type: string
          enum: [pending, answered, dismissed]
      - name: sort
        in: query
        schema:
          type: string
          enum: [newest, upvotes]
          default: newest
    responses:
      200:
        description: 질문 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "qst_001"
                  content: "for 문과 while 문의 차이점이 궁금합니다."
                  askedBy:
                    id: "usr_stu001"
                    name: "홍길동"
                    isAnonymous: false
                  upvotes: 5
                  hasUpvoted: false
                  status: "pending"
                  createdAt: "2025-03-03T09:20:00Z"

  post:
    tags: [Questions]
    summary: 질문 등록
    operationId: createQuestion
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [content]
            properties:
              content:
                type: string
                maxLength: 500
              isAnonymous:
                type: boolean
                default: false
    responses:
      201:
        description: 질문 등록됨

/sessions/{sessionId}/questions/{questionId}/upvote:
  post:
    tags: [Questions]
    summary: 질문 추천
    operationId: upvoteQuestion
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: questionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 추천 완료

  delete:
    tags: [Questions]
    summary: 질문 추천 취소
    operationId: removeUpvote
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: questionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 추천 취소됨

/sessions/{sessionId}/questions/{questionId}/answer:
  post:
    tags: [Questions]
    summary: 질문 답변 (호스트)
    operationId: answerQuestion
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: questionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [answer]
            properties:
              answer:
                type: string
    responses:
      200:
        description: 답변 완료

/sessions/{sessionId}/questions/{questionId}/dismiss:
  post:
    tags: [Questions]
    summary: 질문 보류/해제 (호스트)
    operationId: dismissQuestion
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
      - name: questionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 보류 처리됨
```

---

> **다음 파트**: Part 5에서 평가/성적 API 및 분석 API, WebSocket API를 작성합니다.

---

## 9. 평가 API (Assessment)

### 9.1 성적 관리 (Grades)

```yaml
/courses/{courseId}/grades:
  get:
    tags: [Grades]
    summary: 코스 전체 성적 조회 (교수용)
    operationId: listGrades
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: studentId
        in: query
        schema:
          type: string
      - $ref: '#/components/parameters/PageParam'
    responses:
      200:
        description: 성적 목록
        content:
          application/json:
            example:
              success: true
              data:
                courseInfo:
                  id: "crs_abc123"
                  name: "컴퓨터 프로그래밍 기초"
                  totalStudents: 45
                gradeItems:
                  - id: "asg_001"
                    title: "과제 1: Python 기초"
                    type: "assignment"
                    maxScore: 100
                    weight: 10
                    averageScore: 85.5
                    submissionRate: 95.6
                  - id: "quiz_001"
                    title: "Week 1 퀴즈"
                    type: "quiz"
                    maxScore: 100
                    weight: 5
                    averageScore: 78.2
                students:
                  - studentId: "usr_stu001"
                    name: "홍길동"
                    grades:
                      - itemId: "asg_001"
                        score: 95
                        percentage: 95
                      - itemId: "quiz_001"
                        score: 80
                        percentage: 80
                    totalScore: 175
                    totalPercentage: 87.5
                    rank: 5

/courses/{courseId}/grades/me:
  get:
    tags: [Grades]
    summary: 내 성적 조회 (학생용)
    operationId: getMyGrades
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 내 성적
        content:
          application/json:
            example:
              success: true
              data:
                courseInfo:
                  name: "컴퓨터 프로그래밍 기초"
                  instructor: "김교수"
                summary:
                  totalScore: 175
                  totalPercentage: 87.5
                  rank: 5
                  totalStudents: 45
                  letterGrade: "A"
                grades:
                  - id: "asg_001"
                    title: "과제 1: Python 기초"
                    type: "assignment"
                    score: 95
                    maxScore: 100
                    weight: 10
                    feedback: "잘 작성했습니다."
                    gradedAt: "2025-03-11T10:00:00Z"
                  - id: "quiz_001"
                    title: "Week 1 퀴즈"
                    type: "quiz"
                    score: 80
                    maxScore: 100
                    weight: 5

/courses/{courseId}/grades/export:
  get:
    tags: [Grades]
    summary: 성적표 내보내기
    operationId: exportGrades
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: format
        in: query
        schema:
          type: string
          enum: [csv, xlsx, pdf]
          default: xlsx
    responses:
      200:
        description: 다운로드 URL
        content:
          application/json:
            example:
              success: true
              data:
                downloadUrl: "https://cdn.eduforum.com/exports/grades_cs101.xlsx"
                expiresAt: "2025-03-03T11:00:00Z"
```

### 9.2 AI 채점 (AI Grading)

```yaml
/courses/{courseId}/assignments/{assignmentId}/submissions/{submissionId}/ai-grade:
  post:
    tags: [Grades]
    summary: AI 자동 채점 요청
    operationId: requestAIGrading
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
      - name: submissionId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              rubric:
                type: array
                items:
                  type: object
                  properties:
                    criterion:
                      type: string
                    maxPoints:
                      type: integer
                    description:
                      type: string
    responses:
      202:
        description: AI 채점 요청 접수
        content:
          application/json:
            example:
              success: true
              data:
                jobId: "ai_job_001"
                status: "processing"
                estimatedTime: 30

  get:
    tags: [Grades]
    summary: AI 채점 결과 조회
    operationId: getAIGradingResult
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: assignmentId
        in: path
        required: true
        schema:
          type: string
      - name: submissionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: AI 채점 결과
        content:
          application/json:
            example:
              success: true
              data:
                status: "completed"
                suggestedScore: 85
                confidence: 0.92
                rubricScores:
                  - criterion: "코드 정확성"
                    score: 28
                    maxPoints: 30
                    feedback: "대부분의 테스트 케이스를 통과했습니다."
                  - criterion: "코드 스타일"
                    score: 18
                    maxPoints: 20
                    feedback: "변수명이 명확하지만 일부 주석이 부족합니다."
                overallFeedback: "전체적으로 잘 작성된 코드입니다. 예외 처리를 더 추가하면 좋겠습니다."
                isApproved: false
```

### 9.3 동료 평가 (Peer Evaluation)

```yaml
/courses/{courseId}/peer-evaluations:
  get:
    tags: [Grades]
    summary: 동료 평가 목록 조회
    operationId: listPeerEvaluations
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: status
        in: query
        schema:
          type: string
          enum: [pending, in_progress, completed]
    responses:
      200:
        description: 동료 평가 목록

  post:
    tags: [Grades]
    summary: 동료 평가 생성 (교수)
    operationId: createPeerEvaluation
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [assignmentId, dueDate]
            properties:
              assignmentId:
                type: string
              dueDate:
                type: string
                format: date-time
              evaluationsPerStudent:
                type: integer
                default: 3
              anonymous:
                type: boolean
                default: true
              rubric:
                type: array
                items:
                  type: object
                  properties:
                    criterion:
                      type: string
                    maxPoints:
                      type: integer
    responses:
      201:
        description: 동료 평가 생성됨

/courses/{courseId}/peer-evaluations/{evaluationId}/assigned:
  get:
    tags: [Grades]
    summary: 나에게 배정된 평가 대상 조회
    operationId: getAssignedEvaluations
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: evaluationId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 배정된 평가 대상
        content:
          application/json:
            example:
              success: true
              data:
                evaluationId: "peer_001"
                dueDate: "2025-03-15T23:59:59Z"
                assigned:
                  - id: "target_001"
                    submissionId: "sub_005"
                    status: "pending"
                  - id: "target_002"
                    submissionId: "sub_012"
                    status: "completed"
                    submittedAt: "2025-03-14T10:00:00Z"

/courses/{courseId}/peer-evaluations/{evaluationId}/submit:
  post:
    tags: [Grades]
    summary: 동료 평가 제출
    operationId: submitPeerEvaluation
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: evaluationId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [targetId, rubricScores]
            properties:
              targetId:
                type: string
              rubricScores:
                type: array
                items:
                  type: object
                  properties:
                    criterionId:
                      type: string
                    score:
                      type: integer
                    comment:
                      type: string
              overallComment:
                type: string
    responses:
      201:
        description: 평가 제출됨

/courses/{courseId}/peer-evaluations/{evaluationId}/results:
  get:
    tags: [Grades]
    summary: 동료 평가 결과 (교수: 전체, 학생: 본인만)
    operationId: getPeerEvaluationResults
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: evaluationId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 동료 평가 결과
```

### 9.4 코드 평가 (Code Assessment)

```yaml
/courses/{courseId}/code-submissions:
  post:
    tags: [Grades]
    summary: 코드 제출 및 자동 평가
    operationId: submitCode
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [assignmentId, code, language]
            properties:
              assignmentId:
                type: string
              code:
                type: string
              language:
                type: string
                enum: [python, java, javascript, c, cpp]
              stdin:
                type: string
    responses:
      200:
        description: 실행 결과
        content:
          application/json:
            example:
              success: true
              data:
                submissionId: "code_001"
                status: "completed"
                executionTime: 0.125
                memoryUsed: 12.5
                output: "Hello, World!\n"
                testResults:
                  - testCase: "기본 테스트"
                    passed: true
                    expected: "Hello, World!"
                    actual: "Hello, World!"
                  - testCase: "경계값 테스트"
                    passed: false
                    expected: "Error"
                    actual: "0"
                score: 80
                totalTests: 10
                passedTests: 8

/courses/{courseId}/code-submissions/{submissionId}/plagiarism:
  get:
    tags: [Grades]
    summary: 표절 검사 결과 조회
    operationId: getPlagiarismResult
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: submissionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 표절 검사 결과
        content:
          application/json:
            example:
              success: true
              data:
                submissionId: "code_001"
                overallSimilarity: 15.5
                status: "clean"
                matches:
                  - matchedSubmissionId: "code_050"
                    similarity: 12.3
                    matchedStudent: "익명 (usr_***)"
                    snippets:
                      - startLine: 10
                        endLine: 15
                        similarity: 85.0
```

---

## 10. 분석 API (Analytics)

### 10.1 실시간 분석

```yaml
/sessions/{sessionId}/analytics/realtime:
  get:
    tags: [Analytics]
    summary: 실시간 세션 분석
    operationId: getRealtimeAnalytics
    security:
      - BearerAuth: []
    parameters:
      - name: sessionId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 실시간 분석 데이터
        content:
          application/json:
            example:
              success: true
              data:
                sessionId: "ses_001"
                currentTime: "2025-03-03T09:45:00Z"
                duration: 2700
                participants:
                  total: 42
                  active: 38
                  inactive: 4
                  left: 3
                engagement:
                  overall: 78.5
                  distribution:
                    high: 20
                    medium: 15
                    low: 7
                  trend:
                    - time: "09:00"
                      score: 85
                    - time: "09:15"
                      score: 82
                    - time: "09:30"
                      score: 78
                    - time: "09:45"
                      score: 75
                participation:
                  chatMessages: 45
                  questionsAsked: 8
                  pollsParticipated: 42
                  handsRaised: 12
                alerts:
                  - type: "low_engagement"
                    count: 4
                    students:
                      - id: "usr_stu010"
                        name: "박민수"
                        engagementScore: 25
```

### 10.2 코스 분석

```yaml
/courses/{courseId}/analytics:
  get:
    tags: [Analytics]
    summary: 코스 전체 분석
    operationId: getCourseAnalytics
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: from
        in: query
        schema:
          type: string
          format: date
      - name: to
        in: query
        schema:
          type: string
          format: date
    responses:
      200:
        description: 코스 분석 데이터
        content:
          application/json:
            example:
              success: true
              data:
                courseId: "crs_abc123"
                period:
                  from: "2025-03-01"
                  to: "2025-03-31"
                overview:
                  totalSessions: 8
                  totalStudents: 45
                  averageAttendance: 92.5
                  averageEngagement: 76.8
                  completionRate: 95.0
                attendance:
                  bySession:
                    - sessionId: "ses_001"
                      title: "Week 1"
                      rate: 95.6
                    - sessionId: "ses_002"
                      title: "Week 2"
                      rate: 91.1
                  trend:
                    - week: 1
                      rate: 95.6
                    - week: 2
                      rate: 91.1
                grades:
                  average: 82.5
                  distribution:
                    - grade: "A"
                      count: 10
                    - grade: "B"
                      count: 20
                    - grade: "C"
                      count: 12
                    - grade: "D"
                      count: 3
                atRiskStudents:
                  count: 5
                  students:
                    - id: "usr_stu020"
                      name: "이철수"
                      riskScore: 85
                      reasons:
                        - "최근 3회 결석"
                        - "과제 2건 미제출"
```

### 10.3 학생별 분석

```yaml
/courses/{courseId}/analytics/students/{studentId}:
  get:
    tags: [Analytics]
    summary: 학생 개인 분석
    operationId: getStudentAnalytics
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: studentId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 학생 분석 데이터
        content:
          application/json:
            example:
              success: true
              data:
                student:
                  id: "usr_stu001"
                  name: "홍길동"
                attendance:
                  rate: 95.0
                  attended: 19
                  total: 20
                  absences:
                    - sessionId: "ses_015"
                      date: "2025-04-10"
                engagement:
                  average: 78.5
                  trend:
                    - week: 1
                      score: 85
                    - week: 2
                      score: 80
                    - week: 3
                      score: 75
                grades:
                  current: 85.5
                  rank: 8
                  trend:
                    - assignment: "과제 1"
                      score: 95
                    - quiz: "퀴즈 1"
                      score: 80
                participation:
                  chatMessages: 25
                  questionsAsked: 5
                  pollsAnswered: 18
                  breakoutContributions: 12
                comparison:
                  attendanceVsAverage: 2.5
                  engagementVsAverage: 1.7
                  gradeVsAverage: 3.0

/courses/{courseId}/analytics/me:
  get:
    tags: [Analytics]
    summary: 내 학습 분석 (학생용)
    operationId: getMyAnalytics
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 내 학습 분석
```

### 10.4 조기 경보 (Alerts)

```yaml
/courses/{courseId}/alerts:
  get:
    tags: [Analytics]
    summary: 조기 경보 알림 목록
    operationId: listAlerts
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: type
        in: query
        schema:
          type: string
          enum: [attendance, engagement, grade, deadline]
      - name: status
        in: query
        schema:
          type: string
          enum: [active, resolved, dismissed]
    responses:
      200:
        description: 알림 목록
        content:
          application/json:
            example:
              success: true
              data:
                - id: "alert_001"
                  type: "attendance"
                  severity: "high"
                  student:
                    id: "usr_stu020"
                    name: "이철수"
                  message: "최근 3회 연속 결석"
                  triggeredAt: "2025-03-28T10:00:00Z"
                  status: "active"
                  details:
                    missedSessions:
                      - "ses_018"
                      - "ses_019"
                      - "ses_020"

/courses/{courseId}/alerts/{alertId}:
  patch:
    tags: [Analytics]
    summary: 알림 상태 변경
    operationId: updateAlert
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: alertId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            properties:
              status:
                type: string
                enum: [resolved, dismissed]
              note:
                type: string
    responses:
      200:
        description: 상태 변경됨

/courses/{courseId}/alerts/settings:
  get:
    tags: [Analytics]
    summary: 알림 설정 조회
    operationId: getAlertSettings
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 알림 설정

  put:
    tags: [Analytics]
    summary: 알림 설정 변경
    operationId: updateAlertSettings
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            properties:
              attendanceThreshold:
                type: integer
                description: 연속 결석 횟수
              engagementThreshold:
                type: integer
                description: 참여도 하한선 (%)
              gradeThreshold:
                type: integer
                description: 성적 하한선 (%)
              emailNotification:
                type: boolean
              pushNotification:
                type: boolean
    responses:
      200:
        description: 설정 변경됨
```

### 10.5 리포트 생성

```yaml
/courses/{courseId}/reports:
  post:
    tags: [Analytics]
    summary: 리포트 생성 요청
    operationId: generateReport
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
    requestBody:
      required: true
      content:
        application/json:
          schema:
            type: object
            required: [type]
            properties:
              type:
                type: string
                enum: [course_summary, attendance, grades, engagement, student_individual]
              period:
                type: object
                properties:
                  from:
                    type: string
                    format: date
                  to:
                    type: string
                    format: date
              format:
                type: string
                enum: [pdf, xlsx]
                default: pdf
              studentId:
                type: string
                description: student_individual 타입 시 필요
    responses:
      202:
        description: 리포트 생성 요청 접수
        content:
          application/json:
            example:
              success: true
              data:
                reportId: "rpt_001"
                status: "generating"
                estimatedTime: 60

/courses/{courseId}/reports/{reportId}:
  get:
    tags: [Analytics]
    summary: 리포트 조회/다운로드
    operationId: getReport
    security:
      - BearerAuth: []
    parameters:
      - name: courseId
        in: path
        required: true
        schema:
          type: string
      - name: reportId
        in: path
        required: true
        schema:
          type: string
    responses:
      200:
        description: 리포트 정보
        content:
          application/json:
            example:
              success: true
              data:
                reportId: "rpt_001"
                type: "course_summary"
                status: "completed"
                downloadUrl: "https://cdn.eduforum.com/reports/rpt_001.pdf"
                expiresAt: "2025-04-01T00:00:00Z"
                generatedAt: "2025-03-28T11:00:00Z"
```

---

## 11. WebSocket API

### 11.1 연결

```yaml
WebSocket 연결 URL: wss://ws.eduforum.com/v1

연결 시 인증:
  - Query Parameter: ?token={access_token}
  - 또는 첫 메시지로 인증:
    {
      "type": "auth",
      "token": "{access_token}"
    }
```

### 11.2 메시지 형식

```json
// 클라이언트 → 서버
{
  "type": "string",       // 메시지 타입
  "channel": "string",    // 채널명 (예: session:ses_001)
  "payload": {}           // 데이터
}

// 서버 → 클라이언트
{
  "type": "string",
  "channel": "string",
  "payload": {},
  "timestamp": "ISO8601"
}
```

### 11.3 채널 및 이벤트

```yaml
# 세션 채널
channel: session:{sessionId}

# 구독
{ "type": "subscribe", "channel": "session:ses_001" }

# 구독 해제
{ "type": "unsubscribe", "channel": "session:ses_001" }

# 서버 이벤트 (session 채널)
events:
  - session.started          # 세션 시작
  - session.ended            # 세션 종료
  - participant.joined       # 참가자 입장
  - participant.left         # 참가자 퇴장
  - participant.media_changed # 미디어 상태 변경
  - hand.raised              # 손들기
  - hand.lowered             # 손 내리기
  - reaction.sent            # 리액션
  - chat.message             # 채팅 메시지
  - chat.deleted             # 채팅 삭제
  - poll.started             # 투표 시작
  - poll.vote                # 투표 발생
  - poll.closed              # 투표 종료
  - quiz.started             # 퀴즈 시작
  - quiz.closed              # 퀴즈 종료
  - breakout.started         # 분반 시작
  - breakout.closing         # 분반 종료 예고
  - breakout.ended           # 분반 종료
  - engagement.update        # 참여도 업데이트
  - screen.shared            # 화면 공유 시작
  - screen.stopped           # 화면 공유 종료
  - whiteboard.updated       # 화이트보드 업데이트

# 이벤트 페이로드 예시
{
  "type": "participant.joined",
  "channel": "session:ses_001",
  "payload": {
    "participantId": "prt_abc123",
    "user": {
      "id": "usr_stu001",
      "name": "홍길동",
      "profileImageUrl": "https://cdn.eduforum.com/profiles/stu001.jpg"
    },
    "joinedAt": "2025-03-03T09:02:00Z"
  },
  "timestamp": "2025-03-03T09:02:00Z"
}

{
  "type": "chat.message",
  "channel": "session:ses_001",
  "payload": {
    "id": "msg_001",
    "sender": {
      "id": "usr_stu001",
      "name": "홍길동",
      "role": "student"
    },
    "content": "질문이 있습니다!",
    "type": "text",
    "isPrivate": false
  },
  "timestamp": "2025-03-03T09:15:00Z"
}

{
  "type": "engagement.update",
  "channel": "session:ses_001",
  "payload": {
    "overall": 78.5,
    "distribution": {
      "high": 20,
      "medium": 15,
      "low": 7
    },
    "alerts": [
      {
        "userId": "usr_stu010",
        "score": 25,
        "status": "inactive"
      }
    ]
  },
  "timestamp": "2025-03-03T09:30:00Z"
}
```

### 11.4 분반 채널

```yaml
channel: breakout:{roomId}

events:
  - breakout.participant_joined
  - breakout.participant_left
  - breakout.chat_message
  - breakout.time_warning
  - breakout.host_broadcast
```

### 11.5 화이트보드 채널 (CRDT)

```yaml
channel: whiteboard:{whiteboardId}

# 클라이언트 → 서버 (동기화)
{
  "type": "whiteboard.operation",
  "channel": "whiteboard:wb_001",
  "payload": {
    "operation": "add",
    "element": {
      "id": "elem_003",
      "type": "rectangle",
      "x": 200,
      "y": 200,
      "width": 100,
      "height": 50,
      "fill": "#E3F2FD"
    },
    "vectorClock": { "usr_001": 5 }
  }
}

# 서버 → 클라이언트 (브로드캐스트)
{
  "type": "whiteboard.sync",
  "channel": "whiteboard:wb_001",
  "payload": {
    "operation": "add",
    "element": { ... },
    "author": "usr_001",
    "vectorClock": { "usr_001": 5, "usr_002": 3 }
  },
  "timestamp": "2025-03-03T09:52:00Z"
}
```

### 11.6 알림 채널

```yaml
channel: user:{userId}

events:
  - notification.new           # 새 알림
  - notification.read          # 알림 읽음
  - session.reminder           # 세션 시작 알림
  - assignment.deadline        # 과제 마감 알림
  - grade.published            # 성적 공개 알림
```

### 11.7 Heartbeat

```yaml
# 클라이언트 → 서버 (30초마다)
{ "type": "ping" }

# 서버 → 클라이언트
{ "type": "pong", "timestamp": "2025-03-03T09:30:00Z" }

# 연결 유지 실패 시 자동 재연결 권장
```

---

## 부록: 추가 스키마 정의

```yaml
components:
  schemas:
    PollCreateRequest:
      type: object
      required: [question, type, options]
      properties:
        question:
          type: string
        type:
          type: string
          enum: [single_choice, multiple_choice, word_cloud, scale]
        options:
          type: array
          items:
            type: object
            properties:
              text:
                type: string
        settings:
          type: object
          properties:
            anonymous:
              type: boolean
            showResultsRealtime:
              type: boolean
            allowChangeVote:
              type: boolean
            duration:
              type: integer

    QuizCreateRequest:
      type: object
      required: [title, questions]
      properties:
        title:
          type: string
        description:
          type: string
        settings:
          type: object
          properties:
            timeLimit:
              type: integer
            shuffleQuestions:
              type: boolean
            shuffleOptions:
              type: boolean
            showCorrectAnswers:
              type: string
              enum: [immediately, after_close, never]
            allowRetake:
              type: boolean
            passingScore:
              type: integer
        questions:
          type: array
          items:
            type: object
            properties:
              type:
                type: string
                enum: [single_choice, multiple_choice, true_false, short_answer]
              question:
                type: string
              options:
                type: array
                items:
                  type: object
                  properties:
                    text:
                      type: string
                    isCorrect:
                      type: boolean
              points:
                type: integer
              explanation:
                type: string

    QuizUpdateRequest:
      type: object
      properties:
        title:
          type: string
        description:
          type: string
        settings:
          type: object
        questions:
          type: array

    BreakoutCreateRequest:
      type: object
      required: [roomCount]
      properties:
        roomCount:
          type: integer
          minimum: 2
          maximum: 50
        assignmentMethod:
          type: string
          enum: [random, balanced, manual, grade_based]
          default: random
        duration:
          type: integer
          description: 초 단위
        topic:
          type: string
        settings:
          type: object
          properties:
            showTimer:
              type: boolean
            oneMinuteWarning:
              type: boolean
            autoReturn:
              type: boolean
            allowHostBroadcast:
              type: boolean
        manualAssignments:
          type: array
          items:
            type: object
            properties:
              roomNumber:
                type: integer
              participantIds:
                type: array
                items:
                  type: string
```

---

> **문서 종료**: 이 API 설계서는 EduForum 플랫폼의 전체 REST API 및 WebSocket API를 포함합니다.
