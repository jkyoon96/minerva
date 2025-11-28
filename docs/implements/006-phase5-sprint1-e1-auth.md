# Phase 5 Sprint 1 - E1 인증 시스템 완성 (BE + FE)

## 개요

| 항목 | 내용 |
|------|------|
| **작업일** | 2025-01-29 |
| **Phase** | Phase 5 - Sprint 1 |
| **Epic** | E1 - 사용자 인증 및 권한 관리 |
| **범위** | Backend API + Frontend UI |
| **관련 Issues (BE)** | #2, #17, #18, #28, #33 |
| **관련 Issues (FE)** | #7, #14, #20, #31, #32 |

---

## Part 1: Backend API

### 구현된 API 엔드포인트 (19개)

#### 1. 인증 API (AuthController)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/auth/register` | 회원가입 |
| POST | `/v1/auth/login` | 로그인 |
| POST | `/v1/auth/logout` | 로그아웃 |
| POST | `/v1/auth/refresh` | 토큰 갱신 |
| POST | `/v1/auth/verify-email` | 이메일 인증 확인 |
| GET | `/v1/auth/me` | 현재 사용자 정보 조회 |
| PUT | `/v1/auth/profile` | 프로필 수정 |
| POST | `/v1/auth/password/reset` | 비밀번호 재설정 요청 |
| POST | `/v1/auth/password/reset/confirm` | 비밀번호 재설정 확인 |

#### 2. OAuth API (OAuthController)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/auth/oauth/google` | Google OAuth 시작 |
| GET | `/v1/auth/oauth/google/callback` | Google OAuth 콜백 |
| POST | `/v1/auth/oauth/google/login` | Google OAuth API 로그인 |
| GET | `/v1/auth/oauth/microsoft` | Microsoft OAuth 시작 |
| GET | `/v1/auth/oauth/microsoft/callback` | Microsoft OAuth 콜백 |
| POST | `/v1/auth/oauth/microsoft/login` | Microsoft OAuth API 로그인 |

#### 3. 역할 관리 API (RoleController)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/admin/roles` | 전체 역할 목록 조회 |
| POST | `/v1/admin/users/{userId}/roles` | 사용자에게 역할 할당 |
| DELETE | `/v1/admin/users/{userId}/roles/{roleId}` | 사용자 역할 제거 |
| GET | `/v1/admin/users/{userId}/roles` | 사용자 역할 목록 조회 |

### BE 파일 구조

```
apps/backend/src/main/java/com/eduforum/api/domain/auth/
├── controller/
│   ├── AuthController.java      # 인증 API (9 endpoints)
│   ├── OAuthController.java     # OAuth API (6 endpoints)
│   └── RoleController.java      # 역할 관리 API (4 endpoints)
├── service/
│   ├── AuthService.java         # 인증 핵심 로직
│   ├── UserService.java         # 사용자 관리
│   ├── PasswordResetService.java # 비밀번호 재설정
│   ├── OAuthService.java        # OAuth 처리
│   └── RoleService.java         # 역할 관리
├── entity/
│   ├── PasswordResetToken.java
│   ├── EmailVerificationToken.java
│   └── OAuthAccount.java
├── repository/
│   ├── PasswordResetTokenRepository.java
│   ├── EmailVerificationTokenRepository.java
│   ├── OAuthAccountRepository.java
│   └── UserRoleRepository.java
└── dto/
    ├── PasswordResetRequest.java
    ├── PasswordResetConfirmRequest.java
    ├── UserProfileResponse.java
    ├── UserProfileUpdateRequest.java
    ├── EmailVerificationRequest.java
    ├── OAuthLoginRequest.java
    ├── RoleResponse.java
    ├── AssignRoleRequest.java
    └── LogoutRequest.java
```

### BE 주요 기능

1. **회원가입** - BCrypt 해싱, 이메일 중복 검증, 기본 역할 할당
2. **JWT 인증** - Access Token (1h), Refresh Token (14d)
3. **OAuth 로그인** - Google, Microsoft 지원
4. **비밀번호 재설정** - UUID 토큰, 1시간 유효
5. **RBAC** - 역할 기반 권한 관리

---

## Part 2: Frontend UI

### 구현된 컴포넌트

#### 1. 인증 폼 컴포넌트

| 컴포넌트 | 파일 | 기능 |
|----------|------|------|
| LoginForm | `components/auth/login-form.tsx` | 이메일/비밀번호 로그인 |
| RegisterForm | `components/auth/register-form.tsx` | 회원가입 (비밀번호 강도 표시) |
| OAuthButtons | `components/auth/oauth-buttons.tsx` | Google/Microsoft 버튼 |
| AuthGuard | `components/auth/auth-guard.tsx` | 인증 보호 HOC |

#### 2. 인증 페이지

| 페이지 | 경로 | 기능 |
|--------|------|------|
| 로그인 | `/login` | 로그인 폼 + OAuth 버튼 |
| 회원가입 | `/register` | 회원가입 폼 |
| 비밀번호 찾기 | `/forgot-password` | 이메일 입력 |
| 비밀번호 재설정 | `/reset-password` | 새 비밀번호 설정 |

### FE 파일 구조

```
apps/frontend/src/
├── app/(auth)/
│   ├── layout.tsx              # 인증 레이아웃
│   ├── login/page.tsx          # 로그인 페이지
│   ├── register/page.tsx       # 회원가입 페이지
│   ├── forgot-password/page.tsx # 비밀번호 찾기
│   └── reset-password/page.tsx  # 비밀번호 재설정
├── components/auth/
│   ├── login-form.tsx          # 로그인 폼
│   ├── register-form.tsx       # 회원가입 폼
│   ├── oauth-buttons.tsx       # OAuth 버튼
│   ├── auth-guard.tsx          # 인증 가드
│   └── index.ts                # 배럴 익스포트
├── stores/
│   └── authStore.ts            # Zustand 인증 상태
├── lib/
│   ├── api/auth.ts             # 인증 API 클라이언트
│   ├── api/client.ts           # Axios 인터셉터
│   └── validation.ts           # 유효성 검사 유틸
└── types/
    └── auth.ts                 # 타입 정의
```

### FE 주요 기능

1. **LoginForm** - 이메일/비밀번호 입력, 로그인 유지, 에러 처리
2. **RegisterForm** - 다중 필드 검증, 비밀번호 강도 표시 (4단계)
3. **OAuthButtons** - Google/Microsoft OAuth 리다이렉트
4. **AuthGuard** - 인증 필요 페이지 보호, 자동 리다이렉트
5. **validation.ts** - 이메일, 비밀번호, 이름 유효성 검사

---

## 커밋 정보

### BE 커밋
```
commit c309ce5
feat: Phase 5 Sprint 1 - E1 인증 BE API 완성 (#2, #17, #18, #28, #33)
26 files changed, 1896 insertions(+), 65 deletions(-)
```

### FE 커밋
```
commit d18d709
feat: Phase 5 Sprint 1 - E1 인증 FE UI 완성 (#7, #14, #20, #31, #32)
13 files changed, 2109 insertions(+), 77 deletions(-)
```

---

## 완료된 GitHub Issues

### Backend Issues

| Issue | 제목 | 상태 |
|-------|------|------|
| #2 | [E1-S1-T1] [BE] 회원가입 API 엔드포인트 개발 | ✅ Closed |
| #17 | [E1-S3-T1] [BE] Google OAuth 통합 | ✅ Closed |
| #18 | [E1-S3-T2] [BE] Microsoft OAuth 통합 | ✅ Closed |
| #28 | [E1-S5-T1] [BE] 비밀번호 재설정 토큰 생성 API | ✅ Closed |
| #33 | [E1-S6-T1] [DB] 역할 및 권한 데이터베이스 스키마 설계 | ✅ Closed |

### Frontend Issues

| Issue | 제목 | 상태 |
|-------|------|------|
| #7 | [E1-S1-T6] [FE] 회원가입 폼 UI 컴포넌트 개발 | ✅ Closed |
| #14 | [E1-S2-T5] [FE] 로그인 폼 UI 컴포넌트 개발 | ✅ Closed |
| #20 | [E1-S3-T4] [FE] 소셜 로그인 버튼 UI 컴포넌트 | ✅ Closed |
| #31 | [E1-S5-T4] [FE] 비밀번호 찾기 UI 개발 | ✅ Closed |
| #32 | [E1-S5-T5] [FE] 새 비밀번호 설정 UI 개발 | ✅ Closed |

**총 10개 Issues 완료**

---

## 기술 스택

### Backend
- Spring Boot 3.2.1
- Java 17
- Spring Security + JWT (JJWT 0.12.3)
- BCrypt 비밀번호 해싱
- PostgreSQL + JPA

### Frontend
- Next.js 14 (App Router)
- React 18
- TypeScript
- Tailwind CSS
- Zustand (상태 관리)
- Axios (HTTP 클라이언트)
- shadcn/ui (UI 컴포넌트)

---

## TODO (후속 작업)

1. **이메일 서비스 연동**
   - 이메일 인증 메일 발송
   - 비밀번호 재설정 메일 발송

2. **OAuth 실제 API 연동**
   - Google/Microsoft 실제 토큰 교환 구현
   - 현재는 샘플 데이터로 동작

3. **추가 FE 기능**
   - 2FA 설정 UI (#26, #27)
   - 프로필 페이지 (#42, #43, #44)
   - 역할 관리 Admin UI (#36)
   - 403 권한 오류 페이지 (#38)

---

## 다음 단계

**Phase 5 Sprint 2: E2 코스 관리**
- 코스 CRUD API
- 수강생 관리 API
- 코스 목록/상세 UI
