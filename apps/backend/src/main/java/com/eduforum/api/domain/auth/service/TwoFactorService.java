package com.eduforum.api.domain.auth.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.auth.dto.*;
import com.eduforum.api.domain.auth.entity.BackupCode;
import com.eduforum.api.domain.auth.entity.TwoFactorSecret;
import com.eduforum.api.domain.auth.entity.User;
import com.eduforum.api.domain.auth.repository.BackupCodeRepository;
import com.eduforum.api.domain.auth.repository.TwoFactorSecretRepository;
import com.eduforum.api.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Two-Factor Authentication Service
 * Implements RFC 6238 TOTP (Time-based One-Time Password)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TwoFactorService {

    private static final String ALGORITHM = "HmacSHA1";
    private static final int SECRET_BITS = 160; // 20 bytes
    private static final int TIME_STEP = 30; // seconds
    private static final int CODE_DIGITS = 6;
    private static final int BACKUP_CODE_COUNT = 10;
    private static final int BACKUP_CODE_LENGTH = 8;
    private static final String ISSUER = "EduForum";

    private final TwoFactorSecretRepository twoFactorSecretRepository;
    private final BackupCodeRepository backupCodeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Generate Base32 encoded TOTP secret
     */
    public String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SECRET_BITS / 8];
        random.nextBytes(bytes);
        return base32Encode(bytes);
    }

    /**
     * Generate QR code URI for authenticator apps
     * Format: otpauth://totp/EduForum:{email}?secret={secret}&issuer=EduForum
     */
    public String generateQrCodeUri(String email, String secret) {
        return String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=%d&period=%d",
            ISSUER, email, secret, ISSUER, CODE_DIGITS, TIME_STEP
        );
    }

    /**
     * Verify TOTP code
     * Allows 1 time window before and after current time to account for clock skew
     */
    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null || code.length() != CODE_DIGITS) {
            return false;
        }

        try {
            long currentTimeStep = System.currentTimeMillis() / 1000 / TIME_STEP;

            // Check current window and 1 window before/after (±30 seconds)
            for (int i = -1; i <= 1; i++) {
                String expectedCode = generateTOTP(secret, currentTimeStep + i);
                if (code.equals(expectedCode)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error verifying TOTP code", e);
            return false;
        }
    }

    /**
     * Setup 2FA for a user
     * Generates secret and backup codes but doesn't enable 2FA yet
     */
    @Transactional
    public TwoFactorSetupResponse setupTwoFactor(Long userId) {
        log.info("Setting up 2FA for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다"));

        // Check if 2FA is already set up
        TwoFactorSecret existingSecret = twoFactorSecretRepository.findByUser(user).orElse(null);
        if (existingSecret != null && existingSecret.isEnabled()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 2FA가 활성화되어 있습니다");
        }

        // Generate new secret
        String secret = generateSecret();
        String qrCodeUri = generateQrCodeUri(user.getEmail(), secret);

        // Save or update secret (not enabled yet)
        TwoFactorSecret twoFactorSecret;
        if (existingSecret != null) {
            existingSecret.setSecret(secret);
            twoFactorSecret = twoFactorSecretRepository.save(existingSecret);
        } else {
            twoFactorSecret = TwoFactorSecret.builder()
                .user(user)
                .secret(secret)
                .isEnabled(false)
                .build();
            twoFactorSecret = twoFactorSecretRepository.save(twoFactorSecret);
        }

        // Generate backup codes
        List<String> backupCodes = generateBackupCodes();
        saveBackupCodes(user, backupCodes);

        log.info("2FA setup completed for user ID: {}", userId);

        return TwoFactorSetupResponse.builder()
            .secret(secret)
            .qrCodeUri(qrCodeUri)
            .backupCodes(backupCodes)
            .enabled(false)
            .build();
    }

    /**
     * Verify code and enable 2FA
     */
    @Transactional
    public void enableTwoFactor(Long userId, String code) {
        log.info("Enabling 2FA for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다"));

        TwoFactorSecret twoFactorSecret = twoFactorSecretRepository.findByUser(user)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "2FA 설정을 먼저 진행해주세요"));

        if (twoFactorSecret.isEnabled()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 2FA가 활성화되어 있습니다");
        }

        // Verify the code
        if (!verifyCode(twoFactorSecret.getSecret(), code)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 인증 코드입니다");
        }

        // Enable 2FA
        twoFactorSecret.enable();
        twoFactorSecretRepository.save(twoFactorSecret);

        log.info("2FA enabled successfully for user ID: {}", userId);
    }

    /**
     * Disable 2FA
     */
    @Transactional
    public void disableTwoFactor(Long userId, String code, String backupCode) {
        log.info("Disabling 2FA for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다"));

        TwoFactorSecret twoFactorSecret = twoFactorSecretRepository.findByUser(user)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "2FA가 설정되지 않았습니다"));

        if (!twoFactorSecret.isEnabled()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "2FA가 활성화되어 있지 않습니다");
        }

        // Verify either TOTP code or backup code
        boolean verified = false;
        if (code != null && !code.isEmpty()) {
            verified = verifyCode(twoFactorSecret.getSecret(), code);
        } else if (backupCode != null && !backupCode.isEmpty()) {
            verified = verifyBackupCodeInternal(user, backupCode);
        }

        if (!verified) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 인증 코드입니다");
        }

        // Disable 2FA and delete all data
        twoFactorSecretRepository.delete(twoFactorSecret);
        backupCodeRepository.deleteByUser(user);

        log.info("2FA disabled successfully for user ID: {}", userId);
    }

    /**
     * Get 2FA status
     */
    @Transactional(readOnly = true)
    public TwoFactorStatusResponse getStatus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다"));

        TwoFactorSecret twoFactorSecret = twoFactorSecretRepository.findByUser(user).orElse(null);

        if (twoFactorSecret == null || !twoFactorSecret.isEnabled()) {
            return TwoFactorStatusResponse.builder()
                .enabled(false)
                .enabledAt(null)
                .remainingBackupCodes(0L)
                .build();
        }

        long remainingCodes = backupCodeRepository.countByUserAndIsUsedFalse(user);

        return TwoFactorStatusResponse.builder()
            .enabled(true)
            .enabledAt(twoFactorSecret.getEnabledAt())
            .remainingBackupCodes(remainingCodes)
            .build();
    }

    /**
     * Regenerate backup codes
     */
    @Transactional
    public BackupCodesResponse regenerateBackupCodes(Long userId) {
        log.info("Regenerating backup codes for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다"));

        TwoFactorSecret twoFactorSecret = twoFactorSecretRepository.findByUser(user).orElse(null);
        if (twoFactorSecret == null || !twoFactorSecret.isEnabled()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "2FA가 활성화되어 있지 않습니다");
        }

        // Delete old backup codes
        backupCodeRepository.deleteByUser(user);

        // Generate new backup codes
        List<String> backupCodes = generateBackupCodes();
        saveBackupCodes(user, backupCodes);

        log.info("Backup codes regenerated for user ID: {}", userId);

        return BackupCodesResponse.builder()
            .codes(backupCodes)
            .count(backupCodes.size())
            .build();
    }

    /**
     * Verify backup code
     */
    @Transactional
    public boolean verifyAndUseBackupCode(Long userId, String code) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다"));

        return verifyBackupCodeInternal(user, code);
    }

    /**
     * Internal method to verify backup code
     */
    private boolean verifyBackupCodeInternal(User user, String code) {
        List<BackupCode> backupCodes = backupCodeRepository.findByUserAndIsUsedFalse(user);

        for (BackupCode backupCode : backupCodes) {
            if (passwordEncoder.matches(code, backupCode.getCodeHash())) {
                backupCode.markAsUsed();
                backupCodeRepository.save(backupCode);
                log.info("Backup code used for user ID: {}", user.getId());
                return true;
            }
        }

        return false;
    }

    /**
     * Generate TOTP code for a given time step
     */
    private String generateTOTP(String secret, long timeStep) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] key = base32Decode(secret);
        byte[] data = ByteBuffer.allocate(8).putLong(timeStep).array();

        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(key, ALGORITHM));
        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24)
            | ((hash[offset + 1] & 0xFF) << 16)
            | ((hash[offset + 2] & 0xFF) << 8)
            | (hash[offset + 3] & 0xFF);

        int otp = binary % (int) Math.pow(10, CODE_DIGITS);
        return String.format("%0" + CODE_DIGITS + "d", otp);
    }

    /**
     * Generate backup codes
     */
    private List<String> generateBackupCodes() {
        SecureRandom random = new SecureRandom();
        List<String> codes = new ArrayList<>();

        for (int i = 0; i < BACKUP_CODE_COUNT; i++) {
            StringBuilder code = new StringBuilder();
            for (int j = 0; j < BACKUP_CODE_LENGTH; j++) {
                code.append(random.nextInt(10));
            }
            codes.add(code.toString());
        }

        return codes;
    }

    /**
     * Save backup codes with BCrypt hashing
     */
    private void saveBackupCodes(User user, List<String> codes) {
        List<BackupCode> backupCodes = codes.stream()
            .map(code -> BackupCode.builder()
                .user(user)
                .codeHash(passwordEncoder.encode(code))
                .isUsed(false)
                .build())
            .collect(Collectors.toList());

        backupCodeRepository.saveAll(backupCodes);
    }

    /**
     * Base32 encoding (RFC 4648)
     */
    private String base32Encode(byte[] bytes) {
        String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        StringBuilder result = new StringBuilder();
        int buffer = 0;
        int bitsInBuffer = 0;

        for (byte b : bytes) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsInBuffer += 8;

            while (bitsInBuffer >= 5) {
                result.append(base32Chars.charAt((buffer >> (bitsInBuffer - 5)) & 0x1F));
                bitsInBuffer -= 5;
            }
        }

        if (bitsInBuffer > 0) {
            result.append(base32Chars.charAt((buffer << (5 - bitsInBuffer)) & 0x1F));
        }

        return result.toString();
    }

    /**
     * Base32 decoding (RFC 4648)
     */
    private byte[] base32Decode(String base32) {
        String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        base32 = base32.toUpperCase().replaceAll("=", "");

        int[] buffer = new int[base32.length()];
        for (int i = 0; i < base32.length(); i++) {
            buffer[i] = base32Chars.indexOf(base32.charAt(i));
            if (buffer[i] == -1) {
                throw new IllegalArgumentException("Invalid Base32 character: " + base32.charAt(i));
            }
        }

        int outputLength = base32.length() * 5 / 8;
        byte[] output = new byte[outputLength];
        int bitsInBuffer = 0;
        int currentByte = 0;
        int outputIndex = 0;

        for (int value : buffer) {
            currentByte = (currentByte << 5) | value;
            bitsInBuffer += 5;

            if (bitsInBuffer >= 8) {
                output[outputIndex++] = (byte) (currentByte >> (bitsInBuffer - 8));
                bitsInBuffer -= 8;
            }
        }

        return output;
    }
}
