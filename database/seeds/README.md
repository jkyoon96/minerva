# Database Seeding Scripts

데이터베이스 초기 데이터 및 테스트 데이터 시딩 스크립트 모음입니다.

## 파일 목록

### 1. 001_test_data.sql
**기본 테스트 데이터**
- 9명의 사용자 (Admin 1, Professor 2, TA 1, Student 5)
- 2개의 코스 (CS101, CS201)
- 기본 세션 및 과제 데이터
- 역할 매핑 및 초기 성적 데이터

### 2. 002_system_config.sql
**시스템 설정 데이터**
- 모든 사용자에 대한 알림 설정
- 역할별 맞춤 알림 타입
- Quiet Hours 설정

### 3. 003_additional_test_data.sql
**확장 테스트 데이터**
- 추가 사용자 11명 (총 20명)
  - Professor 1명 추가 (총 3명)
  - TA 2명 추가 (총 3명)
  - Student 8명 추가 (총 13명)
- 추가 코스 3개 (총 5개)
  - MATH201: 선형대수학
  - CS301: 운영체제
  - CS202: 데이터베이스 시스템
- 추가 세션 7개
- 투표/퀴즈 샘플 데이터
- 분석 로그 샘플 (참여 로그, 상호작용 로그, 알림, 일일 통계)

### 4. rollback_seeds.sql
**롤백 스크립트**
- 모든 시드 데이터 제거
- 외래 키 의존성을 고려한 역순 삭제
- 정리 후 검증 쿼리 포함

## 실행 순서

### 초기 시딩
```bash
# 1. 기본 테스트 데이터
psql -h 210.115.229.12 -U eduforum -d eduforum -f 001_test_data.sql

# 2. 시스템 설정
psql -h 210.115.229.12 -U eduforum -d eduforum -f 002_system_config.sql

# 3. 확장 테스트 데이터
psql -h 210.115.229.12 -U eduforum -d eduforum -f 003_additional_test_data.sql
```

### 롤백 (전체 데이터 제거)
```bash
psql -h 210.115.229.12 -U eduforum -d eduforum -f rollback_seeds.sql
```

## 데이터 통계

### 사용자 구성 (총 20명)
- Admin: 1명
- Professor: 3명
- TA: 3명
- Student: 13명

### 코스 구성 (총 5개)
| 코드 | 과목명 | 담당교수 | 수강생 | 조교 |
|------|--------|----------|--------|------|
| CS101 | Introduction to Computer Science | Prof. Kim | 5명 | TA Park |
| CS201 | Data Structures and Algorithms | Prof. Lee | 3명 | - |
| MATH201 | 선형대수학 | Prof. Choi | 8명 | TA Kim |
| CS301 | 운영체제 | Prof. Kim | 6명 | TA Lee |
| CS202 | 데이터베이스 시스템 | Prof. Lee | 10명 | TA Park |

### 테스트 계정 정보

**관리자**
- Email: admin@eduforum.com
- Password: password (해시화됨)

**교수**
- prof.kim@eduforum.com
- prof.lee@eduforum.com
- prof.choi@eduforum.com

**조교**
- ta.park@eduforum.com
- ta.kim@eduforum.com
- ta.lee@eduforum.com

**학생**
- student1@eduforum.com ~ student13@eduforum.com

모든 계정의 비밀번호 해시는 동일하며, 실제 비밀번호는 "password"입니다.
(프로덕션에서는 반드시 변경 필요)

## 데이터 특징

### 실전 시나리오
1. **다양한 수강 패턴**: 학생들이 여러 코스에 분산 등록
2. **교차 등록**: 일부 학생이 2~3개 코스 동시 수강
3. **TA 배정**: 각 코스별 TA 1명씩 배정
4. **한글 데이터**: 실제 한국 대학 환경 반영

### 학습 활동 데이터
1. **투표**: 익명/공개 투표, 단일/다중 선택
2. **퀴즈**: 시간 제한, 난이도별 문제, 합격 점수
3. **참여 로그**: 발언 시간, 채팅, 투표/퀴즈 참여
4. **상호작용 로그**: 학생 간 채팅 응답
5. **알림**: 저조한 참여도 경고

### 분석 데이터
1. **일일 통계**: 활성 사용자, 평균 참여도, 퀴즈 성적
2. **참여 로그**: 세션별 학생 활동 추적
3. **알림 설정**: 역할별 맞춤 알림 환경설정

## 주의사항

1. **순서 준수**: 스크립트는 반드시 순서대로 실행
2. **멱등성 없음**: 중복 실행 시 에러 발생 가능
3. **외래 키**: 롤백 시 반드시 역순으로 삭제
4. **프로덕션 금지**: 개발/테스트 환경에서만 사용

## 검증 쿼리

```sql
-- 사용자 수 확인
SELECT r.name, COUNT(ur.user_id) as count
FROM auth.roles r
LEFT JOIN auth.user_roles ur ON r.id = ur.role_id
GROUP BY r.name
ORDER BY r.name;

-- 코스별 수강생 수
SELECT c.code, c.title, COUNT(e.id) as enrollments
FROM course.courses c
LEFT JOIN course.enrollments e ON c.id = e.course_id
WHERE e.role = 'student'
GROUP BY c.id, c.code, c.title
ORDER BY c.code;

-- 세션 수
SELECT COUNT(*) as total_sessions FROM course.sessions;

-- 투표/퀴즈 수
SELECT
    (SELECT COUNT(*) FROM learning.polls) as polls,
    (SELECT COUNT(*) FROM learning.quizzes) as quizzes;

-- 분석 데이터 수
SELECT
    (SELECT COUNT(*) FROM analytics.participation_logs) as participation_logs,
    (SELECT COUNT(*) FROM analytics.alerts) as alerts,
    (SELECT COUNT(*) FROM analytics.notification_settings) as notification_settings;
```

## 문제 해결

### 외래 키 에러
```sql
-- 의존성 확인
SELECT conname, conrelid::regclass, confrelid::regclass
FROM pg_constraint
WHERE confrelid = 'schema.table_name'::regclass;
```

### 시퀀스 리셋
```sql
-- 특정 테이블의 ID 시퀀스 리셋
SELECT setval('schema.table_id_seq', 1, false);
```

### 데이터 확인
```sql
-- 최근 삽입된 데이터 확인
SELECT * FROM auth.users ORDER BY created_at DESC LIMIT 10;
```

## 라이센스

EduForum 프로젝트 내부용
