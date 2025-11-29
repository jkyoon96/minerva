'use client';

import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { PeerReviewResult } from '@/types/assessment';

interface PeerResultSummaryProps {
  result: PeerReviewResult;
}

export const PeerResultSummary: React.FC<PeerResultSummaryProps> = ({ result }) => (
  <Card>
    <CardHeader>
      <CardTitle>Aggregated Results</CardTitle>
    </CardHeader>
    <CardContent className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <div className="text-center p-4 bg-blue-50 rounded-lg">
          <p className="text-2xl font-bold text-blue-600">{result.aggregatedScore.averageScore.toFixed(1)}</p>
          <p className="text-sm text-gray-600">Average Score</p>
        </div>
        <div className="text-center p-4 bg-purple-50 rounded-lg">
          <p className="text-2xl font-bold text-purple-600">{result.aggregatedScore.medianScore.toFixed(1)}</p>
          <p className="text-sm text-gray-600">Median Score</p>
        </div>
      </div>

      <div className="space-y-3">
        <h4 className="font-semibold">Criteria Breakdown</h4>
        {result.aggregatedScore.criteriaAverages.map((criteria) => (
          <div key={criteria.criteriaId}>
            <div className="flex justify-between text-sm mb-1">
              <span>{criteria.criteriaName}</span>
              <span>{criteria.average.toFixed(1)}/{criteria.maxScore}</span>
            </div>
            <Progress value={(criteria.average / criteria.maxScore) * 100} className="h-2" />
          </div>
        ))}
      </div>
    </CardContent>
  </Card>
);
