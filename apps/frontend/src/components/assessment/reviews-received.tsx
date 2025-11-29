'use client';

import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';

export const ReviewsReceived = ({ reviews }: any) => (
  <Card>
    <CardHeader>
      <CardTitle>Reviews Received</CardTitle>
    </CardHeader>
    <CardContent>
      {reviews.map((review: any, i: number) => (
        <div key={i} className="p-4 border rounded-lg mb-3">
          <div className="flex justify-between mb-2">
            <span className="font-medium">{review.isAnonymous ? 'Anonymous' : review.reviewerName}</span>
            <Badge>{review.totalScore} pts</Badge>
          </div>
          <p className="text-sm text-gray-700">{review.overallComment}</p>
          {review.rubricScores.map((score: any, j: number) => (
            <div key={j} className="mt-3">
              <div className="flex justify-between text-sm mb-1">
                <span>{score.criteriaName}</span>
                <span>{score.score}/{score.maxScore}</span>
              </div>
              <Progress value={(score.score / score.maxScore) * 100} className="h-1" />
              {score.comment && <p className="text-xs text-gray-600 mt-1">{score.comment}</p>}
            </div>
          ))}
        </div>
      ))}
    </CardContent>
  </Card>
);
