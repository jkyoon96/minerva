package com.eduforum.api.domain.seminar.repository;

import com.eduforum.api.domain.seminar.entity.RoomStatus;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SeminarRoom entity
 */
@Repository
public interface SeminarRoomRepository extends JpaRepository<SeminarRoom, Long> {

    /**
     * Find room by session ID
     */
    Optional<SeminarRoom> findBySessionId(Long sessionId);

    /**
     * Find all active rooms
     */
    List<SeminarRoom> findByStatus(RoomStatus status);

    /**
     * Find rooms by host user ID
     */
    @Query("SELECT r FROM SeminarRoom r WHERE r.host.id = :hostId AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    List<SeminarRoom> findByHostId(@Param("hostId") Long hostId);

    /**
     * Check if room exists for session
     */
    boolean existsBySessionId(Long sessionId);

    /**
     * Find active room for session
     */
    @Query("SELECT r FROM SeminarRoom r WHERE r.session.id = :sessionId AND r.status IN ('WAITING', 'ACTIVE') AND r.deletedAt IS NULL")
    Optional<SeminarRoom> findActiveRoomBySessionId(@Param("sessionId") Long sessionId);
}
