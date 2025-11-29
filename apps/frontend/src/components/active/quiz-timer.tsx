'use client';

import React, { useEffect } from 'react';
import { Clock, AlertCircle } from 'lucide-react';
import { Progress } from '@/components/ui/progress';
import { useActiveStore } from '@/stores/activeStore';

interface QuizTimerProps {
  totalSeconds: number;
  onTimeUp?: () => void;
}

export const QuizTimer: React.FC<QuizTimerProps> = ({ totalSeconds, onTimeUp }) => {
  const { quizUIState, decrementTimer } = useActiveStore();
  const timeRemaining = quizUIState.timeRemaining ?? totalSeconds;

  useEffect(() => {
    if (timeRemaining <= 0) {
      onTimeUp?.();
      return;
    }

    const interval = setInterval(() => {
      decrementTimer();
    }, 1000);

    return () => clearInterval(interval);
  }, [timeRemaining, decrementTimer, onTimeUp]);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const percentage = (timeRemaining / totalSeconds) * 100;
  const isWarning = timeRemaining <= 60;

  return (
    <div className={`p-4 rounded-lg border-2 ${isWarning ? 'border-red-500 bg-red-50' : 'border-gray-200'}`}>
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2">
          <Clock className={`h-5 w-5 ${isWarning ? 'text-red-600' : 'text-gray-600'}`} />
          <span className={`text-2xl font-bold ${isWarning ? 'text-red-600' : 'text-gray-900'}`}>
            {formatTime(timeRemaining)}
          </span>
        </div>
        {isWarning && (
          <div className="flex items-center gap-1 text-red-600 text-sm">
            <AlertCircle className="h-4 w-4" />
            <span>Time running out!</span>
          </div>
        )}
      </div>
      <Progress value={percentage} className={`h-2 ${isWarning ? 'bg-red-200' : ''}`} />
    </div>
  );
};
