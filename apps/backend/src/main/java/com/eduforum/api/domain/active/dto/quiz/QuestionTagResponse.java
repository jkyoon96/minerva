package com.eduforum.api.domain.active.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTagResponse {

    private Long id;
    private String name;
    private String color;
}
