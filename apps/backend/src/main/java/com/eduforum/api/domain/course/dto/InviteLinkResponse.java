package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "초대 링크 응답")
public class InviteLinkResponse {

    @Schema(description = "초대 링크 ID", example = "1")
    private Long id;

    @Schema(description = "코스 ID", example = "1")
    private Long courseId;

    @Schema(description = "초대 코드", example = "ABC123XYZ")
    private String code;

    @Schema(description = "역할", example = "STUDENT")
    private String role;

    @Schema(description = "최대 사용 횟수", example = "50")
    private Integer maxUses;

    @Schema(description = "사용된 횟수", example = "10")
    private Integer usedCount;

    @Schema(description = "만료 시간")
    private OffsetDateTime expiresAt;

    @Schema(description = "활성 여부", example = "true")
    private Boolean isActive;

    @Schema(description = "생성일시")
    private OffsetDateTime createdAt;
}
