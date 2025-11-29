# EduForum 테스트 인프라

이 문서는 Minerva/EduForum 프로젝트의 테스트 인프라에 대한 개요를 제공합니다.

## 개요

이 프로젝트는 백엔드(Spring Boot)와 프론트엔드(Next.js) 애플리케이션 모두에 대한 종합적인 테스트 인프라를 포함합니다.

## 백엔드 테스트 (Java/Spring Boot)

### 위치
`/mnt/d/Development/git/minerva/apps/backend/src/test/`

### 생성된 테스트 파일

1. **EduForumApplicationTests.java** - 기본 컨텍스트 로드 테스트
   - Spring Boot 애플리케이션 컨텍스트가 성공적으로 로드되는지 확인
   - 빈 설정에 대한 스모크 테스트

2. **AuthServiceTest.java** - 인증 서비스 단위 테스트
   - 사용자 등록 (성공, 중복 이메일, 비밀번호 불일치, 약관 미동의)
   - 사용자 로그인 (성공, 잘못된 자격 증명, 계정 잠금, 정지된 계정)
   - 로그아웃 기능
   - 의존성 모킹에 Mockito 사용

3. **AuthControllerTest.java** - 인증 컨트롤러 테스트
   - @WebMvcTest를 사용한 REST API 엔드포인트 테스트
   - 회원가입, 로그인, 2FA, 토큰 갱신, 이메일 인증, 비밀번호 재설정 테스트
   - 요청/응답 형식 및 HTTP 상태 코드 검증

4. **CourseServiceTest.java** - 코스 서비스 단위 테스트
   - 코스 생성 (성공, 중복 코드)
   - 코스 조회
   - 교수용 코스 목록
   - 리포지토리 모킹에 Mockito 사용

5. **application-test.yml** - 테스트 설정
   - H2 인메모리 데이터베이스 (PostgreSQL 호환 모드)
   - 테스트용 보안 비활성화
   - JWT 설정
   - 최적화된 로깅 레벨

### 기술 스택

- **프레임워크**: Spring Boot 3.2.1
- **Java 버전**: 17
- **빌드 도구**: Gradle
- **테스트 프레임워크**: JUnit 5 (Jupiter)
- **모킹**: Mockito
- **어서션**: AssertJ
- **데이터베이스**: H2 (인메모리, PostgreSQL 모드)
- **보안**: Spring Security Test

### 백엔드 테스트 실행

```bash
cd /mnt/d/Development/git/minerva/apps/backend

# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests AuthServiceTest

# 커버리지와 함께 실행
./gradlew test jacocoTestReport

# 지속 모드로 실행
./gradlew test --continuous
```

## 프론트엔드 테스트 (Next.js/React)

### 위치
`/mnt/d/Development/git/minerva/apps/frontend/src/__tests__/`

### 생성된 테스트 파일

1. **jest.config.js** - Next.js용 Jest 설정
   - 모듈 경로 매핑 (@/ 별칭)
   - CSS 및 에셋 모킹
   - 커버리지 설정
   - 테스트 환경 설정

2. **jest.setup.js** - Jest 글로벌 설정
   - Testing Library DOM 매처
   - Window.matchMedia 모킹
   - IntersectionObserver 모킹
   - ResizeObserver 모킹

3. **button.test.tsx** - Button 컴포넌트 테스트
   - 다양한 variant로 렌더링 (default, outline, ghost, destructive)
   - 다양한 크기로 렌더링 (sm, default, lg)
   - 클릭 이벤트 처리
   - 비활성화 상태
   - Ref 전달
   - 커스텀 className 적용

4. **use-auth.test.ts** - 인증 스토어 (Zustand) 테스트
   - 초기 상태 확인
   - 사용자 로그인 (성공, 2FA, 오류)
   - 사용자 로그아웃
   - 사용자 상태 업데이트
   - 오류 처리

5. **utils.test.ts** - 유틸리티 함수 테스트
   - cn() 클래스명 병합
   - formatDate() 한국어 날짜 포맷팅
   - formatDateTime() 날짜 및 시간 포맷팅

### 기술 스택

- **프레임워크**: Next.js 14, React 18
- **언어**: TypeScript
- **테스트 러너**: Jest 29
- **테스팅 라이브러리**: React Testing Library
- **사용자 인터랙션**: @testing-library/user-event
- **상태 관리**: Zustand (테스트됨)
- **모킹**: Jest mocks, CSS용 identity-obj-proxy

### 프론트엔드 테스트 실행

```bash
cd /mnt/d/Development/git/minerva/apps/frontend

# 의존성 설치 (아직 설치하지 않은 경우)
npm install

# 모든 테스트 실행
npm test

# 감시 모드로 테스트 실행
npm run test:watch

# 커버리지와 함께 테스트 실행
npm run test:coverage

# 특정 테스트 파일 실행
npm test button.test
```

### 프론트엔드 의존성 설치

다음 의존성이 `package.json`에 추가되었습니다:

```json
{
  "devDependencies": {
    "@testing-library/jest-dom": "^6.1.5",
    "@testing-library/react": "^14.1.2",
    "@testing-library/user-event": "^14.5.1",
    "@types/jest": "^29.5.11",
    "identity-obj-proxy": "^3.0.0",
    "jest": "^29.7.0",
    "jest-environment-jsdom": "^29.7.0"
  }
}
```

`npm install`을 실행하여 이러한 의존성을 설치합니다.

## 테스트 커버리지 목표

### 백엔드
- **라인 커버리지**: 최소 80%
- **브랜치 커버리지**: 최소 70%
- **중요 경로**: 100% (인증, 권한 부여, 결제)

### 프론트엔드
- **구문**: 최소 80%
- **브랜치**: 최소 75%
- **함수**: 최소 80%
- **라인**: 최소 80%

## 모범 사례

### 백엔드
1. 설명적인 테스트 메서드명 사용: `methodName_Scenario_ExpectedResult`
2. Given-When-Then (Arrange-Act-Assert) 구조로 테스트 작성
3. 외부 의존성 모킹 (리포지토리, API)
4. 읽기 쉬운 테스트 리포트를 위해 `@DisplayName` 사용
5. 테스트를 격리되고 독립적으로 유지

### 프론트엔드
1. 가능하면 role/label로 쿼리 (접근성 중심)
2. 실제적인 사용자 인터랙션을 위해 `userEvent` 사용
3. 구현 세부 사항이 아닌 사용자 가시적 동작 테스트
4. API 호출 및 외부 모듈 모킹
5. 비동기 작업에 `waitFor` 사용
6. 테스트 간 모킹 초기화

## 문서

상세 문서는 다음에서 확인할 수 있습니다:
- 백엔드: `/mnt/d/Development/git/minerva/apps/backend/src/test/README.md`
- 프론트엔드: `/mnt/d/Development/git/minerva/apps/frontend/src/__tests__/README.md`

## 다음 단계

1. **백엔드**:
   - `./gradlew test`를 실행하여 모든 테스트 통과 확인
   - 중요 흐름에 대한 통합 테스트 추가
   - 테스트 커버리지 리포팅 설정 (JaCoCo)
   - 나머지 도메인에 테스트 추가 (seminar, active, assessment, analytics)

2. **프론트엔드**:
   - `npm install`을 실행하여 테스트 의존성 설치
   - `npm test`를 실행하여 모든 테스트 통과 확인
   - 페이지 컴포넌트에 테스트 추가
   - 나머지 훅 및 유틸리티에 테스트 추가
   - 테스트 자동화로 CI/CD 파이프라인 설정

## CI/CD 통합

### GitHub Actions 예시

```yaml
# .github/workflows/test.yml
name: 테스트

on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: 백엔드 테스트 실행
        run: |
          cd apps/backend
          ./gradlew test

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: 의존성 설치
        run: |
          cd apps/frontend
          npm install
      - name: 프론트엔드 테스트 실행
        run: |
          cd apps/frontend
          npm test
```

## 문제 해결

### 백엔드
- **H2 데이터베이스 문제**: `application-test.yml`에서 올바른 JDBC URL 확인
- **모킹 문제**: `@MockBean` vs `@Mock` 사용 확인
- **보안 문제**: 테스트에서 `@WithMockUser` 사용 또는 보안 비활성화

### 프론트엔드
- **"Not wrapped in act(...)"**: 상태 업데이트에 `act()` 사용 또는 비동기에 `waitFor` 사용
- **"Unable to find element"**: `screen.debug()`로 DOM 검사
- **모킹이 작동하지 않음**: import 전에 모킹 정의 확인, 테스트 간 모킹 초기화

## 요약

테스트 인프라가 백엔드와 프론트엔드 모두에 완전히 설정되었습니다. 다음을 수행할 수 있습니다:

- Gradle로 백엔드 테스트 실행
- Jest로 프론트엔드 테스트 실행
- 확립된 패턴에 따라 새 테스트 작성
- 애플리케이션 전반에 걸쳐 종합적인 테스트 커버리지 달성

자세한 내용은 각 테스트 디렉토리의 README 파일을 참조하세요.
