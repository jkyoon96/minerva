package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.NetworkNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NetworkNodeRepository extends JpaRepository<NetworkNode, Long> {

    Optional<NetworkNode> findByCourseIdAndStudentId(Long courseId, Long studentId);

    List<NetworkNode> findByCourseId(Long courseId);

    List<NetworkNode> findByCourseIdAndClusterId(Long courseId, Long clusterId);

    @Query("SELECT n FROM NetworkNode n WHERE n.courseId = :courseId " +
           "ORDER BY n.degreeCentrality DESC")
    List<NetworkNode> findTopCentralNodes(@Param("courseId") Long courseId);

    @Query("SELECT n FROM NetworkNode n WHERE n.courseId = :courseId " +
           "AND n.totalConnections = 0")
    List<NetworkNode> findIsolatedNodes(@Param("courseId") Long courseId);

    @Query("SELECT n FROM NetworkNode n WHERE n.courseId = :courseId " +
           "AND n.degreeCentrality > :threshold ORDER BY n.degreeCentrality DESC")
    List<NetworkNode> findHubNodes(
        @Param("courseId") Long courseId,
        @Param("threshold") Double threshold
    );
}
