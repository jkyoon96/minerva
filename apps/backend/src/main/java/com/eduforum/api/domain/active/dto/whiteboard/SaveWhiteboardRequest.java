package com.eduforum.api.domain.active.dto.whiteboard;

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
public class SaveWhiteboardRequest {

    private List<WhiteboardElementDto> elements;
    private Map<String, Object> canvasSettings;
    private Boolean clearExisting;
}
