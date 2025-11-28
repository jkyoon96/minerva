# Phase 5 Sprint 2 - E2 코스 관리 완성 (BE + FE)

## 개요

| 항목 | 내용 |
|------|------|
| **작업일** | 2025-01-29 |
| **Phase** | Phase 5 - Sprint 2 |
| **Epic** | E2 - 코스 관리 |
| **범위** | Backend API + Frontend UI |
| **관련 Issues (BE)** | #45, #46, #47, #53, #54, #56, #60, #61, #64, #70, #71, #74, #86, #92, #97 (15개) |
| **관련 Issues (FE)** | #49, #50, #51, #52, #57, #58, #59, #62, #63, #67, #68, #69, #72, #73, #78, #79, #80, #84, #85, #89, #90, #91, #95, #96, #101, #102, #103 (27개) |

---

## Part 1: Backend API (24개 엔드포인트)

### 1. 코스 CRUD API
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/courses` | 코스 생성 |
| GET | `/v1/courses` | 코스 목록 조회 |
| GET | `/v1/courses/{courseId}` | 코스 상세 조회 |
| PUT | `/v1/courses/{courseId}` | 코스 수정 |
| DELETE | `/v1/courses/{courseId}` | 코스 삭제 |
| POST | `/v1/courses/{courseId}/archive` | 코스 보관 |

### 2. 초대 링크 API
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/courses/{courseId}/invite-links` | 초대 링크 생성 |
| GET | `/v1/courses/{courseId}/invite-links` | 초대 링크 목록 |
| DELETE | `/v1/courses/{courseId}/invite-links/{linkId}` | 초대 링크 삭제 |
| GET | `/v1/courses/invite/{code}` | 초대 코드 검증 |

### 3. 수강 관리 API
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/student/courses` | 내 수강 코스 목록 |
| POST | `/v1/courses/join` | 초대 코드로 코스 등록 |
| POST | `/v1/courses/{courseId}/enrollments/csv` | CSV로 수강생 일괄 등록 |
| GET | `/v1/student/courses/{courseId}` | 학생용 코스 상세 |

### 4. 세션 CRUD API
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/courses/{courseId}/sessions` | 세션 생성 |
| GET | `/v1/courses/{courseId}/sessions` | 세션 목록 |
| GET | `/v1/sessions/{sessionId}` | 세션 상세 |
| PUT | `/v1/sessions/{sessionId}` | 세션 수정 |
| DELETE | `/v1/sessions/{sessionId}` | 세션 삭제 |

### 5. 과제 API
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/courses/{courseId}/assignments` | 과제 생성 |
| GET | `/v1/courses/{courseId}/assignments` | 과제 목록 |
| GET | `/v1/assignments/{assignmentId}` | 과제 상세 |
| POST | `/v1/assignments/{assignmentId}/submit` | 과제 제출 |

### 6. 대시보드 API
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/dashboard` | 교수 대시보드 |
| GET | `/v1/student/dashboard` | 학생 대시보드 |

### BE 파일 구조 (30개)
```
apps/backend/src/main/java/com/eduforum/api/domain/course/
├── controller/ (5개)
│   ├── CourseController.java
│   ├── SessionController.java
│   ├── EnrollmentController.java
│   ├── AssignmentController.java
│   └── DashboardController.java
├── service/ (5개)
│   ├── CourseService.java
│   ├── SessionService.java
│   ├── EnrollmentService.java
│   ├── AssignmentService.java
│   └── DashboardService.java
├── entity/ (6개)
├── repository/ (6개)
└── dto/ (16개)
```

---

## Part 2: Frontend UI (34개 파일)

### 신규 페이지 (7개)
| 페이지 | 경로 | 기능 |
|--------|------|------|
| 교수 대시보드 | `/professor` | 통계, 일정, 알림, 채점대기 |
| 교수 코스 목록 | `/professor/courses` | 코스 카드 목록 |
| 코스 생성 | `/professor/courses/new` | 코스 생성 폼 |
| 학생 대시보드 | `/student` | 통계, 일정, 마감과제, 성적 |
| 학생 코스 목록 | `/student/courses` | 수강 코스 목록 |
| 코스 상세 | `/courses/[courseId]` | 세션/과제/학생 탭 |
| 코스 설정 | `/courses/[courseId]/settings` | 설정, 초대링크, CSV |

### 신규 컴포넌트 (16개)
```
apps/frontend/src/components/
├── ui/
│   └── stat-card.tsx           # 통계 카드
├── dashboard/
│   ├── schedule-section.tsx    # 일정 섹션
│   └── notification-section.tsx # 알림 섹션
├── course/
│   ├── course-card.tsx         # 코스 카드
│   ├── course-form.tsx         # 코스 생성/수정 폼
│   ├── enroll-modal.tsx        # 수강 등록 모달
│   └── invite-link-manager.tsx # 초대 링크 관리
├── session/
│   └── session-modal.tsx       # 세션 생성/수정 모달
├── assignment/
│   ├── file-upload.tsx         # 파일 업로드
│   └── assignment-detail.tsx   # 과제 상세
└── enrollment/
    └── csv-upload.tsx          # CSV 업로드
```

### API 클라이언트 (4개)
```
apps/frontend/src/lib/api/
├── dashboard.ts    # 대시보드 API
├── sessions.ts     # 세션 API
├── assignments.ts  # 과제 API
└── enrollments.ts  # 수강/초대 API
```

### 타입 정의 (3개)
```
apps/frontend/src/types/
├── dashboard.ts    # 대시보드 타입
├── assignment.ts   # 과제 타입
└── session.ts      # 세션 타입
```

---

## 커밋 정보

### BE 커밋
```
commit d61c1b8
feat: Phase 5 Sprint 2 - E2 코스 관리 BE API 완성
30 files changed, 2868 insertions(+)
```

### FE 커밋
```
commit 1dd5c01
feat: Phase 5 Sprint 2 - E2 코스 관리 FE UI 완성
34 files changed, 4010 insertions(+)
```

---

## 완료된 GitHub Issues (42개)

### Backend Issues (15개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #45 | [E2-S0-T1] 대시보드 데이터 집계 API | ✅ Closed |
| #46 | [E2-S0-T2] 알림 조회 API | ✅ Closed |
| #47 | [E2-S0-T3] 채점 대기 과제 목록 API | ✅ Closed |
| #53 | [E2-S0S-T1] 학생 대시보드 데이터 집계 API | ✅ Closed |
| #54 | [E2-S0S-T2] 마감 임박 과제 목록 API | ✅ Closed |
| #56 | [E2-S0S-T4] 최근 성적 목록 API | ✅ Closed |
| #60 | [E2-S0SL-T1] 학생 코스 목록 API | ✅ Closed |
| #61 | [E2-S0SL-T2] 초대 코드 검증 및 등록 API | ✅ Closed |
| #64 | [E2-S0AS-T1] 과제 제출 API | ✅ Closed |
| #70 | [E2-S0CD-T1] 학생 코스 상세 API | ✅ Closed |
| #71 | [E2-S0CD-T2] 학생 세션/과제 현황 API | ✅ Closed |
| #74 | [E2-S1-T1] 코스 CRUD API | ✅ Closed |
| #86 | [E2-S2-T1] CSV 파싱 및 검증 로직 | ✅ Closed |
| #92 | [E2-S3-T1] 초대 링크 생성/검증 API | ✅ Closed |
| #97 | [E2-S4-T1] 세션 CRUD API | ✅ Closed |

### Frontend Issues (27개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #49 | [E2-S0-T5] 대시보드 레이아웃 및 통계 카드 UI | ✅ Closed |
| #50 | [E2-S0-T6] 오늘 일정 섹션 UI | ✅ Closed |
| #51 | [E2-S0-T7] 알림 섹션 UI | ✅ Closed |
| #52 | [E2-S0-T8] 채점 대기/위험 학생 섹션 UI | ✅ Closed |
| #57 | [E2-S0S-T5] 학생 대시보드 레이아웃 UI | ✅ Closed |
| #58 | [E2-S0S-T6] 학생 오늘 일정 섹션 UI | ✅ Closed |
| #59 | [E2-S0S-T7] 마감 임박/참여도/성적 섹션 UI | ✅ Closed |
| #62 | [E2-S0SL-T3] 학생 코스 목록 UI | ✅ Closed |
| #63 | [E2-S0SL-T4] 코스 등록 모달 UI | ✅ Closed |
| #67 | [E2-S0AS-T4] 파일 업로드 UI | ✅ Closed |
| #68 | [E2-S0AS-T5] 과제 상세 및 루브릭 표시 UI | ✅ Closed |
| #69 | [E2-S0AS-T6] 제출 확인 모달 UI | ✅ Closed |
| #72 | [E2-S0CD-T3] 학생 코스 상세 UI | ✅ Closed |
| #73 | [E2-S0CD-T4] 세션/과제/녹화 목록 컴포넌트 | ✅ Closed |
| #78 | [E2-S1-T5] 코스 생성 폼 UI | ✅ Closed |
| #79 | [E2-S1-T6] 코스 설정 페이지 UI | ✅ Closed |
| #80 | [E2-S1-T7] 코스 목록 및 카드 컴포넌트 | ✅ Closed |
| #84 | [E2-S1-1-T4] 코스 설정 페이지 UI | ✅ Closed |
| #85 | [E2-S1-1-T5] 위험 영역 UI 및 확인 모달 | ✅ Closed |
| #89 | [E2-S2-T4] CSV 업로드 UI | ✅ Closed |
| #90 | [E2-S2-T5] 검증 결과 미리보기 UI | ✅ Closed |
| #91 | [E2-S2-T6] 오류 표시 및 수정 UI | ✅ Closed |
| #95 | [E2-S3-T4] 초대 링크 관리 UI | ✅ Closed |
| #96 | [E2-S3-T5] 초대 수락 플로우 UI | ✅ Closed |
| #101 | [E2-S4-T5] 캘린더 뷰 UI | ✅ Closed |
| #102 | [E2-S4-T6] 세션 생성/수정 모달 | ✅ Closed |
| #103 | [E2-S4-T7] 알림 표시 컴포넌트 | ✅ Closed |

---

## 기술 스택

### Backend
- Spring Boot 3.2.1, Java 17
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Swagger/OpenAPI 3.0

### Frontend
- Next.js 14 (App Router)
- React 18, TypeScript
- Tailwind CSS
- Zustand + TanStack Query
- shadcn/ui, Lucide React

---

## 주요 기능

### 교수 기능
- 코스 생성/수정/삭제/보관
- 세션 관리 (생성/수정/삭제)
- 과제 관리 및 채점
- 초대 링크 관리
- CSV 수강생 일괄 등록
- 대시보드 (통계, 일정, 알림)

### 학생 기능
- 초대 코드로 코스 등록
- 수강 코스 목록/상세 조회
- 과제 제출 (파일 첨부)
- 대시보드 (통계, 마감과제, 성적)

---

## 다음 단계

**Phase 5 Sprint 3**: E3 실시간 세미나 BE API
- WebSocket 기반 실시간 통신
- 화상 세션 관리
- 화면 공유, 채팅
