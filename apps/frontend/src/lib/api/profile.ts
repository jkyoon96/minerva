/**
 * 프로필 관련 API 함수
 */

import apiClient, { parseApiError } from './client';
import { API_ENDPOINTS } from './endpoints';
import { ApiResponse } from './types';
import {
  Profile,
  ProfileUpdateRequest,
  AvatarUploadResponse,
  EmailChangeRequest,
  EmailVerifyRequest,
  PasswordChangeRequest,
} from '@/types/profile';

/**
 * 프로필 조회
 */
export const getProfile = async (): Promise<Profile> => {
  try {
    const response = await apiClient.get<ApiResponse<Profile>>(
      API_ENDPOINTS.PROFILE.GET,
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
  data: ProfileUpdateRequest,
): Promise<Profile> => {
  try {
    const response = await apiClient.put<ApiResponse<Profile>>(
      API_ENDPOINTS.PROFILE.UPDATE,
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 아바타 업로드
 */
export const uploadAvatar = async (file: File): Promise<string> => {
  try {
    const formData = new FormData();
    formData.append('avatar', file);

    const response = await apiClient.post<ApiResponse<AvatarUploadResponse>>(
      API_ENDPOINTS.PROFILE.UPLOAD_AVATAR,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      },
    );
    return response.data.data.avatarUrl;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 아바타 삭제
 */
export const deleteAvatar = async (): Promise<void> => {
  try {
    await apiClient.delete(API_ENDPOINTS.PROFILE.DELETE_AVATAR);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 이메일 변경 요청
 */
export const changeEmail = async (
  data: EmailChangeRequest,
): Promise<void> => {
  try {
    await apiClient.post(API_ENDPOINTS.PROFILE.CHANGE_EMAIL, data);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 이메일 변경 인증
 */
export const verifyEmailChange = async (
  data: EmailVerifyRequest,
): Promise<void> => {
  try {
    await apiClient.post(API_ENDPOINTS.PROFILE.VERIFY_EMAIL, data);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 비밀번호 변경
 */
export const changePassword = async (
  data: PasswordChangeRequest,
): Promise<void> => {
  try {
    await apiClient.put(API_ENDPOINTS.PROFILE.CHANGE_PASSWORD, data);
  } catch (error) {
    throw parseApiError(error);
  }
};
