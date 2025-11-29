'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { StudentReport } from '@/types/analytics';
import { TrendingUp, Award, AlertTriangle } from 'lucide-react';
import { Progress } from '@/components/ui/progress';

interface StudentReportCardProps {
  report: StudentReport;
  onViewDetails?: () => void;
}

export function StudentReportCard({ report, onViewDetails }: StudentReportCardProps) {
  const { summary, strengths, weaknesses } = report;

  const getRankBadge = () => {
    const percentage = (summary.overallRank / summary.totalStudents) * 100;
    if (percentage <= 20) return <Badge>Top 20%</Badge>;
    if (percentage <= 40) return <Badge variant="secondary">Top 40%</Badge>;
    return null;
  };

  return (
    <Card className="cursor-pointer transition-shadow hover:shadow-lg" onClick={onViewDetails}>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div>
            <CardTitle>{report.studentName}</CardTitle>
            <p className="text-sm text-muted-foreground">{report.courseName}</p>
          </div>
          {getRankBadge()}
        </div>
      </CardHeader>
      <CardContent className="space-y-6">
        {/* Summary Metrics */}
        <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
          <div>
            <p className="text-sm text-muted-foreground">Attendance</p>
            <p className="text-2xl font-bold">{summary.attendanceRate.toFixed(0)}%</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Participation</p>
            <p className="text-2xl font-bold">{summary.participationScore}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Quiz Avg</p>
            <p className="text-2xl font-bold">{summary.averageQuizScore}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Assignment Avg</p>
            <p className="text-2xl font-bold">{summary.averageAssignmentScore}</p>
          </div>
        </div>

        {/* Rank */}
        <div>
          <div className="flex items-center justify-between text-sm">
            <span className="text-muted-foreground">Class Rank</span>
            <span className="font-medium">
              {summary.overallRank} / {summary.totalStudents}
            </span>
          </div>
          <Progress
            value={((summary.totalStudents - summary.overallRank + 1) / summary.totalStudents) * 100}
            className="mt-2 h-2"
          />
        </div>

        {/* Strengths */}
        {strengths.length > 0 && (
          <div>
            <h4 className="mb-2 flex items-center gap-2 text-sm font-semibold">
              <Award className="h-4 w-4 text-green-600" />
              Top Strengths
            </h4>
            <div className="space-y-2">
              {strengths.slice(0, 3).map((strength, idx) => (
                <div key={idx} className="flex items-center justify-between text-sm">
                  <span>{strength.topic}</span>
                  <Badge variant="secondary">{strength.percentage.toFixed(0)}%</Badge>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Weaknesses */}
        {weaknesses.length > 0 && (
          <div>
            <h4 className="mb-2 flex items-center gap-2 text-sm font-semibold">
              <AlertTriangle className="h-4 w-4 text-orange-600" />
              Areas for Improvement
            </h4>
            <div className="space-y-2">
              {weaknesses.slice(0, 3).map((weakness, idx) => (
                <div key={idx} className="flex items-center justify-between text-sm">
                  <span>{weakness.topic}</span>
                  <Badge variant="outline">{weakness.percentage.toFixed(0)}%</Badge>
                </div>
              ))}
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
