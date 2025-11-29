package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Rubric item DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "루브릭 항목")
public class RubricItemDto {

    @Schema(description = "루브릭 항목 ID")
    private Long id;

    @NotBlank(message = "레벨은 필수입니다")
    @Schema(description = "평가 레벨", example = "Excellent")
    private String level;

    @NotBlank(message = "설명은 필수입니다")
    @Schema(description = "레벨 설명", example = "매우 우수한 수준의 이해와 적용")
    private String description;

    @NotNull(message = "점수는 필수입니다")
    @PositiveOrZero(message = "점수는 0 이상이어야 합니다")
    @Schema(description = "점수", example = "100")
    private BigDecimal score;

    @Schema(description = "표시 순서", example = "0")
    private Integer orderIndex;
}
