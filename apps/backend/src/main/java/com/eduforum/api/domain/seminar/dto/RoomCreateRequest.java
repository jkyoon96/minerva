package com.eduforum.api.domain.seminar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "세미나 룸 생성 요청")
public class RoomCreateRequest {

    @NotNull(message = "세션 ID는 필수입니다")
    @Schema(description = "코스 세션 ID", example = "1")
    private Long sessionId;

    @Min(value = 1, message = "최대 참가자 수는 1명 이상이어야 합니다")
    @Max(value = 500, message = "최대 참가자 수는 500명 이하여야 합니다")
    @Schema(description = "최대 참가자 수", example = "100")
    @Builder.Default
    private Integer maxParticipants = 100;

    @Schema(description = "룸 설정 (대기실 활성화, 자동 녹화 등)")
    private Map<String, Object> settings;
}
