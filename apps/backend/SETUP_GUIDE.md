# EduForum Backend - 빠른 시작 가이드

## 사전 요구사항

1. **Node.js** 18.x 이상
2. **npm** 9.x 이상
3. **PostgreSQL** 16.x
4. **Git**

## 설치 단계

### 1. 데이터베이스 설정

PostgreSQL에 접속하여 데이터베이스를 생성합니다:

```sql
-- PostgreSQL 접속
psql -U postgres

-- 데이터베이스 및 사용자 생성
CREATE DATABASE eduforum;
CREATE USER eduforum WITH PASSWORD 'eduforum12';
GRANT ALL PRIVILEGES ON DATABASE eduforum TO eduforum;

-- PostgreSQL 15+ 추가 권한 설정 (필요시)
\c eduforum
GRANT ALL ON SCHEMA public TO eduforum;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO eduforum;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO eduforum;

-- 종료
\q
```

### 2. 프로젝트 클론 (이미 완료된 경우 생략)

```bash
cd /mnt/d/Development/git/minerva/apps/backend
```

### 3. 의존성 설치

```bash
npm install
```

### 4. 환경 변수 확인

`.env.development` 파일이 이미 생성되어 있습니다. 필요시 수정:

```env
NODE_ENV=development
PORT=3000
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=eduforum
DB_PASSWORD=eduforum12
DB_DATABASE=eduforum
```

### 5. 개발 서버 실행

```bash
npm run start:dev
```

서버가 정상적으로 실행되면 다음과 같은 로그가 출력됩니다:

```
[Nest] INFO - Application is running on: http://localhost:3000/api/v1
[Nest] INFO - Swagger documentation available at: http://localhost:3000/api/docs
[Nest] INFO - Environment: development
```

## 테스트

### 헬스체크 테스트

```bash
# 기본 헬스체크
curl http://localhost:3000/api/v1/health

# Readiness probe
curl http://localhost:3000/api/v1/health/ready

# Liveness probe
curl http://localhost:3000/api/v1/health/live
```

### 예상 응답

```json
{
  "status": "ok",
  "info": {
    "database": {
      "status": "up"
    },
    "memory_heap": {
      "status": "up"
    },
    "memory_rss": {
      "status": "up"
    },
    "storage": {
      "status": "up"
    }
  },
  "error": {},
  "details": {
    "database": {
      "status": "up"
    },
    "memory_heap": {
      "status": "up"
    },
    "memory_rss": {
      "status": "up"
    },
    "storage": {
      "status": "up"
    }
  }
}
```

## API 문서 접근

브라우저에서 다음 URL로 접속:

```
http://localhost:3000/api/docs
```

Swagger UI에서 모든 API 엔드포인트를 확인하고 테스트할 수 있습니다.

## 주요 명령어

```bash
# 개발 서버 실행 (watch mode)
npm run start:dev

# 디버그 모드
npm run start:debug

# 빌드
npm run build

# 프로덕션 모드 실행
npm run start:prod

# 테스트
npm run test

# E2E 테스트
npm run test:e2e

# 코드 포맷팅
npm run format

# 린팅
npm run lint
```

## 문제 해결

### PostgreSQL 연결 오류

```
[TypeOrmModule] Unable to connect to the database
```

**해결 방법:**
1. PostgreSQL이 실행 중인지 확인
2. `.env.development` 파일의 DB 설정 확인
3. 데이터베이스와 사용자가 생성되었는지 확인

### 포트 이미 사용 중

```
Error: listen EADDRINUSE: address already in use :::3000
```

**해결 방법:**
1. `.env.development`에서 다른 포트 지정
2. 또는 기존 프로세스 종료

```bash
# Linux/Mac
lsof -i :3000
kill -9 [PID]

# Windows
netstat -ano | findstr :3000
taskkill /PID [PID] /F
```

## 다음 단계

1. **BE-002**: 사용자 인증 모듈 구현
2. **BE-003**: 코스 관리 모듈 구현
3. **BE-004**: 실시간 세미나 기반 구축

## 도움말

- [NestJS 공식 문서](https://docs.nestjs.com/)
- [TypeORM 가이드](https://typeorm.io/)
- [PostgreSQL 문서](https://www.postgresql.org/docs/)
