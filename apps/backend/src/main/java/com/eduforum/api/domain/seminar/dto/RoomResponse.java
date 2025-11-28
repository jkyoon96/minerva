package com.eduforum.api.domain.seminar.dto;

import com.eduforum.api.domain.seminar.entity.LayoutType;
import com.eduforum.api.domain.seminar.entity.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "세미나 룸 응답")
public class RoomResponse {

    @Schema(description = "룸 ID", example = "1")
    private Long id;

    @Schema(description = "세션 ID", example = "1")
    private Long sessionId;

    @Schema(description = "호스트 사용자 ID", example = "1")
    private Long hostId;

    @Schema(description = "호스트 이름", example = "김교수")
    private String hostName;

    @Schema(description = "룸 상태", example = "ACTIVE")
    private RoomStatus status;

    @Schema(description = "최대 참가자 수", example = "100")
    private Integer maxParticipants;

    @Schema(description = "현재 참가자 수", example = "25")
    private Integer currentParticipants;

    @Schema(description = "시작 시간")
    private OffsetDateTime startedAt;

    @Schema(description = "종료 시간")
    private OffsetDateTime endedAt;

    @Schema(description = "미팅 URL")
    private String meetingUrl;

    @Schema(description = "녹화 URL")
    private String recordingUrl;

    @Schema(description = "현재 레이아웃", example = "GALLERY")
    private LayoutType layout;

    @Schema(description = "룸 설정")
    private Map<String, Object> settings;

    @Schema(description = "생성 시간")
    private OffsetDateTime createdAt;

    @Schema(description = "수정 시간")
    private OffsetDateTime updatedAt;
}
