# Testing Setup Guide

Quick start guide for setting up and running tests in the Minerva/EduForum project.

## Backend Setup (Spring Boot)

The backend tests are ready to run immediately. No additional setup required.

### Run Backend Tests

```bash
cd /mnt/d/Development/git/minerva/apps/backend

# Run all tests
./gradlew test

# Run with output
./gradlew test --info

# Run specific test
./gradlew test --tests AuthServiceTest
```

### Expected Output
```
> Task :test
AuthServiceTest > register_Success_WithValidData() PASSED
AuthServiceTest > register_Fail_DuplicateEmail() PASSED
AuthServiceTest > login_Success_WithValidCredentials() PASSED
...
BUILD SUCCESSFUL
```

## Frontend Setup (Next.js)

The frontend requires installing test dependencies first.

### Step 1: Install Dependencies

```bash
cd /mnt/d/Development/git/minerva/apps/frontend

# Install all dependencies including test libraries
npm install
```

This will install:
- jest
- @testing-library/react
- @testing-library/jest-dom
- @testing-library/user-event
- jest-environment-jsdom
- identity-obj-proxy
- @types/jest

### Step 2: Run Frontend Tests

```bash
# Run all tests
npm test

# Run in watch mode (recommended for development)
npm run test:watch

# Run with coverage report
npm run test:coverage
```

### Expected Output
```
PASS  src/__tests__/components/ui/button.test.tsx
PASS  src/__tests__/hooks/use-auth.test.ts
PASS  src/__tests__/lib/utils.test.ts

Test Suites: 3 passed, 3 total
Tests:       25 passed, 25 total
Snapshots:   0 total
Time:        2.5s
```

## Troubleshooting

### Backend

#### Issue: Tests fail with H2 database errors
```
Solution: Check that application-test.yml is in src/test/resources/
```

#### Issue: Tests fail with "Bean not found"
```
Solution: Make sure @MockBean is used for dependencies in @WebMvcTest
```

### Frontend

#### Issue: "Cannot find module '@testing-library/react'"
```bash
Solution: Run npm install in the frontend directory
cd apps/frontend && npm install
```

#### Issue: "SyntaxError: Unexpected token 'export'"
```
Solution: This is usually a module import issue. Check jest.config.js
transformIgnorePatterns should include '/node_modules/'
```

#### Issue: Tests fail with "TextEncoder is not defined"
```javascript
Solution: Add to jest.setup.js:
import { TextEncoder, TextDecoder } from 'util';
global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;
```

## Quick Test Examples

### Backend: Add a New Service Test

```java
// src/test/java/com/eduforum/api/domain/example/service/ExampleServiceTest.java
@ExtendWith(MockitoExtension.class)
class ExampleServiceTest {
    @Mock
    private ExampleRepository repository;

    @InjectMocks
    private ExampleService service;

    @Test
    @DisplayName("Should do something successfully")
    void testMethod_Success() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        // When
        Result result = service.doSomething(1L);

        // Then
        assertThat(result).isNotNull();
    }
}
```

### Frontend: Add a New Component Test

```tsx
// src/__tests__/components/example.test.tsx
import { render, screen } from '@testing-library/react';
import { Example } from '@/components/example';

describe('Example Component', () => {
  it('renders correctly', () => {
    render(<Example />);
    expect(screen.getByText('Hello')).toBeInTheDocument();
  });
});
```

## Running Tests in CI/CD

### GitHub Actions

Create `.github/workflows/test.yml`:

```yaml
name: Run Tests

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
      - name: Test Backend
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
      - name: Install & Test Frontend
        run: |
          cd apps/frontend
          npm ci
          npm test
```

## Test Coverage Reports

### Backend Coverage (JaCoCo)

```bash
cd apps/backend
./gradlew test jacocoTestReport

# View report at:
# apps/backend/build/reports/jacoco/test/html/index.html
```

### Frontend Coverage (Jest)

```bash
cd apps/frontend
npm run test:coverage

# View report at:
# apps/frontend/coverage/lcov-report/index.html
```

## Next Steps

1. **Run initial tests** to verify setup
2. **Add tests** for new features as you develop
3. **Maintain coverage** above 80%
4. **Review test reports** regularly
5. **Update tests** when refactoring code

## Test File Locations

### Backend
- Application test: `/mnt/d/Development/git/minerva/apps/backend/src/test/java/com/eduforum/api/EduForumApplicationTests.java`
- Auth service tests: `/mnt/d/Development/git/minerva/apps/backend/src/test/java/com/eduforum/api/domain/auth/service/AuthServiceTest.java`
- Auth controller tests: `/mnt/d/Development/git/minerva/apps/backend/src/test/java/com/eduforum/api/domain/auth/controller/AuthControllerTest.java`
- Course service tests: `/mnt/d/Development/git/minerva/apps/backend/src/test/java/com/eduforum/api/domain/course/service/CourseServiceTest.java`
- Test config: `/mnt/d/Development/git/minerva/apps/backend/src/test/resources/application-test.yml`

### Frontend
- Jest config: `/mnt/d/Development/git/minerva/apps/frontend/jest.config.js`
- Jest setup: `/mnt/d/Development/git/minerva/apps/frontend/jest.setup.js`
- Button tests: `/mnt/d/Development/git/minerva/apps/frontend/src/__tests__/components/ui/button.test.tsx`
- Auth hook tests: `/mnt/d/Development/git/minerva/apps/frontend/src/__tests__/hooks/use-auth.test.ts`
- Utils tests: `/mnt/d/Development/git/minerva/apps/frontend/src/__tests__/lib/utils.test.ts`

## Resources

- Backend README: `/mnt/d/Development/git/minerva/apps/backend/src/test/README.md`
- Frontend README: `/mnt/d/Development/git/minerva/apps/frontend/src/__tests__/README.md`
- Full Documentation: `/mnt/d/Development/git/minerva/TEST_INFRASTRUCTURE.md`

## Support

For issues or questions:
1. Check the README files in test directories
2. Review the test examples provided
3. Consult the troubleshooting section above
4. Check Jest/JUnit documentation for specific errors
