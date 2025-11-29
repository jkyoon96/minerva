'use client';

import React, { useState } from 'react';
import { Search, Filter, Plus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Checkbox } from '@/components/ui/checkbox';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Question, QuestionType, QuestionDifficulty } from '@/types/active';

interface QuestionBankListProps {
  questions: Question[];
  selectedIds: number[];
  onSelectionChange: (ids: number[]) => void;
  onEdit?: (question: Question) => void;
  onDelete?: (questionId: number) => void;
}

const TYPE_LABELS: Record<QuestionType, string> = {
  [QuestionType.MULTIPLE_CHOICE]: 'Multiple Choice',
  [QuestionType.TRUE_FALSE]: 'True/False',
  [QuestionType.SHORT_ANSWER]: 'Short Answer',
  [QuestionType.ESSAY]: 'Essay',
  [QuestionType.MATCHING]: 'Matching',
  [QuestionType.FILL_BLANK]: 'Fill in Blank',
};

export const QuestionBankList: React.FC<QuestionBankListProps> = ({
  questions,
  selectedIds,
  onSelectionChange,
  onEdit,
  onDelete,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [filterDifficulty, setFilterDifficulty] = useState<QuestionDifficulty | 'ALL'>('ALL');

  const filteredQuestions = questions.filter((q) => {
    const matchesSearch = q.questionText.toLowerCase().includes(searchQuery.toLowerCase()) ||
      q.tags.some(tag => tag.toLowerCase().includes(searchQuery.toLowerCase()));
    const matchesDifficulty = filterDifficulty === 'ALL' || q.difficulty === filterDifficulty;
    return matchesSearch && matchesDifficulty;
  });

  const handleToggle = (questionId: number) => {
    if (selectedIds.includes(questionId)) {
      onSelectionChange(selectedIds.filter(id => id !== questionId));
    } else {
      onSelectionChange([...selectedIds, questionId]);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex gap-2">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-500" />
          <Input
            placeholder="Search questions..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-9"
          />
        </div>
        <Button variant="outline" size="icon">
          <Filter className="h-4 w-4" />
        </Button>
      </div>

      <ScrollArea className="h-[600px]">
        <div className="space-y-3">
          {filteredQuestions.map((question) => (
            <Card key={question.id} className="hover:shadow-md transition-shadow">
              <CardContent className="p-4">
                <div className="flex items-start gap-3">
                  <Checkbox
                    checked={selectedIds.includes(question.id)}
                    onCheckedChange={() => handleToggle(question.id)}
                  />
                  <div className="flex-1">
                    <div className="flex items-start justify-between mb-2">
                      <p className="font-medium text-gray-900">{question.questionText}</p>
                      <Badge variant="outline">{question.points} pts</Badge>
                    </div>
                    <div className="flex flex-wrap gap-2">
                      <Badge variant="secondary">{TYPE_LABELS[question.questionType]}</Badge>
                      <Badge>{question.difficulty}</Badge>
                      {question.tags.map(tag => (
                        <Badge key={tag} variant="outline">{tag}</Badge>
                      ))}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </ScrollArea>

      <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
        <span className="text-sm text-gray-600">
          {selectedIds.length} question{selectedIds.length !== 1 ? 's' : ''} selected
        </span>
        <span className="text-sm text-gray-600">
          {filteredQuestions.length} total
        </span>
      </div>
    </div>
  );
};
