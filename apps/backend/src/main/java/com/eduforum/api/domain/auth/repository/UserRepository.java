package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email (excluding soft-deleted)
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findActiveByEmail(@Param("email") String email);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Find users by status (excluding soft-deleted)
     */
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.deletedAt IS NULL")
    List<User> findActiveByStatus(@Param("status") UserStatus status);

    /**
     * Find all active users (not soft-deleted)
     */
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActive();
}
