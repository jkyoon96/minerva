package com.eduforum.api.domain.course.dto;

import com.eduforum.api.domain.course.entity.EnrollmentRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "초대 링크 생성 요청")
public class InviteLinkCreateRequest {

    @NotNull(message = "역할은 필수입니다")
    @Schema(description = "역할 (STUDENT, TA)", example = "STUDENT")
    private EnrollmentRole role;

    @Min(value = 1, message = "최대 사용 횟수는 1 이상이어야 합니다")
    @Schema(description = "최대 사용 횟수 (null이면 무제한)", example = "50")
    private Integer maxUses;

    @Future(message = "만료 시간은 미래여야 합니다")
    @Schema(description = "만료 시간 (null이면 무기한)", example = "2024-12-31T23:59:59Z")
    private OffsetDateTime expiresAt;
}
