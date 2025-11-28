/**
 * 인증 관련 타입 정의
 */

import { User, UserRole } from './index';

/**
 * 로그인 폼 데이터
 */
export interface LoginFormData {
  email: string;
  password: string;
  rememberMe?: boolean;
}

/**
 * 회원가입 폼 데이터
 */
export interface RegisterFormData {
  email: string;
  password: string;
  passwordConfirm: string;
  name: string;
  role: UserRole;
  termsAgreed: boolean;
}

/**
 * 비밀번호 변경 폼 데이터
 */
export interface ChangePasswordFormData {
  currentPassword: string;
  newPassword: string;
  newPasswordConfirm: string;
}

/**
 * 인증 컨텍스트 타입
 */
export interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (credentials: LoginFormData) => Promise<void>;
  register: (data: RegisterFormData) => Promise<void>;
  logout: () => Promise<void>;
  clearError: () => void;
}

/**
 * JWT 페이로드 타입
 */
export interface JwtPayload {
  sub: string; // user id
  email: string;
  role: UserRole;
  iat: number; // issued at
  exp: number; // expiration
}
