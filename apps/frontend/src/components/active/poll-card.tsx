'use client';

/**
 * Poll card component for displaying poll in list
 */

import React from 'react';
import { Clock, Users, BarChart3, CheckCircle, Circle } from 'lucide-react';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Poll, PollType, PollStatus } from '@/types/active';
import { cn } from '@/lib/utils';
import { format } from 'date-fns';

interface PollCardProps {
  poll: Poll;
  userResponse?: boolean;
  onView?: () => void;
  onStart?: () => void;
  onEnd?: () => void;
  onDelete?: () => void;
  isHost?: boolean;
}

const POLL_TYPE_LABELS: Record<PollType, string> = {
  [PollType.MULTIPLE_CHOICE]: 'Multiple Choice',
  [PollType.RATING]: 'Rating',
  [PollType.WORD_CLOUD]: 'Word Cloud',
  [PollType.OPEN_ENDED]: 'Open Ended',
  [PollType.YES_NO]: 'Yes/No',
};

const STATUS_COLORS: Record<PollStatus, string> = {
  [PollStatus.DRAFT]: 'bg-gray-500',
  [PollStatus.ACTIVE]: 'bg-green-500',
  [PollStatus.ENDED]: 'bg-blue-500',
  [PollStatus.ARCHIVED]: 'bg-gray-400',
};

export const PollCard: React.FC<PollCardProps> = ({
  poll,
  userResponse,
  onView,
  onStart,
  onEnd,
  onDelete,
  isHost,
}) => {
  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-2">
              <Badge variant="outline" className="text-xs">
                {POLL_TYPE_LABELS[poll.pollType]}
              </Badge>
              <Badge className={cn('text-xs text-white', STATUS_COLORS[poll.status])}>
                {poll.status}
              </Badge>
              {userResponse && (
                <Badge variant="secondary" className="text-xs">
                  <CheckCircle className="h-3 w-3 mr-1" />
                  Responded
                </Badge>
              )}
            </div>
            <h3 className="text-lg font-semibold text-gray-900 line-clamp-2">{poll.question}</h3>
            {poll.description && (
              <p className="text-sm text-gray-600 mt-1 line-clamp-2">{poll.description}</p>
            )}
          </div>
        </div>
      </CardHeader>

      <CardContent>
        <div className="flex items-center justify-between text-sm text-gray-600 mb-4">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-1">
              <Users className="h-4 w-4" />
              <span>{poll.totalResponses} responses</span>
            </div>
            {poll.startedAt && (
              <div className="flex items-center gap-1">
                <Clock className="h-4 w-4" />
                <span>{format(new Date(poll.startedAt), 'MMM d, HH:mm')}</span>
              </div>
            )}
          </div>
          <span className="text-xs text-gray-500">by {poll.createdByName}</span>
        </div>

        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={onView} className="flex-1">
            <BarChart3 className="h-4 w-4 mr-1" />
            View Results
          </Button>

          {isHost && (
            <>
              {poll.status === PollStatus.DRAFT && (
                <Button variant="default" size="sm" onClick={onStart}>
                  Start Poll
                </Button>
              )}
              {poll.status === PollStatus.ACTIVE && (
                <Button variant="secondary" size="sm" onClick={onEnd}>
                  End Poll
                </Button>
              )}
              {poll.status === PollStatus.ENDED && (
                <Button variant="ghost" size="sm" onClick={onDelete}>
                  Archive
                </Button>
              )}
            </>
          )}

          {!isHost && poll.status === PollStatus.ACTIVE && !userResponse && (
            <Button variant="default" size="sm" onClick={onView}>
              <Circle className="h-4 w-4 mr-1" />
              Vote Now
            </Button>
          )}
        </div>
      </CardContent>
    </Card>
  );
};
