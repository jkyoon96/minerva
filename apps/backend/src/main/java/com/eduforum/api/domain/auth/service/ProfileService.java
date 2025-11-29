package com.eduforum.api.domain.auth.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.dto.*;
import com.eduforum.api.domain.auth.entity.EmailChangeToken;
import com.eduforum.api.domain.auth.entity.TwoFactorSecret;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.entity.UserRole;
import com.eduforum.api.domain.auth.repository.EmailChangeTokenRepository;
import com.eduforum.api.domain.auth.repository.TwoFactorSecretRepository;
import com.eduforum.api.domain.auth.repository.UserRepository;
import com.eduforum.api.domain.auth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Profile management service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailChangeTokenRepository emailChangeTokenRepository;
    private final TwoFactorSecretRepository twoFactorSecretRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.avatar.storage-path:static/avatars}")
    private String avatarStoragePath;

    @Value("${app.avatar.max-size-mb:5}")
    private int maxAvatarSizeMb;

    /**
     * Get current user's profile
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        User user = getCurrentAuthenticatedUser();
        log.info("Getting profile for user: {}", user.getEmail());

        return mapToProfileResponse(user);
    }

    /**
     * Update user profile
     */
    @Transactional
    public ProfileResponse updateProfile(ProfileUpdateRequest request) {
        User user = getCurrentAuthenticatedUser();
        log.info("Updating profile for user: {}", user.getEmail());

        // Update name if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            String[] nameParts = request.getName().split(" ", 2);
            String firstName = nameParts.length > 1 ? nameParts[1] : nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[0] : "";

            user.setFirstName(firstName);
            user.setLastName(lastName);
        }

        // Update bio if provided
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        user = userRepository.save(user);

        log.info("Profile updated successfully for user: {}", user.getEmail());

        return mapToProfileResponse(user);
    }

    /**
     * Upload avatar image
     */
    @Transactional
    public AvatarUploadResponse uploadAvatar(AvatarUploadRequest request) {
        User user = getCurrentAuthenticatedUser();
        log.info("Uploading avatar for user: {}", user.getEmail());

        try {
            // Decode Base64 image
            byte[] imageBytes = Base64.getDecoder().decode(request.getImageBase64());

            // Validate file size (5MB default)
            int maxSizeBytes = maxAvatarSizeMb * 1024 * 1024;
            if (imageBytes.length > maxSizeBytes) {
                throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    String.format("이미지 크기는 %dMB를 초과할 수 없습니다", maxAvatarSizeMb)
                );
            }

            // Determine file extension from MIME type
            String extension = getExtensionFromMimeType(request.getMimeType());

            // Delete old avatar if exists
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                deleteAvatarFile(user.getAvatarUrl());
            }

            // Generate unique filename
            String filename = String.format("user_%d_%d.%s", user.getId(), System.currentTimeMillis(), extension);

            // Ensure storage directory exists
            Path storagePath = Paths.get(avatarStoragePath);
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }

            // Save file
            Path filePath = storagePath.resolve(filename);
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(imageBytes);
            }

            // Update user avatar URL
            String avatarUrl = "/" + avatarStoragePath + "/" + filename;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            log.info("Avatar uploaded successfully for user: {} (URL: {})", user.getEmail(), avatarUrl);

            return AvatarUploadResponse.builder()
                .avatarUrl(avatarUrl)
                .message("프로필 사진이 업로드되었습니다")
                .build();

        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 Base64 인코딩입니다");
        } catch (IOException e) {
            log.error("Failed to save avatar file", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다");
        }
    }

    /**
     * Delete avatar image
     */
    @Transactional
    public void deleteAvatar() {
        User user = getCurrentAuthenticatedUser();
        log.info("Deleting avatar for user: {}", user.getEmail());

        if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "프로필 사진이 존재하지 않습니다");
        }

        // Delete file
        deleteAvatarFile(user.getAvatarUrl());

        // Update user
        user.setAvatarUrl(null);
        userRepository.save(user);

        log.info("Avatar deleted successfully for user: {}", user.getEmail());
    }

    /**
     * Request email change
     */
    @Transactional
    public void requestEmailChange(EmailChangeRequest request) {
        User user = getCurrentAuthenticatedUser();
        log.info("Requesting email change for user: {} -> {}", user.getEmail(), request.getNewEmail());

        // Verify current password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "비밀번호가 일치하지 않습니다");
        }

        // Check if new email is same as current
        if (user.getEmail().equals(request.getNewEmail())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "현재 이메일과 동일합니다");
        }

        // Check if new email is already taken
        if (userRepository.existsByEmail(request.getNewEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다");
        }

        // Check if there's a pending change for this email
        if (emailChangeTokenRepository.existsPendingChangeForEmail(request.getNewEmail(), OffsetDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "해당 이메일로 진행 중인 변경 요청이 있습니다");
        }

        // Invalidate any existing unused tokens for this user
        List<EmailChangeToken> unusedTokens = emailChangeTokenRepository.findUnusedByUser(user);
        unusedTokens.forEach(token -> {
            token.markAsUsed();
            emailChangeTokenRepository.save(token);
        });

        // Create new token
        String token = UUID.randomUUID().toString();
        EmailChangeToken emailChangeToken = EmailChangeToken.builder()
            .user(user)
            .newEmail(request.getNewEmail())
            .token(token)
            .expiresAt(OffsetDateTime.now().plusDays(1))
            .build();

        emailChangeTokenRepository.save(emailChangeToken);

        log.info("Email change token created for user: {} (Token: {})", user.getEmail(), token);

        // TODO: Send email with verification link
        // emailService.sendEmailChangeVerification(request.getNewEmail(), token);
    }

    /**
     * Confirm email change
     */
    @Transactional
    public void confirmEmailChange(EmailVerifyRequest request) {
        log.info("Confirming email change with token: {}", request.getToken());

        // Find token
        EmailChangeToken token = emailChangeTokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다"));

        // Validate token
        if (!token.isValid()) {
            if (token.isExpired()) {
                throw new BusinessException(ErrorCode.EXPIRED_TOKEN, "만료된 토큰입니다");
            }
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "이미 사용된 토큰입니다");
        }

        // Check if new email is still available
        if (userRepository.existsByEmail(token.getNewEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다");
        }

        // Update user email
        User user = token.getUser();
        String oldEmail = user.getEmail();
        user.setEmail(token.getNewEmail());
        userRepository.save(user);

        // Mark token as used
        token.markAsUsed();
        emailChangeTokenRepository.save(token);

        log.info("Email changed successfully: {} -> {}", oldEmail, token.getNewEmail());

        // TODO: Send notification emails
        // emailService.sendEmailChangeNotification(oldEmail, token.getNewEmail());
    }

    /**
     * Change password
     */
    @Transactional
    public PasswordChangeResponse changePassword(PasswordChangeRequest request) {
        User user = getCurrentAuthenticatedUser();
        log.info("Changing password for user: {}", user.getEmail());

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "현재 비밀번호가 일치하지 않습니다");
        }

        // Verify new password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "새 비밀번호가 일치하지 않습니다");
        }

        // Check if new password is same as current
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "현재 비밀번호와 동일한 비밀번호는 사용할 수 없습니다");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getEmail());

        return PasswordChangeResponse.builder()
            .success(true)
            .message("비밀번호가 성공적으로 변경되었습니다")
            .build();
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findActiveByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Map User entity to ProfileResponse DTO
     */
    private ProfileResponse mapToProfileResponse(User user) {
        // Get primary role
        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        String primaryRole = userRoles.isEmpty() ? "USER" : userRoles.get(0).getRole().getName();

        // Check 2FA status
        boolean twoFactorEnabled = twoFactorSecretRepository.findByUser(user)
            .map(TwoFactorSecret::isEnabled)
            .orElse(false);

        return ProfileResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getFullName())
            .avatarUrl(user.getAvatarUrl())
            .bio(user.getBio())
            .role(primaryRole)
            .createdAt(user.getCreatedAt())
            .emailVerified(user.isEmailVerified())
            .twoFactorEnabled(twoFactorEnabled)
            .build();
    }

    /**
     * Get file extension from MIME type
     */
    private String getExtensionFromMimeType(String mimeType) {
        return switch (mimeType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            default -> throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 이미지 형식입니다");
        };
    }

    /**
     * Delete avatar file from storage
     */
    private void deleteAvatarFile(String avatarUrl) {
        try {
            // Extract filename from URL (e.g., "/static/avatars/user_1.jpg" -> "user_1.jpg")
            String filename = avatarUrl.substring(avatarUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(avatarStoragePath, filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Avatar file deleted: {}", filePath);
            }
        } catch (IOException e) {
            log.warn("Failed to delete avatar file: {}", avatarUrl, e);
            // Don't throw exception, just log warning
        }
    }
}
