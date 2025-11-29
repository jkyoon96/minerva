'use client';

import { use } from 'react';
import { useQuery } from '@tanstack/react-query';
import analyticsApi from '@/lib/api/analytics';
import { LearningChart } from '@/components/analytics/reports/learning-chart';
import { MetricComparison } from '@/components/analytics/reports/metric-comparison';
import { ExportButton } from '@/components/analytics/reports/export-button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ArrowLeft, Award, AlertTriangle, BookOpen } from 'lucide-react';
import Link from 'next/link';
import { useToast } from '@/hooks/use-toast';

export default function StudentReportDetailPage({
  params,
}: {
  params: Promise<{ studentId: string }>;
}) {
  const { studentId } = use(params);
  const { toast } = useToast();

  const { data: report, isLoading } = useQuery({
    queryKey: ['analytics', 'reports', 'student', studentId],
    queryFn: () =>
      analyticsApi.reports.getStudentReport({
        studentId: parseInt(studentId),
        courseId: 1, // Mock course ID
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

  const comparisonMetrics = [
    {
      label: 'Attendance Rate',
      yourValue: report.summary.attendanceRate,
      classAverage: 90,
      maxValue: 100,
    },
    {
      label: 'Participation Score',
      yourValue: report.summary.participationScore,
      classAverage: 75,
      maxValue: 100,
    },
    {
      label: 'Quiz Average',
      yourValue: report.summary.averageQuizScore,
      classAverage: 78,
      maxValue: 100,
    },
    {
      label: 'Assignment Average',
      yourValue: report.summary.averageAssignmentScore,
      classAverage: 82,
      maxValue: 100,
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Link href="/analytics/reports">
            <ArrowLeft className="h-6 w-6 cursor-pointer text-muted-foreground hover:text-foreground" />
          </Link>
          <div>
            <h1 className="text-3xl font-bold">{report.studentName}</h1>
            <p className="text-muted-foreground">
              {report.courseName} • {report.period.from} - {report.period.to}
            </p>
          </div>
        </div>
        <ExportButton reportId={report.id} onExport={handleExport} />
      </div>

      {/* Summary */}
      <Card>
        <CardHeader>
          <CardTitle>Semester Summary</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-6 md:grid-cols-4">
            <div className="text-center">
              <div className="text-3xl font-bold">{report.summary.attendanceRate.toFixed(0)}%</div>
              <div className="text-sm text-muted-foreground">Attendance Rate</div>
              <Badge className="mt-2" variant="secondary">
                Top {Math.round((report.summary.overallRank / report.summary.totalStudents) * 100)}%
              </Badge>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold">{report.summary.participationScore}</div>
              <div className="text-sm text-muted-foreground">Participation Score</div>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold">{report.summary.averageQuizScore}</div>
              <div className="text-sm text-muted-foreground">Quiz Average</div>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold">{report.summary.averageAssignmentScore}</div>
              <div className="text-sm text-muted-foreground">Assignment Average</div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Progress Chart */}
      <LearningChart
        data={report.weeklyProgress}
        title="Weekly Progress"
        description="Your performance trend over the semester"
      />

      {/* Performance Comparison */}
      <MetricComparison metrics={comparisonMetrics} />

      {/* Strengths & Weaknesses */}
      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Award className="h-5 w-5 text-green-600" />
              Strengths
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {report.strengths.map((strength, idx) => (
              <div key={idx} className="flex items-center justify-between">
                <span className="font-medium">{strength.topic}</span>
                <Badge variant="secondary">{strength.percentage.toFixed(0)}%</Badge>
              </div>
            ))}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <AlertTriangle className="h-5 w-5 text-orange-600" />
              Areas for Improvement
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {report.weaknesses.map((weakness, idx) => (
              <div key={idx}>
                <div className="flex items-center justify-between">
                  <span className="font-medium">{weakness.topic}</span>
                  <Badge variant="outline">{weakness.percentage.toFixed(0)}%</Badge>
                </div>
                <p className="mt-1 text-xs text-muted-foreground">
                  Class average: {weakness.classAverage.toFixed(0)}%
                </p>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>

      {/* Recommendations */}
      {report.recommendations.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <BookOpen className="h-5 w-5" />
              Personalized Recommendations
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {report.recommendations.map((rec, idx) => (
              <div key={idx} className="rounded-lg border p-4">
                <h4 className="font-semibold">{rec.category}</h4>
                <p className="mt-2 text-sm text-muted-foreground">{rec.suggestion}</p>
                {rec.resources.length > 0 && (
                  <div className="mt-3 space-y-2">
                    {rec.resources.map((resource, ridx) => (
                      <a
                        key={ridx}
                        href={resource.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="block text-sm text-primary hover:underline"
                      >
                        {resource.type === 'VIDEO' && '▶ '}
                        {resource.title}
                      </a>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </CardContent>
        </Card>
      )}
    </div>
  );
}
