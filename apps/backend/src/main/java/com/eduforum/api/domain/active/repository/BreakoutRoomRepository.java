package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.BreakoutRoom;
import com.eduforum.api.domain.active.entity.BreakoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreakoutRoomRepository extends JpaRepository<BreakoutRoom, Long> {

    List<BreakoutRoom> findBySeminarRoomId(Long seminarRoomId);

    List<BreakoutRoom> findBySeminarRoomIdAndStatus(Long seminarRoomId, BreakoutStatus status);

    @Query("SELECT br FROM BreakoutRoom br WHERE br.deletedAt IS NULL AND br.id = :id")
    Optional<BreakoutRoom> findByIdAndNotDeleted(Long id);

    @Query("SELECT COUNT(br) FROM BreakoutRoom br WHERE br.seminarRoom.id = :seminarRoomId AND br.status = 'ACTIVE'")
    Long countActiveBySeminarRoomId(Long seminarRoomId);
}
