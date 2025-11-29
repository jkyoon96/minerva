'use client';

/**
 * Explanation panel component for displaying answer explanations
 */

import React from 'react';
import { Lightbulb, BookOpen, AlertCircle } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { AutoGradingResult } from '@/types/assessment';
import { cn } from '@/lib/utils';

interface ExplanationPanelProps {
  result: AutoGradingResult;
  className?: string;
}

export const ExplanationPanel: React.FC<ExplanationPanelProps> = ({ result, className }) => {
  const incorrectQuestions = result.questionResults.filter((q) => !q.isCorrect);
  const correctQuestions = result.questionResults.filter((q) => q.isCorrect);

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Lightbulb className="h-5 w-5 text-yellow-500" />
          Answer Explanations
        </CardTitle>
        <p className="text-sm text-gray-600">
          Review explanations to understand your results
        </p>
      </CardHeader>

      <CardContent>
        <Tabs defaultValue="incorrect">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="incorrect">
              Incorrect ({incorrectQuestions.length})
            </TabsTrigger>
            <TabsTrigger value="all">All ({result.questionResults.length})</TabsTrigger>
          </TabsList>

          <TabsContent value="incorrect" className="space-y-4 mt-4">
            {incorrectQuestions.length === 0 ? (
              <div className="text-center py-8">
                <CheckCircle className="h-12 w-12 text-green-500 mx-auto mb-2" />
                <p className="text-gray-600">Perfect score! All answers are correct.</p>
              </div>
            ) : (
              incorrectQuestions.map((question, index) => (
                <ExplanationCard key={question.questionId} question={question} index={index} />
              ))
            )}
          </TabsContent>

          <TabsContent value="all" className="space-y-4 mt-4">
            {result.questionResults.map((question, index) => (
              <ExplanationCard key={question.questionId} question={question} index={index} />
            ))}
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  );
};

interface ExplanationCardProps {
  question: any;
  index: number;
}

const ExplanationCard: React.FC<ExplanationCardProps> = ({ question, index }) => {
  return (
    <div
      className={cn(
        'p-4 rounded-lg border-2',
        question.isCorrect
          ? 'border-green-200 bg-green-50/50'
          : 'border-red-200 bg-red-50/50'
      )}
    >
      <div className="flex items-start gap-3">
        <div
          className={cn(
            'flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center text-sm font-semibold',
            question.isCorrect ? 'bg-green-500 text-white' : 'bg-red-500 text-white'
          )}
        >
          {index + 1}
        </div>
        <div className="flex-1 space-y-3">
          <div>
            <p className="font-medium text-gray-900 mb-1">{question.questionText}</p>
            <Badge variant={question.isCorrect ? 'default' : 'secondary'} className="text-xs">
              {question.earnedPoints}/{question.points} points
            </Badge>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div>
              <p className="text-xs font-medium text-gray-600 mb-1 flex items-center gap-1">
                <AlertCircle className="h-3 w-3" />
                Your Answer
              </p>
              <div
                className={cn(
                  'p-2 rounded text-sm',
                  question.isCorrect ? 'bg-green-100' : 'bg-red-100'
                )}
              >
                {Array.isArray(question.studentAnswer)
                  ? question.studentAnswer.join(', ')
                  : question.studentAnswer}
              </div>
            </div>
            <div>
              <p className="text-xs font-medium text-gray-600 mb-1 flex items-center gap-1">
                <BookOpen className="h-3 w-3" />
                Correct Answer
              </p>
              <div className="p-2 bg-green-100 rounded text-sm">
                {Array.isArray(question.correctAnswer)
                  ? question.correctAnswer.join(', ')
                  : question.correctAnswer}
              </div>
            </div>
          </div>

          {question.explanation && (
            <div className="pt-3 border-t border-gray-200">
              <p className="text-xs font-medium text-gray-600 mb-2 flex items-center gap-1">
                <Lightbulb className="h-3 w-3" />
                Explanation
              </p>
              <p className="text-sm text-gray-700 leading-relaxed">{question.explanation}</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

// Import CheckCircle for the component
import { CheckCircle } from 'lucide-react';
