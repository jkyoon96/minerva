package com.eduforum.api.domain.seminar.repository;

import com.eduforum.api.domain.seminar.entity.Reaction;
import com.eduforum.api.domain.seminar.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for Reaction entity
 */
@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    /**
     * Find recent reactions in a room
     */
    @Query("SELECT r FROM Reaction r WHERE r.room.id = :roomId AND r.createdAt > :since AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    List<Reaction> findRecentReactions(@Param("roomId") Long roomId, @Param("since") OffsetDateTime since);

    /**
     * Count reactions by type in a room
     */
    @Query("SELECT r.reactionType, COUNT(r) FROM Reaction r WHERE r.room.id = :roomId AND r.deletedAt IS NULL GROUP BY r.reactionType")
    List<Object[]> countReactionsByType(@Param("roomId") Long roomId);

    /**
     * Find reactions in time window
     */
    @Query("SELECT r FROM Reaction r WHERE r.room.id = :roomId AND r.createdAt BETWEEN :start AND :end AND r.deletedAt IS NULL")
    List<Reaction> findReactionsInTimeWindow(@Param("roomId") Long roomId, @Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    /**
     * Delete old reactions (cleanup)
     */
    @Query("DELETE FROM Reaction r WHERE r.createdAt < :before")
    void deleteReactionsBefore(@Param("before") OffsetDateTime before);
}
