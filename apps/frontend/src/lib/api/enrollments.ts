/**
 * 수강 관련 API 함수
 */

import apiClient, { parseApiError } from './client';
import { ApiResponse } from './types';

/**
 * 초대 링크 인터페이스
 */
export interface InviteLink {
  id: string;
  courseId: string;
  code: string;
  role: 'student' | 'ta';
  expiresAt?: string;
  maxUses?: number;
  usedCount: number;
  isActive: boolean;
  createdAt: string;
}

/**
 * 초대 링크 생성 요청
 */
export interface CreateInviteLinkRequest {
  role: 'student' | 'ta';
  expiresAt?: string;
  maxUses?: number;
}

/**
 * CSV 업로드 결과
 */
export interface CsvUploadResult {
  success: number;
  failed: number;
  errors: Array<{
    row: number;
    email: string;
    reason: string;
  }>;
}

/**
 * 초대 링크 생성
 */
export const createInviteLink = async (
  courseId: string,
  data: CreateInviteLinkRequest,
): Promise<InviteLink> => {
  try {
    const response = await apiClient.post<ApiResponse<InviteLink>>(
      `/v1/courses/${courseId}/invites`,
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 초대 링크 목록 조회
 */
export const getInviteLinks = async (courseId: string): Promise<InviteLink[]> => {
  try {
    const response = await apiClient.get<ApiResponse<{ invites: InviteLink[] }>>(
      `/v1/courses/${courseId}/invites`,
    );
    return response.data.data.invites;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 초대 링크 비활성화
 */
export const deactivateInviteLink = async (inviteId: string): Promise<void> => {
  try {
    await apiClient.patch(`/v1/invites/${inviteId}/deactivate`);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 초대 코드로 수강 신청
 */
export const enrollWithInviteCode = async (code: string): Promise<void> => {
  try {
    await apiClient.post(`/v1/enrollments/invite/${code}`);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * CSV 파일로 수강생 일괄 등록
 */
export const uploadEnrollmentCsv = async (
  courseId: string,
  file: File,
): Promise<CsvUploadResult> => {
  try {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post<ApiResponse<CsvUploadResult>>(
      `/v1/courses/${courseId}/enrollments/csv`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      },
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 수강생 제거
 */
export const removeEnrollment = async (courseId: string, userId: string): Promise<void> => {
  try {
    await apiClient.delete(`/v1/courses/${courseId}/enrollments/${userId}`);
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 수강생 역할 변경
 */
export const updateEnrollmentRole = async (
  courseId: string,
  userId: string,
  role: 'student' | 'ta',
): Promise<void> => {
  try {
    await apiClient.patch(`/v1/courses/${courseId}/enrollments/${userId}/role`, { role });
  } catch (error) {
    throw parseApiError(error);
  }
};
