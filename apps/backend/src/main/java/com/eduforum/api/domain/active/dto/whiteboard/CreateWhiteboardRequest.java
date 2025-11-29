package com.eduforum.api.domain.active.dto.whiteboard;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWhiteboardRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private Map<String, Object> canvasSettings;
}
