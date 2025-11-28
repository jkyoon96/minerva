package com.eduforum.api.domain.seminar.repository;

import com.eduforum.api.domain.seminar.entity.ParticipantStatus;
import com.eduforum.api.domain.seminar.entity.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RoomParticipant entity
 */
@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    /**
     * Find all participants in a room
     */
    @Query("SELECT p FROM RoomParticipant p WHERE p.room.id = :roomId AND p.deletedAt IS NULL ORDER BY p.role, p.joinedAt")
    List<RoomParticipant> findByRoomId(@Param("roomId") Long roomId);

    /**
     * Find active participants in a room
     */
    @Query("SELECT p FROM RoomParticipant p WHERE p.room.id = :roomId AND p.status = :status AND p.deletedAt IS NULL")
    List<RoomParticipant> findByRoomIdAndStatus(@Param("roomId") Long roomId, @Param("status") ParticipantStatus status);

    /**
     * Find participant by room and user
     */
    @Query("SELECT p FROM RoomParticipant p WHERE p.room.id = :roomId AND p.user.id = :userId AND p.deletedAt IS NULL")
    Optional<RoomParticipant> findByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    /**
     * Count active participants in a room
     */
    @Query("SELECT COUNT(p) FROM RoomParticipant p WHERE p.room.id = :roomId AND p.status = 'JOINED' AND p.deletedAt IS NULL")
    Long countActiveParticipants(@Param("roomId") Long roomId);

    /**
     * Find participants with raised hands
     */
    @Query("SELECT p FROM RoomParticipant p WHERE p.room.id = :roomId AND p.isHandRaised = true AND p.status = 'JOINED' AND p.deletedAt IS NULL ORDER BY p.updatedAt")
    List<RoomParticipant> findRaisedHandsByRoomId(@Param("roomId") Long roomId);

    /**
     * Check if user is in room
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM RoomParticipant p WHERE p.room.id = :roomId AND p.user.id = :userId AND p.status IN ('WAITING', 'JOINED') AND p.deletedAt IS NULL")
    boolean isUserInRoom(@Param("roomId") Long roomId, @Param("userId") Long userId);
}
