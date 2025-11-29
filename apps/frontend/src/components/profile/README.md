# Profile Management Components

Phase 5.6 Sprint 2 - 프로필 관리 Frontend UI

## 개요

사용자 프로필 정보 관리 및 계정 보안 설정을 위한 컴포넌트 모음입니다.

## 구현된 기능

### 1. 페이지

#### `/app/(dashboard)/settings/profile/page.tsx`
- 프로필 설정 메인 페이지
- 아바타, 기본 정보, 계정 보안 섹션으로 구성
- 실시간 프로필 업데이트 및 에러 처리

**주요 기능:**
- 프로필 조회 (GET /v1/users/profile)
- 프로필 업데이트 (PUT /v1/users/profile)
- 아바타 업로드/삭제
- 이메일 변경 요청
- 비밀번호 변경

### 2. 컴포넌트

#### `avatar-upload.tsx`
프로필 사진 업로드 및 관리 컴포넌트

**기능:**
- 파일 선택 및 드래그 앤 드롭 업로드
- 이미지 미리보기
- 파일 유효성 검사 (타입: JPG/PNG, 크기: 5MB 이하)
- 업로드 진행 상태 표시
- 아바타 삭제

**Props:**
```typescript
{
  currentAvatar?: string;      // 현재 아바타 URL
  userName: string;             // 사용자 이름 (fallback용)
  onUpload: (file: File) => Promise<void>;
  onDelete: () => Promise<void>;
  isUploading?: boolean;
  className?: string;
}
```

#### `profile-form.tsx`
프로필 기본 정보 수정 폼

**기능:**
- 이름, 소개 입력 및 수정
- 실시간 유효성 검사
- 변경사항 감지 및 저장 버튼 활성화
- 성공 메시지 표시

**Props:**
```typescript
{
  initialData: {
    name: string;
    bio?: string;
  };
  onSubmit: (data: ProfileUpdateRequest) => Promise<void>;
  isLoading?: boolean;
}
```

#### `password-change-modal.tsx`
비밀번호 변경 모달

**기능:**
- 현재 비밀번호 확인
- 새 비밀번호 입력 및 확인
- 비밀번호 강도 표시 (Progress bar)
- 비밀번호 보기/숨기기 토글
- 실시간 유효성 검사
- 성공 시 자동 닫기

**Props:**
```typescript
{
  open: boolean;
  onClose: () => void;
  onSubmit: (data: PasswordChangeRequest) => Promise<void>;
  isLoading?: boolean;
}
```

**비밀번호 요구사항:**
- 최소 8자 이상
- 대문자, 소문자, 숫자, 특수문자 포함
- 현재 비밀번호와 다른 비밀번호

#### `email-change-modal.tsx`
이메일 변경 모달

**기능:**
- 2단계 프로세스 (입력 → 인증 이메일 발송 안내)
- 현재 이메일 표시 (읽기 전용)
- 새 이메일 입력 및 유효성 검사
- 현재 비밀번호 확인
- 인증 이메일 발송 안내

**Props:**
```typescript
{
  open: boolean;
  currentEmail: string;
  onClose: () => void;
  onSubmit: (data: EmailChangeRequest) => Promise<void>;
  isLoading?: boolean;
}
```

#### `profile-card.tsx`
프로필 정보 카드 (읽기 전용)

**기능:**
- 아바타, 이름, 역할, 이메일 표시
- 소개 표시
- 가입일 표시
- 역할별 배지 스타일 (관리자/교수/조교/학생)

**Props:**
```typescript
{
  profile: Profile;
  className?: string;
}
```

## API 클라이언트

### `/lib/api/profile.ts`

프로필 관련 API 호출 함수 모음:

```typescript
// 프로필 조회
getProfile(): Promise<Profile>

// 프로필 업데이트
updateProfile(data: ProfileUpdateRequest): Promise<Profile>

// 아바타 업로드
uploadAvatar(file: File): Promise<string>

// 아바타 삭제
deleteAvatar(): Promise<void>

// 이메일 변경 요청
changeEmail(data: EmailChangeRequest): Promise<void>

// 이메일 변경 인증
verifyEmailChange(data: EmailVerifyRequest): Promise<void>

// 비밀번호 변경
changePassword(data: PasswordChangeRequest): Promise<void>
```

## 타입 정의

### `/types/profile.ts`

```typescript
// 프로필 정보
interface Profile extends User {
  // User 타입에서 확장
}

// 프로필 업데이트 요청
interface ProfileUpdateRequest {
  name?: string;
  bio?: string;
}

// 아바타 업로드 응답
interface AvatarUploadResponse {
  avatarUrl: string;
}

// 이메일 변경 요청
interface EmailChangeRequest {
  newEmail: string;
  currentPassword: string;
}

// 이메일 인증 요청
interface EmailVerifyRequest {
  token: string;
}

// 비밀번호 변경 요청
interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

// 비밀번호 강도
type PasswordStrength = 'weak' | 'medium' | 'strong';
```

## API 엔드포인트

업데이트된 엔드포인트 (`/lib/api/endpoints.ts`):

```typescript
PROFILE: {
  GET: '/v1/users/profile',
  UPDATE: '/v1/users/profile',
  UPLOAD_AVATAR: '/v1/users/profile/avatar',
  DELETE_AVATAR: '/v1/users/profile/avatar',
  CHANGE_EMAIL: '/v1/users/email/change',
  VERIFY_EMAIL: '/v1/users/email/verify',
  CHANGE_PASSWORD: '/v1/users/password',
}
```

## 상태 관리

### AuthStore 업데이트

`updateUser` 메서드 추가:

```typescript
// 사용자 정보 부분 업데이트
updateUser: (userData: Partial<User>) => void
```

프로필 업데이트 시 authStore의 user 정보도 함께 업데이트하여 전역 상태 동기화.

## 사용 예시

### 프로필 페이지 라우팅

```typescript
// 접근 경로
/settings/profile

// 예시
import ProfilePage from '@/app/(dashboard)/settings/profile/page';
```

### 컴포넌트 사용

```typescript
import {
  AvatarUpload,
  ProfileForm,
  PasswordChangeModal,
  EmailChangeModal,
  ProfileCard,
} from '@/components/profile';

// 아바타 업로드
<AvatarUpload
  currentAvatar={profile.avatar}
  userName={profile.name}
  onUpload={handleAvatarUpload}
  onDelete={handleAvatarDelete}
  isUploading={isUploadingAvatar}
/>

// 프로필 폼
<ProfileForm
  initialData={{ name: profile.name, bio: profile.bio }}
  onSubmit={handleProfileUpdate}
  isLoading={isUpdating}
/>

// 비밀번호 변경 모달
<PasswordChangeModal
  open={passwordModalOpen}
  onClose={() => setPasswordModalOpen(false)}
  onSubmit={handlePasswordChange}
/>
```

## UI/UX 특징

### 1. 아바타 업로드
- 큰 원형 아바타 (128x128px)
- 드래그 앤 드롭 영역 시각적 표시
- 업로드 진행 중 로딩 오버레이
- Fallback: 이름 이니셜 또는 유저 아이콘

### 2. 폼 유효성 검사
- 실시간 입력 검증
- 에러 메시지 즉시 표시
- 변경사항 감지 및 버튼 활성화/비활성화

### 3. 비밀번호 강도 표시
- 0-4단계 강도 측정
- Progress bar로 시각화
- 색상 코드 (빨강 → 주황 → 노랑 → 초록)

### 4. 모달 UX
- 성공 시 애니메이션 피드백
- 자동 닫기 (2-3초 후)
- 비밀번호 보기/숨기기 토글
- 명확한 단계별 안내

### 5. 반응형 디자인
- 모바일/태블릿/데스크톱 대응
- max-width: 4xl (896px) 컨테이너
- 카드 기반 레이아웃

## 의존성

### 사용된 UI 컴포넌트 (shadcn/ui)
- Avatar, AvatarImage, AvatarFallback
- Button
- Card, CardHeader, CardTitle, CardContent
- Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter
- Input
- Label
- Textarea
- Alert
- Badge
- Progress
- Separator

### 아이콘 (lucide-react)
- Upload, X, User, Mail, Calendar
- Loader2
- Eye, EyeOff
- CheckCircle2
- Lock

## 주의사항

1. **파일 업로드 제한**
   - 최대 크기: 5MB
   - 허용 타입: JPG, PNG만

2. **이메일 변경**
   - 2단계 인증 필요
   - 인증 링크 유효기간: 24시간

3. **비밀번호 변경**
   - 현재 비밀번호 확인 필수
   - 새 비밀번호는 현재 비밀번호와 달라야 함
   - 강도 요구사항 충족 필요

4. **상태 동기화**
   - 프로필 업데이트 시 authStore.updateUser() 호출하여 전역 상태 동기화
   - localStorage에 영속화된 user 정보도 업데이트됨

## 테스트 시나리오

### 1. 프로필 업데이트
- [ ] 이름 변경 후 저장
- [ ] 소개 추가/수정 후 저장
- [ ] 변경사항 없이 저장 버튼 비활성화 확인
- [ ] 취소 버튼으로 원래 값 복원

### 2. 아바타 관리
- [ ] 파일 선택으로 업로드
- [ ] 드래그 앤 드롭으로 업로드
- [ ] 5MB 초과 파일 업로드 시 에러
- [ ] PDF 등 허용되지 않은 파일 타입 업로드 시 에러
- [ ] 아바타 삭제 후 fallback 표시

### 3. 비밀번호 변경
- [ ] 현재 비밀번호 틀렸을 때 에러
- [ ] 새 비밀번호 요구사항 미충족 시 에러
- [ ] 비밀번호 확인 불일치 시 에러
- [ ] 현재 비밀번호와 동일한 새 비밀번호 입력 시 에러
- [ ] 성공 시 모달 자동 닫기

### 4. 이메일 변경
- [ ] 현재 이메일과 동일한 이메일 입력 시 에러
- [ ] 잘못된 이메일 형식 입력 시 에러
- [ ] 현재 비밀번호 틀렸을 때 에러
- [ ] 성공 시 인증 이메일 발송 안내 표시

## 관련 Issues

- #42: 프로필 페이지 UI 개발
- #43: 이미지 업로드 컴포넌트 개발
- #44: 비밀번호 변경 모달 개발

## 향후 개선 사항

1. 이미지 크롭/리사이즈 기능 추가
2. 다크 모드 지원 강화
3. 프로필 사진 여러 개 업로드 및 선택
4. 소셜 미디어 링크 추가 필드
5. 알림 설정 통합
