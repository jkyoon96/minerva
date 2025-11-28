/**
 * 과제 관련 API 함수
 */

import apiClient, { parseApiError } from './client';
import { API_ENDPOINTS } from './endpoints';
import { ApiResponse } from './types';

/**
 * 과제 인터페이스
 */
export interface Assignment {
  id: string;
  courseId: string;
  title: string;
  description?: string;
  dueDate: string;
  maxGrade: number;
  attachments?: string[];
  createdAt: string;
  updatedAt: string;
}

/**
 * 과제 제출 인터페이스
 */
export interface AssignmentSubmission {
  id: string;
  assignmentId: string;
  studentId: string;
  studentName?: string;
  content?: string;
  attachments?: string[];
  grade?: number;
  feedback?: string;
  submittedAt: string;
  gradedAt?: string;
}

/**
 * 과제 생성 요청
 */
export interface CreateAssignmentRequest {
  title: string;
  description?: string;
  dueDate: string;
  maxGrade: number;
  attachments?: string[];
}

/**
 * 과제 업데이트 요청
 */
export interface UpdateAssignmentRequest {
  title?: string;
  description?: string;
  dueDate?: string;
  maxGrade?: number;
  attachments?: string[];
}

/**
 * 과제 제출 요청
 */
export interface SubmitAssignmentRequest {
  content?: string;
  attachments?: string[];
}

/**
 * 코스의 과제 목록 조회
 */
export const getAssignments = async (courseId: string): Promise<Assignment[]> => {
  try {
    const response = await apiClient.get<ApiResponse<{ assignments: Assignment[] }>>(
      API_ENDPOINTS.ASSIGNMENTS.LIST(courseId),
    );
    return response.data.data.assignments;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 과제 상세 조회
 */
export const getAssignment = async (id: string): Promise<Assignment> => {
  try {
    const response = await apiClient.get<ApiResponse<Assignment>>(
      API_ENDPOINTS.ASSIGNMENTS.DETAIL(id),
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 과제 생성
 */
export const createAssignment = async (
  courseId: string,
  data: CreateAssignmentRequest,
): Promise<Assignment> => {
  try {
    const response = await apiClient.post<ApiResponse<Assignment>>(
      API_ENDPOINTS.ASSIGNMENTS.CREATE(courseId),
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 과제 업데이트
 */
export const updateAssignment = async (
  id: string,
  data: UpdateAssignmentRequest,
): Promise<Assignment> => {
  try {
    const response = await apiClient.patch<ApiResponse<Assignment>>(
      API_ENDPOINTS.ASSIGNMENTS.UPDATE(id),
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 과제 삭제
 */
export const deleteAssignment = async (id: string): Promise<void> => {
  try {
    await apiClient.delete(API_ENDPOINTS.ASSIGNMENTS.DELETE(id));
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 과제 제출
 */
export const submitAssignment = async (
  id: string,
  data: SubmitAssignmentRequest,
): Promise<AssignmentSubmission> => {
  try {
    const response = await apiClient.post<ApiResponse<AssignmentSubmission>>(
      API_ENDPOINTS.ASSIGNMENTS.SUBMIT(id),
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 과제 제출 목록 조회 (교수용)
 */
export const getAssignmentSubmissions = async (
  assignmentId: string,
): Promise<AssignmentSubmission[]> => {
  try {
    const response = await apiClient.get<ApiResponse<{ submissions: AssignmentSubmission[] }>>(
      `/assignments/${assignmentId}/submissions`,
    );
    return response.data.data.submissions;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 과제 채점
 */
export const gradeSubmission = async (
  submissionId: string,
  data: { grade: number; feedback?: string },
): Promise<AssignmentSubmission> => {
  try {
    const response = await apiClient.patch<ApiResponse<AssignmentSubmission>>(
      `/submissions/${submissionId}/grade`,
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};
