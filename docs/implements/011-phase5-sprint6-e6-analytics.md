# Phase 5 Sprint 6 - E6 학습 분석 완성 (BE + FE)

## 개요

| 항목 | 내용 |
|------|------|
| **작업일** | 2025-01-29 |
| **Phase** | Phase 5 - Sprint 6 |
| **Epic** | E6 - 학습 분석 |
| **범위** | Backend API + Frontend UI |
| **관련 Issues (BE)** | #255, #256, #257, #258, #262, #263, #264, #265, #266, #270, #271, #272, #276, #277, #278 (15개) |
| **관련 Issues (FE)** | #259, #260, #261, #267, #268, #269, #273, #274, #275, #279, #280, #281 (12개) |

---

## Part 1: Backend API (22개 엔드포인트)

### 1. 실시간 분석 API (RealTimeAnalyticsController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/analytics/realtime/{sessionId}` | 세션 실시간 통계 |
| GET | `/v1/analytics/trends/{courseId}` | 코스 트렌드 분석 |
| GET | `/v1/analytics/snapshots/{courseId}` | 스냅샷 조회 |
| GET | `/v1/analytics/compare/{courseId}` | 세션 간 비교 |

### 2. 리포트 API (ReportController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/analytics/reports/student/{studentId}` | 학생 리포트 조회 |
| POST | `/v1/analytics/reports/student/generate` | 학생 리포트 생성 |
| GET | `/v1/analytics/reports/course/{courseId}` | 코스 리포트 조회 |
| POST | `/v1/analytics/reports/course/generate` | 코스 리포트 생성 |
| GET | `/v1/analytics/reports/export/{reportId}` | 리포트 내보내기 |
| GET | `/v1/analytics/reports/history` | 리포트 이력 조회 |

### 3. 위험 알림 API (RiskAlertController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/analytics/risks/course/{courseId}` | 코스별 위험 학생 |
| GET | `/v1/analytics/risks/student/{studentId}` | 학생 위험 지표 |
| GET | `/v1/analytics/alerts` | 알림 목록 조회 |
| PUT | `/v1/analytics/alerts/{alertId}/acknowledge` | 알림 확인 |
| PUT | `/v1/analytics/alerts/{alertId}/resolve` | 알림 해결 |
| POST | `/v1/analytics/intervention` | 개입 기록 저장 |
| PUT | `/v1/analytics/risks/thresholds/{courseId}` | 임계값 설정 |

### 4. 네트워크 분석 API (NetworkAnalyticsController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/analytics/network/{courseId}` | 네트워크 그래프 |
| GET | `/v1/analytics/network/{courseId}/clusters` | 클러스터 분석 |
| GET | `/v1/analytics/network/students/{studentId}/interactions` | 학생 상호작용 |
| GET | `/v1/analytics/network/{courseId}/centrality` | 중심성 지표 |
| GET | `/v1/analytics/network/{courseId}/timeline` | 시간별 변화 |

### BE 파일 구조 (60개)
```
apps/backend/src/main/java/com/eduforum/api/domain/analytics/
├── controller/ (4개)
│   ├── RealTimeAnalyticsController.java
│   ├── ReportController.java
│   ├── RiskAlertController.java
│   └── NetworkAnalyticsController.java
├── service/ (8개)
│   ├── RealTimeAnalyticsService.java
│   ├── DataAggregationService.java
│   ├── StudentReportService.java
│   ├── CourseReportService.java
│   ├── ExportService.java
│   ├── RiskDetectionService.java
│   ├── AlertService.java
│   └── NetworkAnalysisService.java
├── entity/ (10개)
│   ├── AnalyticsSnapshot.java
│   ├── LearningMetric.java
│   ├── StudentReport.java
│   ├── CourseReport.java
│   ├── RiskIndicator.java
│   ├── RiskAlert.java
│   ├── InteractionLog.java
│   ├── NetworkNode.java
│   ├── NetworkEdge.java
│   └── StudentCluster.java
├── repository/ (10개)
├── dto/ (23개)
│   ├── realtime/ (5개)
│   ├── report/ (6개)
│   ├── risk/ (6개)
│   └── network/ (6개)
├── enums/ (5개)
│   ├── MetricType.java
│   ├── ReportPeriod.java
│   ├── RiskLevel.java
│   ├── AlertStatus.java
│   └── InteractionType.java
└── websocket/ (1개)
    └── AnalyticsWebSocketController.java
```

### DB 마이그레이션
```
apps/backend/src/main/resources/db/migration/
└── V006__Create_Analytics_Schema.sql (294 lines)
    - analytics_snapshots
    - learning_metrics
    - student_reports
    - course_reports
    - risk_indicators
    - risk_alerts
    - interaction_logs
    - network_nodes
    - network_edges
    - student_clusters
    + 5 custom PostgreSQL enums
    + indexes
```

---

## Part 2: Frontend UI (27개 파일)

### 신규 페이지 (6개)
| 페이지 | 경로 | 기능 |
|--------|------|------|
| 실시간 분석 | `/analytics` | 실시간 대시보드 |
| 리포트 목록 | `/analytics/reports` | 리포트 목록/검색 |
| 학생 리포트 | `/analytics/reports/student/[studentId]` | 개인 학습 분석 |
| 코스 리포트 | `/analytics/reports/course/[courseId]` | 코스 종합 분석 |
| 위험 관리 | `/analytics/risks` | 위험 학생 대시보드 |
| 네트워크 | `/analytics/network/[courseId]` | 상호작용 네트워크 |

### 신규 컴포넌트 (18개)
```
apps/frontend/src/components/analytics/
├── realtime/ (5개)
│   ├── realtime-dashboard.tsx    # 실시간 대시보드
│   ├── participation-chart.tsx   # 참여도 차트
│   ├── engagement-heatmap.tsx    # 참여 히트맵
│   ├── live-metrics.tsx          # 실시간 메트릭
│   └── session-timeline.tsx      # 세션 타임라인
├── reports/ (5개)
│   ├── student-report-card.tsx   # 학생 리포트 카드
│   ├── course-report-card.tsx    # 코스 리포트 카드
│   ├── learning-chart.tsx        # 학습 차트
│   ├── export-button.tsx         # PDF/Excel 내보내기
│   └── metric-comparison.tsx     # 지표 비교
├── risk/ (4개)
│   ├── risk-student-list.tsx     # 위험 학생 목록
│   ├── risk-indicator.tsx        # 위험 지표 표시
│   ├── alert-card.tsx            # 알림 카드
│   └── intervention-modal.tsx    # 개입 모달
└── network/ (4개)
    ├── network-graph.tsx         # 네트워크 그래프 (Canvas)
    ├── cluster-view.tsx          # 클러스터 뷰
    ├── node-detail.tsx           # 노드 상세
    └── interaction-list.tsx      # 상호작용 목록
```

### API 클라이언트 (1개)
```
apps/frontend/src/lib/api/
└── analytics.ts                  # E6 API (405 lines)
```

### 상태 관리 (1개)
```
apps/frontend/src/stores/
└── analyticsStore.ts             # Zustand (347 lines)
```

### 타입 정의 (1개)
```
apps/frontend/src/types/
└── analytics.ts                  # TypeScript 타입 (559 lines)
```

---

## 커밋 정보

### BE 커밋
```
commit 3ed4db5
feat: Phase 5 Sprint 6 - E6 학습 분석 BE API 완성
60 files changed, 4047 insertions(+)
```

### FE 커밋
```
commit a372cb9
feat: Phase 5 Sprint 6 - E6 학습 분석 FE UI 완성
27 files changed, 4349 insertions(+)
```

---

## 완료된 GitHub Issues (27개)

### Backend Issues (15개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #255 | [E6-S1-T1] 실시간 참여 데이터 수집 서비스 | ✅ Closed |
| #256 | [E6-S1-T2] 참여도 통계 실시간 계산 | ✅ Closed |
| #257 | [E6-S1-T3] 참여도 WebSocket 스트리밍 | ✅ Closed |
| #258 | [E6-S1-T4] 참여도 알림 로직 | ✅ Closed |
| #262 | [E6-S2-T1] 학습 데이터 집계 배치 작업 | ✅ Closed |
| #263 | [E6-S2-T2] 개인별 분석 API | ✅ Closed |
| #264 | [E6-S2-T3] 코스 분석 API | ✅ Closed |
| #265 | [E6-S2-T4] 리포트 PDF 생성 | ✅ Closed |
| #266 | [E6-S2-T5] Excel 내보내기 로직 | ✅ Closed |
| #270 | [E6-S3-T1] 위험 지표 계산 로직 | ✅ Closed |
| #271 | [E6-S3-T2] 위험 학생 탐지 스케줄러 | ✅ Closed |
| #272 | [E6-S3-T3] 알림 발송 로직 | ✅ Closed |
| #276 | [E6-S4-T1] 상호작용 데이터 수집 로직 | ✅ Closed |
| #277 | [E6-S4-T2] 네트워크 분석 알고리즘 | ✅ Closed |
| #278 | [E6-S4-T3] 클러스터링 로직 | ✅ Closed |

### Frontend Issues (12개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #259 | [E6-S1-T5] 막대 그래프 컴포넌트 | ✅ Closed |
| #260 | [E6-S1-T6] 트렌드 그래프 컴포넌트 | ✅ Closed |
| #261 | [E6-S1-T7] 대시보드 레이아웃 UI | ✅ Closed |
| #267 | [E6-S2-T6] 개인 리포트 UI | ✅ Closed |
| #268 | [E6-S2-T7] 코스 리포트 UI | ✅ Closed |
| #269 | [E6-S2-T8] 분포 그래프 컴포넌트 | ✅ Closed |
| #273 | [E6-S3-T4] 위험 학생 목록 UI | ✅ Closed |
| #274 | [E6-S3-T5] 개입 제안 카드 UI | ✅ Closed |
| #275 | [E6-S3-T6] 임계값 설정 UI | ✅ Closed |
| #279 | [E6-S4-T4] 네트워크 그래프 UI | ✅ Closed |
| #280 | [E6-S4-T5] 시간별 애니메이션 | ✅ Closed |
| #281 | [E6-S4-T6] 개인 네트워크 분석 UI | ✅ Closed |

---

## 기술 스택

### Backend
- Spring Boot 3.2.1, Java 17
- Spring Data JPA
- Spring WebSocket (STOMP)
- Spring Security + JWT
- PostgreSQL (JSONB 활용)
- Swagger/OpenAPI 3.0

### Frontend
- Next.js 14 (App Router)
- React 18, TypeScript
- Tailwind CSS
- Zustand + TanStack Query
- shadcn/ui, Lucide React
- date-fns, recharts

---

## 주요 기능

### 실시간 분석
- WebSocket 기반 실시간 참여도 스트리밍
- 세션별 참여 통계 (발언 시간, 채팅, 반응)
- 참여 히트맵 시각화
- 트렌드 분석 (주간/월간)

### 학습 리포트
- 학생 개별 분석 리포트
- 코스 종합 분석 리포트
- PDF/Excel 내보내기
- 주차별 진행률 추적
- 강점/약점 분석

### 위험 학생 관리
- 5가지 위험 지표 계산 (출석, 참여도, 과제, 퀴즈, 종합)
- 3단계 위험 수준 (LOW, MEDIUM, HIGH)
- 실시간 알림 시스템
- 개입 기록 관리
- 임계값 커스터마이징

### 상호작용 네트워크
- Force-directed 네트워크 그래프 (Canvas)
- 학생 클러스터링 분석
- 중심성 지표 (연결, 매개, 근접)
- 시간대별 네트워크 변화
- 상호작용 히스토리

---

## Phase 5 Sprint 완료 현황

| Sprint | Epic | BE 파일 | FE 파일 | Issues | 상태 |
|--------|------|---------|---------|--------|------|
| Sprint 1 | E1 인증 | 26 | 13 | 10 | ✅ 완료 |
| Sprint 2 | E2 코스 | 30 | 34 | 42 | ✅ 완료 |
| Sprint 3 | E3 세미나 | 72 | 38 | 50 | ✅ 완료 |
| Sprint 4 | E4 액티브 | 78 | 42 | 50 | ✅ 완료 |
| Sprint 5 | E5 평가 | 72 | 31 | 34 | ✅ 완료 |
| Sprint 6 | E6 분석 | 60 | 27 | 27 | ✅ 완료 |
| **합계** | | **338** | **185** | **213** | |

---

## 다음 단계

**Phase 5 완료** - 모든 6개 Epic 개발 완료!

### TODO (후속 작업)
1. **통합 테스트**
   - E2E 테스트 작성
   - API 통합 테스트

2. **성능 최적화**
   - WebSocket 스케일링
   - 캐싱 전략 적용

3. **실제 서비스 연동**
   - 이메일 서비스 연동
   - OAuth 실제 API 연동
   - 파일 스토리지 (S3) 연동

4. **배포 준비**
   - Docker 컨테이너화
   - CI/CD 파이프라인
   - 모니터링 설정
