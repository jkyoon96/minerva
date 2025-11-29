'use client';

/**
 * Answer statistics chart component for displaying question-level stats
 */

import React from 'react';
import { BarChart3, TrendingUp, TrendingDown } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { AnswerStatistics as AnswerStatsType } from '@/types/assessment';
import { cn } from '@/lib/utils';

interface AnswerStatisticsProps {
  statistics: AnswerStatsType[];
  className?: string;
}

export const AnswerStatistics: React.FC<AnswerStatisticsProps> = ({ statistics, className }) => {
  return (
    <div className={cn('space-y-4', className)}>
      {statistics.map((stat) => (
        <Card key={stat.questionId}>
          <CardHeader>
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <CardTitle className="text-base">{stat.questionText}</CardTitle>
                <p className="text-sm text-gray-600 mt-1">
                  {stat.totalResponses} responses
                </p>
              </div>
              <div className="flex items-center gap-2">
                {stat.correctPercentage >= 70 ? (
                  <TrendingUp className="h-5 w-5 text-green-600" />
                ) : (
                  <TrendingDown className="h-5 w-5 text-red-600" />
                )}
                <span
                  className={cn(
                    'text-lg font-semibold',
                    stat.correctPercentage >= 70 ? 'text-green-600' : 'text-red-600'
                  )}
                >
                  {stat.correctPercentage.toFixed(1)}%
                </span>
              </div>
            </div>
          </CardHeader>

          <CardContent className="space-y-4">
            {/* Correct/Incorrect Breakdown */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-600">Correct</span>
                  <span className="font-semibold text-green-600">{stat.correctCount}</span>
                </div>
                <Progress value={stat.correctPercentage} className="h-2 bg-green-100" />
              </div>
              <div className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-600">Incorrect</span>
                  <span className="font-semibold text-red-600">{stat.incorrectCount}</span>
                </div>
                <Progress
                  value={100 - stat.correctPercentage}
                  className="h-2 bg-red-100"
                />
              </div>
            </div>

            {/* Option-level Statistics */}
            {stat.options && stat.options.length > 0 && (
              <div className="pt-4 border-t space-y-3">
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <BarChart3 className="h-4 w-4" />
                  <span>Answer Distribution</span>
                </div>
                {stat.options.map((option, index) => (
                  <div key={index} className="space-y-1">
                    <div className="flex items-center justify-between text-sm">
                      <div className="flex items-center gap-2">
                        <span className="text-gray-700">{option.optionText}</span>
                        {option.isCorrect && (
                          <span className="text-xs text-green-600 font-medium">
                            (Correct)
                          </span>
                        )}
                      </div>
                      <span className="font-medium">
                        {option.count} ({option.percentage.toFixed(1)}%)
                      </span>
                    </div>
                    <Progress
                      value={option.percentage}
                      className={cn(
                        'h-1.5',
                        option.isCorrect ? 'bg-green-100' : 'bg-gray-100'
                      )}
                    />
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      ))}
    </div>
  );
};
