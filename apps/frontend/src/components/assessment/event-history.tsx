'use client';

import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Clock } from 'lucide-react';
import { ParticipationEvent } from '@/types/assessment';
import { format } from 'date-fns';

interface EventHistoryProps {
  events: ParticipationEvent[];
}

export const EventHistory: React.FC<EventHistoryProps> = ({ events }) => (
  <Card>
    <CardHeader>
      <CardTitle>Recent Activity</CardTitle>
    </CardHeader>
    <CardContent className="space-y-3">
      {events.map((event) => (
        <div key={event.id} className="flex items-center justify-between p-3 border rounded-lg">
          <div className="flex-1">
            <p className="font-medium text-sm">{event.eventType.replace(/_/g, ' ')}</p>
            <p className="text-xs text-gray-500 flex items-center gap-1">
              <Clock className="h-3 w-3" />
              {format(new Date(event.timestamp), 'MMM d, HH:mm')}
            </p>
          </div>
          <Badge variant="outline">+{event.points} pts</Badge>
        </div>
      ))}
    </CardContent>
  </Card>
);
