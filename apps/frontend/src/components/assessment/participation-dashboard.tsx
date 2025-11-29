'use client';

import React from 'react';
import { TrendingUp, Award, Calendar, Target } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { Badge } from '@/components/ui/badge';
import { ParticipationDashboard } from '@/types/assessment';

interface ParticipationDashboardProps {
  dashboard: ParticipationDashboard;
  className?: string;
}

export const ParticipationDashboardComponent: React.FC<ParticipationDashboardProps> = ({ dashboard }) => {
  const percentage = dashboard.percentage;

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Current Score</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{dashboard.currentScore}</div>
            <p className="text-xs text-gray-500">out of {dashboard.maxScore}</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Percentage</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-blue-600">{percentage.toFixed(1)}%</div>
            <Progress value={percentage} className="h-2 mt-2" />
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Class Rank</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-purple-600">{dashboard.rank}</div>
            <p className="text-xs text-gray-500">of {dashboard.totalStudents} students</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">Total Events</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">{dashboard.recentEvents.length}</div>
            <p className="text-xs text-gray-500">activities logged</p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Event Summary</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {dashboard.eventSummary.map((event) => (
            <div key={event.eventType} className="flex items-center justify-between p-3 border rounded-lg">
              <div className="flex-1">
                <p className="font-medium">{event.eventType.replace(/_/g, ' ')}</p>
                <p className="text-sm text-gray-600">{event.count} events</p>
              </div>
              <div className="text-right">
                <p className="text-lg font-semibold">{event.points} pts</p>
              </div>
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  );
};
