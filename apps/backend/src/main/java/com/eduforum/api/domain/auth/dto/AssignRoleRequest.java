package com.eduforum.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "역할 할당 요청")
public class AssignRoleRequest {

    @Schema(description = "역할 ID", example = "1", required = true)
    @NotNull(message = "역할 ID는 필수입니다")
    private Long roleId;
}
