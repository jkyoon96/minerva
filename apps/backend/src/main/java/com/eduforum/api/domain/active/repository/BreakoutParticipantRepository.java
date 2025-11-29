package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.BreakoutParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreakoutParticipantRepository extends JpaRepository<BreakoutParticipant, Long> {

    List<BreakoutParticipant> findByBreakoutRoomId(Long breakoutRoomId);

    Optional<BreakoutParticipant> findByBreakoutRoomIdAndUserId(Long breakoutRoomId, Long userId);

    @Query("SELECT bp FROM BreakoutParticipant bp WHERE bp.breakoutRoom.id = :breakoutRoomId AND bp.leftAt IS NULL")
    List<BreakoutParticipant> findActiveByBreakoutRoomId(Long breakoutRoomId);

    @Query("SELECT COUNT(bp) FROM BreakoutParticipant bp WHERE bp.breakoutRoom.id = :breakoutRoomId AND bp.leftAt IS NULL")
    Long countActiveByBreakoutRoomId(Long breakoutRoomId);
}
