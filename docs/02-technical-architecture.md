# 02. 미네르바 포럼 - 기술 스택 및 아키텍처 분석

## 문서 정보
- **작성일**: 2025-11-27
- **목적**: 미네르바 포럼 유사 시스템 개발을 위한 기술 아키텍처 상세 분석
- **대상**: 대학교 및 교육시설 제공용 플랫폼 개발 기술 기획

---

## 목차
1. [미네르바 포럼의 알려진 기술 스택](#1-미네르바-포럼의-알려진-기술-스택)
2. [시스템 아키텍처 설계 원칙](#2-시스템-아키텍처-설계-원칙)
3. [핵심 기술 컴포넌트](#3-핵심-기술-컴포넌트)
4. [실시간 통신 아키텍처](#4-실시간-통신-아키텍처)
5. [학습 분석 및 참여도 추적](#5-학습-분석-및-참여도-추적)
6. [AI/ML 기반 자동 평가 시스템](#6-aiml-기반-자동-평가-시스템)
7. [확장성 및 배포 전략](#7-확장성-및-배포-전략)
8. [데이터 모델 및 저장소](#8-데이터-모델-및-저장소)
9. [보안 및 인증](#9-보안-및-인증)
10. [유사 플랫폼 기술 비교](#10-유사-플랫폼-기술-비교)
11. [권장 기술 스택 (구현 제안)](#11-권장-기술-스택-구현-제안)

---

## 1. 미네르바 포럼의 알려진 기술 스택

### 1.1 공개된 정보

미네르바 대학교의 포럼 플랫폼은 독점 기술로, 상세한 기술 스택이 공개되어 있지 않습니다. 하지만 일부 정보가 확인되었습니다:

#### 초기 개발 (2012-2014)
- **백엔드 언어**: Clojure
  - 실시간 병렬 데이터 처리에 적합
  - 함수형 프로그래밍 패러다임
  - JVM 기반으로 Java 생태계 활용

#### 현재 스택 (2014 이후)
- **백엔드 프레임워크**: Django (Python)
  - Clojure에서 전환 (생태계 및 인기도 고려)
  - Django REST Framework 사용 (API 개발)
  - 빠른 개발 및 유지보수 용이성

#### 플랫폼 특징
- **독립적 커리큘럼 관리 시스템**: 학생 대면 시스템과 분리
  - 코스 설계 도구와 교수-학습 도구의 분리
- **LTI 통합 미지원**: 현재는 별도의 LMS와 병행 사용
  - Forum과 기존 학교 LMS 동시 운영

### 1.2 플랫폼 개발 역사

```
2012: 창립 - 소규모 팀 (3명)으로 시작
      ↓
      Clojure 기반 초기 프로토타입 개발
      Active Learning 철학과 긴밀히 연결된 세미나 환경
      ↓
2014: Django/Python으로 전환
      ↓
      생태계 확장성 및 인력 확보 용이성 고려
      ↓
2021: 독립 대학 인가 (WASC)
      ↓
2022-2023: WURI 혁신 대학 평가 1위
```

---

## 2. 시스템 아키텍처 설계 원칙

### 2.1 교육 철학 우선 설계 (Pedagogy-First Design)

미네르바 포럼은 **"기술이 교육학을 따른다"**는 원칙을 기반으로 설계되었습니다:

1. **교육 목표 정의** → 필요한 상호작용 유형 파악 → 기술 구현
2. 단순한 화상 회의가 아닌 **액티브 러닝 활동 지원**에 최적화
3. 모든 기능은 **학습 과학 연구**에 근거

### 2.2 마이크로 모듈 아키텍처

수업은 **수 분 단위의 마이크로 모듈**로 구성:

```
90분 수업 = 15-20개의 마이크로 모듈
│
├─ 퀴즈 모듈 (3-5분)
├─ 토론 모듈 (8-10분)
├─ 투표 모듈 (2-3분)
├─ 분반 활동 (10-15분)
├─ 발표 및 피드백 (5-8분)
└─ ...
```

각 모듈은:
- **특정 학습 목표**(Learning Outcome)와 연결
- **해시태그 기반 역량**(Competency)과 매핑
- **참여도 및 이해도 실시간 측정**

### 2.3 분산 4-Tier 아키텍처 (참고: Ansys Minerva)

교육 플랫폼의 일반적인 분산 아키텍처 (Ansys Minerva 사례):

```
┌─────────────────┐
│  Client Tier    │  - 웹 브라우저 (SPA)
│                 │  - 네이티브 모바일 앱 (선택)
└────────┬────────┘
         │
┌────────▼────────┐
│   Web Tier      │  - IIS / Nginx / Apache
│                 │  - WebSocket 게이트웨이
│                 │  - API Gateway
└────────┬────────┘
         │
┌────────▼────────┐
│ Enterprise Tier │  - 애플리케이션 서버
│                 │  - 비즈니스 로직
│                 │  - 실시간 처리 엔진
└────────┬────────┘
         │
┌────────▼────────┐
│ Resource Tier   │  - 데이터베이스 (PostgreSQL/MySQL)
│                 │  - 파일 스토리지 (S3/Blob)
│                 │  - 캐싱 (Redis)
└─────────────────┘
```

**가용성 구역 (Availability Zone)** 내에서 단일 가상 네트워크 구성

---

## 3. 핵심 기술 컴포넌트

미네르바 포럼과 같은 액티브 러닝 플랫폼을 구현하기 위한 핵심 컴포넌트:

### 3.1 실시간 비디오 컨퍼런싱

#### WebRTC (Web Real-Time Communication)
- **표준 기술**: 브라우저 기반 P2P 통신
- **장점**:
  - 오픈소스, 무료
  - 낮은 지연시간 (Low Latency)
  - 브라우저 네이티브 지원
- **단점**:
  - NAT 통과 문제 (STUN/TURN 서버 필요)
  - 대규모 회의 시 복잡성 증가

#### 아키텍처 유형

##### (1) Mesh 아키텍처
```
     A ←→ B
     ↕   ↗↖  ↕
     D ←→ C
```
- **방식**: 모든 참가자가 서로 직접 연결
- **장점**: 단순, 낮은 서버 비용
- **단점**: 대규모 불가 (4-5명 한계)
- **적합성**: 미네르바 포럼에 부적합 (20명 세미나)

##### (2) SFU (Selective Forwarding Unit) - 권장
```
     A ──┐
          ↓
     B → SFU → 모든 참가자에게 전달
          ↑
     C ──┘
```
- **방식**: 중앙 서버가 스트림 전달만 담당 (인코딩/디코딩 없음)
- **장점**:
  - 확장성 좋음 (수백 명 가능)
  - 낮은 서버 부하
  - 개별 스트림 품질 조정 가능
- **단점**: 여전히 클라이언트 부담 존재
- **적합성**: **미네르바 포럼에 최적** (20명 세미나)
- **구현 예시**:
  - **Jitsi Meet** (오픈소스)
  - **Mediasoup** (Node.js)
  - **Kurento Media Server**

##### (3) MCU (Multipoint Control Unit)
```
     A ──┐
          ↓
     B → MCU (믹싱) → 단일 스트림
          ↑
     C ──┘
```
- **방식**: 서버가 모든 스트림을 믹싱하여 단일 스트림 전송
- **장점**: 클라이언트 부담 최소
- **단점**: 높은 서버 비용, 유연성 낮음
- **적합성**: 대규모 웨비나에 적합, 상호작용 중심 교육에는 과도

### 3.2 실시간 데이터 동기화

#### WebSocket
- **양방향 통신**: 서버 ↔ 클라이언트 실시간 데이터 교환
- **사용 사례**:
  - 투표 결과 실시간 업데이트
  - 참여도 대시보드 갱신
  - 채팅 메시지
  - 손들기 / 반응 이모지
  - 분반 상태 변경

#### 기술 스택 옵션
- **Socket.io** (Node.js) - 가장 인기
- **Django Channels** (Python/Django) - Django 환경에 최적
- **SignalR** (.NET)
- **Phoenix Channels** (Elixir)

### 3.3 협업 도구

#### (1) 화이트보드
- **기술**: Canvas API, SVG, WebRTC Data Channel
- **라이브러리**:
  - **Excalidraw** (오픈소스)
  - **Fabric.js** (Canvas 추상화)
  - **Konva.js** (고성능 Canvas)

#### (2) 공동 문서 편집
- **기술**: OT (Operational Transformation) 또는 CRDT (Conflict-free Replicated Data Type)
- **라이브러리**:
  - **Yjs** (CRDT, 권장)
  - **ShareDB** (OT)
  - **Automerge** (CRDT)

#### (3) 화면 공유
- **기술**: WebRTC `getDisplayMedia()` API
- **구현**: 별도의 미디어 스트림으로 처리

### 3.4 분반 (Breakout Rooms)

#### 동적 룸 생성
- **시나리오**:
  1. 교수가 "4개 그룹, 각 5명" 설정
  2. 시스템이 자동으로 학생 배정 (랜덤 or 성적 기반)
  3. 각 그룹은 독립된 WebRTC 세션 생성
  4. 교수는 모든 룸 모니터링 및 이동 가능

#### 기술 구현
```python
# Django Channels 예시
class BreakoutRoomConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        self.room_id = self.scope['url_route']['kwargs']['room_id']
        self.room_group_name = f'breakout_{self.room_id}'

        await self.channel_layer.group_add(
            self.room_group_name,
            self.channel_name
        )
        await self.accept()

    async def receive(self, text_data):
        # WebRTC signaling 처리
        ...
```

### 3.5 투표 및 퀴즈 시스템

#### 실시간 투표
- **흐름**:
  1. 교수가 질문 및 선택지 전송 (WebSocket)
  2. 학생들이 응답 (HTTP POST or WebSocket)
  3. 결과 실시간 집계 및 시각화 (Chart.js, D3.js)
  4. 익명 vs 기명 옵션

#### 퀴즈 자동 채점
- **객관식**: 즉시 자동 채점
- **주관식**: NLP 기반 유사도 분석 (후술)
- **코딩 문제**: 코드 실행 샌드박스 (Judge0, CodeJudge)

---

## 4. 실시간 통신 아키텍처

### 4.1 WebRTC Signaling Server

WebRTC는 P2P 연결을 설정하기 위해 **시그널링 서버**가 필요합니다:

```
┌─────────┐                  ┌─────────┐
│ Client A│                  │ Client B│
└────┬────┘                  └────┬────┘
     │                            │
     │  1. Offer (SDP) ──────────>│
     │  2. ICE Candidates ────────>│
     │<──────────── 3. Answer (SDP)│
     │<──────── 4. ICE Candidates  │
     │                            │
     └──── 5. P2P Connection ─────┘
```

#### 시그널링 프로토콜
- **WebSocket** (권장) - 양방향, 낮은 지연
- **HTTP Long Polling** (대체)
- **Server-Sent Events (SSE)** (단방향)

### 4.2 STUN/TURN 서버

#### STUN (Session Traversal Utilities for NAT)
- **목적**: 클라이언트의 공인 IP 주소 확인
- **무료 서버**: `stun:stun.l.google.com:19302`
- **구축**: coturn (오픈소스)

#### TURN (Traversal Using Relays around NAT)
- **목적**: P2P 실패 시 중계 서버 역할
- **필요성**: 방화벽/기업 네트워크 환경
- **비용**: 대역폭 소모 (클라우드 비용 증가)
- **구축**: coturn (STUN과 동일 소프트웨어)

### 4.3 미디어 서버 (SFU 구현)

#### Jitsi Meet (권장 - 오픈소스)
- **언어**: Java (Jitsi Videobridge)
- **장점**:
  - 완전한 오픈소스
  - 검증된 대규모 배포 사례
  - Docker 컨테이너 지원
  - GDPR 준수
- **단점**: 커스터마이징 난이도 높음

#### Mediasoup (권장 - 유연성)
- **언어**: C++/Node.js
- **장점**:
  - 라이브러리 형태 (완전한 커스터마이징)
  - 고성능
  - 활발한 커뮤니티
- **단점**: 직접 구현 필요 (복잡도 높음)

#### Kurento Media Server
- **언어**: C++
- **장점**: 미디어 처리 파이프라인 (녹화, 필터 등)
- **단점**: 복잡한 아키텍처

### 4.4 확장 가능한 아키텍처

#### 다중 미디어 서버 구성
```
                Load Balancer
                      ↓
        ┌─────────────┼─────────────┐
        ↓             ↓             ↓
    SFU Server 1  SFU Server 2  SFU Server 3
    (Jitsi)       (Jitsi)       (Jitsi)
        ↓             ↓             ↓
    각 서버는 여러 방(Room)을 호스팅
    Room 1, 2, 3  Room 4, 5, 6  Room 7, 8, 9
```

#### 지리적 분산 (Multi-Region)
- **CDN 활용**: 미디어 스트림 가속
- **지역별 서버**: 레이턴시 감소
- **자동 라우팅**: 사용자 위치 기반

---

## 5. 학습 분석 및 참여도 추적

### 5.1 학습 분석(Learning Analytics) 정의

> "Learning Analytics는 학습자와 그들의 맥락에 대한 데이터를 측정, 수집, 분석 및 보고하여 학습과 학습 환경을 이해하고 최적화하는 것"

미네르바 포럼의 핵심 차별점은 **실시간 참여도 추적 및 자동 피드백**입니다.

### 5.2 추적해야 할 핵심 메트릭

#### (1) 참여도 지표 (Engagement Metrics)
- **발언 빈도**: 학생별 발언 횟수
- **발언 시간**: 총 발언 시간 및 평균 길이
- **채팅 참여**: 채팅 메시지 수
- **투표/퀴즈 참여**: 응답률 및 정답률
- **손들기**: 질문/의견 표현 빈도
- **카메라/마이크 상태**: 활성화 시간 비율

#### (2) 이해도 지표 (Comprehension Metrics)
- **퀴즈 정답률**: 사전 학습 확인
- **질문 품질**: NLP 기반 질문 수준 분석
- **토론 기여도**: 논리적 연결성 (후속 발언 분석)

#### (3) 협업 지표 (Collaboration Metrics)
- **분반 활동**: 소그룹 내 기여도
- **동료 평가**: Peer Assessment 점수
- **문서 공동 편집**: 기여 비율

#### (4) 행동 지표 (Behavioral Metrics)
- **출석**: 세션 참여 여부
- **시간 엄수**: 정시 참여율
- **지속시간**: 세션 내 머무른 시간
- **화면 집중**: (선택) Eye-tracking, 화면 이탈 감지

### 5.3 실시간 대시보드 (TalkTime™ 유사)

미네르바 포럼의 **TalkTime™** 기능:
- 교수에게 실시간 참여도 시각화
- 학생별 발언 시간 막대 그래프
- 참여가 낮은 학생 자동 하이라이트
- 교수가 균형 잡힌 토론 유도

#### 기술 구현
```javascript
// React + Chart.js 예시
function ParticipationDashboard({ students }) {
  const data = {
    labels: students.map(s => s.name),
    datasets: [{
      label: 'Speaking Time (seconds)',
      data: students.map(s => s.speakingTime),
      backgroundColor: students.map(s =>
        s.speakingTime < averageTime ? 'red' : 'green'
      )
    }]
  };

  return <Bar data={data} options={options} />;
}
```

### 5.4 데이터 수집 아키텍처

#### 이벤트 기반 수집
```python
# Django 예시
from django.db import models
from django.utils import timezone

class ParticipationEvent(models.Model):
    EVENT_TYPES = [
        ('speak_start', 'Started Speaking'),
        ('speak_end', 'Stopped Speaking'),
        ('chat', 'Chat Message'),
        ('poll_response', 'Poll Response'),
        ('hand_raise', 'Hand Raised'),
    ]

    session_id = models.ForeignKey('Session', on_delete=models.CASCADE)
    user_id = models.ForeignKey('User', on_delete=models.CASCADE)
    event_type = models.CharField(max_length=20, choices=EVENT_TYPES)
    timestamp = models.DateTimeField(default=timezone.now)
    metadata = models.JSONField(null=True)  # 추가 정보 (예: 메시지 내용)

    class Meta:
        indexes = [
            models.Index(fields=['session_id', 'timestamp']),
            models.Index(fields=['user_id', 'event_type']),
        ]
```

#### 실시간 처리 파이프라인
```
이벤트 발생 → WebSocket → Event Queue (Redis/RabbitMQ)
                                    ↓
                             실시간 집계 엔진
                                    ↓
                    ┌───────────────┴───────────────┐
                    ↓                               ↓
            실시간 대시보드 업데이트          데이터베이스 저장
            (WebSocket Push)                 (배치 처리)
```

### 5.5 주요 플랫폼 및 기술

#### Suitable (Full-Stack Student Engagement Platform)
- 경험적 및 co-curricular 학습 측정
- 실시간 학생 참여 시각화
- 데이터 기반 인사이트 생성

#### CYPHER Learning
- 완전 커스터마이징 가능한 학습자 대시보드
- 실시간 진행 메트릭
- 목표 기반 학습 경로
- 시간, 평가, 숙련도, 역량, 컴플라이언스 추적

#### Moodle Analytics
- 커스터마이징 가능한 대시보드 및 리포트
- 실시간 학습자 진행 상황 및 성과 표시
- 특정 코스 리소스 소요 시간 추적
- 토론 포럼 학생 상호작용 모니터링

---

## 6. AI/ML 기반 자동 평가 시스템

### 6.1 감성 분석 (Sentiment Analysis)

#### 목적
- 학생의 정서 상태 파악 (긍정/중립/부정)
- 수업 만족도 자동 분석
- 어려움을 겪는 학생 조기 발견

#### 기술 스택

##### (1) 전통적 NLP 접근
- **NLTK (Natural Language Toolkit)**: Python의 대표적 NLP 라이브러리
  - 토큰화, 품사 태깅, 정규화, 텍스트 정제
- **TextBlob**: 간단한 감성 분석
- **VADER**: 소셜 미디어 텍스트에 특화

##### (2) 머신러닝 접근
- **SVM (Support Vector Machine)**: 텍스트 분류
- **Naive Bayes**: 빠른 분류
- **Random Forest**: 앙상블 학습

##### (3) 딥러닝 접근 (최신 트렌드)
- **BERT (Bidirectional Encoder Representations from Transformers)**
  - Hugging Face Transformers 라이브러리
  - Pre-trained 모델 fine-tuning
- **RoBERTa**: BERT의 개선 버전
- **DistilBERT**: 경량화 버전 (속도 중시)
- **GPT 기반**: OpenAI API 활용 가능

#### 구현 예시 (Python)
```python
from transformers import pipeline

# Pre-trained 감성 분석 모델
classifier = pipeline('sentiment-analysis', model='bert-base-multilingual-cased')

# 학생 피드백 분석
feedback = "이 수업은 정말 도움이 되었어요. 그런데 과제가 너무 많아요."
result = classifier(feedback)
# [{'label': 'POSITIVE', 'score': 0.65}, {'label': 'NEGATIVE', 'score': 0.35}]
```

#### 교육 플랫폼 적용 사례

##### Coursera
- 학생 리뷰 및 피드백 처리
- 코스 수준에서 감성 집계
- 가장 가치 있는 수업 식별
- 과제 난이도 조정 의사결정

### 6.2 자동 에세이 채점 (Automated Essay Scoring)

#### 기술
- **NLP 기반 유사도 분석**
  - 모범 답안과의 의미적 유사도 측정
  - Cosine Similarity (TF-IDF, Word2Vec, BERT Embeddings)
- **문법 및 구조 분석**
  - 문법 오류 검출 (LanguageTool)
  - 논리 구조 평가
- **표절 검사**
  - 기존 자료와의 유사도 (Turnitin 유사)

#### 구현 예시
```python
from sentence_transformers import SentenceTransformer, util

model = SentenceTransformer('all-MiniLM-L6-v2')

# 모범 답안
model_answer = "광합성은 식물이 빛 에너지를 화학 에너지로 전환하는 과정이다."

# 학생 답안
student_answer = "식물은 햇빛을 이용해서 에너지를 만듭니다."

# Embedding 및 유사도 계산
emb1 = model.encode(model_answer, convert_to_tensor=True)
emb2 = model.encode(student_answer, convert_to_tensor=True)

similarity = util.cos_sim(emb1, emb2).item()
score = similarity * 100  # 0-100 점수로 변환

print(f"유사도 점수: {score:.2f}")
```

### 6.3 질문 품질 자동 평가

#### 목적
- 학생의 질문 수준 평가 (Bloom's Taxonomy 수준)
- 단순 사실 질문 vs 비판적 사고 질문 구분

#### 접근
- **키워드 분석**: "왜", "어떻게", "비교하면" 등의 고차원 사고 키워드
- **분류 모델**: 질문을 Bloom 레벨로 분류
  - Remember (기억) → Understanding (이해) → Apply (적용) → Analyze (분석) → Evaluate (평가) → Create (창조)

### 6.4 토론 네트워크 분석 (Discussion Network Analysis)

#### 목적
- 누가 누구와 대화하는지 네트워크 구조 파악
- 고립된 학생 발견
- 토론 리더 식별

#### 기술
- **그래프 이론**: NetworkX (Python)
- **시각화**: D3.js, Gephi
- **메트릭**:
  - Centrality (중심성): 누가 중심 역할?
  - Clustering (군집): 소그룹 형성?
  - Degree (연결도): 몇 명과 대화?

---

## 7. 확장성 및 배포 전략

### 7.1 마이크로서비스 아키텍처

#### 장점
- **독립적 확장**: 부하가 높은 서비스만 스케일 아웃
- **기술 스택 다양성**: 각 서비스에 최적 언어/프레임워크
- **장애 격리**: 한 서비스 장애가 전체에 영향 주지 않음
- **팀 독립성**: 각 팀이 서비스별로 개발/배포

#### 서비스 분할 예시

```
┌─────────────────────────────────────────────┐
│          API Gateway (Kong, Nginx)          │
└──────────────────┬──────────────────────────┘
                   │
      ┌────────────┼────────────┬───────────┐
      ↓            ↓            ↓           ↓
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│  User    │ │  Course  │ │  Video   │ │Analytics │
│ Service  │ │ Service  │ │ Service  │ │ Service  │
└──────────┘ └──────────┘ └──────────┘ └──────────┘
      ↓            ↓            ↓           ↓
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│  User    │ │  Course  │ │  Media   │ │Analytics │
│    DB    │ │    DB    │ │ Storage  │ │    DB    │
└──────────┘ └──────────┘ └──────────┘ └──────────┘
```

각 마이크로서비스:
- **User Service**: 인증, 권한, 프로필
- **Course Service**: 코스, 세션, 커리큘럼 관리
- **Video Service**: 실시간 비디오, WebRTC signaling
- **Analytics Service**: 참여도 추적, 리포트 생성
- **Assessment Service**: 퀴즈, 채점, 피드백
- **Notification Service**: 이메일, 푸시, SMS

### 7.2 컨테이너화 (Docker)

#### 장점
- **일관된 환경**: 개발/테스트/운영 동일
- **빠른 배포**: 이미지 빌드 및 푸시
- **리소스 효율**: VM 대비 경량

#### Dockerfile 예시 (Django 서비스)
```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000

CMD ["gunicorn", "config.wsgi:application", "--bind", "0.0.0.0:8000"]
```

### 7.3 오케스트레이션 (Kubernetes)

#### 주요 기능
- **자동 확장 (HPA)**: CPU/메모리 사용률 기반 Pod 증가
- **자동 복구 (Self-Healing)**: 실패한 컨테이너 재시작
- **서비스 디스커버리**: DNS 기반 서비스 찾기
- **로드 밸런싱**: 트래픽 분산
- **롤링 업데이트**: 무중단 배포

#### Kubernetes 구성 예시
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: video-service
spec:
  replicas: 3  # 초기 Pod 수
  selector:
    matchLabels:
      app: video-service
  template:
    metadata:
      labels:
        app: video-service
    spec:
      containers:
      - name: video-service
        image: myregistry/video-service:v1.0
        ports:
        - containerPort: 8080
        resources:
          requests:
            cpu: 500m
            memory: 512Mi
          limits:
            cpu: 1000m
            memory: 1Gi
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: video-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: video-service
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

### 7.4 클라우드 배포 전략

#### 멀티 클라우드 고려
- **AWS**: 가장 넓은 서비스 범위
  - EKS (Elastic Kubernetes Service)
  - S3 (파일 스토리지)
  - RDS (관리형 데이터베이스)
  - CloudFront (CDN)
- **Azure**: 엔터프라이즈 통합
  - AKS (Azure Kubernetes Service)
  - Azure Blob Storage
  - Azure Database for PostgreSQL
- **GCP**: AI/ML 강점
  - GKE (Google Kubernetes Engine)
  - Cloud Storage
  - BigQuery (분석)

#### 지역별 배포 (Multi-Region)
```
북미 리전 (us-east-1)
│
├─ Video Server Cluster
├─ Database Primary
└─ CDN Edge

유럽 리전 (eu-west-1)
│
├─ Video Server Cluster
├─ Database Replica (Read)
└─ CDN Edge

아시아 리전 (ap-northeast-2, 서울)
│
├─ Video Server Cluster
├─ Database Replica (Read)
└─ CDN Edge
```

---

## 8. 데이터 모델 및 저장소

### 8.1 관계형 데이터베이스 (RDBMS)

#### PostgreSQL (권장)
- **장점**:
  - 오픈소스
  - JSONB 타입 지원 (유연한 스키마)
  - 강력한 트랜잭션
  - 확장성 좋음 (Citus, TimescaleDB)
- **용도**:
  - 사용자 정보
  - 코스 및 세션 메타데이터
  - 평가 결과

#### 스키마 예시
```sql
-- 사용자 테이블
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,  -- 'student', 'instructor', 'admin'
    created_at TIMESTAMP DEFAULT NOW()
);

-- 코스 테이블
CREATE TABLE courses (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    instructor_id INT REFERENCES users(id),
    start_date DATE,
    end_date DATE
);

-- 세션 테이블
CREATE TABLE sessions (
    id SERIAL PRIMARY KEY,
    course_id INT REFERENCES courses(id),
    title VARCHAR(255),
    scheduled_at TIMESTAMP,
    duration_minutes INT DEFAULT 90,
    status VARCHAR(50) DEFAULT 'scheduled'  -- 'scheduled', 'live', 'completed'
);

-- 참여 이벤트 테이블
CREATE TABLE participation_events (
    id BIGSERIAL PRIMARY KEY,
    session_id INT REFERENCES sessions(id),
    user_id INT REFERENCES users(id),
    event_type VARCHAR(50),
    timestamp TIMESTAMP DEFAULT NOW(),
    metadata JSONB
);

CREATE INDEX idx_participation_session_timestamp
ON participation_events(session_id, timestamp);
```

### 8.2 NoSQL 데이터베이스

#### MongoDB
- **용도**:
  - 채팅 메시지 (빠른 쓰기)
  - 로그 데이터
  - 유연한 스키마가 필요한 데이터

#### Redis
- **용도**:
  - 세션 캐시 (로그인 상태)
  - 실시간 참여자 목록
  - 투표 결과 임시 저장
  - Rate Limiting
  - Job Queue (Celery with Redis)

### 8.3 객체 스토리지 (Object Storage)

#### AWS S3 / Azure Blob / GCS
- **용도**:
  - 녹화된 세션 동영상
  - 학생 제출 파일
  - 코스 자료 (PDF, 이미지)
- **구조**:
  ```
  /recordings
    /course-123
      /session-456
        recording-456-2025-01-15.mp4
  /submissions
    /course-123
      /assignment-789
        /student-101
          submission.pdf
  ```

### 8.4 시계열 데이터베이스 (Time-Series DB)

#### InfluxDB / TimescaleDB
- **용도**:
  - 참여도 메트릭 (시간별)
  - 시스템 성능 모니터링
  - 대시보드 시각화 데이터

---

## 9. 보안 및 인증

### 9.1 인증 (Authentication)

#### JWT (JSON Web Token)
- **흐름**:
  1. 사용자 로그인 (이메일/비밀번호)
  2. 서버가 JWT 발급 (유효기간 포함)
  3. 클라이언트가 매 요청에 JWT 포함
  4. 서버가 JWT 검증

#### OAuth 2.0 / OpenID Connect
- **SSO (Single Sign-On)** 지원
- **소셜 로그인**: Google, Microsoft, GitHub
- **대학 LMS 연동**: LTI 1.3, SAML

#### 구현 예시 (Django REST Framework)
```python
from rest_framework_simplejwt.views import TokenObtainPairView

# settings.py
INSTALLED_APPS = [
    'rest_framework',
    'rest_framework_simplejwt',
]

REST_FRAMEWORK = {
    'DEFAULT_AUTHENTICATION_CLASSES': [
        'rest_framework_simplejwt.authentication.JWTAuthentication',
    ],
}

# urls.py
from rest_framework_simplejwt.views import TokenRefreshView

urlpatterns = [
    path('api/token/', TokenObtainPairView.as_view()),
    path('api/token/refresh/', TokenRefreshView.as_view()),
]
```

### 9.2 권한 관리 (Authorization)

#### RBAC (Role-Based Access Control)
- **역할**:
  - **Admin**: 모든 권한
  - **Instructor**: 자신의 코스 관리, 평가
  - **TA**: 특정 코스 보조, 제한적 평가
  - **Student**: 자신의 코스 참여, 제출

#### 권한 체크 예시
```python
from rest_framework.permissions import BasePermission

class IsInstructorOfCourse(BasePermission):
    def has_object_permission(self, request, view, obj):
        # obj는 Course 인스턴스
        return obj.instructor == request.user

# View에서 사용
class CourseUpdateView(UpdateAPIView):
    permission_classes = [IsAuthenticated, IsInstructorOfCourse]
```

### 9.3 데이터 보안

#### GDPR / FERPA 준수
- **개인정보 최소 수집**
- **데이터 익명화**: 연구 목적 사용 시
- **학생 동의**: 녹화, 데이터 수집
- **삭제 권리**: 학생이 자신의 데이터 삭제 요청

#### 암호화
- **전송 중 (In Transit)**: HTTPS/TLS 필수
- **저장 시 (At Rest)**: 데이터베이스 암호화 (AWS RDS 자동 지원)
- **민감 정보**: 비밀번호 해싱 (bcrypt, Argon2)

#### 코드 예시
```python
from django.contrib.auth.hashers import make_password, check_password

# 비밀번호 저장 시
hashed_password = make_password('user_password')

# 비밀번호 확인 시
is_correct = check_password('user_input', hashed_password)
```

### 9.4 비디오 스트림 보안

#### E2E Encryption (Optional)
- WebRTC DTLS-SRTP (기본 제공)
- 추가 암호화 레이어 (성능 trade-off)

#### 접근 제어
- JWT 기반 세션 입장 권한 확인
- 시간 기반 토큰 (만료 시간 설정)

---

## 10. 유사 플랫폼 기술 비교

### 10.1 Zoom / Google Meet / Microsoft Teams

| 특징 | Zoom | Google Meet | MS Teams | 미네르바 포럼 |
|------|------|-------------|----------|---------------|
| **주요 기술** | WebRTC + 독점 프로토콜 | WebRTC | WebRTC + Azure | WebRTC + 독점 |
| **SFU/MCU** | 혼합 | SFU | SFU | 추정 SFU |
| **분반 기능** | ✅ (투표 기반 자동 배정) | ✅ (랜덤 배정) | ✅ | ✅ (고급 배정) |
| **투표** | ✅ | ✅ | ✅ | ✅ |
| **참여도 추적** | ❌ (기본적) | ❌ | ❌ | ✅ (핵심 기능) |
| **교육학적 설계** | ❌ | ❌ | ❌ | ✅✅✅ |
| **자동 평가** | ❌ | ❌ | ❌ | ✅ |
| **해시태그 역량 추적** | ❌ | ❌ | ❌ | ✅ |

### 10.2 교육 전용 플랫폼

#### BigBlueButton (오픈소스)
- **장점**:
  - 완전 오픈소스 (AGPLv3)
  - GDPR 준수
  - 화이트보드, 투표, 분반 내장
  - LTI 통합 지원
- **단점**:
  - UI/UX 다소 구식
  - 확장성 제한적 (대규모 회의 어려움)
- **기술 스택**:
  - **프론트엔드**: React
  - **백엔드**: Node.js, Scala
  - **미디어 서버**: FreeSWITCH, Kurento

#### Class Collaborate (Blackboard)
- **장점**: LMS 통합
- **단점**: 상용, 비용

### 10.3 Jitsi Meet (오픈소스)
- **장점**:
  - 완전 무료 오픈소스
  - Docker 컨테이너 쉬운 배포
  - SFU 아키텍처 (확장성)
  - 커스터마이징 가능
- **단점**:
  - 교육 전용 기능 부족 (자체 개발 필요)
  - 참여도 추적 기본 없음
- **기술 스택**:
  - **프론트엔드**: React
  - **백엔드**: Java (Jitsi Videobridge), Node.js (Jicofo)
  - **시그널링**: Prosody (XMPP)

---

## 11. 권장 기술 스택 (구현 제안)

미네르바 포럼 유사 플랫폼 개발을 위한 추천 기술 스택:

### 11.1 프론트엔드

#### 프레임워크
- **React** (권장)
  - 생태계 풍부
  - 컴포넌트 재사용성
  - 대규모 커뮤니티
- **대안**: Vue.js, Svelte

#### 주요 라이브러리
- **WebRTC**: simple-peer, mediasoup-client
- **실시간 통신**: Socket.io-client
- **상태 관리**: Redux Toolkit, Zustand, Jotai
- **UI 컴포넌트**: Material-UI, Ant Design, Tailwind CSS
- **차트**: Chart.js, Recharts, D3.js
- **화이트보드**: Excalidraw, Fabric.js
- **비디오**: Video.js

### 11.2 백엔드

#### 프레임워크
- **Django + Django REST Framework** (Python) - 미네르바와 동일
  - 빠른 개발
  - Admin 패널 자동 생성
  - ORM 강력
  - 풍부한 패키지
- **대안**: FastAPI (Python), NestJS (Node.js), Spring Boot (Java)

#### 실시간 통신
- **Django Channels** (WebSocket)
  - ASGI 지원
  - Channel Layers (Redis)

#### 백그라운드 작업
- **Celery** + Redis/RabbitMQ
  - 녹화 처리
  - 이메일 발송
  - 리포트 생성

### 11.3 미디어 서버

#### 권장 옵션
1. **Jitsi Meet** (완전한 솔루션)
   - Docker Compose로 빠른 배포
   - 커스터마이징: iframe API 또는 React SDK
2. **Mediasoup** (최대 유연성)
   - Node.js로 직접 구현
   - 복잡하지만 완전 제어

### 11.4 데이터베이스

- **Primary DB**: PostgreSQL 14+
- **Cache/Session**: Redis 7+
- **NoSQL (Optional)**: MongoDB (채팅 로그)
- **Time-Series (Optional)**: TimescaleDB (PostgreSQL 확장)

### 11.5 파일 스토리지

- **클라우드**: AWS S3 / Azure Blob / GCP Cloud Storage
- **CDN**: CloudFront / Azure CDN / Cloud CDN

### 11.6 인프라

- **컨테이너화**: Docker, Docker Compose
- **오케스트레이션**: Kubernetes (프로덕션)
- **CI/CD**: GitHub Actions, GitLab CI, Jenkins
- **모니터링**:
  - **APM**: New Relic, DataDog, Sentry
  - **로그**: ELK Stack (Elasticsearch, Logstash, Kibana)
  - **메트릭**: Prometheus + Grafana

### 11.7 AI/ML

- **NLP 프레임워크**: Hugging Face Transformers
- **모델 서빙**: TensorFlow Serving, TorchServe, FastAPI
- **클라우드 AI**: AWS Comprehend, Azure Cognitive Services, Google Cloud NLP

### 11.8 인증/보안

- **JWT**: djangorestframework-simplejwt
- **OAuth**: django-allauth, Auth0
- **HTTPS**: Let's Encrypt (자동 갱신)

---

## 12. 아키텍처 다이어그램 (종합)

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND LAYER                          │
│  React SPA (with Socket.io, WebRTC, Chart.js, Excalidraw)      │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTPS / WebSocket
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY / LOAD BALANCER                │
│                     (Nginx, Kong, AWS ALB)                      │
└───────────┬─────────────────────────────────┬───────────────────┘
            │                                 │
            ↓                                 ↓
┌───────────────────────┐         ┌───────────────────────┐
│  APPLICATION SERVERS  │         │   MEDIA SERVERS       │
│  (Django + Channels)  │         │  (Jitsi / Mediasoup)  │
│                       │         │                       │
│  - REST API           │         │  - WebRTC SFU         │
│  - WebSocket Handlers │         │  - STUN/TURN          │
│  - Business Logic     │         │  - Recording          │
│  - Celery Workers     │         │                       │
└──────┬────────────────┘         └───────────────────────┘
       │                                     │
       ↓                                     ↓
┌─────────────────────────────────────────────────────────────────┐
│                         DATA LAYER                              │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │ PostgreSQL   │  │    Redis     │  │  AWS S3      │         │
│  │ (Primary DB) │  │  (Cache/MQ)  │  │ (Files)      │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐                            │
│  │  MongoDB     │  │ TimescaleDB  │                            │
│  │  (Chat Logs) │  │ (Metrics)    │                            │
│  └──────────────┘  └──────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│                      EXTERNAL SERVICES                          │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │  OpenAI API  │  │  Email SES   │  │   Auth0      │         │
│  │  (NLP/AI)    │  │  (Notif.)    │  │  (SSO)       │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

---

## 13. 개발 로드맵 제안

### Phase 1: MVP (3-4개월)
- ✅ 기본 비디오 컨퍼런싱 (Jitsi 통합)
- ✅ 사용자 인증 및 코스 관리
- ✅ 실시간 채팅
- ✅ 기본 투표 기능
- ✅ 간단한 퀴즈 (객관식)

### Phase 2: 액티브 러닝 기능 (2-3개월)
- ✅ 분반 (Breakout Rooms)
- ✅ 화이트보드
- ✅ 공동 문서 편집
- ✅ 손들기 / 반응
- ✅ 화면 공유

### Phase 3: 참여도 추적 (2-3개월)
- ✅ 실시간 참여도 대시보드
- ✅ 발언 시간 추적
- ✅ 이벤트 로깅
- ✅ 기본 리포트 생성

### Phase 4: AI/ML 통합 (3-4개월)
- ✅ 감성 분석 (피드백)
- ✅ 자동 에세이 채점
- ✅ 질문 품질 평가
- ✅ 개인화 추천

### Phase 5: 고급 기능 (2-3개월)
- ✅ 녹화 및 자동 자막
- ✅ LTI 통합
- ✅ 해시태그 기반 역량 추적
- ✅ 학제간 커리큘럼 지원

### Phase 6: 확장 및 최적화 (계속)
- ✅ Kubernetes 배포
- ✅ 멀티 리전 지원
- ✅ 성능 최적화
- ✅ 모바일 앱 (React Native)

**총 예상 기간**: 12-18개월 (5-10명 팀 기준)

---

## 14. 참고 자료

### 공식 문서
- [Django Documentation](https://docs.djangoproject.com/)
- [Django Channels](https://channels.readthedocs.io/)
- [WebRTC API - MDN](https://developer.mozilla.org/en-US/docs/Web/API/WebRTC_API)
- [Jitsi Meet Documentation](https://jitsi.github.io/handbook/)
- [Mediasoup Documentation](https://mediasoup.org/documentation/v3/)

### 오픈소스 프로젝트
- [Jitsi Meet GitHub](https://github.com/jitsi/jitsi-meet)
- [BigBlueButton GitHub](https://github.com/bigbluebutton/bigbluebutton)
- [Excalidraw GitHub](https://github.com/excalidraw/excalidraw)
- [Yjs GitHub](https://github.com/yjs/yjs)

### 연구 논문 및 기사
- [Sentiment Analysis of Students' Feedback with NLP and Deep Learning](https://www.mdpi.com/2076-3417/11/9/3986)
- [A Data-Driven Approach to Quantify and Measure Students' Engagement](https://pmc.ncbi.nlm.nih.gov/articles/PMC9103305/)
- [Building an EdTech Platform Using Microservices and Docker](https://ieeexplore.ieee.org/document/9686535/)

### 플랫폼 비교 자료
- [Forum™ Platform - Minerva Project](https://www.minervaproject.com/platform)
- [7 Best Adaptive Learning Platforms in 2025](https://whatfix.com/blog/adaptive-learning-platforms/)
- [Top 10 Learning Analytics Platforms](https://www.educate-me.co/blog/learning-analytics-tools)

### 기술 아키텍처 가이드
- [How to Design a Zoom — Distributed Video Conferencing Architecture](https://medium.com/@li.ying.explore/how-to-design-a-zoom-distributed-video-conferencing-architecture-webrtc-rtp-sfu-0a45b3f928d0)
- [Microservices Architecture on Azure Kubernetes Service](https://learn.microsoft.com/en-us/azure/architecture/reference-architectures/containers/aks-microservices/aks-microservices)
- [Kubernetes Architecture Explained](https://www.datacamp.com/blog/kubernetes-architecture-explained)

### 검색 결과 출처
- [Minerva Active Learning Forum](https://wit.io/portfolio/minerva-active-learning-forum)
- [An Architectural Overview for WebRTC](https://eytanmanor.medium.com/an-architectural-overview-for-web-rtc-a-protocol-for-implementing-video-conferencing-e2a914628d0e)
- [Full-Stack Student Engagement Platform - Suitable](https://www.suitable.co/solutions)
- [Sentiment analysis and opinion mining on educational data](https://www.sciencedirect.com/science/article/pii/S2949719122000036)
- [Microservices on Kubernetes](https://codefresh.io/learn/microservices/microservices-on-kubernetes-how-it-works-and-6-tips-for-success/)

---

## 결론

미네르바 포럼과 같은 액티브 러닝 플랫폼은 단순히 기술의 집합이 아니라, **교육 철학이 기술로 구현된 통합 시스템**입니다.

핵심 성공 요소:
1. **교육학적 기반**: 인지과학 연구에 기반한 설계
2. **실시간 분석**: 참여도 및 이해도 실시간 추적
3. **자동화된 피드백**: AI/ML 기반 평가 및 개인화
4. **확장 가능한 아키텍처**: 마이크로서비스 + Kubernetes
5. **사용자 경험**: 직관적이고 반응성 좋은 UI/UX

이 문서에서 제안한 기술 스택과 아키텍처는 검증된 오픈소스 기술과 클라우드 서비스를 조합하여, 대학교 및 교육기관에 제공 가능한 수준의 플랫폼을 개발할 수 있는 실질적인 로드맵을 제공합니다.

다음 문서에서는 **실제 구현 예시 코드**와 **프로토타입 개발 가이드**를 다룰 예정입니다.
