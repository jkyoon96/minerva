/**
 * 대시보드 관련 타입 정의
 */

/**
 * 일정 항목 타입
 */
export type ScheduleItemType = 'session' | 'assignment';

/**
 * 알림 타입
 */
export type NotificationType = 'assignment' | 'session' | 'student' | 'system';

/**
 * 일정 항목
 */
export interface ScheduleItem {
  id: string;
  courseId: string;
  courseTitle: string;
  title: string;
  scheduledAt: string;
  duration: number;
  type: ScheduleItemType;
}

/**
 * 알림
 */
export interface Notification {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  createdAt: string;
  read: boolean;
}

/**
 * 채점 대기 항목
 */
export interface PendingGradingItem {
  id: string;
  assignmentId: string;
  assignmentTitle: string;
  courseTitle: string;
  studentCount: number;
  dueDate: string;
}

/**
 * 위험 학생
 */
export interface AtRiskStudent {
  id: string;
  studentId: string;
  studentName: string;
  courseTitle: string;
  reason: string;
  score: number;
}

/**
 * 마감 임박 과제
 */
export interface UpcomingAssignment {
  id: string;
  courseId: string;
  courseTitle: string;
  title: string;
  dueDate: string;
  submitted: boolean;
  grade?: number;
}

/**
 * 참여도 정보
 */
export interface ParticipationInfo {
  courseId: string;
  courseTitle: string;
  attendanceRate: number;
  participationScore: number;
}

/**
 * 최근 성적
 */
export interface RecentGrade {
  id: string;
  assignmentTitle: string;
  courseTitle: string;
  grade: number;
  maxGrade: number;
  gradedAt: string;
}
