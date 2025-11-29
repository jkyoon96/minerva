package com.eduforum.api.domain.active.repository;

import com.eduforum.api.domain.active.entity.WhiteboardElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhiteboardElementRepository extends JpaRepository<WhiteboardElement, Long> {

    List<WhiteboardElement> findByWhiteboardIdOrderByZIndexAsc(Long whiteboardId);

    void deleteByWhiteboardId(Long whiteboardId);
}
