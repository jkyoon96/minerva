# DB-003: 인덱스 및 제약조건 최적화 가이드

## 개요

Phase 3 - DB-003 작업으로 추가된 성능 최적화 인덱스와 데이터 무결성 제약조건에 대한 가이드입니다.

## 작업 완료 파일

| 파일명 | 목적 | 인덱스/제약조건 수 |
|--------|------|-------------------|
| `011_additional_indexes.sql` | 성능 최적화 인덱스 추가 | 74개 인덱스 |
| `011_rollback.sql` | 011 마이그레이션 롤백 | - |
| `012_constraints_review.sql` | 데이터 무결성 제약조건 추가 | 95개 제약조건 |
| `012_rollback.sql` | 012 마이그레이션 롤백 | - |

## Migration 011: 추가 성능 인덱스

### 실행 방법

```bash
# 프로덕션 환경 (CONCURRENTLY 옵션으로 안전하게 실행)
psql -U eduforum_user -d eduforum_db -f database/migrations/011_additional_indexes.sql

# 예상 실행 시간: 대규모 데이터베이스의 경우 5-30분 소요 가능
# CONCURRENTLY 옵션으로 인해 서비스 중단 없이 실행 가능
```

### 주요 인덱스 카테고리

#### 1. 인증 관련 (10개 인덱스)

- **이메일 검색 최적화**: 대소문자 구분 없는 검색
- **토큰 조회**: 리프레시 토큰, 비밀번호 리셋 토큰
- **사용자 상태별 조회**: 활성/비활성 사용자, 미인증 사용자

```sql
-- 예시: 이메일로 사용자 찾기 (대소문자 무관)
SELECT * FROM auth.users
WHERE LOWER(email) = LOWER('user@example.com')
AND deleted_at IS NULL;
-- idx_users_email_lower 사용
```

#### 2. 코스 관련 (8개 인덱스)

- **전체 텍스트 검색**: 코스 제목 검색 (pg_trgm)
- **학기/년도별 조회**: 게시된 코스 필터링
- **초대 코드 검증**: 유효한 초대 코드 확인

```sql
-- 예시: 코스 제목 검색
SELECT * FROM course.courses
WHERE title ILIKE '%machine learning%'
AND deleted_at IS NULL;
-- idx_courses_title_trgm 사용 (GIN 인덱스)
```

#### 3. 세션 관련 (8개 인덱스)

- **다가오는 세션**: 예정된 세션 조회
- **활성 세션**: 현재 진행 중인 세션
- **녹화 상태**: 처리 중/완료된 녹화 파일

```sql
-- 예시: 다가오는 세션 조회
SELECT * FROM course.sessions
WHERE course_id = 1
AND scheduled_at > NOW()
AND status IN ('scheduled', 'in_progress');
-- idx_sessions_upcoming 사용
```

#### 4. 과제 및 제출물 (7개 인덱스)

- **채점 대기**: 제출되었으나 미채점된 과제
- **지각 제출**: 마감일 이후 제출물 추적
- **사용자 제출 이력**: 재시도 내역

```sql
-- 예시: 채점 필요한 제출물
SELECT * FROM course.submissions
WHERE assignment_id = 123
AND status = 'submitted'
AND graded_at IS NULL
ORDER BY submitted_at;
-- idx_submissions_needs_grading 사용
```

#### 5. 실시간 상호작용 (9개 인덱스)

- **현재 참여자**: 세션에 접속 중인 사용자
- **화면 공유**: 현재 화면 공유 중인 사용자
- **비공개 채팅**: 1:1 메시지

```sql
-- 예시: 현재 세션 참여자
SELECT * FROM live.session_participants
WHERE session_id = 456
AND left_at IS NULL
ORDER BY joined_at DESC;
-- idx_participants_current 사용
```

#### 6. 학습 도구 (11개 인덱스)

- **활성 투표**: 현재 진행 중인 투표
- **퀴즈 성적**: 완료된 퀴즈 점수 순위
- **문제 은행**: 난이도/태그별 문제 검색

```sql
-- 예시: 진행 중인 퀴즈
SELECT * FROM learning.quiz_attempts
WHERE quiz_id = 789
AND user_id = 101
AND status = 'in_progress';
-- idx_quiz_attempts_progress 사용
```

#### 7. 평가 시스템 (9개 인덱스)

- **AI 채점 검토**: 낮은 신뢰도 채점 결과
- **성적 순위**: 코스별 성적 리더보드
- **코드 실행**: 프로그래밍 언어별 결과

```sql
-- 예시: 검토 필요한 AI 채점
SELECT * FROM assess.ai_gradings
WHERE reviewed_at IS NULL
AND confidence < 0.8
ORDER BY created_at;
-- idx_ai_gradings_review 사용
```

#### 8. 분석 데이터 (12개 인덱스)

- **참여도 추적**: 학생별 engagement score
- **저참여 알림**: 참여도 낮은 학생 식별
- **상호작용 네트워크**: 학생 간 상호작용 분석

```sql
-- 예시: 저참여 학생 찾기
SELECT user_id, AVG(engagement_score) as avg_engagement
FROM analytics.participation_logs
WHERE engagement_score < 30
GROUP BY user_id
HAVING COUNT(*) >= 3;
-- idx_participation_low_engagement 사용
```

### JSONB 인덱스 (GIN)

다음 컬럼에 GIN 인덱스가 추가되어 JSONB 내 데이터 검색이 가능합니다:

- `course.courses.settings`
- `course.sessions.settings`
- `course.recordings.metadata`
- `learning.questions.tags`
- `learning.whiteboard_elements.data`
- `assess.ai_gradings.feedback`
- `analytics.alerts.data`
- `analytics.interaction_logs.context`

```sql
-- 예시: JSONB 컬럼 검색
SELECT * FROM course.courses
WHERE settings @> '{"grading_weights": {"participation": 30}}';
-- idx_courses_settings_gin 사용
```

### 롤백 방법

```bash
# 011 마이그레이션 롤백
psql -U eduforum_user -d eduforum_db -f database/migrations/011_rollback.sql
```

---

## Migration 012: 제약조건 강화

### 실행 방법

```bash
# 제약조건 추가
psql -U eduforum_user -d eduforum_db -f database/migrations/012_constraints_review.sql

# 주의: 기존 데이터가 제약조건을 위반하는 경우 에러 발생
# 반드시 테스트 환경에서 먼저 검증 후 프로덕션 적용
```

### 주요 제약조건 카테고리

#### 1. 데이터 형식 검증 (Format Validation)

```sql
-- 이메일 형식
users_email_format_check: 유효한 이메일 주소 형식

-- 전화번호 형식
users_phone_format_check: 10-15자리 숫자

-- 코스 코드 형식
courses_code_format: CS101, MATH200 등의 형식

-- 학기 제한
courses_semester_valid: Spring, Summer, Fall, Winter만 허용
```

#### 2. 숫자 범위 검증 (Range Validation)

```sql
-- 점수 범위
grades_final_score_range: 0-100
assignments_max_score_positive: 1-1000
quizzes_passing_score_range: 0-100

-- 시간 제한
sessions_duration_reasonable: 15-480분
quizzes_time_limit_positive: 1-14400초 (4시간)

-- 학생 수
courses_max_students_positive: 1-1000명
```

#### 3. 논리적 일관성 (Logical Consistency)

```sql
-- 시간 순서
sessions_started_after_scheduled: 시작 시간 >= 예정 시간
sessions_ended_after_started: 종료 시간 >= 시작 시간
submissions_submitted_after_created: 제출 시간 >= 생성 시간

-- 상태 일관성
submissions_graded_timestamp: graded 상태일 때 graded_at 필수
assignments_published_timestamp: published 상태일 때 published_at 필수
ai_gradings_reviewed_timestamp: 검토자 있으면 검토 시간 필수
```

#### 4. 열거형 제약 (Enum Constraints)

```sql
-- 세션 참여자 역할
participants_role_valid: host, cohost, participant, observer

-- 연결 품질
participants_connection_quality_valid: excellent, good, fair, poor, disconnected

-- 분반 배정 방식
breakout_assigned_by_valid: auto, manual, random, self

-- 문제 난이도
questions_difficulty_valid: easy, medium, hard, expert

-- 코드 실행 상태
code_exec_status_valid: pending, running, completed, failed, timeout, error

-- 프로그래밍 언어
code_exec_language_valid: python, java, javascript, c, cpp, go 등
```

#### 5. 참조 무결성 강화 (Foreign Key Enhancement)

기존 CASCADE 동작을 SET NULL로 변경하여 히스토리 보존:

```sql
-- 채점자 정보 보존
submissions_graded_by_fkey: ON DELETE SET NULL

-- 호출자 정보 보존
hand_raises_called_by_fkey: ON DELETE SET NULL

-- 역할 부여자 보존
user_roles_assigned_by_fkey: ON DELETE SET NULL

-- AI 채점 검토자 보존
ai_gradings_reviewed_by_fkey: ON DELETE SET NULL

-- 알림 해결자 보존
alerts_resolved_by_fkey: ON DELETE SET NULL
```

#### 6. 고유성 제약 (Uniqueness Constraints)

```sql
-- 중복 손들기 방지
idx_hand_raises_active_unique: 세션당 사용자별 1개의 활성 손들기만 허용

-- 중복 퀴즈 시도 방지
idx_quiz_attempts_in_progress_unique: 퀴즈당 사용자별 1개의 진행 중 시도만 허용
```

### 제약조건 위반 사례 및 해결

#### 사례 1: 이메일 형식 오류

```sql
-- 오류 발생
INSERT INTO auth.users (email, ...) VALUES ('invalid-email', ...);
-- ERROR: users_email_format_check

-- 해결
INSERT INTO auth.users (email, ...) VALUES ('valid@example.com', ...);
```

#### 사례 2: 점수 범위 초과

```sql
-- 오류 발생
UPDATE assess.grades SET final_score = 105 WHERE id = 1;
-- ERROR: grades_final_score_range

-- 해결
UPDATE assess.grades SET final_score = 95 WHERE id = 1;
```

#### 사례 3: 시간 순서 오류

```sql
-- 오류 발생
UPDATE course.sessions
SET ended_at = '2025-01-01 10:00:00',
    started_at = '2025-01-01 12:00:00';
-- ERROR: sessions_ended_after_started

-- 해결
UPDATE course.sessions
SET started_at = '2025-01-01 10:00:00',
    ended_at = '2025-01-01 12:00:00';
```

### 기존 데이터 검증 스크립트

마이그레이션 실행 전 기존 데이터 검증:

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
WHERE ended_at IS NOT NULL
AND started_at IS NOT NULL
AND ended_at < started_at;

-- 상태 일관성 검증
SELECT id FROM course.submissions
WHERE status = 'graded'
AND (graded_at IS NULL OR graded_by IS NULL);
```

### 롤백 방법

```bash
# 012 마이그레이션 롤백
psql -U eduforum_user -d eduforum_db -f database/migrations/012_rollback.sql
```

---

## 성능 영향 분석

### 인덱스 추가의 영향

**장점:**
- SELECT 쿼리 성능 향상 (10-100배)
- 복잡한 WHERE, JOIN, ORDER BY 절 최적화
- 프로덕션 환경에서 CONCURRENTLY 옵션으로 무중단 추가 가능

**단점:**
- 디스크 공간 사용량 증가 (예상: 5-10%)
- INSERT/UPDATE/DELETE 성능 소폭 감소 (예상: 5-10%)
- 인덱스 생성 시간 (대규모 DB의 경우 5-30분)

### 제약조건 추가의 영향

**장점:**
- 데이터 무결성 보장
- 애플리케이션 레벨 검증 실패 시 DB 레벨에서 방어
- 데이터 품질 향상

**단점:**
- INSERT/UPDATE 시 검증 오버헤드 (미미함, <1%)
- 기존 데이터가 제약조건 위반 시 마이그레이션 실패 가능

---

## 모니터링 및 유지보수

### 인덱스 사용률 모니터링

```sql
-- 인덱스 사용 통계
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
ORDER BY idx_scan DESC;

-- 사용되지 않는 인덱스 찾기
SELECT schemaname, tablename, indexname
FROM pg_stat_user_indexes
WHERE idx_scan = 0
AND indexname NOT LIKE '%_pkey'
AND schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics');
```

### 인덱스 크기 확인

```sql
-- 스키마별 인덱스 크기
SELECT schemaname,
       SUM(pg_relation_size(indexrelid)) / 1024 / 1024 AS size_mb
FROM pg_stat_user_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
GROUP BY schemaname
ORDER BY size_mb DESC;
```

### 제약조건 위반 로그

```sql
-- 최근 제약조건 위반 확인 (PostgreSQL 로그 필요)
-- 애플리케이션 로그에서 CHECK constraint 에러 모니터링
```

---

## 추가 최적화 권장사항

### 1. 파티셔닝 고려 대상

대용량 테이블은 파티셔닝 검토:
- `analytics.participation_logs` - 날짜별 파티셔닝
- `analytics.interaction_logs` - 날짜별 파티셔닝
- `live.chats` - 세션별 또는 날짜별 파티셔닝

### 2. VACUUM 및 ANALYZE

인덱스 추가 후 실행:

```sql
-- 스키마별 VACUUM ANALYZE
VACUUM ANALYZE auth.users;
VACUUM ANALYZE course.courses;
VACUUM ANALYZE course.sessions;
VACUUM ANALYZE learning.quizzes;
VACUUM ANALYZE analytics.participation_logs;
```

### 3. 인덱스 재구성

정기적으로 인덱스 재구성 (6개월마다):

```sql
-- 개별 인덱스 재구성
REINDEX INDEX CONCURRENTLY auth.idx_users_email_lower;

-- 테이블 전체 재구성 (다운타임 필요)
REINDEX TABLE course.courses;
```

---

## 참고 자료

- PostgreSQL 인덱스 타입: https://www.postgresql.org/docs/current/indexes-types.html
- GIN 인덱스: https://www.postgresql.org/docs/current/gin.html
- CHECK 제약조건: https://www.postgresql.org/docs/current/ddl-constraints.html
- pg_trgm 확장: https://www.postgresql.org/docs/current/pgtrgm.html

---

## 변경 이력

| 날짜 | 버전 | 변경 내용 |
|------|------|-----------|
| 2025-01-29 | 1.0 | 초기 작성 (DB-003 완료) |
