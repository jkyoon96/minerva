package com.eduforum.api.domain.active.dto.poll;

import com.eduforum.api.domain.active.entity.PollStatus;
import com.eduforum.api.domain.active.entity.PollType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollResponse {

    private Long id;
    private Long courseId;
    private Long creatorId;
    private String creatorName;
    private String question;
    private PollType type;
    private PollStatus status;
    private Boolean allowMultiple;
    private Boolean showResults;
    private Boolean anonymous;
    private List<PollOptionResponse> options;
    private Long responseCount;
    private Map<String, Object> settings;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
