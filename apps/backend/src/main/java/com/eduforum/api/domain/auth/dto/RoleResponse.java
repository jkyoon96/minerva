package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "역할 응답")
public class RoleResponse {

    @Schema(description = "역할 ID", example = "1")
    private Long id;

    @Schema(description = "역할 이름", example = "STUDENT")
    private String name;

    @Schema(description = "역할 설명", example = "학생 역할")
    private String description;

    @Schema(description = "생성일시")
    private OffsetDateTime createdAt;
}
