package com.eduforum.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 (Swagger) 설정
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:EduForum API}")
    private String applicationName;

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "bearerAuth";

        return new OpenAPI()
            .info(new Info()
                .title(applicationName)
                .description("미네르바 대학의 Active Learning Forum을 참고한 교육 플랫폼 API")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("EduForum Team")
                    .email("dev@eduforum.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server().url("http://localhost:8000/api").description("Local Development"),
                new Server().url("http://210.115.229.12:8000/api").description("Development Server"),
                new Server().url("https://staging.eduforum.com/api").description("Staging Server"),
                new Server().url("https://api.eduforum.com/api").description("Production Server")
            ))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT 토큰을 입력하세요 (Bearer 접두사 불필요)")
                )
            );
    }
}
