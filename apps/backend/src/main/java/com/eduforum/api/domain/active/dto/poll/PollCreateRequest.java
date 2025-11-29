package com.eduforum.api.domain.active.dto.poll;

import com.eduforum.api.domain.active.entity.PollType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollCreateRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotBlank(message = "Question is required")
    private String question;

    @NotNull(message = "Poll type is required")
    private PollType type;

    private List<PollOptionRequest> options;

    private Boolean allowMultiple;

    private Boolean showResults;

    private Boolean anonymous;

    private Map<String, Object> settings;
}
