'use client';

import React from 'react';
import { Shield, AlertTriangle, Users } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { PlagiarismCheckResult } from '@/types/assessment';
import { cn } from '@/lib/utils';

interface PlagiarismResultProps {
  result: PlagiarismCheckResult;
  className?: string;
}

export const PlagiarismResult: React.FC<PlagiarismResultProps> = ({ result, className }) => {
  const getSeverityColor = (score: number) => {
    if (score >= 70) return 'text-red-600';
    if (score >= 40) return 'text-yellow-600';
    return 'text-green-600';
  };

  return (
    <Card className={cn(result.flagged && 'border-2 border-red-300', className)}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Shield className="h-5 w-5 text-blue-600" />
            <CardTitle>Plagiarism Check</CardTitle>
          </div>
          {result.flagged && (
            <Badge className="bg-red-500 text-white">Flagged</Badge>
          )}
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        <div className="text-center p-4 bg-gray-50 rounded-lg">
          <div className={cn('text-4xl font-bold', getSeverityColor(result.similarityScore))}>
            {result.similarityScore}%
          </div>
          <p className="text-sm text-gray-600 mt-1">Overall Similarity</p>
        </div>

        <Progress value={result.similarityScore} className="h-2" />

        {result.matchedSubmissions.length > 0 && (
          <div className="space-y-3">
            <h4 className="text-sm font-semibold flex items-center gap-2">
              <Users className="h-4 w-4" />
              Matched Submissions ({result.matchedSubmissions.length})
            </h4>
            {result.matchedSubmissions.map((match) => (
              <div key={match.submissionId} className="p-3 border rounded-lg">
                <div className="flex justify-between items-start mb-2">
                  <span className="font-medium">{match.studentName}</span>
                  <Badge variant="outline">{match.similarityPercentage}% similar</Badge>
                </div>
                <p className="text-xs text-gray-600">{match.matchedLines} lines matched</p>
              </div>
            ))}
          </div>
        )}

        {result.flagged && (
          <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
            <div className="flex items-start gap-2">
              <AlertTriangle className="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
              <div>
                <p className="font-medium text-red-900">High Similarity Detected</p>
                <p className="text-xs text-red-700 mt-1">This submission requires manual review</p>
              </div>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};
