package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.Whiteboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WhiteboardRepository extends JpaRepository<Whiteboard, Long> {

    List<Whiteboard> findByRoomId(Long roomId);

    @Query("SELECT w FROM Whiteboard w WHERE w.deletedAt IS NULL AND w.id = :id")
    Optional<Whiteboard> findByIdAndNotDeleted(Long id);
}
