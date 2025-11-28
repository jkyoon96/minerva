package com.eduforum.api.domain.seminar.dto;

import com.eduforum.api.domain.seminar.entity.LayoutType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "레이아웃 변경 요청")
public class LayoutUpdateRequest {

    @NotNull(message = "레이아웃 타입은 필수입니다")
    @Schema(description = "레이아웃 타입", example = "SPEAKER")
    private LayoutType layout;
}
