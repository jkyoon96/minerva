package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "세션 응답")
public class SessionResponse {

    @Schema(description = "세션 ID", example = "1")
    private Long id;

    @Schema(description = "코스 ID", example = "1")
    private Long courseId;

    @Schema(description = "세션 제목", example = "Week 1: Introduction to Programming")
    private String title;

    @Schema(description = "세션 설명")
    private String description;

    @Schema(description = "예정 시간")
    private OffsetDateTime scheduledAt;

    @Schema(description = "세션 시간(분)", example = "90")
    private Integer durationMinutes;

    @Schema(description = "세션 상태", example = "SCHEDULED")
    private String status;

    @Schema(description = "시작 시간")
    private OffsetDateTime startedAt;

    @Schema(description = "종료 시간")
    private OffsetDateTime endedAt;

    @Schema(description = "미팅 URL")
    private String meetingUrl;

    @Schema(description = "세션 설정")
    private Map<String, Object> settings;

    @Schema(description = "생성일시")
    private OffsetDateTime createdAt;

    @Schema(description = "수정일시")
    private OffsetDateTime updatedAt;
}
