# Docker 설정 파일 요약

이 문서는 EduForum/Minerva 프로젝트의 Docker 관련 파일들에 대한 개요를 제공합니다.

## 생성된 파일

### 루트 레벨 설정

1. **docker-compose.yml** (3.5 KB)
   - 프로덕션용 Docker Compose 설정
   - 3개 서비스 정의: postgres, backend, frontend
   - 헬스체크 및 네트워크 설정 포함
   - 보안 모범 사례를 적용한 프로덕션 준비 상태

2. **docker-compose.dev.yml** (3.5 KB)
   - docker-compose.yml의 개발용 오버라이드
   - 볼륨 마운트로 핫 리로드 지원
   - 디버그 포트 노출 (Backend: 5005, Frontend: 9229)
   - 프론트엔드용 개발 Dockerfile 사용

3. **.env.example** (893 bytes)
   - 환경 변수 템플릿
   - 모든 필수 설정 포함
   - 플레이스홀더 값으로 보안 중심 설계

4. **.dockerignore** (245 bytes)
   - 루트 레벨 Docker 제외 파일
   - 문서, git 파일 등 제외

5. **Makefile** (2.5 KB)
   - Docker 명령어 단축키
   - 데이터베이스 백업/복원 유틸리티 포함
   - 사용하기 쉬운 타겟 (make up, make dev 등)

### 백엔드 설정

6. **apps/backend/Dockerfile** (1.4 KB)
   - 멀티스테이지 빌드 (builder + runtime)
   - 빌드에 eclipse-temurin:17-jdk-alpine 사용
   - 런타임에 eclipse-temurin:17-jre-alpine 사용
   - 보안을 위한 non-root 사용자
   - 컨테이너 최적화 JVM 설정
   - Spring Actuator를 이용한 헬스체크

7. **apps/backend/.dockerignore** (266 bytes)
   - 빌드 결과물, IDE 파일 제외
   - 기존 파일 유지

### 프론트엔드 설정

8. **apps/frontend/Dockerfile** (1.7 KB)
   - 멀티스테이지 빌드 (deps, builder, runner)
   - node:20-alpine 사용
   - Next.js standalone 빌드
   - non-root 사용자 (nextjs)
   - 프로덕션 최적화

9. **apps/frontend/Dockerfile.dev** (563 bytes)
   - 개발 전용 Dockerfile
   - 개발 의존성 포함
   - 핫 리로드 지원

10. **apps/frontend/.dockerignore** (523 bytes)
    - node_modules, 빌드 결과물 제외
    - 환경 파일 및 IDE 설정 제외

11. **apps/frontend/next.config.js** (업데이트됨)
    - Docker용 `output: 'standalone'` 추가
    - API URL 기본값을 8080 포트로 업데이트

### 지원 파일

12. **scripts/init-db.sql** (614 bytes)
    - PostgreSQL 초기화 스크립트
    - UUID 및 pgcrypto 확장 활성화
    - 첫 데이터베이스 생성 시 실행

13. **scripts/dev-seed.sql** (878 bytes)
    - 개발용 시드 데이터 템플릿
    - 개발 모드에서만 로드

14. **apps/frontend/src/app/api/health/route.ts** (351 bytes)
    - 프론트엔드용 헬스체크 엔드포인트
    - JSON 상태 응답 반환
    - Docker 헬스체크에서 사용

### 문서

15. **DOCKER.md** (8.8 KB)
    - 종합 Docker 배포 가이드
    - 문제 해결, 모범 사례 포함
    - 프로덕션 체크리스트
    - CI/CD 통합 예시

16. **QUICKSTART-DOCKER.md** (3.0 KB)
    - 빠른 시작 가이드 (5분 내 실행)
    - 단계별 지침
    - 자주 사용하는 명령어 참조

## 아키텍처 개요

```
┌─────────────────────────────────────────────────────────────┐
│                Docker 네트워크 (bridge)                       │
│                   eduforum-network                           │
│                                                              │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │  PostgreSQL  │◄───│   Backend    │◄───│   Frontend   │  │
│  │   (5432)     │    │   (8080)     │    │   (3000)     │  │
│  │              │    │              │    │              │  │
│  │ postgres:15  │    │ Spring Boot  │    │  Next.js 14  │  │
│  │   alpine     │    │   Java 17    │    │   Node 20    │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
│        │                                                     │
│        ▼                                                     │
│  ┌──────────────┐                                           │
│  │   볼륨        │                                           │
│  │postgres_data │                                           │
│  └──────────────┘                                           │
└─────────────────────────────────────────────────────────────┘

외부 접근:
- localhost:3000 → 프론트엔드
- localhost:8080 → 백엔드 API
- localhost:5432 → PostgreSQL (프로덕션)
- localhost:5433 → PostgreSQL (개발)
```

## 주요 기능

### 보안
- 모든 컨테이너에서 non-root 사용자 사용
- 빌드와 런타임 단계 분리
- 시크릿을 위한 환경 변수 사용
- 모든 서비스에 헬스체크 적용
- 하드코딩된 자격 증명 없음

### 성능
- 멀티스테이지 빌드 (더 작은 이미지)
- 의존성 레이어 캐싱
- JVM 설정 최적화
- Next.js standalone 출력
- 데이터베이스 볼륨 영속성

### 개발 경험
- 두 앱 모두 핫 리로드 지원
- 디버그 포트 노출
- 개발용 시드 데이터
- 개발 모드에서 상세 로깅
- 소스 코드 볼륨 마운트

### 프로덕션 준비
- 헬스체크
- 리소스 제한 기능
- 적절한 로깅 설정
- 그레이스풀 셧다운
- 무중단 배포 준비 완료

## 빠른 사용법 참조

### 프로덕션
```bash
# 모든 서비스 시작
docker-compose up -d

# Make 사용
make up

# 로그 보기
make logs

# 서비스 중지
make down
```

### 개발
```bash
# 핫 리로드로 시작
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# Make 사용
make dev
```

### 데이터베이스 관리
```bash
# 백업
make db-backup

# 복원
make db-restore FILE=backups/backup_20240101_120000.sql
```

## 환경 변수

모든 환경 변수는 `.env.example`에 문서화되어 있습니다:

### 필수
- `POSTGRES_PASSWORD` - 데이터베이스 비밀번호
- `JWT_SECRET` - JWT 서명 키 (32자 이상)
- `NEXTAUTH_SECRET` - NextAuth 암호화 키 (32자 이상)

### 선택
- `CORS_ALLOWED_ORIGINS` - 허용된 CORS 출처
- `NEXT_PUBLIC_API_URL` - 공개 API URL
- `API_URL` - 내부 API URL

## 포트 매핑

| 서비스 | 내부 | 외부 (프로덕션) | 외부 (개발) |
|--------|------|-----------------|-------------|
| Frontend | 3000 | 3000 | 3000 |
| Backend | 8080 | 8080 | 8080 |
| Backend Debug | - | - | 5005 |
| Frontend Debug | - | - | 9229 |
| PostgreSQL | 5432 | 5432 | 5433 |

## 파일 크기

총 설정: ~30 KB
- Docker 설정: ~12 KB
- 문서: ~12 KB
- 지원 파일: ~6 KB

## 다음 단계

1. `.env.example`을 `.env`로 복사하고 설정
2. `make up` 또는 `docker-compose up -d` 실행
3. http://localhost:3000 접속
4. 고급 사용법은 DOCKER.md 참조

## 유지 관리

모든 파일은 다음 원칙을 따릅니다:
- **보안 우선**: non-root 사용자, 하드코딩된 시크릿 없음
- **프로덕션 준비**: 헬스체크, 적절한 오류 처리
- **개발자 친화적**: 핫 리로드, 디버그 포트, 명확한 로깅
- **잘 문서화됨**: 인라인 주석, 종합 가이드
- **업계 표준**: 공식 문서의 모범 사례

## 지원

- QUICKSTART-DOCKER.md - 빠른 5분 가이드
- DOCKER.md - 종합 문서
- Makefile - 명령어 참조 (`make help`)
