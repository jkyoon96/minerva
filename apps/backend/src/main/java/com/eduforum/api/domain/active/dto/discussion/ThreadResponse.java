package com.eduforum.api.domain.active.dto.discussion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadResponse {

    private Long id;
    private Long roomId;
    private Long creatorId;
    private String creatorName;
    private Long parentThreadId;
    private String title;
    private String content;
    private Boolean isPinned;
    private Long replyCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
