'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Plus, Clock, Users } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { useActiveStore } from '@/stores/activeStore';
import { QuizStatus } from '@/types/active';

export default function QuizzesPage() {
  const router = useRouter();
  const { quizzes } = useActiveStore();

  return (
    <div className="container mx-auto py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Quizzes</h1>
          <p className="text-gray-600 mt-1">Create and manage quizzes</p>
        </div>
        <Button onClick={() => router.push('/active/quizzes/new')}>
          <Plus className="h-4 w-4 mr-2" />
          Create Quiz
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {quizzes.map((quiz) => (
          <Card key={quiz.id} className="hover:shadow-md transition-shadow cursor-pointer"
            onClick={() => router.push(`/active/quizzes/${quiz.id}`)}>
            <CardHeader>
              <div className="flex items-center justify-between mb-2">
                <Badge>{quiz.status}</Badge>
                <span className="text-sm text-gray-600">{quiz.totalPoints} pts</span>
              </div>
              <CardTitle className="text-lg">{quiz.title}</CardTitle>
              {quiz.description && (
                <p className="text-sm text-gray-600 mt-2">{quiz.description}</p>
              )}
            </CardHeader>
            <CardContent>
              <div className="flex items-center gap-4 text-sm text-gray-600">
                <div className="flex items-center gap-1">
                  <Users className="h-4 w-4" />
                  <span>{quiz.questions.length} questions</span>
                </div>
                {quiz.timeLimitMinutes && (
                  <div className="flex items-center gap-1">
                    <Clock className="h-4 w-4" />
                    <span>{quiz.timeLimitMinutes} min</span>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {quizzes.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500">No quizzes found</p>
          <Button variant="outline" className="mt-4" onClick={() => router.push('/active/quizzes/new')}>
            Create your first quiz
          </Button>
        </div>
      )}
    </div>
  );
}
