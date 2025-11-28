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
@Schema(description = "학생 대시보드 응답")
public class StudentDashboardResponse {

    @Schema(description = "수강 코스 목록")
    private List<StudentCourse> courses;

    @Schema(description = "마감 임박 과제 목록")
    private List<UpcomingAssignment> upcomingAssignments;

    @Schema(description = "다가오는 세션 목록")
    private List<UpcomingSession> upcomingSessions;

    @Schema(description = "최근 성적 목록")
    private List<RecentGrade> recentGrades;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "학생 코스 정보")
    public static class StudentCourse {
        @Schema(description = "코스 ID", example = "1")
        private Long id;

        @Schema(description = "코스 코드", example = "CS101")
        private String code;

        @Schema(description = "코스 제목", example = "Introduction to Computer Science")
        private String title;

        @Schema(description = "교수 이름", example = "김교수")
        private String professorName;

        @Schema(description = "썸네일 URL")
        private String thumbnailUrl;

        @Schema(description = "다음 세션 시간")
        private OffsetDateTime nextSessionAt;

        @Schema(description = "미제출 과제 수", example = "2")
        private Integer pendingAssignments;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "마감 임박 과제")
    public static class UpcomingAssignment {
        @Schema(description = "과제 ID", example = "1")
        private Long id;

        @Schema(description = "코스 제목", example = "Introduction to Computer Science")
        private String courseTitle;

        @Schema(description = "과제 제목", example = "Homework 1")
        private String assignmentTitle;

        @Schema(description = "마감일")
        private OffsetDateTime dueDate;

        @Schema(description = "제출 여부", example = "false")
        private Boolean isSubmitted;
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "최근 성적")
    public static class RecentGrade {
        @Schema(description = "과제 ID", example = "1")
        private Long assignmentId;

        @Schema(description = "코스 제목", example = "Introduction to Computer Science")
        private String courseTitle;

        @Schema(description = "과제 제목", example = "Homework 1")
        private String assignmentTitle;

        @Schema(description = "점수", example = "95")
        private Integer score;

        @Schema(description = "최대 점수", example = "100")
        private Integer maxScore;

        @Schema(description = "채점일시")
        private OffsetDateTime gradedAt;
    }
}
