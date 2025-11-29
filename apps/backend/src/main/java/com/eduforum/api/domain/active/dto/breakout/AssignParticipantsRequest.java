package com.eduforum.api.domain.active.dto.breakout;

import com.eduforum.api.domain.active.entity.AssignmentMethod;
import jakarta.validation.constraints.NotEmpty;
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
public class AssignParticipantsRequest {

    @NotEmpty(message = "Participant IDs are required")
    private List<Long> participantIds;

    private AssignmentMethod assignmentMethod;

    private Boolean clearExisting;

    // For MANUAL assignment: Map of breakout room ID -> List of user IDs
    private Map<Long, List<Long>> manualAssignments;
}
