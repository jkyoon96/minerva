# EduForum API - 프로젝트 구조

## 디렉토리 구조

```
/mnt/d/Development/git/minerva/apps/backend/
├── src/
│   ├── main/
│   │   ├── java/com/eduforum/api/
│   │   │   ├── EduforumApplication.java           # 메인 애플리케이션
│   │   │   ├── config/                            # 설정
│   │   │   │   ├── SecurityConfig.java            # Spring Security + JWT
│   │   │   │   ├── SwaggerConfig.java             # OpenAPI/Swagger
│   │   │   │   ├── JpaConfig.java                 # JPA 설정
│   │   │   │   └── WebConfig.java                 # CORS 설정
│   │   │   ├── common/                            # 공통 모듈
│   │   │   │   ├── dto/
│   │   │   │   │   └── ApiResponse.java           # API 응답 래퍼
│   │   │   │   └── exception/
│   │   │   │       ├── ErrorCode.java             # 에러 코드
│   │   │   │       ├── BusinessException.java     # 비즈니스 예외
│   │   │   │       └── GlobalExceptionHandler.java # 전역 예외 처리
│   │   │   ├── domain/                            # 도메인
│   │   │   │   └── health/
│   │   │   │       └── HealthController.java      # 헬스 체크 API
│   │   │   └── security/                          # 보안
│   │   │       └── jwt/
│   │   │           ├── JwtTokenProvider.java      # JWT 토큰 생성/검증
│   │   │           └── JwtAuthenticationFilter.java # JWT 인증 필터
│   │   └── resources/
│   │       ├── application.yml                    # 공통 설정
│   │       ├── application-dev.yml                # 개발 환경
│   │       ├── application-staging.yml            # 스테이징 환경
│   │       └── application-prod.yml               # 프로덕션 환경
│   └── test/
│       ├── java/com/eduforum/api/
│       │   └── EduforumApplicationTests.java      # 기본 테스트
│       └── resources/
│           └── application-test.yml               # 테스트 설정
├── gradle/wrapper/                                # Gradle Wrapper
│   └── gradle-wrapper.properties
├── build.gradle.kts                               # Gradle 빌드 설정
├── settings.gradle.kts                            # Gradle 설정
├── gradlew                                        # Gradle Wrapper (Unix)
├── gradlew.bat                                    # Gradle Wrapper (Windows)
├── Dockerfile                                     # Docker 이미지
├── docker-compose.yml                             # Docker Compose
├── .gitignore                                     # Git ignore
├── .dockerignore                                  # Docker ignore
├── README.md                                      # 프로젝트 개요
├── QUICKSTART.md                                  # 빠른 시작 가이드
└── PROJECT-STRUCTURE.md                           # 이 파일
```

## 파일 목록 (24개)

### Java 소스 파일 (11개)
1. `EduforumApplication.java` - Spring Boot 메인 클래스
2. `SecurityConfig.java` - Spring Security 설정
3. `SwaggerConfig.java` - OpenAPI 3.0 설정
4. `JpaConfig.java` - JPA 리포지토리 설정
5. `WebConfig.java` - CORS 설정
6. `ApiResponse.java` - 공통 API 응답 DTO
7. `ErrorCode.java` - 에러 코드 enum
8. `BusinessException.java` - 비즈니스 예외
9. `GlobalExceptionHandler.java` - 전역 예외 핸들러
10. `HealthController.java` - 헬스 체크 컨트롤러
11. `JwtTokenProvider.java` - JWT 토큰 생성/검증

### 추가 Java 파일 (2개)
12. `JwtAuthenticationFilter.java` - JWT 인증 필터
13. `EduforumApplicationTests.java` - 테스트 클래스

### 설정 파일 (5개)
14. `application.yml` - 공통 설정
15. `application-dev.yml` - 개발 환경 설정
16. `application-staging.yml` - 스테이징 환경 설정
17. `application-prod.yml` - 프로덕션 환경 설정
18. `application-test.yml` - 테스트 환경 설정

### 빌드 파일 (2개)
19. `build.gradle.kts` - Gradle 빌드 설정 (Kotlin DSL)
20. `settings.gradle.kts` - Gradle 프로젝트 설정

### 문서 파일 (3개)
21. `README.md` - 프로젝트 개요 및 사용법
22. `QUICKSTART.md` - 빠른 시작 가이드
23. `PROJECT-STRUCTURE.md` - 프로젝트 구조 설명

### Docker 파일 (2개)
24. `Dockerfile` - Multi-stage Docker 빌드
25. `docker-compose.yml` - Docker Compose 설정

### 기타 파일
- `.gitignore` - Git ignore 설정
- `.dockerignore` - Docker ignore 설정
- `gradlew`, `gradlew.bat` - Gradle Wrapper 실행 파일
- `gradle/wrapper/gradle-wrapper.properties` - Gradle Wrapper 설정

## 주요 기능

### 1. 인증 및 보안
- JWT 기반 인증 (JJWT 0.12.3)
- Spring Security 설정
- 비밀번호 암호화 (BCrypt)
- CORS 설정

### 2. API 문서화
- SpringDoc OpenAPI 3.0
- Swagger UI 통합
- 자동 API 문서 생성

### 3. 데이터베이스
- PostgreSQL 연동
- Spring Data JPA
- Hibernate
- 환경별 데이터베이스 설정

### 4. 예외 처리
- 전역 예외 핸들러
- 커스텀 에러 코드
- 표준화된 에러 응답

### 5. 헬스 체크
- Actuator 통합
- Kubernetes 프로브 지원 (Readiness, Liveness)
- 커스텀 헬스 체크 엔드포인트

### 6. 프로파일 관리
- 환경별 설정 분리 (dev, staging, prod, test)
- 환경 변수 지원
- 안전한 비밀 관리

## API 엔드포인트

### Public Endpoints (인증 불필요)
- `GET /api/v1/health` - 헬스 체크
- `GET /api/v1/health/ready` - Readiness 프로브
- `GET /api/v1/health/live` - Liveness 프로브
- `GET /api/docs/**` - API 문서
- `POST /api/v1/auth/**` - 인증 관련 (예정)

### Protected Endpoints (인증 필요)
- 추후 추가 예정

## 기술 스택

| 카테고리 | 기술 | 버전 |
|---------|------|------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.1 |
| Security | Spring Security + JWT | - |
| Database | PostgreSQL | 15+ |
| ORM | Spring Data JPA | - |
| API Docs | SpringDoc OpenAPI | 2.3.0 |
| JWT | JJWT | 0.12.3 |
| Build | Gradle | 8.5 |
| Container | Docker | - |

## 환경 설정

### 개발 환경 (dev)
- 포트: 8000
- DB: 210.115.229.12:5432/eduforum
- 로그 레벨: DEBUG
- DDL: update

### 스테이징 환경 (staging)
- 환경 변수 기반
- 로그 레벨: INFO
- DDL: validate

### 프로덕션 환경 (prod)
- 환경 변수 기반
- 로그 레벨: WARN
- DDL: validate
- 헬스 체크 상세 정보 숨김

### 테스트 환경 (test)
- H2 인메모리 DB
- DDL: create-drop

## 빌드 및 실행

### 로컬 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Docker 실행
```bash
docker-compose up -d
```

### 빌드
```bash
./gradlew clean build
```

### 테스트
```bash
./gradlew test
```

## 다음 단계

1. **도메인 모델 추가**
   - User, Course, Session 등 엔티티 작성
   - Repository, Service, Controller 계층 구현

2. **인증 기능 구현**
   - 회원가입, 로그인 API
   - JWT 토큰 발급 및 갱신
   - 권한 관리 (RBAC)

3. **코어 기능 개발**
   - 코스 관리
   - 실시간 세미나
   - 액티브 러닝 도구
   - 평가 시스템

4. **테스트 작성**
   - Unit 테스트
   - Integration 테스트
   - E2E 테스트

5. **배포 설정**
   - CI/CD 파이프라인
   - Kubernetes 매니페스트
   - 모니터링 및 로깅

## 참고 문서

- [README.md](README.md) - 프로젝트 개요
- [QUICKSTART.md](QUICKSTART.md) - 빠른 시작 가이드
- [API 설계서](../../docs/api-design.md)
- [데이터베이스 설계](../../docs/db-design.md)
- [기술 아키텍처](../../docs/02-technical-architecture.md)

## 라이선스

MIT License
