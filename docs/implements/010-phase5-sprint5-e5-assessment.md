# Phase 5 Sprint 5 - E5 평가/피드백 완성 (BE + FE)

## 개요

| 항목 | 내용 |
|------|------|
| **작업일** | 2025-01-29 |
| **Phase** | Phase 5 - Sprint 5 |
| **Epic** | E5 - 평가 및 피드백 |
| **범위** | Backend API + Frontend UI |
| **관련 Issues (BE)** | #221, #222, #225, #226, #227, #228, #231, #232, #233, #234, #238, #239, #240, #243, #244, #245, #246, #249, #250, #251 (20개) |
| **관련 Issues (FE)** | #223, #224, #229, #230, #235, #236, #237, #241, #242, #247, #248, #252, #253, #254 (14개) |

---

## Part 1: Backend API (33개 엔드포인트)

### 1. 자동 채점 API (GradingController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/grading/auto/{quizSessionId}` | 퀴즈 자동 채점 |
| GET | `/v1/grading/{resultId}` | 채점 결과 조회 |
| GET | `/v1/grading/assignment/{assignmentId}` | 과제별 성적 조회 |
| GET | `/v1/grading/statistics/{quizId}` | 정답률 통계 |

### 2. AI 채점 API (GradingController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/grading/ai/{submissionId}` | AI 채점 실행 |
| PUT | `/v1/grading/{resultId}` | 성적 수정 |
| GET | `/v1/grading/ai/pending` | 검토 대기 목록 |
| POST | `/v1/grading/ai/batch` | 일괄 AI 채점 |

### 3. 코드 평가 API (CodeEvaluationController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/code/submit` | 코드 제출 |
| GET | `/v1/code/submissions/{submissionId}` | 제출 조회 |
| GET | `/v1/code/submissions/assignment/{assignmentId}` | 과제별 제출 목록 |
| POST | `/v1/code/run/{submissionId}` | 코드 실행 |
| GET | `/v1/code/results/{submissionId}` | 실행 결과 조회 |
| POST | `/v1/code/plagiarism/{assignmentId}` | 표절 검사 실행 |
| GET | `/v1/code/plagiarism/report/{reportId}` | 표절 리포트 조회 |
| GET | `/v1/code/test-cases/{assignmentId}` | 테스트 케이스 조회 |

### 4. 피드백 API (FeedbackController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/feedback/generate/{submissionId}` | AI 피드백 생성 |
| GET | `/v1/feedback/{feedbackId}` | 피드백 조회 |
| GET | `/v1/feedback/student/{studentId}` | 학생별 피드백 목록 |
| GET | `/v1/recommendations/{studentId}` | 학습 자료 추천 |
| POST | `/v1/feedback/save` | 피드백 저장 |

### 5. 참여도 API (ParticipationController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/participation/events` | 이벤트 기록 |
| GET | `/v1/participation/scores/{courseId}` | 코스별 점수 조회 |
| GET | `/v1/participation/scores/{courseId}/{studentId}` | 학생별 점수 |
| PUT | `/v1/participation/weights/{courseId}` | 가중치 설정 |
| GET | `/v1/participation/weights/{courseId}` | 가중치 조회 |
| GET | `/v1/participation/dashboard/{studentId}` | 대시보드 데이터 |

### 6. 동료 평가 API (PeerReviewController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/peer-review/assignments/{assignmentId}/setup` | 동료 평가 설정 |
| GET | `/v1/peer-review/assignments/{assignmentId}` | 배정 목록 조회 |
| POST | `/v1/peer-review/submit` | 평가 제출 |
| GET | `/v1/peer-review/received/{submissionId}` | 받은 평가 조회 |
| GET | `/v1/peer-review/given/{studentId}` | 제출한 평가 조회 |
| GET | `/v1/peer-review/results/{assignmentId}` | 집계 결과 조회 |

### BE 파일 구조 (72개)
```
apps/backend/src/main/java/com/eduforum/api/domain/assessment/
├── controller/ (5개)
│   ├── GradingController.java
│   ├── CodeEvaluationController.java
│   ├── FeedbackController.java
│   ├── ParticipationController.java
│   └── PeerReviewController.java
├── service/ (7개)
│   ├── AutoGradingService.java
│   ├── AiGradingService.java
│   ├── CodeExecutionService.java
│   ├── PlagiarismService.java
│   ├── FeedbackService.java
│   ├── ParticipationService.java
│   └── PeerReviewService.java
├── entity/ (13개)
│   ├── GradingResult.java
│   ├── AnswerStatistics.java
│   ├── CodeSubmission.java
│   ├── TestCase.java
│   ├── ExecutionResult.java
│   ├── PlagiarismReport.java
│   ├── Feedback.java
│   ├── LearningResource.java
│   ├── ParticipationEvent.java
│   ├── ParticipationScore.java
│   ├── ParticipationWeight.java
│   ├── PeerReview.java
│   └── PeerReviewAssignment.java
├── repository/ (13개)
├── dto/ (26개)
│   ├── grading/ (6개)
│   ├── code/ (6개)
│   ├── feedback/ (4개)
│   ├── participation/ (5개)
│   └── peer/ (5개)
└── enums/ (6개)
    ├── GradingType.java
    ├── GradingStatus.java
    ├── SubmissionStatus.java
    ├── ExecutionStatus.java
    ├── EventType.java
    └── FeedbackType.java
```

---

## Part 2: Frontend UI (31개 파일)

### 신규 페이지 (6개)
| 페이지 | 경로 | 기능 |
|--------|------|------|
| 성적 목록 | `/assessment/grades` | 성적 개요 |
| 성적 상세 | `/assessment/grades/[assignmentId]` | 과제별 성적 |
| 코드 제출 | `/assessment/code/[submissionId]` | 코드 상세 |
| 피드백 | `/assessment/feedback` | 피드백 목록 |
| 참여도 | `/assessment/participation` | 참여도 대시보드 |
| 동료 평가 | `/assessment/peer-review/[assignmentId]` | 동료 평가 |

### 신규 컴포넌트 (22개)
```
apps/frontend/src/components/assessment/
├── grading/ (4개)
│   ├── grading-result-card.tsx    # 성적 카드
│   ├── answer-statistics.tsx      # 정답률 차트
│   ├── grade-breakdown.tsx        # 성적 세부
│   └── explanation-panel.tsx      # 해설 패널
├── ai-grading/ (3개)
│   ├── ai-grading-result.tsx      # AI 채점 결과
│   ├── grade-editor.tsx           # 성적 수정
│   └── pending-reviews.tsx        # 검토 대기
├── code/ (4개)
│   ├── code-editor.tsx            # 코드 에디터
│   ├── test-case-list.tsx         # 테스트 케이스
│   ├── execution-result.tsx       # 실행 결과
│   └── plagiarism-result.tsx      # 표절 결과
├── feedback/ (3개)
│   ├── feedback-card.tsx          # 피드백 카드
│   ├── resource-recommendation.tsx # 추천 자료
│   └── improvement-tips.tsx       # 개선 팁
├── participation/ (4개)
│   ├── participation-dashboard.tsx # 참여도 대시보드
│   ├── participation-chart.tsx    # 참여도 차트
│   ├── weight-config.tsx          # 가중치 설정
│   └── event-history.tsx          # 이벤트 기록
└── peer/ (4개)
    ├── peer-review-form.tsx       # 평가 폼
    ├── reviews-received.tsx       # 받은 평가
    ├── reviews-given.tsx          # 제출한 평가
    └── peer-result-summary.tsx    # 결과 요약
```

### API 클라이언트 (1개)
```
apps/frontend/src/lib/api/
└── assessment.ts                  # E5 API (489 lines)
```

### 상태 관리 (1개)
```
apps/frontend/src/stores/
└── assessmentStore.ts             # Zustand (291 lines)
```

### 타입 정의 (1개)
```
apps/frontend/src/types/
└── assessment.ts                  # TypeScript 타입 (484 lines)
```

---

## 커밋 정보

### BE 커밋
```
commit 3806f53
feat: Phase 5 Sprint 5 - E5 평가/피드백 BE API 완성
72 files changed, 4552 insertions(+)
```

### FE 커밋
```
commit b0b2e14
feat: Phase 5 Sprint 5 - E5 평가/피드백 FE UI 완성
31 files changed, 3665 insertions(+)
```

---

## 완료된 GitHub Issues (34개)

### Backend Issues (20개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #221 | [E5-S1-T1] 객관식 채점 로직 개발 | ✅ Closed |
| #222 | [E5-S1-T2] 정답률 통계 수집 로직 | ✅ Closed |
| #225 | [E5-S2-T1] NLP 모델 통합 | ✅ Closed |
| #226 | [E5-S2-T2] 유사도 계산 API | ✅ Closed |
| #227 | [E5-S2-T3] 키워드 분석 로직 | ✅ Closed |
| #228 | [E5-S2-T4] 채점 결과 저장/수정 API | ✅ Closed |
| #231 | [E5-S3-T1] 코드 실행 환경 구성 | ✅ Closed |
| #232 | [E5-S3-T2] 테스트 케이스 실행 API | ✅ Closed |
| #233 | [E5-S3-T3] 시간/메모리 제한 로직 | ✅ Closed |
| #234 | [E5-S3-T4] 표절 검사 로직 | ✅ Closed |
| #238 | [E5-S4-T1] 피드백 생성 로직 | ✅ Closed |
| #239 | [E5-S4-T2] 학습 자료 추천 API | ✅ Closed |
| #240 | [E5-S4-T3] 피드백 저장 API | ✅ Closed |
| #243 | [E5-S5-T1] 참여 이벤트 수집 로직 | ✅ Closed |
| #244 | [E5-S5-T2] 참여도 점수 계산 알고리즘 | ✅ Closed |
| #245 | [E5-S5-T3] 가중치 설정 API | ✅ Closed |
| #246 | [E5-S5-T4] 참여도 조회 API | ✅ Closed |
| #249 | [E5-S6-T1] 동료 평가 CRUD API | ✅ Closed |
| #250 | [E5-S6-T2] 이상치 제거 알고리즘 | ✅ Closed |
| #251 | [E5-S6-T3] 평가 결과 집계 로직 | ✅ Closed |

### Frontend Issues (14개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #223 | [E5-S1-T3] 채점 결과 UI | ✅ Closed |
| #224 | [E5-S1-T4] 해설 표시 컴포넌트 | ✅ Closed |
| #229 | [E5-S2-T5] AI 채점 결과 UI (교수용) | ✅ Closed |
| #230 | [E5-S2-T6] 점수 수정 인터페이스 | ✅ Closed |
| #235 | [E5-S3-T5] 코드 에디터 컴포넌트 | ✅ Closed |
| #236 | [E5-S3-T6] 실행 결과 UI | ✅ Closed |
| #237 | [E5-S3-T7] 표절 검사 결과 UI | ✅ Closed |
| #241 | [E5-S4-T4] 피드백 표시 UI | ✅ Closed |
| #242 | [E5-S4-T5] 추천 자료 카드 컴포넌트 | ✅ Closed |
| #247 | [E5-S5-T5] 참여도 가중치 설정 UI | ✅ Closed |
| #248 | [E5-S5-T6] 학생용 참여도 대시보드 | ✅ Closed |
| #252 | [E5-S6-T4] 동료 평가 폼 UI | ✅ Closed |
| #253 | [E5-S6-T5] 평가 결과 요약 UI | ✅ Closed |
| #254 | [E5-S6-T6] 동료 평가 설정 UI | ✅ Closed |

---

## 기술 스택

### Backend
- Spring Boot 3.2.1, Java 17
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL (JSONB 활용)
- Swagger/OpenAPI 3.0

### Frontend
- Next.js 14 (App Router)
- React 18, TypeScript
- Tailwind CSS
- Zustand + TanStack Query
- shadcn/ui, Lucide React
- date-fns

---

## 주요 기능

### 자동 채점
- 객관식/참거짓 자동 채점
- 정답률 통계 수집
- 문제별 정답률 분석
- 해설 표시

### AI 채점
- 서술형/단답형 AI 채점 (시뮬레이션)
- 유사도 계산 (코사인 유사도)
- 키워드 분석
- 신뢰도 점수 표시
- 교수 검토/수정 인터페이스

### 코드 평가
- 코드 제출 및 저장
- 테스트 케이스 실행
- 시간/메모리 제한
- 표절 검사 (유사도 기반)
- 실행 결과 상세 표시

### AI 피드백
- 성과 기반 피드백 생성
- 학습 자료 추천
- 개선 제안

### 참여도 점수
- 10가지 이벤트 유형 추적
- 가중치 설정 (코스별)
- 점수 계산 알고리즘
- 참여도 대시보드

### 동료 평가
- 평가자 자동 배정
- 익명 평가
- 이상치 제거 (IQR 기반)
- 결과 집계 및 표시

---

## 다음 단계

**Phase 5 Sprint 6**: E6 학습 분석
- 실시간 참여 분석
- 학습 리포트
- 위험 학생 알림
- 상호작용 네트워크
