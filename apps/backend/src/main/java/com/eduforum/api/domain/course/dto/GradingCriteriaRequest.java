package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for creating/updating grading criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "평가 기준 생성/수정 요청")
public class GradingCriteriaRequest {

    @NotBlank(message = "평가 기준 이름은 필수입니다")
    @Schema(description = "평가 기준 이름", example = "과제 완성도")
    private String name;

    @Schema(description = "평가 기준 설명", example = "과제의 완성도와 품질을 평가합니다")
    private String description;

    @NotNull(message = "가중치는 필수입니다")
    @PositiveOrZero(message = "가중치는 0 이상이어야 합니다")
    @Schema(description = "가중치 (%)", example = "30.0")
    private BigDecimal weight;

    @NotNull(message = "최대 점수는 필수입니다")
    @PositiveOrZero(message = "최대 점수는 0 이상이어야 합니다")
    @Schema(description = "최대 점수", example = "100.0")
    private BigDecimal maxScore;

    @Schema(description = "표시 순서", example = "0")
    private Integer orderIndex;

    @Valid
    @Schema(description = "루브릭 항목 목록")
    private List<RubricItemDto> rubricItems;
}
