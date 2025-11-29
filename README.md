# EduForum (Minerva)

[![CI](https://github.com/jkyoon96/minerva/actions/workflows/ci.yml/badge.svg)](https://github.com/jkyoon96/minerva/actions/workflows/ci.yml)
[![Docker Build](https://github.com/jkyoon96/minerva/actions/workflows/docker-build.yml/badge.svg)](https://github.com/jkyoon96/minerva/actions/workflows/docker-build.yml)
[![CodeQL](https://github.com/jkyoon96/minerva/actions/workflows/codeql.yml/badge.svg)](https://github.com/jkyoon96/minerva/actions/workflows/codeql.yml)

미네르바 대학의 Active Learning Forum을 참고하여 개발된 **대학교/교육기관용 온라인 학습 플랫폼**입니다.

## 주요 기능

### E1: 사용자 인증 (Authentication)
- 회원가입/로그인 (JWT 기반)
- 2단계 인증 (TOTP)
- 비밀번호 재설정
- 프로필 관리
- 역할 기반 접근 제어 (RBAC)

### E2: 코스 관리 (Course Management)
- 코스 생성/수정/삭제
- 수강생 등록 및 일괄 등록 (CSV)
- TA 배정 및 권한 관리
- 과제 관리
- 파일/콘텐츠 라이브러리
- 캘린더 내보내기 (iCal)

### E3: 실시간 세미나 (Live Seminar)
- WebRTC 기반 화상 세션
- 화면 공유
- 실시간 채팅
- 세션 녹화

### E4: 액티브 러닝 (Active Learning)
- 실시간 투표/퀴즈
- 분반 토론
- 화이트보드
- 손들기 기능

### E5: 평가 및 피드백 (Assessment)
- 채점 기준 (루브릭) 관리
- 과제 제출 및 채점
- 동료 평가
- 성적 관리

### E6: 학습 분석 (Analytics)
- 실시간 참여도 분석
- 학습 리포트
- 위험 학생 알림
- 대시보드

## 기술 스택

### Backend
- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **Authentication**: Spring Security + JWT
- **Build**: Gradle

### Frontend
- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **UI**: React 18 + shadcn/ui
- **State**: Zustand
- **Styling**: Tailwind CSS

### Infrastructure
- **Container**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Registry**: GitHub Container Registry (ghcr.io)

## 프로젝트 구조

```
minerva/
├── apps/
│   ├── backend/          # Spring Boot API 서버
│   │   ├── src/main/java/com/eduforum/api/
│   │   │   ├── common/   # 공통 유틸리티
│   │   │   ├── config/   # 설정
│   │   │   └── domain/   # 도메인 (auth, course, seminar, etc.)
│   │   └── src/test/     # 테스트
│   └── frontend/         # Next.js 클라이언트
│       ├── src/
│       │   ├── app/      # App Router 페이지
│       │   ├── components/
│       │   ├── hooks/
│       │   ├── lib/
│       │   └── store/
│       └── __tests__/    # 테스트
├── docs/                 # 문서
│   ├── api/              # API 설계서
│   ├── database/         # DB 설계서
│   ├── design/           # 와이어프레임 명세
│   └── wireframes/       # HTML 와이어프레임
├── scripts/              # 유틸리티 스크립트
├── docker-compose.yml    # Docker 프로덕션 설정
├── docker-compose.dev.yml # Docker 개발 설정
└── Makefile              # 편의 명령어
```

## 빠른 시작

### 요구사항
- Docker & Docker Compose
- Node.js 20+ (개발 시)
- Java 17+ (개발 시)
- PostgreSQL 15+ (Docker 없이 실행 시)

### Docker로 실행 (권장)

```bash
# 1. 환경 변수 설정
cp .env.example .env

# 2. 프로덕션 모드 실행
make up

# 또는 개발 모드 (hot reload)
make dev

# 3. 서비스 접속
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# API Docs: http://localhost:8080/swagger-ui.html
```

### 로컬 개발 환경

```bash
# Backend
cd apps/backend
./gradlew bootRun

# Frontend
cd apps/frontend
npm install
npm run dev
```

### 테스트 실행

```bash
# Backend 테스트
cd apps/backend
./gradlew test

# Frontend 테스트
cd apps/frontend
npm test
```

## 환경 변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| `DATABASE_URL` | PostgreSQL 연결 URL | - |
| `JWT_SECRET` | JWT 서명 키 (최소 256bit) | - |
| `JWT_EXPIRATION` | 액세스 토큰 만료 시간 | 3600000 |
| `MAIL_PROVIDER` | 이메일 제공자 (console/smtp/sendgrid) | console |
| `STORAGE_PROVIDER` | 스토리지 제공자 (local/s3) | local |

자세한 설정은 [.env.example](.env.example)을 참조하세요.

## API 문서

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **설계 문서**: [docs/api/](docs/api/)

## 개발 가이드

### 커밋 컨벤션

```
feat: 새로운 기능
fix: 버그 수정
docs: 문서 변경
style: 코드 포맷팅
refactor: 리팩토링
test: 테스트 추가/수정
chore: 빌드, 설정 변경
```

### 브랜치 전략

- `main`: 프로덕션 배포
- `develop`: 개발 통합
- `feature/*`: 기능 개발
- `fix/*`: 버그 수정

### PR 체크리스트

- [ ] 코드 린트 통과 (`./gradlew check`, `npm run lint`)
- [ ] 테스트 통과 (`./gradlew test`, `npm test`)
- [ ] 타입 체크 통과 (`npm run type-check`)
- [ ] 문서 업데이트 (필요시)

## 문서

| 문서 | 설명 |
|------|------|
| [DOCKER.md](DOCKER.md) | Docker 배포 가이드 |
| [TEST_INFRASTRUCTURE.md](TEST_INFRASTRUCTURE.md) | 테스트 가이드 |
| [.github/CICD_GUIDE.md](.github/CICD_GUIDE.md) | CI/CD 가이드 |
| [docs/api/](docs/api/) | API 설계서 |
| [docs/database/](docs/database/) | DB 설계서 |

## 라이선스

MIT License

## 기여

기여를 환영합니다! 이슈나 PR을 통해 참여해 주세요.
