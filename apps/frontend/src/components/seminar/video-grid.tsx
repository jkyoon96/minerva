'use client';

/**
 * Video grid component for displaying multiple participant videos
 */

import React, { useMemo } from 'react';
import { VideoTile } from './video-tile';
import { Participant } from '@/types/seminar';
import { cn } from '@/lib/utils';

interface VideoGridProps {
  participants: Participant[];
  streams: Map<number, MediaStream>;
  currentUserId?: number;
  className?: string;
}

export const VideoGrid: React.FC<VideoGridProps> = ({
  participants,
  streams,
  currentUserId,
  className,
}) => {
  // Calculate grid layout based on number of participants
  const gridClass = useMemo(() => {
    const count = participants.length;
    if (count === 1) return 'grid-cols-1';
    if (count === 2) return 'grid-cols-2';
    if (count <= 4) return 'grid-cols-2 md:grid-cols-2';
    if (count <= 6) return 'grid-cols-2 md:grid-cols-3';
    if (count <= 9) return 'grid-cols-2 md:grid-cols-3 lg:grid-cols-3';
    if (count <= 12) return 'grid-cols-2 md:grid-cols-3 lg:grid-cols-4';
    return 'grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5';
  }, [participants.length]);

  return (
    <div
      className={cn(
        'grid gap-4 p-4',
        gridClass,
        className,
      )}
    >
      {participants.map((participant) => {
        const stream = streams.get(participant.userId) || null;
        const isLocal = participant.userId === currentUserId;

        return (
          <VideoTile
            key={participant.id}
            participant={participant}
            stream={stream}
            isLocal={isLocal}
          />
        );
      })}
    </div>
  );
};
