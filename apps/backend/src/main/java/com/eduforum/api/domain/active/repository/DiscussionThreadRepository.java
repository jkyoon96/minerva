package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.DiscussionThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscussionThreadRepository extends JpaRepository<DiscussionThread, Long> {

    List<DiscussionThread> findByRoomIdOrderByCreatedAtDesc(Long roomId);

    List<DiscussionThread> findByRoomIdAndIsResolvedOrderByUpvoteCountDesc(Long roomId, Boolean isResolved);

    @Query("SELECT dt FROM DiscussionThread dt WHERE dt.deletedAt IS NULL AND dt.id = :id")
    Optional<DiscussionThread> findByIdAndNotDeleted(Long id);
}
