# EduForum Backend API

EduForum 백엔드 API - NestJS 기반 RESTful API 서버

## 기술 스택

- **Framework**: NestJS 11.x
- **Language**: TypeScript 5.x
- **Database**: PostgreSQL 16
- **ORM**: TypeORM 0.3.x
- **Authentication**: JWT (JSON Web Token)
- **API Documentation**: Swagger/OpenAPI 3.0
- **Validation**: class-validator, class-transformer
- **Security**: Helmet, CORS

## 프로젝트 구조

```
apps/backend/
├── src/
│   ├── common/                    # 공통 모듈
│   │   ├── decorators/           # 커스텀 데코레이터
│   │   ├── dto/                  # 공통 DTO
│   │   ├── exceptions/           # 커스텀 예외
│   │   ├── filters/              # 예외 필터
│   │   ├── guards/               # 가드
│   │   ├── interceptors/         # 인터셉터
│   │   ├── interfaces/           # 공통 인터페이스
│   │   ├── middleware/           # 미들웨어
│   │   ├── pipes/                # 파이프
│   │   └── utils/                # 유틸리티
│   ├── config/                   # 환경 설정
│   │   ├── app.config.ts
│   │   ├── database.config.ts
│   │   ├── jwt.config.ts
│   │   └── swagger.config.ts
│   ├── modules/                  # 기능 모듈
│   │   └── health/              # 헬스체크 모듈
│   ├── app.module.ts
│   └── main.ts
├── test/                         # 테스트
│   ├── e2e/
│   └── unit/
├── .env.example                  # 환경 변수 예시
├── .env.development              # 개발 환경
├── .env.staging                  # 스테이징 환경
├── .env.production               # 운영 환경
├── nest-cli.json
├── package.json
├── tsconfig.json
└── README.md
```

## 설치 및 실행

### 1. 의존성 설치

```bash
npm install
```

### 2. 환경 변수 설정

`.env.development` 파일을 확인하고 필요한 값을 수정합니다:

```env
# Application
NODE_ENV=development
PORT=3000
API_PREFIX=api/v1

# Database
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=eduforum
DB_PASSWORD=eduforum12
DB_DATABASE=eduforum

# JWT
JWT_SECRET=dev-secret-key-not-for-production
JWT_EXPIRES_IN=1d
```

### 3. 데이터베이스 준비

PostgreSQL이 설치되어 있어야 하며, 데이터베이스를 생성해야 합니다:

```sql
CREATE DATABASE eduforum;
CREATE USER eduforum WITH PASSWORD 'eduforum12';
GRANT ALL PRIVILEGES ON DATABASE eduforum TO eduforum;
```

### 4. 개발 서버 실행

```bash
# 개발 모드 (watch mode)
npm run start:dev

# 디버그 모드
npm run start:debug

# 프로덕션 모드
npm run build
npm run start:prod
```

## API 문서

서버 실행 후 다음 URL에서 Swagger API 문서를 확인할 수 있습니다:

```
http://localhost:3000/api/docs
```

## 헬스체크

### 전체 헬스체크
```
GET http://localhost:3000/api/v1/health
```

응답:
```json
{
  "status": "ok",
  "info": {
    "database": { "status": "up" },
    "memory_heap": { "status": "up" },
    "memory_rss": { "status": "up" },
    "storage": { "status": "up" }
  }
}
```

### Readiness Probe
```
GET http://localhost:3000/api/v1/health/ready
```

### Liveness Probe
```
GET http://localhost:3000/api/v1/health/live
```

## 주요 기능

### 1. 환경별 설정
- Development, Staging, Production 환경별 설정 파일
- ConfigModule을 통한 중앙화된 설정 관리

### 2. 전역 예외 처리
- HttpExceptionFilter: HTTP 예외 처리
- 일관된 에러 응답 형식
- 자동 로깅

### 3. 응답 변환
- TransformInterceptor: 모든 응답을 일관된 형식으로 변환
```json
{
  "data": {},
  "statusCode": 200,
  "timestamp": "2025-11-28T00:00:00.000Z"
}
```

### 4. 요청 로깅
- LoggingInterceptor: 모든 요청/응답 자동 로깅
- 요청 처리 시간 측정

### 5. 입력값 검증
- ValidationPipe: class-validator 기반 자동 검증
- DTO 클래스를 통한 타입 안전성

### 6. 보안
- Helmet: 보안 헤더 설정
- CORS: Cross-Origin 요청 관리
- JWT: 토큰 기반 인증

## 테스트

```bash
# 단위 테스트
npm run test

# e2e 테스트
npm run test:e2e

# 테스트 커버리지
npm run test:cov
```

## 빌드

```bash
# 프로덕션 빌드
npm run build

# 빌드 후 실행
npm run start:prod
```

## 환경 변수

| 변수명 | 설명 | 기본값 |
|--------|------|--------|
| NODE_ENV | 실행 환경 | development |
| PORT | 서버 포트 | 3000 |
| API_PREFIX | API 경로 접두사 | api/v1 |
| DB_HOST | 데이터베이스 호스트 | localhost |
| DB_PORT | 데이터베이스 포트 | 5432 |
| DB_USERNAME | 데이터베이스 사용자 | eduforum |
| DB_PASSWORD | 데이터베이스 비밀번호 | eduforum12 |
| DB_DATABASE | 데이터베이스 이름 | eduforum |
| JWT_SECRET | JWT 시크릿 키 | - |
| JWT_EXPIRES_IN | JWT 만료 시간 | 1d |
| SWAGGER_ENABLED | Swagger 활성화 | true |
| SWAGGER_PATH | Swagger 경로 | api/docs |
| CORS_ORIGIN | CORS 허용 도메인 | http://localhost:3001 |

## 다음 단계

### Epic 1: 사용자 인증 (BE-002)
- 사용자 엔티티 생성
- 회원가입/로그인 API
- JWT 인증 가드
- 비밀번호 암호화

### Epic 2: 코스 관리 (BE-003)
- 코스 엔티티 생성
- 코스 CRUD API
- 수강생 관리
- 권한 관리

### Epic 3: 실시간 세미나 (BE-004)
- WebSocket 설정
- 세션 관리
- 실시간 통신 구조

## 라이센스

MIT

## 문의

EduForum Team
