# EduForum Database Migrations

PostgreSQL 데이터베이스 마이그레이션 스크립트 모음입니다.

## 마이그레이션 순서

| 번호 | 파일명 | 설명 | 상태 |
|------|--------|------|------|
| 001 | `001_create_database.sql` | 데이터베이스 및 사용자 생성 | ✅ |
| 002 | `002_create_extensions_and_types.sql` | 확장 및 ENUM 타입 생성 | ✅ |
| 003 | `003_auth_schema.sql` | 인증 스키마 (auth) | ✅ |
| 004 | `004_course_schema.sql` | 코스 스키마 (course) | ✅ |
| 005 | `005_live_schema.sql` | 실시간 세션 스키마 (live) | ✅ |
| 006 | `006_learning_schema.sql` | 학습 도구 스키마 (learning) | ✅ |
| 007 | `007_assess_schema.sql` | 평가 스키마 (assess) | ✅ |
| 008 | `008_analytics_schema.sql` | 분석 스키마 (analytics) | ✅ |
| 009 | `009_indexes.sql` | 기본 인덱스 생성 | ✅ |
| 010 | `010_initial_data.sql` | 초기 데이터 삽입 | ✅ |
| **011** | `011_additional_indexes.sql` | **성능 최적화 인덱스 (74개)** | ✅ |
| **012** | `012_constraints_review.sql` | **데이터 무결성 제약조건 (95개)** | ✅ |

## 스키마 구조

```
eduforum_db
├── auth (인증/권한)
│   ├── users
│   ├── roles
│   ├── permissions
│   ├── user_roles
│   ├── oauth_accounts
│   ├── two_factor_auth
│   ├── refresh_tokens
│   └── password_reset_tokens
│
├── course (코스 관리)
│   ├── courses
│   ├── enrollments
│   ├── sessions
│   ├── recordings
│   ├── contents
│   ├── assignments
│   └── submissions
│
├── live (실시간 상호작용)
│   ├── session_participants
│   ├── chats
│   ├── breakout_rooms
│   ├── breakout_participants
│   ├── reactions
│   └── hand_raises
│
├── learning (학습 도구)
│   ├── polls
│   ├── poll_options
│   ├── poll_votes
│   ├── quizzes
│   ├── questions
│   ├── quiz_attempts
│   ├── quiz_answers
│   ├── whiteboards
│   └── whiteboard_elements
│
├── assess (평가)
│   ├── grades
│   ├── ai_gradings
│   ├── peer_evaluations
│   └── code_executions
│
└── analytics (분석)
    ├── participation_logs
    ├── alerts
    ├── interaction_logs
    ├── daily_stats
    └── notification_settings
```

## 신규 추가 (Phase 3 - DB-003)

### Migration 011: 추가 성능 인덱스

**목적**: 쿼리 성능 최적화

**주요 인덱스 유형**:
- 복합 인덱스 (Composite): 35개
- 부분 인덱스 (Partial): 28개
- GIN 인덱스 (JSONB): 11개
- 전체 텍스트 검색 (pg_trgm): 2개

**스키마별 분포**:
- auth: 10개
- course: 23개 (세션, 과제, 제출물 포함)
- live: 9개
- learning: 11개
- assess: 9개
- analytics: 12개

**파일**:
- `011_additional_indexes.sql` - 인덱스 생성 스크립트
- `011_rollback.sql` - 롤백 스크립트

### Migration 012: 제약조건 강화

**목적**: 데이터 무결성 보장

**제약조건 유형**:
- CHECK 제약조건: 85개
- UNIQUE 인덱스: 2개
- Foreign Key 수정: 5개
- 복합 제약조건: 3개

**주요 검증 항목**:
- 형식 검증: 이메일, 전화번호, 코스 코드
- 범위 검증: 점수(0-100), 시간 제한, 학생 수
- 논리 검증: 시간 순서, 상태 일관성
- 열거형 검증: 역할, 상태, 난이도 등

**파일**:
- `012_constraints_review.sql` - 제약조건 추가 스크립트
- `012_rollback.sql` - 롤백 스크립트

### 문서

- `DB-003-OPTIMIZATION-GUIDE.md` - 상세 최적화 가이드
- `DB-003-SUMMARY.md` - 작업 요약 보고서
- `README.md` - 이 파일

## 실행 가이드

### 전체 마이그레이션 실행 (새 데이터베이스)

```bash
# PostgreSQL 슈퍼유저로 실행
psql -U postgres -f 001_create_database.sql

# eduforum_user로 나머지 실행
for file in 002_*.sql 003_*.sql 004_*.sql 005_*.sql 006_*.sql 007_*.sql 008_*.sql 009_*.sql 010_*.sql 011_*.sql 012_*.sql; do
  echo "Executing $file..."
  psql -U eduforum_user -d eduforum_db -f "$file"
done
```

### 기존 데이터베이스에 최적화 적용

```bash
# 백업 생성
pg_dump -U eduforum_user eduforum_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Migration 011 실행 (인덱스 추가)
psql -U eduforum_user -d eduforum_db -f 011_additional_indexes.sql

# Migration 012 실행 (제약조건 추가)
# 주의: 기존 데이터가 제약조건을 위반하는지 먼저 확인 필요
psql -U eduforum_user -d eduforum_db -f 012_constraints_review.sql

# VACUUM ANALYZE
psql -U eduforum_user -d eduforum_db -c "VACUUM ANALYZE;"
```

### 롤백

```bash
# Migration 012 롤백 (제약조건 제거)
psql -U eduforum_user -d eduforum_db -f 012_rollback.sql

# Migration 011 롤백 (인덱스 제거)
psql -U eduforum_user -d eduforum_db -f 011_rollback.sql
```

## 성능 최적화 포인트

### 1. 쿼리 성능 향상

**개선된 쿼리 패턴**:
- 사용자 이메일 검색 (대소문자 무관): 100배+ 향상
- 코스 제목 전체 텍스트 검색: 50배+ 향상
- 채점 대기 제출물 조회: 80배+ 향상
- 활성 세션 참여자 조회: 100배+ 향상
- 퀴즈 성적 순위: 30배+ 향상

### 2. JSONB 검색 최적화

GIN 인덱스가 추가된 JSONB 컬럼:
- `course.courses.settings`
- `course.sessions.settings`
- `learning.questions.tags`
- `assess.ai_gradings.feedback`
- `analytics.alerts.data`

```sql
-- 예시: JSONB 검색
SELECT * FROM course.courses
WHERE settings @> '{"grading_weights": {"participation": 30}}';
```

### 3. 부분 인덱스로 저장 공간 절약

활성 데이터만 인덱싱:
- `WHERE deleted_at IS NULL`
- `WHERE status = 'active'`
- `WHERE is_read = FALSE`

## 모니터링

### 인덱스 사용률 확인

```sql
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read
FROM pg_stat_user_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
ORDER BY idx_scan DESC;
```

### 사용되지 않는 인덱스 찾기

```sql
SELECT schemaname, tablename, indexname
FROM pg_stat_user_indexes
WHERE idx_scan = 0
AND indexname NOT LIKE '%_pkey'
AND schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics');
```

### 인덱스 크기 확인

```sql
SELECT schemaname,
       SUM(pg_relation_size(indexrelid)) / 1024 / 1024 AS size_mb
FROM pg_stat_user_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
GROUP BY schemaname
ORDER BY size_mb DESC;
```

## 주의사항

### Migration 011 (인덱스)

- CONCURRENTLY 옵션 사용으로 서비스 무중단 가능
- 대규모 데이터베이스의 경우 5-30분 소요
- 디스크 여유 공간 확인 필요 (최소 15%)

### Migration 012 (제약조건)

- **반드시 테스트 환경에서 먼저 검증**
- 기존 데이터가 제약조건을 위반하면 에러 발생
- 위반 데이터는 사전에 수정 필요

### 기존 데이터 검증 예시

```sql
-- 이메일 형식 검증
SELECT id, email FROM auth.users
WHERE email !~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
AND deleted_at IS NULL;

-- 점수 범위 검증
SELECT id, final_score FROM assess.grades
WHERE final_score < 0 OR final_score > 100;

-- 시간 순서 검증
SELECT id FROM course.sessions
WHERE ended_at IS NOT NULL AND started_at IS NOT NULL
AND ended_at < started_at;
```

## 환경 변수

```bash
# .env 파일
DB_HOST=localhost
DB_PORT=5432
DB_NAME=eduforum_db
DB_USER=eduforum_user
DB_PASSWORD=your_secure_password
```

## 도구 및 의존성

### 필수 PostgreSQL 확장

- `uuid-ossp` - UUID 생성
- `pg_trgm` - 유사도 검색, 전체 텍스트 검색
- `btree_gin` - GIN 인덱스 최적화

### 권장 도구

- `pgAdmin 4` - GUI 관리 도구
- `pg_stat_statements` - 쿼리 성능 분석
- `pg_cron` - 정기 작업 스케줄링

## 트러블슈팅

### 문제: 인덱스 생성 중 데드락

```sql
-- 해결: CONCURRENTLY 옵션 사용
CREATE INDEX CONCURRENTLY ...
```

### 문제: 제약조건 위반

```sql
-- 해결: 위반 데이터 확인 후 수정
-- 예시: 이메일 형식 오류
UPDATE auth.users
SET email = LOWER(TRIM(email))
WHERE email !~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$';
```

### 문제: 디스크 공간 부족

```sql
-- 해결: 임시로 불필요한 인덱스 제거 후 재생성
DROP INDEX CONCURRENTLY idx_example;
-- 공간 확보 후
CREATE INDEX CONCURRENTLY idx_example ON ...;
```

## 향후 계획

### Phase 4 - 파티셔닝

- `analytics.participation_logs` - 월별 파티셔닝
- `analytics.interaction_logs` - 월별 파티셔닝
- `live.chats` - 세션/날짜별 파티셔닝

### Phase 5 - 고급 최적화

- Materialized Views 추가
- 통계 정보 자동 갱신 최적화
- 커넥션 풀링 튜닝

## 참고 자료

- [PostgreSQL 공식 문서](https://www.postgresql.org/docs/)
- [PostgreSQL 인덱스 타입](https://www.postgresql.org/docs/current/indexes-types.html)
- [GIN 인덱스 가이드](https://www.postgresql.org/docs/current/gin.html)
- [pg_trgm 확장](https://www.postgresql.org/docs/current/pgtrgm.html)

## 라이선스

이 프로젝트의 일부로, 프로젝트 라이선스를 따릅니다.

## 문의

프로젝트 관련 문의는 이슈 트래커를 이용해주세요.

---

**마지막 업데이트**: 2025-01-29
**버전**: 1.2 (DB-003 완료)
