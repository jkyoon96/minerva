/**
 * 교수 코스 목록 페이지
 * 운영 중인 코스 목록 및 관리
 */

'use client';

import * as React from 'react';
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Plus, Search } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { CourseCard } from '@/components/course/course-card';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { EmptyState } from '@/components/common/EmptyState';
import { getCourses } from '@/lib/api/courses';
import { CourseStatus } from '@/types/course';
import Link from 'next/link';

export default function ProfessorCoursesPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<CourseStatus | 'all'>('all');

  const { data, isLoading } = useQuery({
    queryKey: ['courses', 'professor', searchQuery, statusFilter],
    queryFn: () =>
      getCourses({
        search: searchQuery,
        role: 'professor',
        status: statusFilter === 'all' ? undefined : statusFilter,
      }),
  });

  const courses = data?.courses || [];

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">내 코스</h1>
          <p className="text-muted-foreground">운영 중인 코스를 관리하세요</p>
        </div>
        <Button asChild>
          <Link href="/professor/courses/new">
            <Plus className="mr-2 h-4 w-4" />
            새 코스 만들기
          </Link>
        </Button>
      </div>

      {/* 필터 */}
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
        <Select value={statusFilter} onValueChange={(value) => setStatusFilter(value as any)}>
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="상태 선택" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">전체</SelectItem>
            <SelectItem value="active">활성</SelectItem>
            <SelectItem value="archived">보관됨</SelectItem>
            <SelectItem value="draft">임시저장</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* 코스 목록 */}
      {isLoading ? (
        <div className="flex min-h-[400px] items-center justify-center">
          <LoadingSpinner />
        </div>
      ) : courses.length === 0 ? (
        <EmptyState
          title="코스가 없습니다"
          description="새로운 코스를 만들어 학생들을 가르쳐보세요."
          action={
            <Button asChild>
              <Link href="/professor/courses/new">
                <Plus className="mr-2 h-4 w-4" />
                새 코스 만들기
              </Link>
            </Button>
          }
        />
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {courses.map((course) => (
            <CourseCard key={course.id} course={course} viewMode="professor" />
          ))}
        </div>
      )}
    </div>
  );
}
