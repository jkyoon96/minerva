'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Plus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { QuestionBankList } from '@/components/active/question-bank-list';
import { useActiveStore } from '@/stores/activeStore';

export default function QuestionBankPage() {
  const router = useRouter();
  const { questions } = useActiveStore();
  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  return (
    <div className="container mx-auto py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Question Bank</h1>
          <p className="text-gray-600 mt-1">Manage your quiz questions</p>
        </div>
        <div className="flex gap-2">
          {selectedIds.length > 0 && (
            <Button variant="outline">
              Create Quiz ({selectedIds.length} questions)
            </Button>
          )}
          <Button onClick={() => router.push('/active/questions/new')}>
            <Plus className="h-4 w-4 mr-2" />
            Add Question
          </Button>
        </div>
      </div>

      <QuestionBankList
        questions={questions}
        selectedIds={selectedIds}
        onSelectionChange={setSelectedIds}
      />
    </div>
  );
}
