package com.eduforum.api.domain.seminar.repository;

import com.eduforum.api.domain.seminar.entity.ChatMessage;
import com.eduforum.api.domain.seminar.entity.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for ChatMessage entity
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Find messages by room ID (paginated)
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.room.id = :roomId AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    Page<ChatMessage> findByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    /**
     * Find recent messages
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.room.id = :roomId AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    List<ChatMessage> findRecentMessages(@Param("roomId") Long roomId, Pageable pageable);

    /**
     * Find messages after timestamp (for real-time sync)
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.room.id = :roomId AND m.createdAt > :since AND m.deletedAt IS NULL ORDER BY m.createdAt")
    List<ChatMessage> findMessagesSince(@Param("roomId") Long roomId, @Param("since") OffsetDateTime since);

    /**
     * Find messages by type
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.room.id = :roomId AND m.messageType = :type AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    List<ChatMessage> findByRoomIdAndMessageType(@Param("roomId") Long roomId, @Param("type") MessageType type);

    /**
     * Count messages in room
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.room.id = :roomId AND m.deletedAt IS NULL")
    Long countByRoomId(@Param("roomId") Long roomId);

    /**
     * Find files shared in room
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.room.id = :roomId AND m.messageType = 'FILE' AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    List<ChatMessage> findFilesByRoomId(@Param("roomId") Long roomId);
}
