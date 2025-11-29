'use client';

import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Switch } from '@/components/ui/switch';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { QuizCreateRequest } from '@/types/active';

interface QuizFormProps {
  initialData?: Partial<QuizCreateRequest>;
  selectedQuestionIds: number[];
  onSubmit: (data: QuizCreateRequest) => void;
  onCancel?: () => void;
  courseId: number;
  sessionId?: number;
}

export const QuizForm: React.FC<QuizFormProps> = ({
  initialData,
  selectedQuestionIds,
  onSubmit,
  onCancel,
  courseId,
  sessionId,
}) => {
  const [title, setTitle] = useState(initialData?.title || '');
  const [description, setDescription] = useState(initialData?.description || '');
  const [passingScore, setPassingScore] = useState(initialData?.passingScore || 70);
  const [timeLimitMinutes, setTimeLimitMinutes] = useState(initialData?.timeLimitMinutes);
  const [allowRetake, setAllowRetake] = useState(initialData?.allowRetake !== false);
  const [shuffleQuestions, setShuffleQuestions] = useState(initialData?.shuffleQuestions !== false);
  const [showCorrectAnswers, setShowCorrectAnswers] = useState(initialData?.showCorrectAnswers !== false);
  const [showScoreImmediately, setShowScoreImmediately] = useState(initialData?.showScoreImmediately !== false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const data: QuizCreateRequest = {
      courseId,
      sessionId,
      title,
      description: description || undefined,
      questionIds: selectedQuestionIds,
      passingScore,
      timeLimitMinutes,
      allowRetake,
      shuffleQuestions,
      showCorrectAnswers,
      showScoreImmediately,
    };

    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Quiz Details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="title">Title *</Label>
            <Input
              id="title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Passing Score (%)</Label>
              <Input
                type="number"
                min="0"
                max="100"
                value={passingScore}
                onChange={(e) => setPassingScore(Number(e.target.value))}
              />
            </div>

            <div className="space-y-2">
              <Label>Time Limit (minutes)</Label>
              <Input
                type="number"
                min="5"
                value={timeLimitMinutes || ''}
                onChange={(e) => setTimeLimitMinutes(e.target.value ? Number(e.target.value) : undefined)}
              />
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Settings</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between">
            <Label>Allow Retake</Label>
            <Switch checked={allowRetake} onCheckedChange={setAllowRetake} />
          </div>
          <div className="flex items-center justify-between">
            <Label>Shuffle Questions</Label>
            <Switch checked={shuffleQuestions} onCheckedChange={setShuffleQuestions} />
          </div>
          <div className="flex items-center justify-between">
            <Label>Show Correct Answers</Label>
            <Switch checked={showCorrectAnswers} onCheckedChange={setShowCorrectAnswers} />
          </div>
          <div className="flex items-center justify-between">
            <Label>Show Score Immediately</Label>
            <Switch checked={showScoreImmediately} onCheckedChange={setShowScoreImmediately} />
          </div>
        </CardContent>
      </Card>

      <div className="flex justify-end gap-2">
        {onCancel && (
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
        )}
        <Button type="submit" disabled={selectedQuestionIds.length === 0}>
          {initialData ? 'Update Quiz' : 'Create Quiz'} ({selectedQuestionIds.length} questions)
        </Button>
      </div>
    </form>
  );
};
