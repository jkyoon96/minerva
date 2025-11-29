'use client';

import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useAnalyticsStore } from '@/stores/analyticsStore';
import analyticsApi from '@/lib/api/analytics';
import { RealtimeDashboard } from '@/components/analytics/realtime/realtime-dashboard';
import { ParticipationChart } from '@/components/analytics/realtime/participation-chart';
import { EngagementHeatmap } from '@/components/analytics/realtime/engagement-heatmap';
import { LiveMetrics } from '@/components/analytics/realtime/live-metrics';
import { SessionTimeline } from '@/components/analytics/realtime/session-timeline';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { RefreshCw } from 'lucide-react';
import { useToast } from '@/hooks/use-toast';
import { MetricType } from '@/types/analytics';

export default function AnalyticsPage() {
  const { toast } = useToast();
  const {
    currentSnapshot,
    participantMetrics,
    engagementTrends,
    historicalSnapshots,
    setCurrentSnapshot,
    setParticipantMetrics,
    setEngagementTrends,
    setHistoricalSnapshots,
    setLoading,
    setError,
    uiState,
  } = useAnalyticsStore();

  // Mock session ID - in production, this would come from route params or context
  const sessionId = 1;

  // Fetch real-time snapshot
  const { data: snapshot, refetch: refetchSnapshot } = useQuery({
    queryKey: ['analytics', 'realtime', sessionId],
    queryFn: () => analyticsApi.realtime.getSnapshot({ sessionId }),
    refetchInterval: 30000, // Refresh every 30 seconds
    onSuccess: (data) => {
      setCurrentSnapshot(data);
      setLoading(false);
    },
    onError: (error: any) => {
      setError(error.message);
      setLoading(false);
      toast({
        title: 'Error',
        description: 'Failed to load real-time analytics',
        variant: 'destructive',
      });
    },
  });

  // Fetch participant metrics
  useQuery({
    queryKey: ['analytics', 'participants', sessionId],
    queryFn: () => analyticsApi.realtime.getParticipantMetrics(sessionId),
    refetchInterval: 30000,
    onSuccess: (data) => {
      setParticipantMetrics(data);
    },
  });

  // Fetch engagement trends
  useQuery({
    queryKey: ['analytics', 'trends', sessionId],
    queryFn: () => analyticsApi.realtime.getTrends({ courseId: 1 }), // Mock course ID
    onSuccess: (data) => {
      setEngagementTrends(data);
    },
  });

  // Fetch historical snapshots
  useQuery({
    queryKey: ['analytics', 'snapshots', sessionId],
    queryFn: () => analyticsApi.realtime.getSnapshots({ courseId: 1 }),
    onSuccess: (data) => {
      setHistoricalSnapshots(data);
    },
  });

  // Generate live metrics from snapshot
  const liveMetrics = currentSnapshot
    ? [
        {
          metricType: MetricType.PARTICIPATION,
          value: currentSnapshot.participants.active,
          label: 'Active Participants',
          trend: 'STABLE' as const,
          percentageChange: 0,
          comparison: {
            type: 'CLASS_AVERAGE' as const,
            value: currentSnapshot.participants.total,
          },
        },
        {
          metricType: MetricType.ENGAGEMENT,
          value: currentSnapshot.engagement.overall,
          label: 'Engagement Score',
          trend: 'UP' as const,
          percentageChange: 5.2,
        },
        {
          metricType: MetricType.INTERACTION,
          value: currentSnapshot.participation.chatMessages,
          label: 'Messages',
          trend: 'UP' as const,
          percentageChange: 12.5,
        },
      ]
    : [];

  const handleRefresh = () => {
    refetchSnapshot();
    toast({
      title: 'Refreshing',
      description: 'Updating real-time analytics...',
    });
  };

  if (!currentSnapshot) {
    return (
      <div className="flex h-[400px] items-center justify-center">
        <div className="text-center">
          <div className="mb-4 h-12 w-12 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
          <p className="text-muted-foreground">Loading analytics...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Real-time Analytics Dashboard</h1>
          <p className="text-muted-foreground">Monitor live session engagement and participation</p>
        </div>
        <Button onClick={handleRefresh} variant="outline" size="sm">
          <RefreshCw className="mr-2 h-4 w-4" />
          Refresh
        </Button>
      </div>

      {/* Live Metrics */}
      <LiveMetrics metrics={liveMetrics} />

      {/* Main Dashboard */}
      <RealtimeDashboard snapshot={currentSnapshot} />

      {/* Charts Grid */}
      <div className="grid gap-6 md:grid-cols-2">
        <ParticipationChart metrics={participantMetrics} sortBy="talkTime" />
        <EngagementHeatmap trends={engagementTrends} />
      </div>

      {/* Session Timeline */}
      {historicalSnapshots.length > 0 && (
        <SessionTimeline snapshots={historicalSnapshots} currentTime={currentSnapshot.currentTime} />
      )}
    </div>
  );
}
