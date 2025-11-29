'use client';

import React from 'react';
import { CheckCircle, XCircle, Award } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { QuizResult } from '@/types/active';

interface QuizResultsProps {
  result: QuizResult;
}

export const QuizResults: React.FC<QuizResultsProps> = ({ result }) => {
  const { submission, questionResults } = result;
  const passed = submission.percentage >= 70;

  return (
    <div className="space-y-6">
      <Card className={passed ? 'border-green-500' : 'border-red-500'}>
        <CardHeader>
          <div className="text-center space-y-4">
            <Award className={`h-16 w-16 mx-auto ${passed ? 'text-green-500' : 'text-red-500'}`} />
            <div>
              <h2 className="text-3xl font-bold">
                {submission.score} / {submission.totalPoints}
              </h2>
              <p className="text-2xl text-gray-600">{submission.percentage.toFixed(1)}%</p>
            </div>
            <Badge className={passed ? 'bg-green-500' : 'bg-red-500'}>
              {passed ? 'PASSED' : 'FAILED'}
            </Badge>
          </div>
        </CardHeader>
      </Card>

      <div className="space-y-4">
        {questionResults.map((qResult, index) => (
          <Card key={qResult.questionId}>
            <CardHeader>
              <div className="flex items-start justify-between">
                <CardTitle className="text-base flex items-center gap-2">
                  {qResult.isCorrect ? (
                    <CheckCircle className="h-5 w-5 text-green-500" />
                  ) : (
                    <XCircle className="h-5 w-5 text-red-500" />
                  )}
                  Question {index + 1}
                </CardTitle>
                <span className="text-sm">
                  {qResult.earnedPoints} / {qResult.points} pts
                </span>
              </div>
            </CardHeader>
            <CardContent>
              <p className="font-medium mb-3">{qResult.questionText}</p>
              {qResult.explanation && (
                <div className="mt-3 p-3 bg-blue-50 rounded-lg">
                  <p className="text-sm text-blue-900">{qResult.explanation}</p>
                </div>
              )}
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};
