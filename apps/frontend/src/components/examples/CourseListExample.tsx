/**
 * 코스 목록 예제 컴포넌트
 * useCourses 훅 사용법 데모
 */

'use client';

import { useState } from 'react';
import { useCourses, useDebounce } from '@/hooks';
import { useCourseStore } from '@/stores/courseStore';

export function CourseListExample() {
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const debouncedSearch = useDebounce(search, 500);

  // React Query로 코스 데이터 가져오기
  const { data, isLoading, error, refetch } = useCourses({
    search: debouncedSearch,
    page,
    limit: 10,
  });

  // Zustand store에서 선택된 코스 가져오기
  const { selectedCourse, selectCourseById } = useCourseStore();

  if (isLoading) {
    return <div className="p-4">Loading courses...</div>;
  }

  if (error) {
    return (
      <div className="p-4 text-red-600">
        Error: {error.message}
        <button onClick={() => refetch()} className="ml-4 underline">
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className="p-4 space-y-4">
      {/* 검색 입력 */}
      <div>
        <input
          type="search"
          placeholder="코스 검색..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full px-4 py-2 border rounded-lg"
        />
      </div>

      {/* 코스 목록 */}
      <div className="space-y-2">
        {data?.courses.map((course) => (
          <div
            key={course.id}
            onClick={() => selectCourseById(course.id)}
            className={`p-4 border rounded-lg cursor-pointer transition-colors ${
              selectedCourse?.id === course.id
                ? 'bg-blue-50 border-blue-500'
                : 'hover:bg-gray-50'
            }`}
          >
            <h3 className="font-semibold">{course.title}</h3>
            <p className="text-sm text-gray-600">
              {course.code} - {course.semester}
            </p>
            {course.description && (
              <p className="text-sm text-gray-500 mt-1">{course.description}</p>
            )}
          </div>
        ))}
      </div>

      {/* 페이지네이션 */}
      {data && (
        <div className="flex items-center justify-between">
          <p className="text-sm text-gray-600">
            Total: {data.pagination.total} courses (Page {data.pagination.page} of{' '}
            {data.pagination.totalPages})
          </p>

          <div className="flex gap-2">
            <button
              onClick={() => setPage((p) => Math.max(1, p - 1))}
              disabled={page === 1}
              className="px-4 py-2 border rounded-lg disabled:opacity-50"
            >
              Previous
            </button>
            <button
              onClick={() => setPage((p) => p + 1)}
              disabled={page >= data.pagination.totalPages}
              className="px-4 py-2 border rounded-lg disabled:opacity-50"
            >
              Next
            </button>
          </div>
        </div>
      )}

      {/* 선택된 코스 정보 */}
      {selectedCourse && (
        <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
          <h4 className="font-semibold mb-2">Selected Course:</h4>
          <p>
            <strong>{selectedCourse.title}</strong>
          </p>
          <p className="text-sm text-gray-600">
            {selectedCourse.code} - {selectedCourse.semester}
          </p>
        </div>
      )}
    </div>
  );
}
