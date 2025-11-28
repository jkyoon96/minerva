/**
 * 사용자 역할 타입
 */
export type UserRole = 'admin' | 'professor' | 'ta' | 'student';

/**
 * 사용자 인터페이스
 */
export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
  avatar?: string;
  bio?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * 코스 인터페이스
 */
export interface Course {
  id: string;
  title: string;
  code: string;
  semester: string;
  description?: string;
  professorId: string;
  professor?: User;
  createdAt: string;
  updatedAt: string;
}

/**
 * 세션 상태 타입
 */
export type SessionStatus = 'scheduled' | 'active' | 'ended' | 'cancelled';

/**
 * 세션 인터페이스
 */
export interface Session {
  id: string;
  courseId: string;
  title: string;
  scheduledAt: string;
  duration: number; // 분 단위
  status: SessionStatus;
  recordingUrl?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * API 응답 인터페이스
 */
export interface ApiResponse<T = unknown> {
  data: T;
  meta?: {
    pagination?: {
      page: number;
      limit: number;
      total: number;
      totalPages: number;
    };
  };
}

/**
 * API 에러 인터페이스
 */
export interface ApiError {
  code: string;
  message: string;
  details?: Array<{
    field: string;
    message: string;
  }>;
}

// Re-export 타입들
export * from './auth';
export * from './course';
