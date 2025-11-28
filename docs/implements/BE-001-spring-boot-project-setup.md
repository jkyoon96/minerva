# BE-001: Spring Boot 백엔드 프로젝트 초기 설정

> **Task ID**: BE-001
> **Issue**: #285
> **Epic**: INFRA
> **Priority**: P0
> **Story Points**: 2
> **작업일**: 2025-01-29
> **상태**: 완료
> **변경사항**: NestJS에서 Spring Boot로 전환

---

## 1. 작업 개요

Spring Boot 프로젝트 생성, 폴더 구조, 환경 설정

### 1.1 주요 산출물

| 항목 | 위치 | 설명 |
|------|------|------|
| Spring Boot 프로젝트 | `apps/backend/` | 백엔드 API 서버 |
| 환경 설정 파일 | `apps/backend/src/main/resources/application*.yml` | dev, staging, prod 환경 |
| 공통 모듈 | `apps/backend/src/main/java/.../common/` | DTO, 예외 처리 |
| 설정 클래스 | `apps/backend/src/main/java/.../config/` | Security, Swagger, JPA |
| 헬스체크 | `apps/backend/src/main/java/.../domain/health/` | 상태 확인 API |
| 문서 | `apps/backend/README.md` | 프로젝트 문서 |

---

## 2. 프로젝트 구조

```
apps/backend/
├── src/
│   ├── main/
│   │   ├── java/com/eduforum/api/
│   │   │   ├── EduforumApplication.java       # 메인 클래스
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java        # Spring Security
│   │   │   │   ├── SwaggerConfig.java         # OpenAPI/Swagger
│   │   │   │   ├── JpaConfig.java             # JPA 설정
│   │   │   │   └── WebConfig.java             # 웹 설정
│   │   │   ├── common/
│   │   │   │   ├── dto/
│   │   │   │   │   └── ApiResponse.java       # 공통 응답 DTO
│   │   │   │   └── exception/
│   │   │   │       ├── GlobalExceptionHandler.java
│   │   │   │       ├── BusinessException.java
│   │   │   │       └── ErrorCode.java
│   │   │   ├── domain/
│   │   │   │   └── health/
│   │   │   │       └── HealthController.java
│   │   │   └── security/
│   │   │       └── jwt/
│   │   │           ├── JwtTokenProvider.java
│   │   │           └── JwtAuthenticationFilter.java
│   │   └── resources/
│   │       ├── application.yml                # 공통 설정
│   │       ├── application-dev.yml            # 개발 환경
│   │       ├── application-staging.yml        # 스테이징 환경
│   │       └── application-prod.yml           # 운영 환경
│   └── test/
│       └── java/com/eduforum/api/
├── build.gradle.kts                           # Gradle 빌드 설정
├── settings.gradle.kts
├── gradlew / gradlew.bat                      # Gradle Wrapper
├── Dockerfile
└── README.md
```

---

## 3. 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| 언어 | Java | 17 |
| 프레임워크 | Spring Boot | 3.2.1 |
| 보안 | Spring Security | - |
| ORM | Spring Data JPA | - |
| 데이터베이스 | PostgreSQL | 16 |
| JWT | JJWT | 0.12.3 |
| API 문서 | Springdoc OpenAPI | 2.3.0 |
| 빌드 도구 | Gradle (Kotlin DSL) | 8.5 |
| 유틸 | Lombok | - |

---

## 4. 주요 의존성 (build.gradle.kts)

```kotlin
dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
```

---

## 5. 환경 설정

### 5.1 application.yml (공통)

```yaml
server:
  port: 8000
  servlet:
    context-path: /api

spring:
  application:
    name: eduforum-api

jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-for-jwt-token-generation}
  access-token-validity: 900000      # 15분
  refresh-token-validity: 604800000  # 7일

springdoc:
  api-docs:
    path: /v1/api-docs
  swagger-ui:
    path: /docs
```

### 5.2 application-dev.yml (개발 환경)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://210.115.229.12:5432/eduforum
    username: eduforum
    password: eduforum12
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true

cors:
  allowed-origins: http://localhost:3000,http://localhost:5173
```

### 5.3 환경별 설정

| 환경 | 파일 | DB DDL | SQL 로그 | Swagger |
|------|------|--------|----------|---------|
| Development | `application-dev.yml` | validate | true | true |
| Staging | `application-staging.yml` | validate | false | true |
| Production | `application-prod.yml` | none | false | false |

---

## 6. 주요 구현 내용

### 6.1 SecurityConfig

- JWT 기반 인증
- Stateless 세션 관리
- CORS 설정 (프로파일별)
- BCrypt 패스워드 인코딩
- 공개 엔드포인트: `/v1/health/**`, `/docs/**`, `/v1/auth/**`

### 6.2 GlobalExceptionHandler

일관된 에러 응답 형식:

```json
{
  "status": "error",
  "message": "Validation failed",
  "data": null,
  "meta": {
    "errorCode": "VALIDATION_ERROR",
    "errors": ["field: must not be blank"]
  },
  "timestamp": "2025-01-29T10:00:00"
}
```

### 6.3 ApiResponse

일관된 성공 응답 형식:

```json
{
  "status": "success",
  "message": "Success",
  "data": { ... },
  "meta": null,
  "timestamp": "2025-01-29T10:00:00"
}
```

### 6.4 HealthController

| 엔드포인트 | 설명 |
|-----------|------|
| `GET /api/v1/health` | 전체 시스템 상태 |
| `GET /api/v1/health/ready` | Readiness Probe (K8s) |
| `GET /api/v1/health/live` | Liveness Probe (K8s) |

### 6.5 JwtTokenProvider

- Access Token 생성 (15분)
- Refresh Token 생성 (7일)
- 토큰 검증
- Claims 추출

---

## 7. 실행 방법

### 7.1 빌드

```bash
cd /mnt/d/Development/git/minerva/apps/backend

# Gradle 빌드
./gradlew build

# JAR 빌드
./gradlew bootJar
```

### 7.2 실행

```bash
# 개발 모드 실행
./gradlew bootRun

# 또는 JAR 직접 실행
java -jar build/libs/eduforum-api-1.0.0.jar --spring.profiles.active=dev
```

### 7.3 Docker 실행

```bash
# 이미지 빌드
docker build -t eduforum-api:latest .

# 컨테이너 실행
docker run -d -p 8000:8000 \
  -e SPRING_PROFILES_ACTIVE=dev \
  --name eduforum-api \
  eduforum-api:latest
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
| GET | `/api/v1/api-docs` | OpenAPI 스펙 (JSON) |

### 8.2 응답 예시

```bash
# 헬스체크
curl http://localhost:8000/api/v1/health

# 응답
{
  "status": "success",
  "message": "Service is healthy",
  "data": {
    "status": "UP",
    "service": "eduforum-api",
    "timestamp": "2025-01-29T10:00:00"
  },
  "timestamp": "2025-01-29T10:00:00"
}
```

---

## 9. 데이터베이스 연결 정보

| 항목 | 값 |
|------|-----|
| Host | 210.115.229.12 |
| Port | 5432 |
| Database | eduforum |
| Username | eduforum |
| Password | eduforum12 |

---

## 10. NestJS에서 Spring Boot로 전환 이유

| 항목 | NestJS | Spring Boot |
|------|--------|-------------|
| 팀 익숙도 | 낮음 | 높음 |
| 엔터프라이즈 안정성 | 중간 | 높음 |
| 한국 시장 채용 | 어려움 | 용이 |
| 성능 | 중간 | 높음 |

---

## 11. Acceptance Criteria 충족 현황

### Backend 요구사항

- [x] 프로젝트 구조 설정
- [x] 환경 설정 (dev, staging, prod)
- [x] 입력값 유효성 검사 구현 (Bean Validation)
- [x] 에러 핸들링 및 적절한 HTTP 상태 코드 반환
- [x] API 문서 (Swagger/OpenAPI) 설정
- [ ] 단위 테스트 작성 (커버리지 80% 이상) - 후속 작업

### 품질 요구사항

- [x] 코드 리뷰 완료
- [x] 문서화 완료

---

## 12. 다음 단계

| Issue | 작업 | 의존성 |
|-------|------|--------|
| #286 | BE-002: 공통 모듈 설정 | BE-001 ✅ |
| #287 | BE-003: 데이터베이스 연결 설정 | BE-001 ✅ |

---

## 13. 참조 문서

- `docs/02-technical-architecture.md` - 기술 아키텍처
- `apps/backend/README.md` - 프로젝트 README
- `apps/backend/QUICKSTART.md` - 빠른 시작 가이드

---

## 14. 변경 이력

| 버전 | 날짜 | 작성자 | 변경 내용 |
|------|------|--------|----------|
| 1.0 | 2025-01-28 | Claude | NestJS 프로젝트 초기 설정 |
| 2.0 | 2025-01-29 | Claude | Spring Boot로 전환 |

---

**작업 완료**
