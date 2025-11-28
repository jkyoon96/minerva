# 07. 기능세분화 문서 (Feature Breakdown)

## 문서 정보
- **제품명**: EduForum
- **버전**: 1.0
- **작성일**: 2025-11-27
- **작성자**: Development Team
- **참조 문서**: 03-product-requirements.md (PRD)

---

## 목차
1. [개요](#1-개요)
2. [Epic 1: 사용자 인증 및 관리](#epic-1-사용자-인증-및-관리)
3. [Epic 2: 코스 관리](#epic-2-코스-관리)
4. [Epic 3: 실시간 세미나 (Live Session)](#epic-3-실시간-세미나-live-session)
5. [Epic 4: 액티브 러닝 도구](#epic-4-액티브-러닝-도구)
6. [Epic 5: 평가 및 피드백](#epic-5-평가-및-피드백)
7. [Epic 6: 학습 분석](#epic-6-학습-분석)
8. [우선순위 요약](#우선순위-요약)

---

## 1. 개요

### 1.1 문서 목적
본 문서는 PRD(제품요구기능서)를 기반으로 개발에 필요한 기능을 **Epic > Story > Task** 계층으로 분해하여 정의합니다.

### 1.2 용어 정의
- **Epic**: 대규모 기능 영역 (예: 사용자 인증)
- **Story**: 사용자 관점의 기능 요구사항 (User Story)
- **Task**: 개발 단위의 구체적 작업

### 1.3 우선순위 정의
| 우선순위 | 코드 | 설명 |
|---------|------|------|
| **Must Have** | P0 | MVP 필수 기능, 없으면 제품 출시 불가 |
| **Should Have** | P1 | 중요 기능, 출시 후 초기 버전에 포함 |
| **Could Have** | P2 | 있으면 좋은 기능, v2 이후 고려 |

### 1.4 Story Point 기준
| SP | 예상 시간 | 복잡도 |
|----|----------|--------|
| 1 | 2-4시간 | 단순 UI 변경, 설정 수정 |
| 2 | 0.5-1일 | 간단한 API, 컴포넌트 |
| 3 | 1-2일 | 중간 복잡도 기능 |
| 5 | 3-5일 | 복잡한 기능, 통합 필요 |
| 8 | 1-2주 | 매우 복잡, 연구 필요 |
| 13 | 2주+ | 대규모 기능, 분할 권장 |

---

## Epic 1: 사용자 인증 및 관리

> **Epic ID**: E1
> **Epic 설명**: 사용자 계정 생성, 인증, 권한 관리 시스템
> **우선순위**: P0 (Must Have)
> **PRD 참조**: F6.1, F6.2

---

### Story 1.1: 사용자 회원가입
**Story ID**: E1-S1
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 신규 사용자
> **I want to** 이메일과 비밀번호로 계정을 생성하고
> **So that** 플랫폼의 기능을 이용할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 이메일, 비밀번호, 이름을 입력하여 회원가입 가능
- [ ] AC2: 비밀번호는 8자 이상, 영문+숫자+특수문자 포함 검증
- [ ] AC3: 이메일 중복 체크 후 중복 시 오류 메시지 표시
- [ ] AC4: 회원가입 완료 후 이메일 인증 메일 발송
- [ ] AC5: 이메일 인증 링크 클릭 시 계정 활성화

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E1-S1-T1 | 회원가입 API 엔드포인트 개발 (POST /api/auth/register) | 4h | Backend |
| E1-S1-T2 | 비밀번호 해싱 및 검증 로직 구현 (bcrypt) | 2h | Backend |
| E1-S1-T3 | 이메일 중복 체크 API 개발 | 2h | Backend |
| E1-S1-T4 | 이메일 인증 토큰 생성 및 저장 로직 | 3h | Backend |
| E1-S1-T5 | 이메일 발송 서비스 통합 (SendGrid/SES) | 4h | Backend |
| E1-S1-T6 | 회원가입 폼 UI 컴포넌트 개발 | 4h | Frontend |
| E1-S1-T7 | 폼 유효성 검사 로직 구현 | 3h | Frontend |
| E1-S1-T8 | 이메일 인증 완료 페이지 개발 | 2h | Frontend |

---

### Story 1.2: 사용자 로그인
**Story ID**: E1-S2
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 등록된 사용자
> **I want to** 이메일과 비밀번호로 로그인하고
> **So that** 내 계정에 접근할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 이메일과 비밀번호 입력으로 로그인 가능
- [ ] AC2: 로그인 성공 시 JWT 토큰 발급 (Access + Refresh)
- [ ] AC3: 잘못된 자격 증명 시 적절한 오류 메시지 표시
- [ ] AC4: 5회 연속 실패 시 15분 잠금
- [ ] AC5: "로그인 상태 유지" 옵션 제공 (7일 vs 24시간)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E1-S2-T1 | 로그인 API 엔드포인트 개발 (POST /api/auth/login) | 4h | Backend |
| E1-S2-T2 | JWT 토큰 생성 및 검증 로직 구현 | 4h | Backend |
| E1-S2-T3 | Refresh Token 로직 구현 | 3h | Backend |
| E1-S2-T4 | 로그인 실패 카운터 및 잠금 로직 | 3h | Backend |
| E1-S2-T5 | 로그인 폼 UI 컴포넌트 개발 | 3h | Frontend |
| E1-S2-T6 | 토큰 저장 및 자동 갱신 로직 (axios interceptor) | 4h | Frontend |
| E1-S2-T7 | 로그인 상태 전역 관리 (Context/Redux) | 3h | Frontend |

---

### Story 1.3: OAuth 소셜 로그인
**Story ID**: E1-S3
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 사용자
> **I want to** Google 또는 Microsoft 계정으로 로그인하고
> **So that** 별도의 비밀번호 없이 편리하게 접속할 수 있다.

#### Acceptance Criteria
- [ ] AC1: Google OAuth 2.0 로그인 지원
- [ ] AC2: Microsoft OAuth 2.0 로그인 지원
- [ ] AC3: 최초 OAuth 로그인 시 자동 계정 생성
- [ ] AC4: 기존 이메일과 동일 시 계정 연동 옵션 제공
- [ ] AC5: OAuth 제공자 프로필 사진 자동 연동

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E1-S3-T1 | Google OAuth 통합 (클라이언트 ID 설정) | 4h | Backend |
| E1-S3-T2 | Microsoft OAuth 통합 | 4h | Backend |
| E1-S3-T3 | OAuth 콜백 처리 및 사용자 생성/연동 로직 | 4h | Backend |
| E1-S3-T4 | 소셜 로그인 버튼 UI 컴포넌트 | 2h | Frontend |
| E1-S3-T5 | OAuth 리다이렉트 처리 로직 | 3h | Frontend |

---

### Story 1.4: 2단계 인증 (2FA)
**Story ID**: E1-S4
**우선순위**: P1
**Story Points**: 5

#### User Story
> **As a** 보안 의식이 높은 사용자
> **I want to** 2단계 인증을 활성화하고
> **So that** 계정을 더욱 안전하게 보호할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 사용자 설정에서 2FA 활성화/비활성화 가능
- [ ] AC2: TOTP 앱(Google Authenticator 등) 지원
- [ ] AC3: QR 코드 스캔으로 간편 설정
- [ ] AC4: 백업 코드 10개 생성 및 다운로드
- [ ] AC5: 2FA 활성화된 계정은 로그인 시 코드 입력 필수

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E1-S4-T1 | TOTP 시크릿 생성 및 저장 로직 | 3h | Backend |
| E1-S4-T2 | QR 코드 생성 API | 2h | Backend |
| E1-S4-T3 | TOTP 코드 검증 로직 | 2h | Backend |
| E1-S4-T4 | 백업 코드 생성 및 저장 | 2h | Backend |
| E1-S4-T5 | 2FA 설정 UI 개발 | 4h | Frontend |
| E1-S4-T6 | 2FA 코드 입력 화면 개발 | 3h | Frontend |

---

### Story 1.5: 비밀번호 재설정
**Story ID**: E1-S5
**우선순위**: P0
**Story Points**: 3

#### User Story
> **As a** 비밀번호를 잊은 사용자
> **I want to** 이메일로 비밀번호 재설정 링크를 받고
> **So that** 새 비밀번호로 계정에 접근할 수 있다.

#### Acceptance Criteria
- [ ] AC1: "비밀번호 찾기" 버튼으로 이메일 입력
- [ ] AC2: 등록된 이메일로 재설정 링크 발송 (유효기간 1시간)
- [ ] AC3: 링크 클릭 시 새 비밀번호 입력 화면
- [ ] AC4: 새 비밀번호 설정 완료 후 기존 세션 모두 로그아웃
- [ ] AC5: 존재하지 않는 이메일도 동일한 응답 (보안)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E1-S5-T1 | 비밀번호 재설정 토큰 생성 API | 2h | Backend |
| E1-S5-T2 | 재설정 이메일 발송 로직 | 2h | Backend |
| E1-S5-T3 | 새 비밀번호 설정 API | 2h | Backend |
| E1-S5-T4 | 비밀번호 찾기 UI 개발 | 2h | Frontend |
| E1-S5-T5 | 새 비밀번호 설정 UI 개발 | 2h | Frontend |

---

### Story 1.6: 역할 기반 접근 제어 (RBAC)
**Story ID**: E1-S6
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 시스템 관리자
> **I want to** 사용자에게 역할(Admin, Instructor, TA, Student)을 부여하고
> **So that** 역할에 맞는 기능만 접근할 수 있도록 제어한다.

#### Acceptance Criteria
- [ ] AC1: 4가지 역할 정의: Admin, Instructor, TA, Student
- [ ] AC2: 역할별 권한 매트릭스 정의 및 적용
- [ ] AC3: Admin은 모든 기능 접근 가능
- [ ] AC4: Instructor는 코스 생성/관리, 세션 진행 가능
- [ ] AC5: TA는 Instructor가 지정한 코스에서 보조 역할
- [ ] AC6: Student는 등록된 코스의 세션 참여만 가능
- [ ] AC7: 권한 없는 페이지 접근 시 403 오류 표시

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E1-S6-T1 | 역할 및 권한 데이터베이스 스키마 설계 | 3h | Backend |
| E1-S6-T2 | 권한 검증 미들웨어 개발 | 4h | Backend |
| E1-S6-T3 | API 엔드포인트별 권한 적용 | 4h | Backend |
| E1-S6-T4 | 역할 관리 Admin UI 개발 | 4h | Frontend |
| E1-S6-T5 | 역할 기반 라우팅 가드 구현 | 3h | Frontend |
| E1-S6-T6 | 권한 오류 페이지 (403) 개발 | 1h | Frontend |

---

### Story 1.7: 사용자 프로필 관리
**Story ID**: E1-S7
**우선순위**: P1
**Story Points**: 3

#### User Story
> **As a** 사용자
> **I want to** 내 프로필(이름, 사진, 연락처)을 수정하고
> **So that** 다른 사람들에게 나를 올바르게 표시할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 프로필 조회 및 수정 페이지 제공
- [ ] AC2: 프로필 사진 업로드 (JPEG/PNG, 최대 2MB)
- [ ] AC3: 이름, 소속 기관, 자기소개 수정 가능
- [ ] AC4: 이메일 변경 시 재인증 필요
- [ ] AC5: 비밀번호 변경 기능 (현재 비밀번호 확인)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E1-S7-T1 | 프로필 조회/수정 API 개발 | 3h | Backend |
| E1-S7-T2 | 프로필 사진 업로드 API (S3 연동) | 4h | Backend |
| E1-S7-T3 | 이메일 변경 검증 로직 | 2h | Backend |
| E1-S7-T4 | 프로필 페이지 UI 개발 | 4h | Frontend |
| E1-S7-T5 | 이미지 업로드 컴포넌트 개발 | 3h | Frontend |

---

## Epic 2: 코스 관리

> **Epic ID**: E2
> **Epic 설명**: 대시보드, 코스 생성, 학생 등록, 세션 스케줄링, 콘텐츠 관리
> **우선순위**: P0 (Must Have)
> **PRD 참조**: F5.1, F5.2, F5.3, F5.4, F5.5

---

### Story 2.0: 교수 대시보드
**Story ID**: E2-S0
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 로그인 후 통합 대시보드에서 오늘 일정, 알림, 주요 지표를 한눈에 확인하여
> **So that** 빠르게 현재 상황을 파악하고 필요한 작업을 시작할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 오늘 예정된 세션 목록 표시 (LIVE/예정/완료 상태)
- [ ] AC2: 통계 요약 카드 (진행 중 코스, 총 수강생, 채점 대기, 주의 학생)
- [ ] AC3: 최근 알림 목록 (결석, 과제 마감, 참여도 이상 등)
- [ ] AC4: 채점 대기 중인 과제 목록 및 바로가기
- [ ] AC5: 학업 위험 학생 목록 (조기 경보 연동)
- [ ] AC6: 각 섹션에서 상세 페이지로 이동 가능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S0-T1 | 대시보드 데이터 집계 API (오늘 세션, 통계) | 4h | Backend |
| E2-S0-T2 | 알림 조회 API (최근 알림) | 2h | Backend |
| E2-S0-T3 | 채점 대기 과제 목록 API | 2h | Backend |
| E2-S0-T4 | 위험 학생 목록 API (조기 경보 연동) | 2h | Backend |
| E2-S0-T5 | 대시보드 레이아웃 및 통계 카드 UI | 4h | Frontend |
| E2-S0-T6 | 오늘 일정 섹션 UI | 3h | Frontend |
| E2-S0-T7 | 알림 섹션 UI | 2h | Frontend |
| E2-S0-T8 | 채점 대기/위험 학생 섹션 UI | 3h | Frontend |

---

### Story 2.0.1: 학생 대시보드
**Story ID**: E2-S0-S
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 학생
> **I want to** 로그인 후 통합 대시보드에서 오늘 일정, 마감 임박 과제, 참여도를 한눈에 확인하여
> **So that** 빠르게 현재 상황을 파악하고 수업 준비를 할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 오늘 예정된 세션 목록 표시 (LIVE/예정/완료 상태)
- [ ] AC2: 통계 요약 카드 (수강 중 코스, 평균 참여도, 제출 대기 과제, 평균 성적)
- [ ] AC3: 마감 임박 과제/퀴즈 목록 (긴급/주의/일반 구분)
- [ ] AC4: 이번 주 참여도 지표 (발언 시간, 투표 참여, 채팅 참여)
- [ ] AC5: 최근 성적 목록 및 바로가기
- [ ] AC6: 빠른 접근 버튼 (LIVE 세션, 과제 제출, 학습 리포트, 녹화 다시보기)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S0S-T1 | 학생 대시보드 데이터 집계 API (오늘 세션, 통계) | 4h | Backend |
| E2-S0S-T2 | 마감 임박 과제/퀴즈 목록 API | 2h | Backend |
| E2-S0S-T3 | 주간 참여도 집계 API | 3h | Backend |
| E2-S0S-T4 | 최근 성적 목록 API | 2h | Backend |
| E2-S0S-T5 | 학생 대시보드 레이아웃 및 통계 카드 UI | 4h | Frontend |
| E2-S0S-T6 | 오늘 일정 섹션 UI | 3h | Frontend |
| E2-S0S-T7 | 마감 임박/참여도/최근 성적 섹션 UI | 4h | Frontend |

---

### Story 2.0.2: 학생 코스 목록
**Story ID**: E2-S0-SL
**우선순위**: P0
**Story Points**: 3

#### User Story
> **As a** 학생
> **I want to** 수강 중인 코스 목록을 확인하고 진도를 파악하여
> **So that** 효율적으로 학습 일정을 관리할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 수강 중/완료 코스 분류 표시
- [ ] AC2: 코스별 진도율 표시 (주차 기준)
- [ ] AC3: LIVE 세션 상태 표시 및 바로 입장 버튼
- [ ] AC4: 다음 세션 일정 표시
- [ ] AC5: 완료된 코스의 최종 성적 표시
- [ ] AC6: 초대 코드로 신규 코스 등록 기능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S0SL-T1 | 학생 코스 목록 API (진도, 상태 포함) | 3h | Backend |
| E2-S0SL-T2 | 초대 코드 검증 및 등록 API | 2h | Backend |
| E2-S0SL-T3 | 학생 코스 목록 UI (카드 그리드) | 4h | Frontend |
| E2-S0SL-T4 | 진도 표시 및 코스 등록 모달 UI | 3h | Frontend |

#### 관련 화면
- `crs-001-course-list-student.html` - 학생 코스 목록 화면

---

### Story 2.0.3: 과제 제출
**Story ID**: E2-S0-AS
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 학생
> **I want to** 과제 파일을 업로드하고 제출하여
> **So that** 기한 내에 과제를 완료할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 파일 드래그 앤 드롭 또는 클릭 업로드
- [ ] AC2: 마감 시간까지 남은 시간 표시 (긴급/주의 구분)
- [ ] AC3: 평가 기준(루브릭) 표시
- [ ] AC4: 제출 전 확인 모달 (표절 확인 동의)
- [ ] AC5: 임시 저장 기능
- [ ] AC6: 남은 제출 횟수 표시 및 재제출 지원
- [ ] AC7: 첨부 자료(과제 안내서 등) 다운로드

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S0AS-T1 | 과제 제출 API (파일 업로드) | 4h | Backend |
| E2-S0AS-T2 | 임시 저장 API | 2h | Backend |
| E2-S0AS-T3 | 제출 횟수 관리 로직 | 2h | Backend |
| E2-S0AS-T4 | 파일 업로드 UI (드래그앤드롭) | 4h | Frontend |
| E2-S0AS-T5 | 과제 상세 및 루브릭 표시 UI | 3h | Frontend |
| E2-S0AS-T6 | 제출 확인 모달 및 성공 페이지 UI | 2h | Frontend |

---

### Story 2.0.4: 학생 코스 상세
**Story ID**: E2-S0-CD
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 학생
> **I want to** 수강 중인 코스의 상세 정보와 진도를 확인하여
> **So that** 학습 진행 상황을 파악하고 필요한 활동에 참여할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 코스 개요 (제목, 교수, 학기, 일정) 표시
- [ ] AC2: 내 진도 현황 (출석률, 과제 제출, 퀴즈 완료) 표시
- [ ] AC3: 현재 점수 및 예상 등급 표시
- [ ] AC4: LIVE 세션 상태 및 바로 입장 버튼
- [ ] AC5: 다가오는 세션 목록 표시
- [ ] AC6: 미제출 과제 및 마감 임박 알림
- [ ] AC7: 녹화 영상 목록 및 시청 기능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S0CD-T1 | 학생 코스 상세 API (진도, 성적 요약) | 4h | Backend |
| E2-S0CD-T2 | 학생 세션/과제 현황 API | 3h | Backend |
| E2-S0CD-T3 | 코스 상세 UI (Hero, 진도 카드) | 4h | Frontend |
| E2-S0CD-T4 | 세션/과제/녹화 목록 컴포넌트 | 4h | Frontend |

#### 관련 화면
- `crs-003-course-detail-student.html` - 학생 코스 상세 화면

---

### Story 2.1: 코스 생성 및 설정
**Story ID**: E2-S1
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 새 코스를 생성하고 기본 정보를 설정하여
> **So that** 학생들이 등록할 수 있는 강의를 개설할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 코스 생성 폼 (제목, 설명, 학기, 시작/종료일)
- [ ] AC2: 코스 코드 자동 생성 (예: CS101-2025-F)
- [ ] AC3: 코스 썸네일 이미지 업로드
- [ ] AC4: 평가 기준 설정 (참여도, 퀴즈, 과제 가중치)
- [ ] AC5: 코스 공개/비공개 설정
- [ ] AC6: TA 지정 기능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S1-T1 | 코스 CRUD API 개발 | 4h | Backend |
| E2-S1-T2 | 코스 코드 생성 로직 | 1h | Backend |
| E2-S1-T3 | 평가 기준 스키마 설계 및 저장 | 3h | Backend |
| E2-S1-T4 | TA 배정 API 개발 | 2h | Backend |
| E2-S1-T5 | 코스 생성 폼 UI 개발 | 4h | Frontend |
| E2-S1-T6 | 코스 설정 페이지 UI 개발 | 4h | Frontend |
| E2-S1-T7 | 코스 목록 및 카드 컴포넌트 개발 | 3h | Frontend |

---

### Story 2.1.1: 코스 설정 수정
**Story ID**: E2-S1-1
**우선순위**: P0
**Story Points**: 3

#### User Story
> **As a** 교수
> **I want to** 기존 코스의 설정을 수정하고 관리하여
> **So that** 학기 중에도 코스 정보를 최신 상태로 유지할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 코스 기본 정보 수정 (제목, 설명, 학기)
- [ ] AC2: 일정 설정 수정 (시작/종료일, 정규 수업 시간)
- [ ] AC3: 수강생 설정 수정 (최대 인원, 등록 방식)
- [ ] AC4: 초대 코드 관리 (복사, 재생성)
- [ ] AC5: 평가 기준 비중 조절
- [ ] AC6: 조교 추가/제거 관리
- [ ] AC7: 코스 보관 및 삭제 (위험 영역)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S1-1-T1 | 코스 설정 조회/수정 API | 3h | Backend |
| E2-S1-1-T2 | 초대 코드 재생성 API | 1h | Backend |
| E2-S1-1-T3 | 코스 보관/삭제 API | 2h | Backend |
| E2-S1-1-T4 | 코스 설정 페이지 UI (섹션별 폼) | 4h | Frontend |
| E2-S1-1-T5 | 위험 영역 UI 및 확인 모달 | 2h | Frontend |

---

### Story 2.2: 학생 일괄 등록
**Story ID**: E2-S2
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** CSV 파일로 학생들을 일괄 등록하고
> **So that** 학기 시작 전 빠르게 수강생을 구성할 수 있다.

#### Acceptance Criteria
- [ ] AC1: CSV 파일 업로드 (이름, 이메일, 학번)
- [ ] AC2: CSV 템플릿 다운로드 제공
- [ ] AC3: 파일 파싱 및 유효성 검사 (이메일 형식, 중복 체크)
- [ ] AC4: 오류 있는 행 표시 및 수정 기회
- [ ] AC5: 등록 완료된 학생에게 초대 이메일 자동 발송
- [ ] AC6: 기존 사용자는 코스에 자동 추가, 신규는 계정 생성

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S2-T1 | CSV 파싱 및 검증 로직 개발 | 4h | Backend |
| E2-S2-T2 | 일괄 사용자 생성/등록 API | 4h | Backend |
| E2-S2-T3 | 초대 이메일 발송 큐 처리 | 3h | Backend |
| E2-S2-T4 | CSV 업로드 UI 컴포넌트 | 3h | Frontend |
| E2-S2-T5 | 검증 결과 미리보기 UI | 3h | Frontend |
| E2-S2-T6 | 오류 표시 및 수정 UI | 2h | Frontend |

---

### Story 2.3: 초대 링크로 학생 등록
**Story ID**: E2-S3
**우선순위**: P0
**Story Points**: 3

#### User Story
> **As a** 교수
> **I want to** 코스 초대 링크를 공유하여
> **So that** 학생들이 스스로 코스에 등록할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 고유 초대 링크 생성 (코스별)
- [ ] AC2: 초대 링크 만료일 설정 가능
- [ ] AC3: 링크 클릭 시 로그인/회원가입 후 자동 등록
- [ ] AC4: 최대 등록 인원 제한 설정 (선택)
- [ ] AC5: 초대 링크 재생성/무효화 기능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S3-T1 | 초대 링크 생성/검증 API | 3h | Backend |
| E2-S3-T2 | 링크 만료 및 인원 제한 로직 | 2h | Backend |
| E2-S3-T3 | 초대 링크 자동 등록 처리 | 2h | Backend |
| E2-S3-T4 | 초대 링크 관리 UI | 2h | Frontend |
| E2-S3-T5 | 초대 수락 플로우 UI | 2h | Frontend |

---

### Story 2.4: 세션 스케줄링
**Story ID**: E2-S4
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 정기 세션 일정을 등록하고 관리하여
> **So that** 학생들이 수업 일정을 확인하고 참석할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 세션 생성 (제목, 설명, 날짜/시간, 길이)
- [ ] AC2: 반복 일정 설정 (매주 월/수 10:00-11:30)
- [ ] AC3: 캘린더 뷰로 일정 확인
- [ ] AC4: 세션 시작 전 알림 (이메일, 앱 내 알림)
- [ ] AC5: iCal/Google Calendar 내보내기

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S4-T1 | 세션 CRUD API 개발 | 4h | Backend |
| E2-S4-T2 | 반복 일정 생성 로직 (rrule) | 4h | Backend |
| E2-S4-T3 | 세션 알림 스케줄러 개발 | 4h | Backend |
| E2-S4-T4 | iCal 내보내기 API | 2h | Backend |
| E2-S4-T5 | 캘린더 뷰 UI 컴포넌트 (react-big-calendar) | 4h | Frontend |
| E2-S4-T6 | 세션 생성/수정 모달 개발 | 3h | Frontend |
| E2-S4-T7 | 알림 표시 컴포넌트 개발 | 2h | Frontend |

---

### Story 2.5: 콘텐츠 라이브러리
**Story ID**: E2-S5
**우선순위**: P1
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 코스 자료(PDF, PPT, 비디오)를 업로드하고 관리하여
> **So that** 학생들이 필요한 학습 자료에 접근할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 파일 업로드 (PDF, PPT, DOC, 비디오 등)
- [ ] AC2: 폴더 구조로 자료 정리
- [ ] AC3: 파일 크기 제한 (100MB per file)
- [ ] AC4: 학생에게 선택적 공개/비공개 설정
- [ ] AC5: 파일 검색 기능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S5-T1 | 파일 업로드 API (S3 연동) | 4h | Backend |
| E2-S5-T2 | 폴더 구조 CRUD API | 3h | Backend |
| E2-S5-T3 | 파일 권한 관리 로직 | 2h | Backend |
| E2-S5-T4 | 파일 검색 API (Elasticsearch) | 4h | Backend |
| E2-S5-T5 | 콘텐츠 라이브러리 UI 개발 | 6h | Frontend |
| E2-S5-T6 | 드래그 앤 드롭 업로드 컴포넌트 | 3h | Frontend |

---

### Story 2.6: 과제 관리
**Story ID**: E2-S6
**우선순위**: P1
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 과제를 출제하고 학생 제출물을 관리하여
> **So that** 체계적으로 학습 과제를 운영할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 과제 생성 (제목, 설명, 마감일, 배점)
- [ ] AC2: 파일 제출 (다중 파일 지원)
- [ ] AC3: 늦은 제출 허용/패널티 설정
- [ ] AC4: 재제출 허용 횟수 설정
- [ ] AC5: 제출 현황 대시보드 (교수용)
- [ ] AC6: 채점 및 피드백 기능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S6-T1 | 과제 CRUD API 개발 | 4h | Backend |
| E2-S6-T2 | 과제 제출 API 개발 | 4h | Backend |
| E2-S6-T3 | 늦은 제출 및 재제출 로직 | 3h | Backend |
| E2-S6-T4 | 채점 및 피드백 API | 3h | Backend |
| E2-S6-T5 | 과제 목록 및 상세 UI | 4h | Frontend |
| E2-S6-T6 | 과제 제출 UI (학생용) | 3h | Frontend |
| E2-S6-T7 | 채점 UI (교수용) | 4h | Frontend |

---

### Story 2.7: 성적 관리
**Story ID**: E2-S7
**우선순위**: P1
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 참여도, 퀴즈, 과제 점수를 통합 관리하여
> **So that** 학기말 최종 성적을 산출할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 성적 자동 계산 (참여도 + 퀴즈 + 과제)
- [ ] AC2: 가중치 커스터마이징
- [ ] AC3: 학생별 성적 상세 조회
- [ ] AC4: 성적 내보내기 (Excel, CSV)
- [ ] AC5: 학생 본인의 성적 확인 가능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E2-S7-T1 | 성적 계산 로직 개발 | 4h | Backend |
| E2-S7-T2 | 가중치 설정 API | 2h | Backend |
| E2-S7-T3 | 성적 조회 API (교수/학생) | 3h | Backend |
| E2-S7-T4 | 성적 내보내기 기능 | 3h | Backend |
| E2-S7-T5 | 성적 관리 대시보드 UI | 5h | Frontend |
| E2-S7-T6 | 학생용 성적 확인 UI | 3h | Frontend |

---

## Epic 3: 실시간 세미나 (Live Session)

> **Epic ID**: E3
> **Epic 설명**: WebRTC 기반 화상 회의, 화면 공유, 채팅, 녹화 기능
> **우선순위**: P0 (Must Have)
> **PRD 참조**: F1.1, F1.2, F1.3, F1.4, F1.5

---

### Story 3.1: 세미나 룸 생성 및 입장
**Story ID**: E3-S1
**우선순위**: P0
**Story Points**: 8

#### User Story
> **As a** 교수
> **I want to** 예정된 세미나를 시작하여 학생들이 입장할 수 있도록 하고
> **So that** 원활하게 온라인 수업을 시작할 수 있다.

#### Acceptance Criteria
- [ ] AC1: "세미나 시작" 버튼 클릭 시 3초 이내 룸 생성
- [ ] AC2: 학생들에게 실시간 알림 발송
- [ ] AC3: 학생은 링크 클릭으로 즉시 입장 (별도 로그인 불필요)
- [ ] AC4: 입장 시 카메라/마이크 선택 프리뷰
- [ ] AC5: 대기실 기능 (선택) - 교수 승인 후 입장

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E3-S1-T1 | WebRTC 미디어 서버 설정 (Jitsi/mediasoup) | 8h | Backend |
| E3-S1-T2 | 세미나 룸 생성 API | 4h | Backend |
| E3-S1-T3 | 실시간 알림 발송 (WebSocket) | 4h | Backend |
| E3-S1-T4 | 대기실 로직 구현 | 3h | Backend |
| E3-S1-T5 | 세미나 입장 UI 개발 | 4h | Frontend |
| E3-S1-T6 | 미디어 장치 선택/프리뷰 컴포넌트 | 4h | Frontend |
| E3-S1-T7 | 대기실 UI 개발 | 2h | Frontend |

---

### Story 3.1.1: 학생 라이브 세션 참여
**Story ID**: E3-S1-SV
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 학생
> **I want to** 라이브 세션에 참여하여 교수의 강의를 듣고 상호작용하며
> **So that** 실시간으로 학습에 참여할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 메인 비디오 영역에 발표자(교수) 화면 표시
- [ ] AC2: 참여자 목록 및 상태 표시
- [ ] AC3: 마이크/카메라 토글 버튼
- [ ] AC4: 손들기 기능으로 발언 요청
- [ ] AC5: 실시간 채팅 패널 (질문, 의견)
- [ ] AC6: 활동 패널 (진행 중인 투표/퀴즈 참여)
- [ ] AC7: 이모지 반응 기능
- [ ] AC8: 세션 나가기 확인 모달

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E3-S1SV-T1 | 학생 뷰 WebRTC 연결 로직 | 4h | Backend |
| E3-S1SV-T2 | 손들기/반응 실시간 이벤트 처리 | 3h | Backend |
| E3-S1SV-T3 | 학생 라이브 뷰 메인 레이아웃 | 4h | Frontend |
| E3-S1SV-T4 | 채팅/참여자/활동 탭 패널 | 4h | Frontend |
| E3-S1SV-T5 | 컨트롤 바 (마이크, 카메라, 손들기) | 3h | Frontend |

#### 관련 화면
- `live-004-student-view.html` - 학생 라이브 세션 화면

---

### Story 3.2: 비디오/오디오 참여
**Story ID**: E3-S2
**우선순위**: P0
**Story Points**: 8

#### User Story
> **As a** 참가자
> **I want to** 비디오와 오디오를 켜고 끄며 참여하고
> **So that** 상황에 맞게 미디어를 조절할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 카메라/마이크 on/off 토글 버튼
- [ ] AC2: HD 비디오 품질 (720p+)
- [ ] AC3: 네트워크 속도에 따른 자동 품질 조정
- [ ] AC4: 발언 시 비디오 하이라이트 표시
- [ ] AC5: 최대 50명 동시 참여 지원
- [ ] AC6: 지연시간 300ms 이내

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E3-S2-T1 | WebRTC 피어 연결 관리 로직 | 8h | Backend |
| E3-S2-T2 | SFU 아키텍처 구현 | 8h | Backend |
| E3-S2-T3 | 대역폭 적응형 스트리밍 로직 | 4h | Backend |
| E3-S2-T4 | 비디오 그리드 레이아웃 컴포넌트 | 6h | Frontend |
| E3-S2-T5 | 미디어 컨트롤 버튼 UI | 3h | Frontend |
| E3-S2-T6 | 발언자 감지 및 하이라이트 | 3h | Frontend |

---

### Story 3.3: 화면 공유
**Story ID**: E3-S3
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 발표자
> **I want to** 내 화면을 공유하여
> **So that** 참가자들에게 자료를 보여줄 수 있다.

#### Acceptance Criteria
- [ ] AC1: 전체 화면 / 특정 창 / 브라우저 탭 선택 공유
- [ ] AC2: 화면 공유 중 발표자 비디오 PIP 표시
- [ ] AC3: 화면 공유 해상도 최적화 (1080p)
- [ ] AC4: 화면 공유 중 주석 도구 (선택)
- [ ] AC5: 동시에 1명만 화면 공유 가능 (교수가 권한 부여)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E3-S3-T1 | 화면 공유 스트림 처리 로직 | 4h | Backend |
| E3-S3-T2 | 화면 공유 권한 관리 | 2h | Backend |
| E3-S3-T3 | getDisplayMedia API 통합 | 3h | Frontend |
| E3-S3-T4 | 화면 공유 UI (선택 다이얼로그) | 3h | Frontend |
| E3-S3-T5 | PIP 비디오 컴포넌트 | 2h | Frontend |
| E3-S3-T6 | 화면 공유 뷰 레이아웃 | 2h | Frontend |

---

### Story 3.4: 실시간 채팅
**Story ID**: E3-S4
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 참가자
> **I want to** 세션 중 텍스트 채팅으로 소통하고
> **So that** 음성 외에 다른 방법으로도 의견을 나눌 수 있다.

#### Acceptance Criteria
- [ ] AC1: 전체 채팅 기능
- [ ] AC2: 개인 메시지 (DM) 기능
- [ ] AC3: 파일 공유 (이미지, 문서 - 최대 10MB)
- [ ] AC4: 이모지 반응 지원
- [ ] AC5: 채팅 기록 저장 및 다운로드
- [ ] AC6: 채팅 검색 기능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E3-S4-T1 | 채팅 메시지 WebSocket 서버 구현 | 4h | Backend |
| E3-S4-T2 | 채팅 메시지 저장 로직 | 2h | Backend |
| E3-S4-T3 | 파일 업로드/다운로드 API | 3h | Backend |
| E3-S4-T4 | 채팅 UI 컴포넌트 개발 | 4h | Frontend |
| E3-S4-T5 | DM 기능 구현 | 2h | Frontend |
| E3-S4-T6 | 이모지 피커 통합 | 2h | Frontend |
| E3-S4-T7 | 채팅 검색 UI | 2h | Frontend |

---

### Story 3.5: 손들기 및 반응
**Story ID**: E3-S5
**우선순위**: P0
**Story Points**: 3

#### User Story
> **As a** 학생
> **I want to** 손들기 버튼으로 발언 의사를 표시하고
> **So that** 교수가 나를 지목하여 발언 기회를 얻을 수 있다.

#### Acceptance Criteria
- [ ] AC1: 손들기 버튼 - 클릭 시 큐에 추가
- [ ] AC2: 교수가 손든 학생 목록 확인 (시간순)
- [ ] AC3: 빠른 반응 버튼 (👍 👏 ❓ 🎉 등)
- [ ] AC4: 손든 학생의 비디오에 아이콘 표시
- [ ] AC5: 교수가 손든 학생 클릭 시 발언 지목

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E3-S5-T1 | 손들기 상태 관리 API | 2h | Backend |
| E3-S5-T2 | 반응 이벤트 브로드캐스트 | 2h | Backend |
| E3-S5-T3 | 손들기 버튼 및 큐 UI | 3h | Frontend |
| E3-S5-T4 | 반응 버튼 UI | 2h | Frontend |
| E3-S5-T5 | 손든 학생 목록 (교수용) | 2h | Frontend |

---

### Story 3.6: 세션 녹화
**Story ID**: E3-S6
**우선순위**: P1
**Story Points**: 8

#### User Story
> **As a** 교수
> **I want to** 세션을 녹화하여
> **So that** 결석한 학생이 다시보기로 수업을 들을 수 있다.

#### Acceptance Criteria
- [ ] AC1: 원클릭 녹화 시작/종료
- [ ] AC2: 비디오, 오디오, 화면 공유, 채팅 모두 녹화
- [ ] AC3: 클라우드 저장 및 자동 인코딩
- [ ] AC4: 녹화본 다시보기 인터페이스
- [ ] AC5: 자막 자동 생성 (음성인식)
- [ ] AC6: 녹화본 다운로드 기능

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E3-S6-T1 | 녹화 서버 구성 (Jibri/custom) | 8h | Backend |
| E3-S6-T2 | 녹화 시작/종료 API | 3h | Backend |
| E3-S6-T3 | 비디오 인코딩 파이프라인 (FFmpeg) | 6h | Backend |
| E3-S6-T4 | 클라우드 스토리지 연동 (S3) | 3h | Backend |
| E3-S6-T5 | 자막 생성 (Whisper/Google STT) | 6h | Backend |
| E3-S6-T6 | 녹화 컨트롤 UI | 2h | Frontend |
| E3-S6-T7 | 녹화본 다시보기 플레이어 | 6h | Frontend |

---

### Story 3.7: 레이아웃 모드
**Story ID**: E3-S7
**우선순위**: P1
**Story Points**: 3

#### User Story
> **As a** 참가자
> **I want to** 비디오 레이아웃을 상황에 맞게 변경하고
> **So that** 발표 시에는 발표자 중심으로, 토론 시에는 그리드로 볼 수 있다.

#### Acceptance Criteria
- [ ] AC1: 그리드 뷰 (모든 참가자 균등 표시)
- [ ] AC2: 발표자 뷰 (발표자 크게, 나머지 작게)
- [ ] AC3: 스포트라이트 뷰 (특정 인원만 강조)
- [ ] AC4: 교수가 전체 참가자 레이아웃 강제 변경 가능
- [ ] AC5: 개인별 레이아웃 선호도 저장

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E3-S7-T1 | 레이아웃 상태 동기화 API | 2h | Backend |
| E3-S7-T2 | 레이아웃 강제 변경 브로드캐스트 | 2h | Backend |
| E3-S7-T3 | 그리드 뷰 컴포넌트 | 3h | Frontend |
| E3-S7-T4 | 발표자 뷰 컴포넌트 | 3h | Frontend |
| E3-S7-T5 | 레이아웃 선택 UI | 2h | Frontend |

---

## Epic 4: 액티브 러닝 도구

> **Epic ID**: E4
> **Epic 설명**: 투표, 퀴즈, 분반, 화이트보드 등 능동적 학습 도구
> **우선순위**: P0 (Must Have)
> **PRD 참조**: F2.1, F2.2, F2.3, F2.4, F2.6

---

### Story 4.1: 실시간 투표 생성
**Story ID**: E4-S1
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 수업 중 즉석 투표를 생성하여
> **So that** 학생들의 의견이나 이해도를 실시간으로 파악할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 투표 생성 30초 이내 완료
- [ ] AC2: 문제 유형: 객관식, 다중 선택, 주관식, O/X
- [ ] AC3: 익명 vs 기명 선택
- [ ] AC4: 시간 제한 설정 (선택)
- [ ] AC5: 사전 저장된 투표 템플릿 재사용

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E4-S1-T1 | 투표 CRUD API 개발 | 4h | Backend |
| E4-S1-T2 | 투표 타입별 스키마 설계 | 2h | Backend |
| E4-S1-T3 | 투표 시작/종료 실시간 이벤트 | 3h | Backend |
| E4-S1-T4 | 투표 생성 폼 UI | 4h | Frontend |
| E4-S1-T5 | 투표 타입별 입력 컴포넌트 | 3h | Frontend |
| E4-S1-T6 | 템플릿 저장/불러오기 UI | 2h | Frontend |

---

### Story 4.2: 투표 참여 및 결과 확인
**Story ID**: E4-S2
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 학생
> **I want to** 교수가 시작한 투표에 응답하고 결과를 확인하여
> **So that** 수업에 능동적으로 참여할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 투표 시작 시 즉시 팝업 표시
- [ ] AC2: 응답률 실시간 업데이트 (50% → 80% → 100%)
- [ ] AC3: 결과를 차트로 시각화 (막대, 파이, 워드클라우드)
- [ ] AC4: 투표 종료 후 정답 및 해설 표시
- [ ] AC5: 투표 기록 저장 (참여 추적용)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E4-S2-T1 | 투표 응답 저장 API | 2h | Backend |
| E4-S2-T2 | 실시간 응답률 브로드캐스트 | 2h | Backend |
| E4-S2-T3 | 투표 결과 집계 API | 2h | Backend |
| E4-S2-T4 | 투표 응답 UI (학생용) | 3h | Frontend |
| E4-S2-T5 | 결과 차트 컴포넌트 (Chart.js) | 4h | Frontend |
| E4-S2-T6 | 정답/해설 표시 UI | 2h | Frontend |

---

### Story 4.3: 퀴즈 문제 관리
**Story ID**: E4-S3
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 문제 은행을 관리하고 퀴즈를 출제하여
> **So that** 학생들의 학습 상태를 평가할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 문제 유형: 객관식, 주관식, 참/거짓, 코딩
- [ ] AC2: 문제 은행(Question Bank) CRUD
- [ ] AC3: 문제 태그 및 카테고리 분류
- [ ] AC4: 난이도 설정 (상/중/하)
- [ ] AC5: 문제 가져오기/내보내기 (CSV, QTI)
- [ ] AC6: 문제 미리보기

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E4-S3-T1 | 문제 CRUD API 개발 | 4h | Backend |
| E4-S3-T2 | 태그 및 카테고리 관리 API | 2h | Backend |
| E4-S3-T3 | 문제 가져오기/내보내기 로직 | 4h | Backend |
| E4-S3-T4 | 문제 은행 UI 개발 | 4h | Frontend |
| E4-S3-T5 | 문제 유형별 에디터 | 4h | Frontend |
| E4-S3-T6 | 문제 미리보기 컴포넌트 | 2h | Frontend |

---

### Story 4.4: 퀴즈 진행
**Story ID**: E4-S4
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 학생
> **I want to** 퀴즈에 참여하고 즉시 결과를 확인하여
> **So that** 내 이해도를 확인하고 부족한 부분을 파악할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 퀴즈 시작 시 전체 화면 모드
- [ ] AC2: 문제별 시간 제한 표시
- [ ] AC3: 객관식 즉시 자동 채점 (<1초)
- [ ] AC4: 정답/오답 표시 및 해설 제공
- [ ] AC5: 틀린 문제 관련 학습 자료 추천
- [ ] AC6: 퀴즈 완료 후 점수 요약

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E4-S4-T1 | 퀴즈 세션 관리 API | 4h | Backend |
| E4-S4-T2 | 답안 제출 및 채점 API | 4h | Backend |
| E4-S4-T3 | 시간 제한 관리 로직 | 2h | Backend |
| E4-S4-T4 | 퀴즈 진행 UI 개발 | 6h | Frontend |
| E4-S4-T5 | 타이머 컴포넌트 | 2h | Frontend |
| E4-S4-T6 | 결과 요약 UI | 3h | Frontend |

---

### Story 4.5: 분반 (Breakout Rooms) 생성
**Story ID**: E4-S5
**우선순위**: P0
**Story Points**: 8

#### User Story
> **As a** 교수
> **I want to** 학생들을 소그룹으로 나누어 토론시키고
> **So that** 모든 학생이 능동적으로 토론에 참여할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 자동 배정 (랜덤, 균등 분배, 성적 기반)
- [ ] AC2: 수동 배정 (드래그 앤 드롭)
- [ ] AC3: 방 개수 및 인원 설정
- [ ] AC4: 10초 이내 모든 학생 분반 이동
- [ ] AC5: 분반별 이름 지정
- [ ] AC6: 타이머 설정 및 전체 복귀 알림

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E4-S5-T1 | 분반 생성/관리 API | 4h | Backend |
| E4-S5-T2 | 자동 배정 알고리즘 구현 | 4h | Backend |
| E4-S5-T3 | 분반 미디어 서버 연결 로직 | 6h | Backend |
| E4-S5-T4 | 분반 타이머 및 알림 | 2h | Backend |
| E4-S5-T5 | 분반 설정 UI | 4h | Frontend |
| E4-S5-T6 | 드래그 앤 드롭 배정 UI | 4h | Frontend |
| E4-S5-T7 | 분반 이동 트랜지션 | 3h | Frontend |

---

### Story 4.6: 분반 모니터링 및 이동
**Story ID**: E4-S6
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 모든 분반을 모니터링하고 자유롭게 이동하여
> **So that** 각 그룹의 토론을 지도하고 도움이 필요한 그룹을 지원할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 모든 분반 상태 한눈에 확인 (참여자 수, 활성도)
- [ ] AC2: 클릭 한 번으로 분반 간 이동
- [ ] AC3: 교수가 특정 분반에 참관 모드 (음소거로 관찰)
- [ ] AC4: 분반 전체에 메시지 브로드캐스트
- [ ] AC5: 개별 분반에 시간 연장/단축 알림

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E4-S6-T1 | 분반 상태 조회 API | 2h | Backend |
| E4-S6-T2 | 교수 분반 이동 로직 | 3h | Backend |
| E4-S6-T3 | 브로드캐스트 메시지 API | 2h | Backend |
| E4-S6-T4 | 분반 모니터링 대시보드 UI | 4h | Frontend |
| E4-S6-T5 | 분반 이동 UI | 2h | Frontend |
| E4-S6-T6 | 브로드캐스트 메시지 UI | 2h | Frontend |

---

### Story 4.7: 화이트보드
**Story ID**: E4-S7
**우선순위**: P1
**Story Points**: 8

#### User Story
> **As a** 참가자
> **I want to** 실시간 협업 화이트보드를 사용하여
> **So that** 아이디어를 시각적으로 공유하고 함께 작업할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 드로잉 도구 (펜, 형태, 텍스트, 이미지)
- [ ] AC2: 다중 사용자 동시 편집 (실시간 커서 표시)
- [ ] AC3: 무한 캔버스 (줌 인/아웃, 패닝)
- [ ] AC4: 실행 취소/다시 실행
- [ ] AC5: 저장 및 내보내기 (PNG, PDF)
- [ ] AC6: 분반별 독립 화이트보드

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E4-S7-T1 | 화이트보드 실시간 동기화 (CRDT) | 8h | Backend |
| E4-S7-T2 | 화이트보드 저장/로드 API | 3h | Backend |
| E4-S7-T3 | 이미지 내보내기 API | 2h | Backend |
| E4-S7-T4 | 화이트보드 캔버스 컴포넌트 (Excalidraw/Fabric.js) | 8h | Frontend |
| E4-S7-T5 | 드로잉 도구 UI | 4h | Frontend |
| E4-S7-T6 | 협업 커서 표시 | 3h | Frontend |

---

### Story 4.8: 소크라틱 토론 도구
**Story ID**: E4-S8
**우선순위**: P1
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 소크라틱 방식의 토론을 체계적으로 진행하여
> **So that** 학생들의 비판적 사고를 촉진할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 발언 순서 큐 (손든 순서대로)
- [ ] AC2: 발언 타이머 (시간 제한)
- [ ] AC3: 질문 스레드 시각화 (누가 누구에게 질문)
- [ ] AC4: 발언 기회 균등화 알림
- [ ] AC5: 토론 주제 표시 영역

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E4-S8-T1 | 발언 큐 관리 API | 3h | Backend |
| E4-S8-T2 | 발언 시간 추적 로직 | 2h | Backend |
| E4-S8-T3 | 균등 참여 분석 API | 2h | Backend |
| E4-S8-T4 | 발언 큐 UI | 3h | Frontend |
| E4-S8-T5 | 발언 타이머 UI | 2h | Frontend |
| E4-S8-T6 | 질문 스레드 시각화 | 4h | Frontend |

---

## Epic 5: 평가 및 피드백

> **Epic ID**: E5
> **Epic 설명**: 자동 채점, 즉각 피드백, 참여도 평가 시스템
> **우선순위**: P0 (Must Have)
> **PRD 참조**: F3.1, F3.2, F3.3, F3.4

---

### Story 5.1: 객관식 자동 채점
**Story ID**: E5-S1
**우선순위**: P0
**Story Points**: 3

#### User Story
> **As a** 학생
> **I want to** 객관식 퀴즈 제출 즉시 점수를 확인하여
> **So that** 내 이해도를 즉시 파악할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 객관식 답안 제출 즉시 채점 (<1초)
- [ ] AC2: 정답/오답 시각적 표시 (녹색/빨간색)
- [ ] AC3: 정답 해설 자동 표시
- [ ] AC4: 문항별 정답률 통계 수집
- [ ] AC5: 부분 점수 지원 (다중 선택)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E5-S1-T1 | 객관식 채점 로직 개발 | 2h | Backend |
| E5-S1-T2 | 정답률 통계 수집 로직 | 2h | Backend |
| E5-S1-T3 | 채점 결과 UI | 3h | Frontend |
| E5-S1-T4 | 해설 표시 컴포넌트 | 2h | Frontend |

---

### Story 5.2: 주관식 AI 채점
**Story ID**: E5-S2
**우선순위**: P0
**Story Points**: 8

#### User Story
> **As a** 교수
> **I want to** AI가 주관식 답안을 1차 채점하여
> **So that** 채점 시간을 절약하고 교수는 최종 검토만 할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 모범 답안과의 의미적 유사도 분석 (BERT Embedding)
- [ ] AC2: 키워드 포함 여부 체크
- [ ] AC3: 1차 채점 점수 및 신뢰도 표시
- [ ] AC4: 교수가 점수 수정 가능
- [ ] AC5: AI 채점 결과 학습 데이터로 피드백

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E5-S2-T1 | NLP 모델 통합 (BERT/Sentence-BERT) | 8h | Backend |
| E5-S2-T2 | 유사도 계산 API | 4h | Backend |
| E5-S2-T3 | 키워드 분석 로직 | 3h | Backend |
| E5-S2-T4 | 채점 결과 저장/수정 API | 2h | Backend |
| E5-S2-T5 | AI 채점 결과 UI (교수용) | 4h | Frontend |
| E5-S2-T6 | 점수 수정 인터페이스 | 2h | Frontend |

---

### Story 5.3: 코딩 문제 자동 채점
**Story ID**: E5-S3
**우선순위**: P0
**Story Points**: 8

#### User Story
> **As a** 컴퓨터공학 교수
> **I want to** 코딩 문제를 자동 채점하여
> **So that** 프로그래밍 과제의 정확성을 빠르게 확인할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 다양한 언어 지원 (Python, Java, C++, JavaScript)
- [ ] AC2: 테스트 케이스 자동 실행
- [ ] AC3: 히든 테스트 케이스 지원
- [ ] AC4: 실행 결과 및 오류 메시지 표시
- [ ] AC5: 시간/메모리 제한 설정
- [ ] AC6: 표절 검사 (유사도 분석)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E5-S3-T1 | 코드 실행 환경 구성 (Judge0/Docker) | 8h | Backend |
| E5-S3-T2 | 테스트 케이스 실행 API | 4h | Backend |
| E5-S3-T3 | 시간/메모리 제한 로직 | 2h | Backend |
| E5-S3-T4 | 표절 검사 로직 (Moss 연동) | 4h | Backend |
| E5-S3-T5 | 코드 에디터 컴포넌트 (Monaco) | 4h | Frontend |
| E5-S3-T6 | 실행 결과 UI | 3h | Frontend |

---

### Story 5.4: 즉각 피드백 시스템
**Story ID**: E5-S4
**우선순위**: P0
**Story Points**: 3

#### User Story
> **As a** 학생
> **I want to** 답안 제출 즉시 상세한 피드백을 받아
> **So that** 무엇이 잘못되었는지 바로 이해하고 개선할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 오답 시 관련 개념 설명 제공
- [ ] AC2: 유사 문제 추천
- [ ] AC3: 학습 자료 링크 제공
- [ ] AC4: 개선 제안 메시지 (예: "X 개념을 복습해보세요")
- [ ] AC5: 피드백 히스토리 저장

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E5-S4-T1 | 피드백 생성 로직 | 4h | Backend |
| E5-S4-T2 | 학습 자료 추천 API | 3h | Backend |
| E5-S4-T3 | 피드백 저장 API | 2h | Backend |
| E5-S4-T4 | 피드백 표시 UI | 3h | Frontend |
| E5-S4-T5 | 추천 자료 카드 컴포넌트 | 2h | Frontend |

---

### Story 5.5: 참여도 자동 측정
**Story ID**: E5-S5
**우선순위**: P0
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 학생들의 참여도를 자동으로 측정하여
> **So that** 객관적인 참여 점수를 성적에 반영할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 측정 항목: 발언 시간/빈도, 채팅, 투표, 손들기, 분반 기여도
- [ ] AC2: 항목별 가중치 교수 커스터마이징
- [ ] AC3: 참여 점수 자동 계산 (0-100점)
- [ ] AC4: 세션별/주별/학기별 통계
- [ ] AC5: 학생 본인의 참여도 실시간 확인

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E5-S5-T1 | 참여 이벤트 수집 로직 | 4h | Backend |
| E5-S5-T2 | 참여도 점수 계산 알고리즘 | 4h | Backend |
| E5-S5-T3 | 가중치 설정 API | 2h | Backend |
| E5-S5-T4 | 참여도 조회 API (교수/학생) | 2h | Backend |
| E5-S5-T5 | 참여도 가중치 설정 UI | 3h | Frontend |
| E5-S5-T6 | 학생용 참여도 대시보드 | 3h | Frontend |

---

### Story 5.6: 동료 평가
**Story ID**: E5-S6
**우선순위**: P2
**Story Points**: 5

#### User Story
> **As a** 학생
> **I want to** 팀 프로젝트에서 동료를 평가하여
> **So that** 팀원들의 기여도가 공정하게 반영될 수 있다.

#### Acceptance Criteria
- [ ] AC1: 평가 항목 설정 (협업, 기여도, 의사소통)
- [ ] AC2: 익명 평가 지원
- [ ] AC3: 자동 집계 및 평균 계산
- [ ] AC4: 이상치 제거 (편향된 평가 필터링)
- [ ] AC5: 평가 결과 교수에게만 공개 (선택)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E5-S6-T1 | 동료 평가 CRUD API | 4h | Backend |
| E5-S6-T2 | 이상치 제거 알고리즘 | 3h | Backend |
| E5-S6-T3 | 평가 결과 집계 로직 | 2h | Backend |
| E5-S6-T4 | 동료 평가 폼 UI | 3h | Frontend |
| E5-S6-T5 | 평가 결과 요약 UI | 2h | Frontend |

---

## Epic 6: 학습 분석

> **Epic ID**: E6
> **Epic 설명**: 실시간 대시보드, 학습 리포트, 조기 경보 시스템
> **우선순위**: P0/P1 (Must Have/Should Have)
> **PRD 참조**: F4.1, F4.2, F4.3, F4.4

---

### Story 6.1: 실시간 참여도 대시보드 (TalkTime)
**Story ID**: E6-S1
**우선순위**: P0
**Story Points**: 8

#### User Story
> **As a** 교수
> **I want to** 세션 중 실시간 참여도 대시보드를 확인하여
> **So that** 발언이 적은 학생을 즉시 파악하고 참여를 유도할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 학생별 발언 시간 막대 그래프 (실시간 업데이트)
- [ ] AC2: 평균 이하 학생 빨간색 하이라이트
- [ ] AC3: 투표/퀴즈 참여율 실시간 표시
- [ ] AC4: 시간대별 참여도 트렌드 그래프
- [ ] AC5: 클릭으로 특정 학생 지목 (비디오 하이라이트)
- [ ] AC6: 참여도 알림 (특정 학생 30분간 무참여)

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E6-S1-T1 | 실시간 참여 데이터 수집 서비스 | 4h | Backend |
| E6-S1-T2 | 참여도 통계 실시간 계산 | 4h | Backend |
| E6-S1-T3 | 참여도 WebSocket 스트리밍 | 3h | Backend |
| E6-S1-T4 | 참여도 알림 로직 | 2h | Backend |
| E6-S1-T5 | 막대 그래프 컴포넌트 (D3.js/Recharts) | 4h | Frontend |
| E6-S1-T6 | 트렌드 그래프 컴포넌트 | 3h | Frontend |
| E6-S1-T7 | 대시보드 레이아웃 UI | 4h | Frontend |

---

### Story 6.2: 학습 분석 리포트
**Story ID**: E6-S2
**우선순위**: P1
**Story Points**: 8

#### User Story
> **As a** 교수
> **I want to** 학기 전체 학습 데이터를 종합 리포트로 확인하여
> **So that** 수업을 개선하고 학생별 최종 평가에 활용할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 개인별 리포트 (출석률, 참여도, 퀴즈 성적 추이)
- [ ] AC2: 코스 전체 리포트 (평균 참여도, 문항별 정답률)
- [ ] AC3: 강점/약점 영역 자동 분석
- [ ] AC4: 어려운 개념 Top 5 식별
- [ ] AC5: 학생 간 참여도 분포 그래프
- [ ] AC6: PDF/Excel 내보내기

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E6-S2-T1 | 학습 데이터 집계 배치 작업 | 4h | Backend |
| E6-S2-T2 | 개인별 분석 API | 4h | Backend |
| E6-S2-T3 | 코스 분석 API | 4h | Backend |
| E6-S2-T4 | 리포트 PDF 생성 (WeasyPrint) | 4h | Backend |
| E6-S2-T5 | Excel 내보내기 로직 | 2h | Backend |
| E6-S2-T6 | 개인 리포트 UI | 4h | Frontend |
| E6-S2-T7 | 코스 리포트 UI | 4h | Frontend |
| E6-S2-T8 | 분포 그래프 컴포넌트 | 3h | Frontend |

---

### Story 6.3: 조기 경보 시스템
**Story ID**: E6-S3
**우선순위**: P1
**Story Points**: 5

#### User Story
> **As a** 교수
> **I want to** 학업 위험 학생을 자동으로 식별받아
> **So that** 조기에 개입하여 학생을 지원할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 위험 지표: 연속 결석, 참여도 급락, 성적 하락
- [ ] AC2: 위험 학생 목록 및 알림 (이메일, 대시보드)
- [ ] AC3: 위험도 점수 표시 (상/중/하)
- [ ] AC4: 개입 제안 (1:1 면담, 추가 자료 제공)
- [ ] AC5: 위험 지표 임계값 커스터마이징

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E6-S3-T1 | 위험 지표 계산 로직 | 4h | Backend |
| E6-S3-T2 | 위험 학생 탐지 스케줄러 | 3h | Backend |
| E6-S3-T3 | 알림 발송 로직 | 2h | Backend |
| E6-S3-T4 | 위험 학생 목록 UI | 3h | Frontend |
| E6-S3-T5 | 개입 제안 카드 UI | 2h | Frontend |
| E6-S3-T6 | 임계값 설정 UI | 2h | Frontend |

---

### Story 6.4: 토론 네트워크 분석
**Story ID**: E6-S4
**우선순위**: P2
**Story Points**: 8

#### User Story
> **As a** 교수
> **I want to** 학생 간 토론 상호작용을 네트워크로 시각화하여
> **So that** 고립된 학생이나 중심 인물을 파악할 수 있다.

#### Acceptance Criteria
- [ ] AC1: 학생 간 상호작용 그래프 (노드: 학생, 엣지: 대화)
- [ ] AC2: 중심 인물 (리더) 자동 식별
- [ ] AC3: 고립된 학생 강조 표시
- [ ] AC4: 소그룹 클러스터 자동 탐지
- [ ] AC5: 시간에 따른 네트워크 변화 애니메이션

#### Tasks
| Task ID | Task 설명 | 예상 시간 | 담당 |
|---------|----------|----------|------|
| E6-S4-T1 | 상호작용 데이터 수집 로직 | 4h | Backend |
| E6-S4-T2 | 네트워크 분석 알고리즘 (중심성 등) | 6h | Backend |
| E6-S4-T3 | 클러스터링 로직 | 4h | Backend |
| E6-S4-T4 | 네트워크 그래프 UI (D3.js force graph) | 8h | Frontend |
| E6-S4-T5 | 시간별 애니메이션 | 4h | Frontend |

---

## 우선순위 요약

### P0 (Must Have) - MVP 필수 기능

| Epic | Story | Story 제목 | SP |
|------|-------|-----------|-----|
| E1 | E1-S1 | 사용자 회원가입 | 5 |
| E1 | E1-S2 | 사용자 로그인 | 5 |
| E1 | E1-S3 | OAuth 소셜 로그인 | 5 |
| E1 | E1-S5 | 비밀번호 재설정 | 3 |
| E1 | E1-S6 | 역할 기반 접근 제어 (RBAC) | 5 |
| E2 | E2-S0 | 교수 대시보드 | 5 |
| E2 | E2-S0-S | 학생 대시보드 | 5 |
| E2 | E2-S0-SL | 학생 코스 목록 | 3 |
| E2 | E2-S0-AS | 과제 제출 | 5 |
| E2 | E2-S1 | 코스 생성 및 설정 | 5 |
| E2 | E2-S1-1 | 코스 설정 수정 | 3 |
| E2 | E2-S2 | 학생 일괄 등록 | 5 |
| E2 | E2-S3 | 초대 링크로 학생 등록 | 3 |
| E2 | E2-S4 | 세션 스케줄링 | 5 |
| E3 | E3-S1 | 세미나 룸 생성 및 입장 | 8 |
| E3 | E3-S2 | 비디오/오디오 참여 | 8 |
| E3 | E3-S3 | 화면 공유 | 5 |
| E3 | E3-S4 | 실시간 채팅 | 5 |
| E3 | E3-S5 | 손들기 및 반응 | 3 |
| E4 | E4-S1 | 실시간 투표 생성 | 5 |
| E4 | E4-S2 | 투표 참여 및 결과 확인 | 5 |
| E4 | E4-S3 | 퀴즈 문제 관리 | 5 |
| E4 | E4-S4 | 퀴즈 진행 | 5 |
| E4 | E4-S5 | 분반 (Breakout Rooms) 생성 | 8 |
| E4 | E4-S6 | 분반 모니터링 및 이동 | 5 |
| E5 | E5-S1 | 객관식 자동 채점 | 3 |
| E5 | E5-S2 | 주관식 AI 채점 | 8 |
| E5 | E5-S3 | 코딩 문제 자동 채점 | 8 |
| E5 | E5-S4 | 즉각 피드백 시스템 | 3 |
| E5 | E5-S5 | 참여도 자동 측정 | 5 |
| E6 | E6-S1 | 실시간 참여도 대시보드 | 8 |

**P0 Total**: 31개 Story, ~164 SP

---

### P1 (Should Have) - 초기 버전 포함

| Epic | Story | Story 제목 | SP |
|------|-------|-----------|-----|
| E1 | E1-S4 | 2단계 인증 (2FA) | 5 |
| E1 | E1-S7 | 사용자 프로필 관리 | 3 |
| E2 | E2-S5 | 콘텐츠 라이브러리 | 5 |
| E2 | E2-S6 | 과제 관리 | 5 |
| E2 | E2-S7 | 성적 관리 | 5 |
| E3 | E3-S6 | 세션 녹화 | 8 |
| E3 | E3-S7 | 레이아웃 모드 | 3 |
| E4 | E4-S7 | 화이트보드 | 8 |
| E4 | E4-S8 | 소크라틱 토론 도구 | 5 |
| E6 | E6-S2 | 학습 분석 리포트 | 8 |
| E6 | E6-S3 | 조기 경보 시스템 | 5 |

**P1 Total**: 11개 Story, ~60 SP

---

### P2 (Could Have) - v2 이후 고려

| Epic | Story | Story 제목 | SP |
|------|-------|-----------|-----|
| E5 | E5-S6 | 동료 평가 | 5 |
| E6 | E6-S4 | 토론 네트워크 분석 | 8 |

**P2 Total**: 2개 Story, ~13 SP

---

## 전체 요약

| 항목 | 수치 |
|-----|------|
| **총 Epic 수** | 6개 |
| **총 Story 수** | 44개 |
| **총 Story Points** | ~237 SP |
| **P0 (MVP)** | 31개 Story, 164 SP |
| **P1 (v1.0)** | 11개 Story, 60 SP |
| **P2 (v2.0+)** | 2개 Story, 13 SP |

---

## 다음 단계

1. **Sprint Planning**: Epic/Story를 Sprint 백로그로 분할
2. **기술 스파이크**: WebRTC, AI 채점 등 기술 검증
3. **UI/UX 상세 설계**: 각 Story별 Figma 목업
4. **API 명세서 작성**: OpenAPI/Swagger 문서화
5. **데이터베이스 스키마 설계**: ERD 작성

---

**문서 끝**
