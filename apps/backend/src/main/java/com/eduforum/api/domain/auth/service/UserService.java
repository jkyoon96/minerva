package com.eduforum.api.domain.auth.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.dto.UserProfileResponse;
import com.eduforum.api.domain.auth.dto.UserProfileUpdateRequest;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.entity.UserRole;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.auth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * 현재 로그인한 사용자 조회
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Getting current user: {}", email);

        User user = userRepository.findActiveByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return mapToUserProfileResponse(user);
    }

    /**
     * 사용자 ID로 조회
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long userId) {
        log.info("Getting user by ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return mapToUserProfileResponse(user);
    }

    /**
     * 프로필 수정
     */
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        log.info("Updating profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 수정 가능한 필드만 업데이트
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        user = userRepository.save(user);

        log.info("Profile updated successfully for user ID: {}", userId);

        return mapToUserProfileResponse(user);
    }

    /**
     * User를 UserProfileResponse로 변환
     */
    private UserProfileResponse mapToUserProfileResponse(User user) {
        List<String> roleNames = userRoleRepository.findByUser(user).stream()
            .map(ur -> ur.getRole().getName())
            .collect(Collectors.toList());

        return UserProfileResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(user.getFullName())
            .profileImageUrl(user.getProfileImageUrl())
            .phone(user.getPhone())
            .status(user.getStatus().name())
            .roles(roleNames)
            .emailVerified(user.isEmailVerified())
            .emailVerifiedAt(user.getEmailVerifiedAt())
            .lastLoginAt(user.getLastLoginAt())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
