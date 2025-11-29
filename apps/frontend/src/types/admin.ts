/**
 * Admin 관련 타입 정의
 */

import { UserRole } from './index';

/**
 * 사용자 상태 타입
 */
export type UserStatus = 'active' | 'inactive' | 'suspended';

/**
 * 사용자 목록 아이템
 */
export interface UserListItem {
  id: string;
  email: string;
  name: string;
  role: UserRole;
  status: UserStatus;
  avatar?: string;
  createdAt: string;
  lastLoginAt?: string;
}

/**
 * 사용자 상세 정보
 */
export interface UserDetail extends UserListItem {
  bio?: string;
  phone?: string;
  department?: string;
  updatedAt: string;
  enrolledCourses?: number;
  createdCourses?: number;
}

/**
 * 역할 정보
 */
export interface RoleInfo {
  role: UserRole;
  displayName: string;
  description: string;
  userCount: number;
}

/**
 * 역할 변경 요청
 */
export interface ChangeRoleRequest {
  userId: string;
  newRole: UserRole;
  reason?: string;
}

/**
 * 사용자 검색/필터 파라미터
 */
export interface UserSearchParams {
  query?: string; // 이름 또는 이메일 검색
  role?: UserRole; // 역할 필터
  status?: UserStatus; // 상태 필터
  page?: number;
  limit?: number;
  sortBy?: 'name' | 'email' | 'createdAt' | 'lastLoginAt';
  sortOrder?: 'asc' | 'desc';
}

/**
 * 사용자 목록 응답
 */
export interface UserListResponse {
  users: UserListItem[];
  total: number;
  page: number;
  limit: number;
  totalPages: number;
}

/**
 * 역할 통계
 */
export interface RoleStatistics {
  totalUsers: number;
  byRole: {
    admin: number;
    professor: number;
    ta: number;
    student: number;
  };
  activeUsers: number;
  inactiveUsers: number;
}
