'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { QuizForm } from '@/components/active/quiz-form';
import { QuestionBankList } from '@/components/active/question-bank-list';
import { QuizCreateRequest } from '@/types/active';
import { useActiveStore } from '@/stores/activeStore';
import { quizApi } from '@/lib/api/active';

export default function NewQuizPage() {
  const router = useRouter();
  const { questions } = useActiveStore();
  const [selectedQuestionIds, setSelectedQuestionIds] = useState<number[]>([]);

  const handleSubmit = async (data: QuizCreateRequest) => {
    try {
      await quizApi.createQuiz(data);
      router.push('/active/quizzes');
    } catch (error) {
      console.error('Failed to create quiz:', error);
    }
  };

  return (
    <div className="container mx-auto py-6 space-y-6 max-w-6xl">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="icon" onClick={() => router.back()}>
          <ArrowLeft className="h-5 w-5" />
        </Button>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Create New Quiz</h1>
          <p className="text-gray-600 mt-1">Select questions and configure quiz settings</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          <h2 className="text-xl font-semibold mb-4">Select Questions</h2>
          <QuestionBankList
            questions={questions}
            selectedIds={selectedQuestionIds}
            onSelectionChange={setSelectedQuestionIds}
          />
        </div>

        <div>
          <h2 className="text-xl font-semibold mb-4">Quiz Settings</h2>
          <QuizForm
            courseId={1}
            selectedQuestionIds={selectedQuestionIds}
            onSubmit={handleSubmit}
            onCancel={() => router.back()}
          />
        </div>
      </div>
    </div>
  );
}
