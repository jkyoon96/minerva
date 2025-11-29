'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Users, TrendingUp, MessageSquare, Hand } from 'lucide-react';
import { AnalyticsSnapshot } from '@/types/analytics';
import { formatDuration } from '@/lib/utils';

interface RealtimeDashboardProps {
  snapshot: AnalyticsSnapshot;
  onViewDetails?: () => void;
}

export function RealtimeDashboard({ snapshot, onViewDetails }: RealtimeDashboardProps) {
  const { participants, engagement, participation, duration } = snapshot;

  const participationRate = (participants.active / participants.total) * 100;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold">Real-time Analytics</h2>
          <p className="text-muted-foreground">
            Session Duration: {formatDuration(duration)}
          </p>
        </div>
        {onViewDetails && (
          <button
            onClick={onViewDetails}
            className="text-sm text-primary hover:underline"
          >
            View Full Dashboard
          </button>
        )}
      </div>

      {/* Overview Cards */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Participation Rate</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{participationRate.toFixed(1)}%</div>
            <p className="text-xs text-muted-foreground">
              {participants.active}/{participants.total} active
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Overall Engagement</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{engagement.overall.toFixed(1)}</div>
            <p className="text-xs text-muted-foreground">
              High: {engagement.distribution.high} | Med: {engagement.distribution.medium}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Chat Messages</CardTitle>
            <MessageSquare className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{participation.chatMessages}</div>
            <p className="text-xs text-muted-foreground">
              {participation.questionsAsked} questions asked
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Poll Participation</CardTitle>
            <Hand className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{participation.pollsParticipated}</div>
            <p className="text-xs text-muted-foreground">
              {participation.handsRaised} hands raised
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Alerts */}
      {snapshot.alerts && snapshot.alerts.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>Active Alerts</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {snapshot.alerts.map((alert, idx) => (
              <div key={idx} className="flex items-center justify-between rounded-lg border p-3">
                <div className="flex items-center gap-3">
                  <Badge variant="destructive">{alert.type}</Badge>
                  <div>
                    <p className="font-medium">{alert.count} student(s)</p>
                    <p className="text-sm text-muted-foreground">
                      {alert.students.map((s) => s.name).join(', ')}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </CardContent>
        </Card>
      )}
    </div>
  );
}
