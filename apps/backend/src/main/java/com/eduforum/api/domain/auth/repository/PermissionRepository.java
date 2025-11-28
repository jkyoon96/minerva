package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Permission entity
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permission by name
     */
    Optional<Permission> findByName(String name);

    /**
     * Find permissions by resource
     */
    List<Permission> findByResource(String resource);

    /**
     * Find permission by resource and action
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * Check if permission exists by name
     */
    boolean existsByName(String name);
}
