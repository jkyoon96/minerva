# E3 실시간 세미나 시스템 Backend API - 구현 완료

## 개요

EduForum 프로젝트의 E3 실시간 세미나 시스템을 위한 Spring Boot Backend API를 완성했습니다.

- **프로젝트 경로**: `/mnt/d/Development/git/minerva/apps/backend`
- **도메인 패키지**: `com.eduforum.api.domain.seminar`
- **기술스택**: Spring Boot 3.2.1, Java 17, JPA, PostgreSQL, WebSocket (STOMP)
- **총 생성 파일**: 38개 Java 파일

---

## 📁 파일 구조

```
apps/backend/src/main/java/com/eduforum/api/domain/seminar/
├── entity/                         # JPA 엔티티 (7개)
│   ├── SeminarRoom.java           # 세미나 룸
│   ├── RoomParticipant.java       # 참가자
│   ├── ChatMessage.java           # 채팅 메시지
│   ├── Reaction.java              # 반응
│   ├── RoomStatus.java            # 룸 상태 enum
│   ├── ParticipantRole.java       # 참가자 역할 enum
│   ├── ParticipantStatus.java     # 참가자 상태 enum
│   ├── MessageType.java           # 메시지 타입 enum
│   ├── ReactionType.java          # 반응 타입 enum
│   ├── WebSocketEventType.java    # WebSocket 이벤트 타입 enum
│   └── LayoutType.java            # 레이아웃 타입 enum
│
├── repository/                     # JPA Repository (4개)
│   ├── SeminarRoomRepository.java
│   ├── RoomParticipantRepository.java
│   ├── ChatMessageRepository.java
│   └── ReactionRepository.java
│
├── dto/                           # Request/Response DTO (11개)
│   ├── RoomCreateRequest.java
│   ├── RoomResponse.java
│   ├── RoomJoinRequest.java
│   ├── ParticipantResponse.java
│   ├── ChatMessageRequest.java
│   ├── ChatMessageResponse.java
│   ├── ReactionRequest.java
│   ├── ReactionResponse.java
│   ├── LayoutUpdateRequest.java
│   ├── ScreenShareRequest.java
│   └── WebSocketMessage.java
│
├── service/                       # 비즈니스 로직 (5개)
│   ├── RoomService.java           # 룸 관리
│   ├── ParticipantService.java    # 참가자 관리
│   ├── ChatService.java           # 채팅 관리
│   ├── ReactionService.java       # 반응 관리
│   └── ScreenShareService.java    # 화면 공유 관리
│
├── controller/                    # REST API Controller (4개)
│   ├── RoomController.java
│   ├── ParticipantController.java
│   ├── ChatController.java
│   └── ReactionController.java
│
└── websocket/                     # WebSocket 설정 (3개)
    ├── WebSocketConfig.java       # STOMP 설정
    ├── WebSocketEventPublisher.java  # 이벤트 브로드캐스트
    └── ChatWebSocketController.java  # 실시간 채팅 핸들러
```

---

## 🔑 주요 기능 구현

### 1. 세미나 룸 관리 (#124, #125, #126)

#### REST API Endpoints
- `POST /v1/rooms` - 세미나 룸 생성
- `GET /v1/rooms/{roomId}` - 룸 정보 조회
- `GET /v1/rooms/session/{sessionId}` - 세션의 룸 조회
- `POST /v1/rooms/{roomId}/start` - 룸 시작 (호스트 전용)
- `POST /v1/rooms/{roomId}/end` - 룸 종료 (호스트 전용)
- `PUT /v1/rooms/{roomId}/layout` - 레이아웃 변경
- `GET /v1/rooms/{roomId}/layout` - 현재 레이아웃 조회
- `GET /v1/rooms/active` - 활성 룸 목록
- `GET /v1/rooms/host/{hostId}` - 호스트의 룸 목록

#### 주요 기능
- 대기실 기능 (WAITING → ACTIVE → ENDED)
- 최대 참가자 수 제한 (기본 100명)
- 레이아웃 동기화 (GALLERY, SPEAKER, SIDEBAR, PRESENTATION)
- 룸 설정 (JSONB): 대기실, 자동 녹화, 채팅/반응 허용 등

---

### 2. 참가자 관리 (#130, #131)

#### REST API Endpoints
- `POST /v1/rooms/{roomId}/participants/join` - 룸 참가
- `POST /v1/rooms/{roomId}/participants/leave` - 룸 퇴장
- `GET /v1/rooms/{roomId}/participants` - 참가자 목록
- `GET /v1/rooms/{roomId}/participants/active` - 활성 참가자 목록
- `POST /v1/rooms/{roomId}/participants/hand-raise` - 손들기
- `DELETE /v1/rooms/{roomId}/participants/hand-raise` - 손 내리기
- `GET /v1/rooms/{roomId}/participants/raised-hands` - 손든 참가자 목록
- `POST /v1/rooms/{roomId}/participants/toggle-mute` - 음소거 토글
- `POST /v1/rooms/{roomId}/participants/toggle-video` - 비디오 토글

#### 주요 기능
- 참가자 역할: HOST, CO_HOST, PARTICIPANT
- 참가자 상태: WAITING, JOINED, LEFT
- 실시간 상태 추적: 손들기, 음소거, 비디오, 화면공유

---

### 3. 채팅 시스템 (#147, #148, #149)

#### REST API Endpoints
- `POST /v1/rooms/{roomId}/messages` - 메시지 전송
- `GET /v1/rooms/{roomId}/messages` - 메시지 히스토리 (페이징)
- `GET /v1/rooms/{roomId}/messages/recent?limit=50` - 최근 메시지
- `GET /v1/rooms/{roomId}/messages/since?since={timestamp}` - 특정 시간 이후 메시지
- `GET /v1/rooms/{roomId}/messages/files` - 공유된 파일 목록
- `GET /v1/rooms/{roomId}/messages/count` - 메시지 개수
- `POST /v1/rooms/{roomId}/messages/files` - 파일 업로드

#### WebSocket
- `STOMP /app/chat.send` - 실시간 메시지 전송
- `SUBSCRIBE /topic/room/{roomId}` - 룸 메시지 구독

#### 주요 기능
- 메시지 타입: TEXT, FILE, SYSTEM
- 파일 공유 (파일명, URL, 크기 저장)
- 실시간 메시지 브로드캐스트
- 페이징 및 실시간 동기화 지원

---

### 4. 손들기/반응 (#154, #155)

#### REST API Endpoints
- `POST /v1/rooms/{roomId}/reactions` - 반응 보내기
- `GET /v1/rooms/{roomId}/reactions/recent?minutes=5` - 최근 반응 조회

#### 반응 타입
- `THUMBS_UP` (👍)
- `CLAP` (👏)
- `HEART` (❤️)
- `LAUGH` (😂)
- `SURPRISE` (😮)

#### 주요 기능
- 실시간 반응 브로드캐스트
- 시간 기반 반응 조회 (최근 N분)
- 이모지 자동 매핑

---

### 5. 화면 공유 (#141, #142)

#### REST API Endpoints
- `POST /v1/rooms/{roomId}/participants/screen-share/start` - 화면 공유 시작
- `POST /v1/rooms/{roomId}/participants/screen-share/stop` - 화면 공유 중지

#### 주요 기능
- 권한 관리 (교수 기본, 학생 허용 시)
- 동시 화면 공유 방지 (한 번에 한 명만)
- 실시간 이벤트 브로드캐스트

---

### 6. 레이아웃 동기화 (#166)

#### REST API Endpoints
- `PUT /v1/rooms/{roomId}/layout` - 레이아웃 변경 (호스트 전용)
- `GET /v1/rooms/{roomId}/layout` - 현재 레이아웃 조회

#### 레이아웃 타입
- `GALLERY` - 그리드 뷰
- `SPEAKER` - 발표자 중심
- `SIDEBAR` - 사이드바 레이아웃
- `PRESENTATION` - 전체 화면 프레젠테이션

---

## 🔌 WebSocket 통신

### STOMP 엔드포인트
```
ws://localhost:8000/api/ws/seminar
ws://localhost:8000/api/ws/chat
```

### 메시지 브로커 설정
- **Application Prefix**: `/app`
- **Broker Prefix**: `/topic`, `/queue`
- **User Prefix**: `/user`

### WebSocket 이벤트 타입
```java
enum WebSocketEventType {
    // Room events
    PARTICIPANT_JOINED, PARTICIPANT_LEFT,
    ROOM_STARTED, ROOM_ENDED,

    // Chat events
    CHAT_MESSAGE, FILE_SHARED,

    // Interaction events
    HAND_RAISED, HAND_LOWERED, REACTION,

    // Media events
    SCREEN_SHARE_STARTED, SCREEN_SHARE_STOPPED,
    MUTE_CHANGED, VIDEO_CHANGED,

    // Layout events
    LAYOUT_CHANGED
}
```

### WebSocket 메시지 구조
```json
{
  "eventType": "PARTICIPANT_JOINED",
  "roomId": 1,
  "senderId": 10,
  "data": { /* 이벤트 데이터 */ },
  "timestamp": "2025-11-29T10:30:00+00:00"
}
```

---

## 🗄️ 데이터베이스 스키마

### seminar.rooms
```sql
CREATE TABLE seminar.rooms (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES course.sessions(id),
    host_id BIGINT NOT NULL REFERENCES auth.users(id),
    status room_status NOT NULL DEFAULT 'WAITING',
    max_participants INTEGER DEFAULT 100,
    started_at TIMESTAMPTZ,
    ended_at TIMESTAMPTZ,
    meeting_url VARCHAR(500),
    recording_url VARCHAR(500),
    layout layout_type NOT NULL DEFAULT 'GALLERY',
    settings JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);
```

### seminar.room_participants
```sql
CREATE TABLE seminar.room_participants (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id),
    user_id BIGINT NOT NULL REFERENCES auth.users(id),
    role participant_role NOT NULL DEFAULT 'PARTICIPANT',
    status participant_status NOT NULL DEFAULT 'WAITING',
    joined_at TIMESTAMPTZ,
    left_at TIMESTAMPTZ,
    is_hand_raised BOOLEAN NOT NULL DEFAULT FALSE,
    is_muted BOOLEAN NOT NULL DEFAULT FALSE,
    is_video_on BOOLEAN NOT NULL DEFAULT TRUE,
    is_screen_sharing BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ,
    UNIQUE(room_id, user_id)
);
```

### seminar.chat_messages
```sql
CREATE TABLE seminar.chat_messages (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id),
    sender_id BIGINT REFERENCES auth.users(id),
    message_type message_type NOT NULL DEFAULT 'TEXT',
    content TEXT NOT NULL,
    file_url VARCHAR(500),
    file_name VARCHAR(255),
    file_size BIGINT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_chat_room_created ON seminar.chat_messages(room_id, created_at DESC);
```

### seminar.reactions
```sql
CREATE TABLE seminar.reactions (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES seminar.rooms(id),
    user_id BIGINT NOT NULL REFERENCES auth.users(id),
    reaction_type reaction_type NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_reaction_room_created ON seminar.reactions(room_id, created_at DESC);
```

---

## 🚨 에러 코드

### 세미나 룸 관련 (SR)
- `SR001` - 세미나 룸을 찾을 수 없습니다
- `SR002` - 세션에 이미 룸이 존재합니다
- `SR003` - 룸 정원이 초과되었습니다
- `SR004` - 이미 시작된 룸입니다
- `SR005` - 이미 종료된 룸입니다
- `SR006` - 활성화된 룸이 아닙니다

### 참가자 관련 (SP)
- `SP001` - 참가자를 찾을 수 없습니다
- `SP002` - 이미 룸에 참가 중입니다
- `SP003` - 룸에 참가하지 않았습니다
- `SP004` - 호스트 권한이 필요합니다

### 채팅 관련 (CH)
- `CH001` - 채팅 메시지를 찾을 수 없습니다
- `CH002` - 채팅이 비활성화되어 있습니다

### 화면 공유 관련 (SS)
- `SS001` - 화면 공유 권한이 없습니다
- `SS002` - 이미 화면 공유 중입니다

---

## 📝 API 문서 (Swagger)

### 접속 URL
```
http://localhost:8000/api/swagger-ui.html
```

### Swagger 태그 구성
- **Seminar Rooms** - 세미나 룸 관리 API
- **Participants** - 룸 참가자 관리 API
- **Chat** - 채팅 메시지 API
- **Chat WebSocket** - 실시간 채팅 WebSocket API
- **Reactions** - 반응 API

---

## 🔐 인증/인가

### 헤더
```
X-User-Id: {userId}
Authorization: Bearer {jwt_token}
```

### 권한 검증
- **호스트 전용**: 룸 시작/종료, 레이아웃 변경
- **참가자**: 룸 참가, 채팅, 반응, 손들기
- **화면 공유**: 설정에 따라 호스트만 또는 모든 참가자

---

## 🧪 테스트 시나리오

### 1. 세미나 생성 및 시작
```bash
# 1. 룸 생성
POST /v1/rooms
{
  "sessionId": 1,
  "maxParticipants": 100,
  "settings": {
    "enableWaitingRoom": true,
    "autoRecord": true,
    "allowChat": true,
    "allowReactions": true
  }
}

# 2. 룸 시작
POST /v1/rooms/{roomId}/start
Headers: X-User-Id: 1
```

### 2. 학생 참가
```bash
# 룸 참가
POST /v1/rooms/{roomId}/participants/join
Headers: X-User-Id: 2
{
  "roomId": 1,
  "videoEnabled": true,
  "audioEnabled": true
}
```

### 3. 실시간 채팅
```bash
# REST API
POST /v1/rooms/{roomId}/messages
Headers: X-User-Id: 2
{
  "roomId": 1,
  "content": "안녕하세요!"
}

# WebSocket (STOMP)
SEND /app/chat.send
{
  "roomId": 1,
  "content": "안녕하세요!"
}
```

### 4. 손들기 및 반응
```bash
# 손들기
POST /v1/rooms/{roomId}/participants/hand-raise
Headers: X-User-Id: 2

# 반응 보내기
POST /v1/rooms/{roomId}/reactions
Headers: X-User-Id: 2
{
  "roomId": 1,
  "reactionType": "THUMBS_UP"
}
```

### 5. 화면 공유
```bash
# 화면 공유 시작
POST /v1/rooms/{roomId}/participants/screen-share/start
Headers: X-User-Id: 1

# 화면 공유 중지
POST /v1/rooms/{roomId}/participants/screen-share/stop
Headers: X-User-Id: 1
```

---

## 🚀 다음 단계 (구현 필요)

### 1. WebRTC 미디어 서버 연동 (#123)
- STUN/TURN 서버 설정
- WebRTC 시그널링 구현
- 미디어 스트림 관리

### 2. 데이터베이스 마이그레이션
```sql
-- Create enums
CREATE TYPE room_status AS ENUM ('WAITING', 'ACTIVE', 'ENDED');
CREATE TYPE participant_role AS ENUM ('HOST', 'CO_HOST', 'PARTICIPANT');
CREATE TYPE participant_status AS ENUM ('WAITING', 'JOINED', 'LEFT');
CREATE TYPE message_type AS ENUM ('TEXT', 'FILE', 'SYSTEM');
CREATE TYPE reaction_type AS ENUM ('THUMBS_UP', 'CLAP', 'HEART', 'LAUGH', 'SURPRISE');
CREATE TYPE layout_type AS ENUM ('GALLERY', 'SPEAKER', 'SIDEBAR', 'PRESENTATION');

-- Create schema
CREATE SCHEMA IF NOT EXISTS seminar;

-- Create tables (위의 스키마 참조)
```

### 3. 실제 JWT 인증 통합
- `SecurityConfig`에서 WebSocket 엔드포인트 보안 설정
- JWT 토큰에서 `userId` 추출 로직 구현
- `ChatWebSocketController`의 principal 파싱 구현

### 4. 파일 업로드 구현
- Multipart 파일 업로드 핸들러
- S3/MinIO 같은 객체 스토리지 연동
- 파일 크기 및 타입 검증

### 5. 성능 최적화
- Redis를 사용한 WebSocket 세션 관리
- 메시지 큐 (RabbitMQ/Kafka) 도입
- 데이터베이스 인덱스 최적화

---

## 📚 참고 사항

### 기존 코드 패턴 준수
- BaseEntity 상속 (createdAt, updatedAt, deletedAt)
- ApiResponse 래퍼 사용
- BusinessException 예외 처리
- Swagger 문서화 (@Operation, @Schema)
- Lombok 활용 (@Builder, @Getter, @Setter)

### 일관성
- 패키지 구조: course 도메인과 동일한 구조
- 네이밍 규칙: entity, repository, service, controller, dto
- 트랜잭션 관리: @Transactional(readOnly = true) 기본
- 로깅: Slf4j 사용

---

## ✅ 체크리스트

- [x] Entity 클래스 생성 (4개 + 7개 enum)
- [x] Repository 인터페이스 생성 (4개)
- [x] DTO 클래스 생성 (11개)
- [x] Service 클래스 생성 (5개)
- [x] REST Controller 생성 (4개)
- [x] WebSocket 설정 (3개)
- [x] 에러 코드 추가 (12개)
- [x] Swagger 태그 추가
- [ ] 데이터베이스 마이그레이션 스크립트
- [ ] WebRTC 미디어 서버 설정
- [ ] JWT 인증 통합
- [ ] 파일 업로드 구현
- [ ] 단위 테스트 작성
- [ ] 통합 테스트 작성

---

## 📞 문의

구현 관련 문의사항이나 버그 리포트는 GitHub Issues를 통해 남겨주세요.

**프로젝트 저장소**: https://github.com/eduforum/minerva
