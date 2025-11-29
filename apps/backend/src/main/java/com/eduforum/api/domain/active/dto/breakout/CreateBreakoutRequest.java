package com.eduforum.api.domain.active.dto.breakout;

import com.eduforum.api.domain.active.entity.AssignmentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBreakoutRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Assignment method is required")
    private AssignmentMethod assignmentMethod;

    private Integer maxParticipants;
    private Integer durationMinutes;
    private String meetingUrl;
    private Map<String, Object> settings;
}
