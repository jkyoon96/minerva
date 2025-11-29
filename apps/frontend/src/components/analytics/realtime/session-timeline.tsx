'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { AnalyticsSnapshot } from '@/types/analytics';
import { format } from 'date-fns';
import { Activity } from 'lucide-react';

interface SessionTimelineProps {
  snapshots: AnalyticsSnapshot[];
  currentTime?: string;
}

export function SessionTimeline({ snapshots, currentTime }: SessionTimelineProps) {
  const sortedSnapshots = [...snapshots].sort(
    (a, b) => new Date(a.currentTime).getTime() - new Date(b.currentTime).getTime()
  );

  const getEngagementColor = (score: number) => {
    if (score >= 80) return 'bg-green-500';
    if (score >= 60) return 'bg-yellow-500';
    if (score >= 40) return 'bg-orange-500';
    return 'bg-red-500';
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Activity className="h-5 w-5" />
          Session Timeline
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="relative space-y-4">
          {/* Timeline line */}
          <div className="absolute left-4 top-0 h-full w-0.5 bg-border" />

          {sortedSnapshots.map((snapshot, idx) => {
            const isCurrent = currentTime
              ? snapshot.currentTime === currentTime
              : idx === sortedSnapshots.length - 1;

            return (
              <div key={idx} className="relative flex gap-4">
                {/* Timeline dot */}
                <div className="relative z-10">
                  <div
                    className={`h-8 w-8 rounded-full border-4 border-background ${getEngagementColor(
                      snapshot.engagement.overall
                    )} ${isCurrent ? 'ring-2 ring-primary ring-offset-2' : ''}`}
                  />
                </div>

                {/* Content */}
                <div className="flex-1 pb-4">
                  <div className="flex items-center justify-between">
                    <p className="font-medium">
                      {format(new Date(snapshot.currentTime), 'HH:mm:ss')}
                    </p>
                    {isCurrent && (
                      <span className="rounded-full bg-primary px-2 py-1 text-xs text-primary-foreground">
                        Current
                      </span>
                    )}
                  </div>
                  <div className="mt-1 text-sm text-muted-foreground">
                    <p>
                      Engagement: {snapshot.engagement.overall.toFixed(1)} | Active:{' '}
                      {snapshot.participants.active}/{snapshot.participants.total}
                    </p>
                    {snapshot.alerts && snapshot.alerts.length > 0 && (
                      <p className="text-destructive">
                        {snapshot.alerts.length} alert{snapshot.alerts.length > 1 ? 's' : ''}
                      </p>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </CardContent>
    </Card>
  );
}
