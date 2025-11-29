package com.eduforum.api.domain.active.dto.breakout;

import com.eduforum.api.domain.active.entity.AssignmentMethod;
import com.eduforum.api.domain.active.entity.BreakoutStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakoutResponse {

    private Long id;
    private Long seminarRoomId;
    private String name;
    private BreakoutStatus status;
    private AssignmentMethod assignmentMethod;
    private Integer maxParticipants;
    private Long participantCount;
    private Integer durationMinutes;
    private OffsetDateTime startedAt;
    private OffsetDateTime endsAt;
    private String meetingUrl;
    private Map<String, Object> settings;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
