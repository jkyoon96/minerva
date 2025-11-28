# Phase 5 개발 전략

> **작성일**: 2025-01-29
> **목적**: Agent를 활용한 효율적인 기능 개발 전략
> **현재 상태**: 인프라 Phase 1~4 완료, 기능 개발 Phase 5 시작

---

## 1. 현재 상태 분석

### 1.1 완료된 인프라

| Phase | 설명 | 완료 Issues |
|-------|------|-------------|
| Phase 1 | 인프라 기반 | #282 (DB), #285 (BE), #288 (FE) |
| Phase 2 | 공통 모듈 | #286, #287, #289, #290 |
| Phase 3 | DB 최적화 | #283, #284 |
| Phase 4 | API 문서화 | #291 |

### 1.2 남은 Issues 통계

| Priority | Ready | Blocked | 총 Open |
|----------|-------|---------|---------|
| **P0 (MVP)** | ~70개 | ~50개 | ~120개 |
| **P1 (v1.0)** | ~30개 | ~30개 | ~60개 |
| **P2 (v2.0+)** | ~7개 | ~5개 | ~12개 |

### 1.3 Epic별 분류

| Epic | 설명 | Ready Issues |
|------|------|--------------|
| E1 | 사용자 인증 | 약 10개 |
| E2 | 코스 관리 | 약 25개 |
| E3 | 실시간 세미나 | 약 20개 |
| E4 | 액티브 러닝 | 약 25개 |
| E5 | 평가/피드백 | 약 15개 |
| E6 | 학습 분석 | 약 10개 |

---

## 2. 개발 전략

### 2.1 개발 순서 원칙

```
1. BE API 먼저 → FE UI 연동
2. 의존성 없는 Ready 이슈부터 처리
3. 핵심 흐름(Critical Path) 우선
4. Epic 단위로 완성도 확보
```

### 2.2 권장 개발 순서

#### Sprint 1: 인증 시스템 (E1) - 최우선
```
BE: E1-S1-T1 → E1-S2 → E1-S3 → E1-S5 → E1-S6
FE: E1-S1-T6 → E1-S2-T5 → E1-S3-T4 → E1-S5-T4,T5 → E1-S6-T4,T6
```

**이유**: 모든 기능의 기반이 되는 인증 시스템

#### Sprint 2: 대시보드/코스 기본 (E2) - 핵심 흐름
```
BE: E2-S0-T1~T3 → E2-S1-T1 → E2-S3-T1 → E2-S4-T1
FE: E2-S0-T5~T8 → E2-S1-T5~T7 → E2-S3-T4~T5 → E2-S4-T5~T7
```

**이유**: 로그인 후 사용자가 가장 먼저 보는 화면

#### Sprint 3: 세션 기본 (E3) - 핵심 가치
```
BE: E3-S1-T1 → E3-S4-T1,T3 → E3-S5-T1
FE: E3-S1-T5~T7 → E3-S4-T4~T7 → E3-S5-T3~T5
```

**이유**: 실시간 세미나는 제품의 핵심 가치

#### Sprint 4: 액티브 러닝 (E4)
```
BE: E4-S1-T1 → E4-S3-T1 → E4-S5-T1
FE: E4-S1-T4~T6 → E4-S3-T4~T6 → E4-S5-T5~T7
```

#### Sprint 5: 평가/분석 (E5, E6)
```
BE: E5-S5-T1 → E5-S3-T1 → E6-S1-T1
FE: E5-S5-T5~T6 → E5-S3-T5~T7 → E6-S1-T5~T7
```

---

## 3. Agent 활용 전략

### 3.1 효율적인 프롬프트 패턴

#### 패턴 1: Epic 단위 BE API 개발
```
Phase 5 - Epic 1 (E1) 사용자 인증 API 개발을 진행해주세요.

## 작업 범위
- #2 [E1-S1-T1] 회원가입 API
- #7 [E1-S2] 로그인 API
- #17,#18 [E1-S3] OAuth 통합
- #28 [E1-S5-T1] 비밀번호 재설정 API
- #33 [E1-S6-T1] 역할/권한 API

## 기존 코드 참조
- AuthController.java (샘플 구현 있음)
- User, Role, Permission 엔티티 (BE-003에서 생성)
- API 문서: docs/07-api-specification.md

## 요구사항
- Spring Security + JWT 기반
- Swagger 문서화 어노테이션 포함
- 단위 테스트 작성
- 작업 완료 후 Issue 번호 알려주기
```

#### 패턴 2: Story 단위 FE 개발
```
Phase 5 - [E1-S1] 회원가입 UI 개발을 진행해주세요.

## 작업 범위
- #7 [E1-S1-T6] 회원가입 폼 UI
- #8 [E1-S1-T7] 폼 유효성 검사
- #9 [E1-S1-T8] 이메일 인증 완료 페이지

## 기존 코드 참조
- 컴포넌트: apps/frontend/src/components/ui/
- 훅: apps/frontend/src/hooks/useAuth.ts
- API: apps/frontend/src/lib/api/auth.ts
- 와이어프레임: docs/wireframes/e1-auth/

## 요구사항
- shadcn/ui 컴포넌트 활용
- React Hook Form + Zod 유효성 검사
- 반응형 디자인
- 작업 완료 후 Issue 번호 알려주기
```

#### 패턴 3: BE + FE 병렬 개발
```
Phase 5 - [E2-S0] 대시보드 기능을 BE/FE 병렬로 개발해주세요.

## BE 작업 (Agent 1)
- #45 대시보드 데이터 집계 API
- #46 알림 조회 API
- #47 채점 대기 과제 목록 API

## FE 작업 (Agent 2)
- #49 대시보드 레이아웃 UI
- #50 오늘 일정 섹션 UI
- #51 알림 섹션 UI
- #52 채점 대기 섹션 UI

## 통합 요구사항
- API 응답 형식 통일 (ApiResponse<T>)
- 에러 처리 일관성
- 로딩 상태 처리
```

### 3.2 병렬 처리 전략

```
┌─────────────────────────────────────────────────────────────┐
│                    Phase 5 병렬 개발                         │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  [Sprint 1]                                                  │
│     Agent 1 (BE): E1 인증 API ─────────────────────────────→│
│     Agent 2 (FE): E1 인증 UI  ─────────────────────────────→│
│                                                              │
│  [Sprint 2]                                                  │
│     Agent 1 (BE): E2 대시보드/코스 API ────────────────────→│
│     Agent 2 (FE): E2 대시보드/코스 UI  ────────────────────→│
│                                                              │
│  [Sprint 3]                                                  │
│     Agent 1 (BE): E3 세션 API ─────────────────────────────→│
│     Agent 2 (FE): E3 세션 UI  ─────────────────────────────→│
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 3.3 단계별 지시 예시

#### Step 1: 시작 명령
```
"Phase 5 Sprint 1을 시작합니다. E1 인증 시스템을 개발해주세요.
BE API 먼저 완성 후 FE UI를 연동합니다.
Ready 상태인 Issue만 처리하고, Blocked는 건너뜁니다.
완료된 Issue는 Close 해주세요."
```

#### Step 2: 진행 확인
```
"현재 E1 진행 상황을 알려주세요.
- 완료된 Issue 번호
- 진행 중인 Issue 번호
- 발생한 문제점"
```

#### Step 3: 다음 단계 진행
```
"E1 인증 시스템이 완료되었습니다.
E2 코스 관리로 넘어가 주세요.
BE API (#45, #46, #47, #74)부터 시작합니다."
```

---

## 4. 작업 체크리스트

### 4.1 Sprint별 완료 기준

#### Sprint 1: 인증 시스템 ✅
- [ ] 회원가입 API + UI
- [ ] 로그인 API + UI
- [ ] JWT 토큰 발급/갱신
- [ ] OAuth (Google, Microsoft)
- [ ] 비밀번호 재설정
- [ ] 역할/권한 관리
- [ ] 테스트 통과

#### Sprint 2: 대시보드/코스 ✅
- [ ] 교수 대시보드 API + UI
- [ ] 학생 대시보드 API + UI
- [ ] 코스 CRUD API + UI
- [ ] 학생 등록 (초대 코드)
- [ ] CSV 일괄 등록
- [ ] 세션 스케줄링
- [ ] 테스트 통과

#### Sprint 3: 세션 기본 ✅
- [ ] WebRTC 미디어 서버
- [ ] 세미나 입장/대기실
- [ ] 비디오 그리드 레이아웃
- [ ] 채팅 WebSocket
- [ ] 손들기/반응
- [ ] 화면 공유
- [ ] 테스트 통과

#### Sprint 4: 액티브 러닝 ✅
- [ ] 투표 CRUD + 실시간
- [ ] 퀴즈 문제 은행
- [ ] 퀴즈 진행 + 타이머
- [ ] 분반 토론
- [ ] 테스트 통과

#### Sprint 5: 평가/분석 ✅
- [ ] 참여도 점수 계산
- [ ] AI 채점 통합
- [ ] 코드 실행 환경
- [ ] 실시간 대시보드
- [ ] 리포트 생성
- [ ] 테스트 통과

---

## 5. 주의사항

### 5.1 Agent 지시 시 유의점

1. **범위 명확히**: Issue 번호와 Task ID 명시
2. **참조 파일 안내**: 기존 코드, 설계 문서 경로 제공
3. **완료 기준 명시**: 테스트, 문서화, Commit 규칙
4. **Blocked 이슈 제외**: Ready 상태만 처리
5. **병렬 처리 활용**: BE/FE 동시 진행 가능

### 5.2 코드 품질 기준

```yaml
Backend:
  - Spring Security 보안 규칙 준수
  - Swagger 문서화 필수
  - 단위 테스트 커버리지 70%+
  - 트랜잭션 관리

Frontend:
  - TypeScript strict mode
  - 컴포넌트 재사용성
  - 반응형 디자인 (모바일 지원)
  - 접근성 (a11y) 고려
```

### 5.3 Git 커밋 규칙

```
feat: [Epic-Story-Task] 기능 설명
fix: [Issue #번호] 버그 수정 설명
refactor: [Issue #번호] 리팩토링 설명

예시:
feat: [E1-S1-T1] 회원가입 API 구현 (#2)
feat: [E1-S1-T6] 회원가입 폼 UI 구현 (#7)
```

---

## 6. 권장 시작 명령

### 첫 번째 명령 (Sprint 1 시작)

```
Phase 5 Sprint 1을 시작합니다.

## 목표
E1 사용자 인증 시스템 완성

## BE 작업 (우선)
1. #2 [E1-S1-T1] 회원가입 API
2. 로그인/토큰 관리 API (기존 AuthController 보완)
3. #17 [E1-S3-T1] Google OAuth
4. #18 [E1-S3-T2] Microsoft OAuth
5. #28 [E1-S5-T1] 비밀번호 재설정 API
6. #33 [E1-S6-T1] 역할/권한 관리

## 참조 파일
- apps/backend/src/main/java/com/eduforum/api/domain/auth/
- apps/backend/src/main/java/com/eduforum/api/security/
- docs/07-api-specification.md

## 요구사항
- 기존 AuthController.java 구조 활용
- User, Role, Permission 엔티티 연동
- Swagger 문서화
- 완료 후 Issue Close

BE API 완성 후 FE UI 작업을 진행합니다.
```

---

## 7. 예상 일정

| Sprint | 기간 | 주요 산출물 |
|--------|------|------------|
| Sprint 1 | 1주 | 인증 시스템 완성 |
| Sprint 2 | 1주 | 대시보드/코스 관리 |
| Sprint 3 | 1.5주 | 실시간 세션 기본 |
| Sprint 4 | 1주 | 액티브 러닝 도구 |
| Sprint 5 | 1주 | 평가/분석 기능 |
| **총계** | **~5.5주** | **MVP 완성** |

---

## 8. 문서 참조

| 문서 | 경로 | 용도 |
|------|------|------|
| API 설계서 | `docs/07-api-specification.md` | API 스펙 |
| 기능 세분화 | `docs/04-feature-breakdown.md` | Epic/Story/Task |
| DB 설계 | `docs/06-database-design.md` | 엔티티 관계 |
| 와이어프레임 | `docs/wireframes/` | UI 설계 |
| BE 가이드 | `apps/backend/README.md` | 백엔드 구조 |
| FE 가이드 | `apps/frontend/README.md` | 프론트엔드 구조 |
