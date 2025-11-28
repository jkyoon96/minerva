/**
 * 교수 대시보드 페이지
 * 통계, 일정, 알림, 채점 대기, 위험 학생 표시
 */

'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import { BookOpen, Users, FileText, Video } from 'lucide-react';
import { StatCard } from '@/components/ui/stat-card';
import { ScheduleSection } from '@/components/dashboard/schedule-section';
import { NotificationSection } from '@/components/dashboard/notification-section';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { getProfessorDashboard } from '@/lib/api/dashboard';
import { formatDate } from '@/lib/utils';
import Link from 'next/link';

export default function ProfessorDashboardPage() {
  const { data: dashboard, isLoading } = useQuery({
    queryKey: ['professor-dashboard'],
    queryFn: getProfessorDashboard,
  });

  if (isLoading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (!dashboard) {
    return (
      <div className="text-center">
        <p className="text-muted-foreground">대시보드를 불러올 수 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* 헤더 */}
      <div>
        <h1 className="text-3xl font-bold">교수 대시보드</h1>
        <p className="text-muted-foreground">전체 코스 및 학생 현황을 확인하세요</p>
      </div>

      {/* 통계 카드 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="전체 코스"
          value={dashboard.stats.totalCourses}
          icon={BookOpen}
          description="운영 중인 코스"
        />
        <StatCard
          title="전체 학생"
          value={dashboard.stats.totalStudents}
          icon={Users}
          description="수강 중인 학생"
        />
        <StatCard
          title="과제"
          value={dashboard.stats.totalAssignments}
          icon={FileText}
          description="진행 중인 과제"
        />
        <StatCard
          title="세션"
          value={dashboard.stats.totalSessions}
          icon={Video}
          description="예정된 세션"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* 오늘 일정 */}
        <ScheduleSection items={dashboard.todaySchedule} />

        {/* 알림 */}
        <NotificationSection notifications={dashboard.notifications} />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* 채점 대기 */}
        <Card>
          <CardHeader>
            <CardTitle>채점 대기</CardTitle>
            <CardDescription>채점이 필요한 과제</CardDescription>
          </CardHeader>
          <CardContent>
            {dashboard.pendingGrading.length === 0 ? (
              <p className="text-sm text-muted-foreground">채점이 필요한 과제가 없습니다</p>
            ) : (
              <div className="space-y-4">
                {dashboard.pendingGrading.map((item) => (
                  <div key={item.id} className="flex items-start justify-between rounded-lg border p-4">
                    <div className="flex-1">
                      <h4 className="font-semibold">{item.assignmentTitle}</h4>
                      <p className="text-sm text-muted-foreground">{item.courseTitle}</p>
                      <div className="mt-2 flex items-center gap-2 text-sm">
                        <Badge variant="secondary">{item.studentCount}명 제출</Badge>
                        <span className="text-muted-foreground">
                          마감: {formatDate(item.dueDate)}
                        </span>
                      </div>
                    </div>
                    <Button size="sm" asChild>
                      <Link href={`/assignments/${item.assignmentId}/grade`}>채점하기</Link>
                    </Button>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* 위험 학생 */}
        <Card>
          <CardHeader>
            <CardTitle>위험 학생</CardTitle>
            <CardDescription>주의가 필요한 학생</CardDescription>
          </CardHeader>
          <CardContent>
            {dashboard.atRiskStudents.length === 0 ? (
              <p className="text-sm text-muted-foreground">위험 학생이 없습니다</p>
            ) : (
              <div className="space-y-4">
                {dashboard.atRiskStudents.map((student) => (
                  <div
                    key={student.id}
                    className="flex items-start justify-between rounded-lg border border-orange-200 bg-orange-50 p-4"
                  >
                    <div className="flex-1">
                      <h4 className="font-semibold">{student.studentName}</h4>
                      <p className="text-sm text-muted-foreground">{student.courseTitle}</p>
                      <p className="mt-1 text-sm text-orange-700">{student.reason}</p>
                      <div className="mt-2">
                        <Badge variant="destructive">위험도: {student.score}%</Badge>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
