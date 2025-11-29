package com.eduforum.api.domain.active.dto.poll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollOptionResponse {

    private Long id;
    private String text;
    private Integer order;
    private Boolean isCorrect;
    private Long responseCount;
}
