package com.eduforum.api.domain.active.dto.breakout;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMessageRequest {

    @NotBlank(message = "Message is required")
    private String message;

    private String messageType;
}
