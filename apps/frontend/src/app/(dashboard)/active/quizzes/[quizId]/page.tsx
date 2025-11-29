'use client';

import React, { useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { QuizSession } from '@/components/active/quiz-session';
import { useActiveStore } from '@/stores/activeStore';
import { QuizAnswer } from '@/types/active';
import { quizApi } from '@/lib/api/active';

export default function QuizTakePage() {
  const params = useParams();
  const router = useRouter();
  const quizId = Number(params.quizId);
  const { currentQuiz, setCurrentQuiz } = useActiveStore();

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const quiz = await quizApi.getQuiz(quizId);
        setCurrentQuiz(quiz);
      } catch (error) {
        console.error('Failed to fetch quiz:', error);
      }
    };

    fetchQuiz();
  }, [quizId, setCurrentQuiz]);

  const handleSubmit = async (answers: QuizAnswer[]) => {
    try {
      const submission = await quizApi.submitQuiz(quizId, {
        answers,
        timeSpentSeconds: 0, // Calculate actual time
      });
      router.push(`/active/quizzes/${quizId}/results`);
    } catch (error) {
      console.error('Failed to submit quiz:', error);
    }
  };

  if (!currentQuiz) {
    return <div className="container mx-auto py-6">Loading...</div>;
  }

  return (
    <div className="container mx-auto py-6 space-y-6 max-w-4xl">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="icon" onClick={() => router.back()}>
          <ArrowLeft className="h-5 w-5" />
        </Button>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{currentQuiz.title}</h1>
          <p className="text-gray-600 mt-1">{currentQuiz.description}</p>
        </div>
      </div>

      <QuizSession quiz={currentQuiz} onSubmit={handleSubmit} />
    </div>
  );
}
