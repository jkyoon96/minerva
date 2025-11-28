/**
 * 대시보드 관련 API 함수
 */

import apiClient, { parseApiError } from './client';
import { ApiResponse } from './types';

/**
 * 교수 대시보드 데이터
 */
export interface ProfessorDashboardData {
  stats: {
    totalCourses: number;
    totalStudents: number;
    totalAssignments: number;
    totalSessions: number;
  };
  todaySchedule: Array<{
    id: string;
    courseId: string;
    courseTitle: string;
    title: string;
    scheduledAt: string;
    duration: number;
    type: 'session' | 'assignment';
  }>;
  notifications: Array<{
    id: string;
    type: 'assignment' | 'session' | 'student' | 'system';
    title: string;
    message: string;
    createdAt: string;
    read: boolean;
  }>;
  pendingGrading: Array<{
    id: string;
    assignmentId: string;
    assignmentTitle: string;
    courseTitle: string;
    studentCount: number;
    dueDate: string;
  }>;
  atRiskStudents: Array<{
    id: string;
    studentId: string;
    studentName: string;
    courseTitle: string;
    reason: string;
    score: number;
  }>;
}

/**
 * 학생 대시보드 데이터
 */
export interface StudentDashboardData {
  stats: {
    enrolledCourses: number;
    completedAssignments: number;
    averageGrade: number;
  };
  todaySchedule: Array<{
    id: string;
    courseId: string;
    courseTitle: string;
    title: string;
    scheduledAt: string;
    duration: number;
    type: 'session' | 'assignment';
  }>;
  upcomingAssignments: Array<{
    id: string;
    courseId: string;
    courseTitle: string;
    title: string;
    dueDate: string;
    submitted: boolean;
    grade?: number;
  }>;
  participation: Array<{
    courseId: string;
    courseTitle: string;
    attendanceRate: number;
    participationScore: number;
  }>;
  recentGrades: Array<{
    id: string;
    assignmentTitle: string;
    courseTitle: string;
    grade: number;
    maxGrade: number;
    gradedAt: string;
  }>;
}

/**
 * 교수 대시보드 조회
 */
export const getProfessorDashboard = async (): Promise<ProfessorDashboardData> => {
  try {
    const response = await apiClient.get<ApiResponse<ProfessorDashboardData>>('/v1/dashboard');
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 학생 대시보드 조회
 */
export const getStudentDashboard = async (): Promise<StudentDashboardData> => {
  try {
    const response = await apiClient.get<ApiResponse<StudentDashboardData>>(
      '/v1/student/dashboard',
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};
