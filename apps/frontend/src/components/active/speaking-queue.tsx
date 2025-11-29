'use client';

import React from 'react';
import { Hand, Clock, Play, X, CheckCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ScrollArea } from '@/components/ui/scroll-area';
import { SpeakingQueueEntry, SpeakingStatus } from '@/types/active';
import { formatDistanceToNow } from 'date-fns';

interface SpeakingQueueProps {
  queue: SpeakingQueueEntry[];
  currentUserId: number;
  isHost?: boolean;
  onJoin?: (topic?: string) => void;
  onLeave?: (entryId: number) => void;
  onStart?: (entryId: number) => void;
  onEnd?: (entryId: number) => void;
}

export const SpeakingQueue: React.FC<SpeakingQueueProps> = ({
  queue,
  currentUserId,
  isHost,
  onJoin,
  onLeave,
  onStart,
  onEnd,
}) => {
  const userInQueue = queue.find((e) => e.userId === currentUserId && e.status === SpeakingStatus.WAITING);
  const currentSpeaker = queue.find((e) => e.status === SpeakingStatus.SPEAKING);
  const waitingQueue = queue.filter((e) => e.status === SpeakingStatus.WAITING);

  return (
    <div className="space-y-4">
      {/* Current Speaker */}
      {currentSpeaker && (
        <Card className="border-green-500">
          <CardHeader className="pb-3">
            <CardTitle className="text-lg flex items-center gap-2">
              <Play className="h-5 w-5 text-green-600" />
              Currently Speaking
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center justify-between">
              <div>
                <p className="font-semibold text-lg">{currentSpeaker.userName}</p>
                {currentSpeaker.topic && (
                  <p className="text-sm text-gray-600">{currentSpeaker.topic}</p>
                )}
              </div>
              <div className="text-right">
                <Badge variant="secondary" className="mb-2">
                  <Clock className="h-3 w-3 mr-1" />
                  {currentSpeaker.durationSeconds ? `${Math.floor(currentSpeaker.durationSeconds / 60)}:${(currentSpeaker.durationSeconds % 60).toString().padStart(2, '0')}` : 'In progress'}
                </Badge>
                {isHost && (
                  <Button size="sm" variant="outline" onClick={() => onEnd?.(currentSpeaker.id)}>
                    <CheckCircle className="h-4 w-4 mr-1" />
                    End Turn
                  </Button>
                )}
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Queue */}
      <Card>
        <CardHeader className="pb-3">
          <div className="flex items-center justify-between">
            <CardTitle className="text-lg flex items-center gap-2">
              <Hand className="h-5 w-5" />
              Speaking Queue ({waitingQueue.length})
            </CardTitle>
            {!userInQueue && !currentSpeaker?.userId === currentUserId && (
              <Button size="sm" onClick={() => onJoin?.()}>
                <Hand className="h-4 w-4 mr-1" />
                Join Queue
              </Button>
            )}
          </div>
        </CardHeader>
        <CardContent>
          <ScrollArea className="h-96">
            <div className="space-y-2">
              {waitingQueue.length === 0 ? (
                <p className="text-center text-gray-500 py-8">No one in queue</p>
              ) : (
                waitingQueue.map((entry, index) => (
                  <div
                    key={entry.id}
                    className={`flex items-center justify-between p-3 rounded-lg border ${
                      entry.userId === currentUserId ? 'bg-blue-50 border-blue-200' : 'bg-gray-50'
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <div className="flex items-center justify-center w-8 h-8 rounded-full bg-gray-200 font-semibold">
                        {index + 1}
                      </div>
                      <div>
                        <p className="font-medium">{entry.userName}</p>
                        {entry.topic && (
                          <p className="text-sm text-gray-600">{entry.topic}</p>
                        )}
                        <p className="text-xs text-gray-500">
                          Joined {formatDistanceToNow(new Date(entry.requestedAt), { addSuffix: true })}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      {isHost && index === 0 && (
                        <Button
                          size="sm"
                          onClick={() => onStart?.(entry.id)}
                        >
                          <Play className="h-4 w-4 mr-1" />
                          Start
                        </Button>
                      )}
                      {(isHost || entry.userId === currentUserId) && (
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => onLeave?.(entry.id)}
                        >
                          <X className="h-4 w-4" />
                        </Button>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
          </ScrollArea>

          {userInQueue && (
            <div className="mt-4 p-3 bg-blue-50 rounded-lg">
              <p className="text-sm text-blue-900">
                You are #{userInQueue.position} in line
              </p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
