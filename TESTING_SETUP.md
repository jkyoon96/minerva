# 테스트 설정 가이드

Minerva/EduForum 프로젝트에서 테스트를 설정하고 실행하기 위한 빠른 시작 가이드입니다.

## 백엔드 설정 (Spring Boot)

백엔드 테스트는 바로 실행할 수 있습니다. 추가 설정이 필요하지 않습니다.

### 백엔드 테스트 실행

```bash
cd /mnt/d/Development/git/minerva/apps/backend

# 모든 테스트 실행
./gradlew test

# 출력과 함께 실행
./gradlew test --info

# 특정 테스트 실행
./gradlew test --tests AuthServiceTest
```

### 예상 출력
```
> Task :test
AuthServiceTest > register_Success_WithValidData() PASSED
AuthServiceTest > register_Fail_DuplicateEmail() PASSED
AuthServiceTest > login_Success_WithValidCredentials() PASSED
...
BUILD SUCCESSFUL
```

## 프론트엔드 설정 (Next.js)

프론트엔드는 먼저 테스트 의존성을 설치해야 합니다.

### 1단계: 의존성 설치

```bash
cd /mnt/d/Development/git/minerva/apps/frontend

# 테스트 라이브러리 포함 모든 의존성 설치
npm install
```

설치되는 패키지:
- jest
- @testing-library/react
- @testing-library/jest-dom
- @testing-library/user-event
- jest-environment-jsdom
- identity-obj-proxy
- @types/jest

### 2단계: 프론트엔드 테스트 실행

```bash
# 모든 테스트 실행
npm test

# 감시 모드로 실행 (개발 시 권장)
npm run test:watch

# 커버리지 리포트와 함께 실행
npm run test:coverage
```

### 예상 출력
```
PASS  src/__tests__/components/ui/button.test.tsx
PASS  src/__tests__/hooks/use-auth.test.ts
PASS  src/__tests__/lib/utils.test.ts

Test Suites: 3 passed, 3 total
Tests:       25 passed, 25 total
Snapshots:   0 total
Time:        2.5s
```

## 문제 해결

### 백엔드

#### 문제: H2 데이터베이스 오류로 테스트 실패
```
해결: src/test/resources/에 application-test.yml이 있는지 확인
```

#### 문제: "Bean not found" 오류로 테스트 실패
```
해결: @WebMvcTest에서 의존성에 @MockBean 사용 확인
```

### 프론트엔드

#### 문제: "Cannot find module '@testing-library/react'"
```bash
해결: 프론트엔드 디렉토리에서 npm install 실행
cd apps/frontend && npm install
```

#### 문제: "SyntaxError: Unexpected token 'export'"
```
해결: 일반적으로 모듈 import 문제입니다. jest.config.js 확인
transformIgnorePatterns에 '/node_modules/' 포함되어야 함
```

#### 문제: "TextEncoder is not defined" 오류로 테스트 실패
```javascript
해결: jest.setup.js에 추가:
import { TextEncoder, TextDecoder } from 'util';
global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;
```

## 빠른 테스트 예제

### 백엔드: 새 서비스 테스트 추가

```java
// src/test/java/com/eduforum/api/domain/example/service/ExampleServiceTest.java
@ExtendWith(MockitoExtension.class)
class ExampleServiceTest {
    @Mock
    private ExampleRepository repository;

    @InjectMocks
    private ExampleService service;

    @Test
    @DisplayName("성공적으로 작업을 수행해야 함")
    void testMethod_Success() {
        // Given (준비)
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        // When (실행)
        Result result = service.doSomething(1L);

        // Then (검증)
        assertThat(result).isNotNull();
    }
}
```

### 프론트엔드: 새 컴포넌트 테스트 추가

```tsx
// src/__tests__/components/example.test.tsx
import { render, screen } from '@testing-library/react';
import { Example } from '@/components/example';

describe('Example 컴포넌트', () => {
  it('올바르게 렌더링됨', () => {
    render(<Example />);
    expect(screen.getByText('Hello')).toBeInTheDocument();
  });
});
```

## CI/CD에서 테스트 실행

### GitHub Actions

`.github/workflows/test.yml` 생성:

```yaml
name: 테스트 실행

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: 백엔드 테스트
        run: |
          cd apps/backend
          chmod +x gradlew
          ./gradlew test

  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: 프론트엔드 설치 및 테스트
        run: |
          cd apps/frontend
          npm ci
          npm test
```

## 테스트 커버리지 리포트

### 백엔드 커버리지 (JaCoCo)

```bash
cd apps/backend
./gradlew test jacocoTestReport

# 리포트 위치:
# apps/backend/build/reports/jacoco/test/html/index.html
```

### 프론트엔드 커버리지 (Jest)

```bash
cd apps/frontend
npm run test:coverage

# 리포트 위치:
# apps/frontend/coverage/lcov-report/index.html
```

## 다음 단계

1. **초기 테스트 실행**하여 설정 확인
2. 새 기능 개발 시 **테스트 추가**
3. **커버리지 80% 이상** 유지
4. **테스트 리포트** 정기적 검토
5. 코드 리팩토링 시 **테스트 업데이트**

## 테스트 파일 위치

### 백엔드
- 애플리케이션 테스트: `/apps/backend/src/test/java/com/eduforum/api/EduForumApplicationTests.java`
- 인증 서비스 테스트: `/apps/backend/src/test/java/com/eduforum/api/domain/auth/service/AuthServiceTest.java`
- 인증 컨트롤러 테스트: `/apps/backend/src/test/java/com/eduforum/api/domain/auth/controller/AuthControllerTest.java`
- 코스 서비스 테스트: `/apps/backend/src/test/java/com/eduforum/api/domain/course/service/CourseServiceTest.java`
- 테스트 설정: `/apps/backend/src/test/resources/application-test.yml`

### 프론트엔드
- Jest 설정: `/apps/frontend/jest.config.js`
- Jest 셋업: `/apps/frontend/jest.setup.js`
- 버튼 테스트: `/apps/frontend/src/__tests__/components/ui/button.test.tsx`
- 인증 훅 테스트: `/apps/frontend/src/__tests__/hooks/use-auth.test.ts`
- 유틸 테스트: `/apps/frontend/src/__tests__/lib/utils.test.ts`

## 참고 자료

- 백엔드 README: `/apps/backend/src/test/README.md`
- 프론트엔드 README: `/apps/frontend/src/__tests__/README.md`
- 전체 문서: `/TEST_INFRASTRUCTURE.md`

## 지원

문제 또는 질문:
1. 테스트 디렉토리의 README 파일 확인
2. 제공된 테스트 예제 검토
3. 위의 문제 해결 섹션 참조
4. 특정 오류는 Jest/JUnit 문서 참조
