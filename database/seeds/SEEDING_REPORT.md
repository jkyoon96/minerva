# Database Seeding Report

**작업 일시**: 2025-01-29
**데이터베이스**: eduforum@210.115.229.12:5432
**작업자**: System

---

## 실행 스크립트

### 1. 001_test_data.sql (기존)
- 9명의 기본 사용자
- 2개의 기본 코스
- 3개의 세션
- 2개의 과제

### 2. 002_system_config.sql (신규)
- 전체 사용자 알림 설정
- 역할별 맞춤 알림 타입
- Quiet Hours 설정

### 3. 003_additional_test_data.sql (신규)
- 11명의 추가 사용자
- 3개의 추가 코스
- 7개의 추가 세션
- 투표/퀴즈 샘플
- 분석 로그 샘플

---

## 데이터 통계

### 사용자 (총 20명)

| 역할 | 인원 | 비율 |
|------|------|------|
| Admin | 1명 | 5% |
| Professor | 3명 | 15% |
| TA | 3명 | 15% |
| Student | 13명 | 65% |

#### 사용자 목록

**관리자**
- admin@eduforum.com (Admin User)

**교수**
- prof.kim@eduforum.com (Minsoo Kim)
- prof.lee@eduforum.com (Jieun Lee)
- prof.choi@eduforum.com (승민 Choi)

**조교**
- ta.park@eduforum.com (Hyunwoo Park)
- ta.kim@eduforum.com (영희 Kim)
- ta.lee@eduforum.com (철수 Lee)

**학생 (13명)**
- student1@eduforum.com (Soyeon Choi)
- student2@eduforum.com (Jihoon Jung)
- student3@eduforum.com (Minji Kang)
- student4@eduforum.com (Donghyun Yoon)
- student5@eduforum.com (Seunghee Han)
- student6@eduforum.com (유진 Song)
- student7@eduforum.com (준호 Lim)
- student8@eduforum.com (서연 Shin)
- student9@eduforum.com (민재 Oh)
- student10@eduforum.com (지우 Kwon)
- student11@eduforum.com (태양 Baek)
- student12@eduforum.com (수빈 Nam)
- student13@eduforum.com (하늘 Go)

---

### 코스 (총 5개)

| 코드 | 과목명 | 담당교수 | 학생 | 조교 | 세션 |
|------|--------|----------|------|------|------|
| CS101 | Introduction to Computer Science | Prof. Kim | 5명 | 1명 | 4개 |
| CS201 | Data Structures and Algorithms | Prof. Lee | 3명 | 0명 | 2개 |
| CS202 | 데이터베이스 시스템 | Prof. Lee | 10명 | 1명 | 1개 |
| CS301 | 운영체제 | Prof. Kim | 6명 | 1명 | 1개 |
| MATH201 | 선형대수학 | Prof. Choi | 8명 | 1명 | 2개 |

**총 등록**: 36건 (학생 32명 + 조교 4명)

#### 코스별 상세

**CS101 (컴퓨터과학입론)**
- 교수: Prof. Kim
- 조교: TA Park
- 학생: student1, student2, student3, student4, student5
- 세션: 4개 (Week 1~4)

**CS201 (자료구조와 알고리즘)**
- 교수: Prof. Lee
- 학생: student1, student2, student3
- 세션: 2개

**CS202 (데이터베이스 시스템)**
- 교수: Prof. Lee
- 조교: TA Park
- 학생: 10명 (가장 많은 수강생)
- 세션: 1개

**CS301 (운영체제)**
- 교수: Prof. Kim
- 조교: TA Lee
- 학생: 6명
- 세션: 1개

**MATH201 (선형대수학)**
- 교수: Prof. Choi
- 조교: TA Kim
- 학생: 8명
- 세션: 2개

---

### 학습 활동 데이터

#### 투표 (Polls)
- 총 2개
- Draft: 2개
- Active: 0개
- 샘플 질문:
  - "파이썬을 처음 배우는 분들은 몇 명인가요?" (CS101)
  - "이번 주제에서 가장 어려운 부분은?" (CS201)

#### 퀴즈 (Quizzes)
- 총 2개
- Draft: 2개
- 문제: 2개
  - CS101: "Week 1 Quiz: 프로그래밍 기초" (2문제)
  - CS201: "자료구조 중간고사"

#### 과제 (Assignments)
- 총 2개
- CS101: "Assignment 1: Hello World Program"
- CS201: "Assignment 1: Binary Search Tree Implementation"

---

### 분석 데이터

#### 참여 로그 (Participation Logs)
- 총 26건
- 평균 참여도: 79.72점
- 발언 시간: 60~360초
- 채팅 수: 1~10개

#### 알림 (Alerts)
- 총 2건
- 미읽음: 2건
- 유형: 낮은 참여도 경고

#### 일일 통계 (Daily Stats)
- CS101 통계 (전날 기준)
  - 활성 사용자: 15명
  - 세션 수: 2개
  - 총 발언 시간: 4,500초 (75분)
  - 평균 참여도: 75.5점
  - 퀴즈 응시: 12건
  - 평균 퀴즈 점수: 82.3점

#### 상호작용 로그 (Interaction Logs)
- 총 20건
- 유형: 채팅 답글
- 학생 간 상호작용 기록

---

### 시스템 설정

#### 알림 설정 (Notification Settings)
- 총 20명 (전체 사용자)
- 이메일 알림 활성화: 20명 (100%)
- 푸시 알림 활성화: 20명 (100%)

**역할별 알림 타입**

| 역할 | 알림 타입 | Quiet Hours |
|------|-----------|-------------|
| Admin | 모든 알림 (6가지) | 22:00-08:00 |
| Professor | 교육 관련 알림 (6가지) | 22:00-08:00 |
| TA | 기본 알림 (3가지) | 22:00-08:00 |
| Student | 학습 알림 (3가지) | 21:00-09:00 |

**Admin 알림**: absence, low_participation, grade_drop, assignment_due, system_alert, security_alert

**Professor 알림**: absence, low_participation, grade_drop, assignment_due, student_at_risk, course_milestone

**TA/Student 알림**: assignment_due, grade_posted, session_reminder

---

## 데이터 특징

### 1. 실전 시나리오
- 학생들이 2~3개 코스 동시 수강
- 교수별 1~2개 코스 담당
- 조교 1명당 1개 코스 지원

### 2. 한글 데이터
- 한국인 이름 (유진, 준호, 서연 등)
- 한글 과목명 (선형대수학, 운영체제, 데이터베이스)
- 한글 알림 설정 (시스템 관리자, 교수, 조교, 학생)

### 3. 다양한 시나리오
- 소규모 코스 (3명) ~ 대규모 코스 (10명)
- 활발한 참여 (참여도 60~100점)
- 저조한 참여에 대한 알림 발생

---

## 테스트 시나리오

### 1. 인증/권한 테스트
- 역할별 로그인 (admin, professor, ta, student)
- 권한별 기능 접근 제어

### 2. 코스 관리 테스트
- 다중 코스 등록
- 조교 배정
- 세션 스케줄링

### 3. 학습 활동 테스트
- 투표 생성 및 참여
- 퀴즈 출제 및 응시
- 과제 제출 및 채점

### 4. 분석 테스트
- 참여도 추적
- 위험 학생 알림
- 일일 통계 집계

### 5. 알림 테스트
- 역할별 알림 타입
- Quiet Hours 적용
- 읽음/미읽음 상태

---

## 검증 쿼리

```sql
-- 전체 통계
SELECT
    'Total Users' as category, COUNT(*) as count FROM auth.users
UNION ALL
SELECT 'Total Courses', COUNT(*) FROM course.courses
UNION ALL
SELECT 'Total Sessions', COUNT(*) FROM course.sessions
UNION ALL
SELECT 'Total Enrollments', COUNT(*) FROM course.enrollments;

-- 역할별 사용자
SELECT r.name as role, COUNT(ur.user_id) as count
FROM auth.roles r
LEFT JOIN auth.user_roles ur ON r.id = ur.role_id
GROUP BY r.name
ORDER BY r.name;

-- 코스별 수강생
SELECT c.code, c.title, COUNT(e.id) as student_count
FROM course.courses c
LEFT JOIN course.enrollments e ON c.id = e.course_id AND e.role = 'student'
GROUP BY c.id, c.code, c.title
ORDER BY c.code;
```

---

## 롤백

전체 데이터 제거:
```bash
psql -h 210.115.229.12 -U eduforum -d eduforum -f rollback_seeds.sql
```

---

## 다음 단계

1. **API 테스트**: 시딩된 데이터로 REST API 엔드포인트 테스트
2. **성능 테스트**: 대량 데이터 조회 및 집계 쿼리 성능 측정
3. **UI 테스트**: 프론트엔드에서 데이터 표시 및 상호작용
4. **시나리오 테스트**: 실제 수업 진행 시나리오 재현

---

## 참고사항

- **비밀번호**: 모든 테스트 계정의 비밀번호는 "password" (해시화됨)
- **초대 코드**: 각 코스별 고유 초대 코드 설정됨
- **시간대**: 모든 타임스탬프는 UTC 기준
- **데이터 무결성**: 외래 키 제약조건 모두 준수

---

**작업 완료**: ✅ 성공
**검증 상태**: ✅ 통과
**프로덕션 준비**: ⚠️  개발/테스트 전용
