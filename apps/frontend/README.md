# EduForum Frontend

EduForum의 프론트엔드 애플리케이션입니다. Next.js 14 App Router를 사용하여 구축된 대화형 온라인 학습 플랫폼입니다.

## 기술 스택

- **프레임워크**: Next.js 14 (App Router)
- **언어**: TypeScript
- **스타일링**: Tailwind CSS
- **UI 컴포넌트**: shadcn/ui 패턴
- **아이콘**: Lucide React
- **상태 관리**: Zustand (추후 통합)
- **인증**: NextAuth.js (추후 통합)
- **실시간 통신**: Socket.io (추후 통합)

## 시작하기

### 필수 요구사항

- Node.js 18.x 이상
- npm 또는 yarn

### 설치

```bash
# 의존성 설치
npm install

# 환경 변수 설정
cp .env.example .env.local
# .env.local 파일을 열어 필요한 환경 변수를 설정하세요
```

### 개발 서버 실행

```bash
npm run dev
```

브라우저에서 [http://localhost:3000](http://localhost:3000)을 열어 결과를 확인하세요.

### 빌드

```bash
# 프로덕션 빌드
npm run build

# 프로덕션 서버 실행
npm start
```

## 프로젝트 구조

```
apps/frontend/
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── (marketing)/        # 공개 페이지 (SSG)
│   │   │   └── page.tsx        # 랜딩 페이지
│   │   ├── (auth)/             # 인증 페이지 (SSR)
│   │   │   ├── layout.tsx      # 인증 레이아웃
│   │   │   ├── login/          # 로그인
│   │   │   └── register/       # 회원가입
│   │   ├── (dashboard)/        # 보호된 영역
│   │   │   ├── layout.tsx      # 대시보드 레이아웃 (사이드바 포함)
│   │   │   ├── dashboard/      # 대시보드 홈
│   │   │   └── courses/        # 코스 목록
│   │   ├── layout.tsx          # 루트 레이아웃
│   │   ├── error.tsx           # 에러 페이지
│   │   ├── not-found.tsx       # 404 페이지
│   │   └── loading.tsx         # 로딩 UI
│   ├── components/
│   │   ├── ui/                 # 기본 UI 컴포넌트
│   │   │   ├── button.tsx
│   │   │   ├── card.tsx
│   │   │   └── input.tsx
│   │   └── common/             # 공통 컴포넌트
│   │       ├── Navbar.tsx
│   │       └── Sidebar.tsx
│   ├── lib/
│   │   └── utils.ts            # 유틸리티 함수
│   ├── styles/
│   │   └── globals.css         # 전역 스타일
│   └── types/
│       └── index.ts            # TypeScript 타입 정의
├── public/                     # 정적 파일
├── .env.example                # 환경 변수 예제
├── next.config.js              # Next.js 설정
├── tailwind.config.ts          # Tailwind CSS 설정
├── tsconfig.json               # TypeScript 설정
└── package.json                # 프로젝트 의존성
```

## 라우트 그룹 설명

### (marketing) - 공개 페이지
- **렌더링**: SSG (Static Site Generation)
- **목적**: 랜딩 페이지, 기능 소개 등
- **인증**: 불필요

### (auth) - 인증 페이지
- **렌더링**: SSR (Server-Side Rendering)
- **목적**: 로그인, 회원가입, 비밀번호 재설정
- **인증**: 불필요 (인증 전 페이지)

### (dashboard) - 보호된 영역
- **렌더링**: SSR + Client Components (필요시)
- **목적**: 대시보드, 코스 관리, 설정 등
- **인증**: 필수 (middleware를 통한 보호)

## 주요 기능

### 현재 구현된 기능
- ✅ Next.js 14 App Router 기본 구조
- ✅ TypeScript 설정
- ✅ Tailwind CSS 통합
- ✅ 기본 UI 컴포넌트 (Button, Card, Input)
- ✅ 레이아웃 구조 (Navbar, Sidebar)
- ✅ 라우트 그룹 (marketing, auth, dashboard)
- ✅ 에러 및 로딩 UI

### 추후 구현 예정
- ⏳ NextAuth.js 인증 통합
- ⏳ API 연동 (Spring Boot 백엔드)
- ⏳ Socket.io 실시간 통신
- ⏳ 코스 상세 페이지
- ⏳ 실시간 세미나 룸 (WebRTC)
- ⏳ 투표, 퀴즈 등 액티브 러닝 도구
- ⏳ 학습 분석 대시보드

## 환경 변수

`.env.local` 파일에 다음 환경 변수를 설정하세요:

```bash
# API 서버 주소
NEXT_PUBLIC_API_URL=http://localhost:8000/api

# NextAuth.js (추후 설정)
# NEXTAUTH_URL=http://localhost:3000
# NEXTAUTH_SECRET=your-secret-key

# OAuth 제공자 (추후 설정)
# GOOGLE_CLIENT_ID=
# GOOGLE_CLIENT_SECRET=
```

## 개발 가이드

### 새 페이지 추가

1. 적절한 라우트 그룹 선택 (marketing, auth, dashboard)
2. 폴더와 `page.tsx` 파일 생성
3. 필요한 경우 `layout.tsx`, `loading.tsx`, `error.tsx` 추가

### 새 컴포넌트 추가

- **UI 컴포넌트**: `src/components/ui/` (재사용 가능한 기본 컴포넌트)
- **공통 컴포넌트**: `src/components/common/` (비즈니스 로직이 포함된 컴포넌트)

### 스타일 가이드

- Tailwind CSS 유틸리티 클래스 사용
- `cn()` 함수로 조건부 클래스 병합
- CSS 변수는 `globals.css`에 정의

### TypeScript 타입

- 공통 타입은 `src/types/index.ts`에 정의
- 컴포넌트별 타입은 해당 파일 내에 정의

## 사용 가능한 스크립트

```bash
# 개발 서버 (포트 3000)
npm run dev

# 프로덕션 빌드
npm run build

# 프로덕션 서버
npm start

# ESLint 검사
npm run lint

# TypeScript 타입 검사
npm run type-check

# 코드 포맷팅 (Prettier)
npm run format
```

## 코드 스타일

이 프로젝트는 다음 도구를 사용하여 일관된 코드 스타일을 유지합니다:

- **ESLint**: JavaScript/TypeScript 린팅
- **Prettier**: 코드 포맷팅
- **TypeScript**: 정적 타입 검사

## 배포

### Vercel (권장)

```bash
# Vercel CLI 설치
npm i -g vercel

# 배포
vercel
```

### Docker

```bash
# 이미지 빌드
docker build -t eduforum-frontend .

# 컨테이너 실행
docker run -p 3000:3000 eduforum-frontend
```

## 문제 해결

### 포트가 이미 사용 중인 경우

```bash
# 다른 포트로 실행
PORT=3001 npm run dev
```

### 의존성 설치 오류

```bash
# node_modules 및 lock 파일 삭제 후 재설치
rm -rf node_modules package-lock.json
npm install
```

### TypeScript 에러

```bash
# TypeScript 캐시 삭제
rm -rf .next
npm run type-check
```

## 관련 문서

- [Next.js 14 문서](https://nextjs.org/docs)
- [Tailwind CSS 문서](https://tailwindcss.com/docs)
- [TypeScript 문서](https://www.typescriptlang.org/docs)
- [shadcn/ui 문서](https://ui.shadcn.com)

## 라이선스

이 프로젝트는 EduForum의 일부입니다.

---

**문서 버전**: 1.0
**최종 수정일**: 2025-01-29
