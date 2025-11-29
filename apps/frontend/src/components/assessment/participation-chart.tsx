'use client';

import React from 'react';
import { BarChart3 } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';

export const ParticipationChart = ({ eventSummary }: any) => (
  <Card>
    <CardHeader>
      <CardTitle className="flex items-center gap-2">
        <BarChart3 className="h-5 w-5" />
        Score Breakdown
      </CardTitle>
    </CardHeader>
    <CardContent className="space-y-4">
      {eventSummary.map((event: any) => (
        <div key={event.eventType}>
          <div className="flex justify-between text-sm mb-2">
            <span className="font-medium">{event.eventType.replace(/_/g, ' ')}</span>
            <span>{event.points} pts ({event.percentage.toFixed(1)}%)</span>
          </div>
          <Progress value={event.percentage} className="h-2" />
        </div>
      ))}
    </CardContent>
  </Card>
);
