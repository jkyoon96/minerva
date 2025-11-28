package com.eduforum.api.domain.seminar.dto;

import com.eduforum.api.domain.seminar.entity.WebSocketEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "WebSocket 메시지")
public class WebSocketMessage<T> {

    @Schema(description = "이벤트 타입", example = "PARTICIPANT_JOINED")
    private WebSocketEventType eventType;

    @Schema(description = "룸 ID", example = "1")
    private Long roomId;

    @Schema(description = "발신자 ID", example = "10")
    private Long senderId;

    @Schema(description = "메시지 데이터")
    private T data;

    @Schema(description = "타임스탬프")
    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public static <T> WebSocketMessage<T> create(WebSocketEventType eventType, Long roomId, Long senderId, T data) {
        return WebSocketMessage.<T>builder()
            .eventType(eventType)
            .roomId(roomId)
            .senderId(senderId)
            .data(data)
            .build();
    }
}
