# EduForum Test Infrastructure

This document provides an overview of the testing infrastructure for the Minerva/EduForum project.

## Overview

The project includes comprehensive test infrastructure for both backend (Spring Boot) and frontend (Next.js) applications.

## Backend Testing (Java/Spring Boot)

### Location
`/mnt/d/Development/git/minerva/apps/backend/src/test/`

### Test Files Created

1. **EduForumApplicationTests.java** - Basic context load test
   - Ensures Spring Boot application context loads successfully
   - Smoke test for bean configuration

2. **AuthServiceTest.java** - Auth service unit tests
   - User registration (success, duplicate email, password mismatch, terms not agreed)
   - User login (success, invalid credentials, account locked, suspended account)
   - Logout functionality
   - Uses Mockito for mocking dependencies

3. **AuthControllerTest.java** - Auth controller tests
   - REST API endpoint testing with @WebMvcTest
   - Tests for register, login, 2FA, token refresh, email verification, password reset
   - Validates request/response formats and HTTP status codes

4. **CourseServiceTest.java** - Course service unit tests
   - Course creation (success, duplicate code)
   - Course retrieval
   - Course listing for professors
   - Uses Mockito for repository mocking

5. **application-test.yml** - Test configuration
   - H2 in-memory database (PostgreSQL compatibility mode)
   - Disabled security for tests
   - JWT configuration
   - Optimized logging levels

### Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Java Version**: 17
- **Build Tool**: Gradle
- **Test Framework**: JUnit 5 (Jupiter)
- **Mocking**: Mockito
- **Assertions**: AssertJ
- **Database**: H2 (in-memory, PostgreSQL mode)
- **Security**: Spring Security Test

### Running Backend Tests

```bash
cd /mnt/d/Development/git/minerva/apps/backend

# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests AuthServiceTest

# Run with coverage
./gradlew test jacocoTestReport

# Run in continuous mode
./gradlew test --continuous
```

## Frontend Testing (Next.js/React)

### Location
`/mnt/d/Development/git/minerva/apps/frontend/src/__tests__/`

### Test Files Created

1. **jest.config.js** - Jest configuration for Next.js
   - Module path mapping (@/ alias)
   - CSS and asset mocking
   - Coverage configuration
   - Test environment setup

2. **jest.setup.js** - Jest global setup
   - Testing Library DOM matchers
   - Window.matchMedia mock
   - IntersectionObserver mock
   - ResizeObserver mock

3. **button.test.tsx** - Button component tests
   - Renders with different variants (default, outline, ghost, destructive)
   - Renders with different sizes (sm, default, lg)
   - Handles click events
   - Disabled state
   - Ref forwarding
   - Custom className application

4. **use-auth.test.ts** - Auth store (Zustand) tests
   - Initial state verification
   - User login (success, 2FA, errors)
   - User logout
   - User state updates
   - Error handling

5. **utils.test.ts** - Utility function tests
   - cn() className merger
   - formatDate() Korean date formatting
   - formatDateTime() date and time formatting

### Technology Stack

- **Framework**: Next.js 14, React 18
- **Language**: TypeScript
- **Test Runner**: Jest 29
- **Testing Library**: React Testing Library
- **User Interactions**: @testing-library/user-event
- **State Management**: Zustand (tested)
- **Mocking**: Jest mocks, identity-obj-proxy for CSS

### Running Frontend Tests

```bash
cd /mnt/d/Development/git/minerva/apps/frontend

# Install dependencies (if not already installed)
npm install

# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run specific test file
npm test button.test
```

### Installing Frontend Dependencies

The following dependencies have been added to `package.json`:

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

Run `npm install` to install these dependencies.

## Test Coverage Goals

### Backend
- **Line Coverage**: Minimum 80%
- **Branch Coverage**: Minimum 70%
- **Critical Paths**: 100% (auth, authorization, payments)

### Frontend
- **Statements**: Minimum 80%
- **Branches**: Minimum 75%
- **Functions**: Minimum 80%
- **Lines**: Minimum 80%

## Best Practices

### Backend
1. Use descriptive test method names: `methodName_Scenario_ExpectedResult`
2. Structure tests with Given-When-Then (Arrange-Act-Assert)
3. Mock external dependencies (repositories, APIs)
4. Use `@DisplayName` for readable test reports
5. Keep tests isolated and independent

### Frontend
1. Query by role/label when possible (accessibility-focused)
2. Use `userEvent` for realistic user interactions
3. Test user-visible behavior, not implementation details
4. Mock API calls and external modules
5. Use `waitFor` for async operations
6. Clear mocks between tests

## Documentation

Detailed documentation is available in:
- Backend: `/mnt/d/Development/git/minerva/apps/backend/src/test/README.md`
- Frontend: `/mnt/d/Development/git/minerva/apps/frontend/src/__tests__/README.md`

## Next Steps

1. **Backend**:
   - Run `./gradlew test` to verify all tests pass
   - Add integration tests for critical flows
   - Set up test coverage reporting (JaCoCo)
   - Add tests for remaining domains (seminar, active, assessment, analytics)

2. **Frontend**:
   - Run `npm install` to install test dependencies
   - Run `npm test` to verify all tests pass
   - Add tests for page components
   - Add tests for remaining hooks and utilities
   - Set up CI/CD pipeline with test automation

## CI/CD Integration

### GitHub Actions Example

```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run Backend Tests
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
      - name: Install Dependencies
        run: |
          cd apps/frontend
          npm install
      - name: Run Frontend Tests
        run: |
          cd apps/frontend
          npm test
```

## Troubleshooting

### Backend
- **H2 Database Issues**: Check `application-test.yml` for correct JDBC URL
- **Mock Issues**: Verify `@MockBean` vs `@Mock` usage
- **Security Issues**: Use `@WithMockUser` or disable security in tests

### Frontend
- **"Not wrapped in act(...)"**: Use `act()` for state updates or `waitFor` for async
- **"Unable to find element"**: Use `screen.debug()` to inspect DOM
- **Mock Not Working**: Ensure mock is defined before import, clear mocks between tests

## Summary

The test infrastructure is now fully set up for both backend and frontend. You can:

- Run backend tests with Gradle
- Run frontend tests with Jest
- Write new tests following the established patterns
- Achieve comprehensive test coverage across the application

For more details, refer to the README files in each test directory.
