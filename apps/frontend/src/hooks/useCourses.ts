/**
 * 코스 관련 커스텀 훅
 * React Query와 courseStore를 사용하여 코스 데이터 관리
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useCourseStore } from '@/stores/courseStore';
import * as coursesApi from '@/lib/api/courses';
import {
  GetCoursesParams,
  CreateCourseRequest,
  UpdateCourseRequest,
} from '@/lib/api/types';
import { useUiStore } from '@/stores/uiStore';

// Query Keys
export const COURSE_QUERY_KEYS = {
  all: ['courses'] as const,
  lists: () => [...COURSE_QUERY_KEYS.all, 'list'] as const,
  list: (params?: GetCoursesParams) =>
    [...COURSE_QUERY_KEYS.lists(), params] as const,
  details: () => [...COURSE_QUERY_KEYS.all, 'detail'] as const,
  detail: (id: string) => [...COURSE_QUERY_KEYS.details(), id] as const,
  enrollments: (id: string) =>
    [...COURSE_QUERY_KEYS.detail(id), 'enrollments'] as const,
};

/**
 * 코스 목록 조회 훅
 */
export const useCourses = (params?: GetCoursesParams) => {
  const setCourses = useCourseStore((state) => state.setCourses);
  const setPagination = useCourseStore((state) => state.setPagination);

  const query = useQuery({
    queryKey: COURSE_QUERY_KEYS.list(params),
    queryFn: async () => {
      const result = await coursesApi.getCourses(params);
      return result;
    },
    staleTime: 1000 * 60 * 5, // 5분
  });

  // 데이터가 로드되면 store 업데이트
  if (query.data) {
    setCourses(query.data.courses);
    setPagination(
      query.data.pagination.page,
      query.data.pagination.totalPages,
      query.data.pagination.total,
    );
  }

  return query;
};

/**
 * 코스 상세 조회 훅
 */
export const useCourse = (id: string) => {
  const setSelectedCourse = useCourseStore((state) => state.setSelectedCourse);

  const query = useQuery({
    queryKey: COURSE_QUERY_KEYS.detail(id),
    queryFn: async () => {
      const course = await coursesApi.getCourse(id);
      return course;
    },
    enabled: !!id, // id가 있을 때만 실행
  });

  // 데이터가 로드되면 store 업데이트
  if (query.data) {
    setSelectedCourse(query.data);
  }

  return query;
};

/**
 * 코스 생성 훅
 */
export const useCreateCourse = () => {
  const queryClient = useQueryClient();
  const addCourse = useCourseStore((state) => state.addCourse);
  const addNotification = useUiStore((state) => state.addNotification);

  return useMutation({
    mutationFn: (data: CreateCourseRequest) => coursesApi.createCourse(data),
    onSuccess: (newCourse) => {
      // 캐시 무효화
      queryClient.invalidateQueries({ queryKey: COURSE_QUERY_KEYS.lists() });

      // Store 업데이트
      addCourse(newCourse);

      // 성공 알림
      addNotification({
        type: 'success',
        title: '코스 생성 완료',
        message: `${newCourse.title} 코스가 생성되었습니다.`,
        duration: 3000,
      });
    },
    onError: (error: any) => {
      addNotification({
        type: 'error',
        title: '코스 생성 실패',
        message: error.message || '코스 생성 중 오류가 발생했습니다.',
        duration: 5000,
      });
    },
  });
};

/**
 * 코스 업데이트 훅
 */
export const useUpdateCourse = () => {
  const queryClient = useQueryClient();
  const updateCourse = useCourseStore((state) => state.updateCourse);
  const addNotification = useUiStore((state) => state.addNotification);

  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: UpdateCourseRequest;
    }) => coursesApi.updateCourse(id, data),
    onSuccess: (updatedCourse) => {
      // 캐시 무효화
      queryClient.invalidateQueries({ queryKey: COURSE_QUERY_KEYS.lists() });
      queryClient.invalidateQueries({
        queryKey: COURSE_QUERY_KEYS.detail(updatedCourse.id),
      });

      // Store 업데이트
      updateCourse(updatedCourse.id, updatedCourse);

      // 성공 알림
      addNotification({
        type: 'success',
        title: '코스 업데이트 완료',
        message: `${updatedCourse.title} 코스가 업데이트되었습니다.`,
        duration: 3000,
      });
    },
    onError: (error: any) => {
      addNotification({
        type: 'error',
        title: '코스 업데이트 실패',
        message: error.message || '코스 업데이트 중 오류가 발생했습니다.',
        duration: 5000,
      });
    },
  });
};

/**
 * 코스 삭제 훅
 */
export const useDeleteCourse = () => {
  const queryClient = useQueryClient();
  const removeCourse = useCourseStore((state) => state.removeCourse);
  const addNotification = useUiStore((state) => state.addNotification);

  return useMutation({
    mutationFn: (id: string) => coursesApi.deleteCourse(id),
    onSuccess: (_, deletedId) => {
      // 캐시 무효화
      queryClient.invalidateQueries({ queryKey: COURSE_QUERY_KEYS.lists() });
      queryClient.removeQueries({ queryKey: COURSE_QUERY_KEYS.detail(deletedId) });

      // Store 업데이트
      removeCourse(deletedId);

      // 성공 알림
      addNotification({
        type: 'success',
        title: '코스 삭제 완료',
        message: '코스가 삭제되었습니다.',
        duration: 3000,
      });
    },
    onError: (error: any) => {
      addNotification({
        type: 'error',
        title: '코스 삭제 실패',
        message: error.message || '코스 삭제 중 오류가 발생했습니다.',
        duration: 5000,
      });
    },
  });
};

/**
 * 코스 수강생 조회 훅
 */
export const useEnrollments = (courseId: string) => {
  return useQuery({
    queryKey: COURSE_QUERY_KEYS.enrollments(courseId),
    queryFn: () => coursesApi.getEnrollments(courseId),
    enabled: !!courseId,
  });
};

/**
 * 수강 신청 훅
 */
export const useEnrollCourse = () => {
  const queryClient = useQueryClient();
  const addNotification = useUiStore((state) => state.addNotification);

  return useMutation({
    mutationFn: (courseId: string) => coursesApi.enrollCourse(courseId),
    onSuccess: (_, courseId) => {
      // 캐시 무효화
      queryClient.invalidateQueries({ queryKey: COURSE_QUERY_KEYS.lists() });
      queryClient.invalidateQueries({
        queryKey: COURSE_QUERY_KEYS.enrollments(courseId),
      });

      addNotification({
        type: 'success',
        title: '수강 신청 완료',
        message: '수강 신청이 완료되었습니다.',
        duration: 3000,
      });
    },
    onError: (error: any) => {
      addNotification({
        type: 'error',
        title: '수강 신청 실패',
        message: error.message || '수강 신청 중 오류가 발생했습니다.',
        duration: 5000,
      });
    },
  });
};
