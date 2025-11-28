/**
 * API 응답 타입 정의
 */

/**
 * 표준 API 응답 래퍼
 */
export interface ApiResponse<T = unknown> {
  data: T;
  meta?: {
    pagination?: PaginationMeta;
    timestamp?: string;
  };
}

/**
 * 페이지네이션 메타데이터
 */
export interface PaginationMeta {
  page: number;
  limit: number;
  total: number;
  totalPages: number;
}

/**
 * API 에러 응답
 */
export interface ApiError {
  code: string;
  message: string;
  details?: ValidationError[];
  timestamp?: string;
}

/**
 * 유효성 검증 에러
 */
export interface ValidationError {
  field: string;
  message: string;
  code?: string;
}

/**
 * 인증 토큰 응답
 */
export interface AuthTokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number; // 초 단위
}

/**
 * 로그인 요청
 */
export interface LoginRequest {
  email: string;
  password: string;
  rememberMe?: boolean;
}

/**
 * 회원가입 요청
 */
export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
  role: 'student' | 'professor' | 'ta';
}

/**
 * 프로필 업데이트 요청
 */
export interface UpdateProfileRequest {
  name?: string;
  avatar?: string;
  bio?: string;
}

/**
 * 코스 생성 요청
 */
export interface CreateCourseRequest {
  title: string;
  code: string;
  semester: string;
  description?: string;
}

/**
 * 코스 업데이트 요청
 */
export interface UpdateCourseRequest {
  title?: string;
  code?: string;
  semester?: string;
  description?: string;
}

/**
 * 코스 조회 파라미터
 */
export interface GetCoursesParams {
  page?: number;
  limit?: number;
  search?: string;
  semester?: string;
  status?: 'active' | 'archived';
}

/**
 * 수강 신청 정보
 */
export interface Enrollment {
  id: string;
  userId: string;
  courseId: string;
  role: 'student' | 'ta';
  enrolledAt: string;
  user?: {
    id: string;
    name: string;
    email: string;
    avatar?: string;
  };
}
