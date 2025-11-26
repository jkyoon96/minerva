# 05. UI/UX 디자인 가이드

## 문서 정보
- **제품명**: EduForum
- **버전**: 1.0
- **작성일**: 2025-11-27
- **대상**: 디자이너, 프론트엔드 개발자
- **관련 문서**:
  - [PRD](./03-product-requirements.md)
  - [기술 명세서](./04-technical-specification.md)
  - [ASCII 와이어프레임](./06-wireframes-ascii.md)

---

## 목차
1. [디자인 원칙](#1-디자인-원칙)
2. [디자인 시스템](#2-디자인-시스템)
3. [컬러 시스템](#3-컬러-시스템)
4. [타이포그래피](#4-타이포그래피)
5. [레이아웃 그리드](#5-레이아웃-그리드)
6. [컴포넌트 라이브러리](#6-컴포넌트-라이브러리)
7. [아이콘 시스템](#7-아이콘-시스템)
8. [인터랙션 패턴](#8-인터랙션-패턴)
9. [사용자 플로우](#9-사용자-플로우)
10. [반응형 디자인](#10-반응형-디자인)
11. [접근성 (Accessibility)](#11-접근성-accessibility)
12. [Figma 파일 구조](#12-figma-파일-구조)

---

## 1. 디자인 원칙

### 1.1 핵심 원칙

#### 원칙 1: 교육 중심 (Education-First)
- **의미**: 모든 디자인 결정은 학습 효과를 최우선으로 고려
- **적용**:
  - 산만함 최소화 (최소한의 장식)
  - 학습 콘텐츠에 집중할 수 있는 레이아웃
  - 명확한 정보 계층 구조

**예시**:
```
❌ 나쁜 예: 화려한 애니메이션, 불필요한 그래픽
✅ 좋은 예: 깔끔한 배경, 콘텐츠 중심 레이아웃
```

#### 원칙 2: 직관성 (Intuitive)
- **의미**: 학습 곡선 없이 즉시 사용 가능
- **적용**:
  - 표준 UI 패턴 준수
  - 명확한 라벨링
  - 일관된 네비게이션

**예시**:
```
❌ 나쁜 예: 아이콘만으로 기능 표시 (의미 불명확)
✅ 좋은 예: 아이콘 + 텍스트 라벨
```

#### 원칙 3: 실시간 피드백 (Real-time Feedback)
- **의미**: 모든 사용자 행동에 즉각적인 시각적 피드백
- **적용**:
  - 버튼 클릭 시 즉시 상태 변화
  - 로딩 인디케이터
  - 성공/실패 알림

**예시**:
```
❌ 나쁜 예: 버튼 클릭 후 무반응 (사용자 혼란)
✅ 좋은 예: 클릭 → 로딩 → 성공 메시지 (명확한 흐름)
```

#### 원칙 4: 데이터 시각화 (Data Visualization)
- **의미**: 복잡한 참여도 데이터를 쉽게 이해 가능한 시각으로
- **적용**:
  - 차트와 그래프 적극 활용
  - 색상 코딩으로 상태 구분
  - 진행률 표시

**예시**:
```
❌ 나쁜 예: 숫자 나열 (90초, 120초, 45초...)
✅ 좋은 예: 막대 그래프로 한눈에 비교
```

#### 원칙 5: 접근성 (Accessibility)
- **의미**: 모든 사용자가 차별 없이 사용
- **적용**:
  - WCAG 2.1 AA 준수
  - 키보드 네비게이션
  - 스크린 리더 지원
  - 색맹 고려 (색상만으로 정보 전달 금지)

---

## 2. 디자인 시스템

### 2.1 Design Tokens

디자인 토큰은 디자인 시스템의 원자 단위입니다.

```json
{
  "colors": {
    "primary": "#3B82F6",
    "secondary": "#8B5CF6",
    "success": "#10B981",
    "warning": "#F59E0B",
    "error": "#EF4444",
    "neutral": {
      "50": "#F9FAFB",
      "100": "#F3F4F6",
      "200": "#E5E7EB",
      "300": "#D1D5DB",
      "400": "#9CA3AF",
      "500": "#6B7280",
      "600": "#4B5563",
      "700": "#374151",
      "800": "#1F2937",
      "900": "#111827"
    }
  },
  "spacing": {
    "xs": "4px",
    "sm": "8px",
    "md": "16px",
    "lg": "24px",
    "xl": "32px",
    "2xl": "48px",
    "3xl": "64px"
  },
  "borderRadius": {
    "none": "0px",
    "sm": "4px",
    "md": "8px",
    "lg": "12px",
    "full": "9999px"
  },
  "shadows": {
    "sm": "0 1px 2px 0 rgba(0, 0, 0, 0.05)",
    "md": "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
    "lg": "0 10px 15px -3px rgba(0, 0, 0, 0.1)",
    "xl": "0 20px 25px -5px rgba(0, 0, 0, 0.1)"
  }
}
```

### 2.2 스타일 가이드 우선순위

1. **일관성** > 완벽성
2. **명확성** > 아름다움
3. **기능성** > 장식
4. **접근성** > 시각적 매력

---

## 3. 컬러 시스템

### 3.1 Primary Colors

#### Primary Blue (#3B82F6)
- **용도**: 주요 액션 버튼, 링크, 강조
- **의미**: 신뢰, 전문성
- **변형**:
  - Light: `#60A5FA` (Hover)
  - Dark: `#2563EB` (Active)

#### Secondary Purple (#8B5CF6)
- **용도**: 보조 액션, 배지, 하이라이트
- **의미**: 혁신, 창의성
- **변형**:
  - Light: `#A78BFA`
  - Dark: `#7C3AED`

### 3.2 Semantic Colors

| 색상 | Hex | 용도 | 예시 |
|------|-----|------|------|
| Success | `#10B981` | 성공 메시지, 정답 | 퀴즈 정답 표시 |
| Warning | `#F59E0B` | 경고, 주의 필요 | 참여도 낮음 알림 |
| Error | `#EF4444` | 오류, 실패 | 제출 실패 메시지 |
| Info | `#3B82F6` | 정보, 안내 | 도움말 툴팁 |

### 3.3 Neutral Colors

회색 스케일 (50-900):
- **50-200**: 배경색
- **300-400**: 테두리, 구분선
- **500-700**: 본문 텍스트
- **800-900**: 제목, 강조 텍스트

### 3.4 Role-Specific Colors

| 역할 | 색상 | Hex | 용도 |
|------|------|-----|------|
| Instructor | Amber | `#F59E0B` | 교수 아바타, 이름 태그 |
| Student | Blue | `#3B82F6` | 학생 아바타, 이름 태그 |
| TA | Purple | `#8B5CF6` | TA 아바타, 이름 태그 |
| Admin | Red | `#EF4444` | 관리자 전용 UI |

### 3.5 Data Visualization Colors

참여도 그래프 등에 사용:

```
const chartColors = [
  '#3B82F6', // Blue
  '#10B981', // Green
  '#F59E0B', // Amber
  '#EF4444', // Red
  '#8B5CF6', // Purple
  '#EC4899', // Pink
  '#14B8A6', // Teal
  '#F97316'  // Orange
];
```

### 3.6 Dark Mode (선택적, v2)

현재는 Light Mode만 지원. Dark Mode는 향후 고려.

---

## 4. 타이포그래피

### 4.1 폰트 패밀리

#### 한글: Pretendard (권장)
- **이유**: 가독성 우수, 다양한 Weight
- **대체**: Noto Sans KR, Apple SD Gothic Neo

```css
font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, sans-serif;
```

#### 영문: Inter
- **이유**: 웹 친화적, 숫자 가독성 우수
- **대체**: Roboto, San Francisco

```css
font-family: 'Inter', -apple-system, BlinkMacSystemFont, system-ui, sans-serif;
```

#### 코드: JetBrains Mono
- **용도**: 코드 블록, 코딩 퀴즈

```css
font-family: 'JetBrains Mono', 'Courier New', monospace;
```

### 4.2 타이포그래피 스케일

| 레벨 | 크기 | Weight | Line Height | 용도 |
|------|------|--------|-------------|------|
| H1 | 32px | 700 | 40px | 페이지 제목 |
| H2 | 24px | 700 | 32px | 섹션 제목 |
| H3 | 20px | 600 | 28px | 서브섹션 제목 |
| H4 | 18px | 600 | 24px | 카드 제목 |
| Body-L | 16px | 400 | 24px | 본문 (기본) |
| Body-M | 14px | 400 | 20px | 보조 텍스트 |
| Body-S | 12px | 400 | 16px | 캡션, 라벨 |
| Button | 14px | 500 | 20px | 버튼 텍스트 |

### 4.3 텍스트 색상

| 용도 | 색상 | Opacity | Hex |
|------|------|---------|-----|
| Primary Text | neutral-900 | 100% | `#111827` |
| Secondary Text | neutral-600 | 100% | `#4B5563` |
| Disabled Text | neutral-400 | 100% | `#9CA3AF` |
| Link | primary-600 | 100% | `#2563EB` |
| Link Hover | primary-700 | 100% | `#1D4ED8` |

### 4.4 Font Weight 사용 가이드

- **700 (Bold)**: 제목, 강조
- **600 (Semibold)**: 서브 제목, 카드 제목
- **500 (Medium)**: 버튼, 중요한 라벨
- **400 (Regular)**: 본문, 일반 텍스트

---

## 5. 레이아웃 그리드

### 5.1 12-Column Grid System

**데스크탑** (1280px+):
- Container: 1200px max-width
- Gutter: 24px
- Margin: 40px (양쪽)

**태블릿** (768px - 1279px):
- Container: 100% width
- Gutter: 16px
- Margin: 24px

**모바일** (< 768px):
- Container: 100% width
- Gutter: 12px
- Margin: 16px

### 5.2 Breakpoints

```css
/* Mobile */
@media (max-width: 767px) { }

/* Tablet */
@media (min-width: 768px) and (max-width: 1279px) { }

/* Desktop */
@media (min-width: 1280px) { }

/* Large Desktop */
@media (min-width: 1920px) { }
```

### 5.3 Layout Patterns

#### 앱 레이아웃 (로그인 후)

```
┌────────────────────────────────────────────────┐
│ Header (64px)                                  │
├─────────┬──────────────────────────────────────┤
│         │                                      │
│ Sidebar │        Main Content                  │
│ (240px) │                                      │
│         │                                      │
│         │                                      │
└─────────┴──────────────────────────────────────┘
```

#### 세션 레이아웃 (Live)

```
┌────────────────────────────────────────────────┐
│ Session Header (56px)                          │
├────────────────────────┬───────────────────────┤
│                        │                       │
│   Video Grid (Main)    │   Side Panel (360px)  │
│                        │   - Participants      │
│                        │   - Chat              │
│                        │   - Polls             │
│                        │                       │
├────────────────────────┴───────────────────────┤
│ Control Bar (72px)                             │
└────────────────────────────────────────────────┘
```

---

## 6. 컴포넌트 라이브러리

### 6.1 Buttons

#### Primary Button
- **용도**: 주요 액션 (세션 시작, 제출 등)
- **스타일**:
  - Background: `primary-600`
  - Text: `white`
  - Padding: `12px 24px`
  - Border Radius: `8px`
  - Hover: `primary-700`
  - Active: `primary-800`

```css
.btn-primary {
  background-color: #2563EB;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-primary:hover {
  background-color: #1D4ED8;
}
```

#### Secondary Button
- **용도**: 보조 액션 (취소, 뒤로 등)
- **스타일**:
  - Background: `transparent`
  - Border: `1px solid neutral-300`
  - Text: `neutral-700`

#### Danger Button
- **용도**: 위험한 액션 (삭제, 세션 종료)
- **스타일**:
  - Background: `error-600`
  - Text: `white`

#### Icon Button
- **용도**: 손들기, 반응 등
- **크기**: `40px × 40px`
- **아이콘 크기**: `20px`

### 6.2 Input Fields

#### Text Input
```css
.input-text {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #D1D5DB;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.input-text:focus {
  outline: none;
  border-color: #3B82F6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}
```

**States**:
- Default: `border-neutral-300`
- Focus: `border-primary-600` + blue shadow
- Error: `border-error-600` + error message
- Disabled: `bg-neutral-100`, `text-neutral-400`

#### Textarea
- 동일한 스타일, `min-height: 80px`
- Resize: vertical only

#### Select Dropdown
- Ant Design Select 사용 권장
- Custom Arrow 아이콘

### 6.3 Cards

#### Basic Card
```css
.card {
  background: white;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}
```

#### Course Card
- 썸네일 이미지 (16:9 ratio)
- 코스 제목 (H4)
- 교수 이름 (Body-S, secondary text)
- 학생 수, 세션 수 아이콘
- Hover: Subtle shadow + scale

#### Session Card
- 날짜/시간 배지 (상단)
- 세션 제목
- 상태 뱃지 (scheduled/live/completed)
- 참여 인원 (live인 경우)

### 6.4 Modals

#### 구조
```
┌─────────────────────────────────┐
│ Header (Title + Close Button)   │
├─────────────────────────────────┤
│                                 │
│        Content Area             │
│                                 │
├─────────────────────────────────┤
│ Footer (Cancel + Action Button) │
└─────────────────────────────────┘
```

#### 크기
- Small: 400px
- Medium: 600px (기본)
- Large: 800px
- Full: 90vw (투표 결과 등)

#### 배경
- Overlay: `rgba(0, 0, 0, 0.5)`
- 모달: `white`, shadow-xl
- Border Radius: `12px`

### 6.5 Toast Notifications

#### 위치
- 우측 상단 (fixed)
- Top: `80px`, Right: `24px`

#### 타입별 스타일
```css
/* Success */
.toast-success {
  background: #10B981;
  color: white;
  icon: CheckCircle;
}

/* Error */
.toast-error {
  background: #EF4444;
  color: white;
  icon: XCircle;
}

/* Info */
.toast-info {
  background: #3B82F6;
  color: white;
  icon: InfoCircle;
}

/* Warning */
.toast-warning {
  background: #F59E0B;
  color: white;
  icon: ExclamationCircle;
}
```

#### 애니메이션
- Enter: Slide in from right + Fade in (0.3s)
- Exit: Slide out to right + Fade out (0.2s)
- Auto-dismiss: 5초 후

### 6.6 Badges

#### 상태 뱃지
```css
.badge {
  display: inline-flex;
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 500;
}

.badge-live {
  background: #FEE2E2;
  color: #991B1B;
}

.badge-scheduled {
  background: #DBEAFE;
  color: #1E40AF;
}

.badge-completed {
  background: #D1FAE5;
  color: #065F46;
}
```

### 6.7 Progress Bars

#### Linear Progress
```css
.progress-bar {
  width: 100%;
  height: 8px;
  background: #E5E7EB;
  border-radius: 9999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #3B82F6;
  border-radius: 9999px;
  transition: width 0.3s ease;
}
```

#### Circular Progress (Spinner)
- Size: 24px (small), 40px (medium), 64px (large)
- Color: `primary-600`
- Animation: Spin 1s linear infinite

---

## 7. 아이콘 시스템

### 7.1 아이콘 라이브러리

**Lucide React** (권장)
- 일관된 스타일
- 가벼운 번들 크기
- Tree-shaking 지원

```bash
npm install lucide-react
```

```tsx
import { Video, Mic, MicOff, Hand, MessageCircle } from 'lucide-react';
```

### 7.2 아이콘 크기

| 크기 | Pixels | 용도 |
|------|--------|------|
| xs | 12px | 인라인 텍스트 아이콘 |
| sm | 16px | 버튼 내부, 라벨 |
| md | 20px | 기본 아이콘 버튼 |
| lg | 24px | 주요 액션 버튼 |
| xl | 32px | 헤더, 큰 버튼 |

### 7.3 주요 아이콘 매핑

| 기능 | 아이콘 | Lucide 이름 |
|------|--------|-------------|
| 비디오 On | 📹 | `Video` |
| 비디오 Off | 📹🚫 | `VideoOff` |
| 마이크 On | 🎤 | `Mic` |
| 마이크 Off | 🎤🚫 | `MicOff` |
| 손들기 | ✋ | `Hand` |
| 채팅 | 💬 | `MessageCircle` |
| 화면 공유 | 🖥️ | `Monitor` |
| 설정 | ⚙️ | `Settings` |
| 참여자 | 👥 | `Users` |
| 투표 | 📊 | `BarChart3` |
| 퀴즈 | ✏️ | `ClipboardEdit` |
| 분반 | 🏘️ | `Users` (with variant) |
| 녹화 | 🔴 | `Circle` (filled red) |
| 더보기 | ⋮ | `MoreVertical` |

---

## 8. 인터랙션 패턴

### 8.1 Hover States

**원칙**: 모든 클릭 가능한 요소는 명확한 Hover 상태 필요

```css
/* Button Hover */
button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Card Hover */
.card:hover {
  border-color: #3B82F6;
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

/* Link Hover */
a:hover {
  text-decoration: underline;
  color: #1D4ED8;
}
```

### 8.2 Focus States

**키보드 네비게이션 필수**

```css
button:focus-visible,
input:focus-visible {
  outline: 2px solid #3B82F6;
  outline-offset: 2px;
}
```

### 8.3 Loading States

#### 버튼 로딩
```tsx
<button disabled={isLoading}>
  {isLoading ? (
    <>
      <Spinner size="sm" />
      <span>Loading...</span>
    </>
  ) : (
    'Submit'
  )}
</button>
```

#### 페이지 로딩
- Skeleton 스크린 사용 (Shimmer 효과)
- Content placeholder

### 8.4 Animation Timing

```css
/* 표준 타이밍 */
--duration-fast: 0.15s;
--duration-normal: 0.2s;
--duration-slow: 0.3s;

/* Easing */
--ease-in: cubic-bezier(0.4, 0, 1, 1);
--ease-out: cubic-bezier(0, 0, 0.2, 1);
--ease-in-out: cubic-bezier(0.4, 0, 0.2, 1);
```

**사용 가이드**:
- Micro-interactions: 0.15s (버튼 클릭)
- Component transitions: 0.2s (모달 open/close)
- Page transitions: 0.3s (페이지 이동)

### 8.5 Drag & Drop (선택적)

분반 배정 시 드래그 앤 드롭:
- 드래그 시작: `cursor: grabbing`
- 드롭 영역: `border-dashed`, `bg-primary-50`
- 드롭 성공: 짧은 성공 애니메이션

---

## 9. 사용자 플로우

### 9.1 교수 - 세션 진행 플로우

```
1. 로그인
   ↓
2. 대시보드 → "내 코스" 목록
   ↓
3. 코스 선택 → 세션 목록
   ↓
4. "세션 시작" 버튼 클릭
   ↓
5. Jitsi 비디오 로딩 (2-3초)
   ↓
6. 세션 화면 진입
   ├─ 왼쪽: 비디오 그리드
   └─ 오른쪽: 사이드 패널 (참여자, 채팅, 대시보드)
   ↓
7. 실시간 참여도 대시보드 확인
   ↓
8. 투표 생성 → 학생들 응답 실시간 확인
   ↓
9. 분반 활동 시작 → 각 그룹 순회
   ↓
10. 세션 종료 → 참여도 리포트 확인
```

### 9.2 학생 - 세션 참여 플로우

```
1. 로그인
   ↓
2. 대시보드 → "오늘의 세션" 알림
   ↓
3. "세션 입장" 버튼 클릭
   ↓
4. 권한 요청 (카메라, 마이크)
   ├─ 허용 → 세션 입장
   └─ 거부 → 오디오만 입장 (옵션)
   ↓
5. 세션 화면 진입
   ↓
6. 투표 팝업 알림 → 응답
   ↓
7. 퀴즈 제출 → 즉시 결과 확인
   ↓
8. 분반 배정 알림 → 자동 이동
   ↓
9. 메인 룸 복귀
   ↓
10. 세션 종료 → 자신의 참여도 확인
```

### 9.3 첫 사용자 온보딩 플로우

```
1. 회원가입 페이지
   ├─ 이메일 입력
   ├─ 비밀번호 설정
   └─ 역할 선택 (교수/학생)
   ↓
2. 이메일 인증 (선택적)
   ↓
3. 프로필 설정
   ├─ 이름
   ├─ 프로필 사진 업로드
   └─ 소속 기관
   ↓
4. 교수 전용: 첫 코스 생성 가이드
   ├─ "첫 코스 만들기" 튜토리얼
   └─ 학생 초대 방법 안내
   ↓
5. 학생 전용: 코스 등록 가이드
   ├─ "코스 코드 입력" 안내
   └─ 또는 교수 초대 링크 클릭
   ↓
6. 대시보드 진입 (완료)
```

### 9.4 소셜 로그인 (OAuth) 플로우

```
1. 로그인 페이지
   ├─ "Google로 로그인" 버튼
   └─ "Microsoft로 로그인" 버튼
   ↓
2. OAuth 제공자 인증 화면 (새 창/리다이렉트)
   ├─ 사용자 인증
   └─ 권한 동의
   ↓
3. 콜백 처리
   ├─ 기존 계정 연동 → 대시보드 진입
   └─ 신규 사용자 → 역할 선택 화면
   ↓
4. 신규 사용자 역할 선택
   ├─ "교수" 선택 → 교수 온보딩
   └─ "학생" 선택 → 학생 온보딩
   ↓
5. 프로필 자동 완성 (OAuth 정보 활용)
   ├─ 이름 (OAuth에서 가져옴)
   ├─ 이메일 (OAuth에서 가져옴, 수정 불가)
   └─ 프로필 사진 (선택적)
   ↓
6. 대시보드 진입
```

### 9.5 2단계 인증 (2FA) 설정 플로우

```
1. 설정 페이지 → 보안 탭
   ↓
2. "2단계 인증 활성화" 클릭
   ↓
3. 인증 방법 선택
   ├─ 인증 앱 (Google Authenticator, Authy 등)
   └─ SMS 인증 (선택적)
   ↓
4. 인증 앱 설정 시
   ├─ QR 코드 표시
   ├─ 수동 입력 코드 제공
   └─ 인증 앱에서 스캔/입력
   ↓
5. 6자리 인증 코드 입력 (검증)
   ├─ 성공 → 백업 코드 생성
   └─ 실패 → 재시도 안내
   ↓
6. 백업 코드 표시 (10개)
   ├─ 복사 버튼
   ├─ 다운로드 버튼
   └─ "코드를 안전하게 보관하세요" 경고
   ↓
7. 설정 완료 확인
```

### 9.6 수강생 일괄 등록 (CSV) 플로우

```
1. 강좌 관리 → 수강생 탭
   ↓
2. "CSV로 일괄 등록" 버튼 클릭
   ↓
3. CSV 업로드 모달
   ├─ 드래그 앤 드롭 영역
   ├─ 파일 선택 버튼
   └─ 템플릿 다운로드 링크
   ↓
4. 파일 업로드 → 검증 중 (로딩)
   ↓
5. 검증 결과 미리보기
   ├─ 유효한 항목: N명
   ├─ 오류 항목: M명 (상세 보기)
   └─ 중복 항목: K명
   ↓
6. 오류 항목 수정 (선택적)
   ├─ 인라인 편집
   └─ 오류 행 제외 옵션
   ↓
7. "등록하기" 버튼 클릭
   ↓
8. 등록 처리 (프로그레스 바)
   ↓
9. 완료 결과
   ├─ 성공: N명 등록됨
   ├─ 이미 등록된 계정: M명 (기존 계정 연결)
   └─ 초대 이메일 발송됨: K명
```

### 9.7 과제 제출 플로우 (학생)

```
1. 대시보드 → "마감 임박 과제" 알림
   ↓
2. 과제 상세 페이지
   ├─ 과제 설명
   ├─ 마감일 및 남은 시간
   ├─ 제출 형식 안내
   └─ 첨부파일 (참고자료)
   ↓
3. 과제 작성
   ├─ 텍스트 입력 (Rich Editor)
   ├─ 파일 업로드 (드래그 앤 드롭)
   └─ 코드 에디터 (코딩 과제 시)
   ↓
4. 임시 저장 (자동)
   ├─ 30초마다 자동 저장
   └─ "마지막 저장: HH:MM" 표시
   ↓
5. 제출 전 미리보기
   ├─ 제출 내용 확인
   └─ 첨부파일 목록
   ↓
6. "제출하기" 버튼 클릭
   ↓
7. 제출 확인 모달
   ├─ "마감 전까지 재제출 가능합니다"
   └─ 확인/취소 버튼
   ↓
8. 제출 완료
   ├─ 성공 메시지
   ├─ 제출 시간 기록
   └─ 이메일 확인 발송 (선택적)
```

### 9.8 AI 채점 및 피드백 플로우 (교수)

```
1. 과제 관리 → 제출물 목록
   ↓
2. "AI 채점 시작" 버튼 클릭
   ↓
3. AI 채점 설정 모달
   ├─ 채점 기준 입력 (루브릭)
   ├─ 배점 설정
   └─ 부분 점수 허용 여부
   ↓
4. AI 채점 진행 (프로그레스)
   ├─ 진행률 표시
   ├─ 예상 완료 시간
   └─ 백그라운드 실행 옵션
   ↓
5. 채점 결과 리뷰 화면
   ├─ 학생별 점수
   ├─ AI 생성 피드백
   └─ 신뢰도 지표 (높음/중간/낮음)
   ↓
6. 교수 검토 및 수정
   ├─ 점수 조정
   ├─ 피드백 수정/추가
   └─ 개별 승인/반려
   ↓
7. 일괄 공개 설정
   ├─ 즉시 공개
   ├─ 예약 공개
   └─ 점수만 공개 (피드백 비공개)
   ↓
8. 학생에게 결과 알림 발송
```

### 9.9 세션 녹화 재생 플로우 (학생)

```
1. 강좌 페이지 → "녹화된 세션" 탭
   ↓
2. 녹화 목록
   ├─ 세션 제목
   ├─ 날짜 및 길이
   ├─ 조회수
   └─ 진행률 (이어보기 지원)
   ↓
3. 녹화 선택 → 재생 페이지
   ↓
4. 비디오 플레이어 화면
   ├─ 메인 영역: 비디오
   ├─ 타임라인: 주요 구간 마커
   └─ 사이드: 챕터 목록
   ↓
5. 타임라인 마커 종류
   ├─ 📊 투표/퀴즈 진행 시점
   ├─ 📝 판서/화이트보드 시점
   ├─ 💬 활발한 토론 구간
   └─ 🔖 교수 북마크
   ↓
6. 마커 클릭 → 해당 시점으로 이동
   ↓
7. 플레이어 기능
   ├─ 재생 속도 조절 (0.5x ~ 2x)
   ├─ 자막 ON/OFF (자동 생성)
   ├─ 전체화면
   └─ 개인 북마크 추가
   ↓
8. 시청 진행률 자동 저장
```

### 9.10 성적 확인 및 이의 신청 플로우 (학생)

```
1. 대시보드 → "새로운 성적 발표" 알림
   ↓
2. 성적 상세 페이지
   ├─ 총점 및 등급
   ├─ 항목별 점수 breakdown
   └─ AI/교수 피드백
   ↓
3. 본인 제출물 확인
   ├─ 원본 제출 내용
   └─ 채점 기준 (루브릭)
   ↓
4. 이의 신청 필요 시
   ├─ "이의 신청" 버튼 클릭
   ↓
5. 이의 신청 폼
   ├─ 이의 항목 선택 (복수 선택 가능)
   ├─ 사유 입력 (필수)
   └─ 근거 자료 첨부 (선택)
   ↓
6. 신청 제출
   ├─ 확인 메시지
   └─ 예상 처리 기간 안내
   ↓
7. 신청 상태 추적
   ├─ 접수됨
   ├─ 검토 중
   ├─ 처리 완료 → 결과 알림
```

### 9.11 동료 평가 플로우 (학생)

```
1. 대시보드 → "동료 평가 요청" 알림
   ↓
2. 동료 평가 목록
   ├─ 평가할 과제 수
   ├─ 마감일
   └─ 익명 여부 표시
   ↓
3. 평가 대상 선택 → 평가 화면
   ↓
4. 평가 화면 구성
   ├─ 왼쪽: 동료 제출물 (읽기 전용)
   └─ 오른쪽: 평가 폼
   ↓
5. 평가 기준별 점수 입력
   ├─ 루브릭 항목별 점수 (1-5)
   ├─ 항목별 코멘트 (선택)
   └─ 종합 피드백 (필수)
   ↓
6. 평가 미리보기
   ├─ 입력 내용 확인
   └─ 익명 처리 확인
   ↓
7. 평가 제출
   ↓
8. 다음 평가 대상으로 이동 (반복)
   ↓
9. 모든 평가 완료
   ├─ 완료 메시지
   └─ 본인 평가 결과 공개 시점 안내
```

### 9.12 학습 분석 조기경보 플로우 (교수)

```
1. 대시보드 → "위험 학생 알림" 뱃지
   ↓
2. 조기경보 대시보드
   ├─ 위험 학생 목록 (빨간색)
   ├─ 주의 학생 목록 (노란색)
   └─ 위험 지표 요약
   ↓
3. 학생 카드 정보
   ├─ 이름/학번
   ├─ 위험 점수 (0-100)
   ├─ 주요 위험 요인
   └─ 최근 활동 요약
   ↓
4. 학생 상세 클릭 → 개별 분석
   ├─ 출석률 그래프
   ├─ 참여도 트렌드
   ├─ 성적 추이
   └─ 예상 최종 등급
   ↓
5. 개입 조치
   ├─ "이메일 보내기" (템플릿 제공)
   ├─ "상담 예약" (캘린더 연동)
   └─ "맞춤 학습자료 제공"
   ↓
6. 조치 기록
   ├─ 조치 내용 입력
   ├─ 후속 알림 설정
   └─ 효과 추적 활성화
   ↓
7. 조치 후 모니터링
   ├─ 변화 추이 그래프
   └─ 개선/악화 알림
```

---

## 10. 반응형 디자인

### 10.1 모바일 우선 고려사항

현재 v1에서는 **데스크탑/태블릿 우선**, 모바일은 기본 기능만 지원:

#### 모바일에서 제한되는 기능
- ❌ 세션 주최 (교수 기능)
- ❌ 실시간 참여도 대시보드
- ❌ 분반 관리
- ✅ 세션 참여 (학생)
- ✅ 투표/퀴즈 응답
- ✅ 채팅

### 10.2 Break point별 레이아웃 조정

#### 데스크탑 (1280px+)
- Sidebar 고정 표시
- 비디오 그리드: 3x3 또는 4x4
- 사이드 패널: 360px 고정

#### 태블릿 (768px - 1279px)
- Sidebar 접기/펼치기
- 비디오 그리드: 2x3
- 사이드 패널: 280px

#### 모바일 (< 768px)
- Sidebar 하단 탭으로 전환
- 비디오 그리드: 1x2 (스크롤)
- 사이드 패널: 전체 화면 모달

### 10.3 Touch-Friendly Design

모바일 터치 고려:
- 버튼 최소 크기: `44px × 44px`
- 터치 영역 간격: `8px` 이상
- Swipe 제스처 지원 (사이드 패널)

---

## 11. 접근성 (Accessibility)

### 11.1 WCAG 2.1 AA 준수 체크리스트

#### 색상 대비 (Contrast)
- [ ] 텍스트 대비: 최소 4.5:1 (일반 텍스트)
- [ ] 큰 텍스트 대비: 최소 3:1 (18px+ bold, 24px+ regular)
- [ ] UI 컴포넌트 대비: 최소 3:1 (버튼, 입력 필드)

**도구**: [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/)

#### 키보드 네비게이션
- [ ] 모든 기능이 키보드로 접근 가능
- [ ] Tab 순서 논리적
- [ ] Focus 상태 명확히 표시
- [ ] Esc 키로 모달 닫기

#### 스크린 리더
- [ ] 모든 이미지에 `alt` 텍스트
- [ ] ARIA 라벨 적절히 사용
- [ ] Heading 계층 구조 (H1 → H2 → H3)
- [ ] 링크 텍스트 명확 (❌ "여기", ✅ "세션 입장하기")

#### 폼 접근성
- [ ] `<label>` 명확히 연결
- [ ] 오류 메시지 명확
- [ ] 필수 입력 표시 (`*` + aria-required)

### 11.2 ARIA 사용 예시

```tsx
// 버튼
<button aria-label="마이크 음소거">
  <MicOff />
</button>

// 투표 진행률
<div role="progressbar" aria-valuenow={75} aria-valuemin={0} aria-valuemax={100}>
  75% 응답
</div>

// 알림
<div role="alert" aria-live="polite">
  투표가 종료되었습니다.
</div>

// 탭 네비게이션
<div role="tablist">
  <button role="tab" aria-selected="true" aria-controls="panel-1">
    참여자
  </button>
  <button role="tab" aria-selected="false" aria-controls="panel-2">
    채팅
  </button>
</div>
```

### 11.3 색맹 고려

**색상만으로 정보 전달 금지**

❌ 나쁜 예:
```
참여도 높음: 파란색
참여도 낮음: 빨간색
```

✅ 좋은 예:
```
참여도 높음: 파란색 + "↑" 아이콘
참여도 낮음: 빨간색 + "↓" 아이콘 + "낮음" 텍스트
```

---

## 12. Figma 파일 구조

### 12.1 Figma 프로젝트 조직

```
EduForum Design System
│
├─ 📁 1. Foundation
│  ├─ Colors
│  ├─ Typography
│  ├─ Grid & Spacing
│  └─ Icons
│
├─ 📁 2. Components
│  ├─ Buttons
│  ├─ Inputs
│  ├─ Cards
│  ├─ Modals
│  ├─ Badges
│  └─ Navigation
│
├─ 📁 3. Wireframes (Low-Fi)
│  ├─ User Flows
│  ├─ Information Architecture
│  └─ Layout Concepts
│
├─ 📁 4. High-Fidelity Screens
│  ├─ Authentication
│  │  ├─ Login
│  │  ├─ Register
│  │  └─ Password Reset
│  │
│  ├─ Dashboard
│  │  ├─ Instructor Dashboard
│  │  └─ Student Dashboard
│  │
│  ├─ Course Management
│  │  ├─ Course List
│  │  ├─ Course Detail
│  │  ├─ Create Course
│  │  └─ Student Enrollment
│  │
│  ├─ Session
│  │  ├─ Session List
│  │  ├─ Create Session
│  │  ├─ Live Session (Instructor View)
│  │  ├─ Live Session (Student View)
│  │  └─ Session Analytics
│  │
│  ├─ Active Learning Tools
│  │  ├─ Poll Creation
│  │  ├─ Poll Response (Student)
│  │  ├─ Poll Results
│  │  ├─ Quiz Taking
│  │  ├─ Quiz Results
│  │  └─ Breakout Rooms
│  │
│  └─ Analytics
│     ├─ Participation Dashboard
│     ├─ Course Analytics
│     └─ Student Profile
│
└─ 📁 5. Prototypes
   ├─ Instructor Flow
   ├─ Student Flow
   └─ Admin Flow
```

### 12.2 Figma 컴포넌트 네이밍 규칙

```
[Category]/[Component]/[Variant]/[State]

예시:
Button/Primary/Default
Button/Primary/Hover
Button/Primary/Disabled
Button/Secondary/Default
Button/Icon/Default

Input/Text/Default
Input/Text/Focus
Input/Text/Error

Card/Course/Default
Card/Course/Hover
Card/Session/Live
```

### 12.3 Auto Layout 사용 가이드

**원칙**: 모든 컴포넌트는 Auto Layout 적용

```
버튼 예시:
┌─────────────────────┐
│  [Icon] Button Text │  ← Auto Layout (Horizontal)
│  Padding: 12px 24px │
│  Gap: 8px           │
└─────────────────────┘
```

**장점**:
- 반응형 디자인 쉬움
- 텍스트 길이 변화 대응
- 일관된 간격 유지

### 12.4 Design Tokens 플러그인

**Figma Tokens** 플러그인 사용 권장:
- JSON으로 토큰 export
- 코드와 디자인 동기화
- 다크모드 토글 쉬움

```json
{
  "color": {
    "primary": {
      "value": "#3B82F6",
      "type": "color"
    }
  },
  "spacing": {
    "md": {
      "value": "16px",
      "type": "spacing"
    }
  }
}
```

### 12.5 Handoff to Developers

#### 개발자에게 전달할 정보
1. **Figma 링크** (View 권한)
2. **Design Specs**:
   - Inspect 모드 활성화
   - CSS 코드 복사 가능
3. **Assets Export**:
   - 아이콘: SVG
   - 이미지: WebP (2x 해상도)
4. **Style Guide PDF** (별도 문서)

#### Figma Inspect 설정
```
Settings → Preferences → Developer Handoff
- Show code snippets: ✅
- Unit: px
- Color format: Hex
- Export settings: 1x, 2x
```

---

## 13. 디자인 작업 프로세스

### 13.1 디자인 스프린트

```
Week 1: Discovery & Research
├─ 사용자 인터뷰
├─ 경쟁사 분석
└─ 요구사항 정리

Week 2: Wireframing
├─ Low-fi 와이어프레임 (Paper/Figma)
├─ User Flow 다이어그램
└─ 이해관계자 리뷰

Week 3-4: High-Fidelity Design
├─ 디자인 시스템 구축
├─ 주요 화면 디자인
└─ Prototype 제작

Week 5: Usability Testing
├─ 5-10명 사용자 테스트
├─ 피드백 수집
└─ 수정 반영

Week 6: Handoff & Support
├─ 개발자에게 전달
├─ QA 지원
└─ 디자인 수정 (필요 시)
```

### 13.2 디자인 리뷰 체크리스트

#### 시각적 일관성
- [ ] 컬러 팔레트 준수
- [ ] 타이포그래피 스케일 일관성
- [ ] 간격(Spacing) 일관성
- [ ] Border Radius 일관성

#### 사용성
- [ ] 주요 액션 명확
- [ ] 오류 상태 처리
- [ ] 로딩 상태 표시
- [ ] 빈 상태 (Empty State) 디자인

#### 접근성
- [ ] 색상 대비 검증
- [ ] Focus 상태 명확
- [ ] 키보드 네비게이션 가능
- [ ] 대체 텍스트 (Alt text)

#### 반응형
- [ ] 데스크탑 레이아웃
- [ ] 태블릿 레이아웃
- [ ] 모바일 레이아웃 (기본)

---

## 14. 참고 자료

### 14.1 디자인 시스템 레퍼런스
- **Material Design 3** (Google)
- **Ant Design** (Alibaba)
- **Carbon Design System** (IBM)
- **Atlassian Design System**

### 14.2 컬러 도구
- **Coolors.co**: 컬러 팔레트 생성
- **Adobe Color**: 색상 조합 탐색
- **WebAIM Contrast Checker**: 접근성 검증

### 14.3 타이포그래피 도구
- **Google Fonts**: 웹 폰트
- **Font Pair**: 폰트 조합 추천
- **Type Scale**: 타이포그래피 스케일 계산

### 14.4 아이콘 리소스
- **Lucide Icons**: https://lucide.dev
- **Heroicons**: https://heroicons.com
- **Feather Icons**: https://feathericons.com

### 14.5 Figma 플러그인 추천
- **Figma Tokens**: Design tokens 관리
- **Stark**: 접근성 검증
- **A11y - Focus Orderer**: 키보드 순서 확인
- **Iconify**: 아이콘 라이브러리 통합
- **Content Reel**: Lorem ipsum 대체 (실제 텍스트)

---

## 다음 단계

디자인 가이드를 기반으로:
1. **Figma 파일 제작** (디자이너 작업)
2. **[ASCII 와이어프레임](./06-wireframes-ascii.md)** 확인 (주요 화면 레이아웃)
3. **디자인 시스템 Storybook 구축** (개발자 + 디자이너 협업)
4. **프론트엔드 컴포넌트 구현** (React + Tailwind CSS)

---

**문서 끝**