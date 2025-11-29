package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for assigning TA to course
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "TA 배정 요청")
public class AssignTARequest {

    @NotNull(message = "TA 사용자 ID는 필수입니다")
    @Schema(description = "TA로 배정할 사용자 ID", example = "123")
    private Long taUserId;

    @Schema(description = "TA 권한 설정")
    private TAPermissions permissions;
}
