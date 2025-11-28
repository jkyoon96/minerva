package com.eduforum.api.domain.course.repository;

import com.eduforum.api.domain.course.entity.Course;
import com.eduforum.api.domain.course.entity.InviteLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for InviteLink entity
 */
@Repository
public interface InviteLinkRepository extends JpaRepository<InviteLink, Long> {

    /**
     * Find invite link by code
     */
    Optional<InviteLink> findByCode(String code);

    /**
     * Find active invite links by course
     */
    @Query("SELECT i FROM InviteLink i WHERE i.course = :course AND i.isActive = true AND i.deletedAt IS NULL")
    List<InviteLink> findActiveInviteLinksByCourse(@Param("course") Course course);

    /**
     * Find all invite links by course (including inactive)
     */
    @Query("SELECT i FROM InviteLink i WHERE i.course = :course AND i.deletedAt IS NULL")
    List<InviteLink> findInviteLinksByCourse(@Param("course") Course course);

    /**
     * Check if code already exists
     */
    boolean existsByCode(String code);
}
