# DB-003: 인덱스 및 제약조건 최적화 작업 요약

## 작업 개요

**작업명**: Phase 3 - DB-003: 인덱스 및 제약조건 최적화
**날짜**: 2025-01-29
**상태**: ✅ 완료

## 작업 목표

1. 쿼리 성능 최적화를 위한 추가 인덱스 생성
2. 데이터 무결성 보장을 위한 제약조건 강화
3. JSONB 컬럼에 대한 GIN 인덱스 추가
4. 부분 인덱스를 통한 저장 공간 최적화

## 생성된 파일

| 파일명 | 라인 수 | 목적 |
|--------|---------|------|
| `011_additional_indexes.sql` | 484 | 74개 성능 최적화 인덱스 |
| `011_rollback.sql` | 108 | Migration 011 롤백 스크립트 |
| `012_constraints_review.sql` | 443 | 95개 데이터 무결성 제약조건 |
| `012_rollback.sql` | 163 | Migration 012 롤백 스크립트 |
| `DB-003-OPTIMIZATION-GUIDE.md` | 600+ | 최적화 가이드 문서 |
| `DB-003-SUMMARY.md` | 이 문서 | 작업 요약 |

**전체 파일 경로**: `/mnt/d/Development/git/minerva/database/migrations/`

---

## Migration 011: 추가 인덱스 (74개)

### 스키마별 인덱스 분포

| 스키마 | 인덱스 수 | 주요 목적 |
|--------|----------|-----------|
| auth | 10 | 사용자 인증, 토큰 관리 |
| course | 23 | 코스, 세션, 과제 관리 |
| live | 9 | 실시간 상호작용 |
| learning | 11 | 투표, 퀴즈, 화이트보드 |
| assess | 9 | 평가, AI 채점 |
| analytics | 12 | 참여도, 분석 데이터 |

### 인덱스 유형별 분류

#### 1. 복합 인덱스 (Composite Indexes) - 35개

여러 컬럼을 조합하여 검색 성능 향상:

```sql
-- 예시
idx_enrollments_user_course_status (user_id, course_id, status)
idx_sessions_upcoming (course_id, scheduled_at)
idx_quiz_attempts_user_performance (user_id, quiz_id, score DESC)
```

#### 2. 부분 인덱스 (Partial Indexes) - 28개

WHERE 조건을 포함하여 저장 공간 절약:

```sql
-- 예시
idx_users_active_status ... WHERE deleted_at IS NULL AND status = 'active'
idx_submissions_needs_grading ... WHERE status = 'submitted' AND graded_at IS NULL
idx_participants_current ... WHERE left_at IS NULL
```

#### 3. GIN 인덱스 (JSONB 전용) - 11개

JSONB 컬럼 내부 데이터 검색:

```sql
-- 예시
idx_courses_settings_gin ON courses USING gin(settings)
idx_questions_tags_gin ON questions USING gin(tags)
idx_alerts_data_gin ON alerts USING gin(data)
```

#### 4. 전체 텍스트 검색 (pg_trgm) - 2개

```sql
-- 예시
idx_courses_title_trgm ON courses USING gin(title gin_trgm_ops)
```

#### 5. 함수 기반 인덱스 - 2개

```sql
-- 예시
idx_users_email_lower ON users(LOWER(email))
idx_courses_code_lower ON courses(LOWER(code))
```

### 주요 성능 개선 예상

| 쿼리 유형 | 개선 전 | 개선 후 | 비고 |
|----------|---------|---------|------|
| 이메일 검색 (대소문자 무관) | Full Scan | Index Scan | 100배+ 향상 |
| 코스 제목 검색 | Full Scan | GIN Index | 50배+ 향상 |
| 채점 대기 제출물 조회 | Full Scan | Partial Index | 80배+ 향상 |
| 활성 세션 참여자 | Full Scan | Partial Index | 100배+ 향상 |
| 퀴즈 성적 순위 | Sort + Scan | Index Scan | 30배+ 향상 |

---

## Migration 012: 제약조건 강화 (95개)

### 제약조건 유형별 분류

| 유형 | 수량 | 목적 |
|------|------|------|
| CHECK 제약조건 | 85 | 값 범위, 형식, 논리 검증 |
| UNIQUE 인덱스 | 2 | 중복 방지 |
| Foreign Key 수정 | 5 | 참조 무결성 강화 |
| 복합 제약조건 | 3 | 다중 컬럼 검증 |

### 제약조건 카테고리

#### 1. 형식 검증 (Format Validation) - 8개

```sql
users_email_format_check: 이메일 형식
users_phone_format_check: 전화번호 형식
courses_code_format: 코스 코드 (CS101 형식)
courses_semester_valid: 학기 (Spring/Summer/Fall/Winter)
```

#### 2. 범위 검증 (Range Validation) - 32개

```sql
-- 점수 범위 (0-100)
grades_final_score_range
quizzes_passing_score_range
participation_engagement_range

-- 시간 제한
sessions_duration_reasonable (15-480분)
quizzes_time_limit_positive (1-14400초)

-- 카운트 제한
courses_max_students_positive (1-1000)
assignments_max_attempts_positive (1-10)
```

#### 3. 논리적 일관성 (Logical Consistency) - 30개

```sql
-- 시간 순서
sessions_started_after_scheduled
sessions_ended_after_started
submissions_submitted_after_created

-- 상태 일관성
submissions_graded_timestamp
assignments_published_timestamp
ai_gradings_reviewed_timestamp
```

#### 4. 열거형 검증 (Enum Validation) - 15개

```sql
participants_role_valid: host/cohost/participant/observer
participants_connection_quality_valid: excellent/good/fair/poor
questions_difficulty_valid: easy/medium/hard/expert
code_exec_language_valid: python/java/javascript/...
```

#### 5. 참조 무결성 강화 - 5개

CASCADE에서 SET NULL로 변경하여 히스토리 보존:

```sql
submissions_graded_by_fkey: ON DELETE SET NULL
hand_raises_called_by_fkey: ON DELETE SET NULL
user_roles_assigned_by_fkey: ON DELETE SET NULL
ai_gradings_reviewed_by_fkey: ON DELETE SET NULL
alerts_resolved_by_fkey: ON DELETE SET NULL
```

#### 6. 고유성 제약 - 2개

```sql
idx_hand_raises_active_unique: 세션당 사용자별 1개의 활성 손들기
idx_quiz_attempts_in_progress_unique: 퀴즈당 사용자별 1개의 진행 중 시도
```

---

## 실행 가이드

### 1. 테스트 환경에서 검증

```bash
# 백업 생성
pg_dump -U eduforum_user eduforum_db > backup_before_optimization.sql

# 인덱스 추가 (Migration 011)
psql -U eduforum_user -d eduforum_db -f database/migrations/011_additional_indexes.sql

# 제약조건 추가 (Migration 012)
psql -U eduforum_user -d eduforum_db -f database/migrations/012_constraints_review.sql

# 검증
psql -U eduforum_user -d eduforum_db -c "\di+ auth.*"
psql -U eduforum_user -d eduforum_db -c "SELECT conname FROM pg_constraint WHERE connamespace = 'course'::regnamespace;"
```

### 2. 프로덕션 환경 배포

```bash
# 1단계: 유지보수 모드 공지 (선택사항)
# 2단계: 백업
pg_dump -U eduforum_user -h production-host eduforum_db > prod_backup_$(date +%Y%m%d).sql

# 3단계: Migration 011 실행 (무중단 가능)
psql -U eduforum_user -h production-host -d eduforum_db \
  -f database/migrations/011_additional_indexes.sql

# 4단계: 기존 데이터 검증 스크립트 실행
# (DB-003-OPTIMIZATION-GUIDE.md 참조)

# 5단계: Migration 012 실행
psql -U eduforum_user -h production-host -d eduforum_db \
  -f database/migrations/012_constraints_review.sql

# 6단계: VACUUM ANALYZE
psql -U eduforum_user -h production-host -d eduforum_db \
  -c "VACUUM ANALYZE;"
```

### 3. 롤백 절차 (필요 시)

```bash
# Migration 012 롤백
psql -U eduforum_user -d eduforum_db -f database/migrations/012_rollback.sql

# Migration 011 롤백
psql -U eduforum_user -d eduforum_db -f database/migrations/011_rollback.sql

# 백업 복원 (최후 수단)
psql -U eduforum_user -d eduforum_db < backup_before_optimization.sql
```

---

## 성능 및 영향 분석

### 예상 성능 개선

| 영역 | 개선율 | 근거 |
|------|--------|------|
| 사용자 검색 | 100배+ | 이메일 인덱스 + LOWER() 함수 |
| 코스 검색 | 50배+ | GIN 전체 텍스트 검색 |
| 세션 조회 | 80배+ | 복합 + 부분 인덱스 |
| 채점 워크플로우 | 100배+ | 부분 인덱스 (status 필터) |
| 분석 쿼리 | 30배+ | 시간 범위 인덱스 |

### 예상 부작용

| 항목 | 영향 | 완화 방안 |
|------|------|----------|
| 디스크 사용량 | +5-10% | 부분 인덱스로 최소화 |
| INSERT 성능 | -5-10% | 배치 처리 권장 |
| UPDATE 성능 | -5-10% | 트랜잭션 최적화 |
| 마이그레이션 시간 | 5-30분 | CONCURRENTLY 옵션 사용 |

---

## 테스트 결과 (예상)

### 인덱스 효과 테스트

```sql
-- 테스트 1: 이메일 검색
EXPLAIN ANALYZE
SELECT * FROM auth.users
WHERE LOWER(email) = 'test@example.com'
AND deleted_at IS NULL;

-- 예상 결과:
-- Before: Seq Scan (cost=0..500, rows=10000)
-- After: Index Scan using idx_users_email_lower (cost=0..8, rows=1)

-- 테스트 2: 코스 제목 검색
EXPLAIN ANALYZE
SELECT * FROM course.courses
WHERE title ILIKE '%machine%'
AND deleted_at IS NULL;

-- 예상 결과:
-- Before: Seq Scan + Filter (cost=0..800, rows=1000)
-- After: Bitmap Index Scan using idx_courses_title_trgm (cost=0..50, rows=10)

-- 테스트 3: 채점 대기 조회
EXPLAIN ANALYZE
SELECT * FROM course.submissions
WHERE assignment_id = 123
AND status = 'submitted'
AND graded_at IS NULL;

-- 예상 결과:
-- Before: Seq Scan + Filter (cost=0..1000, rows=5000)
-- After: Index Scan using idx_submissions_needs_grading (cost=0..20, rows=50)
```

### 제약조건 검증 테스트

```sql
-- 테스트 1: 이메일 형식 검증
INSERT INTO auth.users (email, first_name, last_name)
VALUES ('invalid-email', 'Test', 'User');
-- 예상: ERROR - users_email_format_check

-- 테스트 2: 점수 범위 검증
UPDATE assess.grades SET final_score = 105 WHERE id = 1;
-- 예상: ERROR - grades_final_score_range

-- 테스트 3: 시간 순서 검증
UPDATE course.sessions
SET started_at = '2025-01-01 12:00',
    ended_at = '2025-01-01 10:00';
-- 예상: ERROR - sessions_ended_after_started
```

---

## 모니터링 포인트

### 1. 인덱스 사용률

```sql
-- 일주일 후 확인
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read
FROM pg_stat_user_indexes
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
AND idx_scan = 0
ORDER BY pg_relation_size(indexrelid) DESC;
```

### 2. 쿼리 성능

```sql
-- 느린 쿼리 모니터링 (pg_stat_statements 필요)
SELECT query, calls, mean_exec_time, total_exec_time
FROM pg_stat_statements
WHERE query LIKE '%course.courses%'
ORDER BY mean_exec_time DESC
LIMIT 10;
```

### 3. 제약조건 위반

```sql
-- 애플리케이션 로그에서 CHECK constraint 에러 패턴 모니터링
-- 자주 발생하는 제약조건 위반 파악 및 애플리케이션 수정
```

---

## 향후 최적화 계획

### 1. 파티셔닝 검토 (Phase 4)

- `analytics.participation_logs` - 월별 파티셔닝
- `analytics.interaction_logs` - 월별 파티셔닝
- `live.chats` - 세션 또는 날짜별 파티셔닝

### 2. 인덱스 튜닝

- 6개월 후 사용률 낮은 인덱스 제거
- 쿼리 패턴 변화에 따른 인덱스 재설계

### 3. 통계 정보 자동 갱신

```sql
-- Auto-vacuum 설정 최적화
ALTER TABLE analytics.participation_logs
SET (autovacuum_vacuum_scale_factor = 0.05);
```

---

## 체크리스트

### 배포 전 확인사항

- [ ] 테스트 환경에서 마이그레이션 검증 완료
- [ ] 백업 생성 완료
- [ ] 기존 데이터 제약조건 위반 여부 확인
- [ ] 롤백 스크립트 테스트 완료
- [ ] 디스크 여유 공간 확인 (최소 15% 여유)
- [ ] 유지보수 시간 공지 (선택사항)

### 배포 후 확인사항

- [ ] 인덱스 생성 완료 확인
- [ ] 제약조건 추가 완료 확인
- [ ] VACUUM ANALYZE 실행
- [ ] 주요 쿼리 성능 테스트
- [ ] 애플리케이션 정상 동작 확인
- [ ] 에러 로그 모니터링 (24시간)

---

## 관련 문서

- `DB-003-OPTIMIZATION-GUIDE.md` - 상세 최적화 가이드
- `009_indexes.sql` - 기존 기본 인덱스
- `003_auth_schema.sql` ~ `008_analytics_schema.sql` - 스키마 정의

---

## 작업 완료 보고

**작업자**: Claude Code
**작업 일시**: 2025-01-29
**작업 내용**:
- ✅ 74개 성능 최적화 인덱스 추가 (Migration 011)
- ✅ 95개 데이터 무결성 제약조건 추가 (Migration 012)
- ✅ 롤백 스크립트 작성 (011_rollback.sql, 012_rollback.sql)
- ✅ 최적화 가이드 문서 작성
- ✅ 작업 요약 문서 작성

**예상 효과**:
- 전체 쿼리 성능 30-100배 향상
- 데이터 무결성 보장 강화
- 애플리케이션 안정성 향상

**다음 단계**:
1. 테스트 환경 배포 및 검증
2. 프로덕션 배포 계획 수립
3. 성능 모니터링 대시보드 구성
4. Phase 4 파티셔닝 작업 준비
