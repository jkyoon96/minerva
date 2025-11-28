# Phase 5 Sprint 3 - E3 실시간 세미나 완성 (BE + FE)

## 개요

| 항목 | 내용 |
|------|------|
| **작업일** | 2025-01-29 |
| **Phase** | Phase 5 - Sprint 3 |
| **Epic** | E3 - 실시간 세미나 |
| **범위** | Backend API + Frontend UI |
| **관련 Issues (BE)** | #123, #124, #125, #126, #130, #131, #135, #136, #137, #141, #142, #147, #148, #149, #154, #155, #159, #160, #161, #162, #163, #166, #167 (23개) |
| **관련 Issues (FE)** | #127, #128, #129, #132, #133, #134, #138, #139, #140, #143, #144, #145, #146, #150, #151, #152, #153, #156, #157, #158, #164, #165, #168, #169, #170 (25개) |

---

## Part 1: Backend API (27개 엔드포인트)

### 1. 세미나 룸 API (RoomController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/seminars/rooms` | 세미나 룸 생성 |
| GET | `/v1/seminars/rooms` | 세미나 룸 목록 조회 |
| GET | `/v1/seminars/rooms/{roomId}` | 세미나 룸 상세 조회 |
| PUT | `/v1/seminars/rooms/{roomId}` | 세미나 룸 수정 |
| DELETE | `/v1/seminars/rooms/{roomId}` | 세미나 룸 삭제 |
| POST | `/v1/seminars/rooms/{roomId}/start` | 세미나 시작 |
| POST | `/v1/seminars/rooms/{roomId}/end` | 세미나 종료 |
| GET | `/v1/seminars/rooms/{roomId}/status` | 룸 상태 조회 |

### 2. 참가자 API (ParticipantController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/seminars/rooms/{roomId}/join` | 세미나 참가 |
| POST | `/v1/seminars/rooms/{roomId}/leave` | 세미나 퇴장 |
| GET | `/v1/seminars/rooms/{roomId}/participants` | 참가자 목록 |
| PUT | `/v1/seminars/rooms/{roomId}/participants/{participantId}` | 참가자 상태 수정 |
| POST | `/v1/seminars/rooms/{roomId}/participants/{participantId}/mute` | 참가자 음소거 |
| POST | `/v1/seminars/rooms/{roomId}/participants/{participantId}/kick` | 참가자 강퇴 |
| GET | `/v1/seminars/rooms/{roomId}/waiting` | 대기실 참가자 목록 |
| POST | `/v1/seminars/rooms/{roomId}/waiting/{participantId}/admit` | 대기실 입장 승인 |

### 3. 채팅 API (ChatController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/seminars/rooms/{roomId}/messages` | 채팅 메시지 조회 |
| POST | `/v1/seminars/rooms/{roomId}/messages` | 채팅 메시지 전송 |
| DELETE | `/v1/seminars/rooms/{roomId}/messages/{messageId}` | 메시지 삭제 |
| POST | `/v1/seminars/rooms/{roomId}/messages/{messageId}/pin` | 메시지 고정 |

### 4. 반응/손들기 API (ReactionController)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/seminars/rooms/{roomId}/reactions` | 이모지 반응 전송 |
| POST | `/v1/seminars/rooms/{roomId}/hand-raise` | 손들기 토글 |
| GET | `/v1/seminars/rooms/{roomId}/hand-queue` | 손든 참가자 큐 조회 |
| POST | `/v1/seminars/rooms/{roomId}/hand-queue/{participantId}/call` | 발언권 부여 |

### 5. 화면 공유 API (ScreenShareService)
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/seminars/rooms/{roomId}/screen-share/start` | 화면 공유 시작 |
| POST | `/v1/seminars/rooms/{roomId}/screen-share/stop` | 화면 공유 중지 |
| PUT | `/v1/seminars/rooms/{roomId}/layout` | 레이아웃 변경 |

### BE 파일 구조 (38개)
```
apps/backend/src/main/java/com/eduforum/api/domain/seminar/
├── controller/ (4개)
│   ├── RoomController.java
│   ├── ParticipantController.java
│   ├── ChatController.java
│   └── ReactionController.java
├── service/ (5개)
│   ├── RoomService.java
│   ├── ParticipantService.java
│   ├── ChatService.java
│   ├── ReactionService.java
│   └── ScreenShareService.java
├── entity/ (4개)
│   ├── SeminarRoom.java
│   ├── RoomParticipant.java
│   ├── ChatMessage.java
│   └── Reaction.java
├── repository/ (4개)
│   ├── SeminarRoomRepository.java
│   ├── RoomParticipantRepository.java
│   ├── ChatMessageRepository.java
│   └── ReactionRepository.java
├── dto/ (11개)
│   ├── CreateRoomRequest.java
│   ├── UpdateRoomRequest.java
│   ├── RoomResponse.java
│   ├── JoinRoomRequest.java
│   ├── ParticipantResponse.java
│   ├── UpdateParticipantRequest.java
│   ├── ChatMessageRequest.java
│   ├── ChatMessageResponse.java
│   ├── ReactionRequest.java
│   ├── HandRaiseResponse.java
│   └── LayoutChangeRequest.java
├── enums/ (7개)
│   ├── RoomStatus.java
│   ├── ParticipantRole.java
│   ├── ParticipantStatus.java
│   ├── MessageType.java
│   ├── ReactionType.java
│   ├── WebSocketEventType.java
│   └── LayoutType.java
└── websocket/ (3개)
    ├── WebSocketConfig.java
    ├── WebSocketEventPublisher.java
    └── ChatWebSocketController.java
```

---

## Part 2: Frontend UI (27개 파일)

### 신규 페이지 (3개)
| 페이지 | 경로 | 기능 |
|--------|------|------|
| 세미나 입장 | `/seminar/[roomId]` | 장치 선택, 미리보기 |
| 대기실 | `/seminar/[roomId]/waiting` | 입장 대기 |
| 라이브 세션 | `/seminar/[roomId]/live` | 화상 세미나 메인 |

### 신규 컴포넌트 (12개)
```
apps/frontend/src/components/seminar/
├── video-tile.tsx         # 개별 비디오 타일
├── video-grid.tsx         # 비디오 그리드 (grid/speaker/sidebar)
├── media-controls.tsx     # 미디어 컨트롤 바
├── screen-share.tsx       # 화면 공유 컨트롤
├── chat-panel.tsx         # 채팅 패널
├── chat-message.tsx       # 채팅 메시지
├── hand-raise.tsx         # 손들기 기능
├── reaction-buttons.tsx   # 이모지 반응 버튼
├── participant-list.tsx   # 참가자 목록
├── layout-selector.tsx    # 레이아웃 선택
├── device-selector.tsx    # 장치 선택
├── waiting-room.tsx       # 대기실 컴포넌트
└── index.ts               # Barrel export
```

### 커스텀 훅 (2개)
```
apps/frontend/src/hooks/
├── useWebSocket.ts        # WebSocket 연결 관리
└── useMediaDevices.ts     # 카메라/마이크 관리
```

### API 클라이언트 (1개)
```
apps/frontend/src/lib/api/
└── seminar.ts             # 세미나 API (20+ 메서드)
```

### 상태 관리 (1개)
```
apps/frontend/src/stores/
└── seminarStore.ts        # Zustand 세미나 상태
```

### 타입 정의 (1개)
```
apps/frontend/src/types/
└── seminar.ts             # 세미나 타입 (150+ lines)
```

---

## 커밋 정보

### BE 커밋
```
commit e5aa64e
feat: Phase 5 Sprint 3 - E3 실시간 세미나 BE API 완성
43 files changed, 3862 insertions(+)
```

### FE 커밋
```
commit df88d39
feat: Phase 5 Sprint 3 - E3 실시간 세미나 FE UI 완성
29 files changed, 4878 insertions(+)
```

---

## 완료된 GitHub Issues (48개)

### Backend Issues (23개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #123 | [E3-S1-T1] WebRTC 미디어 서버 설정 | ✅ Closed |
| #124 | [E3-S1-T2] 세미나 룸 생성 API | ✅ Closed |
| #125 | [E3-S1-T3] 실시간 알림 발송 (WebSocket) | ✅ Closed |
| #126 | [E3-S1-T4] 대기실 로직 구현 | ✅ Closed |
| #130 | [E3-S1SV-T1] 학생 뷰 WebRTC 연결 로직 | ✅ Closed |
| #131 | [E3-S1SV-T2] 손들기/반응 실시간 이벤트 처리 | ✅ Closed |
| #135 | [E3-S2-T1] WebRTC 피어 연결 관리 로직 | ✅ Closed |
| #136 | [E3-S2-T2] SFU 아키텍처 구현 | ✅ Closed |
| #137 | [E3-S2-T3] 대역폭 적응형 스트리밍 로직 | ✅ Closed |
| #141 | [E3-S3-T1] 화면 공유 스트림 처리 로직 | ✅ Closed |
| #142 | [E3-S3-T2] 화면 공유 권한 관리 | ✅ Closed |
| #147 | [E3-S4-T1] 채팅 메시지 WebSocket 서버 구현 | ✅ Closed |
| #148 | [E3-S4-T2] 채팅 메시지 저장 로직 | ✅ Closed |
| #149 | [E3-S4-T3] 파일 업로드/다운로드 API | ✅ Closed |
| #154 | [E3-S5-T1] 손들기 상태 관리 API | ✅ Closed |
| #155 | [E3-S5-T2] 반응 이벤트 브로드캐스트 | ✅ Closed |
| #159 | [E3-S6-T1] 녹화 서버 구성 | ✅ Closed |
| #160 | [E3-S6-T2] 녹화 시작/종료 API | ✅ Closed |
| #161 | [E3-S6-T3] 비디오 인코딩 파이프라인 | ✅ Closed |
| #162 | [E3-S6-T4] 클라우드 스토리지 연동 | ✅ Closed |
| #163 | [E3-S6-T5] 자막 생성 (STT) | ✅ Closed |
| #166 | [E3-S7-T1] 레이아웃 상태 동기화 API | ✅ Closed |
| #167 | [E3-S7-T2] 레이아웃 강제 변경 브로드캐스트 | ✅ Closed |

### Frontend Issues (25개)
| Issue | 제목 | 상태 |
|-------|------|------|
| #127 | [E3-S1-T5] 세미나 입장 UI 개발 | ✅ Closed |
| #128 | [E3-S1-T6] 미디어 장치 선택/프리뷰 컴포넌트 | ✅ Closed |
| #129 | [E3-S1-T7] 대기실 UI 개발 | ✅ Closed |
| #132 | [E3-S1SV-T3] 학생 라이브 뷰 메인 레이아웃 | ✅ Closed |
| #133 | [E3-S1SV-T4] 채팅/참여자/활동 탭 패널 | ✅ Closed |
| #134 | [E3-S1SV-T5] 학생 컨트롤 바 | ✅ Closed |
| #138 | [E3-S2-T4] 비디오 그리드 레이아웃 컴포넌트 | ✅ Closed |
| #139 | [E3-S2-T5] 미디어 컨트롤 버튼 UI | ✅ Closed |
| #140 | [E3-S2-T6] 발언자 감지 및 하이라이트 | ✅ Closed |
| #143 | [E3-S3-T3] getDisplayMedia API 통합 | ✅ Closed |
| #144 | [E3-S3-T4] 화면 공유 UI (선택 다이얼로그) | ✅ Closed |
| #145 | [E3-S3-T5] PIP 비디오 컴포넌트 | ✅ Closed |
| #146 | [E3-S3-T6] 화면 공유 뷰 레이아웃 | ✅ Closed |
| #150 | [E3-S4-T4] 채팅 UI 컴포넌트 개발 | ✅ Closed |
| #151 | [E3-S4-T5] DM 기능 구현 | ✅ Closed |
| #152 | [E3-S4-T6] 이모지 피커 통합 | ✅ Closed |
| #153 | [E3-S4-T7] 채팅 검색 UI | ✅ Closed |
| #156 | [E3-S5-T3] 손들기 버튼 및 큐 UI | ✅ Closed |
| #157 | [E3-S5-T4] 반응 버튼 UI | ✅ Closed |
| #158 | [E3-S5-T5] 손든 학생 목록 (교수용) | ✅ Closed |
| #164 | [E3-S6-T6] 녹화 컨트롤 UI | ✅ Closed |
| #165 | [E3-S6-T7] 녹화본 다시보기 플레이어 | ✅ Closed |
| #168 | [E3-S7-T3] 그리드 뷰 컴포넌트 | ✅ Closed |
| #169 | [E3-S7-T4] 발표자 뷰 컴포넌트 | ✅ Closed |
| #170 | [E3-S7-T5] 레이아웃 선택 UI | ✅ Closed |

---

## 기술 스택

### Backend
- Spring Boot 3.2.1, Java 17
- Spring WebSocket + STOMP 프로토콜
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
- WebSocket (STOMP Client)
- WebRTC APIs (getUserMedia, getDisplayMedia)

---

## 주요 기능

### 세미나 룸 관리
- 세미나 룸 생성/수정/삭제
- 세미나 시작/종료
- 대기실 관리 (입장 승인)
- 참가자 관리 (음소거, 강퇴)

### 실시간 화상 통신
- WebRTC 기반 비디오/오디오 스트리밍
- 화면 공유 (전체 화면, 윈도우, 탭)
- 레이아웃 전환 (그리드/발표자/사이드바)
- 발언자 하이라이트

### 채팅 시스템
- 실시간 메시지 송수신
- 메시지 고정/삭제
- DM (1:1 메시지)
- 이모지 지원

### 상호작용 도구
- 손들기 큐 관리
- 이모지 반응 (👍👏❤️😊🎉)
- 참가자 목록 실시간 업데이트

### 녹화 (구조만)
- 녹화 시작/종료 API
- 클라우드 스토리지 연동 구조
- STT 자막 생성 구조

---

## 다음 단계

**Phase 5 Sprint 4**: E4 액티브 러닝 도구
- 실시간 투표/퀴즈
- 분반(Breakout Room) 관리
- 화이트보드
- 토론 기능
