package com.eduforum.api.domain.auth.repository;

import com.eduforum.api.domain.auth.entity.Role;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser(User user);

    Optional<UserRole> findByUserAndRole(User user, Role role);

    void deleteByUserAndRole(User user, Role role);

    boolean existsByUserAndRole(User user, Role role);
}
