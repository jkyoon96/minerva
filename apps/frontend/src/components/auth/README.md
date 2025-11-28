# Authentication System Documentation

## Overview

EduForum의 E1 인증 시스템 Frontend UI 구현입니다. Next.js 14 App Router, React 18, TypeScript, Tailwind CSS, Zustand를 사용하여 개발되었습니다.

## File Structure

```
apps/frontend/src/
├── app/(auth)/
│   ├── layout.tsx                    # 인증 페이지 공통 레이아웃
│   ├── login/page.tsx                # 로그인 페이지
│   ├── register/page.tsx             # 회원가입 페이지
│   ├── forgot-password/page.tsx      # 비밀번호 찾기 페이지
│   └── reset-password/page.tsx       # 비밀번호 재설정 페이지
├── components/auth/
│   ├── index.ts                      # Barrel export
│   ├── login-form.tsx                # 로그인 폼 컴포넌트
│   ├── register-form.tsx             # 회원가입 폼 컴포넌트
│   ├── oauth-buttons.tsx             # OAuth 로그인 버튼
│   └── auth-guard.tsx                # 인증 가드 HOC
├── lib/
│   ├── validation.ts                 # 폼 유효성 검사 유틸리티
│   └── api/
│       ├── auth.ts                   # 인증 API 클라이언트 (기존)
│       ├── client.ts                 # Axios 인스턴스 (기존)
│       └── types.ts                  # API 타입 정의 (기존)
├── stores/
│   └── authStore.ts                  # 인증 상태 관리 (Zustand, 기존)
└── types/
    ├── index.ts                      # 공통 타입 (기존)
    └── auth.ts                       # 인증 관련 타입 (기존)
```

## Components

### 1. LoginForm (`login-form.tsx`)

로그인 폼 컴포넌트

**Features:**
- 이메일/비밀번호 입력
- 실시간 유효성 검사
- "로그인 유지" 체크박스
- 에러 메시지 표시
- 로딩 상태 처리
- API 연동 (POST /v1/auth/login)

**Props:**
```typescript
interface LoginFormProps {
  redirectTo?: string; // 로그인 성공 후 리다이렉트 경로 (기본: /dashboard)
}
```

**Usage:**
```tsx
import { LoginForm } from '@/components/auth';

<LoginForm redirectTo="/dashboard" />
```

### 2. RegisterForm (`register-form.tsx`)

회원가입 폼 컴포넌트

**Features:**
- 이름, 이메일, 비밀번호, 비밀번호 확인, 역할 선택
- 실시간 유효성 검사
- 비밀번호 강도 표시 (4단계)
- 비밀번호 일치 확인
- 에러 메시지 표시
- 로딩 상태 처리
- API 연동 (POST /v1/auth/register)

**Props:**
```typescript
interface RegisterFormProps {
  redirectTo?: string; // 회원가입 성공 후 리다이렉트 경로 (기본: /dashboard)
}
```

**Usage:**
```tsx
import { RegisterForm } from '@/components/auth';

<RegisterForm redirectTo="/dashboard" />
```

### 3. OAuthButtons (`oauth-buttons.tsx`)

소셜 로그인 버튼 컴포넌트

**Features:**
- Google 로그인
- Microsoft 로그인
- 백엔드 OAuth 엔드포인트로 리다이렉트

**Props:**
```typescript
interface OAuthButtonsProps {
  disabled?: boolean; // 버튼 비활성화 여부
}
```

**Usage:**
```tsx
import { OAuthButtons } from '@/components/auth';

<OAuthButtons disabled={isLoading} />
```

### 4. AuthGuard (`auth-guard.tsx`)

인증 가드 HOC/컴포넌트

**Features:**
- 인증 필요 페이지 보호
- 비인증 사용자 리다이렉트
- 초기 로드 시 프로필 자동 조회
- 로딩 상태 표시

**Props:**
```typescript
interface AuthGuardProps {
  children: ReactNode;
  redirectTo?: string;     // 리다이렉트 경로 (기본: /login)
  requireAuth?: boolean;   // 인증 필요 여부 (기본: true)
}
```

**Usage (Component):**
```tsx
import { AuthGuard } from '@/components/auth';

<AuthGuard requireAuth={true} redirectTo="/login">
  <ProtectedContent />
</AuthGuard>
```

**Usage (HOC):**
```tsx
import { withAuthGuard } from '@/components/auth';

const ProtectedPage = withAuthGuard(MyComponent, {
  requireAuth: true,
  redirectTo: '/login'
});
```

## Pages

### 1. Login Page (`app/(auth)/login/page.tsx`)

**Features:**
- LoginForm 컴포넌트 사용
- OAuth 로그인 버튼
- 회원가입 링크
- 비밀번호 찾기 링크

### 2. Register Page (`app/(auth)/register/page.tsx`)

**Features:**
- RegisterForm 컴포넌트 사용
- 로그인 링크

### 3. Forgot Password Page (`app/(auth)/forgot-password/page.tsx`)

**Features:**
- 이메일 입력
- 비밀번호 재설정 링크 발송
- 성공 메시지 표시
- API 연동 (POST /v1/auth/forgot-password)

### 4. Reset Password Page (`app/(auth)/reset-password/page.tsx`)

**Features:**
- 새 비밀번호 입력
- 비밀번호 강도 표시
- 비밀번호 확인
- URL 토큰 검증
- 성공 후 자동 리다이렉트
- API 연동 (POST /v1/auth/reset-password)

## Validation Utilities

### `lib/validation.ts`

폼 유효성 검사 함수 모음

**Functions:**

1. **validateEmail(email: string): string | null**
   - 이메일 형식 검증

2. **validatePassword(password: string): string | null**
   - 비밀번호 강도 검증 (최소 8자, 대소문자, 숫자, 특수문자)

3. **validatePasswordConfirm(password: string, passwordConfirm: string): string | null**
   - 비밀번호 일치 확인

4. **validateName(name: string): string | null**
   - 이름 유효성 검증 (2-50자)

5. **validateRequired(value: string, fieldName: string): string | null**
   - 필수 입력 검증

6. **getPasswordStrength(password: string): number**
   - 비밀번호 강도 계산 (0-4)

7. **getPasswordStrengthText(strength: number): string**
   - 비밀번호 강도 텍스트 반환

8. **getPasswordStrengthColor(strength: number): string**
   - 비밀번호 강도 색상 클래스 반환

## State Management

### Zustand Auth Store (`stores/authStore.ts`)

**State:**
```typescript
{
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}
```

**Actions:**
- `login(credentials)` - 로그인
- `register(data)` - 회원가입
- `logout()` - 로그아웃
- `fetchProfile()` - 프로필 조회
- `setUser(user)` - 사용자 설정
- `setLoading(loading)` - 로딩 상태 설정
- `setError(error)` - 에러 설정
- `clearError()` - 에러 초기화

**Usage:**
```tsx
import { useAuthStore } from '@/stores/authStore';

const { user, isAuthenticated, login, logout } = useAuthStore();
```

## API Integration

### Authentication API (`lib/api/auth.ts`)

모든 API 함수는 이미 구현되어 있으며, 컴포넌트에서 직접 사용하거나 Zustand 스토어를 통해 사용합니다.

**Available Functions:**
- `login(credentials)` - POST /v1/auth/login
- `register(data)` - POST /v1/auth/register
- `logout()` - POST /v1/auth/logout
- `refreshToken(token)` - POST /v1/auth/refresh
- `getProfile()` - GET /v1/auth/profile
- `updateProfile(data)` - PATCH /v1/auth/profile
- `changePassword(current, new)` - POST /v1/auth/change-password
- `forgotPassword(email)` - POST /v1/auth/forgot-password
- `resetPassword(token, password)` - POST /v1/auth/reset-password

### Token Management

JWT 토큰은 `localStorage`에 저장되며, Axios 인터셉터를 통해 자동으로 요청 헤더에 추가됩니다.

- `accessToken` - API 요청 인증
- `refreshToken` - 토큰 갱신

토큰이 만료되면 자동으로 갱신 시도하며, 실패 시 로그인 페이지로 리다이렉트됩니다.

## Environment Variables

```env
NEXT_PUBLIC_API_URL=http://localhost:8000/api
```

## Styling

- **Design System**: shadcn/ui 기반
- **CSS Framework**: Tailwind CSS
- **Icons**: Lucide React
- **반응형**: 모바일 우선 (w-full max-w-md)

## Best Practices

1. **Client Components**: 모든 인터랙티브 컴포넌트는 `'use client'` 지시어 사용
2. **Error Handling**: API 에러는 Zustand 스토어에서 중앙 관리
3. **Loading States**: 모든 비동기 작업에 로딩 상태 표시
4. **Validation**: 클라이언트 측 실시간 유효성 검사
5. **Accessibility**: 시맨틱 HTML 및 ARIA 속성 사용
6. **Type Safety**: TypeScript로 타입 안전성 보장

## Testing Checklist

- [ ] 로그인 성공/실패 시나리오
- [ ] 회원가입 성공/실패 시나리오
- [ ] 비밀번호 찾기 이메일 발송
- [ ] 비밀번호 재설정 (유효/무효 토큰)
- [ ] OAuth 로그인 리다이렉트
- [ ] 인증 가드 (인증/비인증 페이지)
- [ ] 토큰 자동 갱신
- [ ] 로그아웃 후 상태 초기화
- [ ] 폼 유효성 검사 (모든 필드)
- [ ] 반응형 레이아웃

## Future Enhancements

- [ ] 2FA (Two-Factor Authentication)
- [ ] 이메일 인증
- [ ] 소셜 계정 연동
- [ ] 비밀번호 변경 UI
- [ ] 프로필 편집 UI
- [ ] Remember Me 기능 개선
- [ ] Rate Limiting 표시
- [ ] CAPTCHA 통합
