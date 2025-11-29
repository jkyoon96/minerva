/**
 * Admin API 클라이언트
 * - 사용자 관리
 * - 역할 관리
 */

import apiClient from './client';
import { ApiResponse } from './types';
import {
  UserListItem,
  UserDetail,
  RoleInfo,
  ChangeRoleRequest,
  UserSearchParams,
  UserListResponse,
  RoleStatistics,
  UserStatus,
} from '@/types/admin';
import { UserRole } from '@/types';

/**
 * 사용자 목록 조회
 */
export async function getUsers(
  params?: UserSearchParams
): Promise<UserListResponse> {
  const response = await apiClient.get<ApiResponse<UserListResponse>>(
    '/v1/admin/users',
    { params }
  );
  return response.data.data;
}

/**
 * 사용자 상세 조회
 */
export async function getUserDetail(userId: string): Promise<UserDetail> {
  const response = await apiClient.get<ApiResponse<UserDetail>>(
    `/v1/admin/users/${userId}`
  );
  return response.data.data;
}

/**
 * 역할 변경
 */
export async function changeUserRole(
  userId: string,
  newRole: UserRole,
  reason?: string
): Promise<void> {
  await apiClient.put(`/v1/admin/users/${userId}/role`, {
    newRole,
    reason,
  });
}

/**
 * 사용자 상태 변경
 */
export async function changeUserStatus(
  userId: string,
  status: UserStatus
): Promise<void> {
  await apiClient.put(`/v1/admin/users/${userId}/status`, {
    status,
  });
}

/**
 * 역할 목록 조회
 */
export async function getRoles(): Promise<RoleInfo[]> {
  const response = await apiClient.get<ApiResponse<RoleInfo[]>>(
    '/v1/admin/roles'
  );
  return response.data.data;
}

/**
 * 사용자에게 역할 할당
 */
export async function assignRole(userId: string, role: UserRole): Promise<void> {
  await apiClient.post(`/v1/admin/users/${userId}/roles`, {
    role,
  });
}

/**
 * 사용자에게서 역할 제거
 */
export async function removeRole(userId: string, roleId: string): Promise<void> {
  await apiClient.delete(`/v1/admin/users/${userId}/roles/${roleId}`);
}

/**
 * 역할 통계 조회
 */
export async function getRoleStatistics(): Promise<RoleStatistics> {
  const response = await apiClient.get<ApiResponse<RoleStatistics>>(
    '/v1/admin/statistics/roles'
  );
  return response.data.data;
}

/**
 * 사용자 검색
 */
export async function searchUsers(query: string): Promise<UserListItem[]> {
  const response = await apiClient.get<ApiResponse<UserListItem[]>>(
    '/v1/admin/users/search',
    {
      params: { query },
    }
  );
  return response.data.data;
}

/**
 * 사용자 일괄 삭제
 */
export async function deleteUsers(userIds: string[]): Promise<void> {
  await apiClient.post('/v1/admin/users/batch-delete', {
    userIds,
  });
}

/**
 * 사용자 CSV 내보내기
 */
export async function exportUsersCSV(params?: UserSearchParams): Promise<Blob> {
  const response = await apiClient.get('/v1/admin/users/export', {
    params,
    responseType: 'blob',
  });
  return response.data;
}
