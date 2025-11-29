'use client';

import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { CodeEditor } from '@/components/assessment/code-editor';
import { TestCaseList } from '@/components/assessment/test-case-list';
import { ExecutionResult } from '@/components/assessment/execution-result';
import { PlagiarismResult } from '@/components/assessment/plagiarism-result';
import { useAssessmentStore } from '@/stores/assessmentStore';
import { codeEvaluationApi } from '@/lib/api/assessment';

export default function CodeSubmissionPage() {
  const params = useParams();
  const submissionId = parseInt(params.submissionId as string);
  const { currentCodeSubmission, setCurrentCodeSubmission, executionResults } = useAssessmentStore();
  const [testCases, setTestCases] = useState<any[]>([]);

  useEffect(() => {
    loadSubmission();
  }, [submissionId]);

  const loadSubmission = async () => {
    try {
      const submission = await codeEvaluationApi.getSubmission(submissionId);
      setCurrentCodeSubmission(submission);
    } catch (error) {
      console.error('Failed to load submission:', error);
    }
  };

  if (!currentCodeSubmission) {
    return <div className="container mx-auto py-12 text-center">Loading...</div>;
  }

  const executionResult = executionResults.find((r) => r.submissionId === submissionId);

  return (
    <div className="container mx-auto py-6 space-y-6">
      <h1 className="text-3xl font-bold">Code Submission</h1>
      <p className="text-gray-600">{currentCodeSubmission.studentName}</p>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <CodeEditor code={currentCodeSubmission.code} language={currentCodeSubmission.language} readOnly />
        {executionResult && <ExecutionResult result={executionResult} />}
      </div>

      <TestCaseList testCases={testCases} results={executionResult?.testResults} />
    </div>
  );
}
