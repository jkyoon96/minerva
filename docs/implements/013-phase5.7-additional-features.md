# Phase 5.7: 추가 기능 구현 (옵션 A)

**작업일**: 2024-11-29
**커밋**: 69dd424

## 개요

외부 서비스 연동 없이 구현 가능한 핵심 기능들을 구현했습니다.

## 구현 이슈

| 이슈 | 제목 | 상태 |
|------|------|------|
| #13 | [E1-S2-T2] 로그인 시도 제한 로직 | ✅ 완료 |
| #76 | [E2-S7-T1] 채점 기준 CRUD API | ✅ 완료 |
| #77 | [E2-S7-T2] 루브릭 항목 관리 API | ✅ 완료 |
| #87 | [E2-S2-T2] 사용자 생성 및 등록 처리 | ✅ 완료 |
| #100 | [E2-S4-T3] iCal 내보내기 API | ✅ 완료 |

## 구현 상세

### 1. 로그인 시도 제한 (#13)

**파일**:
- `LoginAttempt.java` - 로그인 시도 엔티티
- `LoginAttemptRepository.java` - 리포지토리
- `LoginAttemptService.java` - 서비스 로직

**기능**:
- 5회 연속 실패 시 15분 계정 잠금
- IP 기반 시도 추적
- 성공 시 시도 기록 초기화
- 잠금 해제 시간 자동 계산

```java
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    public boolean isAccountLocked(String email) {
        List<LoginAttempt> recentAttempts = repository
            .findRecentAttempts(email, cutoffTime);
        long failedCount = recentAttempts.stream()
            .filter(a -> !a.getSuccessful())
            .count();
        return failedCount >= MAX_ATTEMPTS;
    }
}
```

### 2. 채점 기준 관리 (#76, #77)

**파일**:
- `GradingCriteria.java` - 채점 기준 엔티티
- `RubricItem.java` - 루브릭 항목 엔티티
- `GradingCriteriaRepository.java`
- `RubricItemRepository.java`
- `GradingCriteriaService.java`
- `GradingCriteriaController.java`

**기능**:
- 코스별 채점 기준 생성/수정/삭제
- 루브릭 항목 관리 (점수 레벨, 설명)
- 기준 복사 기능
- 과제와 연결

**API 엔드포인트**:
```
POST   /v1/courses/{courseId}/grading-criteria
GET    /v1/courses/{courseId}/grading-criteria
GET    /v1/grading-criteria/{id}
PUT    /v1/grading-criteria/{id}
DELETE /v1/grading-criteria/{id}
POST   /v1/grading-criteria/{id}/rubric-items
PUT    /v1/rubric-items/{id}
DELETE /v1/rubric-items/{id}
```

### 3. TA 배정 관리

**파일**:
- `CourseTA.java` - TA 배정 엔티티
- `CourseTARepository.java`
- `CourseTAService.java`
- `CourseTAController.java`

**기능**:
- TA 배정/해제
- 세분화된 권한 관리:
  - `canGrade`: 채점 권한
  - `canManageContent`: 콘텐츠 관리 권한
  - `canViewAnalytics`: 분석 조회 권한
  - `canModerateDiscussions`: 토론 관리 권한
- 배정된 TA 목록 조회

**API 엔드포인트**:
```
POST   /v1/courses/{courseId}/tas
GET    /v1/courses/{courseId}/tas
PUT    /v1/courses/{courseId}/tas/{taId}
DELETE /v1/courses/{courseId}/tas/{taId}
```

### 4. 일괄 등록 (#87)

**파일**:
- `BulkEnrollmentService.java` - 일괄 등록 서비스
- `BulkEnrollmentController.java` - 컨트롤러
- `BulkEnrollmentRequest.java` - 요청 DTO
- `BulkEnrollmentResult.java` - 결과 DTO
- `EnrollmentPreview.java` - 미리보기 DTO

**기능**:
- CSV 파싱 (이메일, 이름, 성, 역할)
- 신규 사용자 자동 생성 (임시 비밀번호)
- 기존 사용자 등록 처리
- 미리보기 기능 (유효성 검증)
- 건너뛰기/오류 처리 옵션

**CSV 형식**:
```csv
email,firstName,lastName,role
student1@univ.edu,홍,길동,STUDENT
student2@univ.edu,김,철수,STUDENT
```

**API 엔드포인트**:
```
POST /v1/courses/{courseId}/enrollments/bulk/preview
POST /v1/courses/{courseId}/enrollments/bulk
```

### 5. iCal 캘린더 내보내기 (#100)

**파일**:
- `ICalService.java` - iCal 생성 서비스
- `ICalController.java` - 컨트롤러

**기능**:
- RFC 5545 준수 iCal 형식
- 코스별 캘린더 내보내기
- 학생용 전체 일정 내보내기
- 교수용 강의 일정 내보내기
- 세션 상태에 따른 이벤트 상태 설정

**API 엔드포인트**:
```
GET /v1/courses/{courseId}/calendar.ics
GET /v1/calendar/student.ics
GET /v1/calendar/professor.ics
```

**iCal 출력 예시**:
```ics
BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//EduForum//Course Calendar//EN
BEGIN:VEVENT
UID:session-1@eduforum.com
DTSTART:20241201T090000Z
DTEND:20241201T110000Z
SUMMARY:CS101: 알고리즘 기초
LOCATION:Online
STATUS:CONFIRMED
END:VEVENT
END:VCALENDAR
```

## 데이터베이스 마이그레이션

**파일**: `V009__Create_Additional_Features.sql`

```sql
-- 로그인 시도 테이블
CREATE TABLE login_attempts (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    successful BOOLEAN NOT NULL DEFAULT FALSE,
    failure_reason VARCHAR(100),
    attempted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 채점 기준 테이블
CREATE TABLE grading_criteria (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES courses(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    max_score INTEGER NOT NULL DEFAULT 100,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 루브릭 항목 테이블
CREATE TABLE rubric_items (
    id BIGSERIAL PRIMARY KEY,
    criteria_id BIGINT REFERENCES grading_criteria(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    max_points INTEGER NOT NULL,
    order_index INTEGER DEFAULT 0
);

-- TA 배정 테이블
CREATE TABLE course_tas (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT REFERENCES courses(id),
    user_id BIGINT REFERENCES users(id),
    can_grade BOOLEAN DEFAULT TRUE,
    can_manage_content BOOLEAN DEFAULT FALSE,
    can_view_analytics BOOLEAN DEFAULT TRUE,
    can_moderate_discussions BOOLEAN DEFAULT TRUE,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(course_id, user_id)
);
```

## 통계

| 항목 | 수량 |
|------|------|
| 생성된 파일 | 28개 |
| 총 코드 라인 | 2,684줄 |
| API 엔드포인트 | 15개 |
| DB 테이블 | 4개 |

## 테스트

```bash
# 로그인 잠금 테스트
curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"wrong"}'
# 5회 실패 후 잠금 확인

# iCal 내보내기 테스트
curl -H "Authorization: Bearer {token}" \
  http://localhost:8080/v1/courses/1/calendar.ics
```

## 다음 단계

- 옵션 B: 외부 서비스 연동 (이메일, 파일 스토리지)
- 옵션 C: 프로젝트 마무리 (Docker, CI/CD, 테스트)
