package com.eduforum.api.domain.course.repository;

import com.eduforum.api.domain.course.entity.RubricItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for rubric items
 */
@Repository
public interface RubricItemRepository extends JpaRepository<RubricItem, Long> {

    /**
     * Find all rubric items by criteria ID
     */
    @Query("SELECT ri FROM RubricItem ri WHERE ri.criteria.id = :criteriaId " +
           "AND ri.deletedAt IS NULL ORDER BY ri.orderIndex ASC")
    List<RubricItem> findByCriteriaIdOrderByOrderIndex(@Param("criteriaId") Long criteriaId);
}
