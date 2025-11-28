package com.eduforum.api.domain.seminar.entity;

/**
 * WebSocket event types for real-time communication
 */
public enum WebSocketEventType {
    // Room events
    PARTICIPANT_JOINED,
    PARTICIPANT_LEFT,
    ROOM_STARTED,
    ROOM_ENDED,

    // Chat events
    CHAT_MESSAGE,
    FILE_SHARED,

    // Interaction events
    HAND_RAISED,
    HAND_LOWERED,
    REACTION,

    // Media events
    SCREEN_SHARE_STARTED,
    SCREEN_SHARE_STOPPED,
    MUTE_CHANGED,
    VIDEO_CHANGED,

    // Layout events
    LAYOUT_CHANGED
}
