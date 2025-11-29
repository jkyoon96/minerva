package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Response DTO for grading criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "평가 기준 응답")
public class GradingCriteriaResponse {

    @Schema(description = "평가 기준 ID")
    private Long id;

    @Schema(description = "코스 ID")
    private Long courseId;

    @Schema(description = "평가 기준 이름", example = "과제 완성도")
    private String name;

    @Schema(description = "평가 기준 설명", example = "과제의 완성도와 품질을 평가합니다")
    private String description;

    @Schema(description = "가중치 (%)", example = "30.0")
    private BigDecimal weight;

    @Schema(description = "최대 점수", example = "100.0")
    private BigDecimal maxScore;

    @Schema(description = "표시 순서", example = "0")
    private Integer orderIndex;

    @Schema(description = "루브릭 항목 목록")
    private List<RubricItemDto> rubricItems;

    @Schema(description = "생성 시간")
    private OffsetDateTime createdAt;

    @Schema(description = "수정 시간")
    private OffsetDateTime updatedAt;
}
