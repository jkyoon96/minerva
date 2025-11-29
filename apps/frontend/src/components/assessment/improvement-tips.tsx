'use client';

import React from 'react';
import { Lightbulb, TrendingUp, Target } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ImprovementSuggestion } from '@/types/assessment';
import { cn } from '@/lib/utils';

interface ImprovementTipsProps {
  suggestions: ImprovementSuggestion[];
  className?: string;
}

const PRIORITY_CONFIG = {
  HIGH: { color: 'bg-red-500', label: 'High Priority' },
  MEDIUM: { color: 'bg-yellow-500', label: 'Medium Priority' },
  LOW: { color: 'bg-blue-500', label: 'Low Priority' },
};

export const ImprovementTips: React.FC<ImprovementTipsProps> = ({ suggestions, className }) => {
  const sortedSuggestions = [...suggestions].sort((a, b) => {
    const priority = { HIGH: 0, MEDIUM: 1, LOW: 2 };
    return priority[a.priority] - priority[b.priority];
  });

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Lightbulb className="h-5 w-5 text-yellow-500" />
          Improvement Suggestions
        </CardTitle>
        <p className="text-sm text-gray-600">Personalized tips to enhance your work</p>
      </CardHeader>

      <CardContent className="space-y-4">
        {sortedSuggestions.map((suggestion) => {
          const config = PRIORITY_CONFIG[suggestion.priority];
          return (
            <div key={suggestion.id} className="p-4 border-l-4 border-blue-500 bg-blue-50/50 rounded-r-lg">
              <div className="flex items-start justify-between mb-2">
                <div className="flex items-center gap-2">
                  <Target className="h-4 w-4 text-blue-600" />
                  <span className="font-semibold text-sm text-gray-900">{suggestion.category}</span>
                </div>
                <Badge className={cn('text-white text-xs', config.color)}>{config.label}</Badge>
              </div>
              
              <p className="text-sm text-gray-700 mb-3">{suggestion.suggestion}</p>

              {suggestion.examples && suggestion.examples.length > 0 && (
                <div className="mt-3">
                  <p className="text-xs font-medium text-gray-600 mb-1">Examples:</p>
                  <ul className="space-y-1">
                    {suggestion.examples.map((example, i) => (
                      <li key={i} className="text-xs text-gray-700 pl-4 relative before:content-['→'] before:absolute before:left-0">
                        {example}
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {suggestion.relatedConcepts && suggestion.relatedConcepts.length > 0 && (
                <div className="mt-3 flex flex-wrap gap-1">
                  {suggestion.relatedConcepts.map((concept, i) => (
                    <Badge key={i} variant="outline" className="text-xs">{concept}</Badge>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
};
