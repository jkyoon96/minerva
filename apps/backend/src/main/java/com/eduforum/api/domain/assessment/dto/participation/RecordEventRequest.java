package com.eduforum.api.domain.assessment.dto.participation;

import com.eduforum.api.domain.assessment.entity.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Record participation event request")
public class RecordEventRequest {

    @NotNull
    @Schema(description = "Student ID", example = "1")
    private Long studentId;

    @NotNull
    @Schema(description = "Course ID", example = "1")
    private Long courseId;

    @Schema(description = "Session ID", example = "1")
    private Long sessionId;

    @NotNull
    @Schema(description = "Event type")
    private EventType eventType;

    @Schema(description = "Points", example = "1")
    @Builder.Default
    private Integer points = 1;

    @Schema(description = "Event data")
    private Map<String, Object> eventData;
}
