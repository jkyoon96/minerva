'use client';

/**
 * Poll response UI for students
 */

import React, { useState } from 'react';
import { Send } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Poll, PollType, PollResponseRequest } from '@/types/active';

interface PollResponseProps {
  poll: Poll;
  onSubmit: (response: PollResponseRequest) => void;
  disabled?: boolean;
}

export const PollResponse: React.FC<PollResponseProps> = ({ poll, onSubmit, disabled }) => {
  const [selectedOptionIds, setSelectedOptionIds] = useState<number[]>([]);
  const [textResponse, setTextResponse] = useState('');
  const [ratingValue, setRatingValue] = useState(0);

  const handleSubmit = () => {
    const response: PollResponseRequest = {};

    if (poll.pollType === PollType.MULTIPLE_CHOICE || poll.pollType === PollType.YES_NO) {
      response.selectedOptionIds = selectedOptionIds;
    } else if (poll.pollType === PollType.RATING) {
      response.ratingValue = ratingValue;
    } else if (poll.pollType === PollType.WORD_CLOUD || poll.pollType === PollType.OPEN_ENDED) {
      response.textResponse = textResponse;
    }

    response.isAnonymous = poll.allowAnonymous;

    onSubmit(response);
  };

  const handleOptionSelect = (optionId: number) => {
    if (poll.allowMultiple) {
      if (selectedOptionIds.includes(optionId)) {
        setSelectedOptionIds(selectedOptionIds.filter((id) => id !== optionId));
      } else {
        setSelectedOptionIds([...selectedOptionIds, optionId]);
      }
    } else {
      setSelectedOptionIds([optionId]);
    }
  };

  const isValid = () => {
    if (poll.pollType === PollType.MULTIPLE_CHOICE || poll.pollType === PollType.YES_NO) {
      return selectedOptionIds.length > 0;
    } else if (poll.pollType === PollType.RATING) {
      return ratingValue > 0;
    } else {
      return textResponse.trim().length > 0;
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-xl">{poll.question}</CardTitle>
        {poll.description && <p className="text-sm text-gray-600 mt-2">{poll.description}</p>}
      </CardHeader>

      <CardContent className="space-y-6">
        {/* Multiple Choice / Yes-No */}
        {(poll.pollType === PollType.MULTIPLE_CHOICE || poll.pollType === PollType.YES_NO) && (
          <div className="space-y-3">
            {poll.allowMultiple ? (
              // Checkboxes for multiple selection
              poll.options.map((option) => (
                <div key={option.id} className="flex items-center space-x-2 p-3 rounded-lg hover:bg-gray-50">
                  <Checkbox
                    id={`option-${option.id}`}
                    checked={selectedOptionIds.includes(option.id)}
                    onCheckedChange={() => handleOptionSelect(option.id)}
                    disabled={disabled}
                  />
                  <Label htmlFor={`option-${option.id}`} className="flex-1 cursor-pointer text-base">
                    {option.optionText}
                  </Label>
                </div>
              ))
            ) : (
              // Radio buttons for single selection
              <RadioGroup
                value={selectedOptionIds[0]?.toString()}
                onValueChange={(value) => handleOptionSelect(Number(value))}
                disabled={disabled}
              >
                {poll.options.map((option) => (
                  <div key={option.id} className="flex items-center space-x-2 p-3 rounded-lg hover:bg-gray-50">
                    <RadioGroupItem value={option.id.toString()} id={`option-${option.id}`} />
                    <Label htmlFor={`option-${option.id}`} className="flex-1 cursor-pointer text-base">
                      {option.optionText}
                    </Label>
                  </div>
                ))}
              </RadioGroup>
            )}
          </div>
        )}

        {/* Rating Scale */}
        {poll.pollType === PollType.RATING && (
          <div className="space-y-4">
            <div className="flex justify-between items-center px-4">
              {[1, 2, 3, 4, 5].map((value) => (
                <button
                  key={value}
                  type="button"
                  onClick={() => setRatingValue(value)}
                  disabled={disabled}
                  className={`w-12 h-12 rounded-full border-2 transition-all ${
                    ratingValue >= value
                      ? 'bg-blue-500 border-blue-500 text-white'
                      : 'border-gray-300 hover:border-blue-300'
                  } ${disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}`}
                >
                  {value}
                </button>
              ))}
            </div>
            <div className="flex justify-between text-sm text-gray-600 px-4">
              <span>Strongly Disagree</span>
              <span>Strongly Agree</span>
            </div>
          </div>
        )}

        {/* Word Cloud */}
        {poll.pollType === PollType.WORD_CLOUD && (
          <div className="space-y-2">
            <Label htmlFor="word-input">Enter a word or short phrase</Label>
            <Input
              id="word-input"
              placeholder="Type your response..."
              value={textResponse}
              onChange={(e) => setTextResponse(e.target.value)}
              disabled={disabled}
              maxLength={50}
            />
            <p className="text-xs text-gray-500">{textResponse.length}/50 characters</p>
          </div>
        )}

        {/* Open Ended */}
        {poll.pollType === PollType.OPEN_ENDED && (
          <div className="space-y-2">
            <Label htmlFor="text-response">Your Response</Label>
            <Textarea
              id="text-response"
              placeholder="Type your detailed response..."
              value={textResponse}
              onChange={(e) => setTextResponse(e.target.value)}
              disabled={disabled}
              rows={5}
              maxLength={500}
            />
            <p className="text-xs text-gray-500">{textResponse.length}/500 characters</p>
          </div>
        )}

        {poll.allowAnonymous && (
          <p className="text-sm text-gray-500 italic">Your response will be anonymous</p>
        )}

        <Button onClick={handleSubmit} disabled={!isValid() || disabled} className="w-full">
          <Send className="h-4 w-4 mr-2" />
          Submit Response
        </Button>
      </CardContent>
    </Card>
  );
};
