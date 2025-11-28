# EduForum API

미네르바 대학의 Active Learning Forum을 참고하여 개발된 교육 플랫폼 백엔드 API

## 프로젝트 개요

EduForum은 대학교 및 교육기관을 위한 온라인 학습 플랫폼으로, 실시간 세미나, 액티브 러닝 도구, 평가 시스템을 제공합니다.

### 주요 기능

- **사용자 인증**: 회원가입, 로그인, 2FA, JWT 기반 인증
- **코스 관리**: 코스 생성, 수강생 관리, 과제 관리
- **실시간 세미나**: 화상 세션, 화면 공유, 채팅, 녹화
- **액티브 러닝**: 실시간 투표, 퀴즈, 분반 활동, 화이트보드
- **평가 및 피드백**: 성적 관리, AI 채점, 동료 평가
- **학습 분석**: 실시간 분석, 리포트, 위험 학생 알림

## 기술 스택

- **Java**: 17
- **Spring Boot**: 3.2.1
- **Database**: PostgreSQL 15
- **Security**: Spring Security + JWT (JJWT 0.12.3)
- **Documentation**: SpringDoc OpenAPI 3.0
- **Build**: Gradle 8.5

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/eduforum/api/
│   │   ├── EduforumApplication.java
│   │   ├── config/                 # 설정 클래스
│   │   │   ├── SecurityConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   ├── JpaConfig.java
│   │   │   └── WebConfig.java
│   │   ├── common/                 # 공통 모듈
│   │   │   ├── dto/
│   │   │   │   └── ApiResponse.java
│   │   │   └── exception/
│   │   │       ├── ErrorCode.java
│   │   │       ├── BusinessException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   ├── domain/                 # 도메인 모듈
│   │   │   └── health/
│   │   │       └── HealthController.java
│   │   └── security/               # 보안 모듈
│   │       └── jwt/
│   │           ├── JwtTokenProvider.java
│   │           └── JwtAuthenticationFilter.java
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-staging.yml
│       └── application-prod.yml
└── test/
    └── java/com/eduforum/api/
        └── EduforumApplicationTests.java
```

## 시작하기

### 필수 요구사항

- Java 17
- Gradle 8.5 이상
- PostgreSQL 15 이상

### 설치 및 실행

자세한 실행 방법은 [QUICKSTART.md](QUICKSTART.md)를 참조하세요.

#### 1. 의존성 설치

```bash
./gradlew build
```

#### 2. 데이터베이스 설정

```sql
CREATE DATABASE eduforum;
CREATE USER eduforum WITH PASSWORD 'eduforum12';
GRANT ALL PRIVILEGES ON DATABASE eduforum TO eduforum;
```

#### 3. 애플리케이션 실행

```bash
# 개발 환경
./gradlew bootRun --args='--spring.profiles.active=dev'

# 또는
java -jar build/libs/eduforum-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

#### 4. API 문서 확인

브라우저에서 다음 URL로 접속:
- Swagger UI: http://localhost:8000/api/docs/swagger-ui.html
- API Docs: http://localhost:8000/api/docs/api-docs

## API 엔드포인트

### Health Check

- `GET /api/v1/health` - 기본 헬스 체크
- `GET /api/v1/health/ready` - Readiness 프로브
- `GET /api/v1/health/live` - Liveness 프로브

### 인증 (예정)

- `POST /api/v1/auth/register` - 회원가입
- `POST /api/v1/auth/login` - 로그인
- `POST /api/v1/auth/refresh` - 토큰 갱신
- `POST /api/v1/auth/logout` - 로그아웃

## 환경 설정

### 프로파일

- `dev` - 개발 환경 (기본)
- `staging` - 스테이징 환경
- `prod` - 프로덕션 환경
- `test` - 테스트 환경

### 환경 변수

프로덕션 환경에서는 다음 환경 변수를 설정해야 합니다:

```bash
DB_USERNAME=eduforum
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key_must_be_at_least_256_bits_long
```

## Docker

### 이미지 빌드

```bash
docker build -t eduforum-api:latest .
```

### 컨테이너 실행

```bash
docker run -d \
  -p 8000:8000 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e DB_USERNAME=eduforum \
  -e DB_PASSWORD=eduforum12 \
  --name eduforum-api \
  eduforum-api:latest
```

## 테스트

```bash
# 전체 테스트
./gradlew test

# 특정 테스트
./gradlew test --tests EduforumApplicationTests
```

## 개발 가이드

### 코드 스타일

- Java 17 features 사용
- Lombok을 활용한 보일러플레이트 코드 최소화
- RESTful API 설계 원칙 준수
- 패키지별 응집도 유지

### API 응답 형식

모든 API는 다음과 같은 공통 응답 형식을 따릅니다:

```json
{
  "status": 200,
  "message": "Success",
  "data": {},
  "meta": {},
  "timestamp": "2024-01-01T12:00:00"
}
```

### 에러 처리

- `ErrorCode` enum을 사용한 에러 코드 관리
- `BusinessException`으로 비즈니스 로직 에러 처리
- `GlobalExceptionHandler`에서 전역 예외 처리

## 배포

### CI/CD

GitHub Actions를 통한 자동 배포 (예정)

### 모니터링

- Spring Actuator를 통한 헬스 체크
- Prometheus + Grafana 연동 (예정)

## 라이선스

MIT License

## 기여

이슈 및 풀 리퀘스트는 언제나 환영합니다.

## 문서

- [빠른 시작 가이드](QUICKSTART.md)
- [API 설계서](../../docs/api-design.md)
- [데이터베이스 설계](../../docs/db-design.md)
- [기술 아키텍처](../../docs/02-technical-architecture.md)

## 관련 프로젝트

- Frontend: `/apps/frontend` (예정)
- Wireframes: `/docs/wireframes`

## 문의

개발팀: dev@eduforum.com
