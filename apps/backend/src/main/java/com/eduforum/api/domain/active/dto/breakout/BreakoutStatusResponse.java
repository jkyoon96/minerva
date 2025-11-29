package com.eduforum.api.domain.active.dto.breakout;

import com.eduforum.api.domain.active.entity.BreakoutStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakoutStatusResponse {

    private Long id;
    private String name;
    private BreakoutStatus status;
    private Long participantCount;
    private Integer maxParticipants;
    private OffsetDateTime startedAt;
    private OffsetDateTime endsAt;
    private Integer remainingMinutes;
}
