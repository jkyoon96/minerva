package com.eduforum.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * OpenAPI 3.0 (Swagger) 설정
 * - 태그 그룹화
 * - 공통 응답 스키마
 * - 에러 응답 예시
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:EduForum API}")
    private String applicationName;

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "bearerAuth";

        return new OpenAPI()
            .info(apiInfo())
            .servers(apiServers())
            .tags(apiTags())
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, securityScheme())
                .addSchemas("ApiResponse", apiResponseSchema())
                .addSchemas("ApiErrorResponse", apiErrorResponseSchema())
                .addSchemas("ValidationErrorResponse", validationErrorResponseSchema())
                .addResponses("BadRequest", badRequestResponse())
                .addResponses("Unauthorized", unauthorizedResponse())
                .addResponses("Forbidden", forbiddenResponse())
                .addResponses("NotFound", notFoundResponse())
                .addResponses("InternalServerError", internalServerErrorResponse())
            );
    }

    private Info apiInfo() {
        return new Info()
            .title(applicationName)
            .description("""
                ## EduForum API 문서

                미네르바 대학의 Active Learning Forum을 참고한 교육 플랫폼 API입니다.

                ### 주요 기능
                - 사용자 인증 및 권한 관리
                - 코스 및 세션 관리
                - 실시간 강의 및 액티브 러닝 도구
                - 평가 및 피드백 시스템
                - 학습 분석 및 리포트

                ### 인증 방식
                JWT Bearer Token을 사용합니다. 로그인 후 받은 액세스 토큰을 'Authorize' 버튼을 통해 등록하세요.

                ### 공통 응답 형식
                - 성공: `ApiResponse<T>` 형식
                - 실패: `ApiErrorResponse` 형식
                - 유효성 검증 실패: `ValidationErrorResponse` 형식
                """)
            .version("v1.0.0")
            .contact(new Contact()
                .name("EduForum Team")
                .email("dev@eduforum.com")
                .url("https://github.com/eduforum/minerva"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> apiServers() {
        return List.of(
            new Server().url("http://localhost:8000/api").description("로컬 개발 환경"),
            new Server().url("http://210.115.229.12:8000/api").description("개발 서버"),
            new Server().url("https://staging.eduforum.com/api").description("스테이징 서버"),
            new Server().url("https://api.eduforum.com/api").description("운영 서버")
        );
    }

    private List<Tag> apiTags() {
        return List.of(
            new Tag().name("Authentication").description("인증 및 권한 관리 API"),
            new Tag().name("Users").description("사용자 관리 API"),
            new Tag().name("Courses").description("코스 관리 API"),
            new Tag().name("Sessions").description("세션 관리 API"),
            new Tag().name("Seminar Rooms").description("세미나 룸 관리 API"),
            new Tag().name("Participants").description("룸 참가자 관리 API"),
            new Tag().name("Chat").description("채팅 메시지 API"),
            new Tag().name("Chat WebSocket").description("실시간 채팅 WebSocket API"),
            new Tag().name("Reactions").description("반응 API"),
            new Tag().name("Live").description("실시간 강의 API"),
            new Tag().name("Polls").description("투표 API"),
            new Tag().name("Quizzes").description("퀴즈 API"),
            new Tag().name("Grades").description("성적 관리 API"),
            new Tag().name("Analytics").description("학습 분석 API"),
            new Tag().name("Health").description("헬스체크 API")
        );
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
            .name("bearerAuth")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT 액세스 토큰을 입력하세요 (Bearer 접두사 불필요)");
    }

    // 공통 응답 스키마
    private Schema<?> apiResponseSchema() {
        return new Schema<>()
            .type("object")
            .description("표준 성공 응답")
            .addProperty("success", new Schema<>().type("boolean").description("성공 여부").example(true))
            .addProperty("message", new Schema<>().type("string").description("응답 메시지").example("요청이 성공적으로 처리되었습니다"))
            .addProperty("data", new Schema<>().type("object").description("응답 데이터"))
            .addProperty("timestamp", new Schema<>().type("string").format("date-time").description("응답 시간"));
    }

    private Schema<?> apiErrorResponseSchema() {
        return new Schema<>()
            .type("object")
            .description("표준 에러 응답")
            .addProperty("success", new Schema<>().type("boolean").description("성공 여부").example(false))
            .addProperty("message", new Schema<>().type("string").description("에러 메시지").example("요청 처리 중 오류가 발생했습니다"))
            .addProperty("errorCode", new Schema<>().type("string").description("에러 코드").example("COMMON_001"))
            .addProperty("timestamp", new Schema<>().type("string").format("date-time").description("응답 시간"));
    }

    private Schema<?> validationErrorResponseSchema() {
        return new Schema<>()
            .type("object")
            .description("유효성 검증 에러 응답")
            .addProperty("success", new Schema<>().type("boolean").description("성공 여부").example(false))
            .addProperty("message", new Schema<>().type("string").description("에러 메시지").example("입력 값이 올바르지 않습니다"))
            .addProperty("errorCode", new Schema<>().type("string").description("에러 코드").example("VALIDATION_ERROR"))
            .addProperty("errors", new Schema<>().type("array").description("필드별 에러 목록")
                .items(new Schema<>()
                    .addProperty("field", new Schema<>().type("string").example("email"))
                    .addProperty("message", new Schema<>().type("string").example("이메일 형식이 올바르지 않습니다"))
                    .addProperty("rejectedValue", new Schema<>().type("string").example("invalid-email"))))
            .addProperty("timestamp", new Schema<>().type("string").format("date-time").description("응답 시간"));
    }

    // 공통 에러 응답
    private ApiResponse badRequestResponse() {
        return new ApiResponse()
            .description("잘못된 요청")
            .content(new Content().addMediaType("application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ValidationErrorResponse"))
                    .example(Map.of(
                        "success", false,
                        "message", "입력 값이 올바르지 않습니다",
                        "errorCode", "VALIDATION_ERROR",
                        "errors", List.of(
                            Map.of("field", "email", "message", "이메일 형식이 올바르지 않습니다", "rejectedValue", "invalid-email")
                        ),
                        "timestamp", "2025-11-29T10:30:00"
                    ))));
    }

    private ApiResponse unauthorizedResponse() {
        return new ApiResponse()
            .description("인증 실패")
            .content(new Content().addMediaType("application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse"))
                    .example(Map.of(
                        "success", false,
                        "message", "인증에 실패했습니다",
                        "errorCode", "AUTH_001",
                        "timestamp", "2025-11-29T10:30:00"
                    ))));
    }

    private ApiResponse forbiddenResponse() {
        return new ApiResponse()
            .description("권한 없음")
            .content(new Content().addMediaType("application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse"))
                    .example(Map.of(
                        "success", false,
                        "message", "접근 권한이 없습니다",
                        "errorCode", "AUTH_002",
                        "timestamp", "2025-11-29T10:30:00"
                    ))));
    }

    private ApiResponse notFoundResponse() {
        return new ApiResponse()
            .description("리소스를 찾을 수 없음")
            .content(new Content().addMediaType("application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse"))
                    .example(Map.of(
                        "success", false,
                        "message", "요청한 리소스를 찾을 수 없습니다",
                        "errorCode", "COMMON_002",
                        "timestamp", "2025-11-29T10:30:00"
                    ))));
    }

    private ApiResponse internalServerErrorResponse() {
        return new ApiResponse()
            .description("서버 내부 오류")
            .content(new Content().addMediaType("application/json",
                new MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse"))
                    .example(Map.of(
                        "success", false,
                        "message", "서버 내부 오류가 발생했습니다",
                        "errorCode", "COMMON_003",
                        "timestamp", "2025-11-29T10:30:00"
                    ))));
    }
}
