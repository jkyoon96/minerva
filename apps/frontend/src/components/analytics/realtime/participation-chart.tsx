'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ParticipantMetric, RiskLevel } from '@/types/analytics';
import { Progress } from '@/components/ui/progress';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';

interface ParticipationChartProps {
  metrics: ParticipantMetric[];
  sortBy?: 'talkTime' | 'engagement' | 'name';
  onStudentClick?: (studentId: number) => void;
}

export function ParticipationChart({
  metrics,
  sortBy = 'talkTime',
  onStudentClick,
}: ParticipationChartProps) {
  const sortedMetrics = [...metrics].sort((a, b) => {
    if (sortBy === 'name') return a.studentName.localeCompare(b.studentName);
    if (sortBy === 'engagement') return b.engagementScore - a.engagementScore;
    return b.talkTime - a.talkTime;
  });

  const maxTalkTime = Math.max(...metrics.map((m) => m.talkTime));
  const avgTalkTime = metrics.reduce((sum, m) => sum + m.talkTime, 0) / metrics.length;

  const getRiskBadge = (riskLevel: RiskLevel) => {
    const variants = {
      [RiskLevel.NONE]: { variant: 'outline' as const, label: '' },
      [RiskLevel.LOW]: { variant: 'outline' as const, label: '' },
      [RiskLevel.MEDIUM]: { variant: 'secondary' as const, label: 'Watch' },
      [RiskLevel.HIGH]: { variant: 'destructive' as const, label: 'Alert' },
      [RiskLevel.CRITICAL]: { variant: 'destructive' as const, label: 'Critical' },
    };

    const config = variants[riskLevel];
    if (!config.label) return null;

    return <Badge variant={config.variant}>{config.label}</Badge>;
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Student Talk Time</CardTitle>
        <p className="text-sm text-muted-foreground">
          Average: {formatTime(Math.floor(avgTalkTime))}
        </p>
      </CardHeader>
      <CardContent className="space-y-3">
        {sortedMetrics.map((metric) => {
          const percentage = (metric.talkTime / maxTalkTime) * 100;
          const isAboveAverage = metric.talkTime > avgTalkTime;

          return (
            <div
              key={metric.studentId}
              className={cn(
                'space-y-2 rounded-lg p-3 transition-colors',
                onStudentClick && 'cursor-pointer hover:bg-muted',
                !metric.isActive && 'opacity-60'
              )}
              onClick={() => onStudentClick?.(metric.studentId)}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="font-medium">{metric.studentName}</span>
                  {isAboveAverage && <Badge variant="secondary">Top</Badge>}
                  {getRiskBadge(metric.riskLevel)}
                </div>
                <span className="text-sm font-medium">{formatTime(metric.talkTime)}</span>
              </div>
              <Progress value={percentage} className="h-2" />
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
}
