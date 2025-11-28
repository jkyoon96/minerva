/**
 * 코스 관련 타입 정의
 */

import { Course, User } from './index';

/**
 * 코스 상태
 */
export type CourseStatus = 'active' | 'archived' | 'draft';

/**
 * 확장된 코스 인터페이스
 */
export interface ExtendedCourse extends Course {
  status?: CourseStatus;
  enrollmentCount?: number;
  sessionCount?: number;
  assignmentCount?: number;
  isEnrolled?: boolean;
}

/**
 * 코스 생성 폼 데이터
 */
export interface CourseFormData {
  title: string;
  code: string;
  semester: string;
  description?: string;
}

/**
 * 코스 필터 옵션
 */
export interface CourseFilters {
  search?: string;
  semester?: string;
  status?: CourseStatus;
  role?: 'professor' | 'ta' | 'student';
}

/**
 * 코스 정렬 옵션
 */
export type CourseSortBy =
  | 'createdAt'
  | 'updatedAt'
  | 'title'
  | 'code'
  | 'enrollmentCount';

export type CourseSortOrder = 'asc' | 'desc';

export interface CourseSortOptions {
  sortBy: CourseSortBy;
  sortOrder: CourseSortOrder;
}

/**
 * 수강생 역할
 */
export type EnrollmentRole = 'student' | 'ta';

/**
 * 수강 정보
 */
export interface CourseEnrollment {
  id: string;
  userId: string;
  courseId: string;
  role: EnrollmentRole;
  enrolledAt: string;
  user?: User;
  course?: Course;
}

/**
 * 코스 통계
 */
export interface CourseStats {
  totalStudents: number;
  totalTAs: number;
  totalSessions: number;
  completedSessions: number;
  upcomingSessions: number;
  totalAssignments: number;
  averageAttendance: number;
  averageGrade?: number;
}
