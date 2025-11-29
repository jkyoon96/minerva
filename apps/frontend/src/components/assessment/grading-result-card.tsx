'use client';

/**
 * Grading result card component for displaying grade summary
 */

import React from 'react';
import { CheckCircle, XCircle, AlertCircle } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { AutoGradingResult } from '@/types/assessment';
import { cn } from '@/lib/utils';
import { format } from 'date-fns';

interface GradingResultCardProps {
  result: AutoGradingResult;
  showDetails?: boolean;
  className?: string;
}

export const GradingResultCard: React.FC<GradingResultCardProps> = ({
  result,
  showDetails = true,
  className,
}) => {
  const { submission, statistics } = result;
  const percentage = submission.percentage;

  const getScoreColor = (percentage: number) => {
    if (percentage >= 80) return 'text-green-600';
    if (percentage >= 60) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getScoreBadge = (percentage: number) => {
    if (percentage >= 80) return { label: 'Excellent', color: 'bg-green-500' };
    if (percentage >= 60) return { label: 'Good', color: 'bg-yellow-500' };
    return { label: 'Needs Improvement', color: 'bg-red-500' };
  };

  const scoreBadge = getScoreBadge(percentage);

  return (
    <Card className={cn('hover:shadow-md transition-shadow', className)}>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <CardTitle className="text-lg">{submission.studentName}</CardTitle>
            <p className="text-sm text-gray-600 mt-1">
              Submitted {format(new Date(submission.submittedAt), 'MMM d, yyyy HH:mm')}
            </p>
          </div>
          <Badge className={cn('text-white', scoreBadge.color)}>{scoreBadge.label}</Badge>
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        {/* Score Display */}
        <div className="text-center py-4">
          <div className={cn('text-4xl font-bold', getScoreColor(percentage))}>
            {submission.score}/{submission.totalPoints}
          </div>
          <p className="text-sm text-gray-600 mt-1">{percentage.toFixed(1)}%</p>
        </div>

        {/* Progress Bar */}
        <div className="space-y-2">
          <Progress value={percentage} className="h-2" />
          <div className="flex justify-between text-xs text-gray-600">
            <span>0%</span>
            <span>100%</span>
          </div>
        </div>

        {showDetails && (
          <>
            {/* Statistics Summary */}
            <div className="grid grid-cols-3 gap-4 pt-4 border-t">
              <div className="text-center">
                <div className="flex items-center justify-center mb-1">
                  <CheckCircle className="h-4 w-4 text-green-600 mr-1" />
                  <span className="text-lg font-semibold text-green-600">
                    {statistics.correctAnswers}
                  </span>
                </div>
                <p className="text-xs text-gray-600">Correct</p>
              </div>
              <div className="text-center">
                <div className="flex items-center justify-center mb-1">
                  <XCircle className="h-4 w-4 text-red-600 mr-1" />
                  <span className="text-lg font-semibold text-red-600">
                    {statistics.incorrectAnswers}
                  </span>
                </div>
                <p className="text-xs text-gray-600">Incorrect</p>
              </div>
              <div className="text-center">
                <div className="flex items-center justify-center mb-1">
                  <AlertCircle className="h-4 w-4 text-gray-600 mr-1" />
                  <span className="text-lg font-semibold text-gray-600">
                    {statistics.totalQuestions}
                  </span>
                </div>
                <p className="text-xs text-gray-600">Total</p>
              </div>
            </div>

            {/* Average Score */}
            <div className="pt-4 border-t">
              <div className="flex items-center justify-between text-sm">
                <span className="text-gray-600">Class Average:</span>
                <span className="font-semibold">{statistics.averageScore.toFixed(1)}%</span>
              </div>
            </div>
          </>
        )}
      </CardContent>
    </Card>
  );
};
