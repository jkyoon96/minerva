package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.PollResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollResponseRepository extends JpaRepository<PollResponse, Long> {

    List<PollResponse> findByPollId(Long pollId);

    Optional<PollResponse> findByPollIdAndUserId(Long pollId, Long userId);

    @Query("SELECT COUNT(pr) FROM PollResponse pr WHERE pr.poll.id = :pollId")
    Long countByPollId(Long pollId);

    boolean existsByPollIdAndUserId(Long pollId, Long userId);
}
