'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { EngagementTrend } from '@/types/analytics';
import { format } from 'date-fns';

interface EngagementHeatmapProps {
  trends: EngagementTrend[];
}

export function EngagementHeatmap({ trends }: EngagementHeatmapProps) {
  const getColorForScore = (score: number) => {
    if (score >= 80) return 'bg-green-500';
    if (score >= 60) return 'bg-yellow-500';
    if (score >= 40) return 'bg-orange-500';
    return 'bg-red-500';
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Engagement Heatmap</CardTitle>
        <p className="text-sm text-muted-foreground">Real-time participation over time</p>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          {trends.map((trend, idx) => (
            <div key={idx} className="flex items-center gap-3">
              <span className="w-16 text-sm text-muted-foreground">
                {format(new Date(trend.timestamp), 'HH:mm')}
              </span>
              <div className="flex-1">
                <div className="flex items-center gap-1">
                  <div className="h-8 flex-1 rounded bg-muted">
                    <div
                      className={`h-full rounded transition-all ${getColorForScore(
                        trend.participationRate
                      )}`}
                      style={{ width: `${trend.participationRate}%` }}
                    />
                  </div>
                  <span className="w-12 text-right text-sm font-medium">
                    {trend.participationRate.toFixed(0)}%
                  </span>
                </div>
              </div>
              <span className="w-20 text-right text-sm text-muted-foreground">
                {trend.activeUsers} active
              </span>
            </div>
          ))}
        </div>

        <div className="mt-4 flex items-center justify-center gap-4 text-xs">
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 rounded bg-green-500" />
            <span>High (80+)</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 rounded bg-yellow-500" />
            <span>Medium (60-79)</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 rounded bg-orange-500" />
            <span>Low (40-59)</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 rounded bg-red-500" />
            <span>Very Low (&lt;40)</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
