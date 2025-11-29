# EduForum E2E 테스트 계획서

**문서 버전**: 1.0
**작성일**: 2024-11-29
**작성자**: Claude Code

## 1. 개요

### 1.1 목적
이 문서는 EduForum 플랫폼의 End-to-End(E2E) 테스트 전략, 범위, 시나리오 및 실행 계획을 정의합니다.

### 1.2 범위
- 6개 Epic (E1-E6)의 주요 사용자 플로우
- 크로스 브라우저 호환성
- 반응형 디자인 (데스크톱/태블릿/모바일)
- 성능 및 접근성 테스트

### 1.3 테스트 도구

| 도구 | 용도 |
|------|------|
| Playwright | E2E 테스트 프레임워크 |
| Jest | 테스트 러너 |
| Allure | 테스트 리포트 |
| GitHub Actions | CI/CD 통합 |

## 2. 테스트 환경

### 2.1 브라우저 매트릭스

| 브라우저 | 버전 | 우선순위 |
|----------|------|----------|
| Chrome | 최신 2개 버전 | P0 |
| Firefox | 최신 2개 버전 | P1 |
| Safari | 최신 2개 버전 | P1 |
| Edge | 최신 2개 버전 | P2 |

### 2.2 디바이스 뷰포트

| 디바이스 | 해상도 | 우선순위 |
|----------|--------|----------|
| Desktop | 1920x1080 | P0 |
| Laptop | 1366x768 | P0 |
| Tablet | 768x1024 | P1 |
| Mobile | 375x667 | P1 |

### 2.3 테스트 환경

| 환경 | URL | 용도 |
|------|-----|------|
| Local | http://localhost:3000 | 개발 테스트 |
| Staging | https://staging.eduforum.com | QA 테스트 |
| Production | https://eduforum.com | 스모크 테스트 |

## 3. E2E 테스트 시나리오

### 3.1 Epic 1: 사용자 인증 (E1)

#### E1-TC-001: 회원가입 플로우
**우선순위**: P0
**전제조건**: 미인증 사용자

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 회원가입 페이지 접속 | 회원가입 폼 표시 |
| 2 | 이메일, 비밀번호, 이름 입력 | 입력값 반영 |
| 3 | 약관 동의 체크 | 체크박스 선택됨 |
| 4 | 가입 버튼 클릭 | 이메일 인증 안내 페이지 이동 |
| 5 | 이메일 인증 링크 클릭 | 인증 완료 메시지 표시 |
| 6 | 로그인 페이지 이동 | 로그인 폼 표시 |

```typescript
test('E1-TC-001: 회원가입 플로우', async ({ page }) => {
  await page.goto('/register');

  await page.fill('[data-testid="email"]', 'newuser@test.com');
  await page.fill('[data-testid="password"]', 'SecurePass123!');
  await page.fill('[data-testid="confirm-password"]', 'SecurePass123!');
  await page.fill('[data-testid="first-name"]', '길동');
  await page.fill('[data-testid="last-name"]', '홍');
  await page.check('[data-testid="terms-checkbox"]');

  await page.click('[data-testid="register-button"]');

  await expect(page).toHaveURL('/verify-email');
  await expect(page.locator('[data-testid="verification-message"]')).toBeVisible();
});
```

#### E1-TC-002: 로그인 플로우
**우선순위**: P0
**전제조건**: 등록된 사용자

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 로그인 페이지 접속 | 로그인 폼 표시 |
| 2 | 이메일, 비밀번호 입력 | 입력값 반영 |
| 3 | 로그인 버튼 클릭 | 대시보드 이동 |
| 4 | 사용자 정보 확인 | 사용자명 표시 |

```typescript
test('E1-TC-002: 로그인 플로우', async ({ page }) => {
  await page.goto('/login');

  await page.fill('[data-testid="email"]', 'user@test.com');
  await page.fill('[data-testid="password"]', 'TestPass123!');
  await page.click('[data-testid="login-button"]');

  await expect(page).toHaveURL('/dashboard');
  await expect(page.locator('[data-testid="user-menu"]')).toContainText('홍길동');
});
```

#### E1-TC-003: 2FA 설정 및 인증
**우선순위**: P1
**전제조건**: 로그인된 사용자

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 보안 설정 페이지 접속 | 2FA 설정 옵션 표시 |
| 2 | 2FA 활성화 클릭 | QR 코드 모달 표시 |
| 3 | TOTP 코드 입력 | 백업 코드 표시 |
| 4 | 백업 코드 저장 확인 | 2FA 활성화 완료 |
| 5 | 로그아웃 후 재로그인 | 2FA 코드 입력 요청 |
| 6 | TOTP 코드 입력 | 대시보드 이동 |

#### E1-TC-004: 비밀번호 재설정
**우선순위**: P0
**전제조건**: 등록된 사용자

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 비밀번호 찾기 페이지 접속 | 이메일 입력 폼 표시 |
| 2 | 이메일 입력 후 제출 | 이메일 발송 안내 |
| 3 | 재설정 링크 클릭 | 새 비밀번호 입력 폼 |
| 4 | 새 비밀번호 입력 | 비밀번호 변경 완료 |
| 5 | 새 비밀번호로 로그인 | 로그인 성공 |

#### E1-TC-005: 로그인 시도 제한
**우선순위**: P1
**전제조건**: 등록된 사용자

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 잘못된 비밀번호로 5회 시도 | 각 시도마다 오류 메시지 |
| 2 | 6번째 시도 | 계정 잠금 메시지 (15분) |
| 3 | 15분 후 재시도 | 로그인 가능 |

### 3.2 Epic 2: 코스 관리 (E2)

#### E2-TC-001: 코스 생성 (교수)
**우선순위**: P0
**전제조건**: 교수 계정으로 로그인

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 코스 관리 페이지 접속 | 코스 목록 표시 |
| 2 | 새 코스 생성 버튼 클릭 | 코스 생성 폼 표시 |
| 3 | 코스 정보 입력 | 입력값 반영 |
| 4 | 생성 버튼 클릭 | 코스 생성 성공 메시지 |
| 5 | 코스 목록 확인 | 새 코스 표시 |

```typescript
test('E2-TC-001: 코스 생성 (교수)', async ({ page }) => {
  await loginAsProfessor(page);
  await page.goto('/courses');

  await page.click('[data-testid="create-course-button"]');

  await page.fill('[data-testid="course-code"]', 'CS101');
  await page.fill('[data-testid="course-title"]', '컴퓨터 과학 개론');
  await page.fill('[data-testid="course-description"]', '컴퓨터 과학의 기초를 배웁니다.');
  await page.selectOption('[data-testid="semester"]', '2024-1');
  await page.fill('[data-testid="max-students"]', '30');

  await page.click('[data-testid="submit-course"]');

  await expect(page.locator('[data-testid="success-message"]')).toBeVisible();
  await expect(page.locator('[data-testid="course-list"]')).toContainText('CS101');
});
```

#### E2-TC-002: 수강생 등록
**우선순위**: P0
**전제조건**: 교수 계정, 생성된 코스

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 코스 상세 페이지 접속 | 코스 정보 표시 |
| 2 | 수강생 관리 탭 클릭 | 수강생 목록 표시 |
| 3 | 수강생 추가 버튼 클릭 | 수강생 검색 모달 |
| 4 | 학생 검색 및 선택 | 선택된 학생 표시 |
| 5 | 등록 확인 | 수강생 목록에 추가 |

#### E2-TC-003: 일괄 등록 (CSV)
**우선순위**: P1
**전제조건**: 교수 계정, 생성된 코스

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 일괄 등록 버튼 클릭 | CSV 업로드 모달 |
| 2 | CSV 파일 업로드 | 미리보기 표시 |
| 3 | 미리보기 확인 | 유효/무효 항목 구분 |
| 4 | 등록 진행 | 등록 결과 표시 |
| 5 | 수강생 목록 확인 | 등록된 학생 표시 |

#### E2-TC-004: 세션 일정 관리
**우선순위**: P1
**전제조건**: 교수 계정, 생성된 코스

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 일정 관리 탭 클릭 | 캘린더 뷰 표시 |
| 2 | 새 세션 추가 | 세션 생성 폼 |
| 3 | 세션 정보 입력 | 캘린더에 세션 표시 |
| 4 | iCal 내보내기 | .ics 파일 다운로드 |

#### E2-TC-005: 파일 업로드 및 관리
**우선순위**: P1
**전제조건**: 교수 계정, 생성된 코스

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 콘텐츠 라이브러리 접속 | 파일 목록 표시 |
| 2 | 폴더 생성 | 새 폴더 표시 |
| 3 | 파일 드래그 앤 드롭 | 업로드 진행 표시 |
| 4 | 업로드 완료 | 파일 목록에 추가 |
| 5 | 파일 다운로드 | 파일 다운로드 시작 |

### 3.3 Epic 3: 실시간 세미나 (E3)

#### E3-TC-001: 세미나 룸 입장
**우선순위**: P0
**전제조건**: 로그인된 사용자, 예정된 세션

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 세션 참여 버튼 클릭 | 미디어 권한 요청 |
| 2 | 카메라/마이크 허용 | 프리뷰 화면 표시 |
| 3 | 입장 버튼 클릭 | 세미나 룸 진입 |
| 4 | 비디오 타일 확인 | 자신의 비디오 표시 |

```typescript
test('E3-TC-001: 세미나 룸 입장', async ({ page, context }) => {
  // 미디어 권한 허용
  await context.grantPermissions(['camera', 'microphone']);

  await loginAsStudent(page);
  await page.goto('/courses/1/sessions/1');

  await page.click('[data-testid="join-session"]');

  // 프리뷰 화면
  await expect(page.locator('[data-testid="video-preview"]')).toBeVisible();

  await page.click('[data-testid="enter-room"]');

  // 세미나 룸
  await expect(page).toHaveURL(/\/seminar\//);
  await expect(page.locator('[data-testid="local-video"]')).toBeVisible();
});
```

#### E3-TC-002: 화면 공유 (교수)
**우선순위**: P0
**전제조건**: 교수로 세미나 룸 입장

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 화면 공유 버튼 클릭 | 화면 선택 다이얼로그 |
| 2 | 화면/창 선택 | 공유 시작 |
| 3 | 학생 화면 확인 | 공유 화면 표시 |
| 4 | 공유 중지 클릭 | 공유 종료 |

#### E3-TC-003: 실시간 채팅
**우선순위**: P1
**전제조건**: 세미나 룸 입장

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 채팅 패널 열기 | 채팅 UI 표시 |
| 2 | 메시지 입력 | 입력 필드에 표시 |
| 3 | 전송 버튼 클릭 | 메시지 채팅창에 표시 |
| 4 | 다른 참가자 확인 | 메시지 실시간 수신 |

#### E3-TC-004: 손들기 기능
**우선순위**: P1
**전제조건**: 학생으로 세미나 룸 입장

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 손들기 버튼 클릭 | 손들기 아이콘 활성화 |
| 2 | 교수 화면 확인 | 손든 학생 표시 |
| 3 | 교수가 발언권 부여 | 발언권 획득 알림 |
| 4 | 손 내리기 클릭 | 손들기 해제 |

#### E3-TC-005: 세션 녹화 (교수)
**우선순위**: P2
**전제조건**: 교수로 세미나 룸 입장

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 녹화 시작 버튼 클릭 | 녹화 시작 표시 |
| 2 | 참가자에게 녹화 알림 | 녹화 중 배지 표시 |
| 3 | 녹화 중지 클릭 | 녹화 저장 중 |
| 4 | 녹화 목록 확인 | 녹화 파일 표시 |

### 3.4 Epic 4: 액티브 러닝 (E4)

#### E4-TC-001: 실시간 투표 생성 및 참여
**우선순위**: P0
**전제조건**: 세미나 세션 진행 중

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 교수: 투표 생성 클릭 | 투표 생성 폼 |
| 2 | 질문 및 옵션 입력 | 미리보기 표시 |
| 3 | 투표 시작 클릭 | 학생에게 투표 표시 |
| 4 | 학생: 옵션 선택 | 투표 완료 표시 |
| 5 | 교수: 결과 공개 | 결과 차트 표시 |

```typescript
test('E4-TC-001: 실시간 투표', async ({ browser }) => {
  // 교수 컨텍스트
  const professorContext = await browser.newContext();
  const professorPage = await professorContext.newPage();
  await loginAsProfessor(professorPage);
  await joinSeminar(professorPage, 'room-123');

  // 학생 컨텍스트
  const studentContext = await browser.newContext();
  const studentPage = await studentContext.newPage();
  await loginAsStudent(studentPage);
  await joinSeminar(studentPage, 'room-123');

  // 교수: 투표 생성
  await professorPage.click('[data-testid="create-poll"]');
  await professorPage.fill('[data-testid="poll-question"]', '이해가 되셨나요?');
  await professorPage.fill('[data-testid="option-1"]', '예');
  await professorPage.fill('[data-testid="option-2"]', '아니오');
  await professorPage.click('[data-testid="start-poll"]');

  // 학생: 투표 참여
  await expect(studentPage.locator('[data-testid="poll-modal"]')).toBeVisible();
  await studentPage.click('[data-testid="option-1"]');

  // 교수: 결과 확인
  await professorPage.click('[data-testid="end-poll"]');
  await expect(professorPage.locator('[data-testid="poll-results"]')).toBeVisible();

  await professorContext.close();
  await studentContext.close();
});
```

#### E4-TC-002: 퀴즈 진행
**우선순위**: P0
**전제조건**: 세미나 세션 진행 중

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 교수: 퀴즈 템플릿 선택 | 퀴즈 설정 화면 |
| 2 | 문제 수정/확인 | 퀴즈 미리보기 |
| 3 | 퀴즈 시작 | 학생에게 퀴즈 표시 |
| 4 | 학생: 답안 제출 | 제출 확인 |
| 5 | 교수: 퀴즈 종료 | 결과 및 통계 표시 |

#### E4-TC-003: 분반 토론
**우선순위**: P1
**전제조건**: 세미나 세션 진행 중

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 교수: 분반 생성 | 분반 설정 화면 |
| 2 | 그룹 수/방식 설정 | 그룹 프리뷰 |
| 3 | 분반 시작 | 학생들 그룹별 분리 |
| 4 | 학생: 소그룹 활동 | 그룹 채팅/비디오 |
| 5 | 교수: 그룹 방문 | 그룹 간 이동 |
| 6 | 교수: 분반 종료 | 전체 세션 복귀 |

#### E4-TC-004: 화이트보드 협업
**우선순위**: P1
**전제조건**: 세미나 세션 진행 중

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 화이트보드 열기 | 빈 캔버스 표시 |
| 2 | 그리기 도구 선택 | 도구 활성화 |
| 3 | 캔버스에 그리기 | 실시간 표시 |
| 4 | 다른 참가자 확인 | 동기화된 내용 |
| 5 | 화이트보드 저장 | 이미지 저장 |

### 3.5 Epic 5: 평가 및 피드백 (E5)

#### E5-TC-001: 과제 제출 (학생)
**우선순위**: P0
**전제조건**: 학생 계정, 열린 과제

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 과제 목록 페이지 접속 | 과제 목록 표시 |
| 2 | 과제 클릭 | 과제 상세 표시 |
| 3 | 파일 업로드 | 파일 첨부됨 |
| 4 | 제출 버튼 클릭 | 제출 확인 메시지 |
| 5 | 제출 내역 확인 | 제출 상태 표시 |

```typescript
test('E5-TC-001: 과제 제출 (학생)', async ({ page }) => {
  await loginAsStudent(page);
  await page.goto('/courses/1/assignments');

  await page.click('[data-testid="assignment-1"]');

  // 파일 업로드
  const fileInput = await page.locator('[data-testid="file-input"]');
  await fileInput.setInputFiles('test-files/assignment.pdf');

  await expect(page.locator('[data-testid="uploaded-file"]')).toContainText('assignment.pdf');

  await page.click('[data-testid="submit-button"]');

  await expect(page.locator('[data-testid="success-message"]')).toBeVisible();
  await expect(page.locator('[data-testid="submission-status"]')).toContainText('제출됨');
});
```

#### E5-TC-002: 과제 채점 (교수)
**우선순위**: P0
**전제조건**: 교수 계정, 제출된 과제

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 채점 페이지 접속 | 제출 목록 표시 |
| 2 | 학생 제출물 클릭 | 제출물 상세 표시 |
| 3 | 루브릭 기반 점수 입력 | 총점 계산 |
| 4 | 피드백 작성 | 피드백 저장 |
| 5 | 채점 완료 클릭 | 학생에게 알림 |

#### E5-TC-003: 동료 평가
**우선순위**: P1
**전제조건**: 동료 평가 설정된 과제

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 동료 평가 페이지 접속 | 평가 대상 목록 |
| 2 | 동료 제출물 확인 | 익명화된 제출물 |
| 3 | 평가 기준에 따라 점수 | 점수 입력 |
| 4 | 피드백 작성 | 피드백 저장 |
| 5 | 제출 | 평가 완료 |

#### E5-TC-004: 성적 확인 (학생)
**우선순위**: P0
**전제조건**: 채점 완료된 과제

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 성적 페이지 접속 | 성적 목록 표시 |
| 2 | 과제별 점수 확인 | 점수 및 피드백 |
| 3 | 전체 평균 확인 | 코스 평균 점수 |
| 4 | 상세 피드백 보기 | 루브릭별 점수 |

### 3.6 Epic 6: 학습 분석 (E6)

#### E6-TC-001: 실시간 참여도 대시보드 (교수)
**우선순위**: P1
**전제조건**: 교수 계정, 세션 진행 중

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 분석 패널 열기 | 실시간 지표 표시 |
| 2 | 참여도 그래프 확인 | 시간별 참여도 |
| 3 | 개별 학생 상태 확인 | 학생별 지표 |
| 4 | 알림 설정 | 임계값 설정 |

#### E6-TC-002: 학습 리포트 생성
**우선순위**: P1
**전제조건**: 교수 계정, 완료된 세션

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 리포트 페이지 접속 | 리포트 옵션 표시 |
| 2 | 기간/코스 선택 | 데이터 필터링 |
| 3 | 리포트 생성 클릭 | 리포트 미리보기 |
| 4 | PDF 다운로드 | 파일 다운로드 |

#### E6-TC-003: 위험 학생 알림
**우선순위**: P2
**전제조건**: 교수 계정, 참여도 낮은 학생 존재

| 단계 | 동작 | 예상 결과 |
|------|------|-----------|
| 1 | 알림 패널 확인 | 위험 학생 목록 |
| 2 | 학생 상세 클릭 | 상세 분석 표시 |
| 3 | 개입 조치 선택 | 조치 옵션 표시 |
| 4 | 이메일 발송 | 알림 발송 확인 |

## 4. 크로스 기능 테스트

### 4.1 권한 검증 테스트

| TC-ID | 시나리오 | 예상 결과 |
|-------|----------|-----------|
| AUTH-001 | 학생이 코스 생성 시도 | 403 Forbidden |
| AUTH-002 | 미등록 학생이 코스 접근 | 접근 거부 |
| AUTH-003 | TA가 허용된 기능 접근 | 접근 허용 |
| AUTH-004 | 만료된 토큰으로 API 호출 | 401 Unauthorized |

### 4.2 오류 처리 테스트

| TC-ID | 시나리오 | 예상 결과 |
|-------|----------|-----------|
| ERR-001 | 네트워크 연결 끊김 | 오프라인 알림 표시 |
| ERR-002 | 서버 500 오류 | 에러 페이지 표시 |
| ERR-003 | 유효하지 않은 URL | 404 페이지 표시 |
| ERR-004 | 세션 만료 | 로그인 페이지 리다이렉트 |

### 4.3 접근성 테스트

| TC-ID | 검증 항목 | 기준 |
|-------|----------|------|
| A11Y-001 | 키보드 네비게이션 | 모든 기능 접근 가능 |
| A11Y-002 | 스크린 리더 호환성 | ARIA 라벨 적용 |
| A11Y-003 | 색상 대비 | WCAG 2.1 AA 기준 |
| A11Y-004 | 폼 레이블 | 모든 입력에 레이블 |

### 4.4 성능 테스트

| TC-ID | 측정 항목 | 목표 |
|-------|----------|------|
| PERF-001 | 페이지 로드 시간 | < 3초 |
| PERF-002 | API 응답 시간 | < 500ms |
| PERF-003 | 동시 사용자 처리 | 100명 |
| PERF-004 | WebSocket 지연 | < 200ms |

## 5. 테스트 데이터

### 5.1 테스트 사용자

| 역할 | 이메일 | 비밀번호 |
|------|--------|----------|
| 관리자 | admin@test.com | Admin123! |
| 교수 | professor@test.com | Prof123! |
| TA | ta@test.com | TA123! |
| 학생 | student@test.com | Student123! |

### 5.2 테스트 코스

| 코드 | 이름 | 교수 |
|------|------|------|
| CS101 | 컴퓨터 과학 개론 | professor@test.com |
| CS201 | 자료구조 | professor@test.com |
| CS301 | 알고리즘 | professor@test.com |

## 6. 실행 계획

### 6.1 테스트 주기

| 유형 | 빈도 | 트리거 |
|------|------|--------|
| 스모크 테스트 | 매 배포 | CI/CD |
| 회귀 테스트 | 매일 밤 | 스케줄 |
| 전체 E2E | 매주 | 스케줄 |
| 성능 테스트 | 매주 | 스케줄 |

### 6.2 CI/CD 통합

```yaml
# .github/workflows/e2e.yml
name: E2E 테스트

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]
  schedule:
    - cron: '0 2 * * *'  # 매일 새벽 2시

jobs:
  e2e:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Docker Compose 시작
        run: docker-compose up -d

      - name: 서비스 대기
        run: |
          npx wait-on http://localhost:3000
          npx wait-on http://localhost:8080/api/actuator/health

      - name: Playwright 설치
        run: npx playwright install --with-deps

      - name: E2E 테스트 실행
        run: npm run test:e2e

      - name: 리포트 업로드
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: playwright-report
          path: playwright-report/

      - name: Docker Compose 정리
        run: docker-compose down -v
        if: always()
```

### 6.3 리포트

| 리포트 유형 | 도구 | 공유 방식 |
|-------------|------|-----------|
| HTML 리포트 | Playwright | GitHub Artifacts |
| 커버리지 리포트 | Istanbul | Codecov |
| 성능 리포트 | Lighthouse | PR 코멘트 |
| 슬랙 알림 | Webhook | 실패 시 알림 |

## 7. 부록

### 7.1 Playwright 설정

```typescript
// playwright.config.ts
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: [
    ['html'],
    ['junit', { outputFile: 'test-results/junit.xml' }],
  ],
  use: {
    baseURL: 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
    {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 5'] },
    },
    {
      name: 'Mobile Safari',
      use: { ...devices['iPhone 12'] },
    },
  ],
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
  },
});
```

### 7.2 테스트 유틸리티

```typescript
// e2e/utils/auth.ts
import { Page } from '@playwright/test';

export async function loginAsProfessor(page: Page) {
  await page.goto('/login');
  await page.fill('[data-testid="email"]', 'professor@test.com');
  await page.fill('[data-testid="password"]', 'Prof123!');
  await page.click('[data-testid="login-button"]');
  await page.waitForURL('/dashboard');
}

export async function loginAsStudent(page: Page) {
  await page.goto('/login');
  await page.fill('[data-testid="email"]', 'student@test.com');
  await page.fill('[data-testid="password"]', 'Student123!');
  await page.click('[data-testid="login-button"]');
  await page.waitForURL('/dashboard');
}

export async function joinSeminar(page: Page, roomId: string) {
  await page.goto(`/seminar/${roomId}`);
  await page.click('[data-testid="join-button"]');
  await page.waitForSelector('[data-testid="seminar-room"]');
}
```

### 7.3 디렉토리 구조

```
e2e/
├── fixtures/
│   ├── test-data.json       # 테스트 데이터
│   └── test-files/          # 업로드 테스트용 파일
├── pages/
│   ├── login.page.ts        # 페이지 오브젝트
│   ├── dashboard.page.ts
│   ├── course.page.ts
│   └── seminar.page.ts
├── utils/
│   ├── auth.ts              # 인증 헬퍼
│   ├── api.ts               # API 헬퍼
│   └── wait.ts              # 대기 헬퍼
├── specs/
│   ├── e1-auth/
│   │   ├── register.spec.ts
│   │   ├── login.spec.ts
│   │   └── 2fa.spec.ts
│   ├── e2-course/
│   │   ├── create-course.spec.ts
│   │   └── enrollment.spec.ts
│   ├── e3-seminar/
│   │   ├── join-room.spec.ts
│   │   └── screen-share.spec.ts
│   ├── e4-active/
│   │   ├── poll.spec.ts
│   │   └── quiz.spec.ts
│   ├── e5-assessment/
│   │   ├── submission.spec.ts
│   │   └── grading.spec.ts
│   └── e6-analytics/
│       └── dashboard.spec.ts
├── global-setup.ts          # 글로벌 설정
├── global-teardown.ts       # 글로벌 정리
└── playwright.config.ts     # Playwright 설정
```

## 8. 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 1.0 | 2024-11-29 | 초기 문서 작성 | Claude Code |
