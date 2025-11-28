/**
 * 학생 대시보드 페이지
 * 수강 코스 통계, 과제, 성적, 참여도 표시
 */

'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import { BookOpen, FileCheck, TrendingUp } from 'lucide-react';
import { StatCard } from '@/components/ui/stat-card';
import { ScheduleSection } from '@/components/dashboard/schedule-section';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { getStudentDashboard } from '@/lib/api/dashboard';
import { formatDate, formatDateTime } from '@/lib/utils';
import Link from 'next/link';

export default function StudentDashboardPage() {
  const { data: dashboard, isLoading } = useQuery({
    queryKey: ['student-dashboard'],
    queryFn: getStudentDashboard,
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
        <h1 className="text-3xl font-bold">학생 대시보드</h1>
        <p className="text-muted-foreground">나의 학습 현황을 확인하세요</p>
      </div>

      {/* 통계 카드 */}
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard
          title="수강 중인 코스"
          value={dashboard.stats.enrolledCourses}
          icon={BookOpen}
          description="이번 학기"
        />
        <StatCard
          title="완료한 과제"
          value={dashboard.stats.completedAssignments}
          icon={FileCheck}
          description="제출 완료"
        />
        <StatCard
          title="평균 점수"
          value={`${dashboard.stats.averageGrade}점`}
          icon={TrendingUp}
          description="전체 과제"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* 오늘 일정 */}
        <ScheduleSection items={dashboard.todaySchedule} />

        {/* 마감 임박 과제 */}
        <Card>
          <CardHeader>
            <CardTitle>마감 임박 과제</CardTitle>
            <CardDescription>제출이 필요한 과제</CardDescription>
          </CardHeader>
          <CardContent>
            {dashboard.upcomingAssignments.length === 0 ? (
              <p className="text-sm text-muted-foreground">마감 임박 과제가 없습니다</p>
            ) : (
              <div className="space-y-4">
                {dashboard.upcomingAssignments.map((assignment) => (
                  <div
                    key={assignment.id}
                    className="flex items-start justify-between rounded-lg border p-4"
                  >
                    <div className="flex-1">
                      <h4 className="font-semibold">{assignment.title}</h4>
                      <p className="text-sm text-muted-foreground">{assignment.courseTitle}</p>
                      <div className="mt-2 flex items-center gap-2 text-sm">
                        {assignment.submitted ? (
                          <Badge variant="default">제출 완료</Badge>
                        ) : (
                          <Badge variant="destructive">미제출</Badge>
                        )}
                        <span className="text-muted-foreground">
                          마감: {formatDate(assignment.dueDate)}
                        </span>
                        {assignment.grade !== undefined && (
                          <Badge variant="secondary">{assignment.grade}점</Badge>
                        )}
                      </div>
                    </div>
                    {!assignment.submitted && (
                      <Button size="sm" asChild>
                        <Link href={`/assignments/${assignment.id}`}>제출하기</Link>
                      </Button>
                    )}
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* 참여도 */}
        <Card>
          <CardHeader>
            <CardTitle>참여도</CardTitle>
            <CardDescription>코스별 출석 및 참여율</CardDescription>
          </CardHeader>
          <CardContent>
            {dashboard.participation.length === 0 ? (
              <p className="text-sm text-muted-foreground">참여도 정보가 없습니다</p>
            ) : (
              <div className="space-y-6">
                {dashboard.participation.map((item) => (
                  <div key={item.courseId} className="space-y-2">
                    <div className="flex items-center justify-between">
                      <h4 className="font-semibold">{item.courseTitle}</h4>
                      <Badge variant="outline">{item.attendanceRate}% 출석</Badge>
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center justify-between text-sm">
                        <span className="text-muted-foreground">출석률</span>
                        <span>{item.attendanceRate}%</span>
                      </div>
                      <Progress value={item.attendanceRate} />
                    </div>
                    <div className="space-y-1">
                      <div className="flex items-center justify-between text-sm">
                        <span className="text-muted-foreground">참여도</span>
                        <span>{item.participationScore}점</span>
                      </div>
                      <Progress value={(item.participationScore / 100) * 100} />
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* 최근 성적 */}
        <Card>
          <CardHeader>
            <CardTitle>최근 성적</CardTitle>
            <CardDescription>최근 채점된 과제</CardDescription>
          </CardHeader>
          <CardContent>
            {dashboard.recentGrades.length === 0 ? (
              <p className="text-sm text-muted-foreground">최근 성적이 없습니다</p>
            ) : (
              <div className="space-y-4">
                {dashboard.recentGrades.map((grade) => (
                  <div key={grade.id} className="flex items-start justify-between rounded-lg border p-4">
                    <div className="flex-1">
                      <h4 className="font-semibold">{grade.assignmentTitle}</h4>
                      <p className="text-sm text-muted-foreground">{grade.courseTitle}</p>
                      <p className="mt-1 text-xs text-muted-foreground">
                        {formatDateTime(grade.gradedAt)}
                      </p>
                    </div>
                    <div className="text-right">
                      <div className="text-2xl font-bold">
                        {grade.grade}
                        <span className="text-sm text-muted-foreground">/{grade.maxGrade}</span>
                      </div>
                      <Badge variant={grade.grade >= grade.maxGrade * 0.8 ? 'default' : 'secondary'}>
                        {((grade.grade / grade.maxGrade) * 100).toFixed(0)}%
                      </Badge>
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
