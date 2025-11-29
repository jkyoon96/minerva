'use client';

/**
 * Grade breakdown component showing question-by-question results
 */

import React from 'react';
import { CheckCircle, XCircle, ChevronDown, ChevronUp } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { AutoGradingResult } from '@/types/assessment';
import { cn } from '@/lib/utils';

interface GradeBreakdownProps {
  result: AutoGradingResult;
  className?: string;
}

export const GradeBreakdown: React.FC<GradeBreakdownProps> = ({ result, className }) => {
  const [expandedQuestions, setExpandedQuestions] = React.useState<Set<number>>(new Set());

  const toggleQuestion = (questionId: number) => {
    setExpandedQuestions((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(questionId)) {
        newSet.delete(questionId);
      } else {
        newSet.add(questionId);
      }
      return newSet;
    });
  };

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle>Grade Breakdown</CardTitle>
        <p className="text-sm text-gray-600">Question-by-question results</p>
      </CardHeader>

      <CardContent className="space-y-3">
        {result.questionResults.map((question, index) => {
          const isExpanded = expandedQuestions.has(question.questionId);
          const isCorrect = question.isCorrect;

          return (
            <div
              key={question.questionId}
              className={cn(
                'border rounded-lg overflow-hidden transition-all',
                isCorrect ? 'border-green-200 bg-green-50/30' : 'border-red-200 bg-red-50/30'
              )}
            >
              <div className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      {isCorrect ? (
                        <CheckCircle className="h-5 w-5 text-green-600" />
                      ) : (
                        <XCircle className="h-5 w-5 text-red-600" />
                      )}
                      <span className="text-sm font-medium">Question {index + 1}</span>
                      <Badge
                        variant={isCorrect ? 'default' : 'secondary'}
                        className={cn(
                          'text-xs',
                          isCorrect ? 'bg-green-500' : 'bg-red-500 text-white'
                        )}
                      >
                        {question.earnedPoints}/{question.points} pts
                      </Badge>
                    </div>
                    <p className="text-sm text-gray-900 mb-2">{question.questionText}</p>

                    {isExpanded && (
                      <div className="mt-4 space-y-3 pt-4 border-t">
                        {/* Student Answer */}
                        <div>
                          <p className="text-xs font-medium text-gray-600 mb-1">
                            Student Answer:
                          </p>
                          <div
                            className={cn(
                              'p-3 rounded text-sm',
                              isCorrect ? 'bg-green-100' : 'bg-red-100'
                            )}
                          >
                            {Array.isArray(question.studentAnswer)
                              ? question.studentAnswer.join(', ')
                              : question.studentAnswer}
                          </div>
                        </div>

                        {/* Correct Answer */}
                        <div>
                          <p className="text-xs font-medium text-gray-600 mb-1">
                            Correct Answer:
                          </p>
                          <div className="p-3 bg-green-100 rounded text-sm">
                            {Array.isArray(question.correctAnswer)
                              ? question.correctAnswer.join(', ')
                              : question.correctAnswer}
                          </div>
                        </div>

                        {/* Explanation */}
                        {question.explanation && (
                          <div>
                            <p className="text-xs font-medium text-gray-600 mb-1">
                              Explanation:
                            </p>
                            <div className="p-3 bg-blue-50 rounded text-sm text-gray-700">
                              {question.explanation}
                            </div>
                          </div>
                        )}
                      </div>
                    )}
                  </div>

                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => toggleQuestion(question.questionId)}
                    className="ml-2"
                  >
                    {isExpanded ? (
                      <ChevronUp className="h-4 w-4" />
                    ) : (
                      <ChevronDown className="h-4 w-4" />
                    )}
                  </Button>
                </div>
              </div>
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
};
