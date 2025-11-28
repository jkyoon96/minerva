/**
 * 코스 상태 관리 스토어 (Zustand)
 * - 코스 목록
 * - 선택된 코스
 * - 필터링 상태
 */

import { create } from 'zustand';
import { Course } from '@/types';

interface CourseState {
  // 상태
  courses: Course[];
  selectedCourse: Course | null;
  isLoading: boolean;
  error: string | null;

  // 필터/검색
  searchQuery: string;
  selectedSemester: string | null;

  // 페이지네이션
  currentPage: number;
  totalPages: number;
  totalCourses: number;

  // 액션
  setCourses: (courses: Course[]) => void;
  setSelectedCourse: (course: Course | null) => void;
  selectCourseById: (id: string) => void;
  setLoading: (isLoading: boolean) => void;
  setError: (error: string | null) => void;
  setSearchQuery: (query: string) => void;
  setSelectedSemester: (semester: string | null) => void;
  setPagination: (page: number, totalPages: number, total: number) => void;
  addCourse: (course: Course) => void;
  updateCourse: (id: string, updates: Partial<Course>) => void;
  removeCourse: (id: string) => void;
  reset: () => void;
}

const initialState = {
  courses: [],
  selectedCourse: null,
  isLoading: false,
  error: null,
  searchQuery: '',
  selectedSemester: null,
  currentPage: 1,
  totalPages: 0,
  totalCourses: 0,
};

export const useCourseStore = create<CourseState>()((set, get) => ({
  ...initialState,

  // 코스 목록 설정
  setCourses: (courses) => {
    set({ courses });
  },

  // 선택된 코스 설정
  setSelectedCourse: (course) => {
    set({ selectedCourse: course });
  },

  // ID로 코스 선택
  selectCourseById: (id) => {
    const course = get().courses.find((c) => c.id === id);
    set({ selectedCourse: course || null });
  },

  // 로딩 상태 설정
  setLoading: (isLoading) => {
    set({ isLoading });
  },

  // 에러 설정
  setError: (error) => {
    set({ error });
  },

  // 검색어 설정
  setSearchQuery: (query) => {
    set({ searchQuery: query, currentPage: 1 }); // 검색 시 첫 페이지로
  },

  // 학기 필터 설정
  setSelectedSemester: (semester) => {
    set({ selectedSemester: semester, currentPage: 1 }); // 필터 변경 시 첫 페이지로
  },

  // 페이지네이션 설정
  setPagination: (page, totalPages, total) => {
    set({
      currentPage: page,
      totalPages,
      totalCourses: total,
    });
  },

  // 코스 추가
  addCourse: (course) => {
    set((state) => ({
      courses: [course, ...state.courses],
      totalCourses: state.totalCourses + 1,
    }));
  },

  // 코스 업데이트
  updateCourse: (id, updates) => {
    set((state) => ({
      courses: state.courses.map((course) =>
        course.id === id ? { ...course, ...updates } : course,
      ),
      selectedCourse:
        state.selectedCourse?.id === id
          ? { ...state.selectedCourse, ...updates }
          : state.selectedCourse,
    }));
  },

  // 코스 제거
  removeCourse: (id) => {
    set((state) => ({
      courses: state.courses.filter((course) => course.id !== id),
      selectedCourse: state.selectedCourse?.id === id ? null : state.selectedCourse,
      totalCourses: Math.max(0, state.totalCourses - 1),
    }));
  },

  // 상태 초기화
  reset: () => {
    set(initialState);
  },
}));
