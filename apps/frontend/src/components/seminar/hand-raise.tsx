'use client';

/**
 * Hand raise component for requesting to speak
 */

import React from 'react';
import { Hand } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface HandRaiseProps {
  isRaised: boolean;
  onToggle: () => void;
  disabled?: boolean;
  className?: string;
}

export const HandRaise: React.FC<HandRaiseProps> = ({
  isRaised,
  onToggle,
  disabled = false,
  className,
}) => {
  return (
    <Button
      variant={isRaised ? 'default' : 'secondary'}
      size="lg"
      onClick={onToggle}
      disabled={disabled}
      className={cn(
        'gap-2',
        isRaised && 'bg-yellow-500 hover:bg-yellow-600',
        className,
      )}
      title={isRaised ? 'Lower hand' : 'Raise hand'}
    >
      <Hand className="h-5 w-5" />
      {isRaised ? 'Lower Hand' : 'Raise Hand'}
    </Button>
  );
};
