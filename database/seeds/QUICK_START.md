# Quick Start Guide - Database Seeding

EduForum 데이터베이스 시딩 빠른 시작 가이드

## 1분 실행 가이드

### 전체 시딩 (한 번에 실행)

```bash
cd /mnt/d/Development/git/minerva/database/seeds

# Windows (WSL/Git Bash)
export PGPASSWORD=eduforum12
psql -h 210.115.229.12 -U eduforum -d eduforum -f 001_test_data.sql
psql -h 210.115.229.12 -U eduforum -d eduforum -f 002_system_config.sql
psql -h 210.115.229.12 -U eduforum -d eduforum -f 003_additional_test_data.sql

# 또는 단일 명령
cat 001_test_data.sql 002_system_config.sql 003_additional_test_data.sql | \
  PGPASSWORD=eduforum12 psql -h 210.115.229.12 -U eduforum -d eduforum
```

### 개별 스크립트 실행

```bash
# 1. 기본 테스트 데이터만 (9명, 2개 코스)
PGPASSWORD=eduforum12 psql -h 210.115.229.12 -U eduforum -d eduforum -f 001_test_data.sql

# 2. 시스템 설정 추가
PGPASSWORD=eduforum12 psql -h 210.115.229.12 -U eduforum -d eduforum -f 002_system_config.sql

# 3. 확장 데이터 추가 (총 20명, 5개 코스)
PGPASSWORD=eduforum12 psql -h 210.115.229.12 -U eduforum -d eduforum -f 003_additional_test_data.sql
```

### 롤백 (전체 삭제)

```bash
PGPASSWORD=eduforum12 psql -h 210.115.229.12 -U eduforum -d eduforum -f rollback_seeds.sql
```

---

## 검증

### 빠른 검증

```bash
PGPASSWORD=eduforum12 psql -h 210.115.229.12 -U eduforum -d eduforum -c "
SELECT
    'Users' as metric, COUNT(*)::text FROM auth.users
UNION ALL SELECT 'Courses', COUNT(*)::text FROM course.courses
UNION ALL SELECT 'Sessions', COUNT(*)::text FROM course.sessions;
"
```

**예상 결과:**
```
  metric  | count
----------+-------
 Users    | 20
 Courses  | 5
 Sessions | 10
```

### 상세 검증

```bash
PGPASSWORD=eduforum12 psql -h 210.115.229.12 -U eduforum -d eduforum -c "
SELECT r.name, COUNT(ur.user_id) as count
FROM auth.roles r
LEFT JOIN auth.user_roles ur ON r.id = ur.role_id
GROUP BY r.name ORDER BY r.name;
"
```

**예상 결과:**
```
   role    | count
-----------+-------
 admin     |     1
 professor |     3
 student   |    13
 ta        |     3
```

---

## 테스트 계정

모든 계정 비밀번호: `password`

### 관리자
- `admin@eduforum.com`

### 교수
- `prof.kim@eduforum.com`
- `prof.lee@eduforum.com`
- `prof.choi@eduforum.com`

### 조교
- `ta.park@eduforum.com`
- `ta.kim@eduforum.com`
- `ta.lee@eduforum.com`

### 학생
- `student1@eduforum.com` ~ `student13@eduforum.com`

---

## 코스 초대 코드

| 코스 | 초대 코드 |
|------|-----------|
| CS101 | CS101SPRING |
| CS201 | CS201SPRING |
| CS202 | CS202SPRING |
| CS301 | CS301SPRING |
| MATH201 | MATH201SPRING |

---

## 자주 사용하는 쿼리

### 1. 특정 학생의 수강 코스 확인

```sql
SELECT c.code, c.title, e.role
FROM course.enrollments e
JOIN course.courses c ON e.course_id = c.id
JOIN auth.users u ON e.user_id = u.id
WHERE u.email = 'student1@eduforum.com';
```

### 2. 코스별 수강생 목록

```sql
SELECT u.email, u.first_name, u.last_name, e.role
FROM course.enrollments e
JOIN auth.users u ON e.user_id = u.id
JOIN course.courses c ON e.course_id = c.id
WHERE c.code = 'CS101'
ORDER BY e.role, u.email;
```

### 3. 참여도 확인

```sql
SELECT
    u.email,
    pl.talk_time_sec,
    pl.chat_count,
    pl.engagement_score
FROM analytics.participation_logs pl
JOIN auth.users u ON pl.user_id = u.id
ORDER BY pl.engagement_score DESC;
```

### 4. 알림 설정 확인

```sql
SELECT
    u.email,
    r.name as role,
    ns.alert_types,
    ns.quiet_hours
FROM analytics.notification_settings ns
JOIN auth.users u ON ns.user_id = u.id
JOIN auth.user_roles ur ON u.id = ur.user_id
JOIN auth.roles r ON ur.role_id = r.id
ORDER BY r.name, u.email;
```

---

## 문제 해결

### 문제: "relation does not exist"

**원인**: 마이그레이션이 실행되지 않음

**해결**:
```bash
cd /mnt/d/Development/git/minerva/database/migrations
./run_migrations.sh
```

### 문제: "duplicate key value violates unique constraint"

**원인**: 이미 데이터가 존재함

**해결**:
```bash
# 롤백 후 재실행
PGPASSWORD=eduforum12 psql -h 210.115.229.12 -U eduforum -d eduforum -f rollback_seeds.sql
# 그 다음 다시 시딩
```

### 문제: "password authentication failed"

**원인**: 비밀번호 또는 접근 권한 문제

**해결**:
```bash
# 접속 테스트
PGPASSWORD=eduforum12 psql -h 210.115.229.12 -U eduforum -d eduforum -c "SELECT 1;"
```

---

## 추가 리소스

- **README.md**: 전체 파일 설명 및 데이터 통계
- **SEEDING_REPORT.md**: 상세 시딩 리포트
- **rollback_seeds.sql**: 롤백 스크립트

---

**작성일**: 2025-01-29
**버전**: 1.0
