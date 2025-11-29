package com.eduforum.api.domain.active.dto.whiteboard;

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
public class WhiteboardResponse {

    private Long id;
    private Long roomId;
    private String name;
    private Map<String, Object> canvasSettings;
    private List<WhiteboardElementDto> elements;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
