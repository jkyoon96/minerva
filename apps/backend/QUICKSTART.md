# EduForum API 빠른 시작 가이드

이 문서는 EduForum API를 빠르게 시작하기 위한 단계별 가이드입니다.

## 사전 준비

### 1. 개발 환경 확인

다음 소프트웨어가 설치되어 있어야 합니다:

```bash
# Java 버전 확인 (17 이상)
java -version

# Gradle 버전 확인 (8.5 이상, 또는 Gradle Wrapper 사용)
./gradlew -version
```

Java 17이 없다면 다음에서 다운로드:
- [Eclipse Temurin](https://adoptium.net/)
- [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)

### 2. PostgreSQL 설치 및 설정

#### PostgreSQL 설치

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

**macOS:**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Windows:**
[PostgreSQL 공식 사이트](https://www.postgresql.org/download/windows/)에서 설치

#### 데이터베이스 생성

```bash
# PostgreSQL 접속
sudo -u postgres psql

# 또는
psql -U postgres
```

```sql
-- 데이터베이스 생성
CREATE DATABASE eduforum;

-- 사용자 생성 및 권한 부여
CREATE USER eduforum WITH PASSWORD 'eduforum12';
GRANT ALL PRIVILEGES ON DATABASE eduforum TO eduforum;

-- PostgreSQL 15 이상인 경우 추가 권한 필요
\c eduforum
GRANT ALL ON SCHEMA public TO eduforum;
```

## 빠른 시작 (3단계)

### 1단계: 프로젝트 클론 및 이동

```bash
cd /mnt/d/Development/git/minerva/apps/backend
```

### 2단계: 빌드

```bash
# Gradle Wrapper를 사용한 빌드 (권장)
./gradlew clean build

# 또는 테스트 제외하고 빌드
./gradlew clean build -x test
```

빌드가 성공하면 `build/libs/` 디렉토리에 JAR 파일이 생성됩니다.

### 3단계: 실행

```bash
# 방법 1: Gradle을 통한 실행 (개발 시 권장)
./gradlew bootRun --args='--spring.profiles.active=dev'

# 방법 2: JAR 파일 직접 실행
java -jar build/libs/eduforum-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## 실행 확인

### 1. 헬스 체크

애플리케이션이 정상적으로 실행되었는지 확인:

```bash
# 기본 헬스 체크
curl http://localhost:8000/api/v1/health

# 예상 응답
{
  "status": 200,
  "message": "Success",
  "data": {
    "status": "UP",
    "application": "EduForum API",
    "profile": "dev",
    "timestamp": "2024-01-01T12:00:00"
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 2. Swagger UI 접속

브라우저에서 다음 URL 접속:

```
http://localhost:8000/api/docs/swagger-ui.html
```

Swagger UI에서 모든 API 엔드포인트를 확인하고 테스트할 수 있습니다.

### 3. API 문서 확인

```bash
# OpenAPI JSON
curl http://localhost:8000/api/docs/api-docs

# OpenAPI YAML
curl http://localhost:8000/api/docs/api-docs.yaml
```

## 개발 환경 설정

### 프로파일별 설정

#### 개발 환경 (dev)

`src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://210.115.229.12:5432/eduforum
    username: eduforum
    password: eduforum12
```

#### 로컬 환경 (선택사항)

로컬 데이터베이스를 사용하려면 `application-local.yml` 생성:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/eduforum
    username: eduforum
    password: eduforum12
```

실행:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 주요 설정 파일

### application.yml (공통 설정)

```yaml
server:
  port: 8000                    # 서버 포트
  servlet:
    context-path: /api          # API 기본 경로

jwt:
  secret: your-secret-key       # JWT 비밀키
  access-token-validity-in-seconds: 3600   # 액세스 토큰 유효 시간 (1시간)
  refresh-token-validity-in-seconds: 1209600  # 리프레시 토큰 유효 시간 (14일)
```

### JWT Secret 변경 (중요!)

프로덕션 환경에서는 반드시 JWT Secret을 변경해야 합니다:

```bash
# 환경 변수로 설정
export JWT_SECRET=your-secure-secret-key-at-least-256-bits

# 실행
java -jar app.jar --spring.profiles.active=prod
```

## 일반적인 문제 해결

### 1. 포트 충돌

```
Port 8000 was already in use
```

**해결책:**
- 8000 포트를 사용 중인 프로세스 종료
- 또는 `application.yml`에서 포트 변경:
  ```yaml
  server:
    port: 8080
  ```

### 2. 데이터베이스 연결 실패

```
Unable to acquire JDBC Connection
```

**해결책:**
1. PostgreSQL 서비스 실행 여부 확인:
   ```bash
   sudo systemctl status postgresql
   # 또는
   brew services list
   ```

2. 데이터베이스 연결 정보 확인:
   ```bash
   psql -U eduforum -d eduforum -h localhost
   ```

3. `application-dev.yml`의 연결 정보 확인

### 3. Gradle 빌드 실패

```
Could not resolve dependencies
```

**해결책:**
```bash
# Gradle 캐시 삭제
./gradlew clean --refresh-dependencies

# 다시 빌드
./gradlew build
```

### 4. Java 버전 불일치

```
Unsupported class file major version
```

**해결책:**
- Java 17 설치 확인
- `JAVA_HOME` 환경 변수 설정:
  ```bash
  export JAVA_HOME=/path/to/java17
  ```

## 개발 워크플로우

### 1. 코드 변경 후 재시작

Gradle을 사용하면 자동으로 재컴파일됩니다:

```bash
./gradlew bootRun
```

### 2. 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트 클래스
./gradlew test --tests EduforumApplicationTests

# 테스트 결과 확인
open build/reports/tests/test/index.html
```

### 3. 코드 포맷팅

```bash
# Gradle 플러그인 추가 시
./gradlew spotlessApply
```

## 다음 단계

1. **API 개발**: 도메인별 Controller, Service, Repository 작성
2. **인증 구현**: User 엔티티 및 인증 로직 추가
3. **데이터베이스 마이그레이션**: Flyway 또는 Liquibase 설정
4. **테스트 작성**: Unit 테스트 및 Integration 테스트
5. **배포 준비**: Docker Compose, Kubernetes 설정

## 추가 리소스

### 프로젝트 문서

- [README.md](README.md) - 프로젝트 개요
- [API 설계서](../../docs/api-design.md)
- [데이터베이스 설계](../../docs/db-design.md)

### Spring Boot 공식 문서

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/3.2.1/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

### 도구

- [Postman](https://www.postman.com/) - API 테스트
- [DBeaver](https://dbeaver.io/) - 데이터베이스 GUI
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - Java IDE (권장)

## 도움말

문제가 발생하면 다음을 확인하세요:

1. 로그 확인: 콘솔 출력 또는 `logs/` 디렉토리
2. 데이터베이스 연결 테스트
3. JWT 설정 확인
4. 포트 충돌 확인

추가 문의: dev@eduforum.com
