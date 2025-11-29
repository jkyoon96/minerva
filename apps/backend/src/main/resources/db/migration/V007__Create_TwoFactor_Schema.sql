-- =====================================================
-- E1 사용자 인증 - 2FA (Two-Factor Authentication) 스키마
-- Version: V007
-- Description: Create two-factor authentication schema for enhanced security
-- =====================================================

-- =====================================================
-- Tables
-- =====================================================

-- Two-Factor Secrets Table
-- Stores TOTP secrets for two-factor authentication
CREATE TABLE IF NOT EXISTS auth.two_factor_secrets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    secret VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    enabled_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_two_factor_secrets_user_id
        FOREIGN KEY (user_id)
        REFERENCES auth.users(id)
        ON DELETE CASCADE
);

-- Backup Codes Table
-- Stores backup codes for two-factor authentication recovery
CREATE TABLE IF NOT EXISTS auth.backup_codes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_backup_codes_user_id
        FOREIGN KEY (user_id)
        REFERENCES auth.users(id)
        ON DELETE CASCADE
);

-- =====================================================
-- Indexes
-- =====================================================

-- Two-Factor Secrets Indexes
CREATE INDEX idx_two_factor_secrets_user_id
    ON auth.two_factor_secrets(user_id);

CREATE INDEX idx_two_factor_secrets_enabled
    ON auth.two_factor_secrets(is_enabled);

-- Backup Codes Indexes
CREATE INDEX idx_backup_codes_user_id
    ON auth.backup_codes(user_id);

CREATE INDEX idx_backup_codes_user_unused
    ON auth.backup_codes(user_id, is_used)
    WHERE is_used = FALSE;

-- =====================================================
-- Comments
-- =====================================================

-- Table Comments
COMMENT ON TABLE auth.two_factor_secrets IS '2FA TOTP 시크릿 저장 테이블';
COMMENT ON TABLE auth.backup_codes IS '2FA 백업 코드 저장 테이블';

-- Column Comments - two_factor_secrets
COMMENT ON COLUMN auth.two_factor_secrets.id IS '시크릿 ID (PK)';
COMMENT ON COLUMN auth.two_factor_secrets.user_id IS '사용자 ID (FK)';
COMMENT ON COLUMN auth.two_factor_secrets.secret IS 'TOTP 시크릿 (Base32 인코딩)';
COMMENT ON COLUMN auth.two_factor_secrets.is_enabled IS '2FA 활성화 여부';
COMMENT ON COLUMN auth.two_factor_secrets.enabled_at IS '2FA 활성화 일시';
COMMENT ON COLUMN auth.two_factor_secrets.created_at IS '생성 일시';
COMMENT ON COLUMN auth.two_factor_secrets.updated_at IS '수정 일시';

-- Column Comments - backup_codes
COMMENT ON COLUMN auth.backup_codes.id IS '백업 코드 ID (PK)';
COMMENT ON COLUMN auth.backup_codes.user_id IS '사용자 ID (FK)';
COMMENT ON COLUMN auth.backup_codes.code_hash IS '백업 코드 해시 (BCrypt)';
COMMENT ON COLUMN auth.backup_codes.is_used IS '사용 여부';
COMMENT ON COLUMN auth.backup_codes.used_at IS '사용 일시';
COMMENT ON COLUMN auth.backup_codes.created_at IS '생성 일시';

-- =====================================================
-- Sample Data (Optional - for development/testing)
-- =====================================================

-- Note: In production, 2FA secrets and backup codes should be generated
-- through the application API, not inserted directly into the database.
-- This section is intentionally left empty for security reasons.
