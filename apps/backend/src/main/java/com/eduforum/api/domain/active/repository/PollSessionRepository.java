package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.PollSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollSessionRepository extends JpaRepository<PollSession, Long> {

    List<PollSession> findByRoomId(Long roomId);

    Optional<PollSession> findByPollIdAndRoomId(Long pollId, Long roomId);

    @Query("SELECT ps FROM PollSession ps WHERE ps.room.id = :roomId AND ps.endedAt IS NULL")
    List<PollSession> findActiveSessionsByRoomId(Long roomId);
}
