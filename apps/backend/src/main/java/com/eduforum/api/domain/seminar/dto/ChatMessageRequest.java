package com.eduforum.api.domain.seminar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "채팅 메시지 전송 요청")
public class ChatMessageRequest {

    @NotNull(message = "룸 ID는 필수입니다")
    @Schema(description = "룸 ID", example = "1")
    private Long roomId;

    @NotBlank(message = "메시지 내용은 필수입니다")
    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;
}
