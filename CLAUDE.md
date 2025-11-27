# CLAUDE.md - EduForum Wireframes Project Guide

## Project Overview

**EduForum**은 미네르바 대학의 Active Learning Forum을 참고하여 개발 중인 대학교/교육기관용 온라인 학습 플랫폼입니다.

- **프로젝트 위치**: `/mnt/d/Development/git/minerva/docs/wireframes/`
- **목적**: CSS 기반 와이어프레임 (HTML + CSS)
- **디자인 시스템**: shadcn/ui 스타일
- **아이콘**: Lucide Icons (CDN)

---

## Directory Structure

```
docs/wireframes/
├── css/
│   ├── variables.css    # CSS 변수 (색상, 간격, 타이포그래피)
│   ├── base.css         # 기본 스타일 (reset, utilities)
│   └── components.css   # 컴포넌트 스타일 (버튼, 카드, 모달 등)
├── e1-auth/             # Epic 1: 사용자 인증 (21개 화면)
├── e2-course/           # Epic 2: 코스 관리 (17개 화면)
├── e3-live/             # Epic 3: 실시간 세미나 (23개 화면)
├── e4-active/           # Epic 4: 액티브 러닝 도구 (17개 화면)
├── e5-assessment/       # Epic 5: 평가 및 피드백 (12개 화면)
├── e6-analytics/        # Epic 6: 학습 분석 (8개 화면)
└── index.html           # 메인 네비게이션
```

---

## CSS Architecture

### 1. variables.css - Design Tokens
```css
/* HSL 기반 색상 시스템 */
--background: 0 0% 100%;
--foreground: 222.2 84% 4.9%;
--border: 214.3 31.8% 82%;        /* 테두리 (밝기 82%) */
--brand-primary: 221.2 83.2% 53.3%;  /* 주요 액센트 색상 */

/* 간격 체계 */
--spacing-1: 0.25rem;  /* 4px */
--spacing-2: 0.5rem;   /* 8px */
--spacing-4: 1rem;     /* 16px */
--spacing-6: 1.5rem;   /* 24px */

/* 반경 */
--radius-md: 0.375rem;
--radius-lg: 0.5rem;
```

### 2. components.css - UI Components
주요 컴포넌트:
- `.btn`, `.btn-primary`, `.btn-outline` - 버튼
- `.card`, `.card-header`, `.card-content` - 카드
- `.input`, `.select`, `.checkbox` - 폼 요소
- `.modal-overlay`, `.modal` - 모달
- `.sidebar`, `.sidebar-link` - 사이드바
- `.navbar`, `.nav-link` - 네비게이션
- `.table`, `.table-wrapper` - 테이블
- `.badge`, `.avatar` - 뱃지, 아바타

---

## Layout Patterns

### 1. Sidebar + Main Content Layout
```html
<aside class="sidebar" style="top: 57px; height: calc(100vh - 57px);">
  <!-- 사이드바 내용 -->
</aside>
<main class="main-content">
  <!-- margin-left: 280px 필수 -->
</main>
```
- 사이드바 너비: **280px**
- main-content의 `margin-left: 280px` 필수

### 2. Modal Pattern
```html
<div class="modal-overlay" style="display: none;">
  <div class="modal">
    <div class="modal-header">
      <h2 class="modal-title">제목</h2>
      <button class="modal-close">×</button>
    </div>
    <div class="modal-body">내용</div>
    <div class="modal-footer">버튼</div>
  </div>
</div>
```
- 열기: `element.style.display = 'flex'`
- 닫기: `element.style.display = 'none'`

### 3. Centered Page Layout (독립 페이지용)
```html
<div class="container" style="min-height: 100vh; display: flex; align-items: center; justify-content: center;">
  <div class="card" style="max-width: 450px;">
    <!-- 콘텐츠 -->
  </div>
</div>
```

---

## Epic Summary

| Epic | 이름 | 화면 수 | 주요 기능 |
|------|------|---------|-----------|
| E1 | 사용자 인증 | 21 | 회원가입, 로그인, 2FA, 비밀번호 재설정, 프로필 |
| E2 | 코스 관리 | 17 | 코스 생성, 수강생 관리, 과제, 성적 |
| E3 | 실시간 세미나 | 23 | 화상 세션, 화면공유, 채팅, 녹화 |
| E4 | 액티브 러닝 | 17 | 투표, 퀴즈, 분반, 화이트보드, 토론 |
| E5 | 평가/피드백 | 12 | 성적, AI 채점, 코드 평가, 동료 평가 |
| E6 | 학습 분석 | 8 | 실시간 분석, 리포트, 위험학생 알림 |

---

## Common Issues & Solutions

### 1. 테두리가 보이지 않음
- 원인: `--border` 밝기가 너무 높음 (91.4%)
- 해결: `variables.css`에서 `--border: 214.3 31.8% 82%;`로 수정

### 2. 사이드바와 컨텐츠 겹침
- 원인: `margin-left` 값이 사이드바 너비(280px)와 불일치
- 해결: `.main-content { margin-left: 280px; }`

### 3. 모달이 열리지 않음
- 원인: `classList.add('modal-open')` 사용 (잘못된 방식)
- 해결: `element.style.display = 'flex'` 사용

---

## File Naming Convention

```
{epic}-{feature}-{screen-number}-{description}.html

예시:
- auth-001-register.html      # 인증 > 회원가입
- crs-003-course-detail.html  # 코스 > 코스 상세
- live-005-professor-view.html # 라이브 > 교수 화면
- poll-002-templates.html     # 투표 > 템플릿
```

---

## Lucide Icons Usage

```html
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>

<!-- 아이콘 사용 -->
<i data-lucide="user" class="icon-sm"></i>
<i data-lucide="settings" class="icon-md"></i>
<i data-lucide="graduation-cap" class="icon-lg"></i>

<!-- 스크립트 (body 끝) -->
<script>lucide.createIcons();</script>
```

아이콘 크기 클래스:
- `.icon-xs` - 12px
- `.icon-sm` - 16px
- `.icon-md` - 20px
- `.icon-lg` - 24px
- `.icon-xl` - 32px

---

## Related Documentation

프로젝트 루트의 docs 폴더에 상세 문서 존재:
- `01-minerva-forum-overview.md` - 프로젝트 개요
- `02-technical-architecture.md` - 기술 아키텍처
- `03-product-requirements.md` - 제품 요구사항
- `04-feature-breakdown.md` - 기능 세분화 (Epic/Story/Task)
- `design/E1~E6-*-wireframes.md` - Epic별 와이어프레임 명세

---

## Quick Commands

```bash
# 와이어프레임 디렉토리로 이동
cd /mnt/d/Development/git/minerva/docs/wireframes

# 특정 패턴의 파일 찾기
find . -name "*.html" | wc -l

# margin-left 값 확인
grep -r "margin-left:" e2-course/*.html

# 모든 HTML 파일에서 특정 텍스트 교체
sed -i 's/old-text/new-text/g' e2-course/*.html
```
