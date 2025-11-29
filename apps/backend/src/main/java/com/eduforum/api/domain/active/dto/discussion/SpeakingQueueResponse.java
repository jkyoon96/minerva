package com.eduforum.api.domain.active.dto.discussion;

import com.eduforum.api.domain.active.entity.SpeakingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingQueueResponse {

    private Long id;
    private Long roomId;
    private Long userId;
    private String userName;
    private SpeakingStatus status;
    private Integer queuePosition;
    private OffsetDateTime grantedAt;
    private OffsetDateTime finishedAt;
    private Integer speakingDurationSeconds;
    private OffsetDateTime createdAt;
}
