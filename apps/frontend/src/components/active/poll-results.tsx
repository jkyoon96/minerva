'use client';

/**
 * Poll results display with charts
 */

import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { Badge } from '@/components/ui/badge';
import { PollResult, PollType } from '@/types/active';

interface PollResultsProps {
  result: PollResult;
}

export const PollResults: React.FC<PollResultsProps> = ({ result }) => {
  const { poll, optionStats, wordCloudData, ratingAverage } = result;

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="text-xl">{poll.question}</CardTitle>
          <Badge variant="secondary">{poll.totalResponses} responses</Badge>
        </div>
        {poll.description && <p className="text-sm text-gray-600 mt-2">{poll.description}</p>}
      </CardHeader>

      <CardContent className="space-y-6">
        {/* Multiple Choice / Yes-No Results */}
        {(poll.pollType === PollType.MULTIPLE_CHOICE || poll.pollType === PollType.YES_NO) && (
          <div className="space-y-4">
            {optionStats.map((stat) => (
              <div key={stat.optionId} className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="font-medium">{stat.optionText}</span>
                  <div className="flex items-center gap-2">
                    <span className="text-gray-600">{stat.count} votes</span>
                    <span className="font-semibold">{stat.percentage.toFixed(1)}%</span>
                  </div>
                </div>
                <Progress value={stat.percentage} className="h-3" />
              </div>
            ))}
          </div>
        )}

        {/* Rating Results */}
        {poll.pollType === PollType.RATING && (
          <div className="space-y-6">
            <div className="text-center">
              <div className="text-5xl font-bold text-blue-600">
                {ratingAverage?.toFixed(1) || '0.0'}
              </div>
              <p className="text-sm text-gray-600 mt-1">Average Rating</p>
            </div>

            <div className="space-y-2">
              {[5, 4, 3, 2, 1].map((rating) => {
                const stat = optionStats.find((s) => s.optionText === rating.toString());
                const percentage = stat?.percentage || 0;
                const count = stat?.count || 0;

                return (
                  <div key={rating} className="flex items-center gap-3">
                    <span className="w-8 text-sm font-medium">{rating} ★</span>
                    <Progress value={percentage} className="flex-1 h-2" />
                    <span className="w-16 text-sm text-gray-600 text-right">
                      {count} ({percentage.toFixed(0)}%)
                    </span>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Word Cloud Results */}
        {poll.pollType === PollType.WORD_CLOUD && wordCloudData && (
          <div className="flex flex-wrap gap-2 justify-center p-6">
            {wordCloudData.map((item, index) => {
              const fontSize = Math.max(12, Math.min(36, item.value * 4));
              const opacity = Math.max(0.5, Math.min(1, item.value / 10));

              return (
                <span
                  key={index}
                  className="inline-block px-2 py-1"
                  style={{
                    fontSize: `${fontSize}px`,
                    opacity,
                    color: `hsl(${(index * 137) % 360}, 70%, 50%)`,
                  }}
                >
                  {item.text}
                </span>
              );
            })}
          </div>
        )}

        {/* Open Ended Results */}
        {poll.pollType === PollType.OPEN_ENDED && (
          <div className="space-y-3">
            <h4 className="font-semibold text-sm text-gray-700">Responses:</h4>
            <div className="space-y-2 max-h-96 overflow-y-auto">
              {result.responses.map((response) => (
                <div key={response.id} className="p-3 bg-gray-50 rounded-lg">
                  <p className="text-sm">{response.textResponse}</p>
                  {!response.isAnonymous && (
                    <p className="text-xs text-gray-500 mt-1">— {response.userName}</p>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};
