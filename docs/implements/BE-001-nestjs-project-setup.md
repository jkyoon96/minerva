# BE-001: NestJS 백엔드 프로젝트 초기 설정

> **Task ID**: BE-001
> **Issue**: #285
> **Epic**: INFRA
> **Priority**: P0
> **Story Points**: 2
> **작업일**: 2025-01-28
> **상태**: 완료

---

## 1. 작업 개요

NestJS 프로젝트 생성, 폴더 구조, 환경 설정

### 1.1 주요 산출물

| 항목 | 위치 | 설명 |
|------|------|------|
| NestJS 프로젝트 | `apps/backend/` | 백엔드 API 서버 |
| 환경 설정 파일 | `apps/backend/.env.*` | dev, staging, prod 환경 |
| 공통 모듈 | `apps/backend/src/common/` | 필터, 인터셉터, 파이프 |
| 설정 모듈 | `apps/backend/src/config/` | 환경 설정 |
| 헬스체크 | `apps/backend/src/modules/health/` | 상태 확인 API |
| 문서 | `apps/backend/README.md` | 프로젝트 문서 |

---

## 2. 프로젝트 구조

```
apps/backend/
├── src/
│   ├── common/
│   │   ├── decorators/         # 커스텀 데코레이터
│   │   ├── dto/                # 공통 DTO
│   │   ├── exceptions/         # 커스텀 예외
│   │   ├── filters/            # 예외 필터
│   │   │   └── http-exception.filter.ts
│   │   ├── guards/             # 가드
│   │   ├── interceptors/       # 인터셉터
│   │   │   ├── logging.interceptor.ts
│   │   │   └── transform.interceptor.ts
│   │   ├── interfaces/         # 공통 인터페이스
│   │   ├── middleware/         # 미들웨어
│   │   ├── pipes/              # 파이프
│   │   │   └── validation.pipe.ts
│   │   └── utils/              # 유틸리티
│   ├── config/
│   │   ├── app.config.ts       # 앱 설정
│   │   ├── database.config.ts  # DB 설정
│   │   ├── jwt.config.ts       # JWT 설정
│   │   └── swagger.config.ts   # Swagger 설정
│   ├── modules/
│   │   └── health/             # 헬스체크 모듈
│   │       ├── health.module.ts
│   │       └── health.controller.ts
│   ├── app.module.ts
│   └── main.ts
├── test/
│   ├── e2e/
│   └── unit/
├── .env.example
├── .env.development
├── .env.staging
├── .env.production
├── nest-cli.json
├── package.json
├── tsconfig.json
└── README.md
```

---

## 3. 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| 프레임워크 | NestJS | 11.x |
| 언어 | TypeScript | 5.x |
| 런타임 | Node.js | 18+ |
| ORM | TypeORM | 0.3.x |
| 데이터베이스 | PostgreSQL | 16 |
| 인증 | JWT (Passport) | - |
| API 문서 | Swagger/OpenAPI | 3.0 |
| 유효성 검사 | class-validator | - |
| 보안 | Helmet, CORS | - |

---

## 4. 설치된 패키지

### 프로덕션 의존성

```json
{
  "@nestjs/common": "^11.0.0",
  "@nestjs/config": "^3.3.0",
  "@nestjs/core": "^11.0.0",
  "@nestjs/jwt": "^10.2.0",
  "@nestjs/passport": "^10.0.3",
  "@nestjs/platform-express": "^11.0.0",
  "@nestjs/swagger": "^8.1.0",
  "@nestjs/terminus": "^10.2.3",
  "@nestjs/typeorm": "^10.0.2",
  "class-transformer": "^0.5.1",
  "class-validator": "^0.14.1",
  "compression": "^1.7.4",
  "helmet": "^8.0.0",
  "passport": "^0.7.0",
  "passport-jwt": "^4.0.1",
  "pg": "^8.13.1",
  "typeorm": "^0.3.20"
}
```

### 개발 의존성

```json
{
  "@nestjs/cli": "^11.0.0",
  "@nestjs/testing": "^11.0.0",
  "@types/compression": "^1.7.5",
  "@types/express": "^5.0.0",
  "@types/jest": "^29.5.14",
  "@types/node": "^22.10.2",
  "@types/passport-jwt": "^4.0.1",
  "eslint": "^9.17.0",
  "jest": "^29.7.0",
  "prettier": "^3.4.2",
  "ts-jest": "^29.2.5",
  "typescript": "^5.7.2"
}
```

---

## 5. 환경 설정

### 5.1 환경 변수 (.env.example)

```env
# Application
NODE_ENV=development
PORT=3000
API_PREFIX=api/v1
CORS_ORIGIN=http://localhost:5173

# Database
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=eduforum
DB_PASSWORD=eduforum12
DB_DATABASE=eduforum
DB_SYNC=true
DB_LOGGING=true

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-in-production
JWT_EXPIRES_IN=1d
JWT_REFRESH_EXPIRES_IN=7d

# Swagger
SWAGGER_ENABLED=true
SWAGGER_PATH=api/docs
```

### 5.2 환경별 설정

| 환경 | 파일 | 특징 |
|------|------|------|
| Development | `.env.development` | DB_SYNC=true, SWAGGER=true |
| Staging | `.env.staging` | DB_SYNC=false, SWAGGER=true |
| Production | `.env.production` | DB_SYNC=false, SWAGGER=false |

---

## 6. 주요 구현 내용

### 6.1 main.ts (앱 엔트리포인트)

- Swagger 문서 설정
- 글로벌 ValidationPipe 적용
- 글로벌 HttpExceptionFilter 적용
- 글로벌 인터셉터 적용 (로깅, 응답 변환)
- CORS, Helmet, Compression 설정
- API 프리픽스 설정 (`/api/v1`)

### 6.2 에러 핸들링 (HttpExceptionFilter)

일관된 에러 응답 형식:

```json
{
  "statusCode": 400,
  "timestamp": "2025-01-28T09:00:00.000Z",
  "path": "/api/v1/endpoint",
  "method": "POST",
  "message": "Bad Request",
  "errors": ["validation error details"]
}
```

### 6.3 응답 변환 (TransformInterceptor)

일관된 성공 응답 형식:

```json
{
  "data": { ... },
  "statusCode": 200,
  "timestamp": "2025-01-28T09:00:00.000Z"
}
```

### 6.4 헬스체크 API

| 엔드포인트 | 설명 |
|-----------|------|
| `GET /api/v1/health` | 전체 시스템 상태 |
| `GET /api/v1/health/ready` | Readiness Probe (K8s) |
| `GET /api/v1/health/live` | Liveness Probe (K8s) |

### 6.5 Swagger API 문서

- 접근 경로: `http://localhost:3000/api/docs`
- JWT Bearer 인증 지원
- 태그별 API 그룹화

---

## 7. 실행 방법

### 7.1 의존성 설치

```bash
cd /mnt/d/Development/git/minerva/apps/backend
npm install
```

### 7.2 개발 서버 실행

```bash
# 개발 모드 (hot-reload)
npm run start:dev

# 프로덕션 빌드
npm run build
npm run start:prod
```

### 7.3 테스트

```bash
# 단위 테스트
npm run test

# E2E 테스트
npm run test:e2e

# 커버리지
npm run test:cov
```

---

## 8. API 엔드포인트

### 8.1 현재 구현된 엔드포인트

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/v1/health` | 전체 시스템 상태 확인 |
| GET | `/api/v1/health/ready` | Readiness Probe |
| GET | `/api/v1/health/live` | Liveness Probe |
| GET | `/api/docs` | Swagger API 문서 |

### 8.2 응답 예시

```bash
# 헬스체크
curl http://localhost:3000/api/v1/health

# 응답
{
  "status": "ok",
  "info": {
    "database": { "status": "up" },
    "memory": { "status": "up" },
    "disk": { "status": "up" }
  }
}
```

---

## 9. 빌드 검증

```bash
npm run build
# webpack 5.103.0 compiled successfully
```

---

## 10. Acceptance Criteria 충족 현황

### Backend 요구사항

- [x] 프로젝트 구조 설정
- [x] 환경 설정 (dev, staging, prod)
- [x] 입력값 유효성 검사 구현 (ValidationPipe)
- [x] 에러 핸들링 및 적절한 HTTP 상태 코드 반환
- [x] API 문서 (Swagger/OpenAPI) 설정
- [ ] 단위 테스트 작성 (커버리지 80% 이상) - 후속 작업

### 품질 요구사항

- [x] 코드 리뷰 완료
- [x] 문서화 완료

---

## 11. 다음 단계

| Issue | 작업 | 의존성 |
|-------|------|--------|
| #286 | BE-002: 공통 모듈 설정 | BE-001 ✅ |
| #287 | BE-003: 데이터베이스 연결 설정 | BE-001 ✅ |

---

## 12. 참조 문서

- `docs/02-technical-architecture.md` - 기술 아키텍처
- `apps/backend/README.md` - 프로젝트 README
- `apps/backend/IMPLEMENTATION.md` - 상세 구현 내역
- `apps/backend/SETUP_GUIDE.md` - 빠른 시작 가이드

---

## 13. 변경 이력

| 버전 | 날짜 | 작성자 | 변경 내용 |
|------|------|--------|----------|
| 1.0 | 2025-01-28 | Claude | 초기 작성 |

---

**작업 완료**
