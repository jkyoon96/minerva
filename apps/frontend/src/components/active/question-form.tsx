'use client';

import React, { useState } from 'react';
import { Plus, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Switch } from '@/components/ui/switch';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { QuestionType, QuestionDifficulty, QuestionCreateRequest } from '@/types/active';

interface QuestionFormProps {
  initialData?: Partial<QuestionCreateRequest>;
  onSubmit: (data: QuestionCreateRequest) => void;
  onCancel?: () => void;
  courseId: number;
}

export const QuestionForm: React.FC<QuestionFormProps> = ({ initialData, onSubmit, onCancel, courseId }) => {
  const [questionType, setQuestionType] = useState<QuestionType>(
    initialData?.questionType || QuestionType.MULTIPLE_CHOICE
  );
  const [questionText, setQuestionText] = useState(initialData?.questionText || '');
  const [description, setDescription] = useState(initialData?.description || '');
  const [points, setPoints] = useState(initialData?.points || 1);
  const [difficulty, setDifficulty] = useState<QuestionDifficulty>(
    initialData?.difficulty || QuestionDifficulty.MEDIUM
  );
  const [options, setOptions] = useState(
    initialData?.options || [
      { optionText: '', isCorrect: false },
      { optionText: '', isCorrect: false },
    ]
  );
  const [correctAnswer, setCorrectAnswer] = useState(initialData?.correctAnswer || '');
  const [explanation, setExplanation] = useState(initialData?.explanation || '');
  const [tags, setTags] = useState<string[]>(initialData?.tags || []);
  const [tagInput, setTagInput] = useState('');
  const [timeLimitSeconds, setTimeLimitSeconds] = useState(initialData?.timeLimitSeconds);

  const needsOptions = [QuestionType.MULTIPLE_CHOICE, QuestionType.TRUE_FALSE].includes(questionType);
  const needsCorrectAnswer = [QuestionType.SHORT_ANSWER, QuestionType.FILL_BLANK].includes(questionType);

  const handleAddOption = () => {
    setOptions([...options, { optionText: '', isCorrect: false }]);
  };

  const handleRemoveOption = (index: number) => {
    if (options.length <= 2) return;
    setOptions(options.filter((_, i) => i !== index));
  };

  const handleOptionChange = (index: number, field: 'optionText' | 'isCorrect', value: string | boolean) => {
    const newOptions = [...options];
    newOptions[index] = { ...newOptions[index], [field]: value };

    // For multiple choice, allow only one correct answer
    if (field === 'isCorrect' && value && questionType === QuestionType.MULTIPLE_CHOICE) {
      newOptions.forEach((opt, i) => {
        if (i !== index) opt.isCorrect = false;
      });
    }

    setOptions(newOptions);
  };

  const handleAddTag = () => {
    if (tagInput.trim() && !tags.includes(tagInput.trim())) {
      setTags([...tags, tagInput.trim()]);
      setTagInput('');
    }
  };

  const handleRemoveTag = (tag: string) => {
    setTags(tags.filter((t) => t !== tag));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const data: QuestionCreateRequest = {
      courseId,
      questionType,
      questionText,
      description: description || undefined,
      points,
      difficulty,
      explanation: explanation || undefined,
      tags: tags.length > 0 ? tags : undefined,
      timeLimitSeconds,
    };

    if (needsOptions) {
      data.options = options.filter((o) => o.optionText.trim());
    }

    if (needsCorrectAnswer) {
      data.correctAnswer = correctAnswer;
    }

    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Question Details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Question Type *</Label>
              <Select value={questionType} onValueChange={(v) => setQuestionType(v as QuestionType)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={QuestionType.MULTIPLE_CHOICE}>Multiple Choice</SelectItem>
                  <SelectItem value={QuestionType.TRUE_FALSE}>True/False</SelectItem>
                  <SelectItem value={QuestionType.SHORT_ANSWER}>Short Answer</SelectItem>
                  <SelectItem value={QuestionType.ESSAY}>Essay</SelectItem>
                  <SelectItem value={QuestionType.FILL_BLANK}>Fill in the Blank</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label>Difficulty</Label>
              <Select value={difficulty} onValueChange={(v) => setDifficulty(v as QuestionDifficulty)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={QuestionDifficulty.EASY}>Easy</SelectItem>
                  <SelectItem value={QuestionDifficulty.MEDIUM}>Medium</SelectItem>
                  <SelectItem value={QuestionDifficulty.HARD}>Hard</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="space-y-2">
            <Label>Question Text *</Label>
            <Textarea
              placeholder="Enter your question..."
              value={questionText}
              onChange={(e) => setQuestionText(e.target.value)}
              required
              rows={3}
            />
          </div>

          <div className="space-y-2">
            <Label>Description (Optional)</Label>
            <Textarea
              placeholder="Add context or instructions..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={2}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Points</Label>
              <Input
                type="number"
                min="1"
                value={points}
                onChange={(e) => setPoints(Number(e.target.value))}
                required
              />
            </div>

            <div className="space-y-2">
              <Label>Time Limit (seconds, optional)</Label>
              <Input
                type="number"
                min="10"
                value={timeLimitSeconds || ''}
                onChange={(e) => setTimeLimitSeconds(e.target.value ? Number(e.target.value) : undefined)}
              />
            </div>
          </div>
        </CardContent>
      </Card>

      {needsOptions && (
        <Card>
          <CardHeader>
            <CardTitle>Answer Options</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {questionType === QuestionType.TRUE_FALSE ? (
              <>
                <div className="flex items-center justify-between p-3 rounded-lg border">
                  <span>True</span>
                  <Switch
                    checked={options[0]?.isCorrect}
                    onCheckedChange={(checked) => handleOptionChange(0, 'isCorrect', checked)}
                  />
                </div>
                <div className="flex items-center justify-between p-3 rounded-lg border">
                  <span>False</span>
                  <Switch
                    checked={options[1]?.isCorrect}
                    onCheckedChange={(checked) => handleOptionChange(1, 'isCorrect', checked)}
                  />
                </div>
              </>
            ) : (
              <>
                {options.map((option, index) => (
                  <div key={index} className="flex gap-2 items-start">
                    <Switch
                      checked={option.isCorrect}
                      onCheckedChange={(checked) => handleOptionChange(index, 'isCorrect', checked)}
                    />
                    <Input
                      placeholder={`Option ${index + 1}`}
                      value={option.optionText}
                      onChange={(e) => handleOptionChange(index, 'optionText', e.target.value)}
                      required
                      className="flex-1"
                    />
                    {options.length > 2 && (
                      <Button type="button" variant="ghost" size="icon" onClick={() => handleRemoveOption(index)}>
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

      {needsCorrectAnswer && (
        <Card>
          <CardHeader>
            <CardTitle>Correct Answer</CardTitle>
          </CardHeader>
          <CardContent>
            <Input
              placeholder="Enter the correct answer..."
              value={correctAnswer}
              onChange={(e) => setCorrectAnswer(e.target.value)}
              required
            />
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader>
          <CardTitle>Additional Information</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label>Explanation (Optional)</Label>
            <Textarea
              placeholder="Explain the correct answer..."
              value={explanation}
              onChange={(e) => setExplanation(e.target.value)}
              rows={3}
            />
          </div>

          <div className="space-y-2">
            <Label>Tags</Label>
            <div className="flex gap-2">
              <Input
                placeholder="Add a tag..."
                value={tagInput}
                onChange={(e) => setTagInput(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), handleAddTag())}
              />
              <Button type="button" onClick={handleAddTag}>
                Add
              </Button>
            </div>
            <div className="flex flex-wrap gap-2 mt-2">
              {tags.map((tag) => (
                <span key={tag} className="px-2 py-1 bg-gray-100 rounded-md text-sm flex items-center gap-1">
                  {tag}
                  <button type="button" onClick={() => handleRemoveTag(tag)}>
                    <X className="h-3 w-3" />
                  </button>
                </span>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>

      <div className="flex justify-end gap-2">
        {onCancel && (
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
        )}
        <Button type="submit">
          {initialData ? 'Update Question' : 'Create Question'}
        </Button>
      </div>
    </form>
  );
};
