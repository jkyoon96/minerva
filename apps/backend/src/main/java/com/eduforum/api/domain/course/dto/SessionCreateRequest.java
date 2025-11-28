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
@Schema(description = "세션 생성 요청")
public class SessionCreateRequest {

    @NotBlank(message = "세션 제목은 필수입니다")
    @Size(max = 255, message = "세션 제목은 255자 이하여야 합니다")
    @Schema(description = "세션 제목", example = "Week 1: Introduction to Programming")
    private String title;

    @Schema(description = "세션 설명")
    private String description;

    @NotNull(message = "예정 시간은 필수입니다")
    @Future(message = "예정 시간은 미래여야 합니다")
    @Schema(description = "예정 시간", example = "2024-12-01T14:00:00Z")
    private OffsetDateTime scheduledAt;

    @NotNull(message = "세션 시간(분)은 필수입니다")
    @Min(value = 15, message = "세션 시간은 최소 15분입니다")
    @Max(value = 480, message = "세션 시간은 최대 480분입니다")
    @Schema(description = "세션 시간(분)", example = "90")
    @Builder.Default
    private Integer durationMinutes = 90;

    @Schema(description = "미팅 URL")
    private String meetingUrl;

    @Schema(description = "세션 설정 (대기실, 녹화 등)")
    private Map<String, Object> settings;
}
