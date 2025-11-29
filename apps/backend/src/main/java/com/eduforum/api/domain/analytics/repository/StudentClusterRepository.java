package com.eduforum.api.domain.analytics.repository;

import com.eduforum.api.domain.analytics.entity.StudentCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentClusterRepository extends JpaRepository<StudentCluster, Long> {

    List<StudentCluster> findByCourseIdOrderByClusterNumberAsc(Long courseId);

    @Query("SELECT c FROM StudentCluster c WHERE c.courseId = :courseId " +
           "AND :studentId MEMBER OF c.memberIds")
    List<StudentCluster> findByStudentMembership(
        @Param("courseId") Long courseId,
        @Param("studentId") Long studentId
    );

    @Query("SELECT COUNT(c) FROM StudentCluster c WHERE c.courseId = :courseId")
    Long countByCourse(@Param("courseId") Long courseId);
}
