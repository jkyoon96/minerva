/**
 * 프로필 관련 타입 정의
 */

import { User } from './index';

/**
 * 프로필 정보 (User 확장)
 */
export interface Profile extends User {
  // User 타입에 이미 id, email, name, role, avatar, bio 포함
}

/**
 * 프로필 업데이트 요청
 */
export interface ProfileUpdateRequest {
  name?: string;
  bio?: string;
}

/**
 * 아바타 업로드 응답
 */
export interface AvatarUploadResponse {
  avatarUrl: string;
}

/**
 * 이메일 변경 요청
 */
export interface EmailChangeRequest {
  newEmail: string;
  currentPassword: string;
}

/**
 * 이메일 인증 요청
 */
export interface EmailVerifyRequest {
  token: string;
}

/**
 * 비밀번호 변경 요청
 */
export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

/**
 * 비밀번호 강도
 */
export type PasswordStrength = 'weak' | 'medium' | 'strong';

/**
 * 비밀번호 유효성 검사 결과
 */
export interface PasswordValidation {
  isValid: boolean;
  strength: PasswordStrength;
  errors: string[];
  suggestions: string[];
}
