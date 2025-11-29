'use client';

/**
 * AI grading result component with confidence display
 */

import React from 'react';
import { Brain, AlertTriangle, CheckCircle, TrendingUp } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { AIGradingResult, AIConfidenceLevel } from '@/types/assessment';
import { cn } from '@/lib/utils';
import { format } from 'date-fns';

interface AIGradingResultProps {
  result: AIGradingResult;
  onReview?: () => void;
  className?: string;
}

const CONFIDENCE_CONFIG = {
  [AIConfidenceLevel.HIGH]: {
    label: 'High Confidence',
    color: 'bg-green-500',
    textColor: 'text-green-700',
    icon: CheckCircle,
  },
  [AIConfidenceLevel.MEDIUM]: {
    label: 'Medium Confidence',
    color: 'bg-yellow-500',
    textColor: 'text-yellow-700',
    icon: TrendingUp,
  },
  [AIConfidenceLevel.LOW]: {
    label: 'Low Confidence',
    color: 'bg-red-500',
    textColor: 'text-red-700',
    icon: AlertTriangle,
  },
};

export const AIGradingResult: React.FC<AIGradingResultProps> = ({
  result,
  onReview,
  className,
}) => {
  const confidenceConfig = CONFIDENCE_CONFIG[result.confidence];
  const Icon = confidenceConfig.icon;
  const percentage = (result.score / result.maxScore) * 100;

  return (
    <Card className={cn('hover:shadow-md transition-shadow', className)}>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-2">
            <Brain className="h-5 w-5 text-purple-600" />
            <CardTitle className="text-lg">AI Grading Result</CardTitle>
          </div>
          <Badge className={cn('text-white', confidenceConfig.color)}>
            {confidenceConfig.label}
          </Badge>
        </div>
        <p className="text-sm text-gray-600">
          Graded {format(new Date(result.gradedAt), 'MMM d, yyyy HH:mm')}
        </p>
      </CardHeader>

      <CardContent className="space-y-6">
        {/* Score Display */}
        <div className="text-center py-4 bg-gradient-to-br from-purple-50 to-blue-50 rounded-lg">
          <div className="flex items-center justify-center gap-2 mb-2">
            <Icon className={cn('h-6 w-6', confidenceConfig.textColor)} />
            <div className="text-4xl font-bold text-gray-900">
              {result.score}/{result.maxScore}
            </div>
          </div>
          <p className="text-lg text-gray-600">{percentage.toFixed(1)}%</p>
        </div>

        {/* Progress Bar */}
        <div className="space-y-2">
          <div className="flex items-center justify-between text-sm">
            <span className="text-gray-600">Confidence Level</span>
            <span className={cn('font-medium', confidenceConfig.textColor)}>
              {result.confidence}
            </span>
          </div>
          <Progress
            value={
              result.confidence === AIConfidenceLevel.HIGH
                ? 90
                : result.confidence === AIConfidenceLevel.MEDIUM
                ? 60
                : 30
            }
            className="h-2"
          />
        </div>

        {/* AI Feedback */}
        {result.feedback && (
          <div className="space-y-2">
            <h4 className="text-sm font-semibold text-gray-900">AI Feedback</h4>
            <div className="p-3 bg-blue-50 rounded-lg">
              <p className="text-sm text-gray-700 leading-relaxed">{result.feedback}</p>
            </div>
          </div>
        )}

        {/* Rubric Scores */}
        {result.rubricScores && result.rubricScores.length > 0 && (
          <div className="space-y-3">
            <h4 className="text-sm font-semibold text-gray-900">Rubric Breakdown</h4>
            {result.rubricScores.map((rubric) => (
              <div key={rubric.criteriaId} className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="font-medium text-gray-700">{rubric.criteriaName}</span>
                  <span className="text-gray-900">
                    {rubric.score}/{rubric.maxScore}
                  </span>
                </div>
                <Progress
                  value={(rubric.score / rubric.maxScore) * 100}
                  className="h-1.5"
                />
                {rubric.feedback && (
                  <p className="text-xs text-gray-600 pl-2">{rubric.feedback}</p>
                )}
              </div>
            ))}
          </div>
        )}

        {/* Suggested Improvements */}
        {result.suggestedImprovements && result.suggestedImprovements.length > 0 && (
          <div className="space-y-2">
            <h4 className="text-sm font-semibold text-gray-900">Suggested Improvements</h4>
            <ul className="space-y-1">
              {result.suggestedImprovements.map((improvement, index) => (
                <li key={index} className="text-sm text-gray-700 flex items-start gap-2">
                  <span className="text-purple-600 mt-1">•</span>
                  <span>{improvement}</span>
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Review Status */}
        {result.reviewedAt && (
          <div className="pt-4 border-t space-y-2">
            <div className="flex items-center justify-between text-sm">
              <span className="text-gray-600">Reviewed by:</span>
              <span className="font-medium">{result.reviewedByName}</span>
            </div>
            <div className="flex items-center justify-between text-sm">
              <span className="text-gray-600">Final Score:</span>
              <span className="font-semibold text-lg">
                {result.finalScore}/{result.maxScore}
              </span>
            </div>
            <p className="text-xs text-gray-500">
              Reviewed {format(new Date(result.reviewedAt), 'MMM d, yyyy HH:mm')}
            </p>
          </div>
        )}

        {/* Action needed for low confidence */}
        {result.confidence === AIConfidenceLevel.LOW && !result.reviewedAt && (
          <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
            <div className="flex items-start gap-2">
              <AlertTriangle className="h-5 w-5 text-yellow-600 flex-shrink-0 mt-0.5" />
              <div className="flex-1">
                <p className="text-sm font-medium text-yellow-900">Review Recommended</p>
                <p className="text-xs text-yellow-700 mt-1">
                  This submission has low AI confidence and may require manual review.
                </p>
              </div>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};
