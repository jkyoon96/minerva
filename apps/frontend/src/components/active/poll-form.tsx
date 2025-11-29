'use client';

/**
 * Poll creation/edit form component
 */

import React, { useState } from 'react';
import { Plus, X, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Switch } from '@/components/ui/switch';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { PollType, PollCreateRequest } from '@/types/active';

interface PollFormProps {
  initialData?: Partial<PollCreateRequest>;
  onSubmit: (data: PollCreateRequest) => void;
  onCancel?: () => void;
  courseId: number;
  sessionId?: number;
}

const POLL_TYPE_OPTIONS = [
  { value: PollType.MULTIPLE_CHOICE, label: 'Multiple Choice' },
  { value: PollType.RATING, label: 'Rating Scale' },
  { value: PollType.WORD_CLOUD, label: 'Word Cloud' },
  { value: PollType.OPEN_ENDED, label: 'Open Ended' },
  { value: PollType.YES_NO, label: 'Yes/No' },
];

export const PollForm: React.FC<PollFormProps> = ({
  initialData,
  onSubmit,
  onCancel,
  courseId,
  sessionId,
}) => {
  const [question, setQuestion] = useState(initialData?.question || '');
  const [description, setDescription] = useState(initialData?.description || '');
  const [pollType, setPollType] = useState<PollType>(initialData?.pollType || PollType.MULTIPLE_CHOICE);
  const [options, setOptions] = useState<string[]>(initialData?.options || ['', '']);
  const [allowMultiple, setAllowMultiple] = useState(initialData?.allowMultiple || false);
  const [allowAnonymous, setAllowAnonymous] = useState(initialData?.allowAnonymous || true);
  const [showResults, setShowResults] = useState(initialData?.showResults !== false);

  const needsOptions = [PollType.MULTIPLE_CHOICE, PollType.YES_NO].includes(pollType);

  const handleAddOption = () => {
    setOptions([...options, '']);
  };

  const handleRemoveOption = (index: number) => {
    if (options.length <= 2) return;
    setOptions(options.filter((_, i) => i !== index));
  };

  const handleOptionChange = (index: number, value: string) => {
    const newOptions = [...options];
    newOptions[index] = value;
    setOptions(newOptions);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const data: PollCreateRequest = {
      courseId,
      sessionId,
      question,
      description: description || undefined,
      pollType,
      options: needsOptions ? options.filter((o) => o.trim()) : undefined,
      allowMultiple: pollType === PollType.MULTIPLE_CHOICE ? allowMultiple : false,
      allowAnonymous,
      showResults,
    };

    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Poll Details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="question">Question *</Label>
            <Textarea
              id="question"
              placeholder="Enter your poll question..."
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              required
              rows={3}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Description (Optional)</Label>
            <Textarea
              id="description"
              placeholder="Add additional context or instructions..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={2}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="pollType">Poll Type *</Label>
            <Select value={pollType} onValueChange={(value) => setPollType(value as PollType)}>
              <SelectTrigger id="pollType">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {POLL_TYPE_OPTIONS.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {needsOptions && (
        <Card>
          <CardHeader>
            <CardTitle>Options</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {pollType === PollType.YES_NO ? (
              <div className="space-y-2">
                <Input value="Yes" disabled />
                <Input value="No" disabled />
              </div>
            ) : (
              <>
                {options.map((option, index) => (
                  <div key={index} className="flex gap-2">
                    <Input
                      placeholder={`Option ${index + 1}`}
                      value={option}
                      onChange={(e) => handleOptionChange(index, e.target.value)}
                      required
                    />
                    {options.length > 2 && (
                      <Button
                        type="button"
                        variant="ghost"
                        size="icon"
                        onClick={() => handleRemoveOption(index)}
                      >
                        <X className="h-4 w-4" />
                      </Button>
                    )}
                  </div>
                ))}
                <Button type="button" variant="outline" size="sm" onClick={handleAddOption}>
                  <Plus className="h-4 w-4 mr-1" />
                  Add Option
                </Button>
              </>
            )}
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader>
          <CardTitle>Settings</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {pollType === PollType.MULTIPLE_CHOICE && (
            <div className="flex items-center justify-between">
              <div className="space-y-0.5">
                <Label>Allow Multiple Selections</Label>
                <p className="text-sm text-gray-500">Let participants select more than one option</p>
              </div>
              <Switch checked={allowMultiple} onCheckedChange={setAllowMultiple} />
            </div>
          )}

          <div className="flex items-center justify-between">
            <div className="space-y-0.5">
              <Label>Anonymous Responses</Label>
              <p className="text-sm text-gray-500">Allow participants to respond anonymously</p>
            </div>
            <Switch checked={allowAnonymous} onCheckedChange={setAllowAnonymous} />
          </div>

          <div className="flex items-center justify-between">
            <div className="space-y-0.5">
              <Label>Show Results</Label>
              <p className="text-sm text-gray-500">Display results to participants after voting</p>
            </div>
            <Switch checked={showResults} onCheckedChange={setShowResults} />
          </div>
        </CardContent>
      </Card>

      <div className="flex justify-end gap-2">
        {onCancel && (
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
        )}
        <Button type="submit" disabled={!question.trim()}>
          {initialData ? 'Update Poll' : 'Create Poll'}
        </Button>
      </div>
    </form>
  );
};
