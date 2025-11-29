package com.eduforum.api.domain.active.dto.poll;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollOptionRequest {

    @NotBlank(message = "Option text is required")
    private String text;

    private Integer order;

    private Boolean isCorrect;
}
