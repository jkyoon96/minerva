package com.eduforum.api.domain.active.dto.poll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollSubmitRequest {

    private List<Long> selectedOptionIds;
    private String textResponse;
}
