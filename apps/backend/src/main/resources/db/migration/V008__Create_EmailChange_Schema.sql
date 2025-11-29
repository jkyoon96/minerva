-- V008: Email Change & Profile Enhancement Schema
-- Author: EduForum Team
-- Date: 2025-11-29
-- Description: Creates email_change_tokens table and adds profile fields (avatar_url, bio) to users table

-- ========================================
-- 1. Add profile fields to users table
-- ========================================
ALTER TABLE auth.users
    ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(500),
    ADD COLUMN IF NOT EXISTS bio TEXT;

-- Add index for efficient queries
CREATE INDEX IF NOT EXISTS idx_users_avatar_url ON auth.users(avatar_url) WHERE avatar_url IS NOT NULL;

-- ========================================
-- 2. Create email_change_tokens table
-- ========================================
CREATE TABLE IF NOT EXISTS auth.email_change_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    new_email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for efficient lookups
CREATE INDEX idx_email_change_tokens_user_id ON auth.email_change_tokens(user_id);
CREATE INDEX idx_email_change_tokens_token ON auth.email_change_tokens(token);
CREATE INDEX idx_email_change_tokens_expires_at ON auth.email_change_tokens(expires_at);

-- Add comments
COMMENT ON TABLE auth.email_change_tokens IS 'Stores email change verification tokens';
COMMENT ON COLUMN auth.email_change_tokens.id IS 'Primary key';
COMMENT ON COLUMN auth.email_change_tokens.user_id IS 'Reference to the user requesting email change';
COMMENT ON COLUMN auth.email_change_tokens.new_email IS 'New email address to be verified';
COMMENT ON COLUMN auth.email_change_tokens.token IS 'Unique verification token';
COMMENT ON COLUMN auth.email_change_tokens.expires_at IS 'Token expiration timestamp (24 hours)';
COMMENT ON COLUMN auth.email_change_tokens.used IS 'Whether the token has been used';
COMMENT ON COLUMN auth.email_change_tokens.created_at IS 'Record creation timestamp';
COMMENT ON COLUMN auth.email_change_tokens.updated_at IS 'Record last update timestamp';

-- ========================================
-- 3. Add trigger for updated_at
-- ========================================
CREATE OR REPLACE FUNCTION auth.update_email_change_tokens_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_email_change_tokens_updated_at
    BEFORE UPDATE ON auth.email_change_tokens
    FOR EACH ROW
    EXECUTE FUNCTION auth.update_email_change_tokens_updated_at();

-- ========================================
-- 4. Add comments to new user columns
-- ========================================
COMMENT ON COLUMN auth.users.avatar_url IS 'URL or path to user profile avatar image';
COMMENT ON COLUMN auth.users.bio IS 'User biography or description (max 500 chars in application)';
