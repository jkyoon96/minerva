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
} as const;
