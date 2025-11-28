package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "세션 수정 요청")
public class SessionUpdateRequest {

    @Size(max = 255, message = "세션 제목은 255자 이하여야 합니다")
    @Schema(description = "세션 제목", example = "Week 1: Introduction to Programming")
    private String title;

    @Schema(description = "세션 설명")
    private String description;

    @Schema(description = "예정 시간", example = "2024-12-01T14:00:00Z")
    private OffsetDateTime scheduledAt;

    @Min(value = 15, message = "세션 시간은 최소 15분입니다")
    @Max(value = 480, message = "세션 시간은 최대 480분입니다")
    @Schema(description = "세션 시간(분)", example = "90")
    private Integer durationMinutes;

    @Schema(description = "미팅 URL")
    private String meetingUrl;

    @Schema(description = "세션 설정 (대기실, 녹화 등)")
    private Map<String, Object> settings;
}
