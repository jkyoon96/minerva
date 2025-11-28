/**
 * 코스 카드 컴포넌트
 * 코스 목록에서 사용되는 카드
 */

'use client';

import * as React from 'react';
import Link from 'next/link';
import { BookOpen, Users, Calendar, Clock } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { ExtendedCourse } from '@/types/course';
import { cn } from '@/lib/utils';

interface CourseCardProps {
  course: ExtendedCourse;
  viewMode?: 'student' | 'professor';
  onEnroll?: (courseId: string) => void;
  className?: string;
}

export function CourseCard({ course, viewMode = 'student', onEnroll, className }: CourseCardProps) {
  const statusColors = {
    active: 'bg-green-100 text-green-800',
    archived: 'bg-gray-100 text-gray-800',
    draft: 'bg-yellow-100 text-yellow-800',
  };

  return (
    <Card className={cn('transition-shadow hover:shadow-md', className)}>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-2">
              <BookOpen className="h-5 w-5 text-primary" />
              <Badge variant="outline">{course.code}</Badge>
              {course.status && (
                <Badge className={statusColors[course.status]}>{course.status}</Badge>
              )}
            </div>
            <CardTitle className="mt-2">
              <Link
                href={`/courses/${course.id}`}
                className="hover:text-primary transition-colors"
              >
                {course.title}
              </Link>
            </CardTitle>
            <CardDescription className="mt-1">{course.semester}</CardDescription>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        {course.description && (
          <p className="mb-4 line-clamp-2 text-sm text-muted-foreground">{course.description}</p>
        )}

        <div className="flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
          {course.enrollmentCount !== undefined && (
            <div className="flex items-center gap-1">
              <Users className="h-4 w-4" />
              <span>{course.enrollmentCount}명</span>
            </div>
          )}
          {course.sessionCount !== undefined && (
            <div className="flex items-center gap-1">
              <Calendar className="h-4 w-4" />
              <span>{course.sessionCount}개 세션</span>
            </div>
          )}
          {course.assignmentCount !== undefined && (
            <div className="flex items-center gap-1">
              <Clock className="h-4 w-4" />
              <span>{course.assignmentCount}개 과제</span>
            </div>
          )}
        </div>

        {viewMode === 'student' && !course.isEnrolled && onEnroll && (
          <div className="mt-4">
            <Button onClick={() => onEnroll(course.id)} className="w-full">
              수강 신청
            </Button>
          </div>
        )}

        {viewMode === 'professor' && (
          <div className="mt-4 flex gap-2">
            <Button variant="outline" size="sm" asChild className="flex-1">
              <Link href={`/courses/${course.id}`}>상세보기</Link>
            </Button>
            <Button variant="outline" size="sm" asChild className="flex-1">
              <Link href={`/courses/${course.id}/settings`}>설정</Link>
            </Button>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
