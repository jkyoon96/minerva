package com.eduforum.api.domain.analytics.websocket;

import com.eduforum.api.domain.analytics.dto.realtime.LiveStatsResponse;
import com.eduforum.api.domain.analytics.service.RealTimeAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for streaming real-time analytics
 * Endpoint: /ws/analytics/{sessionId}
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AnalyticsWebSocketController {

    private final RealTimeAnalyticsService analyticsService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/analytics/{sessionId}/subscribe")
    @SendTo("/topic/analytics/{sessionId}")
    public LiveStatsResponse subscribeToAnalytics(@DestinationVariable Long sessionId) {
        log.info("Client subscribed to analytics for session: {}", sessionId);
        return analyticsService.getLiveSessionStats(sessionId);
    }

    @Scheduled(fixedRate = 5000) // Broadcast updates every 5 seconds
    public void broadcastAnalyticsUpdates() {
        // This would normally iterate through active sessions
        // For now, it's a placeholder for the WebSocket streaming functionality
        log.debug("Broadcasting analytics updates (scheduled)");
    }

    public void sendAnalyticsUpdate(Long sessionId, LiveStatsResponse stats) {
        messagingTemplate.convertAndSend("/topic/analytics/" + sessionId, stats);
    }
}
