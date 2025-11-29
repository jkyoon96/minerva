'use client';

/**
 * Pending reviews list component showing submissions needing manual review
 */

import React from 'react';
import { AlertTriangle, Clock, Eye, TrendingDown } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { AIGradingTask, AIConfidenceLevel, GradingStatus } from '@/types/assessment';
import { cn } from '@/lib/utils';
import { format, formatDistanceToNow } from 'date-fns';

interface PendingReviewsProps {
  tasks: AIGradingTask[];
  onReview: (taskId: number) => void;
  className?: string;
}

export const PendingReviews: React.FC<PendingReviewsProps> = ({
  tasks,
  onReview,
  className,
}) => {
  const pendingTasks = tasks.filter((task) => task.needsReview);
  const lowConfidenceTasks = pendingTasks.filter(
    (task) => task.aiConfidence === AIConfidenceLevel.LOW
  );

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <AlertTriangle className="h-5 w-5 text-yellow-600" />
            <CardTitle>Pending Reviews</CardTitle>
          </div>
          <Badge variant="secondary" className="text-sm">
            {pendingTasks.length} pending
          </Badge>
        </div>
        <p className="text-sm text-gray-600">
          Submissions requiring manual review due to low AI confidence
        </p>
      </CardHeader>

      <CardContent className="space-y-3">
        {pendingTasks.length === 0 ? (
          <div className="text-center py-8">
            <div className="mx-auto w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mb-3">
              <CheckCircle className="h-6 w-6 text-green-600" />
            </div>
            <p className="text-gray-600">All submissions have been reviewed</p>
            <p className="text-sm text-gray-500 mt-1">No pending reviews at this time</p>
          </div>
        ) : (
          <>
            {/* Summary Stats */}
            {lowConfidenceTasks.length > 0 && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg mb-4">
                <div className="flex items-center gap-2">
                  <TrendingDown className="h-5 w-5 text-red-600" />
                  <div>
                    <p className="text-sm font-medium text-red-900">
                      {lowConfidenceTasks.length} Low Confidence Submissions
                    </p>
                    <p className="text-xs text-red-700 mt-0.5">
                      These require immediate attention
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* Task List */}
            {pendingTasks.map((task) => (
              <div
                key={task.id}
                className={cn(
                  'p-4 rounded-lg border-2 transition-all hover:shadow-md',
                  task.aiConfidence === AIConfidenceLevel.LOW
                    ? 'border-red-200 bg-red-50/50'
                    : task.aiConfidence === AIConfidenceLevel.MEDIUM
                    ? 'border-yellow-200 bg-yellow-50/50'
                    : 'border-gray-200 bg-gray-50/50'
                )}
              >
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1 space-y-2">
                    {/* Student Info */}
                    <div>
                      <div className="flex items-center gap-2 mb-1">
                        <h4 className="font-semibold text-gray-900">{task.studentName}</h4>
                        <Badge
                          variant="outline"
                          className={cn(
                            'text-xs',
                            task.aiConfidence === AIConfidenceLevel.LOW
                              ? 'border-red-500 text-red-700'
                              : task.aiConfidence === AIConfidenceLevel.MEDIUM
                              ? 'border-yellow-500 text-yellow-700'
                              : 'border-gray-500 text-gray-700'
                          )}
                        >
                          {task.aiConfidence} Confidence
                        </Badge>
                      </div>
                      <p className="text-sm text-gray-600">{task.assignmentTitle}</p>
                    </div>

                    {/* AI Score */}
                    {task.aiScore !== undefined && (
                      <div className="flex items-center gap-2">
                        <span className="text-xs text-gray-600">AI Score:</span>
                        <span className="text-sm font-semibold">{task.aiScore}</span>
                      </div>
                    )}

                    {/* Timestamp */}
                    <div className="flex items-center gap-1 text-xs text-gray-500">
                      <Clock className="h-3 w-3" />
                      <span>
                        Submitted {formatDistanceToNow(new Date(task.createdAt), { addSuffix: true })}
                      </span>
                    </div>
                  </div>

                  {/* Action Button */}
                  <Button
                    size="sm"
                    onClick={() => onReview(task.id)}
                    variant={
                      task.aiConfidence === AIConfidenceLevel.LOW ? 'default' : 'outline'
                    }
                  >
                    <Eye className="h-4 w-4 mr-1" />
                    Review
                  </Button>
                </div>

                {/* Priority Indicator */}
                {task.aiConfidence === AIConfidenceLevel.LOW && (
                  <div className="mt-3 pt-3 border-t border-red-200">
                    <div className="flex items-center gap-2 text-sm text-red-700">
                      <AlertTriangle className="h-4 w-4" />
                      <span className="font-medium">High Priority Review Required</span>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </>
        )}
      </CardContent>
    </Card>
  );
};

// Import CheckCircle for the component
import { CheckCircle } from 'lucide-react';
