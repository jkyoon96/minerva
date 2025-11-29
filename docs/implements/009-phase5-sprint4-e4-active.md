# Phase 5 Sprint 4 - E4 액티브 러닝 완성 (BE + FE)

## 개요

| 항목 | 내용 |
|------|------|
| **작업일** | 2025-01-29 |
| **Phase** | Phase 5 - Sprint 4 |
| **Epic** | E4 - 액티브 러닝 도구 |
| **범위** | Backend API + Frontend UI |
| **관련 Issues (BE)** | #171, #172, #173, #177, #178, #179, #183, #184, #185, #189, #190, #191, #195, #196, #197, #198, #202, #203, #204, #209, #210, #211, #215, #216, #217 (25개) |
| **관련 Issues (FE)** | #174, #175, #176, #180, #181, #182, #186, #187, #188, #192, #193, #194, #199, #200, #201, #205, #206, #207, #208, #212, #213, #214, #218, #219, #220 (25개) |

---

## Part 1: Backend API (58개 엔드포인트)

### 1. 투표 API (PollController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/polls` | 투표 생성 |
| GET | `/v1/polls/{pollId}` | 투표 상세 조회 |
| GET | `/v1/polls/course/{courseId}` | 코스별 투표 목록 |
| PUT | `/v1/polls/{pollId}` | 투표 수정 |
| DELETE | `/v1/polls/{pollId}` | 투표 삭제 |
| POST | `/v1/polls/{pollId}/start` | 투표 시작 |
| POST | `/v1/polls/{pollId}/stop` | 투표 종료 |
| POST | `/v1/polls/{pollId}/responses` | 응답 제출 |
| GET | `/v1/polls/{pollId}/results` | 결과 조회 |
| POST | `/v1/polls/templates` | 템플릿 저장 |
| GET | `/v1/polls/templates` | 템플릿 목록 |

### 2. 문제 은행 API (QuizController - Questions)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/questions` | 문제 생성 |
| GET | `/v1/questions` | 문제 목록 (필터) |
| GET | `/v1/questions/{questionId}` | 문제 상세 |
| PUT | `/v1/questions/{questionId}` | 문제 수정 |
| DELETE | `/v1/questions/{questionId}` | 문제 삭제 |
| GET | `/v1/questions/tags` | 태그 목록 |
| POST | `/v1/questions/import` | 문제 가져오기 |
| GET | `/v1/questions/export` | 문제 내보내기 |

### 3. 퀴즈 API (QuizController - Quizzes)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/quizzes` | 퀴즈 생성 |
| GET | `/v1/quizzes` | 퀴즈 목록 |
| GET | `/v1/quizzes/{quizId}` | 퀴즈 상세 |
| PUT | `/v1/quizzes/{quizId}` | 퀴즈 수정 |
| DELETE | `/v1/quizzes/{quizId}` | 퀴즈 삭제 |
| POST | `/v1/quizzes/{quizId}/start` | 퀴즈 시작 |
| POST | `/v1/quizzes/{quizId}/submit` | 답안 제출 |
| GET | `/v1/quizzes/{quizId}/results` | 결과 조회 |

### 4. 분반 API (BreakoutController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/seminars/{roomId}/breakouts` | 분반 생성 |
| GET | `/v1/seminars/{roomId}/breakouts` | 분반 목록 |
| PUT | `/v1/breakouts/{breakoutId}` | 분반 수정 |
| DELETE | `/v1/breakouts/{breakoutId}` | 분반 삭제 |
| POST | `/v1/breakouts/{breakoutId}/assign` | 참가자 배정 |
| POST | `/v1/breakouts/{breakoutId}/start` | 분반 시작 |
| POST | `/v1/breakouts/{breakoutId}/close` | 분반 종료 |
| POST | `/v1/breakouts/broadcast` | 브로드캐스트 |
| GET | `/v1/breakouts/{breakoutId}/status` | 상태 조회 |
| POST | `/v1/breakouts/{breakoutId}/join` | 교수 이동 |

### 5. 화이트보드 API (WhiteboardController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/seminars/{roomId}/whiteboards` | 화이트보드 생성 |
| GET | `/v1/whiteboards/{whiteboardId}` | 상태 조회 |
| PUT | `/v1/whiteboards/{whiteboardId}` | 저장 |
| POST | `/v1/whiteboards/{whiteboardId}/elements` | 요소 추가 |
| PUT | `/v1/whiteboards/{whiteboardId}/elements/{elementId}` | 요소 수정 |
| DELETE | `/v1/whiteboards/{whiteboardId}/elements/{elementId}` | 요소 삭제 |
| POST | `/v1/whiteboards/{whiteboardId}/clear` | 전체 삭제 |
| POST | `/v1/whiteboards/{whiteboardId}/export` | 이미지 내보내기 |
| DELETE | `/v1/whiteboards/{whiteboardId}` | 화이트보드 삭제 |

### 6. 토론/발언 API (DiscussionController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/seminars/{roomId}/speaking-queue` | 발언 신청 |
| DELETE | `/v1/speaking-queue/{queueId}` | 신청 취소 |
| GET | `/v1/seminars/{roomId}/speaking-queue` | 대기열 조회 |
| POST | `/v1/speaking-queue/{queueId}/start` | 발언 시작 |
| POST | `/v1/speaking-queue/{queueId}/end` | 발언 종료 |
| GET | `/v1/seminars/{roomId}/participation` | 참여 통계 |
| POST | `/v1/seminars/{roomId}/threads` | 스레드 생성 |
| GET | `/v1/seminars/{roomId}/threads` | 스레드 목록 |
| POST | `/v1/threads/{threadId}/replies` | 답글 작성 |

### BE 파일 구조 (78개)
```
apps/backend/src/main/java/com/eduforum/api/domain/active/
├── controller/ (5개)
│   ├── PollController.java
│   ├── QuizController.java
│   ├── BreakoutController.java
│   ├── WhiteboardController.java
│   └── DiscussionController.java
├── service/ (6개)
│   ├── PollService.java
│   ├── QuestionBankService.java
│   ├── QuizService.java
│   ├── BreakoutService.java
│   ├── WhiteboardService.java
│   └── DiscussionService.java
├── entity/ (15개)
│   ├── Poll.java, PollOption.java, PollResponse.java, PollSession.java
│   ├── Question.java, QuestionTag.java
│   ├── Quiz.java, QuizSession.java, QuizAnswer.java
│   ├── BreakoutRoom.java, BreakoutParticipant.java
│   ├── Whiteboard.java, WhiteboardElement.java
│   ├── SpeakingQueue.java, DiscussionThread.java
├── repository/ (15개)
├── dto/ (27개)
│   ├── poll/ (6개)
│   ├── quiz/ (7개)
│   ├── breakout/ (5개)
│   ├── whiteboard/ (4개)
│   └── discussion/ (5개)
└── enums/ (8개)
    ├── PollType.java, PollStatus.java
    ├── QuestionType.java, QuizStatus.java
    ├── BreakoutStatus.java, AssignmentMethod.java
    ├── WhiteboardTool.java, SpeakingStatus.java
```

---

## Part 2: Frontend UI (42개 파일)

### 신규 페이지 (8개)
| 페이지 | 경로 | 기능 |
|--------|------|------|
| 투표 목록 | `/active/polls` | 투표 목록, 검색, 필터 |
| 투표 생성 | `/active/polls/new` | 투표 생성 폼 |
| 투표 상세 | `/active/polls/[pollId]` | 응답/결과 탭 |
| 퀴즈 목록 | `/active/quizzes` | 퀴즈 목록 |
| 퀴즈 생성 | `/active/quizzes/new` | 퀴즈 생성 |
| 퀴즈 진행 | `/active/quizzes/[quizId]` | 퀴즈 풀기 |
| 문제 은행 | `/active/questions` | 문제 관리 |
| 분반 룸 | `/active/breakouts/[roomId]` | 분반 참여 |

### 신규 컴포넌트 (28개)
```
apps/frontend/src/components/active/
├── poll/ (6개)
│   ├── poll-card.tsx           # 투표 카드
│   ├── poll-form.tsx           # 투표 생성 폼
│   ├── poll-option-input.tsx   # 옵션 입력
│   ├── poll-response.tsx       # 응답 UI
│   ├── poll-results.tsx        # 결과 차트
│   └── poll-templates.tsx      # 템플릿 선택
├── quiz/ (8개)
│   ├── question-form.tsx       # 문제 에디터
│   ├── question-bank-list.tsx  # 문제 목록
│   ├── question-preview.tsx    # 문제 미리보기
│   ├── question-type-input.tsx # 유형별 입력
│   ├── quiz-form.tsx           # 퀴즈 생성
│   ├── quiz-session.tsx        # 퀴즈 진행
│   ├── quiz-timer.tsx          # 타이머
│   └── quiz-results.tsx        # 결과 표시
├── whiteboard/ (3개)
│   ├── whiteboard-canvas.tsx   # 캔버스
│   ├── whiteboard-toolbar.tsx  # 도구 팔레트
│   └── whiteboard-cursors.tsx  # 협업 커서
├── breakout/ (5개)
│   ├── breakout-setup.tsx      # 분반 설정
│   ├── breakout-assignment.tsx # 배정 UI
│   ├── breakout-monitor.tsx    # 모니터링
│   ├── breakout-room.tsx       # 학생 뷰
│   └── broadcast-modal.tsx     # 브로드캐스트
└── discussion/ (5개)
    ├── speaking-queue.tsx      # 발언 대기열
    ├── speaking-timer.tsx      # 발언 타이머
    ├── participation-stats.tsx # 참여 통계
    ├── discussion-threads.tsx  # 토론 스레드
    └── thread-reply.tsx        # 답글
```

### 커스텀 훅 (1개)
```
apps/frontend/src/hooks/
└── useWhiteboard.ts           # 캔버스 드로잉 로직
```

### API 클라이언트 (1개)
```
apps/frontend/src/lib/api/
└── active.ts                  # E4 API (700+ lines)
```

### 상태 관리 (1개)
```
apps/frontend/src/stores/
└── activeStore.ts             # Zustand (500+ lines)
```

### 타입 정의 (1개)
```
apps/frontend/src/types/
└── active.ts                  # TypeScript 타입 (500+ lines)
```

---

## 커밋 정보

### BE 커밋
```
commit fa674c8
feat: Phase 5 Sprint 4 - E4 액티브 러닝 BE API 완성
78 files changed, 4628 insertions(+)
```

### FE 커밋
```
commit 21c8dca
feat: Phase 5 Sprint 4 - E4 액티브 러닝 FE UI 완성
42 files changed, 5169 insertions(+)
```

---

## 완료된 GitHub Issues (50개)

### Backend Issues (25개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #171 | [E4-S1-T1] 투표 CRUD API 개발 | ✅ Closed |
| #172 | [E4-S1-T2] 투표 타입별 스키마 설계 | ✅ Closed |
| #173 | [E4-S1-T3] 투표 시작/종료 실시간 이벤트 | ✅ Closed |
| #177 | [E4-S2-T1] 투표 응답 저장 API | ✅ Closed |
| #178 | [E4-S2-T2] 실시간 응답률 브로드캐스트 | ✅ Closed |
| #179 | [E4-S2-T3] 투표 결과 집계 API | ✅ Closed |
| #183 | [E4-S3-T1] 문제 CRUD API 개발 | ✅ Closed |
| #184 | [E4-S3-T2] 태그 및 카테고리 관리 API | ✅ Closed |
| #185 | [E4-S3-T3] 문제 가져오기/내보내기 로직 | ✅ Closed |
| #189 | [E4-S4-T1] 퀴즈 세션 관리 API | ✅ Closed |
| #190 | [E4-S4-T2] 답안 제출 및 채점 API | ✅ Closed |
| #191 | [E4-S4-T3] 시간 제한 관리 로직 | ✅ Closed |
| #195 | [E4-S5-T1] 분반 생성/관리 API | ✅ Closed |
| #196 | [E4-S5-T2] 자동 배정 알고리즘 구현 | ✅ Closed |
| #197 | [E4-S5-T3] 분반 미디어 서버 연결 로직 | ✅ Closed |
| #198 | [E4-S5-T4] 분반 타이머 및 알림 | ✅ Closed |
| #202 | [E4-S6-T1] 분반 상태 조회 API | ✅ Closed |
| #203 | [E4-S6-T2] 교수 분반 이동 로직 | ✅ Closed |
| #204 | [E4-S6-T3] 브로드캐스트 메시지 API | ✅ Closed |
| #209 | [E4-S7-T1] 화이트보드 실시간 동기화 | ✅ Closed |
| #210 | [E4-S7-T2] 화이트보드 저장/로드 API | ✅ Closed |
| #211 | [E4-S7-T3] 이미지 내보내기 API | ✅ Closed |
| #215 | [E4-S8-T1] 발언 큐 관리 API | ✅ Closed |
| #216 | [E4-S8-T2] 발언 시간 추적 로직 | ✅ Closed |
| #217 | [E4-S8-T3] 균등 참여 분석 API | ✅ Closed |

### Frontend Issues (25개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #174 | [E4-S1-T4] 투표 생성 폼 UI | ✅ Closed |
| #175 | [E4-S1-T5] 투표 타입별 입력 컴포넌트 | ✅ Closed |
| #176 | [E4-S1-T6] 템플릿 저장/불러오기 UI | ✅ Closed |
| #180 | [E4-S2-T4] 투표 응답 UI (학생용) | ✅ Closed |
| #181 | [E4-S2-T5] 결과 차트 컴포넌트 | ✅ Closed |
| #182 | [E4-S2-T6] 정답/해설 표시 UI | ✅ Closed |
| #186 | [E4-S3-T4] 문제 은행 UI 개발 | ✅ Closed |
| #187 | [E4-S3-T5] 문제 유형별 에디터 | ✅ Closed |
| #188 | [E4-S3-T6] 문제 미리보기 컴포넌트 | ✅ Closed |
| #192 | [E4-S4-T4] 퀴즈 진행 UI 개발 | ✅ Closed |
| #193 | [E4-S4-T5] 타이머 컴포넌트 | ✅ Closed |
| #194 | [E4-S4-T6] 결과 요약 UI | ✅ Closed |
| #199 | [E4-S5-T5] 분반 설정 UI | ✅ Closed |
| #200 | [E4-S5-T6] 드래그 앤 드롭 배정 UI | ✅ Closed |
| #201 | [E4-S5-T7] 분반 이동 트랜지션 | ✅ Closed |
| #205 | [E4-S6-T4] 분반 모니터링 대시보드 UI | ✅ Closed |
| #206 | [E4-S6-T5] 분반 이동 UI | ✅ Closed |
| #207 | [E4-S6-T6] 브로드캐스트 메시지 UI | ✅ Closed |
| #208 | [E4-S6-T7] 학생용 분반 참여 UI | ✅ Closed |
| #212 | [E4-S7-T4] 화이트보드 캔버스 컴포넌트 | ✅ Closed |
| #213 | [E4-S7-T5] 드로잉 도구 UI | ✅ Closed |
| #214 | [E4-S7-T6] 협업 커서 표시 | ✅ Closed |
| #218 | [E4-S8-T4] 발언 큐 UI | ✅ Closed |
| #219 | [E4-S8-T5] 발언 타이머 UI | ✅ Closed |
| #220 | [E4-S8-T6] 질문 스레드 시각화 | ✅ Closed |

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
- Canvas API (화이트보드)

---

## 주요 기능

### 투표 시스템
- 객관식, 평점, 워드클라우드, 주관식, 예/아니오
- 실시간 결과 집계 및 차트
- 익명 투표 옵션
- 템플릿 저장/불러오기

### 퀴즈 시스템
- 문제 은행 (태그, 카테고리)
- 객관식, 참/거짓, 단답형, 서술형
- 타이머 및 자동 제출
- 자동 채점 및 결과 표시
- JSON 가져오기/내보내기

### 분반 (Breakout Rooms)
- 랜덤/밸런스/수동 배정
- 타이머 및 알림
- 교수 룸 이동
- 브로드캐스트 메시지
- 실시간 상태 모니터링

### 화이트보드
- 펜, 형광펜, 지우개
- 도형 (사각형, 원, 선, 화살표)
- 텍스트 도구
- Undo/Redo
- 이미지 내보내기

### 토론/발언 큐
- 발언 신청 대기열
- 발언 시간 추적
- 참여 통계
- 토론 스레드

---

## 다음 단계

**Phase 5 Sprint 5**: E5 평가 및 피드백
- 객관식 자동 채점
- AI 채점 (NLP)
- 코드 평가 환경
- 동료 평가
