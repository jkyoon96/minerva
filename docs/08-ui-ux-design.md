# EduForum UI/UX 설계서

## 문서 정보

| 항목 | 내용 |
|------|------|
| 문서명 | EduForum UI/UX 설계서 |
| 버전 | 1.0.0 |
| 최종 수정일 | 2025-01-28 |
| 작성자 | Development Team |

---

## 목차

1. [개요](#1-개요)
2. [디자인 원칙](#2-디자인-원칙)
3. [디자인 시스템](#3-디자인-시스템)
4. [레이아웃 패턴](#4-레이아웃-패턴)
5. [컴포넌트 라이브러리](#5-컴포넌트-라이브러리)
6. [화면 설계 - E1: 사용자 인증](#6-화면-설계---e1-사용자-인증)
7. [화면 설계 - E2: 코스 관리](#7-화면-설계---e2-코스-관리)
8. [화면 설계 - E3: 실시간 세미나](#8-화면-설계---e3-실시간-세미나)
9. [화면 설계 - E4: 액티브 러닝 도구](#9-화면-설계---e4-액티브-러닝-도구)
10. [화면 설계 - E5: 평가 및 피드백](#10-화면-설계---e5-평가-및-피드백)
11. [화면 설계 - E6: 학습 분석](#11-화면-설계---e6-학습-분석)
12. [인터랙션 패턴](#12-인터랙션-패턴)
13. [반응형 디자인](#13-반응형-디자인)
14. [접근성](#14-접근성)

---

## 1. 개요

### 1.1 목적

본 문서는 EduForum 플랫폼의 UI/UX 설계 가이드라인을 정의합니다. 미네르바 대학의 Active Learning Forum을 참고하여 개발된 대학교/교육기관용 온라인 학습 플랫폼으로, 일관된 사용자 경험과 효율적인 학습 환경을 제공하는 것을 목표로 합니다.

### 1.2 대상 사용자

| 사용자 유형 | 설명 | 주요 태스크 |
|------------|------|------------|
| **학생 (Student)** | 강의를 수강하는 학습자 | 수업 참여, 과제 제출, 성적 확인 |
| **교수 (Professor)** | 강의를 진행하는 교수자 | 코스 관리, 수업 진행, 성적 평가 |
| **조교 (TA)** | 교수를 보조하는 조교 | 과제 채점, 학생 지원, 콘텐츠 관리 |
| **관리자 (Admin)** | 시스템 관리자 | 사용자/역할 관리, 시스템 설정 |

### 1.3 디자인 레퍼런스

- **디자인 시스템**: shadcn/ui (Radix UI 기반)
- **아이콘**: Lucide Icons
- **폰트**: Pretendard (한글), Inter (영문)

### 1.4 와이어프레임 위치

```
docs/wireframes/
├── css/
│   ├── variables.css    # 디자인 토큰
│   ├── base.css         # 기본 스타일
│   └── components.css   # 컴포넌트 스타일
├── components/          # 공통 컴포넌트
├── e1-auth/             # 사용자 인증 (21개 화면)
├── e2-course/           # 코스 관리 (21개 화면)
├── e3-live/             # 실시간 세미나 (24개 화면)
├── e4-active/           # 액티브 러닝 (17개 화면)
├── e5-assessment/       # 평가/피드백 (15개 화면)
└── e6-analytics/        # 학습 분석 (9개 화면)
```

---

## 2. 디자인 원칙

### 2.1 핵심 원칙

#### 2.1.1 일관성 (Consistency)
- 동일한 기능은 동일한 UI 패턴 사용
- 색상, 간격, 타이포그래피의 일관된 적용
- 예측 가능한 네비게이션 구조

#### 2.1.2 명확성 (Clarity)
- 명확한 시각적 계층 구조
- 직관적인 레이블과 안내 문구
- 현재 상태와 가능한 액션의 명시적 표시

#### 2.1.3 효율성 (Efficiency)
- 최소한의 클릭으로 목표 달성
- 자주 사용하는 기능의 빠른 접근
- 불필요한 단계 제거

#### 2.1.4 포용성 (Inclusivity)
- WCAG 2.1 AA 수준의 접근성 준수
- 다양한 기기와 환경 지원
- 색각 이상자를 고려한 색상 설계

### 2.2 UX 목표

| 목표 | 측정 지표 | 목표값 |
|------|----------|--------|
| 학습성 | 첫 사용자 태스크 완료 시간 | < 3분 |
| 효율성 | 주요 태스크 클릭 수 | < 5회 |
| 만족도 | SUS (System Usability Scale) | > 80점 |
| 오류율 | 태스크 중 오류 발생률 | < 5% |

---

## 3. 디자인 시스템

### 3.1 색상 체계 (Color System)

#### 3.1.1 시맨틱 색상

```css
/* 기본 색상 */
--background: 0 0% 100%;           /* 배경: #FFFFFF */
--foreground: 222.2 84% 4.9%;      /* 텍스트: #030712 */

/* 브랜드 색상 */
--brand-primary: 221.2 83.2% 53.3%;    /* 주요 액센트: #3B82F6 */
--brand-primary-hover: 224.3 76.3% 48%; /* 호버 상태: #2563EB */
--brand-secondary: 262.1 83.3% 57.8%;   /* 보조 색상: #8B5CF6 */

/* 상태 색상 */
--success: 142.1 76.2% 36.3%;      /* 성공: #22C55E */
--warning: 38 92% 50%;             /* 경고: #F59E0B */
--destructive: 0 84.2% 60.2%;      /* 위험/오류: #EF4444 */
--info: 199 89% 48%;               /* 정보: #0EA5E9 */

/* UI 요소 */
--border: 214.3 31.8% 82%;         /* 테두리: #CBD5E1 */
--muted: 210 40% 96.1%;            /* 비활성 배경: #F1F5F9 */
--muted-foreground: 215.4 16.3% 46.9%; /* 비활성 텍스트: #64748B */
```

#### 3.1.2 다크 모드

```css
.dark {
  --background: 222.2 84% 4.9%;     /* 배경: #030712 */
  --foreground: 210 40% 98%;        /* 텍스트: #F8FAFC */
  --border: 217.2 32.6% 17.5%;      /* 테두리: #1E293B */
  --muted: 217.2 32.6% 17.5%;       /* 비활성 배경: #1E293B */
}
```

### 3.2 타이포그래피 (Typography)

#### 3.2.1 폰트 패밀리

```css
--font-sans: 'Pretendard', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
--font-mono: 'JetBrains Mono', Consolas, Monaco, monospace;
```

#### 3.2.2 폰트 크기 스케일

| 토큰 | 크기 | 용도 |
|------|------|------|
| `--text-xs` | 0.75rem (12px) | 캡션, 힌트 |
| `--text-sm` | 0.875rem (14px) | 본문 보조, 라벨 |
| `--text-base` | 1rem (16px) | 본문 기본 |
| `--text-lg` | 1.125rem (18px) | 소제목, 카드 타이틀 |
| `--text-xl` | 1.25rem (20px) | 섹션 제목 |
| `--text-2xl` | 1.5rem (24px) | 페이지 제목 |
| `--text-3xl` | 1.875rem (30px) | 대제목 |
| `--text-4xl` | 2.25rem (36px) | 히어로 텍스트 |

#### 3.2.3 폰트 웨이트

| 토큰 | 값 | 용도 |
|------|-----|------|
| `--font-normal` | 400 | 본문 기본 |
| `--font-medium` | 500 | 라벨, 버튼 |
| `--font-semibold` | 600 | 제목, 강조 |
| `--font-bold` | 700 | 대제목, 로고 |

### 3.3 간격 체계 (Spacing)

#### 3.3.1 스페이싱 스케일

| 토큰 | 값 | 픽셀 |
|------|-----|------|
| `--spacing-0` | 0 | 0px |
| `--spacing-1` | 0.25rem | 4px |
| `--spacing-2` | 0.5rem | 8px |
| `--spacing-3` | 0.75rem | 12px |
| `--spacing-4` | 1rem | 16px |
| `--spacing-5` | 1.25rem | 20px |
| `--spacing-6` | 1.5rem | 24px |
| `--spacing-8` | 2rem | 32px |
| `--spacing-10` | 2.5rem | 40px |
| `--spacing-12` | 3rem | 48px |
| `--spacing-16` | 4rem | 64px |

### 3.4 모서리 반경 (Border Radius)

| 토큰 | 값 | 용도 |
|------|-----|------|
| `--radius-sm` | 0.25rem | 작은 요소 (체크박스) |
| `--radius-md` | 0.375rem | 기본 요소 (버튼, 입력) |
| `--radius-lg` | 0.5rem | 카드, 모달 |
| `--radius-xl` | 0.75rem | 큰 카드 |
| `--radius-2xl` | 1rem | 특수 요소 |
| `--radius-full` | 9999px | 원형 (아바타, 뱃지) |

### 3.5 그림자 (Shadows)

| 토큰 | 용도 |
|------|------|
| `--shadow-sm` | 미세한 깊이 (호버 상태) |
| `--shadow` | 기본 카드 |
| `--shadow-md` | 드롭다운, 팝오버 |
| `--shadow-lg` | 모달, 토스트 |
| `--shadow-xl` | 포커스된 요소 |

### 3.6 애니메이션 (Transitions)

| 토큰 | 값 | 용도 |
|------|-----|------|
| `--transition-fast` | 150ms | 호버, 포커스 |
| `--transition-normal` | 200ms | 일반 전환 |
| `--transition-slow` | 300ms | 모달, 페이지 전환 |

### 3.7 Z-Index 체계

| 토큰 | 값 | 용도 |
|------|-----|------|
| `--z-dropdown` | 50 | 드롭다운 메뉴 |
| `--z-sticky` | 100 | 고정 헤더 |
| `--z-fixed` | 200 | 고정 요소 |
| `--z-modal-backdrop` | 400 | 모달 배경 |
| `--z-modal` | 500 | 모달 |
| `--z-popover` | 600 | 팝오버 |
| `--z-tooltip` | 700 | 툴팁, 토스트 |

---

## 4. 레이아웃 패턴

### 4.1 전체 레이아웃 구조

#### 4.1.1 메인 애플리케이션 레이아웃

```
┌─────────────────────────────────────────────────────────┐
│                      Header (57px)                       │
│  Logo    │    Navigation    │    Actions    │  User     │
├──────────┼──────────────────────────────────────────────┤
│          │                                              │
│ Sidebar  │                  Main Content                │
│ (280px)  │                                              │
│          │                                              │
│          │                                              │
│          │                                              │
│          │                                              │
│          │                                              │
└──────────┴──────────────────────────────────────────────┘
```

**CSS 구조:**
```css
.sidebar {
  width: 280px;
  position: fixed;
  top: 57px;
  height: calc(100vh - 57px);
}

.main-content {
  margin-left: 280px;
  padding: var(--spacing-6);
}
```

**와이어프레임 참조:**
- 교수 헤더: `components/header-professor.html`
- 학생 헤더: `components/header-student.html`
- 코스 사이드바 (교수): `components/sidebar-course.html`
- 코스 사이드바 (학생): `components/sidebar-course-student.html`

#### 4.1.2 인증 레이아웃 (중앙 정렬)

```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│                                                         │
│               ┌─────────────────────┐                   │
│               │                     │                   │
│               │    Auth Card        │                   │
│               │   (max-w: 420px)    │                   │
│               │                     │                   │
│               └─────────────────────┘                   │
│                                                         │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**CSS 구조:**
```css
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: hsl(var(--muted));
}

.auth-container {
  width: 100%;
  max-width: 420px;
}
```

#### 4.1.3 실시간 세션 레이아웃

```
┌─────────────────────────────────────────────────────────┐
│                   Session Header                         │
│  Session Info  │  Timer  │  Controls  │  End Button     │
├─────────────────────────────────────────┬───────────────┤
│                                         │               │
│                                         │   Sidebar     │
│           Video Grid / Speaker View     │   Panel       │
│                                         │  (Participants│
│                                         │   / Chat)     │
│                                         │               │
├─────────────────────────────────────────┴───────────────┤
│                    Control Bar                          │
│   Mic │ Camera │ Screen │ Hand │ Reactions │ Layout    │
└─────────────────────────────────────────────────────────┘
```

### 4.2 그리드 시스템

#### 4.2.1 기본 그리드

- **컨테이너 최대 너비**: 1280px
- **컬럼 수**: 12
- **거터**: 24px (var(--spacing-6))

#### 4.2.2 카드 그리드

```css
/* 코스 카드 그리드 */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--spacing-6);
}

/* 참가자 그리드 (비디오) */
.participant-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--spacing-4);
}
```

### 4.3 모달 패턴

```css
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-modal-backdrop);
}

.modal {
  background: hsl(var(--card));
  border-radius: var(--radius-lg);
  max-width: 500px;
  width: 90%;
  max-height: 85vh;
  overflow-y: auto;
}
```

**모달 크기 가이드:**

| 유형 | 최대 너비 | 용도 |
|------|----------|------|
| Small | 400px | 확인 다이얼로그, 간단한 입력 |
| Medium | 500px | 폼 입력, 상세 정보 |
| Large | 700px | 복잡한 폼, 미리보기 |
| Full | 90vw | 이미지 뷰어, 전체 화면 모달 |

---

## 5. 컴포넌트 라이브러리

### 5.1 버튼 (Button)

#### 5.1.1 버튼 변형

| 변형 | 클래스 | 용도 |
|------|--------|------|
| Primary | `.btn-primary` | 주요 액션 (저장, 제출) |
| Secondary | `.btn-secondary` | 보조 액션 |
| Outline | `.btn-outline` | 취소, 부가 액션 |
| Ghost | `.btn-ghost` | 아이콘 버튼, 툴바 |
| Destructive | `.btn-destructive` | 삭제, 위험 액션 |
| Link | `.btn-link` | 텍스트 링크 스타일 |

#### 5.1.2 버튼 크기

| 크기 | 클래스 | 패딩 |
|------|--------|------|
| Small | `.btn-sm` | 4px 12px |
| Default | `.btn` | 8px 16px |
| Large | `.btn-lg` | 12px 24px |
| Icon | `.btn-icon` | 8px (36x36) |

### 5.2 입력 필드 (Input)

#### 5.2.1 입력 상태

| 상태 | 클래스 | 설명 |
|------|--------|------|
| Default | `.input` | 기본 상태 |
| Focus | `:focus` | 포커스 상태 (파란 테두리 + 그림자) |
| Error | `.input-error` | 유효성 오류 (빨간 테두리) |
| Disabled | `:disabled` | 비활성화 |

#### 5.2.2 입력 변형

- **기본 입력**: 텍스트, 이메일, 패스워드
- **아이콘 포함**: `.input-group` + `.input-icon`
- **애드온 포함**: `.input-addon` (버튼, 아이콘)
- **텍스트 영역**: `<textarea class="input">`
- **셀렉트**: `.select`

### 5.3 카드 (Card)

```html
<div class="card">
  <div class="card-header">
    <h3 class="card-title">제목</h3>
    <p class="card-description">설명</p>
  </div>
  <div class="card-content">
    <!-- 콘텐츠 -->
  </div>
  <div class="card-footer">
    <!-- 액션 버튼 -->
  </div>
</div>
```

### 5.4 뱃지 (Badge)

| 변형 | 클래스 | 용도 |
|------|--------|------|
| Default | `.badge-default` | 기본 태그 |
| Primary | `.badge-primary` | 강조 태그 |
| Success | `.badge-success` | 성공, 완료 |
| Warning | `.badge-warning` | 경고, 주의 |
| Destructive | `.badge-destructive` | 오류, 긴급 |
| Outline | `.badge-outline` | 테두리만 |

### 5.5 아바타 (Avatar)

| 크기 | 클래스 | 픽셀 |
|------|--------|------|
| Small | `.avatar-sm` | 32x32 |
| Medium | `.avatar-md` | 40x40 |
| Large | `.avatar-lg` | 56x56 |
| Extra Large | `.avatar-xl` | 80x80 |

### 5.6 알림 (Alert)

| 변형 | 클래스 | 용도 |
|------|--------|------|
| Default | `.alert-default` | 일반 정보 |
| Success | `.alert-success` | 성공 메시지 |
| Warning | `.alert-warning` | 경고 메시지 |
| Destructive | `.alert-destructive` | 오류 메시지 |
| Info | `.alert-info` | 안내 메시지 |

### 5.7 테이블 (Table)

```html
<div class="table-wrapper">
  <table class="table">
    <thead>
      <tr>
        <th>헤더</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>데이터</td>
      </tr>
    </tbody>
  </table>
</div>
```

### 5.8 탭 (Tabs)

```html
<div class="tabs">
  <div class="tabs-list">
    <button class="tab-trigger active">탭 1</button>
    <button class="tab-trigger">탭 2</button>
  </div>
  <div class="tab-content">
    <!-- 탭 콘텐츠 -->
  </div>
</div>
```

### 5.9 프로그레스 바 (Progress)

```html
<div class="progress">
  <div class="progress-bar" style="width: 60%"></div>
</div>
```

### 5.10 아이콘 (Lucide Icons)

#### 사용법

```html
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>

<i data-lucide="icon-name" class="icon-size"></i>

<script>lucide.createIcons();</script>
```

#### 아이콘 크기

| 클래스 | 크기 |
|--------|------|
| `.icon-xs` | 12px |
| `.icon-sm` | 16px |
| `.icon-md` | 20px |
| `.icon-lg` | 24px |
| `.icon-xl` | 32px |

#### 주요 아이콘 목록

| 카테고리 | 아이콘 | 용도 |
|---------|--------|------|
| 네비게이션 | `home`, `menu`, `arrow-left`, `chevron-down` | 메뉴, 이동 |
| 액션 | `plus`, `edit`, `trash-2`, `download`, `upload` | CRUD |
| 상태 | `check`, `x`, `alert-triangle`, `info` | 피드백 |
| 미디어 | `video`, `mic`, `mic-off`, `screen-share` | 화상 회의 |
| 사용자 | `user`, `users`, `user-plus`, `log-out` | 사용자 관련 |
| 교육 | `book-open`, `graduation-cap`, `clipboard-list` | 학습 |

---

## 6. 화면 설계 - E1: 사용자 인증

### 6.1 화면 목록

| ID | 화면명 | 파일 | 설명 |
|----|--------|------|------|
| AUTH-001 | 회원가입 | `e1-auth/auth-001-register.html` | 신규 사용자 등록 폼 |
| AUTH-002 | 이메일 인증 대기 | `e1-auth/auth-002-email-verify.html` | 인증 메일 발송 안내 |
| AUTH-003 | 이메일 인증 완료 | `e1-auth/auth-003-email-complete.html` | 인증 성공 메시지 |
| AUTH-004 | 로그인 | `e1-auth/auth-004-login.html` | 로그인 폼 |
| AUTH-005 | 로그인 오류 | `e1-auth/auth-005-login-error.html` | 로그인 실패 상태 |
| AUTH-006 | 계정 잠금 | `e1-auth/auth-006-account-locked.html` | 5회 실패 후 잠금 안내 |
| AUTH-007 | OAuth 연동 | `e1-auth/auth-007-oauth-link.html` | 소셜 계정 연결 |
| AUTH-008 | 2FA 설정 시작 | `e1-auth/auth-008-2fa-setup.html` | 2단계 인증 설정 안내 |
| AUTH-009 | 2FA QR 코드 | `e1-auth/auth-009-2fa-qr.html` | Authenticator 앱 연결 |
| AUTH-010 | 2FA 백업 코드 | `e1-auth/auth-010-2fa-backup.html` | 백업 코드 표시 |
| AUTH-011 | 2FA 인증 | `e1-auth/auth-011-2fa-verify.html` | OTP 입력 화면 |
| AUTH-012 | 비밀번호 찾기 | `e1-auth/auth-012-forgot-password.html` | 재설정 이메일 요청 |
| AUTH-013 | 재설정 메일 발송 | `e1-auth/auth-013-password-sent.html` | 발송 완료 안내 |
| AUTH-014 | 비밀번호 재설정 | `e1-auth/auth-014-reset-password.html` | 새 비밀번호 입력 |
| PROFILE-001 | 프로필 설정 (교수) | `e1-auth/profile-001-settings.html` | 교수 프로필 관리 |
| PROFILE-001-S | 프로필 설정 (학생) | `e1-auth/profile-001-settings-student.html` | 학생 프로필 관리 |
| PROFILE-002 | 프로필 사진 변경 | `e1-auth/profile-002-photo-modal.html` | 사진 업로드 모달 |
| PROFILE-003 | 비밀번호 변경 | `e1-auth/profile-003-change-password.html` | 비밀번호 변경 모달 |
| ADMIN-001 | 역할 관리 | `e1-auth/admin-001-role-management.html` | 사용자 역할 관리 테이블 |
| ADMIN-002 | 역할 편집 | `e1-auth/admin-002-role-modal.html` | 역할 변경 모달 |
| ERROR-001 | 접근 권한 없음 | `e1-auth/error-001-forbidden.html` | 403 오류 페이지 |

### 6.2 주요 화면 설계

#### 6.2.1 회원가입 (AUTH-001)

**레이아웃**: 인증 레이아웃 (중앙 정렬)

**구성 요소:**
- 로고 및 타이틀
- 이메일 입력 필드
- 비밀번호 입력 필드 (강도 표시기 포함)
- 비밀번호 확인 필드
- 이용약관 동의 체크박스
- 회원가입 버튼 (Primary)
- OAuth 버튼 (Google, Microsoft)
- 로그인 페이지 링크

**유효성 검사:**
- 이메일: 형식 검증, 중복 확인
- 비밀번호: 최소 8자, 대소문자/숫자/특수문자 조합
- 실시간 피드백 제공

**와이어프레임**: `e1-auth/auth-001-register.html`

#### 6.2.2 로그인 (AUTH-004)

**레이아웃**: 인증 레이아웃 (중앙 정렬)

**구성 요소:**
- 로고 및 타이틀
- 이메일 입력 필드
- 비밀번호 입력 필드 (표시/숨김 토글)
- 로그인 유지 체크박스
- 로그인 버튼 (Primary)
- 비밀번호 찾기 링크
- OAuth 버튼
- 회원가입 링크

**오류 상태:**
- 잘못된 자격 증명: 인라인 오류 메시지
- 5회 실패: 계정 잠금 페이지로 이동

**와이어프레임**: `e1-auth/auth-004-login.html`

#### 6.2.3 2FA 인증 (AUTH-011)

**레이아웃**: 인증 레이아웃 (중앙 정렬)

**구성 요소:**
- 타이틀 ("2단계 인증")
- 설명 텍스트
- OTP 입력 필드 (6자리, 자동 포커스 이동)
- 인증 버튼
- 백업 코드로 인증 링크
- 재전송/다시 시도 링크

**와이어프레임**: `e1-auth/auth-011-2fa-verify.html`

#### 6.2.4 프로필 설정 (PROFILE-001)

**레이아웃**: 사이드바 + 메인 콘텐츠

**구성 요소:**
- 프로필 사진 (클릭하여 변경)
- 기본 정보 폼
  - 이름
  - 이메일 (읽기 전용)
  - 전화번호
  - 소속 학과
- 보안 설정 섹션
  - 비밀번호 변경 버튼
  - 2FA 활성화/비활성화 토글
  - 연결된 OAuth 계정 목록
- 알림 설정
- 저장 버튼

**와이어프레임**:
- 교수: `e1-auth/profile-001-settings.html`
- 학생: `e1-auth/profile-001-settings-student.html`

### 6.3 사용자 플로우

#### 6.3.1 회원가입 플로우

```
[회원가입] → [이메일 인증 대기] → [이메일 링크 클릭] → [이메일 인증 완료] → [로그인]
     ↓
[OAuth 선택] → [OAuth 제공자] → [계정 연결] → [프로필 완성]
```

#### 6.3.2 로그인 플로우

```
[로그인] → [2FA 활성화?] → Yes → [2FA 인증] → [대시보드]
              ↓ No
        [대시보드]
```

#### 6.3.3 비밀번호 재설정 플로우

```
[비밀번호 찾기] → [이메일 입력] → [재설정 메일 발송] → [메일 링크 클릭] → [새 비밀번호 설정] → [로그인]
```

---

## 7. 화면 설계 - E2: 코스 관리

### 7.1 화면 목록

| ID | 화면명 | 파일 | 설명 |
|----|--------|------|------|
| DASH-001 | 대시보드 (교수) | `e2-course/dashboard.html` | 교수 메인 대시보드 |
| DASH-001-S | 대시보드 (학생) | `e2-course/dashboard-student.html` | 학생 메인 대시보드 |
| CRS-001 | 코스 목록 (교수) | `e2-course/crs-001-course-list.html` | 담당 코스 목록 |
| CRS-001-S | 코스 목록 (학생) | `e2-course/crs-001-course-list-student.html` | 수강 코스 목록 |
| CRS-002 | 코스 생성 | `e2-course/crs-002-create-course.html` | 새 코스 생성 폼 |
| CRS-003 | 코스 상세 (교수) | `e2-course/crs-003-course-detail.html` | 코스 홈 페이지 (교수) |
| CRS-003-S | 코스 상세 (학생) | `e2-course/crs-003-course-detail-student.html` | 코스 홈 페이지 (학생) |
| CRS-004 | 수강생 관리 | `e2-course/crs-004-students.html` | 수강생 목록/관리 |
| CRS-005 | 코스 설정 | `e2-course/crs-005-course-settings.html` | 코스 설정 페이지 |
| CRS-008 | 초대 수락 | `e2-course/crs-008-invite-accept.html` | 코스 초대 수락 |
| SES-001 | 세션 목록 | `e2-course/ses-001-session-list.html` | 코스 세션 일정 |
| SES-002 | 세션 생성 | `e2-course/ses-002-create-session.html` | 새 세션 생성 모달 |
| CNT-001 | 콘텐츠 라이브러리 | `e2-course/cnt-001-content-library.html` | 강의 자료 관리 |
| CNT-002 | 파일 업로드 | `e2-course/cnt-002-upload.html` | 파일 업로드 모달 |
| ASG-001 | 과제 목록 | `e2-course/asg-001-assignment-list.html` | 과제 목록 |
| ASG-002 | 과제 생성 | `e2-course/asg-002-create-assignment.html` | 새 과제 생성 |
| ASG-003 | 제출 현황 | `e2-course/asg-003-submissions.html` | 과제 제출 현황 |
| ASG-004 | 채점 | `e2-course/asg-004-grading.html` | 과제 채점 화면 |
| ASG-005 | 과제 제출 (학생) | `e2-course/asg-005-submit.html` | 학생 과제 제출 |
| GRD-001 | 성적 대시보드 | `e2-course/grd-001-grade-dashboard.html` | 코스 전체 성적 현황 |
| GRD-002 | 학생 성적 상세 | `e2-course/grd-002-student-detail.html` | 개별 학생 성적 |
| GRD-003 | 내 성적 (학생) | `e2-course/grd-003-student-view.html` | 학생 본인 성적 확인 |

### 7.2 주요 화면 설계

#### 7.2.1 대시보드 (DASH-001)

**레이아웃**: 헤더 + 사이드바 없음 (전체 너비)

**구성 요소:**
- 인사 메시지 ("안녕하세요, [이름] 교수님")
- 오늘의 일정 카드
  - 예정된 세션 목록
  - 빠른 세션 시작 버튼
- 코스 카드 그리드
  - 코스 썸네일
  - 코스명, 학기
  - 수강생 수
  - 다음 세션 일시
  - 빠른 액션 버튼
- 최근 활동 피드
- 알림 패널

**와이어프레임**:
- 교수: `e2-course/dashboard.html`
- 학생: `e2-course/dashboard-student.html`

#### 7.2.2 코스 상세 (CRS-003)

**레이아웃**: 헤더 + 코스 사이드바 + 메인 콘텐츠

**사이드바 메뉴:**
- 코스 홈
- 세션 일정
- 콘텐츠 라이브러리
- 과제
- 성적
- 분석 (교수만)
- 설정 (교수만)

**메인 콘텐츠:**
- 코스 정보 헤더
  - 코스명, 학기, 교수명
  - 수강생 수
- 공지사항 섹션
- 다가오는 세션 카드
- 최근 자료 목록
- 최근 과제 목록

**와이어프레임**:
- 교수: `e2-course/crs-003-course-detail.html`
- 학생: `e2-course/crs-003-course-detail-student.html`

#### 7.2.3 세션 생성 (SES-002)

**레이아웃**: 모달 (Medium)

**구성 요소:**
- 세션 제목 입력
- 날짜/시간 선택
- 세션 유형 선택 (실시간, 녹화, 혼합)
- 설명 텍스트 영역
- 반복 설정 (선택)
- 자료 첨부 (선택)
- 취소/생성 버튼

**와이어프레임**: `e2-course/ses-002-create-session.html`

#### 7.2.4 과제 채점 (ASG-004)

**레이아웃**: 분할 화면 (제출물 | 채점 패널)

**왼쪽 패널 (제출물 뷰어):**
- 파일 미리보기
- 다운로드 버튼
- 제출 정보 (제출자, 시간)

**오른쪽 패널 (채점):**
- 점수 입력
- 루브릭 체크리스트
- 피드백 텍스트 영역
- AI 채점 결과 (활성화 시)
- 저장/다음 학생 버튼

**와이어프레임**: `e2-course/asg-004-grading.html`

### 7.3 사용자 플로우

#### 7.3.1 코스 생성 플로우

```
[대시보드] → [코스 생성 클릭] → [코스 정보 입력] → [코스 생성 완료] → [코스 상세]
                                                           ↓
                                               [수강생 초대 (선택)]
```

#### 7.3.2 과제 생성 및 채점 플로우

```
[과제 목록] → [과제 생성] → [과제 정보 입력] → [과제 공개]
                                                  ↓
                                         [학생 제출 대기]
                                                  ↓
[제출 현황 확인] → [채점 시작] → [피드백 작성] → [성적 공개]
```

---

## 8. 화면 설계 - E3: 실시간 세미나

### 8.1 화면 목록

| ID | 화면명 | 파일 | 설명 |
|----|--------|------|------|
| LIVE-001 | 세션 준비 | `e3-live/live-001-session-prep.html` | 교수 세션 시작 전 설정 |
| LIVE-002 | 세션 참여 (학생) | `e3-live/live-002-student-join.html` | 학생 세션 입장 |
| LIVE-003 | 대기실 | `e3-live/live-003-waiting-room.html` | 세션 대기실 |
| LIVE-004 | 대기실 관리 | `e3-live/live-004-waiting-manage.html` | 교수 대기실 관리 |
| LIVE-004-S | 학생 뷰 (대기) | `e3-live/live-004-student-view.html` | 학생 대기 화면 |
| LIVE-005 | 교수 화면 | `e3-live/live-005-professor-view.html` | 교수 메인 세션 화면 |
| LIVE-006 | 학생 화면 | `e3-live/live-006-student-view.html` | 학생 메인 세션 화면 |
| LIVE-007 | 화면 공유 선택 | `e3-live/live-007-screen-share-select.html` | 공유 대상 선택 모달 |
| LIVE-008 | 화면 공유 중 | `e3-live/live-008-screen-sharing.html` | 화면 공유 상태 |
| LIVE-009 | 채팅 패널 | `e3-live/live-009-chat-panel.html` | 채팅 사이드 패널 |
| LIVE-010 | 1:1 메시지 | `e3-live/live-010-direct-message.html` | 개인 메시지 |
| LIVE-011 | 파일 공유 | `e3-live/live-011-file-share.html` | 파일 공유 모달 |
| LIVE-012 | 손들기 대기열 | `e3-live/live-012-hand-queue.html` | 손든 학생 목록 |
| LIVE-013 | 반응 선택 | `e3-live/live-013-reactions.html` | 이모지 반응 패널 |
| LIVE-014 | 반응 표시 | `e3-live/live-014-reaction-display.html` | 화면 반응 애니메이션 |
| LIVE-015 | 녹화 컨트롤 | `e3-live/live-015-recording-control.html` | 녹화 시작/중지 |
| LIVE-016 | 녹화 재생 | `e3-live/live-016-recording-playback.html` | 녹화 영상 재생 |
| LIVE-017 | 그리드 뷰 | `e3-live/live-017-grid-view.html` | 참가자 그리드 레이아웃 |
| LIVE-018 | 스피커 뷰 | `e3-live/live-018-speaker-view.html` | 발표자 중심 레이아웃 |
| LIVE-019 | 레이아웃 선택 | `e3-live/live-019-layout-dropdown.html` | 레이아웃 변경 드롭다운 |
| LIVE-020 | 연결 품질 | `e3-live/live-020-connection-quality.html` | 네트워크 상태 표시 |
| LIVE-021 | 네트워크 경고 | `e3-live/live-021-network-warning.html` | 불안정 연결 알림 |
| LIVE-022 | 세션 종료 확인 | `e3-live/live-022-end-session.html` | 종료 확인 모달 |
| LIVE-023 | 세션 종료됨 | `e3-live/live-023-session-ended.html` | 세션 종료 안내 |

### 8.2 주요 화면 설계

#### 8.2.1 교수 메인 세션 화면 (LIVE-005)

**레이아웃**: 전체 화면 (몰입형)

**상단 바:**
- 세션 정보 (코스명, 세션 제목)
- 타이머 (경과 시간)
- 참가자 수
- 연결 품질 아이콘
- 세션 종료 버튼

**메인 영역:**
- 비디오 그리드 / 스피커 뷰 (전환 가능)
- 화면 공유 시 공유 콘텐츠 중심
- 참가자 비디오 썸네일

**사이드바 (토글):**
- 참가자 목록 탭
- 채팅 탭
- Q&A 탭

**하단 컨트롤 바:**
- 마이크 켜기/끄기
- 카메라 켜기/끄기
- 화면 공유
- 손들기 대기열
- 반응
- 액티브 러닝 도구 (투표, 퀴즈, 분반)
- 녹화
- 레이아웃 변경
- 더보기 메뉴

**와이어프레임**: `e3-live/live-005-professor-view.html`

#### 8.2.2 학생 메인 세션 화면 (LIVE-006)

**레이아웃**: 전체 화면 (몰입형)

**상단 바:**
- 세션 정보
- 타이머
- 연결 품질

**메인 영역:**
- 비디오 그리드 / 스피커 뷰
- 화면 공유 콘텐츠

**사이드바 (토글):**
- 참가자 목록
- 채팅

**하단 컨트롤 바:**
- 마이크 켜기/끄기
- 카메라 켜기/끄기
- 손들기
- 반응
- 레이아웃 변경
- 세션 나가기

**와이어프레임**: `e3-live/live-006-student-view.html`

#### 8.2.3 채팅 패널 (LIVE-009)

**레이아웃**: 사이드 패널 (320px)

**구성 요소:**
- 탭: 전체 채팅 | 1:1 메시지
- 메시지 목록
  - 발신자 아바타, 이름
  - 메시지 내용
  - 타임스탬프
- 파일 첨부 버튼
- 메시지 입력 필드
- 전송 버튼

**와이어프레임**: `e3-live/live-009-chat-panel.html`

#### 8.2.4 녹화 재생 (LIVE-016)

**레이아웃**: 비디오 플레이어 + 사이드 정보

**구성 요소:**
- 비디오 플레이어
  - 재생/일시정지
  - 진행 바
  - 속도 조절
  - 전체 화면
  - 챕터 마커
- 세션 정보 패널
  - 제목, 날짜
  - 재생 시간
  - 참가자 목록
- 타임라인 하이라이트
  - 투표 시점
  - 화면 공유 시점
  - 주요 발언 시점

**와이어프레임**: `e3-live/live-016-recording-playback.html`

### 8.3 사용자 플로우

#### 8.3.1 세션 시작 플로우 (교수)

```
[세션 목록] → [세션 시작 클릭] → [세션 준비 화면]
                                      ↓
                              [장비 점검]
                                      ↓
                              [세션 시작]
                                      ↓
                              [대기실에서 학생 입장 허용]
                                      ↓
                              [메인 세션 화면]
```

#### 8.3.2 세션 참여 플로우 (학생)

```
[코스 상세] → [세션 입장 클릭] → [세션 참여 화면]
                                      ↓
                              [장비 점검]
                                      ↓
                              [입장 요청]
                                      ↓
                              [대기실에서 승인 대기]
                                      ↓
                              [메인 세션 화면]
```

---

## 9. 화면 설계 - E4: 액티브 러닝 도구

### 9.1 화면 목록

| ID | 화면명 | 파일 | 설명 |
|----|--------|------|------|
| POLL-001 | 투표 생성 | `e4-active/poll-001-create-poll.html` | 실시간 투표 생성 |
| POLL-002 | 투표 템플릿 | `e4-active/poll-002-templates.html` | 투표 템플릿 선택 |
| POLL-003 | 투표 참여 | `e4-active/poll-003-participate.html` | 학생 투표 참여 |
| POLL-004 | 투표 결과 | `e4-active/poll-004-results.html` | 투표 결과 차트 |
| POLL-005 | 워드 클라우드 | `e4-active/poll-005-wordcloud.html` | 텍스트 응답 시각화 |
| QUIZ-001 | 문제 은행 | `e4-active/quiz-001-question-bank.html` | 퀴즈 문제 관리 |
| QUIZ-002 | 문제 편집 | `e4-active/quiz-002-edit-question.html` | 문제 생성/수정 |
| QUIZ-003 | 퀴즈 풀기 | `e4-active/quiz-003-taking.html` | 학생 퀴즈 풀기 |
| QUIZ-004 | 퀴즈 결과 | `e4-active/quiz-004-results.html` | 퀴즈 결과 및 해설 |
| BRK-001 | 분반 설정 | `e4-active/brk-001-setup.html` | 분반 구성 모달 |
| BRK-002 | 수동 배정 | `e4-active/brk-002-manual-assign.html` | 학생 수동 배정 |
| BRK-003 | 분반 모니터링 | `e4-active/brk-003-monitor.html` | 교수 분반 모니터링 |
| BRK-004 | 분반 참여 | `e4-active/brk-004-student-view.html` | 학생 분반 토론 |
| WB-001 | 화이트보드 | `e4-active/wb-001-whiteboard.html` | 협업 화이트보드 |
| SOC-001 | 토론 게시판 | `e4-active/soc-001-discussion.html` | 소크라틱 토론 |
| SOC-002 | 지명 발언 | `e4-active/soc-002-nominate.html` | 지명 발언 모달 |

### 9.2 주요 화면 설계

#### 9.2.1 투표 생성 (POLL-001)

**레이아웃**: 모달 (Medium)

**구성 요소:**
- 질문 입력 필드
- 투표 유형 선택
  - 객관식 (단일/다중)
  - 예/아니오
  - 척도 (1-5, 1-10)
  - 텍스트 입력
- 선택지 입력 (동적 추가/삭제)
- 설정 옵션
  - 익명 투표
  - 결과 실시간 표시
  - 제한 시간
- 템플릿 저장 체크박스
- 취소/시작 버튼

**와이어프레임**: `e4-active/poll-001-create-poll.html`

#### 9.2.2 퀴즈 풀기 (QUIZ-003)

**레이아웃**: 전체 화면 (몰입형)

**상단 바:**
- 퀴즈 제목
- 진행 상황 (문제 번호/총 문제)
- 남은 시간 타이머
- 나가기 버튼

**메인 영역:**
- 문제 텍스트
- 선택지 목록 (라디오/체크박스)
- 이미지/코드 블록 (선택)

**하단 바:**
- 이전/다음 버튼
- 문제 네비게이션 (번호 클릭)
- 제출 버튼

**와이어프레임**: `e4-active/quiz-003-taking.html`

#### 9.2.3 분반 설정 (BRK-001)

**레이아웃**: 모달 (Medium)

**구성 요소:**
- 분반 수 설정 (숫자 입력)
- 배정 방식 선택
  - 자동 (랜덤)
  - 자동 (균등 분배)
  - 자동 (성적 기반 혼합)
  - 수동 배정
- 미리보기 그리드
  - 각 분반별 학생 목록
- 설정 옵션
  - 토론 시간 설정
  - 타이머 표시
  - 1분 전 알림
  - 자동 복귀
- 토론 주제 입력 (선택)
- 취소/재배정/시작 버튼

**와이어프레임**: `e4-active/brk-001-setup.html`

#### 9.2.4 화이트보드 (WB-001)

**레이아웃**: 전체 화면 (도구 오버레이)

**상단 툴바:**
- 돌아가기 버튼
- 보드 이름
- 공유/내보내기 버튼

**왼쪽 도구 패널:**
- 선택 도구
- 펜 도구 (색상, 굵기)
- 형광펜
- 지우개
- 텍스트 도구
- 도형 (사각형, 원, 화살표)
- 스티커/이모지
- 이미지 삽입

**메인 캔버스:**
- 무한 캔버스
- 줌 컨트롤
- 미니맵

**오른쪽 패널:**
- 참가자 커서 표시
- 레이어 관리 (선택)

**와이어프레임**: `e4-active/wb-001-whiteboard.html`

### 9.3 사용자 플로우

#### 9.3.1 실시간 투표 플로우

```
[세션 진행 중] → [투표 시작 클릭] → [투표 생성]
                                        ↓
                               [학생에게 투표 표시]
                                        ↓
                               [학생 응답 수집]
                                        ↓
                               [결과 표시/공유]
```

#### 9.3.2 분반 토론 플로우

```
[세션 진행 중] → [분반 시작] → [분반 설정] → [분반 생성]
                                                 ↓
                                         [학생 자동 이동]
                                                 ↓
[교수: 분반 모니터링] ↔ [학생: 분반 토론]
                                                 ↓
                                         [타이머 종료]
                                                 ↓
                                         [전체 세션 복귀]
```

---

## 10. 화면 설계 - E5: 평가 및 피드백

### 10.1 화면 목록

| ID | 화면명 | 파일 | 설명 |
|----|--------|------|------|
| GRADE-001 | 퀴즈 결과 상세 | `e5-assessment/grade-001-quiz-result.html` | 퀴즈 채점 결과 |
| GRADE-002 | 성적 통계 | `e5-assessment/grade-002-statistics.html` | 코스 성적 통계 |
| AI-GRADE-001 | AI 채점 검토 | `e5-assessment/ai-grade-001-review.html` | AI 채점 결과 검토 |
| PEER-001 | 동료 평가 설정 | `e5-assessment/peer-001-setup.html` | 동료 평가 생성 |
| PEER-001-E | 동료 평가 진행 | `e5-assessment/peer-001-evaluate.html` | 교수 평가 화면 |
| PEER-001-S | 동료 평가 (학생) | `e5-assessment/peer-001-evaluate-student.html` | 학생 평가 참여 |
| PEER-002 | 동료 평가 결과 | `e5-assessment/peer-002-result.html` | 평가 결과 조회 |
| CODE-001 | 코드 제출 | `e5-assessment/code-001-submit.html` | 코드 과제 제출 |
| CODE-002 | 코드 평가 결과 | `e5-assessment/code-002-result.html` | 자동 채점 결과 |
| CODE-003 | 표절 검사 결과 | `e5-assessment/code-003-plagiarism.html` | 표절 검사 리포트 |
| ENGAGE-001 | 참여도 설정 | `e5-assessment/engage-001-settings.html` | 참여도 평가 기준 |
| ENGAGE-002 | 참여도 대시보드 | `e5-assessment/engage-002-dashboard.html` | 참여도 현황 |
| FEEDBACK-001 | 즉시 피드백 | `e5-assessment/feedback-001-instant.html` | 실시간 피드백 알림 |

### 10.2 주요 화면 설계

#### 10.2.1 AI 채점 검토 (AI-GRADE-001)

**레이아웃**: 분할 화면 (제출물 | AI 분석)

**왼쪽 패널 (제출물):**
- 학생 정보
- 제출 파일 미리보기
- 원문 텍스트

**오른쪽 패널 (AI 분석):**
- AI 채점 점수
- 채점 근거 목록
  - 항목별 점수
  - 강점/개선점
- 유사 제출물 비교 (선택)
- 교수 수정 영역
  - 점수 조정 입력
  - 추가 피드백
- 승인/수정/거부 버튼

**와이어프레임**: `e5-assessment/ai-grade-001-review.html`

#### 10.2.2 동료 평가 (PEER-001-S)

**레이아웃**: 카드 형태 평가 폼

**구성 요소:**
- 평가 대상 정보 (익명 처리)
- 제출물 미리보기
- 루브릭 기반 평가
  - 각 항목별 점수 선택
  - 항목 설명 툴팁
- 서술형 피드백 영역
- 진행 상황 표시기
- 이전/다음 평가 버튼
- 제출 버튼

**와이어프레임**: `e5-assessment/peer-001-evaluate-student.html`

#### 10.2.3 코드 평가 결과 (CODE-002)

**레이아웃**: 코드 뷰어 + 결과 패널

**코드 뷰어:**
- 구문 강조
- 라인 번호
- 오류 위치 표시

**결과 패널:**
- 전체 점수
- 테스트 케이스 결과
  - 통과/실패 목록
  - 입력/예상 출력/실제 출력
- 코드 품질 분석
  - 코드 스타일
  - 복잡도
  - 메모리 사용량
- 제안 사항

**와이어프레임**: `e5-assessment/code-002-result.html`

#### 10.2.4 표절 검사 결과 (CODE-003)

**레이아웃**: 비교 뷰어

**상단 요약:**
- 유사도 점수 (게이지)
- 위험 수준 뱃지
- 검사 시간

**메인 영역:**
- 코드 비교 뷰 (2열)
  - 제출 코드 | 유사 코드
  - 유사 구간 하이라이트
- 유사 제출물 목록
  - 학생명 (마스킹)
  - 유사도 퍼센트
  - 클릭하여 비교

**와이어프레임**: `e5-assessment/code-003-plagiarism.html`

### 10.3 사용자 플로우

#### 10.3.1 AI 채점 플로우

```
[과제 마감] → [AI 자동 채점] → [AI 채점 완료 알림]
                                       ↓
                              [교수 검토 페이지]
                                       ↓
                    [승인] ← [검토/수정] → [거부 (재채점)]
                       ↓
               [성적 확정 및 공개]
```

#### 10.3.2 동료 평가 플로우

```
[과제 마감] → [동료 평가 설정] → [평가자 배정]
                                       ↓
                              [평가 기간 시작]
                                       ↓
                    [학생: 할당된 제출물 평가]
                                       ↓
                              [평가 기간 종료]
                                       ↓
                    [교수: 결과 검토] → [성적 반영]
```

---

## 11. 화면 설계 - E6: 학습 분석

### 11.1 화면 목록

| ID | 화면명 | 파일 | 설명 |
|----|--------|------|------|
| ANALYTICS-001 | 실시간 분석 | `e6-analytics/analytics-001-realtime.html` | 세션 중 실시간 분석 |
| ANALYTICS-LIST | 분석 목록 | `e6-analytics/analytics-list.html` | 분석 대시보드 목록 |
| ALERT-001 | 알림 대시보드 | `e6-analytics/alert-001-dashboard.html` | 위험 학생 알림 |
| ALERT-002 | 학생 추적 | `e6-analytics/alert-002-tracking.html` | 개별 학생 추적 |
| NETWORK-001 | 네트워크 시각화 | `e6-analytics/network-001-visualization.html` | 학생 관계 네트워크 |
| NETWORK-002 | 개인 네트워크 | `e6-analytics/network-002-individual.html` | 개인 상호작용 분석 |
| REPORT-001 | 개인 리포트 | `e6-analytics/report-001-personal.html` | 학생 개인 리포트 |
| REPORT-002 | 코스 리포트 | `e6-analytics/report-002-course.html` | 코스 전체 리포트 |

### 11.2 주요 화면 설계

#### 11.2.1 실시간 분석 (ANALYTICS-001)

**레이아웃**: 대시보드 그리드

**상단 KPI 카드:**
- 현재 참여율 (%)
- 평균 집중도 점수
- 활성 참가자 수
- 질문/응답 수

**메인 차트:**
- 참여도 타임라인 (실시간 업데이트)
- 집중도 히트맵

**참가자 현황:**
- 상태별 참가자 목록
  - 활성 (녹색)
  - 보통 (노란색)
  - 비활성 (빨간색)

**알림 패널:**
- 실시간 알림
- 개입 제안

**와이어프레임**: `e6-analytics/analytics-001-realtime.html`

#### 11.2.2 위험 학생 대시보드 (ALERT-001)

**레이아웃**: 테이블 + 필터

**필터 영역:**
- 위험 수준 필터
- 기간 선택
- 검색

**테이블:**
- 학생 이름/사진
- 위험 수준 뱃지 (높음/중간/낮음)
- 출석률
- 과제 제출률
- 참여도 점수
- 마지막 활동
- 액션 버튼 (연락, 상세)

**통계 요약:**
- 위험 수준별 학생 수
- 주간 변화 추이

**와이어프레임**: `e6-analytics/alert-001-dashboard.html`

#### 11.2.3 네트워크 시각화 (NETWORK-001)

**레이아웃**: 전체 화면 시각화

**도구 바:**
- 줌 컨트롤
- 필터 (기간, 상호작용 유형)
- 검색

**시각화 영역:**
- 노드-엣지 네트워크 그래프
  - 노드: 학생 (크기 = 상호작용 빈도)
  - 엣지: 상호작용 (두께 = 강도)
- 클러스터 표시
- 호버 시 상세 정보

**사이드 패널:**
- 선택된 학생 정보
- 상호작용 목록
- 통계 요약

**와이어프레임**: `e6-analytics/network-001-visualization.html`

#### 11.2.4 코스 리포트 (REPORT-002)

**레이아웃**: 프린트 가능한 리포트 형식

**구성 요소:**
- 리포트 헤더
  - 코스 정보
  - 기간
  - 생성 일시
- 요약 섹션
  - 전체 출석률
  - 과제 완료율
  - 평균 점수
- 참여도 분석
  - 세션별 참여도 차트
  - 학생별 참여 순위
- 성적 분포
  - 등급별 분포 차트
  - 항목별 평균
- 액티브 러닝 효과
  - 퀴즈/투표 분석
  - 토론 참여 분석
- 위험 학생 목록
- 개선 제안

**액션:**
- PDF 내보내기
- 인쇄

**와이어프레임**: `e6-analytics/report-002-course.html`

### 11.3 사용자 플로우

#### 11.3.1 위험 학생 관리 플로우

```
[알림 수신] → [알림 대시보드] → [학생 선택]
                                      ↓
                            [학생 상세 분석]
                                      ↓
              [이메일 발송] ← [개입 결정] → [상담 예약]
                                      ↓
                              [추적 기록 저장]
```

---

## 12. 인터랙션 패턴

### 12.1 피드백 패턴

#### 12.1.1 로딩 상태

| 유형 | 사용 상황 | 구현 |
|------|----------|------|
| 버튼 스피너 | 폼 제출 | 버튼 내 스피너 + 텍스트 변경 |
| 스켈레톤 | 콘텐츠 로딩 | `.skeleton` 클래스 |
| 프로그레스 바 | 파일 업로드 | `.progress` 컴포넌트 |
| 전체 화면 로더 | 페이지 전환 | 오버레이 + 스피너 |

#### 12.1.2 성공/오류 피드백

| 유형 | 사용 상황 | 구현 |
|------|----------|------|
| 토스트 | 일반 알림 | `.toast` 컴포넌트 (자동 닫힘) |
| 인라인 메시지 | 폼 유효성 | `.form-error` 클래스 |
| 알림 배너 | 중요 정보 | `.alert` 컴포넌트 |
| 모달 | 확인 필요 | `.modal` 컴포넌트 |

### 12.2 네비게이션 패턴

#### 12.2.1 계층 구조

```
대시보드
└── 코스
    ├── 코스 홈
    ├── 세션
    │   ├── 세션 목록
    │   └── 라이브 세션
    ├── 콘텐츠
    ├── 과제
    │   ├── 과제 목록
    │   └── 과제 상세
    ├── 성적
    └── 분석
```

#### 12.2.2 브레드크럼

```html
<nav class="breadcrumb">
  <a href="/dashboard">대시보드</a>
  <span>/</span>
  <a href="/courses/123">웹 프로그래밍</a>
  <span>/</span>
  <span>과제</span>
</nav>
```

### 12.3 폼 인터랙션

#### 12.3.1 유효성 검사 타이밍

| 시점 | 검사 항목 |
|------|----------|
| `blur` | 필수 필드, 형식 검사 |
| `input` | 실시간 피드백 (비밀번호 강도 등) |
| `submit` | 전체 폼 검사 |

#### 12.3.2 자동 저장

- 초안 자동 저장: 30초마다 또는 변경 후 2초 idle
- 저장 상태 표시: "저장됨", "저장 중...", "저장 실패"

### 12.4 드래그 앤 드롭

| 기능 | 사용 화면 |
|------|----------|
| 파일 업로드 | 콘텐츠 라이브러리, 과제 제출 |
| 순서 변경 | 퀴즈 문제 순서, 선택지 순서 |
| 분반 배정 | 수동 분반 배정 |

### 12.5 키보드 단축키

#### 12.5.1 전역 단축키

| 단축키 | 기능 |
|--------|------|
| `?` | 단축키 도움말 |
| `/` | 검색 포커스 |
| `Esc` | 모달 닫기, 취소 |

#### 12.5.2 세션 중 단축키

| 단축키 | 기능 |
|--------|------|
| `M` | 마이크 토글 |
| `V` | 카메라 토글 |
| `S` | 화면 공유 토글 |
| `H` | 손들기 토글 |
| `C` | 채팅 패널 토글 |

---

## 13. 반응형 디자인

### 13.1 브레이크포인트

| 이름 | 너비 | 대상 기기 |
|------|------|----------|
| `sm` | ≥640px | 대형 모바일 |
| `md` | ≥768px | 태블릿 |
| `lg` | ≥1024px | 소형 데스크탑 |
| `xl` | ≥1280px | 대형 데스크탑 |
| `2xl` | ≥1536px | 초대형 화면 |

### 13.2 레이아웃 적응

#### 13.2.1 사이드바

| 브레이크포인트 | 동작 |
|---------------|------|
| < 1024px | 숨김 (햄버거 메뉴로 토글) |
| ≥ 1024px | 고정 표시 |

#### 13.2.2 그리드 레이아웃

```css
/* 코스 카드 그리드 */
.course-grid {
  grid-template-columns: repeat(1, 1fr);  /* 모바일 */
}

@media (min-width: 768px) {
  .course-grid {
    grid-template-columns: repeat(2, 1fr);  /* 태블릿 */
  }
}

@media (min-width: 1280px) {
  .course-grid {
    grid-template-columns: repeat(3, 1fr);  /* 데스크탑 */
  }
}
```

### 13.3 터치 최적화

| 요소 | 최소 크기 |
|------|----------|
| 버튼 | 44x44px |
| 링크 | 44px 높이 |
| 아이콘 버튼 | 48x48px |
| 체크박스/라디오 | 44x44px 터치 영역 |

---

## 14. 접근성

### 14.1 WCAG 2.1 준수

#### 14.1.1 인식의 용이성

| 항목 | 구현 |
|------|------|
| 대체 텍스트 | 모든 이미지에 `alt` 속성 |
| 자막 | 비디오 자막 지원 |
| 색상 대비 | 최소 4.5:1 (AA 수준) |
| 리사이즈 | 200%까지 확대 가능 |

#### 14.1.2 운용의 용이성

| 항목 | 구현 |
|------|------|
| 키보드 접근 | 모든 기능 키보드로 이용 가능 |
| 포커스 표시 | 명확한 포커스 스타일 |
| 건너뛰기 링크 | 본문 바로가기 링크 |
| 충분한 시간 | 타이머 연장 옵션 |

#### 14.1.3 이해의 용이성

| 항목 | 구현 |
|------|------|
| 언어 표시 | `lang` 속성 사용 |
| 일관된 네비게이션 | 동일한 위치의 메뉴 |
| 오류 식별 | 명확한 오류 메시지 |
| 입력 도움 | 라벨, 플레이스홀더, 힌트 |

### 14.2 ARIA 사용

```html
<!-- 모달 -->
<div role="dialog" aria-modal="true" aria-labelledby="modal-title">
  <h2 id="modal-title">모달 제목</h2>
</div>

<!-- 탭 -->
<div role="tablist">
  <button role="tab" aria-selected="true" aria-controls="panel-1">탭 1</button>
  <button role="tab" aria-selected="false" aria-controls="panel-2">탭 2</button>
</div>
<div role="tabpanel" id="panel-1">...</div>

<!-- 알림 -->
<div role="alert" aria-live="polite">
  저장되었습니다.
</div>
```

### 14.3 스크린 리더 지원

| 상황 | 구현 |
|------|------|
| 로딩 상태 | `aria-busy="true"` |
| 동적 콘텐츠 | `aria-live="polite"` |
| 확장/축소 | `aria-expanded` |
| 필수 필드 | `aria-required="true"` |
| 오류 연결 | `aria-describedby` |

---

## 부록: 와이어프레임 파일 인덱스

### A. 공통 컴포넌트

| 파일 | 설명 |
|------|------|
| `components/header.html` | 기본 헤더 |
| `components/header-professor.html` | 교수 헤더 |
| `components/header-student.html` | 학생 헤더 |
| `components/sidebar-course.html` | 코스 사이드바 (교수) |
| `components/sidebar-course-student.html` | 코스 사이드바 (학생) |

### B. CSS 파일

| 파일 | 설명 |
|------|------|
| `css/variables.css` | 디자인 토큰 (색상, 간격, 타이포그래피) |
| `css/base.css` | 기본 스타일 (reset, utilities) |
| `css/components.css` | 컴포넌트 스타일 |

### C. Epic별 와이어프레임

#### E1: 사용자 인증 (e1-auth/)

| 파일 | 화면 |
|------|------|
| `auth-001-register.html` | 회원가입 |
| `auth-002-email-verify.html` | 이메일 인증 대기 |
| `auth-003-email-complete.html` | 이메일 인증 완료 |
| `auth-004-login.html` | 로그인 |
| `auth-005-login-error.html` | 로그인 오류 |
| `auth-006-account-locked.html` | 계정 잠금 |
| `auth-007-oauth-link.html` | OAuth 연동 |
| `auth-008-2fa-setup.html` | 2FA 설정 시작 |
| `auth-009-2fa-qr.html` | 2FA QR 코드 |
| `auth-010-2fa-backup.html` | 2FA 백업 코드 |
| `auth-011-2fa-verify.html` | 2FA 인증 |
| `auth-012-forgot-password.html` | 비밀번호 찾기 |
| `auth-013-password-sent.html` | 재설정 메일 발송 |
| `auth-014-reset-password.html` | 비밀번호 재설정 |
| `profile-001-settings.html` | 프로필 설정 (교수) |
| `profile-001-settings-student.html` | 프로필 설정 (학생) |
| `profile-002-photo-modal.html` | 프로필 사진 변경 |
| `profile-003-change-password.html` | 비밀번호 변경 |
| `admin-001-role-management.html` | 역할 관리 |
| `admin-002-role-modal.html` | 역할 편집 |
| `error-001-forbidden.html` | 접근 권한 없음 |

#### E2: 코스 관리 (e2-course/)

| 파일 | 화면 |
|------|------|
| `dashboard.html` | 대시보드 (교수) |
| `dashboard-student.html` | 대시보드 (학생) |
| `crs-001-course-list.html` | 코스 목록 (교수) |
| `crs-001-course-list-student.html` | 코스 목록 (학생) |
| `crs-002-create-course.html` | 코스 생성 |
| `crs-003-course-detail.html` | 코스 상세 (교수) |
| `crs-003-course-detail-student.html` | 코스 상세 (학생) |
| `crs-004-students.html` | 수강생 관리 |
| `crs-005-course-settings.html` | 코스 설정 |
| `crs-008-invite-accept.html` | 초대 수락 |
| `ses-001-session-list.html` | 세션 목록 |
| `ses-002-create-session.html` | 세션 생성 |
| `cnt-001-content-library.html` | 콘텐츠 라이브러리 |
| `cnt-002-upload.html` | 파일 업로드 |
| `asg-001-assignment-list.html` | 과제 목록 |
| `asg-002-create-assignment.html` | 과제 생성 |
| `asg-003-submissions.html` | 제출 현황 |
| `asg-004-grading.html` | 채점 |
| `asg-005-submit.html` | 과제 제출 (학생) |
| `grd-001-grade-dashboard.html` | 성적 대시보드 |
| `grd-002-student-detail.html` | 학생 성적 상세 |
| `grd-003-student-view.html` | 내 성적 (학생) |

#### E3: 실시간 세미나 (e3-live/)

| 파일 | 화면 |
|------|------|
| `live-001-session-prep.html` | 세션 준비 |
| `live-002-student-join.html` | 세션 참여 (학생) |
| `live-003-waiting-room.html` | 대기실 |
| `live-004-waiting-manage.html` | 대기실 관리 |
| `live-004-student-view.html` | 학생 뷰 (대기) |
| `live-005-professor-view.html` | 교수 화면 |
| `live-006-student-view.html` | 학생 화면 |
| `live-007-screen-share-select.html` | 화면 공유 선택 |
| `live-008-screen-sharing.html` | 화면 공유 중 |
| `live-009-chat-panel.html` | 채팅 패널 |
| `live-010-direct-message.html` | 1:1 메시지 |
| `live-011-file-share.html` | 파일 공유 |
| `live-012-hand-queue.html` | 손들기 대기열 |
| `live-013-reactions.html` | 반응 선택 |
| `live-014-reaction-display.html` | 반응 표시 |
| `live-015-recording-control.html` | 녹화 컨트롤 |
| `live-016-recording-playback.html` | 녹화 재생 |
| `live-017-grid-view.html` | 그리드 뷰 |
| `live-018-speaker-view.html` | 스피커 뷰 |
| `live-019-layout-dropdown.html` | 레이아웃 선택 |
| `live-020-connection-quality.html` | 연결 품질 |
| `live-021-network-warning.html` | 네트워크 경고 |
| `live-022-end-session.html` | 세션 종료 확인 |
| `live-023-session-ended.html` | 세션 종료됨 |

#### E4: 액티브 러닝 도구 (e4-active/)

| 파일 | 화면 |
|------|------|
| `poll-001-create-poll.html` | 투표 생성 |
| `poll-002-templates.html` | 투표 템플릿 |
| `poll-003-participate.html` | 투표 참여 |
| `poll-004-results.html` | 투표 결과 |
| `poll-005-wordcloud.html` | 워드 클라우드 |
| `quiz-001-question-bank.html` | 문제 은행 |
| `quiz-002-edit-question.html` | 문제 편집 |
| `quiz-003-taking.html` | 퀴즈 풀기 |
| `quiz-004-results.html` | 퀴즈 결과 |
| `brk-001-setup.html` | 분반 설정 |
| `brk-002-manual-assign.html` | 수동 배정 |
| `brk-003-monitor.html` | 분반 모니터링 |
| `brk-004-student-view.html` | 분반 참여 |
| `wb-001-whiteboard.html` | 화이트보드 |
| `soc-001-discussion.html` | 토론 게시판 |
| `soc-002-nominate.html` | 지명 발언 |

#### E5: 평가 및 피드백 (e5-assessment/)

| 파일 | 화면 |
|------|------|
| `grade-001-quiz-result.html` | 퀴즈 결과 상세 |
| `grade-002-statistics.html` | 성적 통계 |
| `ai-grade-001-review.html` | AI 채점 검토 |
| `peer-001-setup.html` | 동료 평가 설정 |
| `peer-001-evaluate.html` | 동료 평가 진행 |
| `peer-001-evaluate-student.html` | 동료 평가 (학생) |
| `peer-002-result.html` | 동료 평가 결과 |
| `code-001-submit.html` | 코드 제출 |
| `code-002-result.html` | 코드 평가 결과 |
| `code-003-plagiarism.html` | 표절 검사 결과 |
| `engage-001-settings.html` | 참여도 설정 |
| `engage-002-dashboard.html` | 참여도 대시보드 |
| `feedback-001-instant.html` | 즉시 피드백 |

#### E6: 학습 분석 (e6-analytics/)

| 파일 | 화면 |
|------|------|
| `analytics-001-realtime.html` | 실시간 분석 |
| `analytics-list.html` | 분석 목록 |
| `alert-001-dashboard.html` | 알림 대시보드 |
| `alert-002-tracking.html` | 학생 추적 |
| `network-001-visualization.html` | 네트워크 시각화 |
| `network-002-individual.html` | 개인 네트워크 |
| `report-001-personal.html` | 개인 리포트 |
| `report-002-course.html` | 코스 리포트 |

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|------|------|----------|--------|
| 1.0.0 | 2025-01-28 | 최초 작성 | Development Team |
