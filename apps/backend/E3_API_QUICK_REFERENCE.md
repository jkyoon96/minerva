# E3 실시간 세미나 API - Quick Reference

## Base URL
```
http://localhost:8000/api
```

## Authentication
모든 요청에 다음 헤더 필요:
```
X-User-Id: {userId}
Authorization: Bearer {jwt_token}
```

---

## 🏠 세미나 룸 관리

### 룸 생성
```http
POST /v1/rooms
Content-Type: application/json

{
  "sessionId": 1,
  "maxParticipants": 100,
  "settings": {
    "enableWaitingRoom": true,
    "autoRecord": true,
    "allowChat": true,
    "allowReactions": true,
    "allowScreenShare": true
  }
}

Response: 201 Created
{
  "status": 201,
  "message": "세미나 룸이 생성되었습니다",
  "data": {
    "id": 1,
    "sessionId": 1,
    "hostId": 1,
    "hostName": "김교수",
    "status": "WAITING",
    "maxParticipants": 100,
    "currentParticipants": 1,
    "layout": "GALLERY",
    "settings": { ... },
    "createdAt": "2025-11-29T10:00:00Z"
  }
}
```

### 룸 시작
```http
POST /v1/rooms/{roomId}/start
X-User-Id: 1

Response: 200 OK
{
  "message": "세미나가 시작되었습니다",
  "data": {
    "id": 1,
    "status": "ACTIVE",
    "startedAt": "2025-11-29T10:30:00Z"
  }
}
```

### 룸 종료
```http
POST /v1/rooms/{roomId}/end
X-User-Id: 1

Response: 200 OK
```

### 룸 정보 조회
```http
GET /v1/rooms/{roomId}

Response: 200 OK
```

### 레이아웃 변경
```http
PUT /v1/rooms/{roomId}/layout
X-User-Id: 1

{
  "layout": "SPEAKER"
}

Response: 200 OK
```

### 활성 룸 목록
```http
GET /v1/rooms/active

Response: 200 OK
{
  "data": [...]
}
```

---

## 👥 참가자 관리

### 룸 참가
```http
POST /v1/rooms/{roomId}/participants/join
X-User-Id: 2

{
  "roomId": 1,
  "videoEnabled": true,
  "audioEnabled": true
}

Response: 200 OK
{
  "message": "룸에 참가했습니다",
  "data": {
    "id": 2,
    "userId": 2,
    "userName": "홍길동",
    "role": "PARTICIPANT",
    "status": "JOINED",
    "isHandRaised": false,
    "isMuted": false,
    "isVideoOn": true,
    "joinedAt": "2025-11-29T10:31:00Z"
  }
}
```

### 룸 퇴장
```http
POST /v1/rooms/{roomId}/participants/leave
X-User-Id: 2

Response: 200 OK
```

### 참가자 목록
```http
GET /v1/rooms/{roomId}/participants

Response: 200 OK
{
  "data": [
    {
      "id": 1,
      "userName": "김교수",
      "role": "HOST",
      "status": "JOINED"
    },
    {
      "id": 2,
      "userName": "홍길동",
      "role": "PARTICIPANT",
      "status": "JOINED"
    }
  ]
}
```

### 손들기
```http
POST /v1/rooms/{roomId}/participants/hand-raise
X-User-Id: 2

Response: 200 OK
```

### 손 내리기
```http
DELETE /v1/rooms/{roomId}/participants/hand-raise
X-User-Id: 2

Response: 200 OK
```

### 손든 참가자 목록
```http
GET /v1/rooms/{roomId}/participants/raised-hands

Response: 200 OK
```

### 음소거 토글
```http
POST /v1/rooms/{roomId}/participants/toggle-mute
X-User-Id: 2

Response: 200 OK
```

### 비디오 토글
```http
POST /v1/rooms/{roomId}/participants/toggle-video
X-User-Id: 2

Response: 200 OK
```

---

## 💬 채팅

### 메시지 전송
```http
POST /v1/rooms/{roomId}/messages
X-User-Id: 2

{
  "roomId": 1,
  "content": "안녕하세요!"
}

Response: 200 OK
{
  "message": "메시지가 전송되었습니다",
  "data": {
    "id": 1,
    "roomId": 1,
    "senderId": 2,
    "senderName": "홍길동",
    "messageType": "TEXT",
    "content": "안녕하세요!",
    "createdAt": "2025-11-29T10:32:00Z"
  }
}
```

### 메시지 히스토리 (페이징)
```http
GET /v1/rooms/{roomId}/messages?page=0&size=50&sort=createdAt,desc

Response: 200 OK
{
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 2,
    "number": 0,
    "size": 50
  }
}
```

### 최근 메시지
```http
GET /v1/rooms/{roomId}/messages/recent?limit=50

Response: 200 OK
```

### 특정 시간 이후 메시지 (실시간 동기화)
```http
GET /v1/rooms/{roomId}/messages/since?since=2025-11-29T10:30:00Z

Response: 200 OK
```

### 공유된 파일 목록
```http
GET /v1/rooms/{roomId}/messages/files

Response: 200 OK
```

### 파일 업로드
```http
POST /v1/rooms/{roomId}/messages/files
X-User-Id: 2

{
  "fileName": "document.pdf",
  "fileUrl": "https://storage.example.com/files/document.pdf",
  "fileSize": 1024000
}

Response: 200 OK
```

---

## 🎭 반응

### 반응 보내기
```http
POST /v1/rooms/{roomId}/reactions
X-User-Id: 2

{
  "roomId": 1,
  "reactionType": "THUMBS_UP"
}

Response: 200 OK
{
  "message": "반응이 전송되었습니다",
  "data": {
    "id": 1,
    "roomId": 1,
    "userId": 2,
    "userName": "홍길동",
    "reactionType": "THUMBS_UP",
    "emoji": "👍",
    "createdAt": "2025-11-29T10:33:00Z"
  }
}
```

### 최근 반응 조회
```http
GET /v1/rooms/{roomId}/reactions/recent?minutes=5

Response: 200 OK
```

### 반응 타입
- `THUMBS_UP` - 👍
- `CLAP` - 👏
- `HEART` - ❤️
- `LAUGH` - 😂
- `SURPRISE` - 😮

---

## 🖥️ 화면 공유

### 화면 공유 시작
```http
POST /v1/rooms/{roomId}/participants/screen-share/start
X-User-Id: 1

Response: 200 OK
{
  "message": "화면 공유를 시작했습니다",
  "data": {
    "userId": 1,
    "isScreenSharing": true
  }
}
```

### 화면 공유 중지
```http
POST /v1/rooms/{roomId}/participants/screen-share/stop
X-User-Id: 1

Response: 200 OK
```

---

## 🔌 WebSocket (STOMP)

### 연결
```javascript
const socket = new SockJS('http://localhost:8000/api/ws/seminar');
const stompClient = Stomp.over(socket);

stompClient.connect({
  Authorization: 'Bearer ' + jwtToken
}, function(frame) {
  console.log('Connected: ' + frame);

  // 룸 이벤트 구독
  stompClient.subscribe('/topic/room/' + roomId, function(message) {
    const event = JSON.parse(message.body);
    handleRoomEvent(event);
  });

  // 개인 메시지 구독
  stompClient.subscribe('/user/queue/user/' + userId, function(message) {
    const event = JSON.parse(message.body);
    handlePersonalEvent(event);
  });
});
```

### 채팅 메시지 전송 (WebSocket)
```javascript
stompClient.send('/app/chat.send', {}, JSON.stringify({
  roomId: 1,
  content: '안녕하세요!'
}));
```

### WebSocket 이벤트 타입
```javascript
{
  eventType: 'PARTICIPANT_JOINED',  // 이벤트 타입
  roomId: 1,                        // 룸 ID
  senderId: 2,                      // 발신자 ID
  data: {                           // 이벤트 데이터
    userId: 2,
    userName: '홍길동',
    role: 'PARTICIPANT'
  },
  timestamp: '2025-11-29T10:31:00Z'
}
```

### 주요 WebSocket 이벤트
- `PARTICIPANT_JOINED` - 참가자 입장
- `PARTICIPANT_LEFT` - 참가자 퇴장
- `ROOM_STARTED` - 룸 시작
- `ROOM_ENDED` - 룸 종료
- `CHAT_MESSAGE` - 채팅 메시지
- `FILE_SHARED` - 파일 공유
- `HAND_RAISED` - 손들기
- `HAND_LOWERED` - 손 내리기
- `REACTION` - 반응
- `SCREEN_SHARE_STARTED` - 화면 공유 시작
- `SCREEN_SHARE_STOPPED` - 화면 공유 중지
- `LAYOUT_CHANGED` - 레이아웃 변경

---

## 🚨 에러 응답

### 표준 에러 형식
```json
{
  "status": 400,
  "message": "에러 메시지",
  "errorCode": "SR003",
  "timestamp": "2025-11-29T10:30:00"
}
```

### 주요 에러 코드
- `SR001` - 세미나 룸을 찾을 수 없습니다
- `SR002` - 세션에 이미 룸이 존재합니다
- `SR003` - 룸 정원이 초과되었습니다
- `SR004` - 이미 시작된 룸입니다
- `SP002` - 이미 룸에 참가 중입니다
- `SP003` - 룸에 참가하지 않았습니다
- `SP004` - 호스트 권한이 필요합니다
- `CH002` - 채팅이 비활성화되어 있습니다
- `SS001` - 화면 공유 권한이 없습니다
- `SS002` - 이미 화면 공유 중입니다

---

## 🎯 실전 시나리오

### 시나리오 1: 교수가 세미나 시작
```bash
# 1. 룸 생성
POST /v1/rooms
X-User-Id: 1 (교수)

# 2. 학생들 참가 대기
POST /v1/rooms/1/participants/join
X-User-Id: 2 (학생1)

POST /v1/rooms/1/participants/join
X-User-Id: 3 (학생2)

# 3. 교수가 룸 시작
POST /v1/rooms/1/start
X-User-Id: 1

# → 모든 참가자에게 ROOM_STARTED 이벤트 브로드캐스트
```

### 시나리오 2: 실시간 소통
```bash
# 1. 학생이 손들기
POST /v1/rooms/1/participants/hand-raise
X-User-Id: 2

# → HAND_RAISED 이벤트 브로드캐스트

# 2. 학생이 채팅 전송
POST /v1/rooms/1/messages
X-User-Id: 2
{ "content": "질문 있습니다" }

# → CHAT_MESSAGE 이벤트 브로드캐스트

# 3. 다른 학생들이 반응
POST /v1/rooms/1/reactions
X-User-Id: 3
{ "reactionType": "THUMBS_UP" }

# → REACTION 이벤트 브로드캐스트
```

### 시나리오 3: 화면 공유
```bash
# 1. 교수가 화면 공유 시작
POST /v1/rooms/1/participants/screen-share/start
X-User-Id: 1

# → SCREEN_SHARE_STARTED 이벤트 브로드캐스트

# 2. 레이아웃을 프레젠테이션 모드로 변경
PUT /v1/rooms/1/layout
X-User-Id: 1
{ "layout": "PRESENTATION" }

# → LAYOUT_CHANGED 이벤트 브로드캐스트

# 3. 화면 공유 종료
POST /v1/rooms/1/participants/screen-share/stop
X-User-Id: 1

# → SCREEN_SHARE_STOPPED 이벤트 브로드캐스트
```

---

## 🔍 Swagger UI

실시간 API 문서 및 테스트:
```
http://localhost:8000/api/swagger-ui.html
```

---

## 📝 노트

1. **인증**: 현재는 `X-User-Id` 헤더를 사용하지만, 실제 운영 환경에서는 JWT 토큰에서 추출해야 함
2. **WebSocket**: SockJS fallback을 통해 WebSocket을 지원하지 않는 브라우저도 지원
3. **레이트 리밋**: 반응 및 채팅 메시지는 추후 레이트 리밋 적용 권장
4. **파일 업로드**: 실제 파일 업로드는 별도의 파일 스토리지 서비스와 연동 필요
5. **권한**: 호스트만 수행할 수 있는 작업은 `SP004` 에러 반환

---

Generated with EduForum API Generator
Last updated: 2025-11-29
