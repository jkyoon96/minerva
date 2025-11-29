'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { CourseReport } from '@/types/analytics';
import { Users, CheckCircle, TrendingUp, AlertTriangle } from 'lucide-react';

interface CourseReportCardProps {
  report: CourseReport;
  onViewDetails?: () => void;
}

export function CourseReportCard({ report, onViewDetails }: CourseReportCardProps) {
  const { overview, gradeDistribution, studentClassification } = report;

  const atRiskCount =
    studentClassification.find((c) => c.category === 'AT_RISK')?.count || 0;

  return (
    <Card className="cursor-pointer transition-shadow hover:shadow-lg" onClick={onViewDetails}>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div>
            <CardTitle>{report.courseName}</CardTitle>
            <p className="text-sm text-muted-foreground">
              {report.period.from} - {report.period.to}
            </p>
          </div>
          <Badge variant={atRiskCount > 0 ? 'destructive' : 'secondary'}>
            {overview.totalStudents} Students
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-6">
        {/* Overview Metrics */}
        <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
          <div className="flex items-center gap-2">
            <Users className="h-4 w-4 text-muted-foreground" />
            <div>
              <p className="text-2xl font-bold">{overview.activeStudents}</p>
              <p className="text-xs text-muted-foreground">Active</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <CheckCircle className="h-4 w-4 text-muted-foreground" />
            <div>
              <p className="text-2xl font-bold">{overview.completionRate.toFixed(0)}%</p>
              <p className="text-xs text-muted-foreground">Completion</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
            <div>
              <p className="text-2xl font-bold">{overview.averageParticipationRate.toFixed(0)}%</p>
              <p className="text-xs text-muted-foreground">Participation</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <AlertTriangle className="h-4 w-4 text-muted-foreground" />
            <div>
              <p className="text-2xl font-bold">{atRiskCount}</p>
              <p className="text-xs text-muted-foreground">At Risk</p>
            </div>
          </div>
        </div>

        {/* Sessions Progress */}
        <div>
          <div className="mb-2 flex items-center justify-between text-sm">
            <span className="text-muted-foreground">Sessions Completed</span>
            <span className="font-medium">
              {overview.sessionsCompleted} / {overview.totalSessions}
            </span>
          </div>
          <div className="h-2 overflow-hidden rounded-full bg-muted">
            <div
              className="h-full bg-primary transition-all"
              style={{
                width: `${(overview.sessionsCompleted / overview.totalSessions) * 100}%`,
              }}
            />
          </div>
        </div>

        {/* Grade Distribution */}
        <div>
          <h4 className="mb-3 text-sm font-semibold">Grade Distribution</h4>
          <div className="grid grid-cols-5 gap-2">
            {gradeDistribution.map((dist, idx) => (
              <div key={idx} className="text-center">
                <div className="text-xl font-bold">{dist.count}</div>
                <div className="text-xs text-muted-foreground">{dist.grade}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Student Classification */}
        <div className="grid grid-cols-2 gap-3">
          {studentClassification.map((category, idx) => {
            const colors = {
              EXCELLENT: 'bg-green-100 text-green-700 border-green-200',
              GOOD: 'bg-blue-100 text-blue-700 border-blue-200',
              AVERAGE: 'bg-gray-100 text-gray-700 border-gray-200',
              AT_RISK: 'bg-red-100 text-red-700 border-red-200',
            };

            return (
              <div
                key={idx}
                className={`rounded-lg border p-3 ${colors[category.category]}`}
              >
                <div className="text-lg font-bold">{category.count}</div>
                <div className="text-xs">
                  {category.category.replace('_', ' ')} ({category.percentage.toFixed(0)}%)
                </div>
              </div>
            );
          })}
        </div>
      </CardContent>
    </Card>
  );
}
