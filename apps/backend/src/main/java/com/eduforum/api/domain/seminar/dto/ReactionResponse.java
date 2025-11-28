package com.eduforum.api.domain.seminar.dto;

import com.eduforum.api.domain.seminar.entity.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "반응 응답")
public class ReactionResponse {

    @Schema(description = "반응 ID", example = "1")
    private Long id;

    @Schema(description = "룸 ID", example = "1")
    private Long roomId;

    @Schema(description = "사용자 ID", example = "10")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "반응 타입", example = "THUMBS_UP")
    private ReactionType reactionType;

    @Schema(description = "반응 이모지", example = "👍")
    private String emoji;

    @Schema(description = "생성 시간")
    private OffsetDateTime createdAt;
}
