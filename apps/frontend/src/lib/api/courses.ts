/**
 * 코스 관련 API 함수
 */

import apiClient, { parseApiError } from './client';
import { API_ENDPOINTS } from './endpoints';
import {
  ApiResponse,
  GetCoursesParams,
  CreateCourseRequest,
  UpdateCourseRequest,
  Enrollment,
} from './types';
import { Course } from '@/types';

/**
 * 코스 목록 조회
 */
export const getCourses = async (
  params?: GetCoursesParams,
): Promise<{
  courses: Course[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}> => {
  try {
    const response = await apiClient.get<
      ApiResponse<{
        courses: Course[];
      }>
    >(API_ENDPOINTS.COURSES.LIST, { params });

    return {
      courses: response.data.data.courses,
      pagination: response.data.meta?.pagination || {
        page: 1,
        limit: 10,
        total: 0,
        totalPages: 0,
      },
    };
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 코스 상세 조회
 */
export const getCourse = async (id: string): Promise<Course> => {
  try {
    const response = await apiClient.get<ApiResponse<Course>>(
      API_ENDPOINTS.COURSES.DETAIL(id),
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 코스 생성
 */
export const createCourse = async (data: CreateCourseRequest): Promise<Course> => {
  try {
    const response = await apiClient.post<ApiResponse<Course>>(
      API_ENDPOINTS.COURSES.CREATE,
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 코스 업데이트
 */
export const updateCourse = async (
  id: string,
  data: UpdateCourseRequest,
): Promise<Course> => {
  try {
    const response = await apiClient.patch<ApiResponse<Course>>(
      API_ENDPOINTS.COURSES.UPDATE(id),
      data,
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 코스 삭제
 */
export const deleteCourse = async (id: string): Promise<void> => {
  try {
    await apiClient.delete(API_ENDPOINTS.COURSES.DELETE(id));
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 코스 수강생 목록 조회
 */
export const getEnrollments = async (courseId: string): Promise<Enrollment[]> => {
  try {
    const response = await apiClient.get<ApiResponse<{ enrollments: Enrollment[] }>>(
      API_ENDPOINTS.COURSES.ENROLLMENTS(courseId),
    );
    return response.data.data.enrollments;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 코스 수강 신청
 */
export const enrollCourse = async (courseId: string): Promise<Enrollment> => {
  try {
    const response = await apiClient.post<ApiResponse<Enrollment>>(
      API_ENDPOINTS.COURSES.ENROLL(courseId),
    );
    return response.data.data;
  } catch (error) {
    throw parseApiError(error);
  }
};

/**
 * 코스 수강 취소
 */
export const unenrollCourse = async (courseId: string): Promise<void> => {
  try {
    await apiClient.delete(API_ENDPOINTS.COURSES.UNENROLL(courseId));
  } catch (error) {
    throw parseApiError(error);
  }
};
