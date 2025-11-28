/**
 * 코스 상세 페이지
 * 세션, 과제, 녹화 탭으로 구성
 */

'use client';

import * as React from 'react';
import { use } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, Users, Calendar, FileText } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { getCourse } from '@/lib/api/courses';
import { getSessions } from '@/lib/api/sessions';
import { getAssignments } from '@/lib/api/assignments';
import { formatDateTime } from '@/lib/utils';
import Link from 'next/link';

interface CourseDetailPageProps {
  params: Promise<{
    courseId: string;
  }>;
}

export default function CourseDetailPage({ params }: CourseDetailPageProps) {
  const { courseId } = use(params);

  const { data: course, isLoading: courseLoading } = useQuery({
    queryKey: ['course', courseId],
    queryFn: () => getCourse(courseId),
  });

  const { data: sessions, isLoading: sessionsLoading } = useQuery({
    queryKey: ['sessions', courseId],
    queryFn: () => getSessions(courseId),
    enabled: !!courseId,
  });

  const { data: assignments, isLoading: assignmentsLoading } = useQuery({
    queryKey: ['assignments', courseId],
    queryFn: () => getAssignments(courseId),
    enabled: !!courseId,
  });

  if (courseLoading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (!course) {
    return (
      <div className="text-center">
        <p className="text-muted-foreground">코스를 찾을 수 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div>
        <Button variant="ghost" size="sm" asChild className="mb-4">
          <Link href="/professor/courses">
            <ArrowLeft className="mr-2 h-4 w-4" />
            코스 목록으로
          </Link>
        </Button>
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-2">
              <Badge variant="outline">{course.code}</Badge>
              <Badge>{course.semester}</Badge>
            </div>
            <h1 className="mt-2 text-3xl font-bold">{course.title}</h1>
            {course.description && (
              <p className="mt-1 text-muted-foreground">{course.description}</p>
            )}
          </div>
          <Button asChild>
            <Link href={`/courses/${courseId}/settings`}>코스 설정</Link>
          </Button>
        </div>
      </div>

      {/* 탭 */}
      <Tabs defaultValue="sessions" className="space-y-4">
        <TabsList>
          <TabsTrigger value="sessions">
            <Calendar className="mr-2 h-4 w-4" />
            세션
          </TabsTrigger>
          <TabsTrigger value="assignments">
            <FileText className="mr-2 h-4 w-4" />
            과제
          </TabsTrigger>
          <TabsTrigger value="students">
            <Users className="mr-2 h-4 w-4" />
            수강생
          </TabsTrigger>
        </TabsList>

        {/* 세션 탭 */}
        <TabsContent value="sessions" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>세션 목록</CardTitle>
                  <CardDescription>코스의 모든 세션</CardDescription>
                </div>
                <Button asChild>
                  <Link href={`/courses/${courseId}/sessions/new`}>세션 추가</Link>
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              {sessionsLoading ? (
                <LoadingSpinner />
              ) : !sessions || sessions.length === 0 ? (
                <p className="text-sm text-muted-foreground">등록된 세션이 없습니다</p>
              ) : (
                <div className="space-y-2">
                  {sessions.map((session) => (
                    <div
                      key={session.id}
                      className="flex items-center justify-between rounded-lg border p-4"
                    >
                      <div>
                        <h4 className="font-semibold">{session.title}</h4>
                        <div className="mt-1 flex items-center gap-2 text-sm text-muted-foreground">
                          <span>{formatDateTime(session.scheduledAt)}</span>
                          <span>({session.duration}분)</span>
                          <Badge variant="outline">{session.status}</Badge>
                        </div>
                      </div>
                      <Button variant="outline" size="sm" asChild>
                        <Link href={`/sessions/${session.id}`}>상세보기</Link>
                      </Button>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        {/* 과제 탭 */}
        <TabsContent value="assignments" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>과제 목록</CardTitle>
                  <CardDescription>코스의 모든 과제</CardDescription>
                </div>
                <Button asChild>
                  <Link href={`/courses/${courseId}/assignments/new`}>과제 추가</Link>
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              {assignmentsLoading ? (
                <LoadingSpinner />
              ) : !assignments || assignments.length === 0 ? (
                <p className="text-sm text-muted-foreground">등록된 과제가 없습니다</p>
              ) : (
                <div className="space-y-2">
                  {assignments.map((assignment) => (
                    <div
                      key={assignment.id}
                      className="flex items-center justify-between rounded-lg border p-4"
                    >
                      <div>
                        <h4 className="font-semibold">{assignment.title}</h4>
                        <div className="mt-1 flex items-center gap-2 text-sm text-muted-foreground">
                          <span>마감: {formatDateTime(assignment.dueDate)}</span>
                          <Badge variant="outline">{assignment.maxGrade}점</Badge>
                        </div>
                      </div>
                      <Button variant="outline" size="sm" asChild>
                        <Link href={`/assignments/${assignment.id}`}>상세보기</Link>
                      </Button>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        {/* 수강생 탭 */}
        <TabsContent value="students" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>수강생 관리</CardTitle>
                  <CardDescription>코스 수강생 목록</CardDescription>
                </div>
                <Button asChild>
                  <Link href={`/courses/${courseId}/enrollments`}>수강생 관리</Link>
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground">
                수강생 관리 페이지로 이동하여 상세 정보를 확인하세요
              </p>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
