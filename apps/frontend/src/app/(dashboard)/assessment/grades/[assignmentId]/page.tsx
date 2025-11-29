'use client';

import React, { useEffect } from 'react';
import { useParams } from 'next/navigation';
import { GradeBreakdown } from '@/components/assessment/grade-breakdown';
import { ExplanationPanel } from '@/components/assessment/explanation-panel';
import { GradingResultCard } from '@/components/assessment/grading-result-card';
import { useAssessmentStore } from '@/stores/assessmentStore';
import { autoGradingApi } from '@/lib/api/assessment';

export default function GradeDetailPage() {
  const params = useParams();
  const assignmentId = parseInt(params.assignmentId as string);
  const { currentAutoGradingResult, setCurrentAutoGradingResult } = useAssessmentStore();

  useEffect(() => {
    loadResult();
  }, [assignmentId]);

  const loadResult = async () => {
    try {
      const result = await autoGradingApi.getResult(assignmentId);
      setCurrentAutoGradingResult(result);
    } catch (error) {
      console.error('Failed to load result:', error);
    }
  };

  if (!currentAutoGradingResult) {
    return <div className="container mx-auto py-12 text-center">Loading...</div>;
  }

  return (
    <div className="container mx-auto py-6 space-y-6">
      <h1 className="text-3xl font-bold">Grade Details</h1>
      
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1">
          <GradingResultCard result={currentAutoGradingResult} showDetails />
        </div>
        <div className="lg:col-span-2 space-y-6">
          <GradeBreakdown result={currentAutoGradingResult} />
          <ExplanationPanel result={currentAutoGradingResult} />
        </div>
      </div>
    </div>
  );
}
