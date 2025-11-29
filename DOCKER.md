# Docker 배포 가이드 - EduForum/Minerva

이 가이드는 Docker와 Docker Compose를 사용하여 EduForum 애플리케이션을 실행하는 방법을 설명합니다.

## 사전 요구 사항

- Docker 20.10 이상
- Docker Compose 2.0 이상
- 최소 4GB의 가용 RAM
- 10GB의 여유 디스크 공간

## 빠른 시작

### 1. 복제 및 설정

```bash
# 저장소 복제
git clone <repository-url>
cd minerva

# 환경 파일 복사
cp .env.example .env

# .env를 편집하고 다음 값들을 안전하게 설정:
# - POSTGRES_PASSWORD
# - JWT_SECRET
# - NEXTAUTH_SECRET
nano .env
```

### 2. 프로덕션 배포

```bash
# 모든 서비스 빌드 및 시작
docker-compose up -d

# 로그 보기
docker-compose logs -f

# 서비스 상태 확인
docker-compose ps
```

애플리케이션 접속:
- 프론트엔드: http://localhost:3000
- 백엔드 API: http://localhost:8080/api
- 백엔드 헬스: http://localhost:8080/api/actuator/health
- API 문서: http://localhost:8080/api/swagger-ui.html

### 3. 개발 모드

핫 리로드가 적용된 개발 모드:

```bash
# 개발 오버라이드와 함께 시작
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# 또는 단축 명령어 사용
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

개발 모드 기능:
- 프론트엔드와 백엔드 모두 핫 리로드
- 디버그 포트 노출 (Backend: 5005, Frontend: 9229)
- 상세 로깅 활성화
- 데이터베이스 스키마 자동 업데이트
- 소스 코드 볼륨 마운트

## 서비스 아키텍처

### 서비스 개요

| 서비스 | 포트 | 설명 |
|--------|------|------|
| postgres | 5432 | PostgreSQL 15 데이터베이스 |
| backend | 8080 | Spring Boot API 서버 |
| frontend | 3000 | Next.js 웹 애플리케이션 |

### 네트워크

모든 서비스는 `eduforum-network` 브릿지 네트워크를 통해 통신합니다:
- 프론트엔드 → 백엔드: `http://backend:8080/api`
- 백엔드 → 데이터베이스: `postgresql://postgres:5432/eduforum`

### 볼륨

- `postgres_data`: 영구 데이터베이스 저장소

## 설정

### 환경 변수

#### 데이터베이스
- `POSTGRES_PASSWORD`: PostgreSQL 비밀번호 (필수)

#### 백엔드
- `SPRING_PROFILES_ACTIVE`: Spring 프로파일 (prod/dev)
- `JWT_SECRET`: JWT 토큰용 시크릿 키 (최소 32자)
- `JWT_EXPIRATION`: 토큰 만료 시간 (밀리초)
- `CORS_ALLOWED_ORIGINS`: 쉼표로 구분된 허용 출처

#### 프론트엔드
- `NEXT_PUBLIC_API_URL`: 공개 API URL
- `API_URL`: 내부 API URL (서버 사이드 호출용)
- `NEXTAUTH_URL`: NextAuth 콜백 URL
- `NEXTAUTH_SECRET`: NextAuth 암호화 시크릿

### 헬스체크

모든 서비스는 헬스체크를 포함합니다:
- **PostgreSQL**: 10초마다 `pg_isready` 체크
- **Backend**: 30초마다 Spring Actuator 헬스 엔드포인트
- **Frontend**: 30초마다 커스텀 헬스 엔드포인트

## 자주 사용하는 명령어

### 서비스 시작
```bash
# 모든 서비스 시작
docker-compose up -d

# 특정 서비스만 시작
docker-compose up -d backend
```

### 서비스 중지
```bash
# 모든 서비스 중지
docker-compose down

# 볼륨과 함께 중지 (데이터 삭제)
docker-compose down -v
```

### 로그 보기
```bash
# 모든 서비스
docker-compose logs -f

# 특정 서비스
docker-compose logs -f backend

# 마지막 100줄
docker-compose logs --tail=100 backend
```

### 서비스 재빌드
```bash
# 모든 이미지 재빌드
docker-compose build

# 특정 서비스 재빌드
docker-compose build backend

# 캐시 없이 재빌드
docker-compose build --no-cache
```

### 명령어 실행
```bash
# 백엔드 쉘
docker-compose exec backend sh

# 데이터베이스 쉘
docker-compose exec postgres psql -U eduforum -d eduforum

# 데이터베이스 마이그레이션 실행
docker-compose exec backend java -jar app.jar --spring.profiles.active=prod db migrate
```

### 서비스 스케일링 (필요시)
```bash
# 백엔드를 3개 인스턴스로 스케일
docker-compose up -d --scale backend=3
```

## 데이터베이스 관리

### 데이터베이스 백업
```bash
# 백업 생성
docker-compose exec postgres pg_dump -U eduforum eduforum > backup_$(date +%Y%m%d_%H%M%S).sql

# gzip 압축 백업
docker-compose exec postgres pg_dump -U eduforum eduforum | gzip > backup_$(date +%Y%m%d_%H%M%S).sql.gz
```

### 데이터베이스 복원
```bash
# 연결 방지를 위해 백엔드 중지
docker-compose stop backend

# 백업에서 복원
docker-compose exec -T postgres psql -U eduforum -d eduforum < backup_20240101_120000.sql

# gzip 백업에서 복원
gunzip -c backup_20240101_120000.sql.gz | docker-compose exec -T postgres psql -U eduforum -d eduforum

# 백엔드 시작
docker-compose start backend
```

### 데이터베이스 접속
```bash
# 대화형 psql
docker-compose exec postgres psql -U eduforum -d eduforum

# SQL 파일 실행
docker-compose exec -T postgres psql -U eduforum -d eduforum < scripts/migration.sql
```

## 디버깅

### 백엔드 디버깅

개발 모드에서 백엔드는 원격 디버깅을 위해 5005 포트를 노출합니다:

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Add → Remote JVM Debug
3. Host: localhost, Port: 5005
4. Apply 후 Debug

**VS Code:**
```json
{
  "type": "java",
  "request": "attach",
  "name": "백엔드 디버그",
  "hostName": "localhost",
  "port": 5005
}
```

### 프론트엔드 디버깅

개발 모드에서 Node.js 디버깅을 위해 9229 포트를 노출합니다:

**VS Code:**
```json
{
  "type": "node",
  "request": "attach",
  "name": "프론트엔드 디버그",
  "port": 9229,
  "restart": true,
  "sourceMaps": true
}
```

### 컨테이너 통계 보기
```bash
# 실시간 통계
docker stats

# 특정 서비스
docker stats eduforum-backend
```

## 프로덕션 모범 사례

### 1. 보안

```bash
# 강력한 비밀번호 사용
POSTGRES_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 32)
NEXTAUTH_SECRET=$(openssl rand -base64 32)

# .env 파일 커밋하지 않기
echo ".env" >> .gitignore
```

### 2. SSL/TLS

HTTPS를 위해 리버스 프록시 (nginx/traefik) 사용:

```yaml
# docker-compose.prod.yml
services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
```

### 3. 리소스 제한

```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

### 4. 로깅

```yaml
services:
  backend:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### 5. 모니터링

다음 추가 고려:
- 메트릭을 위한 Prometheus
- 시각화를 위한 Grafana
- 로그 집계를 위한 ELK 스택

## 문제 해결

### 서비스가 시작되지 않음

```bash
# 로그 확인
docker-compose logs

# 서비스 상태 확인
docker-compose ps

# 처음부터 재빌드
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### 데이터베이스 연결 문제

```bash
# 데이터베이스 실행 확인
docker-compose exec postgres pg_isready -U eduforum

# 백엔드가 데이터베이스에 도달할 수 있는지 확인
docker-compose exec backend ping postgres

# 데이터베이스 로그 보기
docker-compose logs postgres
```

### 프론트엔드가 백엔드에 도달할 수 없음

```bash
# 백엔드 헬스 확인
curl http://localhost:8080/api/actuator/health

# 프론트엔드 컨테이너에서 확인
docker-compose exec frontend wget -O- http://backend:8080/api/actuator/health

# 네트워크 확인
docker network inspect eduforum-network
```

### 메모리 부족

```bash
# 메모리 사용량 확인
docker stats

# Docker 메모리 증가 (Docker Desktop)
# Settings → Resources → Memory → 8GB

# 또는 서비스에 메모리 제한 추가
```

### 포트가 이미 사용 중

```bash
# 포트 사용 확인
lsof -i :8080
netstat -tulpn | grep 8080

# 다른 포트 사용
docker-compose -f docker-compose.yml -p eduforum up -d
```

## CI/CD 통합

### GitHub Actions 예시

```yaml
name: Docker 빌드 및 푸시

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: 이미지 빌드
        run: docker-compose build

      - name: 테스트 실행
        run: docker-compose run --rm backend ./gradlew test

      - name: 레지스트리에 푸시
        run: |
          docker-compose push
```

## 업데이트

### 이미지 업데이트

```bash
# 최신 베이스 이미지 가져오기
docker-compose pull

# 최신 의존성으로 재빌드
docker-compose build --pull

# 서비스 재시작
docker-compose up -d
```

### 애플리케이션 업데이트

```bash
# 최신 코드 가져오기
git pull

# 재빌드 및 재시작
docker-compose up -d --build
```

## 정리

```bash
# 모든 컨테이너 및 볼륨 제거
docker-compose down -v

# 사용하지 않는 이미지 제거
docker image prune -a

# 모든 것 제거 (위험!)
docker system prune -a --volumes
```

## 지원

문제 및 질문:
- 로그 확인: `docker-compose logs`
- 헬스체크 검토: `docker-compose ps`
- 문서 참조: `/docs` 디렉토리
- GitHub Issues: [repository-url]/issues

## 추가 자료

- [Docker 공식 문서](https://docs.docker.com/)
- [Docker Compose 문서](https://docs.docker.com/compose/)
- [Spring Boot Docker 가이드](https://spring.io/guides/gs/spring-boot-docker/)
- [Next.js Docker 가이드](https://nextjs.org/docs/deployment#docker-image)
