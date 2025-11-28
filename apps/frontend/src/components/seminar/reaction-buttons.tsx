'use client';

/**
 * Reaction buttons component for quick reactions
 */

import React from 'react';
import { Button } from '@/components/ui/button';
import { ReactionType } from '@/types/seminar';
import { cn } from '@/lib/utils';

interface ReactionButtonsProps {
  onReact: (type: ReactionType) => void;
  className?: string;
}

const reactions = [
  { type: ReactionType.THUMBS_UP, emoji: '👍', label: 'Thumbs up' },
  { type: ReactionType.CLAP, emoji: '👏', label: 'Clap' },
  { type: ReactionType.HEART, emoji: '❤️', label: 'Heart' },
  { type: ReactionType.LAUGH, emoji: '😂', label: 'Laugh' },
  { type: ReactionType.SURPRISED, emoji: '😮', label: 'Surprised' },
];

export const ReactionButtons: React.FC<ReactionButtonsProps> = ({
  onReact,
  className,
}) => {
  return (
    <div className={cn('flex gap-2', className)}>
      {reactions.map((reaction) => (
        <Button
          key={reaction.type}
          variant="ghost"
          size="lg"
          onClick={() => onReact(reaction.type)}
          className="h-12 w-12 text-2xl hover:scale-110 transition-transform"
          title={reaction.label}
        >
          {reaction.emoji}
        </Button>
      ))}
    </div>
  );
};
