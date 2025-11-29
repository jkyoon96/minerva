'use client';

import { use } from 'react';
import { useQuery } from '@tanstack/react-query';
import analyticsApi from '@/lib/api/analytics';
import { LearningChart } from '@/components/analytics/reports/learning-chart';
import { ExportButton } from '@/components/analytics/reports/export-button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ArrowLeft, Users, CheckCircle, TrendingUp, AlertTriangle } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

export default function CourseReportDetailPage({
  params,
}: {
  params: Promise<{ courseId: string }>;
}) {
  const { courseId } = use(params);
  const router = useRouter();

  const { data: report, isLoading } = useQuery({
    queryKey: ['analytics', 'reports', 'course', courseId],
    queryFn: () =>
      analyticsApi.reports.getCourseReport({
        courseId: parseInt(courseId),
      }),
  });

  const handleExport = async (format: 'PDF' | 'EXCEL') => {
    if (!report) return;
    await analyticsApi.reports.exportReport({
      reportId: report.id,
      format,
    });
  };

  if (isLoading) {
    return (
      <div className="flex h-[400px] items-center justify-center">
        <div className="text-center">
          <div className="mb-4 h-12 w-12 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
          <p className="text-muted-foreground">Loading report...</p>
        </div>
      </div>
    );
  }

  if (!report) {
    return (
      <div className="flex h-[400px] items-center justify-center">
        <p className="text-muted-foreground">Report not found</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Link href="/analytics/reports">
            <ArrowLeft className="h-6 w-6 cursor-pointer text-muted-foreground hover:text-foreground" />
          </Link>
          <div>
            <h1 className="text-3xl font-bold">{report.courseName}</h1>
            <p className="text-muted-foreground">
              Course Analytics Report • {report.period.from} - {report.period.to}
            </p>
          </div>
        </div>
        <ExportButton reportId={report.id} onExport={handleExport} />
      </div>

      {/* Overview */}
      <Card>
        <CardHeader>
          <CardTitle>Course Overview</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-6 md:grid-cols-4">
            <div className="flex items-center gap-3">
              <Users className="h-8 w-8 text-muted-foreground" />
              <div>
                <div className="text-2xl font-bold">{report.overview.activeStudents}</div>
                <div className="text-sm text-muted-foreground">
                  Active / {report.overview.totalStudents} Total
                </div>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <CheckCircle className="h-8 w-8 text-muted-foreground" />
              <div>
                <div className="text-2xl font-bold">{report.overview.completionRate.toFixed(0)}%</div>
                <div className="text-sm text-muted-foreground">Completion Rate</div>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <TrendingUp className="h-8 w-8 text-muted-foreground" />
              <div>
                <div className="text-2xl font-bold">
                  {report.overview.averageParticipationRate.toFixed(0)}%
                </div>
                <div className="text-sm text-muted-foreground">Participation</div>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <AlertTriangle className="h-8 w-8 text-muted-foreground" />
              <div>
                <div className="text-2xl font-bold">
                  {report.studentClassification.find((c) => c.category === 'AT_RISK')?.count || 0}
                </div>
                <div className="text-sm text-muted-foreground">At-Risk Students</div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Weekly Trend */}
      <LearningChart
        data={report.weeklyTrend.map((w) => ({
          week: w.week,
          participationScore: w.participationRate,
          quizScore: w.averageScore,
        }))}
        title="Weekly Participation Trend"
      />

      {/* Grade Distribution */}
      <Card>
        <CardHeader>
          <CardTitle>Grade Distribution</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="grid grid-cols-5 gap-4">
              {report.gradeDistribution.map((dist, idx) => (
                <div key={idx} className="rounded-lg border p-4 text-center">
                  <div className="text-3xl font-bold">{dist.count}</div>
                  <div className="mt-1 text-sm text-muted-foreground">{dist.grade}</div>
                  <div className="mt-2 text-xs text-muted-foreground">
                    {dist.percentage.toFixed(0)}%
                  </div>
                </div>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Question Analysis */}
      {report.questionAnalysis.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>Difficult Topics (Correct Rate &lt; 60%)</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {report.questionAnalysis.map((qa, idx) => (
                <div key={idx} className="flex items-center justify-between rounded-lg border p-3">
                  <div className="flex-1">
                    <div className="font-medium">
                      #{qa.rank} {qa.topic}
                    </div>
                    <div className="text-sm text-muted-foreground">{qa.relatedSession}</div>
                  </div>
                  <Badge variant={qa.correctRate < 50 ? 'destructive' : 'secondary'}>
                    {qa.correctRate.toFixed(0)}% correct
                  </Badge>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Student Classification */}
      <Card>
        <CardHeader>
          <CardTitle>Student Performance Groups</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-4">
            {report.studentClassification.map((category, idx) => {
              const colors = {
                EXCELLENT: 'bg-green-100 text-green-700 border-green-200',
                GOOD: 'bg-blue-100 text-blue-700 border-blue-200',
                AVERAGE: 'bg-gray-100 text-gray-700 border-gray-200',
                AT_RISK: 'bg-red-100 text-red-700 border-red-200',
              };

              return (
                <div
                  key={idx}
                  className={`cursor-pointer rounded-lg border-2 p-4 transition-shadow hover:shadow-md ${
                    colors[category.category]
                  }`}
                  onClick={() =>
                    category.category === 'AT_RISK' && router.push('/analytics/risks')
                  }
                >
                  <div className="text-2xl font-bold">{category.count}</div>
                  <div className="mt-1 text-sm">
                    {category.category.replace('_', ' ')} ({category.percentage.toFixed(0)}%)
                  </div>
                  <div className="mt-2 text-xs">
                    {category.students.slice(0, 3).map((s) => s.name).join(', ')}
                    {category.students.length > 3 && ` +${category.students.length - 3} more`}
                  </div>
                </div>
              );
            })}
          </div>
        </CardContent>
      </Card>

      {/* Correlation */}
      <Card>
        <CardHeader>
          <CardTitle>Participation vs. Grade Correlation</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center">
            <div className="text-4xl font-bold text-primary">
              r = {report.correlation.participationVsGrade.toFixed(2)}
            </div>
            <p className="mt-2 text-sm text-muted-foreground">{report.correlation.interpretation}</p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
