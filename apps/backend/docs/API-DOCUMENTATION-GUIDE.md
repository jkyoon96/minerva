# API 문서화 가이드

## 목차
1. [Swagger UI 접속 방법](#swagger-ui-접속-방법)
2. [API 문서화 규칙](#api-문서화-규칙)
3. [어노테이션 사용 가이드](#어노테이션-사용-가이드)
4. [공통 응답 형식](#공통-응답-형식)
5. [예제 코드](#예제-코드)

---

## Swagger UI 접속 방법

### 로컬 개발 환경
애플리케이션 실행 후 아래 URL로 접속:

- **Swagger UI**: http://localhost:8000/api/docs/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8000/api/docs/api-docs

### 개발 서버
- **Swagger UI**: http://210.115.229.12:8000/api/docs/swagger-ui.html

### 인증이 필요한 API 테스트
1. Swagger UI 우측 상단의 `Authorize` 버튼 클릭
2. JWT 액세스 토큰 입력 (Bearer 접두사 불필요)
3. `Authorize` 클릭하여 토큰 등록
4. 이후 API 호출 시 자동으로 헤더에 토큰 포함

---

## API 문서화 규칙

### 1. 필수 어노테이션

모든 REST 컨트롤러는 다음 어노테이션을 포함해야 합니다:

```java
@Tag(name = "태그명", description = "태그 설명")
```

### 2. 태그 분류

| 태그 | 설명 |
|------|------|
| Authentication | 인증 및 권한 관리 API |
| Users | 사용자 관리 API |
| Courses | 코스 관리 API |
| Sessions | 세션 관리 API |
| Live | 실시간 강의 API |
| Polls | 투표 API |
| Quizzes | 퀴즈 API |
| Grades | 성적 관리 API |
| Analytics | 학습 분석 API |
| Health | 헬스체크 API |

### 3. API 메서드 정렬

Swagger UI에서 다음 순서로 정렬됩니다:
- HTTP 메서드별: GET → POST → PUT → PATCH → DELETE
- 태그별: 알파벳 순

---

## 어노테이션 사용 가이드

### 컨트롤러 레벨

```java
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "사용자 관리 API")
public class UserController {
    // ...
}
```

### API 메서드 레벨

```java
@Operation(
    summary = "사용자 목록 조회",
    description = "페이징을 지원하는 사용자 목록을 조회합니다."
)
@ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PageResponse.class),
            examples = @ExampleObject(
                value = """
                    {
                      "status": 200,
                      "message": "Success",
                      "data": {
                        "content": [...],
                        "page": 0,
                        "size": 20,
                        "totalElements": 100,
                        "totalPages": 5
                      }
                    }
                    """
            )
        )
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
    )
})
@GetMapping
public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getUsers(
    @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
    @RequestParam(defaultValue = "0") int page,

    @Parameter(description = "페이지 크기", example = "20")
    @RequestParam(defaultValue = "20") int size
) {
    // 구현
}
```

### DTO 레벨

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 생성 요청")
public class CreateUserRequest {

    @Schema(description = "이메일", example = "student@minerva.edu", required = true)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @Schema(description = "이름", example = "홍길동", required = true)
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 이내여야 합니다")
    private String name;

    @Schema(
        description = "역할",
        example = "STUDENT",
        required = true,
        allowableValues = {"STUDENT", "PROFESSOR", "ADMIN"}
    )
    @NotBlank(message = "역할은 필수입니다")
    private String role;
}
```

---

## 공통 응답 형식

### 성공 응답 - ApiResponse<T>

```json
{
  "status": 200,
  "message": "Success",
  "data": {
    // 응답 데이터
  },
  "meta": {
    // 메타데이터 (페이징 정보 등, 선택사항)
  },
  "timestamp": "2025-11-29T10:30:00"
}
```

**필드 설명**:
- `status` (int): HTTP 상태 코드
- `message` (string): 응답 메시지
- `data` (object): 응답 데이터
- `meta` (object, optional): 메타데이터
- `timestamp` (string): 응답 시간

### 에러 응답 - ApiErrorResponse

```json
{
  "status": 400,
  "message": "잘못된 요청입니다",
  "timestamp": "2025-11-29T10:30:00"
}
```

**필드 설명**:
- `status` (int): HTTP 상태 코드
- `message` (string): 에러 메시지
- `timestamp` (string): 응답 시간

### 유효성 검증 에러 응답 - ValidationErrorResponse

유효성 검증 실패 시 필드별 에러 정보를 포함합니다:

```json
{
  "status": 400,
  "message": "입력값이 올바르지 않습니다",
  "data": {
    "email": "이메일 형식이 올바르지 않습니다",
    "password": "비밀번호는 8-20자 이내여야 합니다"
  },
  "timestamp": "2025-11-29T10:30:00"
}
```

---

## 예제 코드

### 1. 기본 GET API

```java
@Operation(summary = "사용자 상세 조회")
@ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "조회 성공"
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "사용자를 찾을 수 없음",
        content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
    )
})
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserDto>> getUser(
    @Parameter(description = "사용자 ID", example = "1")
    @PathVariable Long id
) {
    UserDto user = userService.findById(id);
    return ResponseEntity.ok(ApiResponse.success(user));
}
```

### 2. POST API (요청 본문 포함)

```java
@Operation(summary = "사용자 생성")
@ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "생성 성공"
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
    )
})
@PostMapping
public ResponseEntity<ApiResponse<UserDto>> createUser(
    @Valid @RequestBody CreateUserRequest request
) {
    UserDto user = userService.create(request);
    return ResponseEntity.ok(ApiResponse.success("사용자가 생성되었습니다", user));
}
```

### 3. 페이징 API

```java
@Operation(summary = "사용자 목록 조회 (페이징)")
@GetMapping
public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getUsers(
    @ParameterObject PageRequest pageRequest
) {
    PageResponse<UserDto> users = userService.findAll(pageRequest);
    return ResponseEntity.ok(ApiResponse.success(users));
}
```

### 4. 인증이 필요한 API

```java
@Operation(
    summary = "내 정보 조회",
    security = @SecurityRequirement(name = "bearerAuth")
)
@GetMapping("/me")
public ResponseEntity<ApiResponse<UserDto>> getMyInfo(
    @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
) {
    UserDto user = userService.findByEmail(userDetails.getUsername());
    return ResponseEntity.ok(ApiResponse.success(user));
}
```

---

## Bean Validation 어노테이션

DTO에서 사용 가능한 유효성 검증 어노테이션:

| 어노테이션 | 설명 | 예시 |
|-----------|------|------|
| `@NotNull` | null 불가 | `@NotNull private Long id;` |
| `@NotBlank` | null, 빈 문자열 불가 | `@NotBlank private String name;` |
| `@NotEmpty` | null, 빈 컬렉션 불가 | `@NotEmpty private List<String> tags;` |
| `@Email` | 이메일 형식 검증 | `@Email private String email;` |
| `@Size` | 문자열/컬렉션 크기 제한 | `@Size(min=2, max=50) private String name;` |
| `@Min` / `@Max` | 숫자 범위 제한 | `@Min(0) @Max(100) private Integer score;` |
| `@Pattern` | 정규식 패턴 검증 | `@Pattern(regexp="^[0-9]+$") private String phone;` |
| `@Past` / `@Future` | 날짜 범위 검증 | `@Past private LocalDate birthDate;` |

---

## 공통 에러 응답 참조

SwaggerConfig에 정의된 공통 에러 응답을 재사용할 수 있습니다:

```java
@ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "권한 없음",
        content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "리소스를 찾을 수 없음",
        content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = @Content(schema = @Schema(ref = "#/components/schemas/ApiErrorResponse"))
    )
})
```

---

## 참고 자료

- [SpringDoc OpenAPI 공식 문서](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://swagger.io/specification/)
- [Bean Validation 2.0 Reference](https://beanvalidation.org/2.0/)
- [프로젝트 API 설계서](../../../docs/07-api-specification.md)

---

## 업데이트 이력

| 날짜 | 버전 | 변경 내용 |
|------|------|----------|
| 2025-11-29 | 1.0.0 | 초기 문서 작성 |
