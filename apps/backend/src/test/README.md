# Backend Testing Infrastructure

This directory contains the test infrastructure for the EduForum backend application.

## Test Structure

```
src/test/
├── java/
│   └── com/eduforum/api/
│       ├── EduForumApplicationTests.java          # Context load test
│       └── domain/
│           ├── auth/
│           │   ├── service/
│           │   │   └── AuthServiceTest.java       # Auth service unit tests
│           │   └── controller/
│           │       └── AuthControllerTest.java    # Auth controller tests
│           └── course/
│               └── service/
│                   └── CourseServiceTest.java     # Course service unit tests
└── resources/
    └── application-test.yml                       # Test configuration
```

## Running Tests

### Run all tests
```bash
./gradlew test
```

### Run specific test class
```bash
./gradlew test --tests AuthServiceTest
```

### Run tests with coverage
```bash
./gradlew test jacocoTestReport
```

### Run tests in watch mode (continuous)
```bash
./gradlew test --continuous
```

## Test Configuration

- **Database**: H2 in-memory database (PostgreSQL compatibility mode)
- **Security**: Disabled for most tests
- **Profiles**: Uses `test` profile (application-test.yml)
- **Test Framework**: JUnit 5 (Jupiter)
- **Mocking**: Mockito
- **Assertions**: AssertJ

## Writing Tests

### Unit Tests (Service Layer)

Use `@ExtendWith(MockitoExtension.class)` and mock dependencies:

```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    @Mock
    private MyRepository repository;

    @InjectMocks
    private MyService service;

    @Test
    void testMethod() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        // When
        Result result = service.doSomething(1L);

        // Then
        assertThat(result).isNotNull();
    }
}
```

### Controller Tests

Use `@WebMvcTest` for controller layer testing:

```java
@WebMvcTest(MyController.class)
@ActiveProfiles("test")
class MyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyService service;

    @Test
    void testEndpoint() throws Exception {
        mockMvc.perform(get("/api/endpoint"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").exists());
    }
}
```

### Integration Tests

Use `@SpringBootTest` for full application context:

```java
@SpringBootTest
@ActiveProfiles("test")
class MyIntegrationTest {
    @Autowired
    private MyService service;

    @Test
    void testIntegration() {
        // Test with real beans
    }
}
```

## Best Practices

1. **Naming Convention**: `ClassNameTest.java`
2. **Test Methods**: Use descriptive names with underscores: `methodName_Scenario_ExpectedResult`
3. **Use @DisplayName**: For better test reports
4. **Arrange-Act-Assert**: Structure tests with Given-When-Then comments
5. **Mock External Dependencies**: Don't call real APIs or databases in unit tests
6. **Clean Up**: Use `@BeforeEach` and `@AfterEach` for setup/cleanup
7. **Test Isolation**: Each test should be independent

## Coverage Goals

- **Line Coverage**: Minimum 80%
- **Branch Coverage**: Minimum 70%
- **Critical Paths**: 100% coverage for authentication, authorization, and payment logic

## Troubleshooting

### H2 Database Issues
If you encounter H2-related errors, ensure:
- The H2 dependency is in the test scope
- `application-test.yml` has correct JDBC URL
- Database mode is set to PostgreSQL compatibility

### Mock Issues
If mocks aren't working:
- Check `@MockBean` vs `@Mock` usage
- Ensure proper initialization with `@ExtendWith(MockitoExtension.class)`
- Verify mock setup in `@BeforeEach`

### Security Issues
If security is blocking tests:
- Use `@WithMockUser` for authenticated tests
- Check `application-test.yml` security settings
- Consider `@AutoConfigureMockMvc(addFilters = false)` for controller tests
