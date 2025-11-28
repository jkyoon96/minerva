/**
 * 세션 관련 타입 정의
 */

import { Session } from './index';

// Re-export for convenience
export type { Session };

/**
 * 확장된 세션 인터페이스
 */
export interface ExtendedSession extends Session {
  course?: {
    id: string;
    title: string;
    code: string;
  };
  attendanceCount?: number;
  totalStudents?: number;
}

/**
 * 세션 필터 옵션
 */
export interface SessionFilters {
  search?: string;
  status?: Session['status'];
  courseId?: string;
  dateFrom?: string;
  dateTo?: string;
}

/**
 * 세션 정렬 옵션
 */
export type SessionSortBy = 'scheduledAt' | 'createdAt' | 'title' | 'duration';
export type SessionSortOrder = 'asc' | 'desc';

export interface SessionSortOptions {
  sortBy: SessionSortBy;
  sortOrder: SessionSortOrder;
}

/**
 * 캘린더 이벤트 (세션을 캘린더 형식으로 표현)
 */
export interface CalendarEvent {
  id: string;
  title: string;
  start: Date;
  end: Date;
  color?: string;
  metadata?: {
    courseId: string;
    courseTitle: string;
    status: Session['status'];
  };
}
