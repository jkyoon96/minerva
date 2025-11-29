'use client';

/**
 * Grade editor component for professors to modify AI grades
 */

import React, { useState } from 'react';
import { Edit2, Save, X, AlertCircle } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AIGradingResult } from '@/types/assessment';
import { cn } from '@/lib/utils';

interface GradeEditorProps {
  result: AIGradingResult;
  onSave: (newScore: number, reason: string) => Promise<void>;
  onCancel: () => void;
  className?: string;
}

export const GradeEditor: React.FC<GradeEditorProps> = ({
  result,
  onSave,
  onCancel,
  className,
}) => {
  const [score, setScore] = useState(result.finalScore || result.score);
  const [reason, setReason] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSave = async () => {
    // Validation
    if (score < 0 || score > result.maxScore) {
      setError(`Score must be between 0 and ${result.maxScore}`);
      return;
    }

    if (!reason.trim()) {
      setError('Please provide a reason for the grade modification');
      return;
    }

    try {
      setIsSaving(true);
      setError(null);
      await onSave(score, reason);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to save grade');
    } finally {
      setIsSaving(false);
    }
  };

  const scoreDifference = score - result.score;
  const percentageChange = ((scoreDifference / result.score) * 100).toFixed(1);

  return (
    <Card className={cn('border-2 border-purple-200', className)}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Edit2 className="h-5 w-5 text-purple-600" />
            <CardTitle className="text-lg">Edit Grade</CardTitle>
          </div>
          <Button variant="ghost" size="sm" onClick={onCancel}>
            <X className="h-4 w-4" />
          </Button>
        </div>
        <p className="text-sm text-gray-600">
          Modify the AI-generated grade and provide justification
        </p>
      </CardHeader>

      <CardContent className="space-y-4">
        {error && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {/* Original AI Score */}
        <div className="p-4 bg-purple-50 rounded-lg">
          <div className="flex items-center justify-between mb-2">
            <span className="text-sm font-medium text-gray-700">Original AI Score</span>
            <span className="text-lg font-semibold text-gray-900">
              {result.score}/{result.maxScore}
            </span>
          </div>
          <p className="text-xs text-gray-600">
            Confidence: {result.confidence}
          </p>
        </div>

        {/* New Score Input */}
        <div className="space-y-2">
          <Label htmlFor="score">New Score</Label>
          <div className="flex items-center gap-2">
            <Input
              id="score"
              type="number"
              min={0}
              max={result.maxScore}
              step={0.5}
              value={score}
              onChange={(e) => setScore(parseFloat(e.target.value))}
              className="text-lg font-semibold"
            />
            <span className="text-gray-600">/ {result.maxScore}</span>
          </div>
          {scoreDifference !== 0 && (
            <p
              className={cn(
                'text-sm font-medium',
                scoreDifference > 0 ? 'text-green-600' : 'text-red-600'
              )}
            >
              {scoreDifference > 0 ? '+' : ''}
              {scoreDifference} ({percentageChange}%)
            </p>
          )}
        </div>

        {/* Score Comparison */}
        <div className="grid grid-cols-2 gap-4 p-4 bg-gray-50 rounded-lg">
          <div>
            <p className="text-xs text-gray-600 mb-1">AI Score</p>
            <p className="text-2xl font-bold text-gray-900">{result.score}</p>
          </div>
          <div>
            <p className="text-xs text-gray-600 mb-1">New Score</p>
            <p className="text-2xl font-bold text-purple-600">{score}</p>
          </div>
        </div>

        {/* Reason for Change */}
        <div className="space-y-2">
          <Label htmlFor="reason">Reason for Modification *</Label>
          <Textarea
            id="reason"
            placeholder="Explain why you're modifying the AI-generated grade..."
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            rows={4}
            className="resize-none"
          />
          <p className="text-xs text-gray-500">
            This will be recorded for transparency and audit purposes
          </p>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-2 pt-2">
          <Button
            onClick={handleSave}
            disabled={isSaving || score === result.score}
            className="flex-1"
          >
            <Save className="h-4 w-4 mr-2" />
            {isSaving ? 'Saving...' : 'Save Grade'}
          </Button>
          <Button variant="outline" onClick={onCancel} disabled={isSaving}>
            Cancel
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};
