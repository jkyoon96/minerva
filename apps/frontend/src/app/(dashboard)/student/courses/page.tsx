/**
 * 학생 코스 목록 페이지
 * 수강 중인 코스 목록 및 코스 등록
 */

'use client';

import * as React from 'react';
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Plus, Search } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { CourseCard } from '@/components/course/course-card';
import { EnrollModal } from '@/components/course/enroll-modal';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { EmptyState } from '@/components/common/EmptyState';
import { getCourses } from '@/lib/api/courses';

export default function StudentCoursesPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [enrollModalOpen, setEnrollModalOpen] = useState(false);

  const { data, isLoading } = useQuery({
    queryKey: ['courses', 'student', searchQuery],
    queryFn: () => getCourses({ search: searchQuery, role: 'student' }),
  });

  const filteredCourses = data?.courses || [];

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">내 코스</h1>
          <p className="text-muted-foreground">수강 중인 코스를 확인하세요</p>
        </div>
        <Button onClick={() => setEnrollModalOpen(true)}>
          <Plus className="mr-2 h-4 w-4" />
          코스 등록
        </Button>
      </div>

      {/* 검색 */}
      <div className="flex items-center gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="코스 검색..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-9"
          />
        </div>
      </div>

      {/* 코스 목록 */}
      {isLoading ? (
        <div className="flex min-h-[400px] items-center justify-center">
          <LoadingSpinner />
        </div>
      ) : filteredCourses.length === 0 ? (
        <EmptyState
          title="수강 중인 코스가 없습니다"
          description="초대 코드를 사용하여 새로운 코스에 등록하세요."
          action={
            <Button onClick={() => setEnrollModalOpen(true)}>
              <Plus className="mr-2 h-4 w-4" />
              코스 등록
            </Button>
          }
        />
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {filteredCourses.map((course) => (
            <CourseCard key={course.id} course={course} viewMode="student" />
          ))}
        </div>
      )}

      {/* 등록 모달 */}
      <EnrollModal open={enrollModalOpen} onOpenChange={setEnrollModalOpen} />
    </div>
  );
}
