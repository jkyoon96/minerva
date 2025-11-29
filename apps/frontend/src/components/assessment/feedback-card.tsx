'use client';

import React from 'react';
import { MessageSquare, Brain, User, Users } from 'lucide-react';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Feedback, FeedbackType } from '@/types/assessment';
import { format } from 'date-fns';
import { cn } from '@/lib/utils';

interface FeedbackCardProps {
  feedback: Feedback;
  className?: string;
}

const TYPE_CONFIG = {
  [FeedbackType.AI_GENERATED]: { icon: Brain, label: 'AI Generated', color: 'bg-purple-500' },
  [FeedbackType.INSTRUCTOR]: { icon: User, label: 'Instructor', color: 'bg-blue-500' },
  [FeedbackType.PEER]: { icon: Users, label: 'Peer Review', color: 'bg-green-500' },
};

export const FeedbackCard: React.FC<FeedbackCardProps> = ({ feedback, className }) => {
  const config = TYPE_CONFIG[feedback.type];
  const Icon = config.icon;

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Icon className="h-5 w-5" />
            <h3 className="font-semibold">Feedback</h3>
          </div>
          <Badge className={cn('text-white', config.color)}>{config.label}</Badge>
        </div>
        <p className="text-xs text-gray-500">
          {format(new Date(feedback.createdAt), 'MMM d, yyyy')}
          {feedback.createdByName && ` by ${feedback.createdByName}`}
        </p>
      </CardHeader>

      <CardContent className="space-y-4">
        <div className="prose prose-sm">
          <p className="text-gray-700 leading-relaxed">{feedback.content}</p>
        </div>

        {feedback.strengths && feedback.strengths.length > 0 && (
          <div>
            <h4 className="text-sm font-semibold text-green-700 mb-2">Strengths</h4>
            <ul className="space-y-1">
              {feedback.strengths.map((strength, i) => (
                <li key={i} className="text-sm text-gray-700 flex items-start gap-2">
                  <span className="text-green-600 mt-1">✓</span>
                  <span>{strength}</span>
                </li>
              ))}
            </ul>
          </div>
        )}

        {feedback.improvements && feedback.improvements.length > 0 && (
          <div>
            <h4 className="text-sm font-semibold text-yellow-700 mb-2">Areas for Improvement</h4>
            <ul className="space-y-1">
              {feedback.improvements.map((improvement, i) => (
                <li key={i} className="text-sm text-gray-700 flex items-start gap-2">
                  <span className="text-yellow-600 mt-1">→</span>
                  <span>{improvement}</span>
                </li>
              ))}
            </ul>
          </div>
        )}
      </CardContent>
    </Card>
  );
};
