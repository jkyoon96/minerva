# DB-001: 전체 데이터베이스 스키마 생성

> **Task ID**: DB-001
> **Issue**: #282
> **Epic**: INFRA
> **Priority**: P0
> **Story Points**: 3
> **작업일**: 2025-01-28
> **상태**: 완료

---

## 1. 작업 개요

PostgreSQL 데이터베이스 스키마 생성 및 모든 테이블 DDL 실행

### 1.1 주요 산출물

| 항목 | 위치 | 설명 |
|------|------|------|
| 마이그레이션 파일 | `database/migrations/` | 10개 SQL 파일 |
| 롤백 파일 | `database/rollback/` | 10개 SQL 파일 |
| 시딩 스크립트 | `database/seeds/` | 1개 SQL 파일 |
| 실행 스크립트 | `database/scripts/` | 3개 Shell 스크립트 |
| 문서 | `database/` | 4개 마크다운 파일 |

---

## 2. 데이터베이스 구성

### 2.1 접속 정보

```
Database: eduforum
User:     eduforum
Password: eduforum12
Host:     localhost
Port:     5432
```

### 2.2 스키마 구조

```
eduforum/
├── auth      - 인증/인가 (8 테이블)
├── course    - 코스 관리 (7 테이블)
├── live      - 실시간 세션 (6 테이블)
├── learning  - 액티브 러닝 (9 테이블)
├── assess    - 평가 (4 테이블)
└── analytics - 분석 (5 테이블)

총 39 테이블
```

---

## 3. 마이그레이션 파일 상세

### 3.1 마이그레이션 순서

| 순서 | 파일명 | 설명 |
|------|--------|------|
| 001 | create_database.sql | DB/사용자 생성 |
| 002 | create_extensions_and_types.sql | 확장/ENUM 타입 |
| 003 | auth_schema.sql | 인증 스키마 |
| 004 | course_schema.sql | 코스 스키마 |
| 005 | live_schema.sql | 라이브 세션 스키마 |
| 006 | learning_schema.sql | 러닝 스키마 |
| 007 | assess_schema.sql | 평가 스키마 |
| 008 | analytics_schema.sql | 분석 스키마 |
| 009 | indexes.sql | 추가 인덱스 |
| 010 | initial_data.sql | 초기 데이터 |

### 3.2 테이블 목록

#### auth 스키마 (8 테이블)
- `users` - 사용자
- `roles` - 역할
- `permissions` - 권한
- `user_roles` - 사용자-역할 매핑
- `role_permissions` - 역할-권한 매핑
- `oauth_accounts` - OAuth 연동
- `two_factor_auth` - 2FA
- `refresh_tokens` - 리프레시 토큰
- `password_reset_tokens` - 비밀번호 재설정 토큰

#### course 스키마 (7 테이블)
- `courses` - 코스
- `enrollments` - 수강 등록
- `sessions` - 세션
- `recordings` - 녹화
- `contents` - 콘텐츠
- `assignments` - 과제
- `submissions` - 제출

#### live 스키마 (6 테이블)
- `session_participants` - 세션 참가자
- `chats` - 채팅
- `breakout_rooms` - 분반
- `breakout_participants` - 분반 참가자
- `reactions` - 반응
- `hand_raises` - 손들기

#### learning 스키마 (9 테이블)
- `polls` - 투표
- `poll_options` - 투표 옵션
- `poll_votes` - 투표 응답
- `quizzes` - 퀴즈
- `questions` - 문제
- `quiz_attempts` - 퀴즈 시도
- `quiz_answers` - 퀴즈 답안
- `whiteboards` - 화이트보드
- `whiteboard_elements` - 화이트보드 요소

#### assess 스키마 (4 테이블)
- `grades` - 성적
- `ai_gradings` - AI 채점
- `peer_evaluations` - 동료 평가
- `code_executions` - 코드 실행

#### analytics 스키마 (5 테이블)
- `participation_logs` - 참여도 로그
- `alerts` - 알림
- `interaction_logs` - 상호작용 로그
- `daily_stats` - 일일 통계
- `notification_settings` - 알림 설정

---

## 4. 실행 방법

### 4.1 마이그레이션 실행

```bash
cd /mnt/d/Development/git/minerva/database/scripts
chmod +x *.sh

# 마이그레이션 실행
./migrate.sh localhost postgres

# 시딩 실행 (선택)
./seed.sh localhost
```

### 4.2 롤백 실행

```bash
# 전체 롤백
./rollback.sh localhost postgres
```

### 4.3 검증

```bash
# 테이블 확인
psql -U eduforum -d eduforum -c "
SELECT schemaname, COUNT(*) as tables
FROM pg_tables
WHERE schemaname IN ('auth', 'course', 'live', 'learning', 'assess', 'analytics')
GROUP BY schemaname
ORDER BY schemaname;
"
```

---

## 5. 초기 데이터

### 5.1 역할 (Roles)

| ID | 이름 | 설명 |
|----|------|------|
| 1 | admin | 시스템 관리자 |
| 2 | professor | 교수 |
| 3 | ta | 조교 |
| 4 | student | 학생 |

### 5.2 권한 (Permissions)

| 리소스 | 권한 | 설명 |
|--------|------|------|
| user | read, write, delete | 사용자 관리 |
| course | read, write, delete | 코스 관리 |
| session | read, write | 세션 관리 |
| grade | read, write | 성적 관리 |
| analytics | read | 분석 조회 |

### 5.3 역할별 권한

| 역할 | 부여된 권한 |
|------|------------|
| admin | 모든 권한 |
| professor | course:*, session:*, grade:*, analytics:read |
| ta | course:read, session:read, grade:* |
| student | course:read, session:read, grade:read |

---

## 6. 테스트 데이터 (시딩)

### 6.1 테스트 사용자

| 이메일 | 역할 | 비밀번호 해시 |
|--------|------|--------------|
| admin@eduforum.edu | admin | bcrypt hash |
| prof.kim@eduforum.edu | professor | bcrypt hash |
| prof.lee@eduforum.edu | professor | bcrypt hash |
| ta.park@eduforum.edu | ta | bcrypt hash |
| student1~5@eduforum.edu | student | bcrypt hash |

### 6.2 테스트 코스

| 코드 | 제목 | 교수 |
|------|------|------|
| CS101 | 컴퓨터 과학 입문 | prof.kim |
| CS201 | 자료구조와 알고리즘 | prof.lee |

---

## 7. 파일 구조

```
database/
├── README.md                      # 사용 가이드
├── SUMMARY.md                     # 상세 요약
├── QUICK_REFERENCE.md            # 빠른 참조
├── MIGRATION_CHECKLIST.md        # 체크리스트
│
├── migrations/
│   ├── 001_create_database.sql
│   ├── 002_create_extensions_and_types.sql
│   ├── 003_auth_schema.sql
│   ├── 004_course_schema.sql
│   ├── 005_live_schema.sql
│   ├── 006_learning_schema.sql
│   ├── 007_assess_schema.sql
│   ├── 008_analytics_schema.sql
│   ├── 009_indexes.sql
│   └── 010_initial_data.sql
│
├── rollback/
│   ├── 001_drop_database.sql
│   ├── 002_drop_extensions_and_types.sql
│   ├── 003_drop_auth_schema.sql
│   ├── 004_drop_course_schema.sql
│   ├── 005_drop_live_schema.sql
│   ├── 006_drop_learning_schema.sql
│   ├── 007_drop_assess_schema.sql
│   ├── 008_drop_analytics_schema.sql
│   ├── 009_drop_indexes.sql
│   └── 010_drop_initial_data.sql
│
├── seeds/
│   └── 001_test_data.sql
│
└── scripts/
    ├── migrate.sh
    ├── rollback.sh
    └── seed.sh
```

---

## 8. Acceptance Criteria 충족 현황

### Database 요구사항

- [x] 스키마 변경사항이 마이그레이션 파일로 작성됨
- [x] 롤백 마이그레이션이 포함됨
- [x] 인덱스 및 제약조건이 적절히 설정됨
- [x] 테스트 데이터 시딩 스크립트 포함

### 품질 요구사항

- [x] 코드 리뷰 완료
- [x] 문서화 완료

---

## 9. 참조 문서

- `docs/06-database-design.md` - 데이터베이스 설계 문서
- `database/README.md` - 마이그레이션 사용 가이드
- `database/QUICK_REFERENCE.md` - 빠른 참조

---

## 10. 변경 이력

| 버전 | 날짜 | 작성자 | 변경 내용 |
|------|------|--------|----------|
| 1.0 | 2025-01-28 | Claude | 초기 작성 |

---

**작업 완료**
