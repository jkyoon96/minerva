package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.NetworkEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NetworkEdgeRepository extends JpaRepository<NetworkEdge, Long> {

    Optional<NetworkEdge> findByCourseIdAndFromStudentIdAndToStudentId(
        Long courseId, Long fromStudentId, Long toStudentId
    );

    List<NetworkEdge> findByCourseId(Long courseId);

    @Query("SELECT e FROM NetworkEdge e WHERE e.courseId = :courseId " +
           "AND (e.fromStudentId = :studentId OR e.toStudentId = :studentId)")
    List<NetworkEdge> findByStudentId(
        @Param("courseId") Long courseId,
        @Param("studentId") Long studentId
    );

    @Query("SELECT e FROM NetworkEdge e WHERE e.courseId = :courseId " +
           "AND e.totalWeight >= :minWeight ORDER BY e.totalWeight DESC")
    List<NetworkEdge> findStrongEdges(
        @Param("courseId") Long courseId,
        @Param("minWeight") Integer minWeight
    );

    @Query("SELECT COUNT(e) FROM NetworkEdge e WHERE e.courseId = :courseId")
    Long countEdgesByCourse(@Param("courseId") Long courseId);
}
