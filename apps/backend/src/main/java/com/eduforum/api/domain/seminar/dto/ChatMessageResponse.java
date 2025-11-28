package com.eduforum.api.domain.seminar.dto;

import com.eduforum.api.domain.seminar.entity.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "채팅 메시지 응답")
public class ChatMessageResponse {

    @Schema(description = "메시지 ID", example = "1")
    private Long id;

    @Schema(description = "룸 ID", example = "1")
    private Long roomId;

    @Schema(description = "발신자 ID", example = "10")
    private Long senderId;

    @Schema(description = "발신자 이름", example = "홍길동")
    private String senderName;

    @Schema(description = "메시지 타입", example = "TEXT")
    private MessageType messageType;

    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;

    @Schema(description = "파일 URL")
    private String fileUrl;

    @Schema(description = "파일 이름")
    private String fileName;

    @Schema(description = "파일 크기 (bytes)")
    private Long fileSize;

    @Schema(description = "생성 시간")
    private OffsetDateTime createdAt;
}
