/**
 * 2FA (Two-Factor Authentication) 관련 API 함수
 */

import apiClient, { parseApiError } from './client';
import { API_ENDPOINTS } from './endpoints';
import { ApiResponse } from './types';
import {
  TwoFactorSetupResponse,
  TwoFactorVerifyRequest,
  TwoFactorStatusResponse,
  BackupCodesResponse,
  TwoFactorLoginRequest,
  TwoFactorDisableRequest,
} from '@/types/two-factor';
import { User, AuthTokenResponse } from '@/types';

/**
 * 2FA 설정 시작 (QR 코드 및 시크릿 생성)
 */
export const setupTwoFactor = async (): Promise<TwoFactorSetupResponse> => {
  try {
    const response = await apiClient.post<ApiResponse<TwoFactorSetupResponse>>(
      API_ENDPOINTS.AUTH.TWO_FACTOR_SETUP,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 2FA 코드 검증 (설정 완료)
 */
export const verifyTwoFactor = async (
  data: TwoFactorVerifyRequest,
): Promise<void> => {
  try {
    await apiClient.post(API_ENDPOINTS.AUTH.TWO_FACTOR_VERIFY, data);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 2FA 비활성화
 */
export const disableTwoFactor = async (
  data: TwoFactorDisableRequest,
): Promise<void> => {
  try {
    await apiClient.post(API_ENDPOINTS.AUTH.TWO_FACTOR_DISABLE, data);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 2FA 상태 조회
 */
export const getTwoFactorStatus = async (): Promise<TwoFactorStatusResponse> => {
  try {
    const response = await apiClient.get<ApiResponse<TwoFactorStatusResponse>>(
      API_ENDPOINTS.AUTH.TWO_FACTOR_STATUS,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 백업 코드 재생성
 */
export const regenerateBackupCodes = async (): Promise<BackupCodesResponse> => {
  try {
    const response = await apiClient.post<ApiResponse<BackupCodesResponse>>(
      API_ENDPOINTS.AUTH.TWO_FACTOR_BACKUP_CODES,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 백업 코드 검증
 */
export const verifyBackupCode = async (
  data: TwoFactorVerifyRequest,
): Promise<void> => {
  try {
    await apiClient.post(API_ENDPOINTS.AUTH.TWO_FACTOR_VERIFY_BACKUP, data);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 2FA 로그인 (2단계 인증)
 */
export const loginWithTwoFactor = async (
  data: TwoFactorLoginRequest,
): Promise<{ user: User; tokens: AuthTokenResponse }> => {
  try {
    const response = await apiClient.post<
      ApiResponse<{ user: User; tokens: AuthTokenResponse }>
    >(API_ENDPOINTS.AUTH.TWO_FACTOR_LOGIN, data);
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};
