package com.eduforum.api.domain.seminar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "화면 공유 요청")
public class ScreenShareRequest {

    @NotNull(message = "룸 ID는 필수입니다")
    @Schema(description = "룸 ID", example = "1")
    private Long roomId;

    @Schema(description = "화면 공유 활성화 여부", example = "true")
    private Boolean enabled;
}
