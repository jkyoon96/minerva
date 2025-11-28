package com.eduforum.api.domain.auth.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.dto.PasswordResetConfirmRequest;
import com.eduforum.api.domain.auth.dto.PasswordResetRequest;
import com.eduforum.api.domain.auth.entity.PasswordResetToken;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.PasswordResetTokenRepository;
import com.eduforum.api.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 비밀번호 재설정 요청
     */
    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        log.info("Password reset requested for email: {}", request.getEmail());

        // 사용자 조회
        User user = userRepository.findActiveByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 기존 토큰 무효화
        passwordResetTokenRepository.findByUserAndUsedAtIsNullAndExpiresAtAfter(user, OffsetDateTime.now())
            .ifPresent(token -> {
                token.markAsUsed();
                passwordResetTokenRepository.save(token);
            });

        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
            .token(token)
            .user(user)
            .expiresAt(OffsetDateTime.now().plusHours(1))
            .build();

        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset token generated for user: {}", user.getEmail());
        // TODO: 이메일 발송 (재설정 링크)
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public void resetPassword(PasswordResetConfirmRequest request) {
        log.info("Password reset confirmation with token");

        // 비밀번호 확인 검증
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "비밀번호가 일치하지 않습니다");
        }

        // 토큰 조회
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 재설정 토큰입니다"));

        // 토큰 만료 확인
        if (resetToken.isExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN, "만료된 재설정 토큰입니다");
        }

        // 이미 사용된 토큰 확인
        if (resetToken.isUsed()) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "이미 사용된 재설정 토큰입니다");
        }

        // 비밀번호 변경
        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 토큰 사용 완료 처리
        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }
}
