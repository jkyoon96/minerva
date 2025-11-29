package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

    List<PollOption> findByPollIdOrderByOrderAsc(Long pollId);

    void deleteByPollId(Long pollId);
}
