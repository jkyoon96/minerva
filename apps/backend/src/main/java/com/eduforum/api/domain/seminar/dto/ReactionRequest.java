package com.eduforum.api.domain.seminar.dto;

import com.eduforum.api.domain.seminar.entity.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "반응 전송 요청")
public class ReactionRequest {

    @NotNull(message = "룸 ID는 필수입니다")
    @Schema(description = "룸 ID", example = "1")
    private Long roomId;

    @NotNull(message = "반응 타입은 필수입니다")
    @Schema(description = "반응 타입", example = "THUMBS_UP")
    private ReactionType reactionType;
}
