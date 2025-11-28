package com.eduforum.api.domain.auth.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.dto.AssignRoleRequest;
import com.eduforum.api.domain.auth.dto.RoleResponse;
import com.eduforum.api.domain.auth.entity.Role;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.entity.UserRole;
import com.eduforum.api.domain.auth.repository.RoleRepository;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.auth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * 모든 역할 조회
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.info("Getting all roles");

        return roleRepository.findAll().stream()
            .map(this::mapToRoleResponse)
            .collect(Collectors.toList());
    }

    /**
     * 사용자에게 역할 할당
     */
    @Transactional
    public void assignRole(Long userId, AssignRoleRequest request) {
        log.info("Assigning role {} to user {}", request.getRoleId(), userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findById(request.getRoleId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "역할을 찾을 수 없습니다"));

        // 이미 할당된 역할인지 확인
        if (userRoleRepository.existsByUserAndRole(user, role)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 할당된 역할입니다");
        }

        UserRole userRole = UserRole.builder()
            .user(user)
            .role(role)
            .build();

        userRoleRepository.save(userRole);

        log.info("Role assigned successfully: user={}, role={}", userId, role.getName());
    }

    /**
     * 사용자로부터 역할 제거
     */
    @Transactional
    public void removeRole(Long userId, Long roleId) {
        log.info("Removing role {} from user {}", roleId, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "역할을 찾을 수 없습니다"));

        UserRole userRole = userRoleRepository.findByUserAndRole(user, role)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자에게 할당된 역할이 아닙니다"));

        userRoleRepository.delete(userRole);

        log.info("Role removed successfully: user={}, role={}", userId, role.getName());
    }

    /**
     * 사용자의 역할 목록 조회
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getUserRoles(Long userId) {
        log.info("Getting roles for user {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return userRoleRepository.findByUser(user).stream()
            .map(ur -> mapToRoleResponse(ur.getRole()))
            .collect(Collectors.toList());
    }

    /**
     * Role을 RoleResponse로 변환
     */
    private RoleResponse mapToRoleResponse(Role role) {
        return RoleResponse.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .createdAt(role.getCreatedAt())
            .build();
    }
}
