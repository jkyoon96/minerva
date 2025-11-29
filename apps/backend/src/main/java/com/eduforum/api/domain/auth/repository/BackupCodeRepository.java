package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.BackupCode;
import com.eduforum.api.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupCodeRepository extends JpaRepository<BackupCode, Long> {

    /**
     * Find all backup codes for a user
     */
    List<BackupCode> findByUser(User user);

    /**
     * Find all unused backup codes for a user
     */
    List<BackupCode> findByUserAndIsUsedFalse(User user);

    /**
     * Count unused backup codes for a user
     */
    long countByUserAndIsUsedFalse(User user);

    /**
     * Delete all backup codes for a user
     */
    void deleteByUser(User user);
}
