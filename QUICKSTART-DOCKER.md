# 빠른 시작 - Docker 배포

5분 안에 Docker로 EduForum을 실행하세요.

## 사전 요구 사항

- Docker Desktop 또는 Docker Engine (20.10+)
- Docker Compose (2.0+)
- 4GB RAM, 10GB 디스크 공간

## 1. 환경 설정

```bash
# 환경 템플릿 복사
cp .env.example .env

# 보안 시크릿 생성 (Linux/macOS)
sed -i "s/your_secure_postgres_password/$(openssl rand -base64 32)/g" .env
sed -i "s/your_jwt_secret_key_minimum_32_characters_long/$(openssl rand -base64 32)/g" .env
sed -i "s/your_nextauth_secret_key_minimum_32_characters_long/$(openssl rand -base64 32)/g" .env
```

또는 `.env`를 직접 편집하여 모든 플레이스홀더 값을 교체하세요.

## 2. 서비스 시작

### 옵션 A: Make 사용 (권장)

```bash
# 프로덕션 모드
make up

# 개발 모드 (핫 리로드)
make dev

# 로그 보기
make logs
```

### 옵션 B: Docker Compose 사용

```bash
# 프로덕션 모드
docker-compose up -d

# 개발 모드
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

## 3. 애플리케이션 접속

- **프론트엔드**: http://localhost:3000
- **백엔드 API**: http://localhost:8080/api
- **API 문서**: http://localhost:8080/api/swagger-ui.html
- **헬스 체크**: http://localhost:8080/api/actuator/health

## 4. 자주 사용하는 명령어

```bash
# 로그 보기
make logs
# 또는
docker-compose logs -f

# 상태 확인
make ps
# 또는
docker-compose ps

# 서비스 중지
make down
# 또는
docker-compose down

# 재시작
make restart
# 또는
docker-compose restart
```

## 문제 해결

### 포트가 이미 사용 중
```bash
# 포트 사용 확인
lsof -i :8080
# 프로세스 종료 또는 docker-compose.yml에서 포트 변경
```

### 서비스가 시작되지 않음
```bash
# 오류 로그 확인
docker-compose logs

# 처음부터 재빌드
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### 데이터베이스 연결 실패
```bash
# 데이터베이스 준비 대기 (30-60초 소요될 수 있음)
docker-compose logs postgres

# 헬스 확인
docker-compose exec postgres pg_isready -U eduforum
```

## 다음 단계

- 상세 문서는 [DOCKER.md](./DOCKER.md) 참조
- 이메일, 파일 스토리지 등 서비스 설정
- 프로덕션용 SSL/TLS 설정
- 백업 및 모니터링 구성

## 개발 모드 기능

`make dev` 또는 `docker-compose.dev.yml` 사용 시:

- **핫 리로드**: 코드 변경이 즉시 반영
- **디버그 포트**: Backend (5005), Frontend (9229)
- **상세 로그**: SQL 쿼리 및 디버그 정보 표시
- **소스 마운트**: 컨테이너 내에서 직접 코드 편집

## 프로덕션 체크리스트

프로덕션 배포 전:

- [ ] `.env`에 강력한 비밀번호 설정
- [ ] SSL/TLS 설정 (nginx/traefik 사용)
- [ ] 데이터베이스 백업 설정
- [ ] 모니터링 구성 (Prometheus/Grafana)
- [ ] 리소스 제한 검토
- [ ] 로그 집계 설정
- [ ] 이메일 서비스 구성
- [ ] 재해 복구 테스트

## 지원

상세 문서:
- [DOCKER.md](./DOCKER.md) - 전체 Docker 가이드
- [README.md](./README.md) - 프로젝트 개요
- `/docs` - 기술 문서
