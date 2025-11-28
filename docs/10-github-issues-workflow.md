# GitHub Issues 워크플로우

> CSV Task 파일을 GitHub Issues로 변환하기 위한 완전한 가이드

## 1. Label 체계

### 1.1 Epic Labels (범주)
| Label | 색상 | 설명 |
|-------|------|------|
| `epic:e1-auth` | `#7057ff` | 사용자 인증/인가 |
| `epic:e2-course` | `#008672` | 코스 관리 |
| `epic:e3-live` | `#d73a4a` | 실시간 세미나 |
| `epic:e4-active` | `#0075ca` | 액티브 러닝 도구 |
| `epic:e5-assessment` | `#e99695` | 평가 및 피드백 |
| `epic:e6-analytics` | `#5319e7` | 학습 분석 |

### 1.2 Type Labels (기술 도메인)
| Label | 색상 | 설명 |
|-------|------|------|
| `type:db` | `#1d76db` | Database 스키마/마이그레이션 |
| `type:be` | `#0e8a16` | Backend API/로직 |
| `type:fe` | `#fbca04` | Frontend UI/컴포넌트 |
| `type:doc` | `#c5def5` | Documentation |
| `type:infra` | `#b60205` | Infrastructure/DevOps |

### 1.3 Priority Labels
| Label | 색상 | 설명 |
|-------|------|------|
| `priority:p0-mvp` | `#d93f0b` | MVP 필수 기능 |
| `priority:p1-v1` | `#fbca04` | v1.0 릴리즈 |
| `priority:p2-v2` | `#0e8a16` | v2.0+ 확장 기능 |

### 1.4 Size Labels (Story Points)
| Label | 색상 | Story Points | 예상 시간 |
|-------|------|--------------|-----------|
| `size:xs` | `#ededed` | 1 SP | 2-4시간 |
| `size:s` | `#c2e0c6` | 2 SP | 0.5-1일 |
| `size:m` | `#bfd4f2` | 3 SP | 1-2일 |
| `size:l` | `#d4c5f9` | 5 SP | 3-5일 |
| `size:xl` | `#f9d0c4` | 8 SP | 1-2주 |

### 1.5 Status Labels
| Label | 색상 | 설명 |
|-------|------|------|
| `status:blocked` | `#b60205` | 의존성으로 블록됨 |
| `status:ready` | `#0e8a16` | 작업 가능 |
| `status:in-progress` | `#fbca04` | 작업 중 |
| `status:in-review` | `#1d76db` | 코드 리뷰 중 |

---

## 2. Issue Template

### 2.1 Task Issue Template

```markdown
---
name: Task
about: 개발 Task Issue
title: "[{TASK_ID}] {TASK_TITLE}"
labels: ''
assignees: ''
---

## 📋 Task 개요

| 항목 | 값 |
|------|-----|
| **Task ID** | {TASK_ID} |
| **Epic** | {EPIC} |
| **Story** | {STORY} |
| **Story Points** | {SP} |
| **Priority** | {PRIORITY} |

## 📝 Description

{DESCRIPTION}

## 🔗 Dependencies

### Blocked By (선행 작업)
{DEPENDENCIES_LIST}

### Blocks (후행 작업)
- 이 Issue 완료 후 작업 가능한 Task들

## 📚 Reference Documents

{REFERENCE_DOCS_LIST}

## 🎨 Wireframe Files (FE Only)

{WIREFRAME_FILES_LIST}

## ✅ Acceptance Criteria

{ACCEPTANCE_CRITERIA}

## 📎 Additional Notes

- 관련 PR:
- 테스트 시나리오:
```

---

## 3. Acceptance Criteria 생성 규칙

### 3.1 Type별 기본 Criteria

#### [DB] Database Tasks
```markdown
- [ ] 스키마 변경사항이 마이그레이션 파일로 작성됨
- [ ] 롤백 마이그레이션이 포함됨
- [ ] 인덱스 및 제약조건이 적절히 설정됨
- [ ] 테스트 데이터 시딩 스크립트 포함
- [ ] ERD 문서 업데이트
```

#### [BE] Backend Tasks
```markdown
- [ ] API 엔드포인트가 명세대로 구현됨
- [ ] 입력값 유효성 검사 구현
- [ ] 에러 핸들링 및 적절한 HTTP 상태 코드 반환
- [ ] 단위 테스트 작성 (커버리지 80% 이상)
- [ ] API 문서 (Swagger/OpenAPI) 업데이트
- [ ] 코드 리뷰 완료
```

#### [FE] Frontend Tasks
```markdown
- [ ] 와이어프레임 디자인대로 UI 구현
- [ ] 반응형 디자인 적용 (모바일/태블릿/데스크톱)
- [ ] 접근성 가이드라인 준수 (WCAG 2.1 AA)
- [ ] 컴포넌트 스토리북 작성
- [ ] 단위 테스트 작성
- [ ] E2E 테스트 시나리오 추가
- [ ] 코드 리뷰 완료
```

#### [DOC] Documentation Tasks
```markdown
- [ ] 문서 초안 작성
- [ ] 기술 검토 완료
- [ ] 스크린샷/다이어그램 포함
- [ ] 문서 리뷰 완료
```

### 3.2 Description 기반 추가 Criteria

| Description 키워드 | 추가 Criteria |
|-------------------|---------------|
| "유효성 검사" | `- [ ] 모든 입력 필드에 대한 유효성 검사 구현` |
| "토큰" | `- [ ] 토큰 만료 처리 구현` |
| "업로드" | `- [ ] 파일 크기 제한 검증 (최대 10MB)` |
| "S3" | `- [ ] S3 버킷 권한 설정 확인` |
| "WebSocket" | `- [ ] 연결 끊김 시 재연결 로직 구현` |
| "실시간" | `- [ ] 지연 시간 200ms 이내 확인` |

---

## 4. 의존성 관리

### 4.1 의존성 표기 규칙

CSV의 `Dependencies` 컬럼 형식:
```
E1-S1-T1; E1-S1-T2
```

GitHub Issue 변환 후:
```markdown
### Blocked By (선행 작업)
- [ ] #1 [E1-S1-T1] 회원가입 API 엔드포인트 개발
- [ ] #2 [E1-S1-T2] 비밀번호 해싱 및 검증 로직 구현
```

### 4.2 의존성 처리 순서

1. **의존성 없는 Task 먼저 생성** (Dependencies 컬럼 비어있는 항목)
2. **Task ID → Issue # 매핑 테이블 생성**
3. **의존성 있는 Task 생성** (매핑 테이블 참조)
4. **역방향 의존성 (Blocks) 업데이트**

---

## 5. 변환 프로세스

### 5.1 Phase 1: Label 생성

```bash
# labels.yml 파일로 일괄 생성
gh label create "epic:e1-auth" --color "7057ff" --description "사용자 인증/인가"
gh label create "epic:e2-course" --color "008672" --description "코스 관리"
gh label create "epic:e3-live" --color "d73a4a" --description "실시간 세미나"
gh label create "epic:e4-active" --color "0075ca" --description "액티브 러닝 도구"
gh label create "epic:e5-assessment" --color "e99695" --description "평가 및 피드백"
gh label create "epic:e6-analytics" --color "5319e7" --description "학습 분석"

gh label create "type:db" --color "1d76db" --description "Database"
gh label create "type:be" --color "0e8a16" --description "Backend"
gh label create "type:fe" --color "fbca04" --description "Frontend"
gh label create "type:doc" --color "c5def5" --description "Documentation"
gh label create "type:infra" --color "b60205" --description "Infrastructure"

gh label create "priority:p0-mvp" --color "d93f0b" --description "MVP 필수"
gh label create "priority:p1-v1" --color "fbca04" --description "v1.0"
gh label create "priority:p2-v2" --color "0e8a16" --description "v2.0+"

gh label create "size:xs" --color "ededed" --description "1 SP (2-4시간)"
gh label create "size:s" --color "c2e0c6" --description "2 SP (0.5-1일)"
gh label create "size:m" --color "bfd4f2" --description "3 SP (1-2일)"
gh label create "size:l" --color "d4c5f9" --description "5 SP (3-5일)"
gh label create "size:xl" --color "f9d0c4" --description "8 SP (1-2주)"

gh label create "status:blocked" --color "b60205" --description "의존성 블록"
gh label create "status:ready" --color "0e8a16" --description "작업 가능"
gh label create "status:in-progress" --color "fbca04" --description "작업 중"
gh label create "status:in-review" --color "1d76db" --description "리뷰 중"
```

### 5.2 Phase 2: Milestone 생성

```bash
gh milestone create "MVP (v0.1)" --description "최소 기능 제품"
gh milestone create "v1.0" --description "첫 번째 정식 릴리즈"
gh milestone create "v2.0" --description "확장 기능"
```

### 5.3 Phase 3: Issue 생성

변환 스크립트 실행:
```bash
./scripts/csv-to-issues.sh docs/09-git-issues-tasks.csv
```

---

## 6. Story Points → Size Label 매핑

| Story Points | Size Label |
|--------------|------------|
| 1 | `size:xs` |
| 2 | `size:s` |
| 3 | `size:m` |
| 5 | `size:l` |
| 8 | `size:xl` |

---

## 7. Priority → Milestone 매핑

| Priority | Milestone |
|----------|-----------|
| P0 | MVP (v0.1) |
| P1 | v1.0 |
| P2 | v2.0 |

---

## 8. Epic별 Story 목록

### E1: 사용자 인증 (44 SP)
| Story ID | Story 명 | Task 수 |
|----------|----------|---------|
| E1-S1 | 회원가입 | 8 |
| E1-S2 | 로그인 | 7 |
| E1-S3 | 소셜 로그인 | 5 |
| E1-S4 | 2FA | 6 |
| E1-S5 | 비밀번호 재설정 | 5 |
| E1-S6 | 역할 기반 접근 제어 | 6 |
| E1-S7 | 프로필 관리 | 6 |

### E2: 코스 관리 (52 SP)
| Story ID | Story 명 | Task 수 |
|----------|----------|---------|
| E2-S0 | 대시보드 | 10 |
| E2-S1 | 코스 생성/수정 | 8 |
| E2-S2 | 수강생 관리 | 6 |
| E2-S3 | 세션 관리 | 6 |
| E2-S4 | 과제 관리 | 9 |
| E2-S5 | 콘텐츠 라이브러리 | 5 |
| E2-S6 | 성적 관리 | 7 |

### E3: 실시간 세미나 (48 SP)
| Story ID | Story 명 | Task 수 |
|----------|----------|---------|
| E3-S1 | 세션 시작/참여 | 9 |
| E3-S2 | 화상 기능 | 7 |
| E3-S3 | 화면 공유 | 5 |
| E3-S4 | 채팅 | 6 |
| E3-S5 | 녹화/재생 | 6 |
| E3-S6 | 레이아웃 관리 | 5 |

### E4: 액티브 러닝 (44 SP)
| Story ID | Story 명 | Task 수 |
|----------|----------|---------|
| E4-S1 | 투표/설문 | 8 |
| E4-S2 | 퀴즈 | 9 |
| E4-S3 | 분반 토론 | 8 |
| E4-S4 | 화이트보드 | 6 |
| E4-S5 | 토론/지명 | 6 |

### E5: 평가 및 피드백 (28 SP)
| Story ID | Story 명 | Task 수 |
|----------|----------|---------|
| E5-S1 | 퀴즈 결과 | 5 |
| E5-S2 | AI 채점 | 4 |
| E5-S3 | 코드 평가 | 5 |
| E5-S4 | 동료 평가 | 5 |
| E5-S5 | 참여도 분석 | 4 |

### E6: 학습 분석 (21 SP)
| Story ID | Story 명 | Task 수 |
|----------|----------|---------|
| E6-S1 | 실시간 분석 | 5 |
| E6-S2 | 리포트 | 5 |
| E6-S3 | 조기 경보 | 5 |
| E6-S4 | 네트워크 분석 | 4 |

---

## 9. 변환 스크립트 사용법

### 9.1 사전 요구사항

```bash
# GitHub CLI 설치 확인
gh --version

# 인증 상태 확인
gh auth status

# jq 설치 (JSON 처리용)
# Ubuntu/Debian: sudo apt install jq
# macOS: brew install jq
```

### 9.2 실행 방법

```bash
# 1. 스크립트 실행 권한 부여
chmod +x scripts/csv-to-issues.sh
chmod +x scripts/create-labels.sh

# 2. Label 생성 (최초 1회)
./scripts/create-labels.sh

# 3. Issue 생성
./scripts/csv-to-issues.sh docs/09-git-issues-tasks.csv

# 4. 생성된 매핑 확인
cat scripts/issues-mapping.json
```

### 9.3 특정 Epic만 생성

```bash
# E1만 생성
./scripts/csv-to-issues.sh docs/09-git-issues-tasks.csv --epic E1

# P0 우선순위만 생성
./scripts/csv-to-issues.sh docs/09-git-issues-tasks.csv --priority P0
```

---

## 10. 주의사항

1. **순차 생성 필수**: 의존성 참조를 위해 순서대로 생성
2. **Rate Limit**: GitHub API 제한으로 대량 생성 시 딜레이 필요
3. **중복 방지**: 동일 Task ID 재생성 방지 로직 포함
4. **롤백**: 생성된 Issue 일괄 삭제 스크립트 별도 제공

---

## 11. 파일 구조

```
minerva/
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   └── task.md
│   └── labels.yml
├── docs/
│   ├── 09-git-issues-tasks.csv
│   └── 10-github-issues-workflow.md
└── scripts/
    ├── create-labels.sh
    ├── csv-to-issues.sh
    └── issues-mapping.json (생성됨)
```
