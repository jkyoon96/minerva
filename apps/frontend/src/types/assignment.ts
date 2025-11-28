/**
 * 과제 관련 타입 정의
 */

import { Assignment, AssignmentSubmission } from '@/lib/api/assignments';

// Re-export for convenience
export type { Assignment, AssignmentSubmission };

/**
 * 과제 상태
 */
export type AssignmentStatus = 'upcoming' | 'active' | 'overdue' | 'graded';

/**
 * 확장된 과제 인터페이스
 */
export interface ExtendedAssignment extends Assignment {
  status?: AssignmentStatus;
  submissionCount?: number;
  gradedCount?: number;
  mySubmission?: AssignmentSubmission;
}

/**
 * 과제 필터 옵션
 */
export interface AssignmentFilters {
  search?: string;
  status?: AssignmentStatus;
  courseId?: string;
}

/**
 * 과제 정렬 옵션
 */
export type AssignmentSortBy = 'dueDate' | 'createdAt' | 'title' | 'grade';
export type AssignmentSortOrder = 'asc' | 'desc';

export interface AssignmentSortOptions {
  sortBy: AssignmentSortBy;
  sortOrder: AssignmentSortOrder;
}
