/**
 * API 엔드포인트 상수 정의
 */

export const API_ENDPOINTS = {
  // 인증 관련
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh',
    PROFILE: '/auth/profile',
    UPDATE_PROFILE: '/auth/profile',
    CHANGE_PASSWORD: '/auth/change-password',
    FORGOT_PASSWORD: '/auth/forgot-password',
    RESET_PASSWORD: '/auth/reset-password',
    VERIFY_EMAIL: '/auth/verify-email',

    // 2FA 관련
    TWO_FACTOR_SETUP: '/auth/2fa/setup',
    TWO_FACTOR_VERIFY: '/auth/2fa/verify',
    TWO_FACTOR_DISABLE: '/auth/2fa/disable',
    TWO_FACTOR_STATUS: '/auth/2fa/status',
    TWO_FACTOR_BACKUP_CODES: '/auth/2fa/backup-codes',
    TWO_FACTOR_VERIFY_BACKUP: '/auth/2fa/verify-backup',
    TWO_FACTOR_LOGIN: '/auth/login/2fa',
  },

  // 코스 관련
  COURSES: {
    LIST: '/courses',
    DETAIL: (id: string) => `/courses/${id}`,
    CREATE: '/courses',
    UPDATE: (id: string) => `/courses/${id}`,
    DELETE: (id: string) => `/courses/${id}`,
    ENROLLMENTS: (id: string) => `/courses/${id}/enrollments`,
    ENROLL: (id: string) => `/courses/${id}/enroll`,
    UNENROLL: (id: string) => `/courses/${id}/unenroll`,
  },

  // 세션 관련
  SESSIONS: {
    LIST: (courseId: string) => `/courses/${courseId}/sessions`,
    DETAIL: (id: string) => `/sessions/${id}`,
    CREATE: (courseId: string) => `/courses/${courseId}/sessions`,
    UPDATE: (id: string) => `/sessions/${id}`,
    DELETE: (id: string) => `/sessions/${id}`,
    START: (id: string) => `/sessions/${id}/start`,
    END: (id: string) => `/sessions/${id}/end`,
  },

  // 과제 관련
  ASSIGNMENTS: {
    LIST: (courseId: string) => `/courses/${courseId}/assignments`,
    DETAIL: (id: string) => `/assignments/${id}`,
    CREATE: (courseId: string) => `/courses/${courseId}/assignments`,
    UPDATE: (id: string) => `/assignments/${id}`,
    DELETE: (id: string) => `/assignments/${id}`,
    SUBMIT: (id: string) => `/assignments/${id}/submit`,
  },

  // 투표 관련
  POLLS: {
    LIST: (sessionId: string) => `/sessions/${sessionId}/polls`,
    DETAIL: (id: string) => `/polls/${id}`,
    CREATE: (sessionId: string) => `/sessions/${sessionId}/polls`,
    UPDATE: (id: string) => `/polls/${id}`,
    DELETE: (id: string) => `/polls/${id}`,
    VOTE: (id: string) => `/polls/${id}/vote`,
    RESULTS: (id: string) => `/polls/${id}/results`,
  },

  // 분석 관련
  ANALYTICS: {
    COURSE: (courseId: string) => `/analytics/courses/${courseId}`,
    STUDENT: (studentId: string) => `/analytics/students/${studentId}`,
    SESSION: (sessionId: string) => `/analytics/sessions/${sessionId}`,
    DASHBOARD: '/analytics/dashboard',
  },

  // 프로필 관련
  PROFILE: {
    GET: '/v1/users/profile',
    UPDATE: '/v1/users/profile',
    UPLOAD_AVATAR: '/v1/users/profile/avatar',
    DELETE_AVATAR: '/v1/users/profile/avatar',
    CHANGE_EMAIL: '/v1/users/email/change',
    VERIFY_EMAIL: '/v1/users/email/verify',
    CHANGE_PASSWORD: '/v1/users/password',
  },

  // Admin 관련
  ADMIN: {
    USERS: '/v1/admin/users',
    USER_DETAIL: (userId: string) => `/v1/admin/users/${userId}`,
    CHANGE_ROLE: (userId: string) => `/v1/admin/users/${userId}/role`,
    CHANGE_STATUS: (userId: string) => `/v1/admin/users/${userId}/status`,
    ROLES: '/v1/admin/roles',
    ASSIGN_ROLE: (userId: string) => `/v1/admin/users/${userId}/roles`,
    REMOVE_ROLE: (userId: string, roleId: string) => `/v1/admin/users/${userId}/roles/${roleId}`,
    STATISTICS: '/v1/admin/statistics/roles',
    SEARCH_USERS: '/v1/admin/users/search',
    BATCH_DELETE: '/v1/admin/users/batch-delete',
    EXPORT_CSV: '/v1/admin/users/export',
  },

  // 파일 관련
  FILES: {
    UPLOAD: (courseId: string) => `/v1/courses/${courseId}/files`,
    LIST: (courseId: string) => `/v1/courses/${courseId}/files`,
    DETAIL: (fileId: string) => `/v1/files/${fileId}`,
    DELETE: (fileId: string) => `/v1/files/${fileId}`,
    DOWNLOAD: (fileId: string) => `/v1/files/${fileId}/download`,
    SEARCH: (courseId: string) => `/v1/courses/${courseId}/files/search`,
    UPDATE_PERMISSION: (fileId: string) => `/v1/files/${fileId}/permission`,
    CREATE_FOLDER: (courseId: string) => `/v1/courses/${courseId}/folders`,
    FOLDER_TREE: (courseId: string) => `/v1/courses/${courseId}/folders/tree`,
    DELETE_FOLDER: (folderId: string) => `/v1/folders/${folderId}`,
  },
} as const;
