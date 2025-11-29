package com.eduforum.api.domain.active.dto.whiteboard;

import com.eduforum.api.domain.active.entity.WhiteboardTool;
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
public class WhiteboardElementDto {

    @NotBlank(message = "Element ID is required")
    private String elementId;

    @NotNull(message = "Element type is required")
    private WhiteboardTool type;

    @NotNull(message = "X coordinate is required")
    private Double x;

    @NotNull(message = "Y coordinate is required")
    private Double y;

    private Double width;
    private Double height;

    private Map<String, Object> properties;
}
