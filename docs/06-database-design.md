# EduForum 데이터베이스 설계

> **버전**: 1.0
> **최종 수정일**: 2025-01-28
> **데이터베이스**: PostgreSQL 16
> **참조 문서**: PRD, 시스템 아키텍처, 기능 세분화

---

## 목차

1. [설계 원칙](#1-설계-원칙)
2. [ERD (Entity Relationship Diagram)](#2-erd-entity-relationship-diagram)
3. [스키마 구조](#3-스키마-구조)
4. [테이블 정의 (DDL)](#4-테이블-정의-ddl)
5. [인덱스 전략](#5-인덱스-전략)
6. [데이터 타입 규칙](#6-데이터-타입-규칙)
7. [파티셔닝 전략](#7-파티셔닝-전략)
8. [마이그레이션 가이드](#8-마이그레이션-가이드)

---

## 1. 설계 원칙

### 1.1 네이밍 컨벤션

| 항목 | 규칙 | 예시 |
|------|------|------|
| 테이블명 | snake_case, 복수형 | `users`, `course_enrollments` |
| 컬럼명 | snake_case | `created_at`, `user_id` |
| PK | `id` (BIGSERIAL) | `id` |
| FK | `{참조테이블_단수}_id` | `user_id`, `course_id` |
| 인덱스 | `idx_{테이블}_{컬럼}` | `idx_users_email` |
| 제약조건 | `{테이블}_{컬럼}_{타입}` | `users_email_unique` |

### 1.2 공통 컬럼

모든 테이블에 포함되는 공통 컬럼:

```sql
id          BIGSERIAL PRIMARY KEY,
created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
```

### 1.3 Soft Delete 정책

삭제 대신 `deleted_at` 컬럼 사용 (nullable):

```sql
deleted_at  TIMESTAMPTZ DEFAULT NULL
```

---

## 2. ERD (Entity Relationship Diagram)

### 2.1 전체 ERD 개요

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    EduForum ERD                                          │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐    │
│  │                              AUTHENTICATION DOMAIN                               │    │
│  │                                                                                  │    │
│  │   ┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌─────────────┐   │    │
│  │   │  users   │────<│ user_roles   │>────│    roles     │────<│ permissions │   │    │
│  │   └────┬─────┘     └──────────────┘     └──────────────┘     └─────────────┘   │    │
│  │        │                                                                        │    │
│  │        │           ┌──────────────┐     ┌──────────────┐                       │    │
│  │        ├──────────<│oauth_accounts│     │  two_factor  │>──────────────────────┤    │
│  │        │           └──────────────┘     │    _auth     │                       │    │
│  │        │                                └──────────────┘                       │    │
│  └────────┼─────────────────────────────────────────────────────────────────────────┘    │
│           │                                                                              │
│  ┌────────┼─────────────────────────────────────────────────────────────────────────┐    │
│  │        │                         COURSE DOMAIN                                   │    │
│  │        │                                                                         │    │
│  │        │    ┌──────────┐     ┌───────────────┐     ┌─────────────┐              │    │
│  │        └───>│ courses  │────<│  enrollments  │     │  sessions   │              │    │
│  │             └────┬─────┘     └───────────────┘     └──────┬──────┘              │    │
│  │                  │                                        │                      │    │
│  │                  │    ┌──────────────┐    ┌──────────────┐│                      │    │
│  │                  ├───<│   contents   │    │  recordings  │<──────────────────────┤    │
│  │                  │    └──────────────┘    └──────────────┘                      │    │
│  │                  │                                                               │    │
│  │                  │    ┌──────────────┐    ┌──────────────┐                      │    │
│  │                  ├───<│ assignments  │───<│ submissions  │                      │    │
│  │                  │    └──────────────┘    └──────────────┘                      │    │
│  └──────────────────┼───────────────────────────────────────────────────────────────┘    │
│                     │                                                                    │
│  ┌──────────────────┼───────────────────────────────────────────────────────────────┐    │
│  │                  │              LIVE SESSION DOMAIN                              │    │
│  │                  │                                                               │    │
│  │                  │    ┌──────────────┐    ┌──────────────┐                      │    │
│  │                  └───>│session_parti-│    │   chats      │                      │    │
│  │                       │  cipants     │    └──────────────┘                      │    │
│  │                       └──────────────┘                                          │    │
│  │                                                                                  │    │
│  │   ┌──────────────┐    ┌──────────────┐    ┌──────────────┐                      │    │
│  │   │breakout_rooms│───<│breakout_parti│    │  reactions   │                      │    │
│  │   └──────────────┘    │   cipants    │    └──────────────┘                      │    │
│  │                       └──────────────┘                                          │    │
│  └──────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                          │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐    │
│  │                           ACTIVE LEARNING DOMAIN                                 │    │
│  │                                                                                  │    │
│  │   ┌──────────┐     ┌──────────────┐     ┌──────────────┐                        │    │
│  │   │  polls   │────<│ poll_options │────<│ poll_votes   │                        │    │
│  │   └──────────┘     └──────────────┘     └──────────────┘                        │    │
│  │                                                                                  │    │
│  │   ┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐  │    │
│  │   │ quizzes  │────<│  questions   │────<│quiz_attempts │────<│quiz_answers  │  │    │
│  │   └──────────┘     └──────────────┘     └──────────────┘     └──────────────┘  │    │
│  │                                                                                  │    │
│  │   ┌──────────────┐     ┌──────────────┐                                         │    │
│  │   │ whiteboards  │────<│whiteboard_   │                                         │    │
│  │   │              │     │  elements    │                                         │    │
│  │   └──────────────┘     └──────────────┘                                         │    │
│  └──────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                          │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐    │
│  │                           ASSESSMENT DOMAIN                                      │    │
│  │                                                                                  │    │
│  │   ┌──────────────┐     ┌──────────────┐     ┌──────────────┐                    │    │
│  │   │    grades    │     │ ai_gradings  │     │peer_evalua-  │                    │    │
│  │   │              │     │              │     │   tions      │                    │    │
│  │   └──────────────┘     └──────────────┘     └──────────────┘                    │    │
│  └──────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                          │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐    │
│  │                           ANALYTICS DOMAIN                                       │    │
│  │                                                                                  │    │
│  │   ┌──────────────┐     ┌──────────────┐     ┌──────────────┐                    │    │
│  │   │participation │     │   alerts     │     │interaction_  │                    │    │
│  │   │   _logs      │     │              │     │    logs      │                    │    │
│  │   └──────────────┘     └──────────────┘     └──────────────┘                    │    │
│  └──────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                          │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 상세 ERD (도메인별)

#### 2.2.1 Authentication Domain

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           AUTHENTICATION                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │       users         │          │       roles         │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │          │ PK id               │                   │
│  │    email            │          │    name             │                   │
│  │    password_hash    │          │    description      │                   │
│  │    first_name       │          │    created_at       │                   │
│  │    last_name        │          └──────────┬──────────┘                   │
│  │    profile_image_url│                     │                              │
│  │    phone            │                     │ 1:N                          │
│  │    status           │                     │                              │
│  │    email_verified_at│          ┌──────────▼──────────┐                   │
│  │    last_login_at    │          │   role_permissions  │                   │
│  │    created_at       │          ├─────────────────────┤                   │
│  │    updated_at       │          │ PK id               │                   │
│  │    deleted_at       │          │ FK role_id          │                   │
│  └──────────┬──────────┘          │ FK permission_id    │                   │
│             │                     └──────────┬──────────┘                   │
│             │                                │                              │
│             │ 1:N                            │ N:1                          │
│             │                                │                              │
│  ┌──────────▼──────────┐          ┌──────────▼──────────┐                   │
│  │     user_roles      │          │    permissions      │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │          │ PK id               │                   │
│  │ FK user_id          │          │    name             │                   │
│  │ FK role_id          │          │    resource         │                   │
│  │    assigned_at      │          │    action           │                   │
│  │    assigned_by      │          │    description      │                   │
│  └─────────────────────┘          └─────────────────────┘                   │
│                                                                              │
│             │ 1:N                                                            │
│             │                                                                │
│  ┌──────────▼──────────┐          ┌─────────────────────┐                   │
│  │   oauth_accounts    │          │   two_factor_auth   │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │          │ PK id               │                   │
│  │ FK user_id          │          │ FK user_id          │                   │
│  │    provider         │          │    secret           │                   │
│  │    provider_user_id │          │    backup_codes     │                   │
│  │    access_token     │          │    is_enabled       │                   │
│  │    refresh_token    │          │    verified_at      │                   │
│  │    expires_at       │          │    created_at       │                   │
│  │    created_at       │          └─────────────────────┘                   │
│  └─────────────────────┘                                                    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 2.2.2 Course Domain

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              COURSE                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────┐                    ┌─────────────────────┐         │
│  │      courses        │                    │      sessions       │         │
│  ├─────────────────────┤                    ├─────────────────────┤         │
│  │ PK id               │◄───────────────────│ PK id               │         │
│  │ FK professor_id     │        1:N         │ FK course_id        │         │
│  │    code             │                    │    title            │         │
│  │    title            │                    │    description      │         │
│  │    description      │                    │    scheduled_at     │         │
│  │    semester         │                    │    duration_minutes │         │
│  │    year             │                    │    status           │         │
│  │    thumbnail_url    │                    │    started_at       │         │
│  │    invite_code      │                    │    ended_at         │         │
│  │    max_students     │                    │    meeting_url      │         │
│  │    is_published     │                    │    created_at       │         │
│  │    settings (JSONB) │                    └──────────┬──────────┘         │
│  │    created_at       │                               │                    │
│  │    deleted_at       │                               │ 1:N                │
│  └──────────┬──────────┘                               │                    │
│             │                               ┌──────────▼──────────┐         │
│             │ 1:N                           │     recordings      │         │
│             │                               ├─────────────────────┤         │
│  ┌──────────▼──────────┐                    │ PK id               │         │
│  │    enrollments      │                    │ FK session_id       │         │
│  ├─────────────────────┤                    │    file_url         │         │
│  │ PK id               │                    │    duration_seconds │         │
│  │ FK user_id          │                    │    file_size_bytes  │         │
│  │ FK course_id        │                    │    captions_url     │         │
│  │    role             │                    │    thumbnail_url    │         │
│  │    joined_at        │                    │    status           │         │
│  │    status           │                    │    created_at       │         │
│  │    created_at       │                    └─────────────────────┘         │
│  └─────────────────────┘                                                    │
│                                                                              │
│             │ (course_id)                                                    │
│             │ 1:N                                                            │
│             │                                                                │
│  ┌──────────▼──────────┐          ┌─────────────────────┐                   │
│  │     assignments     │          │     submissions     │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │◄─────────│ PK id               │                   │
│  │ FK course_id        │   1:N    │ FK assignment_id    │                   │
│  │    title            │          │ FK user_id          │                   │
│  │    description      │          │    file_url         │                   │
│  │    due_date         │          │    submitted_at     │                   │
│  │    max_score        │          │    score            │                   │
│  │    allow_late       │          │    feedback         │                   │
│  │    late_penalty     │          │    graded_at        │                   │
│  │    max_attempts     │          │ FK graded_by        │                   │
│  │    attachments      │          │    status           │                   │
│  │    created_at       │          │    created_at       │                   │
│  └─────────────────────┘          └─────────────────────┘                   │
│                                                                              │
│  ┌─────────────────────┐                                                    │
│  │      contents       │                                                    │
│  ├─────────────────────┤                                                    │
│  │ PK id               │                                                    │
│  │ FK course_id        │                                                    │
│  │ FK folder_id        │ (self-reference for hierarchy)                     │
│  │    title            │                                                    │
│  │    type             │ (file, folder, link)                               │
│  │    file_url         │                                                    │
│  │    file_size_bytes  │                                                    │
│  │    mime_type        │                                                    │
│  │    is_visible       │                                                    │
│  │    order_index      │                                                    │
│  │    created_at       │                                                    │
│  └─────────────────────┘                                                    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 2.2.3 Live Session Domain

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            LIVE SESSION                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │session_participants │          │       chats         │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │          │ PK id               │                   │
│  │ FK session_id       │          │ FK session_id       │                   │
│  │ FK user_id          │          │ FK user_id          │                   │
│  │    joined_at        │          │ FK reply_to_id      │ (self-ref)        │
│  │    left_at          │          │    message          │                   │
│  │    role             │          │    type             │ (text,file,system)│
│  │    is_camera_on     │          │    file_url         │                   │
│  │    is_mic_on        │          │    is_private       │                   │
│  │    connection_quality│         │ FK private_to_id    │                   │
│  │    created_at       │          │    created_at       │                   │
│  └─────────────────────┘          │    deleted_at       │                   │
│                                   └─────────────────────┘                   │
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │   breakout_rooms    │          │breakout_participants│                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │◄─────────│ PK id               │                   │
│  │ FK session_id       │   1:N    │ FK breakout_room_id │                   │
│  │    name             │          │ FK user_id          │                   │
│  │    topic            │          │    joined_at        │                   │
│  │    duration_minutes │          │    left_at          │                   │
│  │    status           │          │    created_at       │                   │
│  │    started_at       │          └─────────────────────┘                   │
│  │    ended_at         │                                                    │
│  │    created_at       │                                                    │
│  └─────────────────────┘                                                    │
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │     reactions       │          │     hand_raises     │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │          │ PK id               │                   │
│  │ FK session_id       │          │ FK session_id       │                   │
│  │ FK user_id          │          │ FK user_id          │                   │
│  │    type             │          │    raised_at        │                   │
│  │    created_at       │          │    lowered_at       │                   │
│  └─────────────────────┘          │    called_at        │                   │
│                                   │ FK called_by        │                   │
│                                   └─────────────────────┘                   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 2.2.4 Active Learning Domain

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          ACTIVE LEARNING                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │       polls         │          │    poll_options     │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │◄─────────│ PK id               │                   │
│  │ FK session_id       │   1:N    │ FK poll_id          │                   │
│  │ FK created_by       │          │    option_text      │                   │
│  │    question         │          │    order_index      │                   │
│  │    type             │          │    is_correct       │                   │
│  │    is_anonymous     │          │    created_at       │                   │
│  │    allow_multiple   │          └──────────┬──────────┘                   │
│  │    show_results     │                     │                              │
│  │    time_limit_sec   │                     │ 1:N                          │
│  │    status           │                     │                              │
│  │    started_at       │          ┌──────────▼──────────┐                   │
│  │    ended_at         │          │    poll_votes       │                   │
│  │    created_at       │          ├─────────────────────┤                   │
│  └─────────────────────┘          │ PK id               │                   │
│                                   │ FK poll_option_id   │                   │
│                                   │ FK user_id          │                   │
│                                   │    voted_at         │                   │
│                                   └─────────────────────┘                   │
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │      quizzes        │          │     questions       │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │◄─────────│ PK id               │                   │
│  │ FK session_id       │   1:N    │ FK quiz_id          │                   │
│  │ FK course_id        │          │    question_text    │                   │
│  │ FK created_by       │          │    type             │                   │
│  │    title            │          │    options (JSONB)  │                   │
│  │    description      │          │    correct_answer   │                   │
│  │    time_limit_sec   │          │    explanation      │                   │
│  │    shuffle_questions│          │    points           │                   │
│  │    show_answers     │          │    difficulty       │                   │
│  │    passing_score    │          │    tags (JSONB)     │                   │
│  │    max_attempts     │          │    order_index      │                   │
│  │    status           │          │    created_at       │                   │
│  │    started_at       │          └──────────┬──────────┘                   │
│  │    ended_at         │                     │                              │
│  │    created_at       │                     │                              │
│  └──────────┬──────────┘                     │                              │
│             │                                │                              │
│             │ 1:N                            │                              │
│             │                                │                              │
│  ┌──────────▼──────────┐          ┌──────────▼──────────┐                   │
│  │   quiz_attempts     │          │   quiz_answers      │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │◄─────────│ PK id               │                   │
│  │ FK quiz_id          │   1:N    │ FK attempt_id       │                   │
│  │ FK user_id          │          │ FK question_id      │                   │
│  │    started_at       │          │    answer (JSONB)   │                   │
│  │    submitted_at     │          │    is_correct       │                   │
│  │    score            │          │    points_earned    │                   │
│  │    max_score        │          │    answered_at      │                   │
│  │    status           │          └─────────────────────┘                   │
│  │    created_at       │                                                    │
│  └─────────────────────┘                                                    │
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │    whiteboards      │          │ whiteboard_elements │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │◄─────────│ PK id               │                   │
│  │ FK session_id       │   1:N    │ FK whiteboard_id    │                   │
│  │ FK breakout_room_id │          │ FK created_by       │                   │
│  │    name             │          │    type             │                   │
│  │    created_at       │          │    data (JSONB)     │                   │
│  └─────────────────────┘          │    z_index          │                   │
│                                   │    created_at       │                   │
│                                   │    updated_at       │                   │
│                                   └─────────────────────┘                   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 2.2.5 Assessment & Analytics Domain

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       ASSESSMENT & ANALYTICS                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │       grades        │          │    ai_gradings      │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │          │ PK id               │                   │
│  │ FK user_id          │          │ FK submission_id    │                   │
│  │ FK course_id        │          │    ai_score         │                   │
│  │    participation    │          │    confidence       │                   │
│  │    quiz_average     │          │    feedback (JSONB) │                   │
│  │    assignment_avg   │          │    keywords_found   │                   │
│  │    final_score      │          │    similarity_score │                   │
│  │    letter_grade     │          │    model_version    │                   │
│  │    updated_at       │          │    reviewed_by      │                   │
│  │    created_at       │          │    reviewed_at      │                   │
│  └─────────────────────┘          │    created_at       │                   │
│                                   └─────────────────────┘                   │
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │  peer_evaluations   │          │ participation_logs  │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │          │ PK id               │                   │
│  │ FK course_id        │          │ FK session_id       │                   │
│  │ FK evaluator_id     │          │ FK user_id          │                   │
│  │ FK evaluatee_id     │          │    talk_time_sec    │                   │
│  │    criteria (JSONB) │          │    chat_count       │                   │
│  │    scores (JSONB)   │          │    poll_count       │                   │
│  │    comments         │          │    quiz_count       │                   │
│  │    is_anonymous     │          │    hand_raise_count │                   │
│  │    submitted_at     │          │    reaction_count   │                   │
│  │    created_at       │          │    breakout_time_sec│                   │
│  └─────────────────────┘          │    engagement_score │                   │
│                                   │    recorded_at      │                   │
│                                   │    created_at       │                   │
│                                   └─────────────────────┘                   │
│                                                                              │
│  ┌─────────────────────┐          ┌─────────────────────┐                   │
│  │       alerts        │          │  interaction_logs   │                   │
│  ├─────────────────────┤          ├─────────────────────┤                   │
│  │ PK id               │          │ PK id               │                   │
│  │ FK user_id          │          │ FK session_id       │                   │
│  │ FK course_id        │          │ FK from_user_id     │                   │
│  │    type             │          │ FK to_user_id       │                   │
│  │    severity         │          │    interaction_type │                   │
│  │    message          │          │    context (JSONB)  │                   │
│  │    data (JSONB)     │          │    created_at       │                   │
│  │    is_read          │          └─────────────────────┘                   │
│  │    resolved_at      │                                                    │
│  │    resolved_by      │                                                    │
│  │    created_at       │                                                    │
│  └─────────────────────┘                                                    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. 스키마 구조

PostgreSQL 스키마를 도메인별로 분리:

```sql
-- 스키마 생성
CREATE SCHEMA IF NOT EXISTS auth;      -- 인증/인가
CREATE SCHEMA IF NOT EXISTS course;    -- 코스 관리
CREATE SCHEMA IF NOT EXISTS live;      -- 라이브 세션
CREATE SCHEMA IF NOT EXISTS learning;  -- 액티브 러닝
CREATE SCHEMA IF NOT EXISTS assess;    -- 평가
CREATE SCHEMA IF NOT EXISTS analytics; -- 분석

-- 검색 경로 설정
SET search_path TO auth, course, live, learning, assess, analytics, public;
```

---

## 4. 테이블 정의 (DDL)

### 4.1 공통 타입 및 확장

```sql
-- UUID 확장 (필요시)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 암호화 확장
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 전문 검색
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ENUM 타입 정의
CREATE TYPE user_status AS ENUM ('active', 'inactive', 'suspended', 'pending');
CREATE TYPE enrollment_role AS ENUM ('student', 'ta', 'auditor');
CREATE TYPE enrollment_status AS ENUM ('active', 'dropped', 'completed');
CREATE TYPE session_status AS ENUM ('scheduled', 'live', 'ended', 'cancelled');
CREATE TYPE assignment_status AS ENUM ('draft', 'published', 'closed');
CREATE TYPE submission_status AS ENUM ('submitted', 'graded', 'returned', 'resubmitted');
CREATE TYPE poll_type AS ENUM ('single', 'multiple', 'open_text', 'scale');
CREATE TYPE poll_status AS ENUM ('draft', 'active', 'ended');
CREATE TYPE question_type AS ENUM ('single_choice', 'multiple_choice', 'true_false', 'short_answer', 'essay', 'code');
CREATE TYPE quiz_status AS ENUM ('draft', 'active', 'ended');
CREATE TYPE quiz_attempt_status AS ENUM ('in_progress', 'submitted', 'graded');
CREATE TYPE alert_type AS ENUM ('absence', 'low_participation', 'grade_drop', 'inactivity');
CREATE TYPE alert_severity AS ENUM ('low', 'medium', 'high', 'critical');
CREATE TYPE content_type AS ENUM ('file', 'folder', 'link', 'video');
CREATE TYPE recording_status AS ENUM ('processing', 'ready', 'failed');
CREATE TYPE breakout_status AS ENUM ('pending', 'active', 'ended');
CREATE TYPE reaction_type AS ENUM ('thumbs_up', 'clap', 'heart', 'laugh', 'thinking', 'surprised');

-- updated_at 자동 갱신 함수
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';
```

### 4.2 Authentication Domain

```sql
-- ============================================
-- AUTH SCHEMA
-- ============================================

-- 사용자 테이블
CREATE TABLE auth.users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    profile_image_url VARCHAR(500),
    phone           VARCHAR(20),
    status          user_status NOT NULL DEFAULT 'pending',
    email_verified_at TIMESTAMPTZ,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT users_email_unique UNIQUE (email)
);

CREATE INDEX idx_users_email ON auth.users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_status ON auth.users(status) WHERE deleted_at IS NULL;

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON auth.users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 역할 테이블
CREATE TABLE auth.roles (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL UNIQUE,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 기본 역할 삽입
INSERT INTO auth.roles (name, description) VALUES
    ('admin', '시스템 관리자'),
    ('professor', '교수'),
    ('ta', '조교'),
    ('student', '학생');

-- 권한 테이블
CREATE TABLE auth.permissions (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    resource        VARCHAR(50) NOT NULL,
    action          VARCHAR(50) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 역할-권한 매핑
CREATE TABLE auth.role_permissions (
    id              BIGSERIAL PRIMARY KEY,
    role_id         BIGINT NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
    permission_id   BIGINT NOT NULL REFERENCES auth.permissions(id) ON DELETE CASCADE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT role_permissions_unique UNIQUE (role_id, permission_id)
);

-- 사용자-역할 매핑
CREATE TABLE auth.user_roles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    role_id         BIGINT NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    assigned_by     BIGINT REFERENCES auth.users(id),

    CONSTRAINT user_roles_unique UNIQUE (user_id, role_id)
);

CREATE INDEX idx_user_roles_user_id ON auth.user_roles(user_id);

-- OAuth 계정 연동
CREATE TABLE auth.oauth_accounts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    provider        VARCHAR(50) NOT NULL,  -- 'google', 'microsoft', 'github'
    provider_user_id VARCHAR(255) NOT NULL,
    access_token    TEXT,
    refresh_token   TEXT,
    token_expires_at TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT oauth_accounts_unique UNIQUE (provider, provider_user_id)
);

CREATE INDEX idx_oauth_accounts_user_id ON auth.oauth_accounts(user_id);

-- 2단계 인증
CREATE TABLE auth.two_factor_auth (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE UNIQUE,
    secret          VARCHAR(255) NOT NULL,
    backup_codes    JSONB NOT NULL DEFAULT '[]',
    is_enabled      BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 세션/토큰 관리 (Redis 권장, 백업용)
CREATE TABLE auth.refresh_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    device_info     JSONB,
    ip_address      INET,
    expires_at      TIMESTAMPTZ NOT NULL,
    revoked_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id ON auth.refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON auth.refresh_tokens(expires_at) WHERE revoked_at IS NULL;

-- 비밀번호 재설정 토큰
CREATE TABLE auth.password_reset_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    expires_at      TIMESTAMPTZ NOT NULL,
    used_at         TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_password_reset_tokens_user_id ON auth.password_reset_tokens(user_id);
```

### 4.3 Course Domain

```sql
-- ============================================
-- COURSE SCHEMA
-- ============================================

-- 코스 테이블
CREATE TABLE course.courses (
    id              BIGSERIAL PRIMARY KEY,
    professor_id    BIGINT NOT NULL REFERENCES auth.users(id),
    code            VARCHAR(50) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    semester        VARCHAR(20) NOT NULL,  -- 'Spring', 'Summer', 'Fall', 'Winter'
    year            INTEGER NOT NULL,
    thumbnail_url   VARCHAR(500),
    invite_code     VARCHAR(20) UNIQUE,
    invite_expires_at TIMESTAMPTZ,
    max_students    INTEGER DEFAULT 50,
    is_published    BOOLEAN NOT NULL DEFAULT FALSE,
    settings        JSONB NOT NULL DEFAULT '{
        "grading_weights": {
            "participation": 30,
            "quiz": 30,
            "assignment": 40
        },
        "allow_late_submission": true,
        "late_penalty_percent": 10
    }',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT courses_code_year_semester_unique UNIQUE (code, year, semester)
);

CREATE INDEX idx_courses_professor_id ON course.courses(professor_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_courses_semester_year ON course.courses(year, semester) WHERE deleted_at IS NULL;
CREATE INDEX idx_courses_invite_code ON course.courses(invite_code) WHERE deleted_at IS NULL AND invite_code IS NOT NULL;

CREATE TRIGGER update_courses_updated_at
    BEFORE UPDATE ON course.courses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 수강 등록
CREATE TABLE course.enrollments (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    role            enrollment_role NOT NULL DEFAULT 'student',
    status          enrollment_status NOT NULL DEFAULT 'active',
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT enrollments_user_course_unique UNIQUE (user_id, course_id)
);

CREATE INDEX idx_enrollments_course_id ON course.enrollments(course_id);
CREATE INDEX idx_enrollments_user_id ON course.enrollments(user_id);
CREATE INDEX idx_enrollments_status ON course.enrollments(status);

-- 세션 (수업 일정)
CREATE TABLE course.sessions (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    scheduled_at    TIMESTAMPTZ NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 90,
    status          session_status NOT NULL DEFAULT 'scheduled',
    started_at      TIMESTAMPTZ,
    ended_at        TIMESTAMPTZ,
    meeting_url     VARCHAR(500),
    settings        JSONB NOT NULL DEFAULT '{
        "enable_waiting_room": false,
        "auto_record": true,
        "allow_chat": true,
        "allow_reactions": true
    }',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sessions_course_id ON course.sessions(course_id);
CREATE INDEX idx_sessions_scheduled_at ON course.sessions(scheduled_at);
CREATE INDEX idx_sessions_status ON course.sessions(status);

CREATE TRIGGER update_sessions_updated_at
    BEFORE UPDATE ON course.sessions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 녹화
CREATE TABLE course.recordings (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    file_url        VARCHAR(500) NOT NULL,
    duration_seconds INTEGER,
    file_size_bytes BIGINT,
    captions_url    VARCHAR(500),
    thumbnail_url   VARCHAR(500),
    status          recording_status NOT NULL DEFAULT 'processing',
    metadata        JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_recordings_session_id ON course.recordings(session_id);

-- 콘텐츠 라이브러리
CREATE TABLE course.contents (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    folder_id       BIGINT REFERENCES course.contents(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    type            content_type NOT NULL DEFAULT 'file',
    file_url        VARCHAR(500),
    file_size_bytes BIGINT,
    mime_type       VARCHAR(100),
    is_visible      BOOLEAN NOT NULL DEFAULT TRUE,
    order_index     INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contents_course_id ON course.contents(course_id);
CREATE INDEX idx_contents_folder_id ON course.contents(folder_id);

-- 과제
CREATE TABLE course.assignments (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    due_date        TIMESTAMPTZ NOT NULL,
    max_score       INTEGER NOT NULL DEFAULT 100,
    allow_late      BOOLEAN NOT NULL DEFAULT TRUE,
    late_penalty_percent INTEGER DEFAULT 10,
    max_attempts    INTEGER DEFAULT 1,
    attachments     JSONB DEFAULT '[]',
    status          assignment_status NOT NULL DEFAULT 'draft',
    published_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_assignments_course_id ON course.assignments(course_id);
CREATE INDEX idx_assignments_due_date ON course.assignments(due_date);
CREATE INDEX idx_assignments_status ON course.assignments(status);

CREATE TRIGGER update_assignments_updated_at
    BEFORE UPDATE ON course.assignments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 과제 제출
CREATE TABLE course.submissions (
    id              BIGSERIAL PRIMARY KEY,
    assignment_id   BIGINT NOT NULL REFERENCES course.assignments(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    file_url        VARCHAR(500),
    content         TEXT,
    submitted_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    score           DECIMAL(5,2),
    feedback        TEXT,
    graded_at       TIMESTAMPTZ,
    graded_by       BIGINT REFERENCES auth.users(id),
    status          submission_status NOT NULL DEFAULT 'submitted',
    attempt_number  INTEGER NOT NULL DEFAULT 1,
    is_late         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_submissions_assignment_id ON course.submissions(assignment_id);
CREATE INDEX idx_submissions_user_id ON course.submissions(user_id);
CREATE INDEX idx_submissions_status ON course.submissions(status);

CREATE TRIGGER update_submissions_updated_at
    BEFORE UPDATE ON course.submissions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

### 4.4 Live Session Domain

```sql
-- ============================================
-- LIVE SCHEMA
-- ============================================

-- 세션 참가자
CREATE TABLE live.session_participants (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    left_at         TIMESTAMPTZ,
    role            VARCHAR(20) NOT NULL DEFAULT 'participant',  -- 'host', 'co-host', 'participant'
    is_camera_on    BOOLEAN NOT NULL DEFAULT FALSE,
    is_mic_on       BOOLEAN NOT NULL DEFAULT FALSE,
    is_screen_sharing BOOLEAN NOT NULL DEFAULT FALSE,
    connection_quality VARCHAR(20) DEFAULT 'good',  -- 'excellent', 'good', 'fair', 'poor'
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT session_participants_unique UNIQUE (session_id, user_id, joined_at)
);

CREATE INDEX idx_session_participants_session_id ON live.session_participants(session_id);
CREATE INDEX idx_session_participants_user_id ON live.session_participants(user_id);

-- 채팅 메시지
CREATE TABLE live.chats (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    reply_to_id     BIGINT REFERENCES live.chats(id) ON DELETE SET NULL,
    message         TEXT NOT NULL,
    type            VARCHAR(20) NOT NULL DEFAULT 'text',  -- 'text', 'file', 'system'
    file_url        VARCHAR(500),
    is_private      BOOLEAN NOT NULL DEFAULT FALSE,
    private_to_id   BIGINT REFERENCES auth.users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_chats_session_id ON live.chats(session_id);
CREATE INDEX idx_chats_user_id ON live.chats(user_id);
CREATE INDEX idx_chats_created_at ON live.chats(created_at);

-- 분반 (Breakout Rooms)
CREATE TABLE live.breakout_rooms (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    topic           TEXT,
    duration_minutes INTEGER DEFAULT 10,
    status          breakout_status NOT NULL DEFAULT 'pending',
    started_at      TIMESTAMPTZ,
    ended_at        TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_breakout_rooms_session_id ON live.breakout_rooms(session_id);

-- 분반 참가자
CREATE TABLE live.breakout_participants (
    id              BIGSERIAL PRIMARY KEY,
    breakout_room_id BIGINT NOT NULL REFERENCES live.breakout_rooms(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    left_at         TIMESTAMPTZ,
    assigned_by     VARCHAR(20) NOT NULL DEFAULT 'auto',  -- 'auto', 'manual', 'self'
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT breakout_participants_unique UNIQUE (breakout_room_id, user_id)
);

CREATE INDEX idx_breakout_participants_room_id ON live.breakout_participants(breakout_room_id);
CREATE INDEX idx_breakout_participants_user_id ON live.breakout_participants(user_id);

-- 반응
CREATE TABLE live.reactions (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    type            reaction_type NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reactions_session_id ON live.reactions(session_id);
CREATE INDEX idx_reactions_created_at ON live.reactions(created_at);

-- 손들기
CREATE TABLE live.hand_raises (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    raised_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    lowered_at      TIMESTAMPTZ,
    called_at       TIMESTAMPTZ,
    called_by       BIGINT REFERENCES auth.users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_hand_raises_session_id ON live.hand_raises(session_id);
CREATE INDEX idx_hand_raises_raised_at ON live.hand_raises(raised_at) WHERE lowered_at IS NULL;
```

### 4.5 Active Learning Domain

```sql
-- ============================================
-- LEARNING SCHEMA
-- ============================================

-- 투표
CREATE TABLE learning.polls (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    created_by      BIGINT NOT NULL REFERENCES auth.users(id),
    question        TEXT NOT NULL,
    type            poll_type NOT NULL DEFAULT 'single',
    is_anonymous    BOOLEAN NOT NULL DEFAULT FALSE,
    allow_multiple  BOOLEAN NOT NULL DEFAULT FALSE,
    show_results    BOOLEAN NOT NULL DEFAULT TRUE,
    time_limit_sec  INTEGER,
    status          poll_status NOT NULL DEFAULT 'draft',
    started_at      TIMESTAMPTZ,
    ended_at        TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_polls_session_id ON learning.polls(session_id);
CREATE INDEX idx_polls_status ON learning.polls(status);

CREATE TRIGGER update_polls_updated_at
    BEFORE UPDATE ON learning.polls
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 투표 선택지
CREATE TABLE learning.poll_options (
    id              BIGSERIAL PRIMARY KEY,
    poll_id         BIGINT NOT NULL REFERENCES learning.polls(id) ON DELETE CASCADE,
    option_text     TEXT NOT NULL,
    order_index     INTEGER NOT NULL DEFAULT 0,
    is_correct      BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_poll_options_poll_id ON learning.poll_options(poll_id);

-- 투표 응답
CREATE TABLE learning.poll_votes (
    id              BIGSERIAL PRIMARY KEY,
    poll_option_id  BIGINT NOT NULL REFERENCES learning.poll_options(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    text_response   TEXT,  -- for open_text type
    voted_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT poll_votes_unique UNIQUE (poll_option_id, user_id)
);

CREATE INDEX idx_poll_votes_poll_option_id ON learning.poll_votes(poll_option_id);
CREATE INDEX idx_poll_votes_user_id ON learning.poll_votes(user_id);

-- 퀴즈
CREATE TABLE learning.quizzes (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT REFERENCES course.sessions(id) ON DELETE SET NULL,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    created_by      BIGINT NOT NULL REFERENCES auth.users(id),
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    time_limit_sec  INTEGER,
    shuffle_questions BOOLEAN NOT NULL DEFAULT FALSE,
    show_answers    BOOLEAN NOT NULL DEFAULT TRUE,
    passing_score   INTEGER DEFAULT 60,
    max_attempts    INTEGER DEFAULT 1,
    status          quiz_status NOT NULL DEFAULT 'draft',
    started_at      TIMESTAMPTZ,
    ended_at        TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_quizzes_course_id ON learning.quizzes(course_id);
CREATE INDEX idx_quizzes_session_id ON learning.quizzes(session_id);
CREATE INDEX idx_quizzes_status ON learning.quizzes(status);

CREATE TRIGGER update_quizzes_updated_at
    BEFORE UPDATE ON learning.quizzes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 문제
CREATE TABLE learning.questions (
    id              BIGSERIAL PRIMARY KEY,
    quiz_id         BIGINT REFERENCES learning.quizzes(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    question_text   TEXT NOT NULL,
    type            question_type NOT NULL,
    options         JSONB,  -- for choice questions: [{"id": 1, "text": "Option A"}, ...]
    correct_answer  JSONB NOT NULL,  -- {"option_ids": [1]} or {"text": "answer"} or {"code": "solution"}
    explanation     TEXT,
    points          INTEGER NOT NULL DEFAULT 1,
    difficulty      VARCHAR(20) DEFAULT 'medium',  -- 'easy', 'medium', 'hard'
    tags            JSONB DEFAULT '[]',
    order_index     INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_questions_quiz_id ON learning.questions(quiz_id);
CREATE INDEX idx_questions_course_id ON learning.questions(course_id);
CREATE INDEX idx_questions_type ON learning.questions(type);
CREATE INDEX idx_questions_tags ON learning.questions USING GIN(tags);

CREATE TRIGGER update_questions_updated_at
    BEFORE UPDATE ON learning.questions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 퀴즈 시도
CREATE TABLE learning.quiz_attempts (
    id              BIGSERIAL PRIMARY KEY,
    quiz_id         BIGINT NOT NULL REFERENCES learning.quizzes(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    started_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    submitted_at    TIMESTAMPTZ,
    score           DECIMAL(5,2),
    max_score       DECIMAL(5,2),
    status          quiz_attempt_status NOT NULL DEFAULT 'in_progress',
    attempt_number  INTEGER NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_quiz_attempts_quiz_id ON learning.quiz_attempts(quiz_id);
CREATE INDEX idx_quiz_attempts_user_id ON learning.quiz_attempts(user_id);
CREATE INDEX idx_quiz_attempts_status ON learning.quiz_attempts(status);

CREATE TRIGGER update_quiz_attempts_updated_at
    BEFORE UPDATE ON learning.quiz_attempts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 퀴즈 답안
CREATE TABLE learning.quiz_answers (
    id              BIGSERIAL PRIMARY KEY,
    attempt_id      BIGINT NOT NULL REFERENCES learning.quiz_attempts(id) ON DELETE CASCADE,
    question_id     BIGINT NOT NULL REFERENCES learning.questions(id) ON DELETE CASCADE,
    answer          JSONB NOT NULL,  -- {"option_ids": [1]} or {"text": "answer"} or {"code": "code"}
    is_correct      BOOLEAN,
    points_earned   DECIMAL(5,2) DEFAULT 0,
    answered_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT quiz_answers_unique UNIQUE (attempt_id, question_id)
);

CREATE INDEX idx_quiz_answers_attempt_id ON learning.quiz_answers(attempt_id);
CREATE INDEX idx_quiz_answers_question_id ON learning.quiz_answers(question_id);

-- 화이트보드
CREATE TABLE learning.whiteboards (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT REFERENCES course.sessions(id) ON DELETE CASCADE,
    breakout_room_id BIGINT REFERENCES live.breakout_rooms(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL DEFAULT 'Whiteboard',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_whiteboards_session_id ON learning.whiteboards(session_id);
CREATE INDEX idx_whiteboards_breakout_room_id ON learning.whiteboards(breakout_room_id);

-- 화이트보드 요소
CREATE TABLE learning.whiteboard_elements (
    id              BIGSERIAL PRIMARY KEY,
    whiteboard_id   BIGINT NOT NULL REFERENCES learning.whiteboards(id) ON DELETE CASCADE,
    created_by      BIGINT NOT NULL REFERENCES auth.users(id),
    type            VARCHAR(50) NOT NULL,  -- 'pen', 'rectangle', 'ellipse', 'text', 'image', 'line'
    data            JSONB NOT NULL,  -- { x, y, width, height, color, strokeWidth, text, imageUrl, points, ... }
    z_index         INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_whiteboard_elements_whiteboard_id ON learning.whiteboard_elements(whiteboard_id);
CREATE INDEX idx_whiteboard_elements_created_by ON learning.whiteboard_elements(created_by);

CREATE TRIGGER update_whiteboard_elements_updated_at
    BEFORE UPDATE ON learning.whiteboard_elements
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

### 4.6 Assessment Domain

```sql
-- ============================================
-- ASSESS SCHEMA
-- ============================================

-- 성적
CREATE TABLE assess.grades (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    participation_score DECIMAL(5,2) DEFAULT 0,
    quiz_average    DECIMAL(5,2) DEFAULT 0,
    assignment_average DECIMAL(5,2) DEFAULT 0,
    final_score     DECIMAL(5,2) DEFAULT 0,
    letter_grade    VARCHAR(2),  -- 'A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D+', 'D', 'F'
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT grades_user_course_unique UNIQUE (user_id, course_id)
);

CREATE INDEX idx_grades_course_id ON assess.grades(course_id);
CREATE INDEX idx_grades_user_id ON assess.grades(user_id);

CREATE TRIGGER update_grades_updated_at
    BEFORE UPDATE ON assess.grades
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- AI 채점 결과
CREATE TABLE assess.ai_gradings (
    id              BIGSERIAL PRIMARY KEY,
    submission_id   BIGINT REFERENCES course.submissions(id) ON DELETE CASCADE,
    quiz_answer_id  BIGINT REFERENCES learning.quiz_answers(id) ON DELETE CASCADE,
    ai_score        DECIMAL(5,2) NOT NULL,
    confidence      DECIMAL(3,2) NOT NULL,  -- 0.00 ~ 1.00
    feedback        JSONB NOT NULL DEFAULT '{}',
    keywords_found  JSONB DEFAULT '[]',
    similarity_score DECIMAL(3,2),  -- 모범 답안과의 유사도
    model_version   VARCHAR(50) NOT NULL,
    reviewed_by     BIGINT REFERENCES auth.users(id),
    reviewed_at     TIMESTAMPTZ,
    final_score     DECIMAL(5,2),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT ai_gradings_submission_or_answer CHECK (
        (submission_id IS NOT NULL AND quiz_answer_id IS NULL) OR
        (submission_id IS NULL AND quiz_answer_id IS NOT NULL)
    )
);

CREATE INDEX idx_ai_gradings_submission_id ON assess.ai_gradings(submission_id);
CREATE INDEX idx_ai_gradings_quiz_answer_id ON assess.ai_gradings(quiz_answer_id);

-- 동료 평가
CREATE TABLE assess.peer_evaluations (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    evaluator_id    BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    evaluatee_id    BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    assignment_id   BIGINT REFERENCES course.assignments(id) ON DELETE SET NULL,
    criteria        JSONB NOT NULL,  -- [{"name": "협업", "weight": 30}, {"name": "기여도", "weight": 40}, ...]
    scores          JSONB NOT NULL,  -- {"협업": 85, "기여도": 90, ...}
    comments        TEXT,
    is_anonymous    BOOLEAN NOT NULL DEFAULT TRUE,
    submitted_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT peer_evaluations_self_check CHECK (evaluator_id != evaluatee_id)
);

CREATE INDEX idx_peer_evaluations_course_id ON assess.peer_evaluations(course_id);
CREATE INDEX idx_peer_evaluations_evaluatee_id ON assess.peer_evaluations(evaluatee_id);

-- 코드 실행 결과 (코딩 문제용)
CREATE TABLE assess.code_executions (
    id              BIGSERIAL PRIMARY KEY,
    quiz_answer_id  BIGINT NOT NULL REFERENCES learning.quiz_answers(id) ON DELETE CASCADE,
    language        VARCHAR(50) NOT NULL,  -- 'python', 'java', 'cpp', 'javascript'
    source_code     TEXT NOT NULL,
    test_cases      JSONB NOT NULL,  -- [{"input": "1 2", "expected": "3", "actual": "3", "passed": true}, ...]
    passed_count    INTEGER NOT NULL DEFAULT 0,
    total_count     INTEGER NOT NULL DEFAULT 0,
    execution_time_ms INTEGER,
    memory_used_kb  INTEGER,
    stdout          TEXT,
    stderr          TEXT,
    status          VARCHAR(20) NOT NULL,  -- 'accepted', 'wrong_answer', 'time_limit', 'runtime_error', 'compile_error'
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_code_executions_quiz_answer_id ON assess.code_executions(quiz_answer_id);
```

### 4.7 Analytics Domain

```sql
-- ============================================
-- ANALYTICS SCHEMA
-- ============================================

-- 참여도 로그 (세션별)
CREATE TABLE analytics.participation_logs (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    talk_time_sec   INTEGER NOT NULL DEFAULT 0,
    chat_count      INTEGER NOT NULL DEFAULT 0,
    poll_count      INTEGER NOT NULL DEFAULT 0,
    quiz_count      INTEGER NOT NULL DEFAULT 0,
    hand_raise_count INTEGER NOT NULL DEFAULT 0,
    reaction_count  INTEGER NOT NULL DEFAULT 0,
    breakout_time_sec INTEGER NOT NULL DEFAULT 0,
    engagement_score DECIMAL(5,2) NOT NULL DEFAULT 0,
    recorded_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_participation_logs_session_id ON analytics.participation_logs(session_id);
CREATE INDEX idx_participation_logs_user_id ON analytics.participation_logs(user_id);
CREATE INDEX idx_participation_logs_recorded_at ON analytics.participation_logs(recorded_at);

-- 파티션 (월별)
-- CREATE TABLE analytics.participation_logs_2025_01 PARTITION OF analytics.participation_logs
--     FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

-- 알림/경보
CREATE TABLE analytics.alerts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    type            alert_type NOT NULL,
    severity        alert_severity NOT NULL DEFAULT 'medium',
    message         TEXT NOT NULL,
    data            JSONB DEFAULT '{}',
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at     TIMESTAMPTZ,
    resolved_by     BIGINT REFERENCES auth.users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_alerts_user_id ON analytics.alerts(user_id);
CREATE INDEX idx_alerts_course_id ON analytics.alerts(course_id);
CREATE INDEX idx_alerts_type ON analytics.alerts(type);
CREATE INDEX idx_alerts_is_read ON analytics.alerts(is_read) WHERE is_read = FALSE;
CREATE INDEX idx_alerts_created_at ON analytics.alerts(created_at);

-- 상호작용 로그 (네트워크 분석용)
CREATE TABLE analytics.interaction_logs (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT NOT NULL REFERENCES course.sessions(id) ON DELETE CASCADE,
    from_user_id    BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    to_user_id      BIGINT REFERENCES auth.users(id) ON DELETE CASCADE,
    interaction_type VARCHAR(50) NOT NULL,  -- 'chat_reply', 'question_to', 'breakout_discussion', 'poll_response'
    context         JSONB DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_interaction_logs_session_id ON analytics.interaction_logs(session_id);
CREATE INDEX idx_interaction_logs_from_user_id ON analytics.interaction_logs(from_user_id);
CREATE INDEX idx_interaction_logs_to_user_id ON analytics.interaction_logs(to_user_id);
CREATE INDEX idx_interaction_logs_created_at ON analytics.interaction_logs(created_at);

-- 일일 통계 (집계 테이블)
CREATE TABLE analytics.daily_stats (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES course.courses(id) ON DELETE CASCADE,
    stat_date       DATE NOT NULL,
    active_users    INTEGER NOT NULL DEFAULT 0,
    total_sessions  INTEGER NOT NULL DEFAULT 0,
    total_talk_time_sec BIGINT NOT NULL DEFAULT 0,
    avg_engagement  DECIMAL(5,2) NOT NULL DEFAULT 0,
    quiz_attempts   INTEGER NOT NULL DEFAULT 0,
    avg_quiz_score  DECIMAL(5,2),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT daily_stats_unique UNIQUE (course_id, stat_date)
);

CREATE INDEX idx_daily_stats_course_id ON analytics.daily_stats(course_id);
CREATE INDEX idx_daily_stats_stat_date ON analytics.daily_stats(stat_date);

-- 알림 설정
CREATE TABLE analytics.notification_settings (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE UNIQUE,
    email_enabled   BOOLEAN NOT NULL DEFAULT TRUE,
    push_enabled    BOOLEAN NOT NULL DEFAULT TRUE,
    alert_types     JSONB NOT NULL DEFAULT '["absence", "low_participation", "grade_drop"]',
    quiet_hours     JSONB DEFAULT '{"start": "22:00", "end": "08:00"}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER update_notification_settings_updated_at
    BEFORE UPDATE ON analytics.notification_settings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

---

## 5. 인덱스 전략

### 5.1 인덱스 유형별 사용

| 인덱스 유형 | 사용 케이스 | 예시 |
|------------|------------|------|
| B-Tree | 동등/범위 검색 | `idx_users_email` |
| GIN | JSONB, 배열, 전문 검색 | `idx_questions_tags` |
| GiST | 지리/범위 데이터 | (해당 없음) |
| Partial | 조건부 인덱스 | `WHERE deleted_at IS NULL` |
| Covering | 쿼리 커버링 | `INCLUDE (column)` |

### 5.2 주요 쿼리 최적화 인덱스

```sql
-- 코스 목록 조회 (교수/학생별)
CREATE INDEX idx_enrollments_user_course_status ON course.enrollments(user_id, course_id, status);

-- 세션 일정 조회
CREATE INDEX idx_sessions_course_scheduled ON course.sessions(course_id, scheduled_at) WHERE status != 'cancelled';

-- 실시간 참여자 조회
CREATE INDEX idx_session_participants_active ON live.session_participants(session_id, user_id) WHERE left_at IS NULL;

-- 미제출 과제 조회
CREATE INDEX idx_submissions_pending ON course.submissions(assignment_id, user_id, status) WHERE status = 'submitted';

-- 퀴즈 답안 조회
CREATE INDEX idx_quiz_answers_attempt_question ON learning.quiz_answers(attempt_id, question_id);

-- 참여도 통계 조회 (기간별)
CREATE INDEX idx_participation_logs_user_period ON analytics.participation_logs(user_id, recorded_at);

-- 알림 조회 (읽지 않은 것만)
CREATE INDEX idx_alerts_unread ON analytics.alerts(user_id, created_at DESC) WHERE is_read = FALSE;
```

---

## 6. 데이터 타입 규칙

### 6.1 표준 데이터 타입

| 용도 | 데이터 타입 | 비고 |
|------|------------|------|
| PK | `BIGSERIAL` | 자동 증가 |
| FK | `BIGINT` | PK 참조 |
| 텍스트 (짧은) | `VARCHAR(n)` | 길이 제한 |
| 텍스트 (긴) | `TEXT` | 무제한 |
| 이메일 | `VARCHAR(255)` | 표준 최대 길이 |
| URL | `VARCHAR(500)` | 긴 URL 허용 |
| 날짜/시간 | `TIMESTAMPTZ` | 타임존 포함 |
| 날짜만 | `DATE` | 시간 없음 |
| 불리언 | `BOOLEAN` | true/false |
| 정수 | `INTEGER` | 4바이트 |
| 소수점 | `DECIMAL(5,2)` | 점수 등 |
| JSON 데이터 | `JSONB` | 바이너리 JSON |
| IP 주소 | `INET` | IPv4/IPv6 |
| 파일 크기 | `BIGINT` | 바이트 단위 |

### 6.2 JSONB 스키마 예시

```javascript
// course.settings
{
    "grading_weights": {
        "participation": 30,
        "quiz": 30,
        "assignment": 40
    },
    "allow_late_submission": true,
    "late_penalty_percent": 10
}

// learning.questions.options
[
    {"id": 1, "text": "Option A"},
    {"id": 2, "text": "Option B"},
    {"id": 3, "text": "Option C"},
    {"id": 4, "text": "Option D"}
]

// learning.questions.correct_answer
{"option_ids": [1]}           // 단일 선택
{"option_ids": [1, 3]}        // 다중 선택
{"text": "정답 텍스트"}        // 주관식
{"code": "def solution()..."}  // 코딩

// assess.ai_gradings.feedback
{
    "summary": "좋은 답변입니다.",
    "strengths": ["핵심 개념 이해", "명확한 설명"],
    "improvements": ["예시 추가 필요"],
    "keywords_matched": ["알고리즘", "시간복잡도"],
    "keywords_missing": ["공간복잡도"]
}
```

---

## 7. 파티셔닝 전략

### 7.1 파티셔닝 대상 테이블

대용량 로그/이벤트 테이블에 적용:

```sql
-- participation_logs 월별 파티셔닝
CREATE TABLE analytics.participation_logs (
    id              BIGSERIAL,
    session_id      BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    talk_time_sec   INTEGER NOT NULL DEFAULT 0,
    chat_count      INTEGER NOT NULL DEFAULT 0,
    poll_count      INTEGER NOT NULL DEFAULT 0,
    quiz_count      INTEGER NOT NULL DEFAULT 0,
    hand_raise_count INTEGER NOT NULL DEFAULT 0,
    reaction_count  INTEGER NOT NULL DEFAULT 0,
    breakout_time_sec INTEGER NOT NULL DEFAULT 0,
    engagement_score DECIMAL(5,2) NOT NULL DEFAULT 0,
    recorded_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, recorded_at)
) PARTITION BY RANGE (recorded_at);

-- 월별 파티션 생성
CREATE TABLE analytics.participation_logs_2025_01
    PARTITION OF analytics.participation_logs
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

CREATE TABLE analytics.participation_logs_2025_02
    PARTITION OF analytics.participation_logs
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');

-- interaction_logs도 동일하게 파티셔닝
CREATE TABLE analytics.interaction_logs (
    id              BIGSERIAL,
    session_id      BIGINT NOT NULL,
    from_user_id    BIGINT NOT NULL,
    to_user_id      BIGINT,
    interaction_type VARCHAR(50) NOT NULL,
    context         JSONB DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);
```

### 7.2 파티션 자동 생성 (pg_partman)

```sql
-- pg_partman 확장 사용
CREATE EXTENSION pg_partman;

SELECT create_parent(
    p_parent_table => 'analytics.participation_logs',
    p_control => 'recorded_at',
    p_type => 'native',
    p_interval => 'monthly',
    p_premake => 3  -- 3개월 미리 생성
);

-- 유지보수 함수 실행 (cron으로 주기적 실행)
SELECT run_maintenance();
```

---

## 8. 마이그레이션 가이드

### 8.1 마이그레이션 파일 구조 (SQL 기반)

```
database/migrations/
├── 001_create_database.sql      # 데이터베이스 및 스키마 생성
├── 002_auth_tables.sql          # 인증 테이블
├── 003_course_tables.sql        # 코스 테이블
├── 004_live_tables.sql          # 라이브 세션 테이블
├── 005_learning_tables.sql      # 액티브 러닝 테이블
├── 006_assessment_tables.sql    # 평가 테이블
├── 007_analytics_tables.sql     # 분석 테이블
├── 008_functions.sql            # 함수 및 트리거
├── 009_indexes.sql              # 인덱스 생성
└── 010_initial_data.sql         # 초기 데이터 (역할, 권한)
```

### 8.2 시드 데이터

```sql
-- 역할 초기 데이터
INSERT INTO auth.roles (name, description) VALUES
    ('admin', '시스템 관리자 - 전체 시스템 관리 권한'),
    ('professor', '교수 - 코스 생성, 세션 진행, 성적 관리'),
    ('ta', '조교 - 코스 관리 보조, 채점 보조'),
    ('student', '학생 - 코스 수강, 세션 참여');

-- 권한 초기 데이터
INSERT INTO auth.permissions (name, resource, action, description) VALUES
    ('user:read', 'user', 'read', '사용자 조회'),
    ('user:write', 'user', 'write', '사용자 생성/수정'),
    ('user:delete', 'user', 'delete', '사용자 삭제'),
    ('course:read', 'course', 'read', '코스 조회'),
    ('course:write', 'course', 'write', '코스 생성/수정'),
    ('course:delete', 'course', 'delete', '코스 삭제'),
    ('session:read', 'session', 'read', '세션 조회'),
    ('session:write', 'session', 'write', '세션 시작/종료'),
    ('grade:read', 'grade', 'read', '성적 조회'),
    ('grade:write', 'grade', 'write', '성적 입력/수정'),
    ('analytics:read', 'analytics', 'read', '분석 데이터 조회');

-- 역할-권한 매핑
INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permissions p
WHERE r.name = 'admin';  -- admin은 모든 권한

INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permissions p
WHERE r.name = 'professor'
  AND p.name IN ('course:read', 'course:write', 'session:read', 'session:write',
                 'grade:read', 'grade:write', 'analytics:read');

INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permissions p
WHERE r.name = 'ta'
  AND p.name IN ('course:read', 'session:read', 'grade:read', 'grade:write');

INSERT INTO auth.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permissions p
WHERE r.name = 'student'
  AND p.name IN ('course:read', 'session:read', 'grade:read');
```

### 8.3 백업 및 복구

```bash
# 전체 백업
pg_dump -h localhost -U postgres -d eduforum -F c -f eduforum_backup.dump

# 스키마별 백업
pg_dump -h localhost -U postgres -d eduforum -n auth -F c -f auth_backup.dump

# 복구
pg_restore -h localhost -U postgres -d eduforum -F c eduforum_backup.dump
```

---

## 부록

### A. 테이블 목록 요약

| 스키마 | 테이블 수 | 주요 테이블 |
|--------|----------|------------|
| auth | 7 | users, roles, permissions, user_roles, oauth_accounts, two_factor_auth, refresh_tokens |
| course | 6 | courses, enrollments, sessions, recordings, contents, assignments, submissions |
| live | 5 | session_participants, chats, breakout_rooms, breakout_participants, reactions, hand_raises |
| learning | 7 | polls, poll_options, poll_votes, quizzes, questions, quiz_attempts, quiz_answers, whiteboards, whiteboard_elements |
| assess | 4 | grades, ai_gradings, peer_evaluations, code_executions |
| analytics | 5 | participation_logs, alerts, interaction_logs, daily_stats, notification_settings |
| **총계** | **34** | |

### B. 변경 이력

| 버전 | 날짜 | 작성자 | 변경 내용 |
|------|------|--------|----------|
| 1.0 | 2025-01-28 | System | 초기 문서 작성 |

---

**문서 끝**
