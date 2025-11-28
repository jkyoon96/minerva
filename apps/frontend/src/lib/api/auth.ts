/**
 * 인증 관련 API 함수
 */

import apiClient, { parseApiError } from './client';
import { API_ENDPOINTS } from './endpoints';
import {
  ApiResponse,
  LoginRequest,
  RegisterRequest,
  UpdateProfileRequest,
  AuthTokenResponse,
} from './types';
import { User } from '@/types';

/**
 * 로그인
 */
export const login = async (
  credentials: LoginRequest,
): Promise<{ user: User; tokens: AuthTokenResponse }> => {
  try {
    const response = await apiClient.post<
      ApiResponse<{ user: User; tokens: AuthTokenResponse }>
    >(API_ENDPOINTS.AUTH.LOGIN, credentials);
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 회원가입
 */
export const register = async (
  data: RegisterRequest,
): Promise<{ user: User; tokens: AuthTokenResponse }> => {
  try {
    const response = await apiClient.post<
      ApiResponse<{ user: User; tokens: AuthTokenResponse }>
    >(API_ENDPOINTS.AUTH.REGISTER, data);
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 로그아웃
 */
export const logout = async (): Promise<void> => {
  try {
    await apiClient.post(API_ENDPOINTS.AUTH.LOGOUT);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 토큰 갱신
 */
export const refreshToken = async (
  refreshToken: string,
): Promise<AuthTokenResponse> => {
  try {
    const response = await apiClient.post<ApiResponse<AuthTokenResponse>>(
      API_ENDPOINTS.AUTH.REFRESH,
      { refreshToken },
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 프로필 조회
 */
export const getProfile = async (): Promise<User> => {
  try {
    const response = await apiClient.get<ApiResponse<User>>(
      API_ENDPOINTS.AUTH.PROFILE,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 프로필 업데이트
 */
export const updateProfile = async (
  data: UpdateProfileRequest,
): Promise<User> => {
  try {
    const response = await apiClient.patch<ApiResponse<User>>(
      API_ENDPOINTS.AUTH.UPDATE_PROFILE,
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 비밀번호 변경
 */
export const changePassword = async (
  currentPassword: string,
  newPassword: string,
): Promise<void> => {
  try {
    await apiClient.post(API_ENDPOINTS.AUTH.CHANGE_PASSWORD, {
      currentPassword,
      newPassword,
    });
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 비밀번호 재설정 이메일 발송
 */
export const forgotPassword = async (email: string): Promise<void> => {
  try {
    await apiClient.post(API_ENDPOINTS.AUTH.FORGOT_PASSWORD, { email });
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 비밀번호 재설정
 */
export const resetPassword = async (
  token: string,
  newPassword: string,
): Promise<void> => {
  try {
    await apiClient.post(API_ENDPOINTS.AUTH.RESET_PASSWORD, {
      token,
      newPassword,
    });
  } catch (error) {
    throw parseApiError(error);
  }
};
