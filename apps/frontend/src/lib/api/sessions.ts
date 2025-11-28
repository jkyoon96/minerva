/**
 * 세션 관련 API 함수
 */

import apiClient, { parseApiError } from './client';
import { API_ENDPOINTS } from './endpoints';
import { ApiResponse } from './types';
import { Session } from '@/types';

/**
 * 세션 생성 요청
 */
export interface CreateSessionRequest {
  title: string;
  scheduledAt: string;
  duration: number; // 분 단위
  description?: string;
}

/**
 * 세션 업데이트 요청
 */
export interface UpdateSessionRequest {
  title?: string;
  scheduledAt?: string;
  duration?: number;
  description?: string;
  status?: 'scheduled' | 'active' | 'ended' | 'cancelled';
}

/**
 * 코스의 세션 목록 조회
 */
export const getSessions = async (courseId: string): Promise<Session[]> => {
  try {
    const response = await apiClient.get<ApiResponse<{ sessions: Session[] }>>(
      API_ENDPOINTS.SESSIONS.LIST(courseId),
    );
    return response.data.data.sessions;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 세션 상세 조회
 */
export const getSession = async (id: string): Promise<Session> => {
  try {
    const response = await apiClient.get<ApiResponse<Session>>(API_ENDPOINTS.SESSIONS.DETAIL(id));
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 세션 생성
 */
export const createSession = async (
  courseId: string,
  data: CreateSessionRequest,
): Promise<Session> => {
  try {
    const response = await apiClient.post<ApiResponse<Session>>(
      API_ENDPOINTS.SESSIONS.CREATE(courseId),
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 세션 업데이트
 */
export const updateSession = async (id: string, data: UpdateSessionRequest): Promise<Session> => {
  try {
    const response = await apiClient.patch<ApiResponse<Session>>(
      API_ENDPOINTS.SESSIONS.UPDATE(id),
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 세션 삭제
 */
export const deleteSession = async (id: string): Promise<void> => {
  try {
    await apiClient.delete(API_ENDPOINTS.SESSIONS.DELETE(id));
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 세션 시작
 */
export const startSession = async (id: string): Promise<Session> => {
  try {
    const response = await apiClient.post<ApiResponse<Session>>(API_ENDPOINTS.SESSIONS.START(id));
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 세션 종료
 */
export const endSession = async (id: string): Promise<Session> => {
  try {
    const response = await apiClient.post<ApiResponse<Session>>(API_ENDPOINTS.SESSIONS.END(id));
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};
