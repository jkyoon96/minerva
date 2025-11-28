# EduForum Frontend - 빠른 시작 가이드

## 📦 설치 및 실행

### 1단계: 의존성 설치

```bash
cd /mnt/d/Development/git/minerva/apps/frontend
npm install
```

### 2단계: 환경 변수 설정

```bash
cp .env.example .env.local
```

`.env.local` 파일을 열어 다음과 같이 수정:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8000/api
```

### 3단계: 개발 서버 실행

```bash
npm run dev
```

브라우저에서 http://localhost:3000 접속

## 🎯 주요 페이지

| URL | 설명 | 라우트 그룹 |
|-----|------|------------|
| `/` | 랜딩 페이지 | (marketing) |
| `/login` | 로그인 | (auth) |
| `/register` | 회원가입 | (auth) |
| `/dashboard` | 대시보드 | (dashboard) |
| `/dashboard/courses` | 코스 목록 | (dashboard) |

## 📁 프로젝트 구조 (간략)

```
apps/frontend/
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── (marketing)/        # 공개 페이지
│   │   ├── (auth)/             # 인증 페이지
│   │   └── (dashboard)/        # 보호된 영역
│   ├── components/
│   │   ├── ui/                 # 기본 UI 컴포넌트
│   │   └── common/             # 공통 컴포넌트
│   ├── lib/                    # 유틸리티 함수
│   ├── styles/                 # 전역 스타일
│   └── types/                  # TypeScript 타입
├── public/                     # 정적 파일
└── [설정 파일들]
```

## 🛠 개발 명령어

```bash
# 개발 서버 (http://localhost:3000)
npm run dev

# TypeScript 타입 검사
npm run type-check

# ESLint 검사
npm run lint

# Prettier 코드 포맷팅
npm run format

# 프로덕션 빌드
npm run build

# 프로덕션 서버 실행
npm start
```

## 🎨 사용된 기술

- **Next.js 14**: App Router, Server Components, Server Actions
- **TypeScript**: 정적 타입 검사
- **Tailwind CSS**: 유틸리티 기반 CSS 프레임워크
- **shadcn/ui 패턴**: 재사용 가능한 UI 컴포넌트
- **Lucide React**: 아이콘 라이브러리

## 📝 다음 단계

1. **인증 구현**: NextAuth.js 통합
2. **API 연동**: Spring Boot 백엔드와 통신
3. **실시간 기능**: Socket.io 통합
4. **코스 상세 페이지**: 코스 관리 기능
5. **라이브 세션**: WebRTC 기반 화상 회의

## 🐛 문제 해결

### 포트 충돌
```bash
PORT=3001 npm run dev
```

### 의존성 오류
```bash
rm -rf node_modules package-lock.json
npm install
```

### TypeScript 오류
```bash
rm -rf .next
npm run type-check
```

## 📚 참고 문서

- 상세 문서: `README.md`
- 시스템 아키텍처: `/mnt/d/Development/git/minerva/docs/05-system-architecture.md`
- 프로젝트 개요: `/mnt/d/Development/git/minerva/CLAUDE.md`
