# Phase 5 Sprint 2 - E2 코스 관리 BE API

## 개요

| 항목 | 내용 |
|------|------|
| **작업일** | 2025-01-29 |
| **Phase** | Phase 5 - Sprint 2 |
| **Epic** | E2 - 코스 관리 |
| **범위** | Backend API |
| **관련 Issues** | #45, #46, #47, #53, #54, #56, #60, #61, #64, #70, #71, #74, #86, #92, #97 |

---

## 구현된 API 엔드포인트 (24개)

### 1. 코스 CRUD API (CourseController)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/courses` | 코스 생성 |
| GET | `/v1/courses` | 코스 목록 조회 |
| GET | `/v1/courses/{courseId}` | 코스 상세 조회 |
| PUT | `/v1/courses/{courseId}` | 코스 수정 |
| DELETE | `/v1/courses/{courseId}` | 코스 삭제 (soft delete) |
| POST | `/v1/courses/{courseId}/archive` | 코스 보관 |

### 2. 초대 링크 API (CourseController)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/courses/{courseId}/invite-links` | 초대 링크 생성 |
| GET | `/v1/courses/{courseId}/invite-links` | 초대 링크 목록 |
| DELETE | `/v1/courses/{courseId}/invite-links/{linkId}` | 초대 링크 삭제 |
| GET | `/v1/courses/invite/{code}` | 초대 코드 검증 |

### 3. 수강 관리 API (EnrollmentController)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/student/courses` | 내 수강 코스 목록 |
| POST | `/v1/courses/join` | 초대 코드로 코스 등록 |
| POST | `/v1/courses/{courseId}/enrollments/csv` | CSV로 수강생 일괄 등록 |
| GET | `/v1/student/courses/{courseId}` | 학생용 코스 상세 |

### 4. 세션 CRUD API (SessionController)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/courses/{courseId}/sessions` | 세션 생성 |
| GET | `/v1/courses/{courseId}/sessions` | 세션 목록 |
| GET | `/v1/sessions/{sessionId}` | 세션 상세 |
| PUT | `/v1/sessions/{sessionId}` | 세션 수정 |
| DELETE | `/v1/sessions/{sessionId}` | 세션 삭제 |

### 5. 과제 API (AssignmentController)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/courses/{courseId}/assignments` | 과제 생성 |
| GET | `/v1/courses/{courseId}/assignments` | 과제 목록 |
| GET | `/v1/assignments/{assignmentId}` | 과제 상세 |
| POST | `/v1/assignments/{assignmentId}/submit` | 과제 제출 |

### 6. 대시보드 API (DashboardController)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/dashboard` | 교수 대시보드 |
| GET | `/v1/student/dashboard` | 학생 대시보드 |

---

## 생성된 파일 목록 (30개)

### Controllers (5개)
```
apps/backend/src/main/java/com/eduforum/api/domain/course/controller/
├── CourseController.java       # 코스 CRUD + 초대링크
├── SessionController.java      # 세션 CRUD
├── EnrollmentController.java   # 수강 관리
├── AssignmentController.java   # 과제 관리
└── DashboardController.java    # 대시보드
```

### Services (5개)
```
apps/backend/src/main/java/com/eduforum/api/domain/course/service/
├── CourseService.java          # 코스 비즈니스 로직
├── SessionService.java         # 세션 비즈니스 로직
├── EnrollmentService.java      # 수강/CSV 파싱
├── AssignmentService.java      # 과제 비즈니스 로직
└── DashboardService.java       # 대시보드 집계
```

### Entities (6개)
```
apps/backend/src/main/java/com/eduforum/api/domain/course/entity/
├── Course.java                 # 기존 확장
├── CourseSession.java          # 기존 확장
├── Enrollment.java             # 기존 확장
├── Assignment.java             # 기존 확장
├── AssignmentSubmission.java   # 신규
└── InviteLink.java             # 신규
```

### Repositories (6개)
```
apps/backend/src/main/java/com/eduforum/api/domain/course/repository/
├── CourseRepository.java       # 기존 확장
├── CourseSessionRepository.java # 기존 확장
├── EnrollmentRepository.java   # 기존 확장
├── AssignmentRepository.java   # 기존 확장
├── AssignmentSubmissionRepository.java # 신규
└── InviteLinkRepository.java   # 신규
```

### DTOs (16개)
```
apps/backend/src/main/java/com/eduforum/api/domain/course/dto/
├── CourseCreateRequest.java
├── CourseUpdateRequest.java
├── CourseResponse.java
├── CourseJoinRequest.java
├── SessionCreateRequest.java
├── SessionUpdateRequest.java
├── SessionResponse.java
├── InviteLinkCreateRequest.java
├── InviteLinkResponse.java
├── EnrollmentResponse.java
├── AssignmentCreateRequest.java
├── AssignmentResponse.java
├── AssignmentSubmissionRequest.java
├── AssignmentSubmissionResponse.java
├── DashboardResponse.java
└── StudentDashboardResponse.java
```

---

## 주요 기능 상세

### 1. 코스 CRUD (#74)
- 코스 코드 자동 생성 (10자리 영숫자)
- 학기/연도 관리
- 최대 수강인원 설정
- Soft Delete (삭제 시 상태 변경)
- 코스 보관 기능

### 2. 초대 링크 (#92)
- 고유 초대 코드 생성 (10자리)
- 만료일 설정
- 사용 횟수 제한
- 사용 횟수 자동 추적

### 3. 수강 관리 (#60, #61)
- 초대 코드로 코스 등록
- 이미 등록된 사용자 체크
- 수강 상태 관리

### 4. CSV 일괄 등록 (#86)
- UTF-8 CSV 파싱
- 이메일, 이름, 역할 추출
- 신규 사용자 자동 생성
- 중복 등록 방지
- 등록 결과 리포트

### 5. 세션 관리 (#97)
- 세션 타입 (LECTURE, SEMINAR, LAB)
- 세션 상태 (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)
- 시작 시간/종료 시간 관리

### 6. 과제 관리 (#64)
- 과제 타입 (ESSAY, CODE, QUIZ, FILE_UPLOAD)
- 마감일 관리
- 과제 제출 (파일 첨부)
- 제출 횟수 관리
- 늦은 제출 처리

### 7. 대시보드 (#45, #46, #47, #53, #54, #56)
- **교수 대시보드**
  - 내 코스 수
  - 총 학생 수
  - 진행 중 과제 수
  - 예정된 세션 수
  - 채점 대기 과제
  - 최근 활동
  - 알림 목록

- **학생 대시보드**
  - 수강 코스 수
  - 완료 과제 수
  - 평균 점수
  - 다음 세션
  - 마감 임박 과제
  - 최근 성적

---

## 보안 구현

### 권한 검증
```java
// 교수 전용
@PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")

// 학생 전용
@PreAuthorize("hasRole('STUDENT')")

// 인증된 모든 사용자
@PreAuthorize("isAuthenticated()")
```

### 소유권 검증
- 코스 수정/삭제: 코스 생성자만 가능
- 세션/과제 관리: 코스 담당 교수만 가능
- 과제 제출: 해당 코스 수강생만 가능

---

## 커밋 정보

```
commit d61c1b8
Author: Claude Code
Date: 2025-01-29

feat: Phase 5 Sprint 2 - E2 코스 관리 BE API 완성

30 files changed, 2868 insertions(+)
```

---

## 완료된 GitHub Issues (15개)

| Issue | 제목 | 상태 |
|-------|------|------|
| #45 | [E2-S0-T1] [BE] 대시보드 데이터 집계 API | ✅ Closed |
| #46 | [E2-S0-T2] [BE] 알림 조회 API | ✅ Closed |
| #47 | [E2-S0-T3] [BE] 채점 대기 과제 목록 API | ✅ Closed |
| #53 | [E2-S0S-T1] [BE] 학생 대시보드 데이터 집계 API | ✅ Closed |
| #54 | [E2-S0S-T2] [BE] 마감 임박 과제/퀴즈 목록 API | ✅ Closed |
| #56 | [E2-S0S-T4] [BE] 최근 성적 목록 API | ✅ Closed |
| #60 | [E2-S0SL-T1] [BE] 학생 코스 목록 API | ✅ Closed |
| #61 | [E2-S0SL-T2] [BE] 초대 코드 검증 및 등록 API | ✅ Closed |
| #64 | [E2-S0AS-T1] [BE] 과제 제출 API | ✅ Closed |
| #70 | [E2-S0CD-T1] [BE] 학생 코스 상세 API | ✅ Closed |
| #71 | [E2-S0CD-T2] [BE] 학생 세션/과제 현황 API | ✅ Closed |
| #74 | [E2-S1-T1] [BE] 코스 CRUD API 개발 | ✅ Closed |
| #86 | [E2-S2-T1] [BE] CSV 파싱 및 검증 로직 | ✅ Closed |
| #92 | [E2-S3-T1] [BE] 초대 링크 생성/검증 API | ✅ Closed |
| #97 | [E2-S4-T1] [BE] 세션 CRUD API 개발 | ✅ Closed |

---

## 기술 스택

- Spring Boot 3.2.1
- Java 17
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Swagger/OpenAPI 3.0
- Lombok

---

## TODO (후속 작업)

1. **E2 FE UI 개발**
   - 코스 목록/상세 페이지
   - 코스 생성/수정 폼
   - 수강생 관리 UI
   - 세션 관리 UI
   - 과제 관리 UI

2. **추가 BE 기능**
   - 반복 일정 생성 (#98)
   - 세션 알림 스케줄러 (#99)
   - iCal 내보내기 (#100)
   - 파일 업로드 S3 연동 (#104)
   - 성적 계산 로직 (#117)

---

## 다음 단계

**Phase 5 Sprint 2 (계속)**: E2 코스 관리 FE UI 개발
- 또는 **Phase 5 Sprint 3**: E3 실시간 세미나 BE API
