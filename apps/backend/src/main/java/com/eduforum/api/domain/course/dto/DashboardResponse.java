package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "대시보드 응답 (교수용)")
public class DashboardResponse {

    @Schema(description = "코스 목록")
    private List<DashboardCourse> courses;

    @Schema(description = "다가오는 세션 목록")
    private List<UpcomingSession> upcomingSessions;

    @Schema(description = "채점 대기 과제 수", example = "5")
    private Integer pendingGradingCount;

    @Schema(description = "총 학생 수", example = "120")
    private Integer totalStudents;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "대시보드 코스 정보")
    public static class DashboardCourse {
        @Schema(description = "코스 ID", example = "1")
        private Long id;

        @Schema(description = "코스 코드", example = "CS101")
        private String code;

        @Schema(description = "코스 제목", example = "Introduction to Computer Science")
        private String title;

        @Schema(description = "학기", example = "Fall")
        private String semester;

        @Schema(description = "연도", example = "2024")
        private Integer year;

        @Schema(description = "학생 수", example = "30")
        private Integer studentCount;

        @Schema(description = "다음 세션 시간")
        private OffsetDateTime nextSessionAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "다가오는 세션")
    public static class UpcomingSession {
        @Schema(description = "세션 ID", example = "1")
        private Long id;

        @Schema(description = "코스 제목", example = "Introduction to Computer Science")
        private String courseTitle;

        @Schema(description = "세션 제목", example = "Week 1: Introduction")
        private String sessionTitle;

        @Schema(description = "예정 시간")
        private OffsetDateTime scheduledAt;

        @Schema(description = "시간(분)", example = "90")
        private Integer durationMinutes;
    }
}
