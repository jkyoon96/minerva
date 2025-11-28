package com.eduforum.api.domain.seminar.entity;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * ChatMessage entity - represents a chat message in a seminar room
 */
@Entity
@Table(schema = "seminar", name = "chat_messages",
    indexes = {
        @Index(name = "idx_chat_room_created", columnList = "room_id, created_at DESC")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private SeminarRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, columnDefinition = "message_type")
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    // Helper methods
    public boolean isSystemMessage() {
        return messageType == MessageType.SYSTEM;
    }

    public boolean isFileMessage() {
        return messageType == MessageType.FILE;
    }

    public boolean isTextMessage() {
        return messageType == MessageType.TEXT;
    }
}
