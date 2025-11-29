package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.Poll;
import com.eduforum.api.domain.active.entity.PollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    List<Poll> findByCourseId(Long courseId);

    List<Poll> findByCourseIdAndStatus(Long courseId, PollStatus status);

    List<Poll> findByCreatorId(Long creatorId);

    @Query("SELECT p FROM Poll p WHERE p.deletedAt IS NULL AND p.id = :id")
    Optional<Poll> findByIdAndNotDeleted(Long id);

    @Query("SELECT p FROM Poll p WHERE p.course.id = :courseId AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Poll> findActiveByCourseId(Long courseId);
}
