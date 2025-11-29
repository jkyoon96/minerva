package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.SpeakingQueue;
import com.eduforum.api.domain.active.entity.SpeakingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeakingQueueRepository extends JpaRepository<SpeakingQueue, Long> {

    List<SpeakingQueue> findByRoomIdOrderByQueuePositionAsc(Long roomId);

    List<SpeakingQueue> findByRoomIdAndStatusOrderByQueuePositionAsc(Long roomId, SpeakingStatus status);

    Optional<SpeakingQueue> findByRoomIdAndUserId(Long roomId, Long userId);

    @Query("SELECT sq FROM SpeakingQueue sq WHERE sq.room.id = :roomId AND sq.status = 'WAITING' ORDER BY sq.queuePosition ASC")
    List<SpeakingQueue> findWaitingByRoomId(Long roomId);

    @Query("SELECT MAX(sq.queuePosition) FROM SpeakingQueue sq WHERE sq.room.id = :roomId")
    Optional<Integer> findMaxQueuePositionByRoomId(Long roomId);
}
