# 04. 기술 명세서 (Technical Specification)

## 문서 정보
- **제품명**: EduForum
- **버전**: 1.0
- **작성일**: 2025-11-27
- **대상**: 개발팀
- **문서 상태**: Draft
- **관련 문서**:
  - [PRD](./03-product-requirements.md)
  - [기술 아키텍처](./02-technical-architecture.md)

---

## 목차
1. [시스템 아키텍처](#1-시스템-아키텍처)
2. [기술 스택](#2-기술-스택)
3. [API 설계](#3-api-설계)
4. [데이터베이스 스키마](#4-데이터베이스-스키마)
5. [WebSocket 프로토콜](#5-websocket-프로토콜)
6. [프론트엔드 아키텍처](#6-프론트엔드-아키텍처)
7. [인증 및 보안](#7-인증-및-보안)
8. [미디어 서버 통합](#8-미디어-서버-통합)
9. [배포 아키텍처](#9-배포-아키텍처)
10. [모니터링 및 로깅](#10-모니터링-및-로깅)
11. [성능 최적화](#11-성능-최적화)
12. [테스트 전략](#12-테스트-전략)

---

## 1. 시스템 아키텍처

### 1.1 전체 시스템 구조

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │   Browser    │  │   Browser    │  │   Browser    │         │
│  │  (React SPA) │  │  (React SPA) │  │  (React SPA) │         │
│  └───────┬──────┘  └───────┬──────┘  └───────┬──────┘         │
│          │                 │                 │                 │
└──────────┼─────────────────┼─────────────────┼─────────────────┘
           │                 │                 │
           │ HTTPS           │ WebSocket       │ WebRTC
           │ (REST API)      │ (Real-time)     │ (Media)
           ↓                 ↓                 ↓
┌─────────────────────────────────────────────────────────────────┐
│                      GATEWAY LAYER                              │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Nginx / Kong API Gateway                     │  │
│  │  - Load Balancing                                         │  │
│  │  - SSL Termination                                        │  │
│  │  - Rate Limiting                                          │  │
│  │  - Request Routing                                        │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
           │                 │                 │
           ↓                 ↓                 ↓
┌─────────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                             │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │    Django    │  │    Django    │  │    Django    │         │
│  │   REST API   │  │   Channels   │  │   Celery     │         │
│  │   Server     │  │  (WebSocket) │  │   Workers    │         │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘         │
│         │                 │                 │                 │
│         └─────────────────┴─────────────────┘                 │
│                           ↓                                    │
│                  ┌─────────────────┐                           │
│                  │  Redis (Cache   │                           │
│                  │  & Message Queue)│                           │
│                  └─────────────────┘                           │
└─────────────────────────────────────────────────────────────────┘
                           │
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│                      MEDIA LAYER                                │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │ Jitsi Meet   │  │ Jitsi Meet   │  │ Jitsi Meet   │         │
│  │ Videobridge  │  │ Videobridge  │  │ Videobridge  │         │
│  │   (SFU 1)    │  │   (SFU 2)    │  │   (SFU 3)    │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐                            │
│  │   Prosody    │  │   Jicofo     │                            │
│  │  (XMPP)      │  │ (Signaling)  │                            │
│  └──────────────┘  └──────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
                           │
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│                       DATA LAYER                                │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │ PostgreSQL   │  │   MongoDB    │  │   AWS S3     │         │
│  │  (Primary)   │  │ (Chat Logs)  │  │  (Files)     │         │
│  └──────┬───────┘  └──────────────┘  └──────────────┘         │
│         │                                                       │
│  ┌──────▼───────┐  ┌──────────────┐                            │
│  │ PostgreSQL   │  │ TimescaleDB  │                            │
│  │  (Replica)   │  │  (Metrics)   │                            │
│  └──────────────┘  └──────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
                           │
                           ↓
┌─────────────────────────────────────────────────────────────────┐
│                   EXTERNAL SERVICES                             │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │  OpenAI API  │  │  Amazon SES  │  │   Auth0 /    │         │
│  │   (NLP/AI)   │  │  (Email)     │  │  Google Auth │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 마이크로서비스 분할

```
┌─────────────────────────────────────────────────────────────────┐
│                    MICROSERVICES ARCHITECTURE                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │  User Service    │  │  Course Service  │                    │
│  ├──────────────────┤  ├──────────────────┤                    │
│  │ - Authentication │  │ - Course CRUD    │                    │
│  │ - User Profile   │  │ - Enrollment     │                    │
│  │ - Permissions    │  │ - Schedules      │                    │
│  └────────┬─────────┘  └────────┬─────────┘                    │
│           │                     │                              │
│           ↓                     ↓                              │
│       PostgreSQL            PostgreSQL                         │
│                                                                 │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │  Session Service │  │ Analytics Service│                    │
│  ├──────────────────┤  ├──────────────────┤                    │
│  │ - Live Sessions  │  │ - Participation  │                    │
│  │ - Breakout Rooms │  │ - Reports        │                    │
│  │ - Polls/Quizzes  │  │ - Early Warning  │                    │
│  └────────┬─────────┘  └────────┬─────────┘                    │
│           │                     │                              │
│           ↓                     ↓                              │
│    PostgreSQL + Redis      TimescaleDB                         │
│                                                                 │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │Assessment Service│  │  Media Service   │                    │
│  ├──────────────────┤  ├──────────────────┤                    │
│  │ - Auto Grading   │  │ - WebRTC Signal  │                    │
│  │ - NLP Scoring    │  │ - Recording      │                    │
│  │ - Feedback       │  │ - Transcription  │                    │
│  └────────┬─────────┘  └────────┬─────────┘                    │
│           │                     │                              │
│           ↓                     ↓                              │
│     PostgreSQL              AWS S3 + Jitsi                     │
│                                                                 │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │Notification Svc  │  │  Content Service │                    │
│  ├──────────────────┤  ├──────────────────┤                    │
│  │ - Email          │  │ - Files/Materials│                    │
│  │ - In-app Alerts  │  │ - Assignments    │                    │
│  │ - Push (future)  │  │ - Submissions    │                    │
│  └────────┬─────────┘  └────────┬─────────┘                    │
│           │                     │                              │
│           ↓                     ↓                              │
│      Redis Queue              AWS S3                           │
└─────────────────────────────────────────────────────────────────┘
```

### 1.3 데이터 흐름

#### 실시간 세션 참여 플로우
```
1. Student Opens Session Link
   │
   ├─> [Frontend] Check Authentication (JWT)
   │
   ├─> [User Service] Verify Token & Permissions
   │
   ├─> [Session Service] Get Session Details (REST API)
   │
   ├─> [WebSocket] Connect to Session Room
   │
   ├─> [Jitsi] Establish WebRTC Connection
   │
   └─> [Analytics Service] Log Join Event
```

#### 투표 생성 및 응답 플로우
```
1. Instructor Creates Poll
   │
   ├─> [Frontend] POST /api/sessions/{id}/polls
   │
   ├─> [Session Service] Create Poll in DB
   │
   ├─> [WebSocket] Broadcast "poll_created" event
   │
   └─> [All Students] Receive Poll Popup

2. Students Respond
   │
   ├─> [Frontend] POST /api/polls/{id}/responses
   │
   ├─> [Session Service] Save Response
   │
   ├─> [WebSocket] Broadcast "poll_response" event (anonymized count)
   │
   ├─> [Analytics Service] Track Participation Event
   │
   └─> [Instructor Dashboard] Real-time Update
```

---

## 2. 기술 스택

### 2.1 백엔드

| 카테고리 | 기술 | 버전 | 용도 |
|----------|------|------|------|
| **언어** | Python | 3.11+ | 주 개발 언어 |
| **프레임워크** | Django | 5.0+ | 웹 프레임워크 |
| **REST API** | Django REST Framework | 3.14+ | RESTful API |
| **WebSocket** | Django Channels | 4.0+ | 실시간 통신 |
| **비동기 작업** | Celery | 5.3+ | 백그라운드 작업 |
| **메시지 큐** | Redis | 7.2+ | Celery Broker, Cache |
| **ASGI 서버** | Daphne | 4.0+ | WebSocket 서버 |
| **WSGI 서버** | Gunicorn | 21.2+ | HTTP 서버 |

### 2.2 프론트엔드

| 카테고리 | 기술 | 버전 | 용도 |
|----------|------|------|------|
| **언어** | TypeScript | 5.0+ | 타입 안전성 |
| **프레임워크** | React | 18.2+ | UI 라이브러리 |
| **빌드 도구** | Vite | 5.0+ | 빠른 빌드 |
| **상태 관리** | Zustand | 4.4+ | 경량 상태 관리 |
| **라우팅** | React Router | 6.20+ | 클라이언트 라우팅 |
| **UI 컴포넌트** | Ant Design | 5.11+ | UI 컴포넌트 |
| **스타일링** | Tailwind CSS | 3.4+ | 유틸리티 CSS |
| **WebRTC** | lib-jitsi-meet | 최신 | Jitsi 클라이언트 |
| **WebSocket** | Socket.io-client | 4.6+ | WebSocket 클라이언트 |
| **차트** | Recharts | 2.10+ | 데이터 시각화 |
| **화이트보드** | Excalidraw | 0.17+ | 협업 화이트보드 |

### 2.3 데이터베이스

| 카테고리 | 기술 | 버전 | 용도 |
|----------|------|------|------|
| **주 DB** | PostgreSQL | 15+ | 관계형 데이터 |
| **캐시/세션** | Redis | 7.2+ | 캐싱, WebSocket 레이어 |
| **문서 DB** | MongoDB | 7.0+ | 채팅 로그 (선택) |
| **시계열 DB** | TimescaleDB | 2.13+ | 메트릭 (PostgreSQL 확장) |
| **객체 스토리지** | AWS S3 / MinIO | - | 파일, 녹화 영상 |

### 2.4 미디어 서버

| 카테고리 | 기술 | 버전 | 용도 |
|----------|------|------|------|
| **SFU** | Jitsi Videobridge | 최신 | WebRTC SFU |
| **시그널링** | Prosody (XMPP) | 0.12+ | Jitsi 시그널링 |
| **조정** | Jicofo | 최신 | 컨퍼런스 조정 |
| **TURN/STUN** | coturn | 4.6+ | NAT 통과 |
| **녹화** | Jibri | 최신 | 세션 녹화 |

### 2.5 AI/ML

| 카테고리 | 기술 | 버전 | 용도 |
|----------|------|------|------|
| **NLP** | Hugging Face Transformers | 4.35+ | 텍스트 분석 |
| **모델** | BERT, RoBERTa | - | 감성 분석, 유사도 |
| **벡터 DB** | FAISS | 1.7+ | 임베딩 검색 (선택) |
| **API** | OpenAI API | - | GPT-4 (선택적 사용) |

### 2.6 인프라 & DevOps

| 카테고리 | 기술 | 버전 | 용도 |
|----------|------|------|------|
| **컨테이너** | Docker | 24.0+ | 컨테이너화 |
| **오케스트레이션** | Kubernetes | 1.28+ | 컨테이너 관리 |
| **CI/CD** | GitHub Actions | - | 자동화 파이프라인 |
| **웹 서버** | Nginx | 1.24+ | 리버스 프록시 |
| **클라우드** | AWS / Azure | - | 호스팅 |
| **CDN** | CloudFront / CloudFlare | - | 정적 파일 가속 |
| **모니터링** | Prometheus + Grafana | - | 메트릭 수집 |
| **로깅** | ELK Stack | - | 중앙집중식 로그 |
| **APM** | Sentry | - | 에러 추적 |

---

## 3. API 설계

### 3.1 REST API 개요

**Base URL**: `https://api.eduforum.com/v1`

**인증**: JWT Bearer Token in `Authorization` header

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3.2 API 엔드포인트

#### 3.2.1 Authentication

##### POST /auth/register
사용자 등록

**Request**:
```json
{
  "email": "student@university.edu",
  "password": "SecurePass123!",
  "full_name": "Kim Younghee",
  "role": "student"
}
```

**Response** (201 Created):
```json
{
  "user": {
    "id": "usr_123456",
    "email": "student@university.edu",
    "full_name": "Kim Younghee",
    "role": "student",
    "created_at": "2025-11-27T10:00:00Z"
  },
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3600
}
```

##### POST /auth/login
로그인

**Request**:
```json
{
  "email": "student@university.edu",
  "password": "SecurePass123!"
}
```

**Response** (200 OK):
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3600,
  "user": {
    "id": "usr_123456",
    "email": "student@university.edu",
    "full_name": "Kim Younghee",
    "role": "student"
  }
}
```

##### POST /auth/refresh
토큰 갱신

**Request**:
```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response** (200 OK):
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3600
}
```

---

#### 3.2.2 Courses

##### GET /courses
코스 목록 조회

**Query Parameters**:
- `page`: 페이지 번호 (기본: 1)
- `page_size`: 페이지 크기 (기본: 20)
- `role`: 내 역할 필터 (`instructor`, `student`, `ta`)

**Response** (200 OK):
```json
{
  "count": 42,
  "next": "https://api.eduforum.com/v1/courses?page=2",
  "previous": null,
  "results": [
    {
      "id": "crs_789012",
      "title": "Introduction to Machine Learning",
      "code": "CS401",
      "semester": "2025 Spring",
      "instructor": {
        "id": "usr_456789",
        "full_name": "Prof. Lee Minho"
      },
      "student_count": 45,
      "session_count": 24,
      "my_role": "student",
      "created_at": "2025-01-10T09:00:00Z"
    }
  ]
}
```

##### POST /courses
코스 생성 (교수만)

**Request**:
```json
{
  "title": "Introduction to Machine Learning",
  "code": "CS401",
  "description": "Fundamentals of ML algorithms and applications",
  "semester": "2025 Spring",
  "max_students": 50
}
```

**Response** (201 Created):
```json
{
  "id": "crs_789012",
  "title": "Introduction to Machine Learning",
  "code": "CS401",
  "description": "Fundamentals of ML algorithms and applications",
  "semester": "2025 Spring",
  "max_students": 50,
  "instructor": {
    "id": "usr_456789",
    "full_name": "Prof. Lee Minho"
  },
  "student_count": 0,
  "created_at": "2025-01-10T09:00:00Z"
}
```

##### GET /courses/{course_id}
코스 상세 조회

**Response** (200 OK):
```json
{
  "id": "crs_789012",
  "title": "Introduction to Machine Learning",
  "code": "CS401",
  "description": "Fundamentals of ML algorithms and applications",
  "semester": "2025 Spring",
  "max_students": 50,
  "instructor": {
    "id": "usr_456789",
    "full_name": "Prof. Lee Minho",
    "email": "minho.lee@university.edu"
  },
  "tas": [
    {
      "id": "usr_111222",
      "full_name": "TA Park Jisoo"
    }
  ],
  "student_count": 45,
  "session_count": 24,
  "my_role": "student",
  "syllabus_url": "https://s3.../syllabus.pdf",
  "created_at": "2025-01-10T09:00:00Z",
  "updated_at": "2025-01-15T14:30:00Z"
}
```

##### POST /courses/{course_id}/enroll
코스 등록 (학생)

**Request**:
```json
{
  "enrollment_key": "ML2025SPRING" // Optional
}
```

**Response** (201 Created):
```json
{
  "message": "Successfully enrolled",
  "course": {
    "id": "crs_789012",
    "title": "Introduction to Machine Learning"
  },
  "enrolled_at": "2025-01-20T10:00:00Z"
}
```

##### POST /courses/{course_id}/students/bulk
학생 일괄 등록 (교수만)

**Request** (multipart/form-data):
```
file: students.csv

CSV Format:
email,full_name,student_id
student1@edu.ac.kr,Kim Minsoo,2021001
student2@edu.ac.kr,Lee Jiyeon,2021002
```

**Response** (201 Created):
```json
{
  "success_count": 45,
  "error_count": 2,
  "errors": [
    {
      "row": 10,
      "email": "invalid_email",
      "error": "Invalid email format"
    }
  ]
}
```

---

#### 3.2.3 Sessions

##### GET /courses/{course_id}/sessions
세션 목록 조회

**Query Parameters**:
- `status`: `scheduled`, `live`, `completed`
- `start_date`: ISO 8601 (필터링)
- `end_date`: ISO 8601

**Response** (200 OK):
```json
{
  "count": 24,
  "results": [
    {
      "id": "ses_345678",
      "course_id": "crs_789012",
      "title": "Week 3: Supervised Learning",
      "description": "Linear regression, logistic regression",
      "scheduled_at": "2025-02-05T14:00:00Z",
      "duration_minutes": 90,
      "status": "scheduled",
      "participant_count": 0,
      "recording_url": null,
      "created_at": "2025-01-25T09:00:00Z"
    },
    {
      "id": "ses_345677",
      "course_id": "crs_789012",
      "title": "Week 2: Python for ML",
      "scheduled_at": "2025-01-29T14:00:00Z",
      "duration_minutes": 90,
      "status": "completed",
      "participant_count": 43,
      "recording_url": "https://cdn.../recording_345677.mp4",
      "created_at": "2025-01-20T09:00:00Z"
    }
  ]
}
```

##### POST /courses/{course_id}/sessions
세션 생성 (교수만)

**Request**:
```json
{
  "title": "Week 3: Supervised Learning",
  "description": "Linear regression, logistic regression",
  "scheduled_at": "2025-02-05T14:00:00Z",
  "duration_minutes": 90,
  "auto_record": true,
  "allow_late_join": true
}
```

**Response** (201 Created):
```json
{
  "id": "ses_345678",
  "title": "Week 3: Supervised Learning",
  "description": "Linear regression, logistic regression",
  "scheduled_at": "2025-02-05T14:00:00Z",
  "duration_minutes": 90,
  "status": "scheduled",
  "join_url": "https://eduforum.com/session/ses_345678",
  "auto_record": true,
  "created_at": "2025-01-25T09:00:00Z"
}
```

##### GET /sessions/{session_id}
세션 상세 조회

**Response** (200 OK):
```json
{
  "id": "ses_345678",
  "course": {
    "id": "crs_789012",
    "title": "Introduction to Machine Learning",
    "instructor": {
      "id": "usr_456789",
      "full_name": "Prof. Lee Minho"
    }
  },
  "title": "Week 3: Supervised Learning",
  "description": "Linear regression, logistic regression",
  "scheduled_at": "2025-02-05T14:00:00Z",
  "duration_minutes": 90,
  "status": "scheduled",
  "participants": [],
  "join_url": "https://eduforum.com/session/ses_345678",
  "jitsi_room_name": "eduforum_ses_345678",
  "my_participation": null,
  "created_at": "2025-01-25T09:00:00Z"
}
```

##### POST /sessions/{session_id}/start
세션 시작 (교수만)

**Response** (200 OK):
```json
{
  "id": "ses_345678",
  "status": "live",
  "started_at": "2025-02-05T14:01:23Z",
  "jitsi_jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "jitsi_config": {
    "room_name": "eduforum_ses_345678",
    "domain": "meet.eduforum.com",
    "moderator": true
  }
}
```

##### POST /sessions/{session_id}/join
세션 참여 (학생)

**Response** (200 OK):
```json
{
  "id": "ses_345678",
  "status": "live",
  "joined_at": "2025-02-05T14:02:15Z",
  "jitsi_jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "jitsi_config": {
    "room_name": "eduforum_ses_345678",
    "domain": "meet.eduforum.com",
    "moderator": false,
    "display_name": "Kim Younghee"
  },
  "websocket_url": "wss://api.eduforum.com/ws/sessions/ses_345678"
}
```

##### POST /sessions/{session_id}/end
세션 종료 (교수만)

**Response** (200 OK):
```json
{
  "id": "ses_345678",
  "status": "completed",
  "started_at": "2025-02-05T14:01:23Z",
  "ended_at": "2025-02-05T15:32:45Z",
  "duration_actual_minutes": 91,
  "participant_count": 43,
  "recording_status": "processing"
}
```

---

#### 3.2.4 Polls

##### POST /sessions/{session_id}/polls
투표 생성 (교수)

**Request**:
```json
{
  "question": "Which ML algorithm should we cover next?",
  "type": "single_choice",
  "options": [
    "Decision Trees",
    "SVM",
    "Neural Networks",
    "Clustering"
  ],
  "anonymous": false,
  "duration_seconds": 60,
  "show_results_after": true
}
```

**Response** (201 Created):
```json
{
  "id": "pol_901234",
  "session_id": "ses_345678",
  "question": "Which ML algorithm should we cover next?",
  "type": "single_choice",
  "options": [
    {"id": "opt_1", "text": "Decision Trees", "votes": 0},
    {"id": "opt_2", "text": "SVM", "votes": 0},
    {"id": "opt_3", "text": "Neural Networks", "votes": 0},
    {"id": "opt_4", "text": "Clustering", "votes": 0}
  ],
  "anonymous": false,
  "duration_seconds": 60,
  "ends_at": "2025-02-05T14:16:00Z",
  "status": "active",
  "created_at": "2025-02-05T14:15:00Z"
}
```

##### POST /polls/{poll_id}/responses
투표 응답 (학생)

**Request**:
```json
{
  "option_ids": ["opt_3"]
}
```

**Response** (201 Created):
```json
{
  "poll_id": "pol_901234",
  "option_ids": ["opt_3"],
  "submitted_at": "2025-02-05T14:15:23Z"
}
```

##### GET /polls/{poll_id}/results
투표 결과 조회

**Response** (200 OK):
```json
{
  "id": "pol_901234",
  "question": "Which ML algorithm should we cover next?",
  "type": "single_choice",
  "status": "closed",
  "total_responses": 41,
  "response_rate": 0.95,
  "options": [
    {
      "id": "opt_1",
      "text": "Decision Trees",
      "votes": 8,
      "percentage": 19.5
    },
    {
      "id": "opt_2",
      "text": "SVM",
      "votes": 5,
      "percentage": 12.2
    },
    {
      "id": "opt_3",
      "text": "Neural Networks",
      "votes": 23,
      "percentage": 56.1
    },
    {
      "id": "opt_4",
      "text": "Clustering",
      "votes": 5,
      "percentage": 12.2
    }
  ],
  "responses": [
    {
      "user": {
        "id": "usr_123456",
        "full_name": "Kim Younghee"
      },
      "option_ids": ["opt_3"],
      "submitted_at": "2025-02-05T14:15:23Z"
    }
    // ... (if not anonymous)
  ]
}
```

---

#### 3.2.5 Quizzes

##### POST /sessions/{session_id}/quizzes
퀴즈 생성 (교수)

**Request**:
```json
{
  "title": "Pre-class Check: Week 3",
  "time_limit_minutes": 10,
  "questions": [
    {
      "type": "multiple_choice",
      "question_text": "What is the main goal of supervised learning?",
      "points": 10,
      "options": [
        "To find patterns in unlabeled data",
        "To learn a mapping from inputs to outputs",
        "To reduce dimensionality",
        "To cluster similar items"
      ],
      "correct_option_index": 1,
      "explanation": "Supervised learning learns from labeled data to predict outputs."
    },
    {
      "type": "short_answer",
      "question_text": "Explain overfitting in 1-2 sentences.",
      "points": 15,
      "model_answer": "Overfitting occurs when a model learns training data too well, including noise, resulting in poor generalization to new data."
    }
  ]
}
```

**Response** (201 Created):
```json
{
  "id": "quz_567890",
  "session_id": "ses_345678",
  "title": "Pre-class Check: Week 3",
  "time_limit_minutes": 10,
  "total_points": 25,
  "question_count": 2,
  "status": "draft",
  "created_at": "2025-02-05T10:00:00Z"
}
```

##### POST /quizzes/{quiz_id}/publish
퀴즈 공개 (교수)

**Response** (200 OK):
```json
{
  "id": "quz_567890",
  "status": "published",
  "published_at": "2025-02-05T13:50:00Z"
}
```

##### POST /quizzes/{quiz_id}/submissions
퀴즈 제출 (학생)

**Request**:
```json
{
  "answers": [
    {
      "question_id": "qst_111",
      "type": "multiple_choice",
      "selected_option_index": 1
    },
    {
      "question_id": "qst_112",
      "type": "short_answer",
      "answer_text": "Overfitting happens when the model memorizes training data and fails to generalize."
    }
  ]
}
```

**Response** (201 Created):
```json
{
  "submission_id": "sub_998877",
  "quiz_id": "quz_567890",
  "submitted_at": "2025-02-05T14:05:00Z",
  "auto_graded_score": 22.5,
  "max_score": 25,
  "percentage": 90.0,
  "status": "graded",
  "feedback": [
    {
      "question_id": "qst_111",
      "correct": true,
      "points_earned": 10,
      "explanation": "Supervised learning learns from labeled data to predict outputs."
    },
    {
      "question_id": "qst_112",
      "correct": true,
      "points_earned": 12.5,
      "similarity_score": 0.85,
      "explanation": "Good understanding! Similarity to model answer: 85%"
    }
  ]
}
```

##### GET /quizzes/{quiz_id}/submissions/{submission_id}
제출 결과 조회

**Response** (200 OK):
```json
{
  "submission_id": "sub_998877",
  "quiz": {
    "id": "quz_567890",
    "title": "Pre-class Check: Week 3"
  },
  "student": {
    "id": "usr_123456",
    "full_name": "Kim Younghee"
  },
  "submitted_at": "2025-02-05T14:05:00Z",
  "score": 22.5,
  "max_score": 25,
  "percentage": 90.0,
  "status": "graded",
  "answers": [
    {
      "question": {
        "id": "qst_111",
        "type": "multiple_choice",
        "question_text": "What is the main goal of supervised learning?",
        "points": 10
      },
      "selected_option_index": 1,
      "correct": true,
      "points_earned": 10,
      "explanation": "Supervised learning learns from labeled data to predict outputs."
    },
    {
      "question": {
        "id": "qst_112",
        "type": "short_answer",
        "question_text": "Explain overfitting in 1-2 sentences.",
        "points": 15
      },
      "answer_text": "Overfitting happens when the model memorizes training data and fails to generalize.",
      "correct": true,
      "points_earned": 12.5,
      "similarity_score": 0.85,
      "feedback": "Good understanding! Similarity to model answer: 85%"
    }
  ]
}
```

---

#### 3.2.6 Analytics

##### GET /sessions/{session_id}/analytics/participation
세션 참여도 분석

**Response** (200 OK):
```json
{
  "session_id": "ses_345678",
  "total_participants": 43,
  "participation_summary": {
    "avg_speaking_time_seconds": 120,
    "avg_chat_messages": 5,
    "poll_response_rate": 0.95,
    "quiz_response_rate": 0.93
  },
  "participants": [
    {
      "user": {
        "id": "usr_123456",
        "full_name": "Kim Younghee"
      },
      "joined_at": "2025-02-05T14:02:15Z",
      "left_at": "2025-02-05T15:30:00Z",
      "speaking_time_seconds": 180,
      "chat_messages_count": 8,
      "polls_responded": 4,
      "quizzes_responded": 1,
      "hand_raises": 2,
      "participation_score": 92.5
    }
    // ...
  ],
  "timeline": [
    {
      "timestamp": "2025-02-05T14:00:00Z",
      "active_participants": 0
    },
    {
      "timestamp": "2025-02-05T14:05:00Z",
      "active_participants": 38
    },
    {
      "timestamp": "2025-02-05T14:10:00Z",
      "active_participants": 43
    }
    // ...
  ]
}
```

##### GET /courses/{course_id}/analytics/overview
코스 전체 분석

**Response** (200 OK):
```json
{
  "course_id": "crs_789012",
  "student_count": 45,
  "session_count": 24,
  "completed_sessions": 10,
  "avg_attendance_rate": 0.93,
  "avg_participation_score": 85.2,
  "avg_quiz_score": 82.5,
  "top_performers": [
    {
      "user": {
        "id": "usr_123456",
        "full_name": "Kim Younghee"
      },
      "avg_participation_score": 95.3,
      "avg_quiz_score": 94.2
    }
    // ...
  ],
  "at_risk_students": [
    {
      "user": {
        "id": "usr_555666",
        "full_name": "Park Junho"
      },
      "attendance_rate": 0.6,
      "avg_participation_score": 45.0,
      "avg_quiz_score": 52.3,
      "risk_factors": ["low_attendance", "low_participation"]
    }
  ]
}
```

---

### 3.3 에러 응답 형식

모든 에러는 일관된 형식으로 반환:

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": {
      "email": ["This field is required."],
      "password": ["Password must be at least 8 characters."]
    },
    "timestamp": "2025-11-27T10:30:00Z",
    "request_id": "req_abc123"
  }
}
```

#### 에러 코드

| HTTP Status | Error Code | 설명 |
|-------------|------------|------|
| 400 | `VALIDATION_ERROR` | 입력 검증 실패 |
| 401 | `UNAUTHORIZED` | 인증 실패 |
| 403 | `FORBIDDEN` | 권한 없음 |
| 404 | `NOT_FOUND` | 리소스 없음 |
| 409 | `CONFLICT` | 중복 리소스 |
| 429 | `RATE_LIMIT_EXCEEDED` | API 요청 한도 초과 |
| 500 | `INTERNAL_ERROR` | 서버 내부 오류 |
| 503 | `SERVICE_UNAVAILABLE` | 서비스 일시 중단 |

---

## 4. 데이터베이스 스키마

### 4.1 ERD (Entity Relationship Diagram)

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│    Users     │         │   Courses    │         │   Sessions   │
├──────────────┤         ├──────────────┤         ├──────────────┤
│ id (PK)      │         │ id (PK)      │         │ id (PK)      │
│ email (UQ)   │         │ title        │         │ course_id(FK)│
│ password_hash│    ┌────│ code         │    ┌────│ title        │
│ full_name    │    │    │ semester     │    │    │ scheduled_at │
│ role (ENUM)  │────┘    │ instructor_id│────┘    │ duration_min │
│ created_at   │         │   (FK→Users) │         │ status (ENUM)│
└──────────────┘         │ created_at   │         │ started_at   │
       │                 └──────────────┘         │ ended_at     │
       │                        │                 │ jitsi_room   │
       │                        │                 │ recording_url│
       │                        │                 │ created_at   │
       │                        │                 └──────────────┘
       │                        │                        │
       │                        │                        │
       ↓                        ↓                        ↓
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│ Enrollments  │         │SessionParts  │         │    Polls     │
├──────────────┤         ├──────────────┤         ├──────────────┤
│ id (PK)      │         │ id (PK)      │         │ id (PK)      │
│ user_id (FK) │         │ session_id FK│         │ session_id FK│
│ course_id(FK)│         │ user_id (FK) │         │ question     │
│ role (ENUM)  │         │ joined_at    │         │ type (ENUM)  │
│ enrolled_at  │         │ left_at      │         │ options (JSON│
└──────────────┘         │ speaking_time│         │ anonymous    │
                         │ chat_count   │         │ duration_sec │
                         └──────────────┘         │ ends_at      │
                                │                 │ status (ENUM)│
                                │                 │ created_at   │
                                │                 └──────────────┘
                                │                        │
                                ↓                        ↓
                         ┌──────────────┐         ┌──────────────┐
                         │PartEvents    │         │PollResponses │
                         ├──────────────┤         ├──────────────┤
                         │ id (PK)      │         │ id (PK)      │
                         │ participant  │         │ poll_id (FK) │
                         │   _id (FK)   │         │ user_id (FK) │
                         │ event_type   │         │ option_ids   │
                         │   (ENUM)     │         │   (JSON)     │
                         │ timestamp    │         │ submitted_at │
                         │ metadata(JSON│         └──────────────┘
                         └──────────────┘
```

### 4.2 테이블 정의

#### users
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('student', 'instructor', 'ta', 'admin')),
    avatar_url TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
```

#### courses
```sql
CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT,
    semester VARCHAR(50) NOT NULL,
    instructor_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    max_students INTEGER DEFAULT 50,
    enrollment_key VARCHAR(100),
    syllabus_url TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_courses_instructor ON courses(instructor_id);
CREATE INDEX idx_courses_semester ON courses(semester);
```

#### enrollments
```sql
CREATE TABLE enrollments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('student', 'ta', 'instructor')),
    enrolled_at TIMESTAMP DEFAULT NOW(),
    dropped_at TIMESTAMP,

    UNIQUE(user_id, course_id)
);

CREATE INDEX idx_enrollments_user ON enrollments(user_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
```

#### sessions
```sql
CREATE TABLE sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    scheduled_at TIMESTAMP NOT NULL,
    duration_minutes INTEGER DEFAULT 90,
    status VARCHAR(20) DEFAULT 'scheduled'
        CHECK (status IN ('scheduled', 'live', 'completed', 'cancelled')),
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    jitsi_room_name VARCHAR(255) UNIQUE,
    auto_record BOOLEAN DEFAULT FALSE,
    recording_url TEXT,
    recording_status VARCHAR(20) CHECK (recording_status IN ('none', 'recording', 'processing', 'available')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_sessions_course ON sessions(course_id);
CREATE INDEX idx_sessions_scheduled ON sessions(scheduled_at);
CREATE INDEX idx_sessions_status ON sessions(status);
```

#### session_participants
```sql
CREATE TABLE session_participants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    joined_at TIMESTAMP DEFAULT NOW(),
    left_at TIMESTAMP,
    speaking_time_seconds INTEGER DEFAULT 0,
    chat_messages_count INTEGER DEFAULT 0,
    hand_raises_count INTEGER DEFAULT 0,
    participation_score DECIMAL(5, 2),

    UNIQUE(session_id, user_id)
);

CREATE INDEX idx_participants_session ON session_participants(session_id);
CREATE INDEX idx_participants_user ON session_participants(user_id);
```

#### participation_events
```sql
CREATE TABLE participation_events (
    id BIGSERIAL PRIMARY KEY,
    participant_id UUID NOT NULL REFERENCES session_participants(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL
        CHECK (event_type IN ('speak_start', 'speak_end', 'chat', 'hand_raise', 'hand_lower', 'reaction')),
    timestamp TIMESTAMP DEFAULT NOW(),
    metadata JSONB
);

CREATE INDEX idx_events_participant ON participation_events(participant_id);
CREATE INDEX idx_events_timestamp ON participation_events(timestamp);
CREATE INDEX idx_events_type ON participation_events(event_type);
```

#### polls
```sql
CREATE TABLE polls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
    question TEXT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('single_choice', 'multiple_choice', 'true_false')),
    options JSONB NOT NULL, -- [{"id": "opt_1", "text": "Option A"}, ...]
    anonymous BOOLEAN DEFAULT FALSE,
    duration_seconds INTEGER,
    ends_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'draft'
        CHECK (status IN ('draft', 'active', 'closed')),
    show_results_after BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_polls_session ON polls(session_id);
CREATE INDEX idx_polls_status ON polls(status);
```

#### poll_responses
```sql
CREATE TABLE poll_responses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    poll_id UUID NOT NULL REFERENCES polls(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    option_ids JSONB NOT NULL, -- ["opt_1", "opt_3"]
    submitted_at TIMESTAMP DEFAULT NOW(),

    UNIQUE(poll_id, user_id)
);

CREATE INDEX idx_poll_responses_poll ON poll_responses(poll_id);
CREATE INDEX idx_poll_responses_user ON poll_responses(user_id);
```

#### quizzes
```sql
CREATE TABLE quizzes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID REFERENCES sessions(id) ON DELETE CASCADE,
    course_id UUID REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    time_limit_minutes INTEGER,
    total_points DECIMAL(6, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'draft'
        CHECK (status IN ('draft', 'published', 'closed')),
    published_at TIMESTAMP,
    due_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_quizzes_session ON quizzes(session_id);
CREATE INDEX idx_quizzes_course ON quizzes(course_id);
```

#### quiz_questions
```sql
CREATE TABLE quiz_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quiz_id UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    order_num INTEGER NOT NULL,
    type VARCHAR(20) NOT NULL
        CHECK (type IN ('multiple_choice', 'short_answer', 'essay', 'code', 'true_false')),
    question_text TEXT NOT NULL,
    points DECIMAL(6, 2) NOT NULL,
    options JSONB, -- For multiple choice: [{"index": 0, "text": "Option A"}, ...]
    correct_option_index INTEGER, -- For multiple choice
    model_answer TEXT, -- For short answer/essay
    explanation TEXT,
    metadata JSONB, -- For code questions: test cases, etc.
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_questions_quiz ON quiz_questions(quiz_id);
```

#### quiz_submissions
```sql
CREATE TABLE quiz_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quiz_id UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    submitted_at TIMESTAMP DEFAULT NOW(),
    score DECIMAL(6, 2),
    max_score DECIMAL(6, 2),
    status VARCHAR(20) DEFAULT 'submitted'
        CHECK (status IN ('in_progress', 'submitted', 'graded', 'returned')),
    graded_at TIMESTAMP,
    graded_by UUID REFERENCES users(id),

    UNIQUE(quiz_id, user_id)
);

CREATE INDEX idx_submissions_quiz ON quiz_submissions(quiz_id);
CREATE INDEX idx_submissions_user ON quiz_submissions(user_id);
```

#### quiz_answers
```sql
CREATE TABLE quiz_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID NOT NULL REFERENCES quiz_submissions(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    selected_option_index INTEGER, -- For multiple choice
    answer_text TEXT, -- For short answer/essay/code
    is_correct BOOLEAN,
    points_earned DECIMAL(6, 2),
    similarity_score DECIMAL(3, 2), -- For AI-graded answers
    feedback TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_answers_submission ON quiz_answers(submission_id);
CREATE INDEX idx_answers_question ON quiz_answers(question_id);
```

#### breakout_rooms
```sql
CREATE TABLE breakout_rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
    room_number INTEGER NOT NULL,
    name VARCHAR(255),
    started_at TIMESTAMP DEFAULT NOW(),
    ended_at TIMESTAMP,

    UNIQUE(session_id, room_number)
);

CREATE INDEX idx_breakout_session ON breakout_rooms(session_id);
```

#### breakout_participants
```sql
CREATE TABLE breakout_participants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID NOT NULL REFERENCES breakout_rooms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    joined_at TIMESTAMP DEFAULT NOW(),
    left_at TIMESTAMP,
    contribution_score DECIMAL(5, 2)
);

CREATE INDEX idx_breakout_participants_room ON breakout_participants(room_id);
CREATE INDEX idx_breakout_participants_user ON breakout_participants(user_id);
```

---

### 4.3 시계열 데이터 (TimescaleDB)

#### session_metrics (Hypertable)
```sql
CREATE TABLE session_metrics (
    time TIMESTAMPTZ NOT NULL,
    session_id UUID NOT NULL,
    active_participants INTEGER,
    avg_latency_ms INTEGER,
    packet_loss_rate DECIMAL(5, 4),
    bandwidth_usage_mbps DECIMAL(8, 2)
);

SELECT create_hypertable('session_metrics', 'time');

CREATE INDEX idx_session_metrics_session ON session_metrics(session_id, time DESC);
```

---

## 5. WebSocket 프로토콜

### 5.1 연결

**URL**: `wss://api.eduforum.com/ws/sessions/{session_id}`

**인증**: JWT를 query parameter로 전달
```
wss://api.eduforum.com/ws/sessions/ses_345678?token=eyJhbGci...
```

### 5.2 메시지 형식

모든 메시지는 JSON 형식:

```json
{
  "type": "event_type",
  "data": { },
  "timestamp": "2025-02-05T14:15:00Z"
}
```

### 5.3 클라이언트 → 서버 이벤트

#### `join_session`
세션 입장

```json
{
  "type": "join_session",
  "data": {
    "display_name": "Kim Younghee",
    "device_info": {
      "browser": "Chrome 120",
      "os": "macOS"
    }
  }
}
```

#### `speak_start`
발언 시작

```json
{
  "type": "speak_start",
  "data": {}
}
```

#### `speak_end`
발언 종료

```json
{
  "type": "speak_end",
  "data": {
    "duration_ms": 15300
  }
}
```

#### `chat_message`
채팅 메시지

```json
{
  "type": "chat_message",
  "data": {
    "message": "Could you explain overfitting again?",
    "reply_to": "msg_12345" // Optional
  }
}
```

#### `hand_raise`
손들기

```json
{
  "type": "hand_raise",
  "data": {}
}
```

#### `hand_lower`
손내리기

```json
{
  "type": "hand_lower",
  "data": {}
}
```

#### `reaction`
반응 (이모지)

```json
{
  "type": "reaction",
  "data": {
    "emoji": "👍"
  }
}
```

---

### 5.4 서버 → 클라이언트 이벤트

#### `session_joined`
입장 확인

```json
{
  "type": "session_joined",
  "data": {
    "participant_id": "prt_123456",
    "session": {
      "id": "ses_345678",
      "title": "Week 3: Supervised Learning",
      "status": "live"
    },
    "current_participants": 42
  },
  "timestamp": "2025-02-05T14:02:15Z"
}
```

#### `participant_joined`
다른 참가자 입장

```json
{
  "type": "participant_joined",
  "data": {
    "user": {
      "id": "usr_789012",
      "full_name": "Lee Jiyeon",
      "role": "student"
    },
    "joined_at": "2025-02-05T14:03:00Z"
  },
  "timestamp": "2025-02-05T14:03:00Z"
}
```

#### `participant_left`
참가자 퇴장

```json
{
  "type": "participant_left",
  "data": {
    "user_id": "usr_789012",
    "left_at": "2025-02-05T15:00:00Z"
  },
  "timestamp": "2025-02-05T15:00:00Z"
}
```

#### `chat_message`
채팅 메시지 브로드캐스트

```json
{
  "type": "chat_message",
  "data": {
    "message_id": "msg_12345",
    "user": {
      "id": "usr_123456",
      "full_name": "Kim Younghee"
    },
    "message": "Could you explain overfitting again?",
    "reply_to": null
  },
  "timestamp": "2025-02-05T14:10:30Z"
}
```

#### `poll_created`
투표 생성 알림

```json
{
  "type": "poll_created",
  "data": {
    "poll": {
      "id": "pol_901234",
      "question": "Which ML algorithm should we cover next?",
      "type": "single_choice",
      "options": [
        {"id": "opt_1", "text": "Decision Trees"},
        {"id": "opt_2", "text": "SVM"},
        {"id": "opt_3", "text": "Neural Networks"},
        {"id": "opt_4", "text": "Clustering"}
      ],
      "duration_seconds": 60,
      "ends_at": "2025-02-05T14:16:00Z"
    }
  },
  "timestamp": "2025-02-05T14:15:00Z"
}
```

#### `poll_response_update`
투표 응답 실시간 업데이트 (익명화된 집계)

```json
{
  "type": "poll_response_update",
  "data": {
    "poll_id": "pol_901234",
    "total_responses": 38,
    "response_rate": 0.88
  },
  "timestamp": "2025-02-05T14:15:45Z"
}
```

#### `poll_closed`
투표 종료 및 결과

```json
{
  "type": "poll_closed",
  "data": {
    "poll_id": "pol_901234",
    "results": {
      "opt_1": {"text": "Decision Trees", "votes": 8, "percentage": 19.5},
      "opt_2": {"text": "SVM", "votes": 5, "percentage": 12.2},
      "opt_3": {"text": "Neural Networks", "votes": 23, "percentage": 56.1},
      "opt_4": {"text": "Clustering", "votes": 5, "percentage": 12.2}
    },
    "total_responses": 41
  },
  "timestamp": "2025-02-05T14:16:10Z"
}
```

#### `hand_raised`
손들기 알림 (교수에게만)

```json
{
  "type": "hand_raised",
  "data": {
    "user": {
      "id": "usr_123456",
      "full_name": "Kim Younghee"
    },
    "queue_position": 3
  },
  "timestamp": "2025-02-05T14:20:00Z"
}
```

#### `participation_update`
참여도 업데이트 (교수 대시보드)

```json
{
  "type": "participation_update",
  "data": {
    "participants": [
      {
        "user_id": "usr_123456",
        "full_name": "Kim Younghee",
        "speaking_time_seconds": 180,
        "chat_messages_count": 8,
        "participation_score": 92.5
      }
      // ...
    ],
    "avg_speaking_time": 120
  },
  "timestamp": "2025-02-05T14:25:00Z"
}
```

#### `breakout_start`
분반 시작

```json
{
  "type": "breakout_start",
  "data": {
    "room_assignments": [
      {
        "room_id": "brk_111",
        "room_number": 1,
        "name": "Group 1",
        "participants": [
          {"id": "usr_123456", "full_name": "Kim Younghee"},
          {"id": "usr_789012", "full_name": "Lee Jiyeon"}
        ]
      }
      // ...
    ],
    "duration_minutes": 15,
    "ends_at": "2025-02-05T14:35:00Z"
  },
  "timestamp": "2025-02-05T14:20:00Z"
}
```

#### `breakout_end`
분반 종료 및 메인 룸 복귀

```json
{
  "type": "breakout_end",
  "data": {
    "message": "Breakout session ended. Returning to main room."
  },
  "timestamp": "2025-02-05T14:35:00Z"
}
```

---

## 6. 프론트엔드 아키텍처

### 6.1 컴포넌트 구조

```
src/
├── main.tsx                 # Entry point
├── App.tsx                  # Root component with routing
├── components/              # Reusable components
│   ├── common/              # Generic UI components
│   │   ├── Button.tsx
│   │   ├── Modal.tsx
│   │   ├── Spinner.tsx
│   │   └── ...
│   ├── layout/              # Layout components
│   │   ├── Header.tsx
│   │   ├── Sidebar.tsx
│   │   └── Footer.tsx
│   ├── session/             # Session-specific components
│   │   ├── VideoGrid.tsx    # Jitsi video grid
│   │   ├── ChatPanel.tsx    # Real-time chat
│   │   ├── PollModal.tsx    # Poll popup
│   │   ├── QuizModal.tsx    # Quiz taking interface
│   │   ├── ParticipantList.tsx
│   │   ├── HandRaiseQueue.tsx
│   │   └── Whiteboard.tsx   # Excalidraw integration
│   ├── analytics/           # Analytics components
│   │   ├── ParticipationDashboard.tsx
│   │   ├── TalkTimeChart.tsx
│   │   └── ReportTable.tsx
│   └── course/              # Course management components
│       ├── CourseCard.tsx
│       ├── SessionList.tsx
│       └── StudentTable.tsx
├── pages/                   # Page components (routes)
│   ├── LoginPage.tsx
│   ├── DashboardPage.tsx
│   ├── CoursePage.tsx
│   ├── SessionPage.tsx      # Live session UI
│   ├── AnalyticsPage.tsx
│   └── SettingsPage.tsx
├── stores/                  # Zustand state stores
│   ├── authStore.ts         # Authentication state
│   ├── sessionStore.ts      # Active session state
│   ├── chatStore.ts         # Chat messages
│   ├── pollStore.ts         # Polls data
│   └── analyticsStore.ts    # Analytics data
├── services/                # API & WebSocket services
│   ├── api.ts               # Axios instance with interceptors
│   ├── authService.ts       # Auth API calls
│   ├── courseService.ts     # Course API calls
│   ├── sessionService.ts    # Session API calls
│   ├── websocketService.ts  # WebSocket manager
│   └── jitsiService.ts      # Jitsi integration
├── hooks/                   # Custom React hooks
│   ├── useAuth.ts
│   ├── useWebSocket.ts
│   ├── useJitsi.ts
│   ├── usePoll.ts
│   └── useAnalytics.ts
├── utils/                   # Utility functions
│   ├── formatters.ts        # Date, number formatters
│   ├── validators.ts        # Form validation
│   └── constants.ts         # App constants
└── types/                   # TypeScript type definitions
    ├── api.types.ts
    ├── session.types.ts
    └── user.types.ts
```

### 6.2 상태 관리 (Zustand)

#### authStore.ts
```typescript
import create from 'zustand';

interface AuthState {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  accessToken: localStorage.getItem('accessToken'),
  isAuthenticated: !!localStorage.getItem('accessToken'),

  login: async (email, password) => {
    const response = await authService.login(email, password);
    localStorage.setItem('accessToken', response.access_token);
    localStorage.setItem('refreshToken', response.refresh_token);
    set({
      user: response.user,
      accessToken: response.access_token,
      isAuthenticated: true
    });
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    set({ user: null, accessToken: null, isAuthenticated: false });
  },

  refreshToken: async () => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) throw new Error('No refresh token');

    const response = await authService.refresh(refreshToken);
    localStorage.setItem('accessToken', response.access_token);
    set({ accessToken: response.access_token });
  }
}));
```

#### sessionStore.ts
```typescript
import create from 'zustand';

interface SessionState {
  currentSession: Session | null;
  participants: Participant[];
  polls: Poll[];
  chatMessages: ChatMessage[];
  isJitsiReady: boolean;

  setCurrentSession: (session: Session) => void;
  addParticipant: (participant: Participant) => void;
  removeParticipant: (userId: string) => void;
  addPoll: (poll: Poll) => void;
  addChatMessage: (message: ChatMessage) => void;
  setJitsiReady: (ready: boolean) => void;
}

export const useSessionStore = create<SessionState>((set) => ({
  currentSession: null,
  participants: [],
  polls: [],
  chatMessages: [],
  isJitsiReady: false,

  setCurrentSession: (session) => set({ currentSession: session }),

  addParticipant: (participant) => set((state) => ({
    participants: [...state.participants, participant]
  })),

  removeParticipant: (userId) => set((state) => ({
    participants: state.participants.filter(p => p.user.id !== userId)
  })),

  addPoll: (poll) => set((state) => ({
    polls: [...state.polls, poll]
  })),

  addChatMessage: (message) => set((state) => ({
    chatMessages: [...state.chatMessages, message]
  })),

  setJitsiReady: (ready) => set({ isJitsiReady: ready })
}));
```

### 6.3 WebSocket 통합

#### websocketService.ts
```typescript
import { io, Socket } from 'socket.io-client';

class WebSocketService {
  private socket: Socket | null = null;

  connect(sessionId: string, token: string) {
    this.socket = io(`wss://api.eduforum.com/ws/sessions/${sessionId}`, {
      query: { token },
      transports: ['websocket']
    });

    this.socket.on('connect', () => {
      console.log('WebSocket connected');
      this.emit('join_session', {
        display_name: useAuthStore.getState().user?.full_name,
        device_info: { browser: 'Chrome', os: 'macOS' }
      });
    });

    this.socket.on('disconnect', () => {
      console.log('WebSocket disconnected');
    });

    // Event listeners
    this.socket.on('chat_message', (data) => {
      useSessionStore.getState().addChatMessage(data.data);
    });

    this.socket.on('poll_created', (data) => {
      useSessionStore.getState().addPoll(data.data.poll);
    });

    this.socket.on('participant_joined', (data) => {
      useSessionStore.getState().addParticipant(data.data);
    });

    this.socket.on('participant_left', (data) => {
      useSessionStore.getState().removeParticipant(data.data.user_id);
    });
  }

  emit(eventType: string, data: any) {
    if (!this.socket) throw new Error('Socket not connected');
    this.socket.emit('message', {
      type: eventType,
      data,
      timestamp: new Date().toISOString()
    });
  }

  disconnect() {
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
    }
  }
}

export default new WebSocketService();
```

---

## 7. 인증 및 보안

### 7.1 JWT 인증 플로우

```
1. User Login
   ├─> POST /auth/login {email, password}
   │
   ├─> Server validates credentials
   │
   ├─> Generate Access Token (expires: 1 hour)
   ├─> Generate Refresh Token (expires: 7 days)
   │
   └─> Return {access_token, refresh_token, user}

2. API Request
   ├─> Client includes: Authorization: Bearer <access_token>
   │
   ├─> Server validates JWT signature
   ├─> Server checks expiration
   │
   └─> Process request

3. Token Refresh
   ├─> Access Token expired
   │
   ├─> POST /auth/refresh {refresh_token}
   │
   ├─> Server validates Refresh Token
   │
   └─> Return new Access Token
```

### 7.2 JWT Payload

```json
{
  "user_id": "usr_123456",
  "email": "student@university.edu",
  "role": "student",
  "exp": 1709654400,
  "iat": 1709650800,
  "jti": "token_unique_id"
}
```

### 7.3 RBAC (Role-Based Access Control)

#### Django Permission Decorator
```python
from functools import wraps
from rest_framework.response import Response
from rest_framework import status

def require_role(*allowed_roles):
    def decorator(view_func):
        @wraps(view_func)
        def wrapper(request, *args, **kwargs):
            if request.user.role not in allowed_roles:
                return Response(
                    {'error': 'Permission denied'},
                    status=status.HTTP_403_FORBIDDEN
                )
            return view_func(request, *args, **kwargs)
        return wrapper
    return decorator

# Usage
@require_role('instructor', 'admin')
def create_session(request, course_id):
    # Only instructors and admins can create sessions
    pass
```

### 7.4 보안 체크리스트

#### 데이터 암호화
- [x] HTTPS/TLS 1.3 강제 (모든 통신)
- [x] 비밀번호 해싱 (bcrypt, rounds=12)
- [x] 데이터베이스 암호화 (AWS RDS 자동)
- [x] S3 버킷 암호화 (AES-256)

#### 인증/인가
- [x] JWT 서명 검증
- [x] Refresh Token Rotation
- [x] Rate Limiting (로그인: 5회/분)
- [x] 2FA (선택적, 관리자 필수)

#### 입력 검증
- [x] Django REST Framework Serializers
- [x] SQL Injection 방지 (ORM 사용)
- [x] XSS 방지 (React 자동 이스케이프)
- [x] CSRF 토큰 (Django 기본)

#### 파일 업로드
- [x] 파일 타입 검증 (MIME type + extension)
- [x] 파일 크기 제한 (50MB)
- [x] 바이러스 스캔 (ClamAV 통합)
- [x] S3 Pre-signed URL (직접 업로드)

#### API 보안
- [x] Rate Limiting: 100 req/min (사용자별)
- [x] CORS 설정 (허용된 도메인만)
- [x] API Key for external services
- [x] Request ID 로깅

#### 규정 준수
- [x] GDPR: 개인정보 처리 동의
- [x] FERPA: 학생 기록 보호
- [x] 데이터 삭제 요청 처리
- [x] 감사 로그 (90일 보관)

---

## 8. 미디어 서버 통합

### 8.1 Jitsi Meet 아키텍처

```
┌─────────────────────────────────────────────────────────────────┐
│                      JITSI ARCHITECTURE                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐         ┌──────────────┐                     │
│  │   Browser    │         │   Browser    │                     │
│  │  (lib-jitsi)│         │  (lib-jitsi)│                     │
│  └──────┬───────┘         └──────┬───────┘                     │
│         │                        │                             │
│         │ XMPP (Websocket)       │                             │
│         ↓                        ↓                             │
│  ┌──────────────────────────────────────┐                      │
│  │          Prosody (XMPP Server)       │                      │
│  │  - Signaling                         │                      │
│  │  - Authentication                    │                      │
│  └──────────────┬───────────────────────┘                      │
│                 │                                               │
│                 ↓                                               │
│  ┌──────────────────────────────────────┐                      │
│  │       Jicofo (Conference Focus)      │                      │
│  │  - Room management                   │                      │
│  │  - Participant coordination          │                      │
│  └──────────────┬───────────────────────┘                      │
│                 │                                               │
│                 ↓                                               │
│  ┌──────────────────────────────────────┐                      │
│  │   Jitsi Videobridge (SFU)            │                      │
│  │  - WebRTC media routing              │                      │
│  │  - No transcoding                    │                      │
│  └──────────────────────────────────────┘                      │
│                                                                 │
│  ┌──────────────────────────────────────┐                      │
│  │   coturn (STUN/TURN)                 │                      │
│  │  - NAT traversal                     │                      │
│  └──────────────────────────────────────┘                      │
│                                                                 │
│  ┌──────────────────────────────────────┐                      │
│  │   Jibri (Recording)                  │                      │
│  │  - Chrome headless                   │                      │
│  │  - FFmpeg encoding                   │                      │
│  └──────────────────────────────────────┘                      │
└─────────────────────────────────────────────────────────────────┘
```

### 8.2 Jitsi JWT 인증

Django에서 Jitsi JWT 생성:

```python
import jwt
from datetime import datetime, timedelta
from django.conf import settings

def generate_jitsi_jwt(user, session, moderator=False):
    now = datetime.utcnow()
    payload = {
        'iss': 'eduforum',  # App ID
        'aud': 'jitsi',
        'sub': settings.JITSI_DOMAIN,  # meet.eduforum.com
        'room': session.jitsi_room_name,
        'exp': now + timedelta(hours=2),
        'iat': now,
        'context': {
            'user': {
                'id': str(user.id),
                'name': user.full_name,
                'email': user.email,
                'avatar': user.avatar_url,
                'moderator': moderator
            },
            'features': {
                'livestreaming': False,
                'recording': moderator,
                'screen-sharing': True
            }
        }
    }

    token = jwt.encode(
        payload,
        settings.JITSI_APP_SECRET,
        algorithm='HS256'
    )

    return token
```

### 8.3 Jitsi 프론트엔드 통합

```typescript
// jitsiService.ts
import { JitsiMeetExternalAPI } from 'lib-jitsi-meet';

class JitsiService {
  private api: JitsiMeetExternalAPI | null = null;

  initialize(containerId: string, config: JitsiConfig) {
    const domain = config.domain; // meet.eduforum.com
    const options = {
      roomName: config.room_name,
      width: '100%',
      height: '100%',
      parentNode: document.getElementById(containerId),
      jwt: config.jwt,
      configOverwrite: {
        startWithAudioMuted: true,
        startWithVideoMuted: false,
        enableWelcomePage: false,
        prejoinPageEnabled: false,
        disableDeepLinking: true,
        defaultLanguage: 'ko',
      },
      interfaceConfigOverwrite: {
        TOOLBAR_BUTTONS: [
          'microphone', 'camera', 'desktop', 'chat',
          'raisehand', 'participants-pane', 'tileview',
          'videobackgroundblur', 'settings', 'fullscreen',
          // Remove recording if not moderator
          ...(config.moderator ? ['recording'] : [])
        ],
        SHOW_JITSI_WATERMARK: false,
        SHOW_WATERMARK_FOR_GUESTS: false
      }
    };

    this.api = new JitsiMeetExternalAPI(domain, options);

    // Event listeners
    this.api.on('videoConferenceJoined', (event) => {
      console.log('Joined:', event);
      websocketService.emit('speak_start', {});
    });

    this.api.on('videoConferenceLeft', () => {
      console.log('Left conference');
    });

    this.api.on('participantJoined', (event) => {
      console.log('Participant joined:', event);
    });

    this.api.on('dominantSpeakerChanged', (id) => {
      console.log('Dominant speaker:', id);
      // Track speaking time
    });
  }

  dispose() {
    if (this.api) {
      this.api.dispose();
      this.api = null;
    }
  }

  executeCommand(command: string, ...args: any[]) {
    if (this.api) {
      this.api.executeCommand(command, ...args);
    }
  }

  // Utility methods
  muteAudio() {
    this.executeCommand('toggleAudio');
  }

  muteVideo() {
    this.executeCommand('toggleVideo');
  }

  startRecording() {
    this.executeCommand('startRecording', {
      mode: 'file', // or 'stream'
      dropboxToken: undefined
    });
  }

  stopRecording() {
    this.executeCommand('stopRecording', 'file');
  }
}

export default new JitsiService();
```

### 8.4 녹화 처리 플로우

```
1. Instructor clicks "Start Recording"
   │
   ├─> Frontend: jitsiService.startRecording()
   │
   ├─> Jitsi: Jibri starts Chrome headless
   │
   ├─> Jibri: FFmpeg encodes to MP4
   │
   └─> Jibri: Save to local storage

2. Instructor clicks "Stop Recording" / Session Ends
   │
   ├─> Jitsi: Jibri stops recording
   │
   ├─> Backend: Webhook notification
   │
   ├─> Celery Task: Upload to S3
   │
   ├─> Celery Task: Generate thumbnail
   │
   ├─> Celery Task: Transcribe audio (Whisper API)
   │
   ├─> Update DB: recording_url, recording_status = 'available'
   │
   └─> Notify students: "Recording available"
```

---

## 9. 배포 아키텍처

### 9.1 Kubernetes 배포

#### Namespace 구조
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: eduforum-prod
```

#### Django Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: django-api
  namespace: eduforum-prod
spec:
  replicas: 3
  selector:
    matchLabels:
      app: django-api
  template:
    metadata:
      labels:
        app: django-api
    spec:
      containers:
      - name: django
        image: eduforum/django-api:v1.0.0
        ports:
        - containerPort: 8000
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: url
        - name: REDIS_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: redis_url
        - name: DJANGO_SETTINGS_MODULE
          value: "config.settings.production"
        resources:
          requests:
            cpu: 500m
            memory: 512Mi
          limits:
            cpu: 1000m
            memory: 1Gi
        livenessProbe:
          httpGet:
            path: /health
            port: 8000
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready
            port: 8000
          initialDelaySeconds: 10
          periodSeconds: 5
```

#### Django Service
```yaml
apiVersion: v1
kind: Service
metadata:
  name: django-api-service
  namespace: eduforum-prod
spec:
  selector:
    app: django-api
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8000
  type: ClusterIP
```

#### HPA (Horizontal Pod Autoscaler)
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: django-api-hpa
  namespace: eduforum-prod
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: django-api
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

#### Ingress (Nginx)
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: eduforum-ingress
  namespace: eduforum-prod
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
    nginx.ingress.kubernetes.io/rate-limit: "100"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - api.eduforum.com
    secretName: eduforum-tls
  rules:
  - host: api.eduforum.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: django-api-service
            port:
              number: 80
```

### 9.2 Docker Compose (Development)

```yaml
version: '3.8'

services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: eduforum
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  django:
    build:
      context: ./backend
      dockerfile: Dockerfile
    command: python manage.py runserver 0.0.0.0:8000
    volumes:
      - ./backend:/app
    ports:
      - "8000:8000"
    environment:
      - DATABASE_URL=postgresql://postgres:postgres@db:5432/eduforum
      - REDIS_URL=redis://redis:6379/0
      - DEBUG=True
    depends_on:
      - db
      - redis

  channels:
    build:
      context: ./backend
      dockerfile: Dockerfile
    command: daphne -b 0.0.0.0 -p 8001 config.asgi:application
    volumes:
      - ./backend:/app
    ports:
      - "8001:8001"
    environment:
      - DATABASE_URL=postgresql://postgres:postgres@db:5432/eduforum
      - REDIS_URL=redis://redis:6379/0
    depends_on:
      - db
      - redis

  celery:
    build:
      context: ./backend
      dockerfile: Dockerfile
    command: celery -A config worker -l info
    volumes:
      - ./backend:/app
    environment:
      - DATABASE_URL=postgresql://postgres:postgres@db:5432/eduforum
      - REDIS_URL=redis://redis:6379/0
    depends_on:
      - db
      - redis

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile.dev
    command: npm run dev
    volumes:
      - ./frontend:/app
      - /app/node_modules
    ports:
      - "3000:3000"
    environment:
      - VITE_API_BASE_URL=http://localhost:8000

  jitsi:
    image: jitsi/jitsi-meet:latest
    ports:
      - "8443:443"
    environment:
      - ENABLE_AUTH=1
      - ENABLE_GUESTS=0
      - AUTH_TYPE=jwt
      - JWT_APP_ID=eduforum
      - JWT_APP_SECRET=your_secret_here

volumes:
  postgres_data:
```

### 9.3 CI/CD Pipeline (GitHub Actions)

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches:
      - main

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.11'

      - name: Install dependencies
        run: |
          cd backend
          pip install -r requirements.txt

      - name: Run tests
        run: |
          cd backend
          pytest --cov=. --cov-report=xml

      - name: Upload coverage
        uses: codecov/codecov-action@v3

  build-and-push:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Login to Docker Hub
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      - name: Build and push Django
        uses: docker/build-push-action@v4
        with:
          context: ./backend
          push: true
          tags: eduforum/django-api:${{ github.sha }},eduforum/django-api:latest

      - name: Build and push Frontend
        uses: docker/build-push-action@v4
        with:
          context: ./frontend
          push: true
          tags: eduforum/frontend:${{ github.sha }},eduforum/frontend:latest

  deploy:
    needs: build-and-push
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up kubectl
        uses: azure/setup-kubectl@v3

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ap-northeast-2

      - name: Update kubeconfig
        run: aws eks update-kubeconfig --name eduforum-cluster --region ap-northeast-2

      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/django-api django=eduforum/django-api:${{ github.sha }} -n eduforum-prod
          kubectl set image deployment/frontend frontend=eduforum/frontend:${{ github.sha }} -n eduforum-prod
          kubectl rollout status deployment/django-api -n eduforum-prod
          kubectl rollout status deployment/frontend -n eduforum-prod
```

---

## 10. 모니터링 및 로깅

### 10.1 Prometheus + Grafana

#### Django Metrics Export
```python
# backend/config/middleware.py
from prometheus_client import Counter, Histogram
import time

request_count = Counter(
    'http_requests_total',
    'Total HTTP requests',
    ['method', 'endpoint', 'status']
)

request_latency = Histogram(
    'http_request_duration_seconds',
    'HTTP request latency',
    ['method', 'endpoint']
)

class PrometheusMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        start_time = time.time()

        response = self.get_response(request)

        duration = time.time() - start_time
        request_latency.labels(
            method=request.method,
            endpoint=request.path
        ).observe(duration)

        request_count.labels(
            method=request.method,
            endpoint=request.path,
            status=response.status_code
        ).inc()

        return response
```

### 10.2 ELK Stack (Elasticsearch, Logstash, Kibana)

#### Structured Logging
```python
# backend/config/logging.py
LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'formatters': {
        'json': {
            '()': 'pythonjsonlogger.jsonlogger.JsonFormatter',
            'format': '%(asctime)s %(name)s %(levelname)s %(message)s'
        }
    },
    'handlers': {
        'console': {
            'class': 'logging.StreamHandler',
            'formatter': 'json'
        },
        'file': {
            'class': 'logging.handlers.RotatingFileHandler',
            'filename': '/var/log/eduforum/app.log',
            'maxBytes': 10485760,  # 10MB
            'backupCount': 5,
            'formatter': 'json'
        }
    },
    'root': {
        'level': 'INFO',
        'handlers': ['console', 'file']
    },
    'loggers': {
        'django': {
            'handlers': ['console', 'file'],
            'level': 'INFO',
            'propagate': False
        }
    }
}
```

### 10.3 Sentry (Error Tracking)

```python
# backend/config/settings/production.py
import sentry_sdk
from sentry_sdk.integrations.django import DjangoIntegration

sentry_sdk.init(
    dsn="https://xxx@sentry.io/yyy",
    integrations=[DjangoIntegration()],
    traces_sample_rate=0.1,
    send_default_pii=False,
    environment="production"
)
```

---

## 11. 성능 최적화

### 11.1 데이터베이스 최적화

#### 인덱스 전략
```sql
-- 자주 조회되는 컬럼에 인덱스
CREATE INDEX idx_sessions_course_scheduled ON sessions(course_id, scheduled_at);

-- 복합 인덱스 (WHERE + ORDER BY)
CREATE INDEX idx_participants_session_joined ON session_participants(session_id, joined_at DESC);

-- JSONB GIN 인덱스 (poll options)
CREATE INDEX idx_polls_options ON polls USING GIN (options);

-- 부분 인덱스 (status별)
CREATE INDEX idx_sessions_live ON sessions(course_id) WHERE status = 'live';
```

#### Query 최적화
```python
# N+1 문제 해결: select_related (ForeignKey)
sessions = Session.objects.select_related('course__instructor').all()

# N+1 문제 해결: prefetch_related (ManyToMany, Reverse FK)
courses = Course.objects.prefetch_related('enrollments__user').all()

# 필요한 컬럼만 선택
participants = SessionParticipant.objects.values('user_id', 'speaking_time_seconds')

# 집계 쿼리
from django.db.models import Avg, Count
stats = Session.objects.filter(course_id=course_id).aggregate(
    avg_participants=Avg('participant_count'),
    total_sessions=Count('id')
)
```

### 11.2 캐싱 전략

#### Redis 캐싱
```python
from django.core.cache import cache

# Function-based caching
def get_course_stats(course_id):
    cache_key = f'course_stats:{course_id}'
    stats = cache.get(cache_key)

    if stats is None:
        stats = calculate_stats(course_id)  # Expensive operation
        cache.set(cache_key, stats, timeout=3600)  # 1 hour

    return stats

# Decorator-based caching
from django.views.decorators.cache import cache_page

@cache_page(60 * 15)  # 15 minutes
def course_list(request):
    courses = Course.objects.all()
    # ...
```

#### CDN for Static Files
```python
# settings.py
AWS_S3_CUSTOM_DOMAIN = 'd111111abcdef8.cloudfront.net'
STATIC_URL = f'https://{AWS_S3_CUSTOM_DOMAIN}/static/'
MEDIA_URL = f'https://{AWS_S3_CUSTOM_DOMAIN}/media/'
```

### 11.3 프론트엔드 최적화

#### Code Splitting
```typescript
// Lazy load pages
const SessionPage = lazy(() => import('./pages/SessionPage'));
const AnalyticsPage = lazy(() => import('./pages/AnalyticsPage'));

function App() {
  return (
    <Suspense fallback={<Spinner />}>
      <Routes>
        <Route path="/session/:id" element={<SessionPage />} />
        <Route path="/analytics" element={<AnalyticsPage />} />
      </Routes>
    </Suspense>
  );
}
```

#### Image Optimization
```typescript
// Use WebP with fallback
<picture>
  <source srcSet="avatar.webp" type="image/webp" />
  <img src="avatar.jpg" alt="User avatar" loading="lazy" />
</picture>
```

---

## 12. 테스트 전략

### 12.1 백엔드 테스트

#### Unit Tests (pytest)
```python
# backend/tests/test_auth.py
import pytest
from django.contrib.auth import get_user_model

User = get_user_model()

@pytest.mark.django_db
def test_create_user():
    user = User.objects.create_user(
        email='test@example.com',
        password='testpass123',
        full_name='Test User',
        role='student'
    )
    assert user.email == 'test@example.com'
    assert user.check_password('testpass123')
    assert user.is_active is True

@pytest.mark.django_db
def test_login_api(client):
    User.objects.create_user(
        email='test@example.com',
        password='testpass123',
        full_name='Test User',
        role='student'
    )

    response = client.post('/api/v1/auth/login', {
        'email': 'test@example.com',
        'password': 'testpass123'
    })

    assert response.status_code == 200
    assert 'access_token' in response.json()
```

#### Integration Tests
```python
# backend/tests/test_session.py
import pytest

@pytest.mark.django_db
def test_create_session_flow(authenticated_client, course):
    # Create session
    response = authenticated_client.post(
        f'/api/v1/courses/{course.id}/sessions',
        {
            'title': 'Test Session',
            'scheduled_at': '2025-02-05T14:00:00Z',
            'duration_minutes': 90
        }
    )
    assert response.status_code == 201
    session_id = response.json()['id']

    # Start session
    response = authenticated_client.post(
        f'/api/v1/sessions/{session_id}/start'
    )
    assert response.status_code == 200
    assert response.json()['status'] == 'live'
```

### 12.2 프론트엔드 테스트

#### Unit Tests (Vitest + React Testing Library)
```typescript
// frontend/src/components/PollModal.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import PollModal from './PollModal';

describe('PollModal', () => {
  it('renders poll question and options', () => {
    const poll = {
      id: 'pol_123',
      question: 'Which topic?',
      options: [
        { id: 'opt_1', text: 'Option A' },
        { id: 'opt_2', text: 'Option B' }
      ]
    };

    render(<PollModal poll={poll} onSubmit={vi.fn()} />);

    expect(screen.getByText('Which topic?')).toBeInTheDocument();
    expect(screen.getByText('Option A')).toBeInTheDocument();
    expect(screen.getByText('Option B')).toBeInTheDocument();
  });

  it('submits selected option', async () => {
    const onSubmit = vi.fn();
    const poll = {
      id: 'pol_123',
      question: 'Which topic?',
      options: [
        { id: 'opt_1', text: 'Option A' }
      ]
    };

    render(<PollModal poll={poll} onSubmit={onSubmit} />);

    fireEvent.click(screen.getByText('Option A'));
    fireEvent.click(screen.getByText('Submit'));

    expect(onSubmit).toHaveBeenCalledWith(['opt_1']);
  });
});
```

#### E2E Tests (Playwright)
```typescript
// frontend/e2e/session.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Session Flow', () => {
  test('instructor can start and end session', async ({ page }) => {
    // Login
    await page.goto('http://localhost:3000/login');
    await page.fill('[name="email"]', 'instructor@test.com');
    await page.fill('[name="password"]', 'password123');
    await page.click('button[type="submit"]');

    // Navigate to course
    await page.waitForURL('**/dashboard');
    await page.click('text=Introduction to ML');

    // Start session
    await page.click('text=Week 3: Supervised Learning');
    await page.click('button:has-text("Start Session")');

    // Verify session is live
    await expect(page.locator('.session-status')).toHaveText('Live');

    // End session
    await page.click('button:has-text("End Session")');
    await expect(page.locator('.session-status')).toHaveText('Completed');
  });
});
```

### 12.3 부하 테스트 (Locust)

```python
# locustfile.py
from locust import HttpUser, task, between

class EduForumUser(HttpUser):
    wait_time = between(1, 3)

    def on_start(self):
        # Login
        response = self.client.post('/api/v1/auth/login', json={
            'email': 'student@test.com',
            'password': 'password123'
        })
        self.token = response.json()['access_token']
        self.headers = {'Authorization': f'Bearer {self.token}'}

    @task(3)
    def get_courses(self):
        self.client.get('/api/v1/courses', headers=self.headers)

    @task(2)
    def get_sessions(self):
        self.client.get('/api/v1/courses/crs_123/sessions', headers=self.headers)

    @task(1)
    def join_session(self):
        self.client.post(
            '/api/v1/sessions/ses_456/join',
            headers=self.headers
        )
```

---

## 부록

### A. 환경 변수

```bash
# .env.example

# Django
SECRET_KEY=your-secret-key-here
DEBUG=False
ALLOWED_HOSTS=api.eduforum.com,localhost

# Database
DATABASE_URL=postgresql://user:password@db:5432/eduforum

# Redis
REDIS_URL=redis://redis:6379/0

# AWS
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_STORAGE_BUCKET_NAME=eduforum-media
AWS_S3_REGION_NAME=ap-northeast-2

# Jitsi
JITSI_DOMAIN=meet.eduforum.com
JITSI_APP_ID=eduforum
JITSI_APP_SECRET=your-jitsi-secret

# OpenAI
OPENAI_API_KEY=sk-xxx

# Email
EMAIL_BACKEND=django.core.mail.backends.smtp.EmailBackend
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USE_TLS=True
EMAIL_HOST_USER=noreply@eduforum.com
EMAIL_HOST_PASSWORD=your-email-password

# Sentry
SENTRY_DSN=https://xxx@sentry.io/yyy

# CORS
CORS_ALLOWED_ORIGINS=https://eduforum.com,http://localhost:3000
```

### B. 개발 환경 설정

```bash
# 백엔드 셋업
cd backend
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
python manage.py migrate
python manage.py createsuperuser
python manage.py runserver

# 프론트엔드 셋업
cd frontend
npm install
npm run dev

# Docker Compose 실행
docker-compose up -d
```

### C. API 문서

**Swagger UI**: https://api.eduforum.com/swagger/
**ReDoc**: https://api.eduforum.com/redoc/

---

**문서 끝**

다음 단계: [UI/UX 디자인 가이드](./05-ui-ux-design.md)
