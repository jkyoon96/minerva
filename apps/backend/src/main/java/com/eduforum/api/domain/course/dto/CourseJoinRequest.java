package com.eduforum.api.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "코스 가입 요청")
public class CourseJoinRequest {

    @NotBlank(message = "초대 코드는 필수입니다")
    @Schema(description = "초대 코드", example = "ABC123XYZ")
    private String inviteCode;
}
