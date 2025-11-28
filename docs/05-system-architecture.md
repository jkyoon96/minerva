# EduForum 시스템 아키텍처

> **버전**: 1.1
> **최종 수정일**: 2025-01-29
> **참조 문서**: PRD (03-product-requirements.md), 기능 세분화 (04-feature-breakdown.md), 기술 아키텍처 (02-technical-architecture.md)

---

## 목차

1. [시스템 개요](#1-시스템-개요)
2. [아키텍처 원칙](#2-아키텍처-원칙)
3. [고수준 아키텍처](#3-고수준-아키텍처)
4. [서비스 아키텍처](#4-서비스-아키텍처)
5. [데이터 아키텍처](#5-데이터-아키텍처)
6. [인프라 아키텍처](#6-인프라-아키텍처)
7. [보안 아키텍처](#7-보안-아키텍처)
8. [확장성 및 성능](#8-확장성-및-성능)
9. [통합 및 외부 시스템](#9-통합-및-외부-시스템)
10. [모니터링 및 운영](#10-모니터링-및-운영)

---

## 1. 시스템 개요

### 1.1 프로젝트 목표

EduForum은 미네르바 대학의 Active Learning Forum을 참고한 **대화형 온라인 학습 플랫폼**입니다. 실시간 화상 세미나, 능동적 학습 도구, AI 기반 평가 시스템을 통합하여 교수와 학생 간의 상호작용을 극대화합니다.

### 1.2 주요 요구사항

| 구분 | 요구사항 | 목표값 |
|------|---------|--------|
| **성능** | 비디오 지연시간 | < 300ms |
| **성능** | 세미나 룸 생성 시간 | < 3초 |
| **확장성** | 동시 세션 수 | 1,000개 |
| **확장성** | 세션당 참가자 | 최대 50명 |
| **가용성** | 시스템 업타임 | 99.9% |
| **실시간** | 참여도 대시보드 업데이트 | 1초 이내 |

### 1.3 핵심 기능 영역 (Epics)

```
┌────────────────────────────────────────────────────────────────────────────┐
│                           EduForum Platform                                 │
├──────────────┬──────────────┬──────────────┬──────────────┬───────────────┤
│    E1        │     E2       │     E3       │     E4       │     E5/E6     │
│  사용자 인증  │  코스 관리    │ 실시간 세미나 │ 액티브 러닝  │  평가/분석     │
│              │              │              │              │               │
│ • 회원가입    │ • 코스 CRUD  │ • 화상 회의   │ • 투표/퀴즈  │ • 자동 채점   │
│ • 로그인/OAuth│ • 학생 등록  │ • 화면 공유   │ • 분반 토론  │ • AI 채점    │
│ • 2FA        │ • 세션 관리  │ • 채팅/녹화   │ • 화이트보드 │ • 참여도 측정 │
│ • RBAC      │ • 과제/성적  │ • 손들기/반응 │ • 토론 도구  │ • 학습 분석   │
└──────────────┴──────────────┴──────────────┴──────────────┴───────────────┘
```

---

## 2. 아키텍처 원칙

### 2.1 설계 원칙

| 원칙 | 설명 | 적용 방안 |
|------|------|----------|
| **마이크로서비스** | 독립적 배포 및 확장 가능한 서비스 단위 | 도메인별 서비스 분리 |
| **이벤트 기반** | 서비스 간 느슨한 결합 | 메시지 큐/이벤트 버스 활용 |
| **확장성 우선** | 수평 확장 가능한 설계 | Stateless 서비스, 캐싱 전략 |
| **실시간 처리** | 낮은 지연시간 | WebSocket, WebRTC, Redis Pub/Sub |
| **보안 내재화** | 모든 계층에 보안 적용 | Zero Trust, 암호화, RBAC |

### 2.2 기술 스택 선정

```yaml
Frontend:
  Framework: Next.js 14+ (App Router, TypeScript)
  Rendering: SSR, SSG, ISR, React Server Components
  State Management: Zustand / React Context
  Real-time: Socket.io-client
  WebRTC: simple-peer / mediasoup-client
  Charts: Chart.js, Recharts, D3.js
  UI Components: shadcn/ui, Tailwind CSS
  Code Editor: Monaco Editor
  Auth: NextAuth.js (Auth.js)

Backend:
  Framework: Spring Boot 3.2.x + Java 17
  Security: Spring Security + JWT
  ORM: Spring Data JPA
  Task Queue: Spring Scheduler + Redis
  WebSocket: Spring WebSocket / Socket.io
  API Docs: Springdoc OpenAPI (Swagger)

Media Server:
  Primary: Jitsi Meet / mediasoup (SFU)
  Recording: Jibri / FFmpeg
  Speech-to-Text: OpenAI Whisper / Google STT

AI/ML:
  NLP: BERT / Sentence-BERT
  Plagiarism: MOSS
  Embeddings: OpenAI / HuggingFace

Database:
  Primary: PostgreSQL 16
  Cache: Redis 7
  Search: Elasticsearch 8
  Document: MongoDB (선택)

Infrastructure:
  Container: Docker
  Orchestration: Kubernetes (K8s)
  Cloud: AWS / GCP / Azure
  CDN: CloudFront / CloudFlare
  Storage: S3 / MinIO
```

---

## 3. 고수준 아키텍처

### 3.1 시스템 컨텍스트 다이어그램

```
                                    ┌─────────────────┐
                                    │   외부 시스템    │
                                    │  • Google OAuth │
                                    │  • LMS (LTI)    │
                                    │  • 이메일 서버   │
                                    └────────┬────────┘
                                             │
    ┌────────────────┐              ┌────────▼────────┐              ┌────────────────┐
    │     학생       │◄────────────►│                 │◄────────────►│      교수      │
    │   (Web/Mobile) │              │   EduForum      │              │   (Web/Mobile) │
    └────────────────┘              │   Platform      │              └────────────────┘
                                    │                 │
    ┌────────────────┐              │  • 실시간 세미나 │              ┌────────────────┐
    │      TA        │◄────────────►│  • 학습 도구    │◄────────────►│     관리자     │
    │   (Web/Mobile) │              │  • 평가/분석    │              │     (Admin)    │
    └────────────────┘              └────────┬────────┘              └────────────────┘
                                             │
                                    ┌────────▼────────┐
                                    │   클라우드 인프라 │
                                    │  • AWS/GCP      │
                                    │  • K8s Cluster  │
                                    └─────────────────┘
```

### 3.2 논리적 아키텍처

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                              Presentation Layer                                   │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐                   │
│  │   Next.js App   │  │  Mobile App     │  │   Admin Panel   │                   │
│  │  (SSR/SSG/CSR)  │  │  (PWA/Native)   │  │ (Spring Admin)  │                   │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘                   │
└───────────┼─────────────────────┼─────────────────────┼──────────────────────────┘
            │                     │                     │
            ▼                     ▼                     ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                                API Gateway Layer                                  │
│  ┌──────────────────────────────────────────────────────────────────────────┐   │
│  │                         Kong / Nginx Ingress                              │   │
│  │  • Rate Limiting  • Auth Validation  • Request Routing  • SSL Termination│   │
│  └──────────────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────────────┬──────────────────────────────────────────┘
                                        │
                                        ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                              Application Layer                                    │
│                                                                                   │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │ Auth Service │ │Course Service│ │Media Service │ │Learning Svc  │            │
│  │              │ │              │ │              │ │              │            │
│  │• 회원가입     │ │• 코스 CRUD   │ │• WebRTC SFU  │ │• 투표/퀴즈   │            │
│  │• 로그인/OAuth │ │• 학생 관리   │ │• 녹화        │ │• 분반        │            │
│  │• 2FA/RBAC   │ │• 세션 관리   │ │• 스트리밍    │ │• 화이트보드  │            │
│  └──────┬───────┘ └──────┬───────┘ └──────┬───────┘ └──────┬───────┘            │
│         │                │                │                │                     │
│  ┌──────┴────────────────┴────────────────┴────────────────┴──────┐             │
│  │                    Assessment Service                          │             │
│  │  • 자동 채점 (객관식)  • AI 채점 (주관식)  • 코드 실행 (Judge0)  │             │
│  └────────────────────────────────────────────────────────────────┘             │
│                                                                                   │
│  ┌──────────────────────────────────────────────────────────────────────────┐   │
│  │                        Analytics Service                                  │   │
│  │  • 참여도 측정  • 실시간 대시보드  • 조기 경보  • 네트워크 분석            │   │
│  └──────────────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────────────┬──────────────────────────────────────────┘
                                        │
                                        ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                              Integration Layer                                    │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐                      │
│  │  Message Queue │  │  Event Bus     │  │  WebSocket Hub │                      │
│  │ (Redis/Spring) │  │  (Redis Pub/Sub)│ │  (Socket.io)   │                      │
│  └────────────────┘  └────────────────┘  └────────────────┘                      │
└───────────────────────────────────────┬──────────────────────────────────────────┘
                                        │
                                        ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                                Data Layer                                         │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐  ┌───────────────┐  │
│  │   PostgreSQL   │  │     Redis      │  │ Elasticsearch  │  │     S3        │  │
│  │   (Primary DB) │  │   (Cache/MQ)   │  │   (Search)     │  │  (Storage)    │  │
│  └────────────────┘  └────────────────┘  └────────────────┘  └───────────────┘  │
└──────────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 서비스 아키텍처

### 4.0 프론트엔드 아키텍처 (Next.js App Router)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      Next.js App Router Structure                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  /app                                                                        │
│  ├── (marketing)/              # 공개 페이지 (SSG)                          │
│  │   ├── page.tsx              # 랜딩 페이지                                │
│  │   └── features/page.tsx     # 기능 소개                                  │
│  │                                                                           │
│  ├── (auth)/                   # 인증 페이지 (SSR)                          │
│  │   ├── layout.tsx            # 인증 레이아웃                              │
│  │   ├── login/page.tsx        # 로그인                                     │
│  │   ├── register/page.tsx     # 회원가입                                   │
│  │   ├── forgot-password/      # 비밀번호 찾기                              │
│  │   └── verify-2fa/page.tsx   # 2FA 검증                                   │
│  │                                                                           │
│  ├── (dashboard)/              # 인증 필요 영역                             │
│  │   ├── layout.tsx            # 사이드바, 네비게이션 (middleware 인증)     │
│  │   ├── page.tsx              # 대시보드 (SSR + Streaming)                 │
│  │   │                                                                       │
│  │   ├── courses/              # 코스 관리                                  │
│  │   │   ├── page.tsx          # 코스 목록 (SSR + ISR)                      │
│  │   │   ├── [courseId]/                                                     │
│  │   │   │   ├── page.tsx      # 코스 상세                                  │
│  │   │   │   ├── students/     # 학생 관리                                  │
│  │   │   │   ├── sessions/     # 세션 목록                                  │
│  │   │   │   ├── assignments/  # 과제 관리                                  │
│  │   │   │   └── grades/       # 성적 관리                                  │
│  │   │   └── create/page.tsx   # 코스 생성                                  │
│  │   │                                                                       │
│  │   ├── analytics/            # 학습 분석                                  │
│  │   │   ├── page.tsx          # 분석 대시보드                              │
│  │   │   └── reports/          # 리포트                                     │
│  │   │                                                                       │
│  │   └── settings/             # 설정                                       │
│  │       ├── profile/          # 프로필 설정                                │
│  │       └── security/         # 보안 설정 (2FA)                            │
│  │                                                                           │
│  ├── (live)/                   # 라이브 세션 (CSR 전용)                     │
│  │   ├── layout.tsx            # 전체 화면 레이아웃                         │
│  │   ├── session/[sessionId]/  # 라이브 세션 룸                             │
│  │   │   ├── page.tsx          # 'use client' - WebRTC                      │
│  │   │   ├── _components/      # 클라이언트 컴포넌트                        │
│  │   │   │   ├── VideoGrid.tsx                                              │
│  │   │   │   ├── ChatPanel.tsx                                              │
│  │   │   │   ├── ParticipantList.tsx                                        │
│  │   │   │   ├── PollModal.tsx                                              │
│  │   │   │   └── BreakoutManager.tsx                                        │
│  │   │   └── loading.tsx       # 스켈레톤 UI                                │
│  │   │                                                                       │
│  │   └── whiteboard/[id]/      # 화이트보드 (CSR)                           │
│  │                                                                           │
│  ├── api/                      # API Routes (BFF Layer)                     │
│  │   ├── auth/                                                               │
│  │   │   └── [...nextauth]/route.ts  # NextAuth.js                          │
│  │   ├── proxy/                                                              │
│  │   │   └── [...path]/route.ts      # Spring Boot API 프록시               │
│  │   └── webhooks/                                                           │
│  │       └── route.ts                # 외부 웹훅                            │
│  │                                                                           │
│  ├── layout.tsx                # 루트 레이아웃                              │
│  ├── error.tsx                 # 전역 에러 핸들러                           │
│  ├── not-found.tsx             # 404 페이지                                 │
│  └── loading.tsx               # 전역 로딩                                  │
│                                                                              │
│  Rendering Strategy:                                                         │
│  ├── SSG (Static): 랜딩, 도움말, 정적 콘텐츠                                │
│  ├── SSR (Dynamic): 대시보드, 코스 목록, 인증 필요 페이지                   │
│  ├── ISR (Hybrid): 코스 상세 (revalidate: 60초)                             │
│  └── CSR (Client): 라이브 세션, 화이트보드, 실시간 기능                      │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### Next.js 미들웨어 (인증)

```typescript
// middleware.ts
import { withAuth } from "next-auth/middleware";

export default withAuth({
  pages: {
    signIn: "/login",
  },
});

export const config = {
  matcher: [
    "/dashboard/:path*",
    "/courses/:path*",
    "/session/:path*",
    "/analytics/:path*",
  ],
};
```

#### Server Components vs Client Components

| 구분 | Server Components | Client Components |
|------|-------------------|-------------------|
| **사용 영역** | 대시보드, 코스 목록, 성적 | 라이브 세션, 채팅, 투표 |
| **데이터 페칭** | 직접 DB/API 호출 | useEffect, SWR, React Query |
| **상태 관리** | 불필요 | Zustand, React Context |
| **번들 크기** | 0 (서버에서 실행) | 클라이언트로 전송 |
| **SEO** | 최적화됨 | hydration 필요 |

### 4.1 서비스 분해

| 서비스 | 책임 | 주요 API | 통신 방식 |
|--------|------|---------|----------|
| **Auth Service** | 인증, 인가, 사용자 관리 | `/api/auth/*` | REST, JWT |
| **Course Service** | 코스, 세션, 과제, 성적 관리 | `/api/courses/*` | REST |
| **Media Service** | 화상 회의, 녹화, 스트리밍 | `/api/media/*` | WebRTC, WebSocket |
| **Learning Service** | 투표, 퀴즈, 분반, 화이트보드 | `/api/learning/*` | REST, WebSocket |
| **Assessment Service** | 자동/AI 채점, 피드백 | `/api/assessment/*` | REST, Async (Spring) |
| **Analytics Service** | 참여도, 리포트, 경보 | `/api/analytics/*` | REST, WebSocket |
| **Notification Service** | 이메일, 푸시, 인앱 알림 | `/api/notifications/*` | Async (Spring) |

### 4.2 서비스 상세 설계

#### 4.2.1 Auth Service

```
┌─────────────────────────────────────────────────────────────────┐
│                        Auth Service                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Endpoints:                                                      │
│  ├── POST /api/auth/register        # 회원가입                   │
│  ├── POST /api/auth/login           # 로그인                     │
│  ├── POST /api/auth/logout          # 로그아웃                   │
│  ├── POST /api/auth/refresh         # 토큰 갱신                  │
│  ├── POST /api/auth/oauth/{provider}# OAuth 로그인               │
│  ├── POST /api/auth/2fa/setup       # 2FA 설정                   │
│  ├── POST /api/auth/2fa/verify      # 2FA 검증                   │
│  ├── POST /api/auth/password/reset  # 비밀번호 재설정            │
│  └── GET  /api/auth/me              # 현재 사용자 정보            │
│                                                                  │
│  Models:                                                         │
│  ├── User (id, email, password_hash, role, ...)                 │
│  ├── Role (id, name, permissions)                               │
│  ├── OAuthAccount (user_id, provider, provider_id)              │
│  └── TwoFactorAuth (user_id, secret, backup_codes)              │
│                                                                  │
│  Dependencies:                                                   │
│  ├── PostgreSQL (사용자 데이터)                                  │
│  ├── Redis (세션, 토큰 블랙리스트)                               │
│  └── Email Service (인증 메일)                                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 4.2.2 Media Service (WebRTC SFU)

```
┌─────────────────────────────────────────────────────────────────┐
│                        Media Service                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                   SFU Architecture                       │    │
│  │                                                          │    │
│  │    ┌────────┐     ┌────────────────┐     ┌────────┐    │    │
│  │    │Client A│────►│                │◄────│Client B│    │    │
│  │    └────────┘     │   SFU Server   │     └────────┘    │    │
│  │                   │  (mediasoup/   │                    │    │
│  │    ┌────────┐     │   Jitsi)       │     ┌────────┐    │    │
│  │    │Client C│────►│                │◄────│Client D│    │    │
│  │    └────────┘     └────────────────┘     └────────┘    │    │
│  │                                                          │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  Features:                                                       │
│  ├── 최대 50명 동시 접속                                         │
│  ├── HD 비디오 (720p/1080p)                                     │
│  ├── 적응형 비트레이트 (ABR)                                     │
│  ├── 화면 공유 (Screen Share)                                   │
│  ├── 서버 측 녹화 (Jibri/FFmpeg)                                │
│  └── 자동 자막 생성 (Whisper)                                   │
│                                                                  │
│  WebSocket Events:                                               │
│  ├── room:join / room:leave                                     │
│  ├── media:produce / media:consume                              │
│  ├── participant:mute / participant:unmute                      │
│  └── recording:start / recording:stop                           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 4.2.3 Learning Service (액티브 러닝)

```
┌─────────────────────────────────────────────────────────────────┐
│                      Learning Service                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  [투표/퀴즈 모듈]                                                 │
│  ├── POST /api/learning/polls              # 투표 생성           │
│  ├── POST /api/learning/polls/{id}/vote    # 투표 참여           │
│  ├── GET  /api/learning/polls/{id}/results # 결과 조회           │
│  ├── POST /api/learning/quizzes            # 퀴즈 생성           │
│  ├── POST /api/learning/quizzes/{id}/submit# 답안 제출           │
│  └── Question Bank CRUD                                          │
│                                                                  │
│  [분반 모듈]                                                      │
│  ├── POST /api/learning/breakout/create    # 분반 생성           │
│  ├── POST /api/learning/breakout/assign    # 자동/수동 배정      │
│  ├── POST /api/learning/breakout/move      # 학생 이동           │
│  ├── POST /api/learning/breakout/broadcast # 전체 메시지          │
│  └── DELETE /api/learning/breakout/end     # 분반 종료           │
│                                                                  │
│  [화이트보드 모듈]                                                │
│  ├── CRDT 기반 실시간 동기화                                     │
│  ├── WebSocket: whiteboard:draw, whiteboard:sync                │
│  └── PNG/PDF 내보내기                                            │
│                                                                  │
│  분반 배정 알고리즘:                                              │
│  ├── 랜덤 (Random)                                               │
│  ├── 균등 분배 (Round-robin)                                     │
│  └── 성적 기반 혼합 (Mixed by Grade)                             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 4.2.4 Assessment Service (평가)

```
┌─────────────────────────────────────────────────────────────────┐
│                     Assessment Service                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  [자동 채점 파이프라인]                                           │
│                                                                  │
│  학생 답안 ──► 객관식? ──► 즉시 채점 (<1초) ──► 결과 반환        │
│                  │                                               │
│                  ▼ (주관식)                                      │
│            AI 채점 큐 ──► BERT Embedding ──► 유사도 계산         │
│                  │                 │                             │
│                  │                 ▼                             │
│                  │        키워드 분석 ──► 1차 점수 + 신뢰도      │
│                  │                 │                             │
│                  │                 ▼                             │
│                  │        교수 검토 ──► 최종 점수                │
│                  │                                               │
│                  ▼ (코딩 문제)                                   │
│            Judge0 컨테이너 ──► 테스트 실행 ──► 결과 반환         │
│                                    │                             │
│                                    ▼                             │
│                            MOSS 표절 검사                         │
│                                                                  │
│  지원 언어: Python, Java, C++, JavaScript, Go                    │
│  시간 제한: 기본 5초 / 메모리 제한: 256MB                        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

#### 4.2.5 Analytics Service (학습 분석)

```
┌─────────────────────────────────────────────────────────────────┐
│                      Analytics Service                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  [실시간 참여도 대시보드 - TalkTime]                              │
│                                                                  │
│  데이터 수집:                                                    │
│  ├── 발언 시간 (Voice Activity Detection)                       │
│  ├── 채팅 메시지 수                                              │
│  ├── 투표/퀴즈 참여                                              │
│  ├── 손들기 횟수                                                 │
│  └── 분반 토론 기여도                                            │
│                                                                  │
│  처리 파이프라인:                                                 │
│  이벤트 ──► Redis Pub/Sub ──► 집계 Worker ──► WebSocket Push     │
│                                    │                             │
│                                    ▼                             │
│                              PostgreSQL (영구 저장)              │
│                                                                  │
│  [조기 경보 시스템]                                               │
│  위험 지표:                                                      │
│  ├── 연속 결석 ≥ 2회                                             │
│  ├── 참여도 점수 < 평균의 50%                                    │
│  ├── 퀴즈 점수 급락 (20% 이상 하락)                              │
│  └── 30분 이상 무참여                                            │
│                                                                  │
│  [네트워크 분석]                                                  │
│  ├── 학생 간 상호작용 그래프                                     │
│  ├── 중심성 계산 (Degree, Betweenness)                          │
│  ├── 커뮤니티 탐지 (Louvain)                                     │
│  └── D3.js Force Graph 시각화                                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 4.3 서비스 통신

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Communication Patterns                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  [동기 통신 - REST API]                                                      │
│  Client ────► API Gateway ────► Service ────► Database                       │
│                                                                              │
│  [비동기 통신 - Message Queue]                                               │
│  Service A ────► Redis/Spring Async ────► Service B                          │
│                                                                              │
│  예시:                                                                       │
│  • 과제 제출 → Assessment Service (채점) → Notification Service (결과 알림)  │
│  • 세션 종료 → Media Service (녹화 인코딩) → Storage (S3 업로드)             │
│                                                                              │
│  [이벤트 기반 - Pub/Sub]                                                     │
│  Publisher ────► Redis Pub/Sub ────► Multiple Subscribers                    │
│                                                                              │
│  예시:                                                                       │
│  • 투표 응답 → Analytics (실시간 집계) + Learning (결과 업데이트)            │
│  • 손들기 → Media (UI 업데이트) + Analytics (참여도 기록)                    │
│                                                                              │
│  [실시간 통신 - WebSocket]                                                   │
│  Client ◄────────► Socket.io Server ◄────────► Redis Pub/Sub                │
│                                                                              │
│  예시:                                                                       │
│  • 채팅 메시지, 투표 실시간 업데이트, 화이트보드 동기화                       │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. 데이터 아키텍처

### 5.1 데이터베이스 선택 기준

| 데이터 유형 | 저장소 | 선택 이유 |
|------------|--------|----------|
| 구조화 데이터 (사용자, 코스, 성적) | PostgreSQL | ACID, 관계 무결성, 복잡한 쿼리 |
| 세션/캐시 데이터 | Redis | 초저지연, TTL 지원, Pub/Sub |
| 검색 데이터 (콘텐츠, 문제 은행) | Elasticsearch | 전문 검색, 필터링 |
| 파일 (녹화, 과제) | S3/MinIO | 대용량, 비용 효율, CDN 연동 |
| 시계열 데이터 (참여도 로그) | TimescaleDB/InfluxDB | 시계열 최적화 (선택) |

### 5.2 핵심 엔티티 관계도 (ERD)

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              Core Entities                                    │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  ┌─────────────┐       ┌─────────────┐       ┌─────────────┐                │
│  │    User     │       │   Course    │       │   Session   │                │
│  ├─────────────┤       ├─────────────┤       ├─────────────┤                │
│  │ id (PK)     │       │ id (PK)     │       │ id (PK)     │                │
│  │ email       │   1:N │ title       │   1:N │ course_id   │                │
│  │ password    │◄──────│ code        │◄──────│ title       │                │
│  │ role        │       │ semester    │       │ scheduled_at│                │
│  │ created_at  │       │ professor_id│       │ duration    │                │
│  └──────┬──────┘       └──────┬──────┘       │ status      │                │
│         │                     │              └──────┬──────┘                │
│         │                     │                     │                        │
│         │ 1:N                 │ M:N                 │ 1:N                    │
│         ▼                     ▼                     ▼                        │
│  ┌─────────────┐       ┌─────────────┐       ┌─────────────┐                │
│  │ Enrollment  │       │ Content     │       │  Recording  │                │
│  ├─────────────┤       ├─────────────┤       ├─────────────┤                │
│  │ user_id     │       │ id (PK)     │       │ id (PK)     │                │
│  │ course_id   │       │ course_id   │       │ session_id  │                │
│  │ role (학생/ │       │ type        │       │ url         │                │
│  │   TA)       │       │ file_url    │       │ duration    │                │
│  │ joined_at   │       │ visibility  │       │ captions    │                │
│  └─────────────┘       └─────────────┘       └─────────────┘                │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────────┐
│                            Assessment Entities                                │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  ┌─────────────┐       ┌─────────────┐       ┌─────────────┐                │
│  │ Assignment  │       │  Question   │       │    Quiz     │                │
│  ├─────────────┤       ├─────────────┤       ├─────────────┤                │
│  │ id (PK)     │       │ id (PK)     │       │ id (PK)     │                │
│  │ course_id   │       │ course_id   │       │ session_id  │                │
│  │ title       │       │ type        │       │ time_limit  │                │
│  │ due_date    │       │ content     │       │ questions[] │                │
│  │ max_score   │       │ answer      │       │ status      │                │
│  └──────┬──────┘       │ difficulty  │       └──────┬──────┘                │
│         │              │ tags[]      │              │                        │
│         │ 1:N          └─────────────┘              │ 1:N                    │
│         ▼                                           ▼                        │
│  ┌─────────────┐                            ┌─────────────┐                 │
│  │ Submission  │                            │ QuizAttempt │                 │
│  ├─────────────┤                            ├─────────────┤                 │
│  │ id (PK)     │                            │ id (PK)     │                 │
│  │ assignment_id│                           │ quiz_id     │                 │
│  │ user_id     │                            │ user_id     │                 │
│  │ file_url    │                            │ answers[]   │                 │
│  │ score       │                            │ score       │                 │
│  │ feedback    │                            │ completed_at│                 │
│  │ graded_by   │                            └─────────────┘                 │
│  └─────────────┘                                                             │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────────┐
│                           Analytics Entities                                  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  ┌─────────────┐       ┌─────────────┐       ┌─────────────┐                │
│  │Participation│       │    Grade    │       │    Alert    │                │
│  ├─────────────┤       ├─────────────┤       ├─────────────┤                │
│  │ id (PK)     │       │ id (PK)     │       │ id (PK)     │                │
│  │ user_id     │       │ user_id     │       │ user_id     │                │
│  │ session_id  │       │ course_id   │       │ course_id   │                │
│  │ talk_time   │       │ participation│      │ type        │                │
│  │ chat_count  │       │ quiz_avg    │       │ severity    │                │
│  │ poll_count  │       │ assignment_avg│     │ message     │                │
│  │ hand_raises │       │ final_grade │       │ created_at  │                │
│  │ created_at  │       │ updated_at  │       │ resolved_at │                │
│  └─────────────┘       └─────────────┘       └─────────────┘                │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 5.3 캐싱 전략

```yaml
Redis Cache Patterns:

# 1. Session Cache (TTL: 24h)
session:{session_id}:
  participants: [user_ids]
  status: "active" | "ended"
  started_at: timestamp

# 2. User Session (TTL: 1h)
user:{user_id}:token: jwt_token
user:{user_id}:permissions: [permissions]

# 3. Real-time Analytics (TTL: Session duration)
analytics:{session_id}:participation:
  {user_id}: { talk_time, chat_count, ... }

# 4. Rate Limiting (TTL: 1m)
ratelimit:{user_id}:{endpoint}: count

# 5. Poll Results (TTL: Until poll ends)
poll:{poll_id}:votes: { option_a: count, option_b: count }
poll:{poll_id}:voted: [user_ids]
```

---

## 6. 인프라 아키텍처

### 6.1 Kubernetes 배포 구조

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Kubernetes Cluster                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Ingress Controller                            │   │
│  │                     (Nginx / Kong Gateway)                           │   │
│  └───────────────────────────────┬─────────────────────────────────────┘   │
│                                  │                                          │
│  ┌───────────────────────────────┴─────────────────────────────────────┐   │
│  │                         Services Namespace                           │   │
│  │                                                                       │   │
│  │  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐            │   │
│  │  │  nextjs-app   │  │  auth-svc     │  │  course-svc   │            │   │
│  │  │  Replicas: 3  │  │  Replicas: 3  │  │  Replicas: 3  │            │   │
│  │  │  CPU: 500m    │  │  CPU: 500m    │  │  CPU: 500m    │            │   │
│  │  │  Mem: 512Mi   │  │  Mem: 512Mi   │  │  Mem: 512Mi   │            │   │
│  │  └───────────────┘  └───────────────┘  └───────────────┘            │   │
│  │                                                                       │   │
│  │  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐            │   │
│  │  │  learning-svc │  │ assessment-svc│  │  analytics-svc│            │   │
│  │  │  Replicas: 3  │  │  Replicas: 3  │  │  Replicas: 2  │            │   │
│  │  │  CPU: 500m    │  │  CPU: 1000m   │  │  CPU: 500m    │            │   │
│  │  │  Mem: 512Mi   │  │  Mem: 1Gi     │  │  Mem: 512Mi   │            │   │
│  │  └───────────────┘  └───────────────┘  └───────────────┘            │   │
│  │                                                                       │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌───────────────────────────────────────────────────────────────────────┐   │
│  │                          Media Namespace                              │   │
│  │                                                                       │   │
│  │  ┌───────────────────────────────────────────────────────────────┐   │   │
│  │  │                     mediasoup / Jitsi                         │   │   │
│  │  │                                                               │   │   │
│  │  │   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐     │   │   │
│  │  │   │ Router1 │   │ Router2 │   │ Router3 │   │ RouterN │     │   │   │
│  │  │   │ (Pod)   │   │ (Pod)   │   │ (Pod)   │   │ (Pod)   │     │   │   │
│  │  │   └─────────┘   └─────────┘   └─────────┘   └─────────┘     │   │   │
│  │  │                                                               │   │   │
│  │  │   HPA: min 5, max 50 (세션 수에 따라 자동 확장)               │   │   │
│  │  │   Node: Dedicated (Network-optimized instances)               │   │   │
│  │  └───────────────────────────────────────────────────────────────┘   │   │
│  │                                                                       │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌───────────────────────────────────────────────────────────────────────┐   │
│  │                          Data Namespace                               │   │
│  │                                                                       │   │
│  │  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐            │   │
│  │  │   PostgreSQL  │  │     Redis     │  │ Elasticsearch │            │   │
│  │  │   (Primary)   │  │   (Cluster)   │  │   (Cluster)   │            │   │
│  │  │               │  │               │  │               │            │   │
│  │  │   + Replica   │  │  Sentinel x3  │  │  Data x3      │            │   │
│  │  │   PVC: 500Gi  │  │  PVC: 50Gi    │  │  Master x1    │            │   │
│  │  └───────────────┘  └───────────────┘  └───────────────┘            │   │
│  │                                                                       │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌───────────────────────────────────────────────────────────────────────┐   │
│  │                         Workers Namespace                             │   │
│  │                                                                       │   │
│  │  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐            │   │
│  │  │ Spring Worker │  │ Spring Worker │  │ Spring Sched  │            │   │
│  │  │ (Assessment)  │  │ (Notification)│  │ (Scheduler)   │            │   │
│  │  │  Replicas: 5  │  │  Replicas: 3  │  │  Replicas: 1  │            │   │
│  │  └───────────────┘  └───────────────┘  └───────────────┘            │   │
│  │                                                                       │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 클라우드 아키텍처 (AWS 기준)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              AWS Architecture                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         Edge / CDN Layer                             │   │
│  │                                                                       │   │
│  │   CloudFront ────► S3 (Static Assets: React Build, Media Files)      │   │
│  │   Route 53 ────► ALB (Application Load Balancer)                     │   │
│  │   WAF (Web Application Firewall)                                      │   │
│  │                                                                       │   │
│  └───────────────────────────────┬─────────────────────────────────────┘   │
│                                  │                                          │
│  ┌───────────────────────────────┴─────────────────────────────────────┐   │
│  │                         VPC (10.0.0.0/16)                            │   │
│  │                                                                       │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │              Public Subnets (10.0.1.0/24, 10.0.2.0/24)       │    │   │
│  │  │                                                               │    │   │
│  │  │   NAT Gateway    ALB    Bastion Host                         │    │   │
│  │  │                                                               │    │   │
│  │  └───────────────────────────────────────────────────────────────┘    │   │
│  │                                                                       │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │             Private Subnets (10.0.10.0/24, 10.0.20.0/24)     │    │   │
│  │  │                                                               │    │   │
│  │  │   EKS Cluster (Kubernetes)                                    │    │   │
│  │  │   ├── Node Group 1: General (m5.xlarge x 3)                  │    │   │
│  │  │   ├── Node Group 2: Media (c5n.2xlarge x 5-50)               │    │   │
│  │  │   └── Node Group 3: Workers (r5.large x 3)                   │    │   │
│  │  │                                                               │    │   │
│  │  └───────────────────────────────────────────────────────────────┘    │   │
│  │                                                                       │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │               Data Subnets (10.0.100.0/24)                   │    │   │
│  │  │                                                               │    │   │
│  │  │   RDS PostgreSQL (Multi-AZ)                                  │    │   │
│  │  │   ElastiCache Redis (Cluster Mode)                           │    │   │
│  │  │   OpenSearch (Elasticsearch)                                  │    │   │
│  │  │                                                               │    │   │
│  │  └───────────────────────────────────────────────────────────────┘    │   │
│  │                                                                       │   │
│  └───────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  External Services:                                                          │
│  ├── S3 (Object Storage)                                                    │
│  ├── SES (Email)                                                            │
│  ├── Secrets Manager (Credentials)                                          │
│  ├── CloudWatch (Monitoring)                                                │
│  └── ECR (Container Registry)                                               │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.3 CI/CD 파이프라인

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            CI/CD Pipeline                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  [개발자] ──► Git Push ──► GitHub / GitLab                                  │
│                                │                                             │
│                                ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         CI (GitHub Actions)                          │   │
│  │                                                                       │   │
│  │   1. Lint & Type Check ────► ESLint, Pylint, mypy                    │   │
│  │   2. Unit Tests ────────────► pytest, Jest                           │   │
│  │   3. Integration Tests ─────► pytest + TestContainers                │   │
│  │   4. Security Scan ─────────► Snyk, Trivy                            │   │
│  │   5. Build Docker Image ────► docker build                           │   │
│  │   6. Push to Registry ──────► ECR / GHCR                             │   │
│  │                                                                       │   │
│  └───────────────────────────────┬─────────────────────────────────────┘   │
│                                  │                                          │
│                                  ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         CD (ArgoCD / Flux)                           │   │
│  │                                                                       │   │
│  │   Development ────► Auto Deploy (main branch merge)                  │   │
│  │   Staging ─────────► Auto Deploy (tag: v*.*.* -rc)                   │   │
│  │   Production ──────► Manual Approval (tag: v*.*.*)                   │   │
│  │                                                                       │   │
│  │   Deployment Strategy: Rolling Update / Blue-Green                   │   │
│  │                                                                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Environments:                                                               │
│  ├── dev.eduforum.io      (Development)                                     │
│  ├── staging.eduforum.io  (Staging)                                         │
│  └── eduforum.io          (Production)                                      │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 7. 보안 아키텍처

### 7.1 인증 및 인가 흐름

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Authentication Flow                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  [일반 로그인]                                                               │
│                                                                              │
│  Client ──► POST /api/auth/login (email, password)                          │
│         │                                                                    │
│         ▼                                                                    │
│  Auth Service ──► Password Verify (bcrypt) ──► 2FA Required?                │
│                                                      │                       │
│                                            Yes ◄─────┴─────► No             │
│                                             │                 │              │
│                                             ▼                 │              │
│                                   Return { requires_2fa }     │              │
│                                             │                 │              │
│                                             ▼                 │              │
│                              POST /api/auth/2fa/verify        │              │
│                                             │                 │              │
│                                             └────────┬────────┘              │
│                                                      │                       │
│                                                      ▼                       │
│                                   Issue JWT { access_token, refresh_token } │
│                                                                              │
│  [OAuth 로그인]                                                              │
│                                                                              │
│  Client ──► GET /api/auth/oauth/google ──► Redirect to Google              │
│         │                                                                    │
│         ▼                                                                    │
│  Google Auth ──► Callback /api/auth/oauth/google/callback                   │
│              │                                                               │
│              ▼                                                               │
│  Auth Service ──► Create/Link User ──► Issue JWT                            │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                          Authorization (RBAC)                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Roles:                                                                      │
│  ├── admin       : 시스템 전체 관리                                          │
│  ├── professor   : 코스 생성/관리, 세션 진행, 성적 관리                       │
│  ├── ta          : 코스 관리 보조, 채점 보조                                  │
│  └── student     : 코스 수강, 세션 참여, 과제 제출                            │
│                                                                              │
│  Permission Matrix:                                                          │
│  ┌─────────────────┬───────┬───────────┬─────┬─────────┐                   │
│  │ Resource        │ Admin │ Professor │ TA  │ Student │                   │
│  ├─────────────────┼───────┼───────────┼─────┼─────────┤                   │
│  │ User Management │  RWD  │     -     │  -  │    -    │                   │
│  │ Course Create   │  RWD  │    RW     │  -  │    -    │                   │
│  │ Course View     │  RWD  │    RW     │  R  │    R    │                   │
│  │ Session Start   │  RWD  │    RW     │  R  │    -    │                   │
│  │ Session Join    │  RWD  │    RW     │ RW  │    R    │                   │
│  │ Grade View      │  RWD  │    RW     │  R  │   Own   │                   │
│  │ Grade Edit      │  RWD  │    RW     │  W  │    -    │                   │
│  │ Assignment Sub  │   -   │     -     │  -  │   RW    │                   │
│  └─────────────────┴───────┴───────────┴─────┴─────────┘                   │
│                                                                              │
│  R=Read, W=Write, D=Delete, Own=자신의 데이터만                              │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 보안 계층

```yaml
Network Security:
  - VPC Isolation: Private Subnets for DB/Workers
  - Security Groups: Least Privilege Access
  - WAF Rules: OWASP Top 10 Protection
  - DDoS Protection: AWS Shield / CloudFlare

Application Security:
  - Input Validation: All API inputs validated
  - SQL Injection: ORM with parameterized queries
  - XSS Prevention: Content Security Policy, Sanitization
  - CSRF Protection: SameSite Cookies, CSRF Tokens
  - Rate Limiting: Per-user, Per-endpoint limits

Data Security:
  - Encryption at Rest: AES-256 (RDS, S3)
  - Encryption in Transit: TLS 1.3
  - PII Handling: Field-level encryption for sensitive data
  - Data Retention: GDPR-compliant retention policies

Authentication Security:
  - Password Hashing: bcrypt (cost factor 12)
  - JWT Security: RS256 signing, short expiry (15m access, 7d refresh)
  - 2FA: TOTP with backup codes
  - Session Management: Redis-backed, device tracking

Audit & Compliance:
  - Audit Logging: All sensitive operations logged
  - Access Logs: CloudWatch / ELK Stack
  - Compliance: FERPA (교육 데이터), GDPR (개인정보)
```

---

## 8. 확장성 및 성능

### 8.1 수평 확장 전략

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Scaling Strategy                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  [API Services - Stateless]                                                  │
│                                                                              │
│  HPA (Horizontal Pod Autoscaler):                                           │
│  ├── Target: CPU 70%, Memory 80%                                            │
│  ├── Min Replicas: 3                                                        │
│  ├── Max Replicas: 20                                                       │
│  └── Scale-up: 30초 이내                                                    │
│                                                                              │
│  [Media Servers - Stateful (Session Affinity)]                              │
│                                                                              │
│  Custom Autoscaler:                                                          │
│  ├── Metric: Active Sessions per Node                                       │
│  ├── Target: 10 sessions/node (50명 기준)                                   │
│  ├── Min Nodes: 5                                                           │
│  ├── Max Nodes: 100                                                         │
│  └── Pre-warming: 피크 시간 전 사전 확장                                    │
│                                                                              │
│  [Database]                                                                  │
│                                                                              │
│  PostgreSQL:                                                                 │
│  ├── Read Replicas: 2개 (읽기 부하 분산)                                    │
│  ├── Connection Pooling: PgBouncer                                          │
│  └── Vertical Scaling: 필요 시 인스턴스 업그레이드                           │
│                                                                              │
│  Redis:                                                                      │
│  ├── Cluster Mode: 6노드 (3 master, 3 replica)                              │
│  └── Sharding: Hash slot 기반 자동 분산                                     │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 8.2 성능 최적화

```yaml
Frontend Optimization (Next.js):
  - Code Splitting: Automatic route-based splitting (App Router)
  - Bundle Size: < 200KB (gzipped, per route)
  - CDN Caching: Static assets (max-age: 1 year)
  - Image Optimization: next/image (WebP, lazy loading, blur placeholder)
  - Font Optimization: next/font (FOUT prevention)
  - Streaming SSR: React Suspense boundaries
  - Server Components: Zero client JS for static content
  - Partial Prerendering: Static shell + dynamic content
  - WebSocket: Reconnection with exponential backoff

Backend Optimization:
  - Query Optimization: N+1 방지, select_related/prefetch_related
  - Caching Strategy:
      - L1: In-memory (local)
      - L2: Redis (distributed)
      - TTL: 사용자 세션 1h, 코스 데이터 5m
  - Connection Pooling: DB 20, Redis 50
  - Async Processing: I/O bound 작업 async 처리

Media Optimization:
  - Adaptive Bitrate: 네트워크 상태에 따른 품질 조정
  - Simulcast: 동일 스트림 다중 해상도 전송
  - SVC (Scalable Video Coding): 레이어 기반 스케일링
  - Bandwidth Estimation: REMB/TWCC 기반

Database Optimization:
  - Indexing: 자주 쿼리되는 컬럼 인덱스
  - Partitioning: 시계열 데이터 (participation_logs)
  - Archiving: 1년 이상 된 데이터 Cold Storage 이동
  - Query Monitoring: slow query 로깅 및 알림
```

### 8.3 성능 목표 및 벤치마크

| 지표 | 목표 | 측정 방법 |
|------|------|----------|
| API 응답 시간 (P95) | < 200ms | Prometheus/Grafana |
| 페이지 로드 시간 | < 2초 (LCP) | Lighthouse CI |
| WebSocket 연결 시간 | < 500ms | Custom metrics |
| 비디오 지연시간 | < 300ms (E2E) | WebRTC stats |
| 동시 접속자 | 50,000명 | Load testing (k6) |
| 동시 세션 | 1,000개 | Load testing |
| DB 쿼리 시간 (P95) | < 50ms | pg_stat_statements |

---

## 9. 통합 및 외부 시스템

### 9.1 외부 시스템 통합

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        External Integrations                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  [OAuth Providers]                                                           │
│  ├── Google OAuth 2.0 (학교 Google Workspace)                               │
│  ├── Microsoft Azure AD (Office 365)                                        │
│  └── 학교 자체 SSO (SAML 2.0)                                               │
│                                                                              │
│  [LMS Integration - LTI 1.3]                                                │
│  ├── Canvas                                                                 │
│  ├── Blackboard                                                             │
│  ├── Moodle                                                                 │
│  └── 성적 동기화, 코스 연동, Deep Linking                                   │
│                                                                              │
│  [Communication]                                                             │
│  ├── Email: AWS SES / SendGrid                                              │
│  ├── Push Notification: Firebase Cloud Messaging                            │
│  └── SMS: Twilio (선택)                                                     │
│                                                                              │
│  [AI/ML Services]                                                            │
│  ├── OpenAI API: GPT-4 (피드백 생성)                                        │
│  ├── OpenAI Whisper: 자막 생성                                              │
│  └── HuggingFace: BERT Embeddings (자체 호스팅)                             │
│                                                                              │
│  [Code Execution]                                                            │
│  └── Judge0: 샌드박스 코드 실행 (Docker 기반)                               │
│                                                                              │
│  [Plagiarism Detection]                                                      │
│  └── MOSS (Measure of Software Similarity): Stanford                        │
│                                                                              │
│  [Calendar]                                                                  │
│  ├── Google Calendar API                                                    │
│  └── iCal Export                                                            │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 9.2 API 설계 원칙

```yaml
API Design:
  Standard: OpenAPI 3.0 (Swagger)
  Versioning: URI-based (/api/v1/*)
  Authentication: Bearer JWT
  Rate Limiting: 1000 req/min (authenticated)

Response Format:
  Success:
    status: 200-299
    data: { ... }
    meta: { pagination, ... }

  Error:
    status: 400-599
    error:
      code: "VALIDATION_ERROR"
      message: "Human readable message"
      details: [{ field, message }]

Pagination:
  Type: Cursor-based (for real-time data)
  Params: ?cursor=xxx&limit=20

Filtering:
  Type: Query params
  Example: ?status=active&sort=-created_at

Webhooks:
  Events: session.started, quiz.completed, grade.updated
  Delivery: POST with HMAC signature
  Retry: 3회 (exponential backoff)
```

---

## 10. 모니터링 및 운영

### 10.1 관측성 (Observability)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Observability Stack                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  [Metrics - Prometheus + Grafana]                                           │
│  ├── System Metrics: CPU, Memory, Disk, Network                             │
│  ├── Application Metrics: Request rate, Error rate, Latency                 │
│  ├── Business Metrics: Active sessions, Participants, Quiz attempts         │
│  └── Custom Dashboards: Service health, User activity, Media quality        │
│                                                                              │
│  [Logging - ELK Stack / Loki]                                               │
│  ├── Structured Logging: JSON format                                        │
│  ├── Log Levels: DEBUG, INFO, WARNING, ERROR, CRITICAL                      │
│  ├── Correlation ID: Request tracing across services                        │
│  └── Retention: 30 days hot, 1 year cold (S3)                               │
│                                                                              │
│  [Tracing - Jaeger / OpenTelemetry]                                         │
│  ├── Distributed Tracing: Cross-service request flow                        │
│  ├── Span Analysis: Bottleneck identification                               │
│  └── Sampling: 10% (production), 100% (staging)                             │
│                                                                              │
│  [Alerting - PagerDuty / Opsgenie]                                          │
│  ├── Critical: Service down, Error rate > 5%                                │
│  ├── Warning: Latency P95 > 500ms, CPU > 80%                                │
│  ├── Info: Deployment completed, Scheduled maintenance                       │
│  └── Escalation: 5분 무응답 시 다음 레벨 호출                                │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 10.2 SLA 및 SLO

| 서비스 | SLO | 측정 방법 |
|--------|-----|----------|
| 전체 시스템 가용성 | 99.9% | Uptime monitoring |
| API 응답 시간 | P95 < 200ms | Prometheus |
| 비디오 품질 | MOS > 4.0 | WebRTC stats |
| 에러율 | < 0.1% | Error tracking |
| 데이터 내구성 | 99.999999999% (11 nines) | S3 SLA |

### 10.3 재해 복구 (DR)

```yaml
Backup Strategy:
  Database:
    - Full backup: Daily (2:00 AM KST)
    - Incremental: Every 6 hours
    - Point-in-time Recovery: 7 days retention
    - Cross-region Replication: ap-northeast-2 → ap-northeast-1

  Object Storage:
    - Versioning: Enabled
    - Cross-region Replication: Enabled
    - Lifecycle: 90 days → Glacier

  Configuration:
    - Infrastructure as Code: Terraform
    - Secrets: AWS Secrets Manager (cross-region)

Recovery Objectives:
  - RTO (Recovery Time Objective): 4 hours
  - RPO (Recovery Point Objective): 1 hour

Disaster Scenarios:
  1. Single AZ failure → Auto failover (RDS Multi-AZ)
  2. Region failure → Manual failover to DR region
  3. Data corruption → Point-in-time recovery
  4. Security breach → Incident response playbook
```

### 10.4 운영 절차

```yaml
Deployment:
  - Blue-Green deployment for zero-downtime
  - Canary releases for risky changes (10% → 50% → 100%)
  - Rollback: < 5 minutes (previous version retained)

Maintenance Windows:
  - Scheduled: Sunday 2:00-4:00 AM KST
  - Notification: 72 hours in advance
  - Emergency: Immediate notification via all channels

Incident Management:
  - Severity Levels:
      P1: Complete outage (response: 15 min)
      P2: Major feature degraded (response: 30 min)
      P3: Minor issue (response: 4 hours)
      P4: Low priority (response: 24 hours)
  - Postmortem: Required for P1/P2 within 48 hours

On-Call Rotation:
  - Primary: 24/7 coverage
  - Secondary: Backup escalation
  - Rotation: Weekly
```

---

## 부록

### A. 용어 정의

| 용어 | 정의 |
|------|------|
| SFU | Selective Forwarding Unit - 미디어 서버가 스트림을 선택적으로 전달 |
| CRDT | Conflict-free Replicated Data Type - 분산 환경 동기화 자료구조 |
| TalkTime | 발언 시간 기반 참여도 측정 시스템 |
| LTI | Learning Tools Interoperability - LMS 통합 표준 |
| RBAC | Role-Based Access Control - 역할 기반 접근 제어 |

### B. 참조 문서

- [PRD (03-product-requirements.md)](./03-product-requirements.md)
- [기능 세분화 (04-feature-breakdown.md)](./04-feature-breakdown.md)
- [기술 아키텍처 (02-technical-architecture.md)](./02-technical-architecture.md)
- [와이어프레임 (docs/wireframes/)](./wireframes/)

### C. 변경 이력

| 버전 | 날짜 | 작성자 | 변경 내용 |
|------|------|--------|----------|
| 1.0 | 2025-01-28 | System | 초기 문서 작성 |
| 1.1 | 2025-01-28 | System | 프론트엔드 프레임워크 React → Next.js 변경 |

---

**문서 끝**
