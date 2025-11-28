package com.eduforum.api.domain.seminar.dto;

import com.eduforum.api.domain.seminar.entity.ParticipantRole;
import com.eduforum.api.domain.seminar.entity.ParticipantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "참가자 정보 응답")
public class ParticipantResponse {

    @Schema(description = "참가자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "10")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "사용자 이메일", example = "hong@example.com")
    private String userEmail;

    @Schema(description = "참가자 역할", example = "PARTICIPANT")
    private ParticipantRole role;

    @Schema(description = "참가자 상태", example = "JOINED")
    private ParticipantStatus status;

    @Schema(description = "손들기 여부", example = "false")
    private Boolean isHandRaised;

    @Schema(description = "음소거 여부", example = "false")
    private Boolean isMuted;

    @Schema(description = "비디오 활성화 여부", example = "true")
    private Boolean isVideoOn;

    @Schema(description = "화면 공유 여부", example = "false")
    private Boolean isScreenSharing;

    @Schema(description = "참가 시간")
    private OffsetDateTime joinedAt;

    @Schema(description = "퇴장 시간")
    private OffsetDateTime leftAt;
}
